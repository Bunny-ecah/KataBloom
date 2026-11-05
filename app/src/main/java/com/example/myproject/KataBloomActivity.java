package com.example.myproject;

import android.app.AlertDialog;
import android.content.*;
import android.content.res.ColorStateList;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.os.*;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.InputType;
import android.view.*;
import android.widget.*;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.*;
import com.example.myproject.learning.*;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;
import org.json.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Main mobile experience: one navigation flow and a single source of learning results. */
public class KataBloomActivity extends AppCompatActivity {
    private static final int INK=0xff26384b, MUTED=0xff526276, TEAL=0xff176b60, PURPLE=0xff66549c, CREAM=0xfffffaef, MINT=0xffe3f1df, LILAC=0xffeee9fa, GOLD=0xffffedbc;
    private LinearLayout shell,body;
    private LearnerStore store;
    private SharedPreferences settings;
    private FirebaseAuth auth;
    private String owner="local",page="landing",feedback="",speechRound="",cloudStatus="";
    private PracticeRound round;
    private final ArrayList<Integer> selectedTiles=new ArrayList<>();
    private TextToSpeech voice;
    private Button audioButton;
    private TextView audioNote;
    private boolean voiceReady,en,large,wide,white,slow,busy,speechPending;
    private int speechIndex=-1;
    private final Handler handler=new Handler(Looper.getMainLooper());
    private final FirebaseAuth.AuthStateListener accountChanged=value->{String uid=value.getCurrentUser()==null?"local":value.getCurrentUser().getUid();if(store!=null&&!uid.equals(owner)){busy=false;switchOwner();go(uid.equals("local")?"landing":"home");}};
    private final ActivityResultLauncher<Intent> speech=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result->{
        speechPending=false;
        if(round==null||!page.equals("practice")||!round.id.equals(speechRound)||round.index!=speechIndex||round.finished)return;
        Intent data=result.getData();
        if(result.getResultCode()==RESULT_OK&&data!=null){
            ArrayList<String> values=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if(LessonBank.matches(word().text,values)){round.solve();feedback=t("Peranti mendengar perkataan yang sepadan. Bagus mencuba!","The device heard a matching word. Good effort!");}
            else{round.help();feedback=t("Peranti belum mendengar padanan. Dengar dan cuba lagi, atau teruskan bersama ibu bapa.","The device did not hear a match. Listen and retry, or continue with a parent.");}
        }else feedback=t("Rakaman dibatalkan. Kamu boleh cuba lagi.","Speech cancelled. You can try again.");
        render();
    });
    private final ActivityResultLauncher<Intent> export=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result->{
        if(result.getResultCode()!=RESULT_OK||result.getData()==null)return;
        try(OutputStream out=getContentResolver().openOutputStream(result.getData().getData())){
            if(out==null)throw new IOException("No destination");out.write(reportText().getBytes(StandardCharsets.UTF_8));toast(t("Laporan disimpan.","Report saved."));
        }catch(IOException|SecurityException e){notice(t("Tidak dapat menyimpan","Could not save"),t("Pilih lokasi lain dan cuba lagi.","Choose another location and retry."));}
    });

    @Override public void onCreate(Bundle saved){
        super.onCreate(saved);
        settings=getSharedPreferences("dyslearn_display_v2",MODE_PRIVATE);
        en=settings.getBoolean("english",false);large=settings.getBoolean("large",false);wide=settings.getBoolean("wide",false);white=settings.getBoolean("white",false);slow=settings.getBoolean("slow",true);
        try{auth=FirebaseAuth.getInstance();}catch(IllegalStateException ignored){auth=null;}
        owner=auth!=null&&auth.getCurrentUser()!=null?auth.getCurrentUser().getUid():"local";
        page=owner.equals("local")?"landing":"home";
        store=new LearnerStore(this,owner);
        if(owner.equals("local"))store.importGarden(getSharedPreferences("adventure_guest",MODE_PRIVATE),t("Pelajar","Learner"));
        if(saved!=null&&owner.equals(saved.getString("owner"))){page=saved.getString("page","home");feedback=saved.getString("feedback","");speechRound=saved.getString("speechRound","");speechIndex=saved.getInt("speechIndex",-1);speechPending=saved.getBoolean("speechPending");readRound(saved.getBundle("round"));ArrayList<Integer> tiles=saved.getIntegerArrayList("tiles");if(tiles!=null)selectedTiles.addAll(tiles);}
        if((page.equals("practice")||page.equals("complete"))&&(round==null||store.child(round.child)==null))page="home";
        voice=new TextToSpeech(this,status->{if(isDestroyed())return;if(status==TextToSpeech.SUCCESS){voiceReady=voice.setLanguage(new Locale("ms","MY"))>=0;voice.setSpeechRate(slow?.7f:.9f);}updateAudioUi();});
        getOnBackPressedDispatcher().addCallback(this,new OnBackPressedCallback(true){@Override public void handleOnBackPressed(){back();}});
        render();
        if(store.recovered)notice(t("Data memerlukan perhatian","Data needs attention"),t("Data tempatan tidak dapat dibaca. Salinan asal disimpan untuk pemulihan. Tiada data awan diubah.","Local data could not be read. The original copy was retained for recovery. Cloud data was not changed."));
    }
    private String t(String ms,String english){return en?english:ms;}
    private String childName(){JSONObject c=store.selected();return c==null?t("pelajar","learner"):c.optString("name");}
    private int dp(float n){return Math.round(n*getResources().getDisplayMetrics().density);}
    private GradientDrawable background(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));return d;}
    private LinearLayout column(){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);return l;}
    private TextView text(LinearLayout parent,String value,int size,int color){TextView v=new TextView(this);v.setText(value);v.setTextColor(color);v.setTextSize(size+(large?3:0));v.setTypeface(Typeface.create("sans-serif",Typeface.NORMAL));v.setLineSpacing(0,1.35f);v.setLetterSpacing(wide?.045f:0);v.setPadding(0,dp(6),0,dp(6));parent.addView(v,new LinearLayout.LayoutParams(-1,-2));return v;}
    private TextView title(String value){TextView v=text(body,value,30,INK);v.setTypeface(Typeface.DEFAULT,Typeface.BOLD);ViewCompat.setAccessibilityHeading(v,true);return v;}
    private TextView note(String value){return text(body,value,16,MUTED);}
    private Button button(LinearLayout parent,String label,Runnable action,boolean primary){
        Button b=new Button(this);b.setText(label);b.setAllCaps(false);b.setTextSize(large?21:18);b.setTextColor(primary?Color.WHITE:TEAL);b.setMinHeight(dp(56));b.setPadding(dp(16),dp(12),dp(16),dp(12));b.setBackground(background(primary?TEAL:0xffe7eee9,16));b.setStateListAnimator(null);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.setMargins(0,dp(6),0,dp(6));parent.addView(b,lp);b.setOnClickListener(v->action.run());return b;
    }
    private LinearLayout card(String heading,String detail,int color){LinearLayout c=column();c.setPadding(dp(20),dp(14),dp(20),dp(16));c.setBackground(background(color,24));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.setMargins(0,dp(10),0,dp(8));body.addView(c,lp);if(!heading.isEmpty())text(c,heading,22,INK).setTypeface(null,Typeface.BOLD);if(!detail.isEmpty())text(c,detail,17,INK);return c;}
    private void go(String next){if(voice!=null)voice.stop();page=next;feedback="";render();}
    private void render(){
        if(isFinishing()||isDestroyed())return;
        audioButton=null;audioNote=null;
        shell=column();shell.setBackgroundColor(white?Color.WHITE:CREAM);setContentView(shell);
        ViewCompat.setOnApplyWindowInsetsListener(shell,(v,insets)->{androidx.core.graphics.Insets bars=insets.getInsets(WindowInsetsCompat.Type.systemBars()|WindowInsetsCompat.Type.ime());v.setPadding(bars.left,bars.top,bars.right,bars.bottom);return insets;});ViewCompat.requestApplyInsets(shell);
        LinearLayout header=new LinearLayout(this);header.setGravity(Gravity.CENTER_VERTICAL);header.setPadding(dp(20),dp(4),dp(20),dp(4));shell.addView(header);
        TextView brand=new TextView(this);brand.setText(R.string.app_name);brand.setTextColor(TEAL);brand.setTextSize(22);brand.setTypeface(null,Typeface.BOLD);header.addView(brand,new LinearLayout.LayoutParams(0,dp(52),1));brand.setGravity(Gravity.CENTER_VERTICAL);
        Button lang=new Button(this);lang.setText(en?"BM":"English");lang.setAllCaps(false);lang.setTextSize(15);lang.setMinHeight(dp(48));lang.setTextColor(PURPLE);lang.setBackground(background(LILAC,16));lang.setStateListAnimator(null);lang.setContentDescription(t("Tukar bahasa kepada English","Switch interface to Bahasa Melayu"));lang.setOnClickListener(v->{en=!en;settings.edit().putBoolean("english",en).apply();render();});header.addView(lang,new LinearLayout.LayoutParams(-2,dp(52)));
        ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);shell.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));android.widget.FrameLayout frame=new android.widget.FrameLayout(this);scroll.addView(frame,new ScrollView.LayoutParams(-1,-2));body=column();body.setPadding(dp(20),dp(8),dp(20),dp(28));int width=getResources().getConfiguration().screenWidthDp;android.widget.FrameLayout.LayoutParams bodyLp=new android.widget.FrameLayout.LayoutParams(width>760?dp(720):-1,-2,Gravity.TOP|Gravity.CENTER_HORIZONTAL);frame.addView(body,bodyLp);
        switch(page){case "landing":landing();break;case "learn":lessons();break;case "practice":practice();break;case "complete":complete();break;case "progress":progress();break;case "settings":settingsPage();break;case "profiles":profiles();break;case "account":account(false);break;case "register":account(true);break;default:page="home";home();}
        if(!page.equals("landing")&&!page.equals("practice")&&!page.equals("complete")&&!page.equals("account")&&!page.equals("register"))navigation();
    }
    private void landing(){
        note(t("Ruang kecil untuk impian besar","A little space for big possibilities"));
        title(t("Belajar dengan yakin.","Learn with confidence."));
        text(body,t("Setiap perkataan, satu langkah baharu.","Every word is a new beginning."),22,PURPLE);
        GardenView garden=new GardenView(this,3);garden.setContentDescription(t("Taman kata KataBloom","The KataBloom word garden"));body.addView(garden,new LinearLayout.LayoutParams(-1,dp(132)));
        button(body,t("Log masuk","Sign in"),()->go("account"),true);
        button(body,t("Daftar akaun","Sign up"),()->go("register"),false);
        button(body,t("Teruskan tanpa akaun","Continue without an account"),()->go("home"),false);
        LinearLayout intro=card(t("Tumbuh mengikut rentak sendiri","Grow at your own pace"),t("Dengar, bina dan sebut perkataan Bahasa Melayu melalui pengembaraan pendek yang meraikan usaha.","Listen, build and say Bahasa Melayu words in short adventures that celebrate effort."),MINT);
        text(intro,t("★ Bintang usaha   •   Bantuan bila perlu","★ Effort stars   •   Hints whenever needed"),16,TEAL);
        note(t("Untuk pelajar 7–11 tahun, bersama ibu bapa. Pilih BM atau English di atas.","For learners aged 7–11, with a parent alongside. Switch BM / English above."));
    }
    private void metrics(String firstValue,String firstLabel,String secondValue,String secondLabel){
        LinearLayout row=new LinearLayout(this);body.addView(row);
        String[] values={firstValue,secondValue},labels={firstLabel,secondLabel};
        for(int i=0;i<2;i++){LinearLayout tile=column();tile.setPadding(dp(16),dp(12),dp(16),dp(16));tile.setBackground(background(i==0?GOLD:LILAC,20));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-1,1);lp.setMargins(i==0?0:dp(5),dp(8),i==0?dp(5):0,dp(8));row.addView(tile,lp);text(tile,values[i],30,i==0?TEAL:PURPLE).setTypeface(null,Typeface.BOLD);text(tile,labels[i],16,INK);}
    }
    private void navigation(){
        LinearLayout nav=new LinearLayout(this);nav.setPadding(dp(4),dp(6),dp(4),dp(6));nav.setBackgroundColor(white?0xfff2f5f2:0xfff0f0e7);shell.addView(nav);
        String[] labels={t("Utama","Home"),t("Belajar","Learn"),t("Kemajuan","Progress"),t("Tetapan","Settings")},pages={"home","learn","progress","settings"};
        for(int i=0;i<4;i++){final String target=pages[i];TextView item=new TextView(this);item.setText(labels[i]);item.setGravity(Gravity.CENTER);item.setTextSize(large?16:14);item.setTextColor(target.equals(page)?TEAL:MUTED);item.setTypeface(null,target.equals(page)?Typeface.BOLD:Typeface.NORMAL);item.setPadding(dp(2),dp(10),dp(2),dp(10));item.setMinHeight(dp(56));item.setSelected(target.equals(page));nav.addView(item,new LinearLayout.LayoutParams(0,-2,1));item.setOnClickListener(v->go(target));}
    }
    private void home(){
        note(t("PAPAN PEMUKA · TAMAN KATA","DASHBOARD · WORD GARDEN"));
        title(store.selected()==null?t("Selamat datang ke taman kata!","Welcome to your word garden!"):t("Hai, ","Hello, ")+childName()+"!");
        GardenView garden=new GardenView(this,store.selected()==null?0:store.selected().optInt("rounds"));garden.setContentDescription(t("Taman tumbuh bersama usaha kamu.","A garden growing with your effort."));body.addView(garden,new LinearLayout.LayoutParams(-1,dp(120)));
        if(store.selected()==null){
            LinearLayout c=card(t("Mari mula bersama","A little adventure awaits"),t("Aktiviti pendek untuk mendengar, mengenal huruf dan membina perkataan Bahasa Melayu.","Short activities to listen, explore letters and build Bahasa Melayu words."),MINT);
            button(c,t("Tambah pelajar","Add a learner"),()->editLearner(null),true);
            button(body,t("Cuba sebagai tetamu","Try as a guest"),()->{store.add(t("Pelajar","Learner"),7);go("learn");},false);
            note(t("Tiada akaun diperlukan. Data disimpan pada peranti ini.","No account needed. Learning is saved on this device."));return;
        }
        JSONObject c=store.selected();int count=c.optInt("rounds");boolean today=store.dates(c).contains(ProgressStats.date(System.currentTimeMillis()));
        LinearLayout hero=card(t("Misi kecil hari ini","Today's little mission"),today?t("✓ Satu pengembaraan selesai. Kamu boleh berehat atau teroka lagi.","✓ One adventure complete. Rest or explore another."):t("Lima perkataan. Ikut rentak kamu sendiri.","Five words. Take all the time you need."),MINT);
        button(hero,t("Jom belajar","Start an adventure"),()->go("learn"),true);
        metrics("★ "+c.optInt("stars"),t("Bintang usaha","Effort stars"),String.valueOf(ProgressStats.streak(store.dates(c),System.currentTimeMillis())),t("Hari berturut-turut","Day practice streak"));
        badgeJourney(count);
        button(body,t("Lihat perkembangan saya","Explore my progress"),()->go("progress"),false);
        button(body,t("Pelajar: ","Learner: ")+childName()+t(" · Tukar"," · Change"),()->go("profiles"),false);
        card(t("Bersama ibu bapa","A moment together"),t("Dengar bersama. Beri masa untuk mencuba. Puji usaha, bukan kepantasan.","Listen together. Allow time to try. Celebrate effort and give breaks."),LILAC);
        note(owner.equals("local")?t("Disimpan pada peranti ini · Mod tempatan","Saved on this device · Local mode"):t("Profil peranti untuk akaun ibu bapa · Sandaran manual dalam Tetapan","Device profiles for your parent account · Manual backup in Settings"));
    }
    private String badge(int count){return count>=10?t("Pokok ilmu","Knowledge tree"):count>=3?t("Tunas rajin","Growing sprout"):count>=1?t("Benih berani","Brave seed"):t("Benih baharu","New seed");}
    private String level(int value){return new String[]{t("Benih vokal","Vowel seeds"),t("Kebun perkataan","Word garden"),t("Pokok suku kata","Syllable tree")}[value];}
    private String mode(int value){return new String[]{t("Dengar & lihat","Listen & look"),t("Cari huruf awal","Find the first letter"),t("Bina perkataan","Build a word"),t("Cuba sebut","Try speaking")}[value];}
    private void lessons(){
        title(t("Pilih pengembaraan","Choose an adventure"));
        if(store.selected()==null){note(t("Tambah pelajar untuk menyimpan usaha mereka.","Add a learner to save their practice."));button(body,t("Tambah pelajar","Add a learner"),()->editLearner(null),true);return;}
        note(childName()+t(", kita belajar satu langkah pada satu masa.",", let's learn one step at a time."));
        String[] details={t("Perkataan yang bermula dengan vokal.","Words beginning with vowels."),t("Perkataan biasa dengan dua suku kata.","Familiar words with two syllables."),t("Teroka perkataan yang lebih panjang.","Explore longer words together.")};
        for(int i=0;i<3;i++){final int n=i;LinearLayout c=card((i+1)+" · "+level(i),details[i],new int[]{MINT,GOLD,LILAC}[i]);button(c,t("Pilih aktiviti","Choose activity"),()->chooseMode(n),true).setContentDescription(t("Mulakan ","Start ")+level(i));}
        note(t("Semua tahap terbuka. Ulang bila-bila masa. Tiada had masa atau nyawa yang hilang.","Every level is available. Repeat freely, with no timers or lost lives."));
    }
    private void chooseMode(int selectedLevel){
        String[] modes={mode(0),mode(1),mode(2),mode(3)};
        new AlertDialog.Builder(this).setTitle(level(selectedLevel)).setItems(modes,(d,which)->{
            round=new PracticeRound();round.child=store.selectedId();round.level=selectedLevel;round.mode=which;round.batch=store.selected().optInt("rounds");selectedTiles.clear();go("practice");
        }).setNegativeButton(t("Kembali","Back"),null).show();
    }
    private LessonBank.Word word(){return LessonBank.word(round.level,round.batch,round.index);}
    private void practice(){
        if(round==null||!round.valid()||store.child(round.child)==null){go("home");return;}
        note(level(round.level)+" · "+(round.index+1)+" / 5");
        ProgressBar bar=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);bar.setMax(5);bar.setProgress(round.index+(round.solved?1:0));bar.setProgressTintList(ColorStateList.valueOf(TEAL));bar.setContentDescription(t("Perkataan ","Word ")+(round.index+1)+t(" daripada lima"," of five"));body.addView(bar,new LinearLayout.LayoutParams(-1,dp(8)));
        title(mode(round.mode));
        LessonBank.Word item=word();
        final int question=round.index;
        LinearLayout c=card("","",round.mode==2?GOLD:MINT);
        TextView picture=text(c,item.picture,56,INK);picture.setGravity(Gravity.CENTER);picture.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        String visible=round.mode==2&&!round.solved?"• • •":item.text;
        TextView wordText=text(c,visible,44,INK);wordText.setGravity(Gravity.CENTER);wordText.setTypeface(null,Typeface.BOLD);wordText.setContentDescription(round.mode==2&&!round.solved?t("Dengar untuk membina perkataan","Listen to build the word"):item.text);
        if(round.mode==0||round.solved)text(c,String.join("  ·  ",item.parts()),26,PURPLE).setGravity(Gravity.CENTER);
        audioButton=button(c,voiceReady?t("Dengar perkataan","Listen to the word"):t("Bantuan audio","Audio help"),()->say(item.text),true);
        audioNote=text(c,t("Audio memerlukan suara Bahasa Melayu. Perkataan dan bantuan bertulis masih tersedia.","Audio needs an installed Malay voice. Written words and hints remain available."),15,MUTED);
        updateAudioUi();
        if(round.solved){
            TextView result=text(body,feedback.isEmpty()?t("✓ Bagus mencuba!","✓ Good effort!"):feedback,21,TEAL);result.setAccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_POLITE);
            button(body,round.index==4?t("Selesaikan pengembaraan","Finish adventure"):t("Perkataan seterusnya","Next word"),()->{if(round.index==question)next();},true);
        }else if(round.mode==0){
            note(t("Dengar, lihat dan sebut bersama jika kamu mahu.","Listen, look and say it together if you wish."));
            button(body,t("Saya sudah teroka","I have explored this word"),()->{if(round.index==question&&round.solve())next();},true);
        }else if(round.mode==1){
            note(t("Huruf manakah yang memulakan perkataan ini?","Which letter begins this word?"));
            for(String choice:LessonBank.initials(item,round.index+round.batch))button(body,choice,()->{if(round.index==question)answerInitial(choice);},false).setTextSize(large?32:28);
        }else if(round.mode==2){buildTiles(item);}
        else{
            note(t("Padanan bergantung pada perkataan yang didengar oleh peranti. Ini bukan penilaian sebutan.","Matching depends on words heard by the device. This is not a pronunciation assessment."));
            button(body,speechPending?t("Menunggu peranti…","Waiting for device…"):t("Cuba sebut perkataan","Try saying the word"),this::recognize,true).setEnabled(!speechPending);
            button(body,t("Teruskan bersama ibu bapa","Continue with a parent"),()->{round.help();round.solve();feedback=t("Latihan bersama direkodkan. Bagus berusaha!","Guided practice recorded. Good effort!");render();},false);
        }
        if(!round.solved&&!feedback.isEmpty()){TextView f=text(body,feedback,18,TEAL);f.setAccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_POLITE);}
        if(!round.solved&&round.mode!=0)button(body,t("Tunjuk bantuan","Show a hint"),this::hint,false);
        button(body,t("Rehat sebentar","Take a break"),this::pause,false);
    }
    private void answerInitial(String choice){
        if(round.solved)return;
        if(word().text.startsWith(choice)){round.solve();feedback=t("✓ Ya! ","✓ Yes! ")+choice+" — "+word().text;}
        else{round.help();feedback=t("Mari cuba lagi. Kamu boleh dengar atau gunakan bantuan.","Let's try again. Listen or use a hint whenever you need.");}
        render();
    }
    private void buildTiles(LessonBank.Word item){
        note(t("Dengar dan susun suku kata mengikut urutan.","Listen and put the syllables in order."));
        List<String> tiles=LessonBank.tiles(item,round.index+round.batch);StringBuilder built=new StringBuilder();
        for(int n:selectedTiles)if(n>=0&&n<tiles.size())built.append(tiles.get(n));
        text(body,built.length()==0?t("Perkataan kamu: …","Your word: …"):built.toString(),30,PURPLE);
        LinearLayout row=new LinearLayout(this);body.addView(row);
        for(int i=0;i<tiles.size();i++){final int n=i;Button b=new Button(this);b.setText(tiles.get(i));b.setAllCaps(false);b.setTextSize(large?27:24);b.setMinHeight(dp(64));b.setEnabled(!selectedTiles.contains(i));b.setContentDescription(t("Suku kata ","Syllable ")+tiles.get(i));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-2,1);lp.setMargins(dp(3),dp(6),dp(3),dp(6));row.addView(b,lp);b.setOnClickListener(v->{if(selectedTiles.contains(n)||round.solved)return;selectedTiles.add(n);if(selectedTiles.size()==tiles.size()){StringBuilder value=new StringBuilder();for(int x:selectedTiles)value.append(tiles.get(x));if(value.toString().equals(item.text)){round.solve();feedback=t("✓ Kamu berjaya membina ","✓ You built ")+item.text+"!";}else{round.help();feedback=t("Belum lagi. Dengar dan cuba susun semula.","Not quite yet. Listen and try a new order.");selectedTiles.clear();}}render();});}
        if(!selectedTiles.isEmpty())button(body,t("Susun semula","Start again"),()->{selectedTiles.clear();render();},false);
    }
    private void hint(){round.help();LessonBank.Word item=word();new AlertDialog.Builder(this).setTitle(t("Kita cuba bersama","Let's try together")).setMessage(item.text+"\n\n"+String.join(" · ",item.parts())+(round.mode==1?"\n\n"+t("Huruf awal: ","First letter: ")+item.text.substring(0,1):"")).setPositiveButton(t("Cuba lagi","Try again"),null).show();}
    private void next(){
        if(round==null||!round.next())return;
        feedback="";selectedTiles.clear();if(voice!=null)voice.stop();
        if(round.finished){store.complete(round);page="complete";}render();
    }
    private void complete(){
        title(t("Usaha kamu membuat taman tumbuh!","Your effort helps the garden grow!"));
        GardenView garden=new GardenView(this,store.child(round.child).optInt("rounds"));body.addView(garden,new LinearLayout.LayoutParams(-1,dp(170)));
        card(t("+5 bintang usaha","+5 effort stars"),t("Lima perkataan selesai. Bantuan dan cubaan semula juga sebahagian daripada pembelajaran.","Five words explored. Hints and retries are part of learning too."),GOLD);
        if(round.mode==1||round.mode==2)note(round.independent+t(" / 5 jawapan tanpa bantuan dalam pusingan ini."," / 5 answers without help in this round."));
        card(t("Lencana taman","Garden badge"),badge(store.child(round.child).optInt("rounds")),MINT);
        note(t("Regangkan badan, minum air, atau berehat sebentar.","Stretch, have a drink, or take a short break."));
        button(body,t("Kembali ke taman","Back to my garden"),()->go("home"),true);button(body,t("Lihat kemajuan","View progress"),()->go("progress"),false);
    }
    private void recognize(){
        if(speechPending||round==null||round.solved)return;
        if(voice!=null)voice.stop();Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ms-MY");intent.putExtra(RecognizerIntent.EXTRA_PROMPT,t("Cuba sebut: ","Try saying: ")+word().text);intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);
        speechRound=round.id;speechIndex=round.index;speechPending=true;
        try{speech.launch(intent);}catch(ActivityNotFoundException|SecurityException e){speechPending=false;feedback=t("Perkhidmatan pertuturan tiada. Teruskan bersama ibu bapa atau pilih aktiviti lain.","Speech recognition is unavailable. Continue with a parent or choose another activity.");render();}
    }
    private void say(String value){
        if(!voiceReady){new AlertDialog.Builder(this).setTitle(t("Sediakan audio Bahasa Melayu","Set up Malay audio")).setMessage(t("Pasang atau aktifkan suara Bahasa Melayu dalam tetapan teks ke pertuturan Android. Muat turun suara untuk kegunaan luar talian.","Install or enable a Malay voice in Android text-to-speech settings. Download a voice for offline use.")).setPositiveButton(t("Buka tetapan","Open settings"),(d,w)->{try{startActivity(new Intent("com.android.settings.TTS_SETTINGS"));}catch(ActivityNotFoundException e){toast(t("Buka tetapan teks ke pertuturan pada peranti.","Open text-to-speech settings on your device."));}}).setNegativeButton(t("Kembali","Back"),null).show();return;}
        voice.setSpeechRate(slow?.7f:.9f);if(voice.speak(value,TextToSpeech.QUEUE_FLUSH,null,"katabloom-word")==TextToSpeech.ERROR)toast(t("Audio tidak dapat dimainkan.","Audio could not play."));
    }
    private void updateAudioUi(){if(audioButton!=null)audioButton.setText(voiceReady?t("Dengar perkataan","Listen to the word"):t("Bantuan audio","Audio help"));if(audioNote!=null)audioNote.setVisibility(voiceReady?View.GONE:View.VISIBLE);}
    private void pause(){new AlertDialog.Builder(this).setTitle(t("Masa untuk berehat?","Time for a break?")).setMessage(t("Pusingan ini belum disimpan. Sambung sekarang atau kembali ke taman.","This round is not saved yet. Continue now or return to the garden.")).setNegativeButton(t("Sambung","Continue"),null).setPositiveButton(t("Ke taman","To the garden"),(d,w)->{round=null;selectedTiles.clear();go("home");}).show();}

    private void progress(){
        note(t("JURNAL PEMBELAJARAN","LEARNING JOURNAL"));title(t("Lihat betapa kamu berkembang","Look how you are growing"));JSONObject child=store.selected();
        if(child==null){note(t("Tambah pelajar untuk melihat kemajuan.","Add a learner to see their progress."));button(body,t("Tambah pelajar","Add a learner"),()->editLearner(null),true);return;}
        note(childName()+t(" · Rekod pada peranti ini"," · Records on this device"));
        int rounds=child.optInt("rounds"),questions=child.optInt("questions"),correct=child.optInt("independent");
        metrics("★ "+child.optInt("stars"),t("Bintang usaha","Effort stars"),String.valueOf(rounds),t("Pengembaraan selesai","Completed adventures"));
        LinearLayout week=card(t("Rentak minggu ini","Your week in bloom"),t("Pengembaraan selesai dalam tujuh hari terakhir.","Completed adventures over the last seven days."),MINT);
        int[] counts=new int[7];String[] labels=new String[7];StringBuilder accessible=new StringBuilder();Calendar day=Calendar.getInstance();day.add(Calendar.DAY_OF_YEAR,-6);
        for(int i=0;i<7;i++){String date=ProgressStats.date(day.getTimeInMillis());labels[i]=new java.text.SimpleDateFormat("EEE",en?Locale.ENGLISH:new Locale("ms","MY")).format(day.getTime());for(int j=0;j<store.records().length();j++){JSONObject r=store.records().optJSONObject(j);if(r.optString("child").equals(store.selectedId())&&r.optString("date").equals(date))counts[i]++;}accessible.append(date).append(": ").append(counts[i]).append(". ");day.add(Calendar.DAY_OF_YEAR,1);}
        WeekView chart=new WeekView(this,counts,labels);chart.setContentDescription(t("Aktiviti tujuh hari: ","Seven-day activity: ")+accessible);week.addView(chart,new LinearLayout.LayoutParams(-1,dp(170)));
        text(week,ProgressStats.streak(store.dates(child),System.currentTimeMillis())+t(" hari latihan berturut-turut. Mulakan semula bila-bila masa."," consecutive practice days. You can start fresh anytime."),16,TEAL);
        LinearLayout answers=card(t("Jawapan tanpa bantuan","Answers without help"),"",LILAC);
        RingView ring=new RingView(this,correct,questions);ring.setContentDescription(questions==0?t("Belum ada jawapan untuk dibandingkan","No answers to compare yet"):correct+" / "+questions+t(" jawapan tanpa bantuan"," answers without help"));answers.addView(ring,new LinearLayout.LayoutParams(-1,dp(140)));
        text(answers,questions==0?t("Belum ada aktiviti huruf atau bina perkataan selesai.","No letter or word-building activities completed yet."):correct+" / "+questions+"\n"+t("Aktiviti huruf dan bina perkataan sahaja. Petunjuk dan cubaan semula dikira sebagai bantuan.","Letter and word-building activities only. Hints and retries count as help."),17,INK);
        badgeJourney(rounds);
        text(body,t("Latihan terkini","Recent practice"),24,INK);int shown=0;
        for(int i=store.records().length()-1;i>=0&&shown<10;i--){JSONObject r=store.records().optJSONObject(i);if(!r.optString("child").equals(store.selectedId()))continue;shown++;card(mode(r.optInt("mode")),level(r.optInt("level"))+" · "+r.optString("date")+"\n"+(r.optInt("mode")==1||r.optInt("mode")==2?r.optInt("independent")+t(" / 5 tanpa bantuan"," / 5 without help"):t("Latihan selesai","Practice completed")),white?0xfff2f5f2:0xfff1efe7);}
        if(shown==0)note(t("Belum ada latihan selesai. Mari mulakan satu pengembaraan.","No completed practice yet. Let's start an adventure."));
        note(t("Paparan ini bukan diagnosis atau ukuran penguasaan membaca. Log menyimpan sehingga 500 aktiviti terkini; jumlah bintang dan pusingan kekal.","This is not a diagnosis or a reading mastery measure. The log keeps up to 500 recent activities; star and round totals remain."));
        button(body,t("Eksport laporan teks","Export text report"),()->parentGate(()->{Intent intent=new Intent(Intent.ACTION_CREATE_DOCUMENT);intent.setType("text/plain");intent.addCategory(Intent.CATEGORY_OPENABLE);intent.putExtra(Intent.EXTRA_TITLE,"KataBloom-progress.txt");try{export.launch(intent);}catch(ActivityNotFoundException e){notice(t("Eksport tidak tersedia","Export unavailable"),t("Peranti tiada pemilih dokumen.","No document picker is installed."));}}),false);
    }
    private void badgeJourney(int rounds){
        int target=rounds<1?1:rounds<3?3:10;
        LinearLayout c=card(t("Jejak lencana kamu","Your badge trail"),rounds>=10?t("Tiga lencana diperoleh. Teruskan meneroka!","All three badges earned. Keep exploring!"):t("Langkah kecil menuju lencana seterusnya.","Small steps towards your next badge."),GOLD);
        ProgressBar bar=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);bar.setMax(target);bar.setProgress(Math.min(rounds,target));bar.setProgressTintList(ColorStateList.valueOf(PURPLE));bar.setContentDescription(Math.min(rounds,target)+" / "+target+t(" pengembaraan menuju lencana"," adventures towards a badge"));c.addView(bar,new LinearLayout.LayoutParams(-1,dp(10)));
        int[] goals={1,3,10};String[] names={t("Benih berani","Brave seed"),t("Tunas rajin","Growing sprout"),t("Pokok ilmu","Knowledge tree")};
        for(int i=0;i<3;i++)text(c,(rounds>=goals[i]?"✓ ":"○ ")+names[i]+"  ·  "+goals[i]+t(" pengembaraan"," adventures"),17,rounds>=goals[i]?TEAL:MUTED);
        text(c,Math.min(rounds,target)+" / "+target+t(" selesai"," completed"),16,PURPLE);
    }
    private String reportText(){
        JSONObject c=store.selected();if(c==null)return "KataBloom — no learner selected";
        StringBuilder out=new StringBuilder("KataBloom\n");out.append(t("Pelajar: ","Learner: ")).append(c.optString("name")).append('\n').append(t("Umur: ","Age: ")).append(c.optInt("age")).append('\n').append(t("Pusingan selesai: ","Completed rounds: ")).append(c.optInt("rounds")).append('\n').append(t("Bintang usaha: ","Effort stars: ")).append(c.optInt("stars")).append('\n').append(t("Jawapan tanpa bantuan: ","Answers without help: ")).append(c.optInt("independent")).append('/').append(c.optInt("questions")).append("\n\n");
        for(int i=store.records().length()-1;i>=0;i--){JSONObject r=store.records().optJSONObject(i);if(r.optString("child").equals(c.optString("id")))out.append(r.optString("date")).append(" | ").append(level(r.optInt("level"))).append(" | ").append(mode(r.optInt("mode"))).append(" | ").append((r.optInt("mode")==1||r.optInt("mode")==2)?r.optInt("independent")+"/5":"—").append('\n');}
        return out.append('\n').append(t("Rekod latihan sahaja; bukan penilaian klinikal atau bukti penguasaan.","Practice records only; not a clinical assessment or evidence of mastery.")).toString();
    }
    private void profiles(){
        title(t("Pelajar kecil kita","Our little learners"));note(t("Setiap pelajar mempunyai taman dan rekod sendiri.","Each learner has their own garden and practice records."));
        for(int i=0;i<store.children().length();i++){JSONObject child=store.children().optJSONObject(i);String id=child.optString("id");LinearLayout c=card(child.optString("name"),child.optInt("age")+t(" tahun"," years old")+(id.equals(store.selectedId())?t(" · Dipilih"," · Selected"):""),id.equals(store.selectedId())?MINT:LILAC);button(c,t("Pilih & belajar","Select & learn"),()->{store.select(id);go("learn");},true);button(c,t("Ubah profil","Edit profile"),()->parentGate(()->editLearner(child)),false);}
        button(body,t("Tambah pelajar","Add a learner"),()->parentGate(()->editLearner(null)),true);
        button(body,t("Kembali ke taman","Back to the garden"),()->go("home"),false);
    }
    private EditText field(LinearLayout container,String label,String value,int type){
        text(container,label,17,INK);EditText edit=new EditText(this);edit.setText(value);edit.setTextSize(large?22:19);edit.setTextColor(INK);edit.setHintTextColor(MUTED);edit.setInputType(type);edit.setSingleLine(true);edit.setSelectAllOnFocus(false);edit.setMinHeight(dp(56));edit.setPadding(dp(12),dp(8),dp(12),dp(8));edit.setBackground(background(0xfff0f2ef,12));edit.setContentDescription(label);container.addView(edit,new LinearLayout.LayoutParams(-1,-2));return edit;
    }
    private ScrollView formScroll(LinearLayout form){ScrollView scroll=new ScrollView(this);scroll.addView(form);return scroll;}
    private void editLearner(JSONObject existing){
        LinearLayout form=column();form.setPadding(dp(24),dp(8),dp(24),dp(8));EditText name=field(form,t("Nama panggilan (maksimum 40 aksara)","Nickname (up to 40 characters)"),existing==null?"":existing.optString("name"),InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_WORDS);EditText age=field(form,t("Umur (7–11)","Age (7–11)"),existing==null?"":existing.optString("age"),InputType.TYPE_CLASS_NUMBER);
        AlertDialog.Builder builder=new AlertDialog.Builder(this).setTitle(existing==null?t("Tambah pelajar","Add a learner"):t("Ubah pelajar","Edit learner")).setView(formScroll(form)).setPositiveButton(t("Simpan","Save"),null).setNegativeButton(t("Batal","Cancel"),null);
        if(existing!=null)builder.setNeutralButton(t("Padam","Delete"),(d,w)->new AlertDialog.Builder(this).setTitle(t("Padam profil ini?","Delete this learner?")).setMessage(t("Profil, bintang dan semua rekod tempatan pelajar ini akan dipadam. Sandaran awan tidak diubah sehingga disimpan semula.","This learner's profile, stars and local records will be deleted. Cloud backups stay unchanged until saved again.")).setNegativeButton(t("Simpan profil","Keep profile"),null).setPositiveButton(t("Padam","Delete"),(x,y)->{store.delete(existing.optString("id"));go("profiles");}).show());
        AlertDialog dialog=builder.create();dialog.setOnShowListener(d->dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v->{
            try{int years=Integer.parseInt(age.getText().toString().trim());if(existing==null)store.add(name.getText().toString(),years);else store.edit(existing.optString("id"),name.getText().toString(),years);dialog.dismiss();go("home");}
            catch(IllegalArgumentException e){name.setError(t("Masukkan nama 1–40 aksara dan umur 7–11. Maksimum 20 pelajar.","Enter a name of 1–40 characters and age 7–11. Maximum 20 learners."));}
        }));dialog.show();
    }
    private void settingsPage(){
        title(t("Selesa untuk kamu","Make yourself comfortable"));
        LinearLayout display=card(t("Pilihan membaca","Reading preferences"),t("Pilih paparan yang paling selesa.","Choose what feels comfortable."),MINT);
        toggle(display,t("Teks lebih besar","Larger text"),large,"large",value->large=value);
        toggle(display,t("Jarak huruf tambahan","More letter spacing"),wide,"wide",value->wide=value);
        toggle(display,t("Latar putih","White background"),white,"white",value->white=value);
        toggle(display,t("Audio lebih perlahan","Slower audio"),slow,"slow",value->slow=value);
        button(display,en?"Bahasa Melayu":"English",()->{en=!en;settings.edit().putBoolean("english",en).apply();render();},false);
        button(body,t("Urus pelajar","Manage learners"),()->parentGate(()->go("profiles")),false);
        button(body,t("Akaun & sandaran ibu bapa","Parent account & backup"),()->parentGate(this::cloudOptions),false);
        LinearLayout session=card(t("Sesi ibu bapa","Parent session"),owner.equals("local")?t("Mod tempatan · Tiada akaun dilog masuk.","Local mode · No account is signed in."):(auth!=null&&auth.getCurrentUser()!=null?auth.getCurrentUser().getEmail():t("Sesi telah tamat","Session expired")),LILAC);
        if(owner.equals("local")){
            button(session,t("Log masuk","Sign in"),()->parentGate(()->go("account")),true);
            button(session,t("Daftar akaun","Sign up"),()->parentGate(()->go("register")),false);
        }
        button(session,owner.equals("local")?t("Keluar ke halaman mula","Return to welcome"):t("Log keluar","Sign out"),this::endSession,false);
        card(t("Belajar bersama","Learn together"),t("Cuba satu pusingan pendek. Dengar dahulu, beri masa, dan rehat bila perlu. Bintang diberi untuk usaha; petunjuk tidak mengurangkan ganjaran.","Try one short round. Listen first, allow time and take breaks. Stars reward effort; hints do not reduce rewards."),GOLD);
        card(t("Privasi & skop","Privacy & learning scope"),t("Profil dan keputusan disimpan dalam storan peribadi aplikasi. Tiada iklan atau rakaman suara disimpan oleh KataBloom. Perkhidmatan pertuturan peranti mungkin menggunakan rangkaian dan tertakluk pada privasinya sendiri.\n\nLatihan ini bukan diagnosis atau rawatan disleksia. Audio sintetik ialah sebutan perkataan, bukan rakaman fonem. Sila semak kandungan bersama guru.","Profiles and results stay in app-private storage. KataBloom stores no audio recordings and contains no ads. Your device's speech service may use a network and has its own privacy terms.\n\nThis practice is not a dyslexia diagnosis or treatment. Synthetic audio reads words; it is not recorded phoneme audio. Review content with a teacher."),LILAC);
        note("KataBloom 2.2.1 · "+t("Direka untuk telefon & tablet","Designed for phones & tablets"));
    }
    interface Flag{void set(boolean value);}
    private void toggle(LinearLayout container,String label,boolean checked,String key,Flag flag){androidx.appcompat.widget.SwitchCompat s=new androidx.appcompat.widget.SwitchCompat(this);s.setText(label);s.setTextColor(INK);s.setTextSize(large?21:18);s.setMinHeight(dp(64));s.setPadding(0,dp(8),0,dp(8));s.setChecked(checked);container.addView(s,new LinearLayout.LayoutParams(-1,-2));s.setOnCheckedChangeListener((b,value)->{flag.set(value);settings.edit().putBoolean(key,value).apply();render();});}
    private void parentGate(Runnable action){
        int a=3+new Random().nextInt(7),b=2+new Random().nextInt(7);LinearLayout form=column();form.setPadding(dp(24),0,dp(24),0);EditText answer=field(form,t("Untuk ibu bapa: ","For a parent: ")+a+" + "+b+" = ?","",InputType.TYPE_CLASS_NUMBER);
        AlertDialog dialog=new AlertDialog.Builder(this).setTitle(t("Ruang ibu bapa","Parent area")).setMessage(t("Minta ibu bapa membantu. Ini mengelakkan perubahan tidak sengaja, bukan kunci keselamatan.","Ask a parent to help. This prevents accidental changes; it is not a security lock.")).setView(formScroll(form)).setNegativeButton(t("Kembali","Back"),null).setPositiveButton(t("Teruskan","Continue"),null).create();dialog.setOnShowListener(d->dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v->{if(answer.getText().toString().trim().equals(String.valueOf(a+b))){dialog.dismiss();action.run();}else answer.setError(t("Cuba sekali lagi","Try again"));}));dialog.show();
    }
    private void cloudOptions(){
        if(auth==null){notice(t("Akaun belum tersedia","Accounts unavailable"),t("Konfigurasi Firebase belum tersedia. Semua latihan tempatan masih boleh digunakan.","Firebase configuration is unavailable. All local learning still works."));return;}
        if(auth.getCurrentUser()==null){go("account");return;}
        String[] options={t("Simpan sandaran sekarang","Save backup now"),t("Pulihkan sandaran awan","Restore cloud backup"),t("Import profil versi lama","Import original child profiles"),t("Log keluar","Sign out")};
        new AlertDialog.Builder(this).setTitle(auth.getCurrentUser().getEmail()).setItems(options,(d,n)->{if(n==0)backup();else if(n==1)restore();else if(n==2)importLegacy();else new AlertDialog.Builder(this).setTitle(t("Log keluar?","Sign out?")).setMessage(t("Data akaun kekal pada peranti ini. Mod tempatan mempunyai profil berasingan.","Account data remains on this device. Local mode has separate profiles.")).setNegativeButton(t("Batal","Cancel"),null).setPositiveButton(t("Log keluar","Sign out"),(x,w)->{auth.signOut();switchOwner();go("landing");}).show();}).show();
    }
    private void switchOwner(){owner=auth!=null&&auth.getCurrentUser()!=null?auth.getCurrentUser().getUid():"local";store=new LearnerStore(this,owner);round=null;selectedTiles.clear();}
    private void endSession(){
        new AlertDialog.Builder(this).setTitle(owner.equals("local")?t("Kembali ke halaman mula?","Return to welcome?"):t("Log keluar?","Sign out?")).setMessage(t("Profil dan kemajuan kekal disimpan pada peranti ini.","Profiles and progress stay saved on this device.")).setNegativeButton(t("Batal","Cancel"),null).setPositiveButton(t("Teruskan","Continue"),(d,w)->{if(auth!=null)auth.signOut();busy=false;switchOwner();go("landing");}).show();
    }
    private void account(boolean register){
        title(register?t("Akaun ibu bapa baharu","Create a parent account"):t("Selamat kembali","Welcome back"));note(t("Akaun adalah pilihan. Profil akaun dan profil tempatan disimpan berasingan.","Accounts are optional. Account profiles and local profiles are stored separately."));
        if(auth==null)note(t("Akaun belum tersedia. Pembelajaran tempatan masih boleh digunakan.","Account services are unavailable. You can continue learning locally."));
        LinearLayout form=card("","",white?0xfff2f5f2:LILAC);EditText email=field(form,t("E-mel ibu bapa","Parent email"),"",InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);EditText password=field(form,t("Kata laluan (sekurang-kurangnya 6 aksara)","Password (at least 6 characters)"),"",InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);EditText confirm=register?field(form,t("Sahkan kata laluan","Confirm password"),"",InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD):null;
        Button submit=button(form,busy?t("Sila tunggu…","Please wait…"):register?t("Daftar","Create account"):t("Log masuk","Sign in"),()->{
            String address=email.getText().toString().trim(),pass=password.getText().toString();
            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(address).matches()){email.setError(t("Masukkan e-mel yang sah","Enter a valid email"));return;}
            if(pass.length()<6){password.setError(t("Sekurang-kurangnya 6 aksara","At least 6 characters"));return;}
            if(register&&!pass.equals(confirm.getText().toString())){confirm.setError(t("Kata laluan tidak sepadan","Passwords do not match"));return;}
            busy=true;setEnabled(form,false);
            com.google.android.gms.tasks.Task<AuthResult> task=register?auth.createUserWithEmailAndPassword(address,pass):auth.signInWithEmailAndPassword(address,pass);
            task.addOnCompleteListener(result->{if(isDestroyed())return;busy=false;if(result.isSuccessful()){switchOwner();go("home");notice(t("Akaun sedia","Account ready"),t("Tambah pelajar atau pulihkan sandaran melalui Tetapan. Profil tempatan tidak dipindahkan secara automatik.","Add a learner or restore your backup in Settings. Local profiles are not moved automatically."));}else{render();notice(register?t("Tidak dapat mendaftar","Could not create account"):t("Tidak dapat log masuk","Could not sign in"),t("Semak sambungan, e-mel dan kata laluan. Untuk pendaftaran, cuba e-mel lain atau kata laluan yang lebih kukuh jika perlu.","Check your connection, email and password. For registration, try another email or a stronger password if needed."));}});
        },true);submit.setEnabled(!busy&&auth!=null);
        if(!register&&auth!=null)button(body,t("Lupa kata laluan","Forgot password"),()->{String address=email.getText().toString().trim();if(!android.util.Patterns.EMAIL_ADDRESS.matcher(address).matches()){email.setError(t("Masukkan e-mel dahulu","Enter your email first"));return;}auth.sendPasswordResetEmail(address).addOnCompleteListener(this,result->notice(t("Tetapan semula kata laluan","Password reset"),result.isSuccessful()?t("Jika akaun wujud, ikut arahan dalam e-mel.","If the account exists, follow the email instructions."):t("Permintaan gagal. Semak sambungan dan cuba lagi.","The request failed. Check your connection and retry.")));},false);
        button(body,register?t("Sudah ada akaun? Log masuk","Already registered? Sign in"):t("Daftar akaun ibu bapa","Create a parent account"),()->{if(!busy)go(register?"account":"register");},false);button(body,t("Kembali ke halaman mula","Back to welcome"),()->{if(!busy)go("landing");},false);
    }
    private void setEnabled(View view,boolean enabled){view.setEnabled(enabled);if(view instanceof android.view.ViewGroup){android.view.ViewGroup group=(android.view.ViewGroup)view;for(int i=0;i<group.getChildCount();i++)setEnabled(group.getChildAt(i),enabled);}}
    private DocumentReference backupRef(){return FirebaseFirestore.getInstance().collection("users").document(owner).collection("dyslearn").document("state");}
    private void backup(){
        String snapshot=store.snapshot();if(snapshot.getBytes(StandardCharsets.UTF_8).length>700000){notice(t("Sandaran terlalu besar","Backup too large"),t("Eksport laporan tempatan dahulu.","Export a local report first."));return;}
        if(busy){toast(t("Permintaan sedang diproses.","A request is already running."));return;}
        busy=true;String account=owner;cloudStatus=t("Sandaran dihantar. Menunggu pengesahan pelayan.","Backup sent. Waiting for server confirmation.");notice(t("Sandaran","Backup"),cloudStatus);
        Map<String,Object> payload=new HashMap<>();payload.put("payload",snapshot);payload.put("updatedAt",FieldValue.serverTimestamp());
        backupRef().set(payload).addOnCompleteListener(this,result->{busy=false;if(!owner.equals(account))return;notice(t("Sandaran","Backup"),result.isSuccessful()?t("Sandaran disahkan oleh pelayan.","Backup confirmed by the server."):t("Sandaran gagal. Data peranti masih selamat. Semak rangkaian dan peraturan Firestore.","Backup failed. Device data is safe. Check the network and Firestore rules."));});
        handler.postDelayed(()->{if(busy&&owner.equals(account)){busy=false;notice(t("Belum disahkan","Not confirmed yet"),t("Tiada pengesahan pelayan. Penulisan mungkin masih dalam giliran dan akan dihantar apabila rangkaian pulih. Jangan anggap sandaran selesai.","No server confirmation yet. The write may remain queued until the network returns. Do not assume the backup is complete."));}},15000);
    }
    private void restore(){
        String account=owner;backupRef().get(Source.SERVER).addOnCompleteListener(this,result->{if(!owner.equals(account))return;if(!result.isSuccessful()){notice(t("Pulihan gagal","Restore failed"),t("Semak rangkaian dan akses Firestore. Data tempatan tidak diubah.","Check network and Firestore access. Local data was not changed."));return;}String snapshot=result.getResult().getString("payload");if(snapshot==null){notice(t("Tiada sandaran","No backup"),t("Belum ada sandaran versi ini.","There is no backup for this version yet."));return;}new AlertDialog.Builder(this).setTitle(t("Gantikan data peranti?","Replace device data?")).setMessage(t("Semua profil dan rekod versi ini akan diganti dengan sandaran awan. Eksport laporan dahulu jika perlu.","All profiles and records in this version will be replaced by the cloud backup. Export a report first if needed.")).setNegativeButton(t("Batal","Cancel"),null).setPositiveButton(t("Pulihkan","Restore"),(d,w)->{if(!owner.equals(account))return;try{store.restore(snapshot);go("home");}catch(JSONException|IllegalArgumentException e){notice(t("Data tidak serasi","Incompatible data"),t("Sandaran tidak sah. Data peranti tidak diubah.","The backup is invalid. Device data was not changed."));}}).show();});
    }
    private void importLegacy(){
        String account=owner;FirebaseFirestore.getInstance().collection("users").document(owner).collection("children").get(Source.SERVER).addOnCompleteListener(this,result->{if(!owner.equals(account))return;if(!result.isSuccessful()){notice(t("Import gagal","Import failed"),t("Semak sambungan dan peraturan Firestore.","Check your connection and Firestore rules."));return;}int before=store.children().length();for(DocumentSnapshot doc:result.getResult()){Object raw=doc.get("age");try{int age=Integer.parseInt(String.valueOf(raw));String name=doc.getString("name");if(name!=null&&age>=7&&age<=11)store.importChild(doc.getId(),name,age);}catch(IllegalArgumentException ignored){/* Invalid legacy profiles remain on the server for review. */}}go("profiles");notice(t("Import selesai","Import finished"),(store.children().length()-before)+t(" profil ditambah. Skor lama tidak ditukar kepada penguasaan atau bintang baharu."," profiles added. Old scores were not converted into mastery or new stars."));});
    }
    private void back(){if(page.equals("practice")){pause();return;}if(busy&&(page.equals("account")||page.equals("register"))){toast(t("Tunggu permintaan selesai.","Wait for the request to finish."));return;}if(page.equals("account")||page.equals("register")){go("landing");return;}if(page.equals("landing")||page.equals("home")){finish();return;}go("home");}
    private void notice(String heading,String message){if(!isFinishing()&&!isDestroyed())new AlertDialog.Builder(this).setTitle(heading).setMessage(message).setPositiveButton(t("Baik","OK"),null).show();}
    private void toast(String value){Toast.makeText(this,value,Toast.LENGTH_LONG).show();}
    @Override public void onSaveInstanceState(Bundle out){
        super.onSaveInstanceState(out);out.putString("owner",owner);out.putString("page",page);out.putString("feedback",feedback);out.putString("speechRound",speechRound);out.putInt("speechIndex",speechIndex);out.putBoolean("speechPending",speechPending);out.putIntegerArrayList("tiles",new ArrayList<>(selectedTiles));
        if(round!=null){Bundle b=new Bundle();b.putString("id",round.id);b.putString("child",round.child);b.putInt("level",round.level);b.putInt("mode",round.mode);b.putInt("batch",round.batch);b.putInt("index",round.index);b.putInt("independent",round.independent);b.putBoolean("helped",round.helped);b.putBoolean("solved",round.solved);b.putBoolean("finished",round.finished);out.putBundle("round",b);}
    }
    private void readRound(Bundle b){if(b==null)return;PracticeRound r=new PracticeRound();r.id=b.getString("id","");r.child=b.getString("child","");r.level=b.getInt("level");r.mode=b.getInt("mode");r.batch=b.getInt("batch");r.index=b.getInt("index");r.independent=b.getInt("independent");r.helped=b.getBoolean("helped");r.solved=b.getBoolean("solved");r.finished=b.getBoolean("finished");if(r.valid())round=r;}
    @Override protected void onStart(){super.onStart();if(auth!=null)auth.addAuthStateListener(accountChanged);}
    @Override protected void onStop(){if(auth!=null)auth.removeAuthStateListener(accountChanged);if(voice!=null)voice.stop();super.onStop();}
    @Override protected void onDestroy(){handler.removeCallbacksAndMessages(null);if(voice!=null){voice.stop();voice.shutdown();}super.onDestroy();}

    private static final class RingView extends View {
        private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);private final int correct,total;
        RingView(Context context,int correct,int total){super(context);this.correct=correct;this.total=total;}
        @Override protected void onDraw(Canvas canvas){super.onDraw(canvas);float density=getResources().getDisplayMetrics().density,cx=getWidth()/2f,cy=getHeight()/2f,radius=Math.min(getWidth(),getHeight())*.36f;paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(12*density);paint.setStrokeCap(Paint.Cap.ROUND);paint.setColor(0xffd8d1e8);canvas.drawCircle(cx,cy,radius,paint);if(total>0){paint.setColor(PURPLE);canvas.drawArc(cx-radius,cy-radius,cx+radius,cy+radius,-90,3.6f*ProgressStats.percent(correct,total),false,paint);}paint.setStyle(Paint.Style.FILL);paint.setColor(INK);paint.setTextAlign(Paint.Align.CENTER);paint.setTypeface(Typeface.create("sans-serif",Typeface.BOLD));paint.setTextSize(28*getResources().getDisplayMetrics().scaledDensity);String label=total==0?"—":ProgressStats.percent(correct,total)+"%";canvas.drawText(label,cx,cy-(paint.ascent()+paint.descent())/2,paint);}
    }
    private static final class WeekView extends View {
        private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);private final int[] counts;private final String[] labels;
        WeekView(Context context,int[] counts,String[] labels){super(context);this.counts=counts.clone();this.labels=labels.clone();}
        @Override protected void onDraw(Canvas canvas){super.onDraw(canvas);float density=getResources().getDisplayMetrics().density,base=getHeight()-28*density,available=Math.max(1,base-28*density),slot=getWidth()/7f;int max=1;for(int count:counts)max=Math.max(max,count);paint.setTextAlign(Paint.Align.CENTER);paint.setTypeface(Typeface.create("sans-serif",Typeface.NORMAL));for(int i=0;i<7;i++){float x=slot*(i+.5f),height=available*counts[i]/max;paint.setColor(i==6?PURPLE:TEAL);if(counts[i]>0)canvas.drawRoundRect(x-slot*.23f,base-height,x+slot*.23f,base,6*density,6*density,paint);else{paint.setColor(0xffb8cdb3);canvas.drawCircle(x,base-2*density,3*density,paint);}paint.setColor(INK);paint.setTextSize(13*getResources().getDisplayMetrics().scaledDensity);canvas.drawText(String.valueOf(counts[i]),x,base-height-9*density,paint);paint.setTextSize(11*getResources().getDisplayMetrics().scaledDensity);canvas.drawText(labels[i],x,getHeight()-7*density,paint);}}
    }
    /** Lightweight vector garden: scales to phones/tablets without bitmap downloads. */
    private static final class GardenView extends View {
        private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);private final int rounds;
        GardenView(Context context,int rounds){super(context);this.rounds=rounds;setLayerType(View.LAYER_TYPE_SOFTWARE,null);}
        @Override protected void onDraw(Canvas c){super.onDraw(c);float w=getWidth(),h=getHeight();p.setColor(0xffe4efdf);c.drawRoundRect(0,0,w,h,32,32,p);p.setColor(0xffffdc88);c.drawCircle(w*.82f,h*.24f,h*.12f,p);p.setColor(0xffc3d8be);c.drawOval(-w*.1f,h*.5f,w*.8f,h*1.5f,p);p.setColor(0xffa8c6a5);c.drawOval(w*.3f,h*.7f,w*1.2f,h*1.5f,p);float x=w*.45f,y=h*.79f;float stem=h*(rounds>=10?.51f:rounds>=3?.38f:.26f);p.setColor(0xff367566);p.setStrokeWidth(h*.035f);p.setStrokeCap(Paint.Cap.ROUND);c.drawLine(x,y,x,y-stem,p);p.setColor(0xff4e9372);c.drawOval(x-h*.2f,y-stem*.9f,x,y-stem*.45f,p);c.drawOval(x,y-stem*.75f,x+h*.21f,y-stem*.22f,p);p.setColor(0xffe2a171);c.drawRoundRect(x-h*.17f,y-h*.04f,x+h*.17f,y+h*.17f,12,12,p);if(rounds>=1){p.setColor(0xffb39bd0);c.drawCircle(x,y-stem,h*.095f,p);p.setColor(0xffffe49d);c.drawCircle(x,y-stem,h*.04f,p);}p.setColor(0xff26384b);c.drawCircle(x-h*.045f,y+h*.035f,h*.012f,p);c.drawCircle(x+h*.045f,y+h*.035f,h*.012f,p);}
    }
}
