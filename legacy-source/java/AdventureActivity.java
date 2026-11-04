package com.example.myproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.*;
import java.util.Locale;
import java.util.UUID;

/** Small offline practice rounds. Stars reward participation, never reading speed. */
public class AdventureActivity extends Activity {
    private static final String[][] WORDS={{"api","ayam","ikan","ibu","ular"},{"buku","bola","susu","mata","kaki"},{"kelapa","kereta","sepatu","kepala","menara"}};
    private static final String[][] PARTS={{"a · pi","a · yam","i · kan","i · bu","u · lar"},{"bu · ku","bo · la","su · su","ma · ta","ka · ki"},{"ke · la · pa","ke · re · ta","se · pa · tu","ke · pa · la","me · na · ra"}};
    private LinearLayout body;
    private SharedPreferences prefs, rewards;
    private TextToSpeech voice;
    private boolean ready, large, white, wide, solved, helped;
    private int level, mode, index, independent;
    private String screen="map", roundId="";
    private final int ink=Color.rgb(35,48,67), teal=Color.rgb(22,100,97);
    @Override public void onCreate(Bundle state){
        super.onCreate(state);
        prefs=getSharedPreferences("adventure_display",0);
        String owner=ChildSessionManager.userId, child=ChildSessionManager.childId;
        String learner=(owner==null||child==null)?"guest":owner+"_"+child;
        if(state!=null)learner=state.getString("learner",learner);
        getIntent().putExtra("learner",learner);
        rewards=getSharedPreferences("adventure_"+learner,0);
        large=prefs.getBoolean("large",false); white=prefs.getBoolean("white",false); wide=prefs.getBoolean("wide",false);
        if(state!=null){screen=state.getString("screen","map");level=state.getInt("level");mode=state.getInt("mode");index=state.getInt("index");independent=state.getInt("independent");solved=state.getBoolean("solved");helped=state.getBoolean("helped");roundId=state.getString("round","");}
        voice=new TextToSpeech(this,status->{if(status==TextToSpeech.SUCCESS){ready=voice.setLanguage(new Locale("ms","MY"))>=0;voice.setSpeechRate(.7f);}});
        render();
    }
    @Override public void onSaveInstanceState(Bundle b){super.onSaveInstanceState(b);b.putString("learner",getIntent().getStringExtra("learner"));b.putString("screen",screen);b.putInt("level",level);b.putInt("mode",mode);b.putInt("index",index);b.putInt("independent",independent);b.putBoolean("solved",solved);b.putBoolean("helped",helped);b.putString("round",roundId);}
    private int dp(int n){return Math.round(n*getResources().getDisplayMetrics().density);}
    private GradientDrawable bg(int color){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(22));return d;}
    private TextView text(LinearLayout parent,String value,int size){TextView t=new TextView(this);t.setText(value);t.setTextColor(ink);t.setTextSize(size+(large?4:0));t.setTypeface(Typeface.create("sans-serif",Typeface.NORMAL));t.setLetterSpacing(wide?.06f:0);t.setLineSpacing(0,1.4f);t.setPadding(0,dp(8),0,dp(8));parent.addView(t);return t;}
    private void button(LinearLayout parent,String label,Runnable action){Button b=new Button(this);b.setText(label);b.setAllCaps(false);b.setTextSize(large?24:20);b.setTextColor(Color.WHITE);b.setMinHeight(dp(60));b.setPadding(dp(16),dp(12),dp(16),dp(12));b.setBackground(bg(teal));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.setMargins(0,dp(7),0,dp(7));parent.addView(b,lp);b.setOnClickListener(v->action.run());}
    private LinearLayout card(String heading,String detail,int color){LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.VERTICAL);c.setPadding(dp(20),dp(12),dp(20),dp(16));c.setBackground(bg(color));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.setMargins(0,dp(12),0,dp(8));body.addView(c,lp);text(c,heading,26).setTypeface(null,Typeface.BOLD);if(!detail.isEmpty())text(c,detail,18);return c;}
    private void render(){
        ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);scroll.setBackgroundColor(white?Color.WHITE:Color.rgb(255,249,237));body=new LinearLayout(this);body.setOrientation(LinearLayout.VERTICAL);body.setPadding(dp(24),dp(24),dp(24),dp(32));scroll.addView(body);setContentView(scroll);
        scroll.setOnApplyWindowInsetsListener((v,insets)->{v.setPadding(insets.getSystemWindowInsetLeft(),insets.getSystemWindowInsetTop(),insets.getSystemWindowInsetRight(),insets.getSystemWindowInsetBottom());return insets;});
        if(screen.equals("map"))map();else if(screen.equals("settings"))settings();else if(screen.equals("done"))done();else play();
    }
    private void map(){
        text(body,"DysLearn • Taman kata",18);
        text(body,"Mari tumbuh bersama!",32).setTypeface(null,Typeface.BOLD);
        text(body,"Dengar. Cuba. Raikan usaha kamu.",20);
        int rounds=rewards.getInt("rounds",0),stars=rewards.getInt("stars",0);
        card("★ "+stars+" bintang",""+rounds+" pengembaraan selesai\n"+(rounds>=10?"Lencana: Pokok ilmu":rounds>=3?"Lencana: Tunas rajin":rounds>=1?"Lencana: Benih berani":"Selesaikan satu pusingan untuk lencana pertama."),Color.rgb(255,231,170));
        text(body,"Pilih pengembaraan",24);
        String[] names={"1 • Benih vokal","2 • Kebun perkataan","3 • Pokok suku kata"};
        for(int i=0;i<3;i++){final int n=i;LinearLayout c=card(names[i],i==0?"Perkataan pendek dan huruf awal":"Dengar dan bina perkataan.",i==1?Color.rgb(227,235,255):Color.rgb(220,240,224));button(c,"Teroka",()->choose(n));}
        button(body,"Paparan & panduan ibu bapa",()->{screen="settings";render();});
        button(body,"Kembali",this::finish);
    }
    private void choose(int n){new AlertDialog.Builder(this).setTitle("Pilih cara belajar").setItems(new String[]{"Dengar dan lihat","Cari huruf awal","Lengkapkan perkataan"},(d,which)->{level=n;mode=which;index=0;independent=0;solved=false;helped=false;roundId=UUID.randomUUID().toString();screen="play";render();}).show();}
    private void play(){
        String word=WORDS[level][index];
        text(body,"Taman kata • "+(index+1)+" / 5",18);
        ProgressBar p=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);p.setMax(5);p.setProgress(index+(solved?1:0));body.addView(p,new LinearLayout.LayoutParams(-1,dp(12)));
        text(body,mode==0?"Dengar dan lihat":mode==1?"Cari huruf awal":"Lengkapkan perkataan",28);
        LinearLayout c=card(mode==2?word.substring(0,1)+" _ "+word.substring(2):word,mode==0?PARTS[level][index]:"Dengar dahulu. Ambil masa kamu.",Color.rgb(220,240,224));
        button(c,"▶ Dengar perkataan",()->say(word));
        if(mode==0){text(body,"Sebut bersama jika kamu mahu.",20);button(body,index==4?"Selesai":"Perkataan seterusnya",()->advance());}
        else {
            String answer=word.substring(mode==1?0:1,mode==1?1:2);
            String[] options=AdventureRules.options(answer,index);
            if(!solved)for(String option:options)button(body,option,()->{if(solved)return;if(option.equals(answer)){solved=true;if(!helped)independent++;render();}else{helped=true;Toast.makeText(this,"Cuba lagi. Dengar perkataan sekali lagi.",Toast.LENGTH_SHORT).show();say(word);}});
            if(solved){TextView feedback=text(body,"✓ Bagus! "+word,24);feedback.setAccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_POLITE);button(body,index==4?"Selesai":"Seterusnya",this::advance);}
            else button(body,"Bantuan",()->{helped=true;new AlertDialog.Builder(this).setTitle("Kita cuba bersama").setMessage("Jawapan: "+answer+"\nPerkataan: "+word+"\n"+PARTS[level][index]).setPositiveButton("Cuba sekarang",null).show();});
        }
        button(body,"Rehat sebentar",()->new AlertDialog.Builder(this).setTitle("Mahu berehat?").setMessage("Bintang hanya disimpan selepas lima perkataan selesai.").setNegativeButton("Sambung",null).setPositiveButton("Ke taman",(d,w)->{screen="map";render();}).show());
    }
    private void advance(){if(index<4){index++;solved=false;helped=false;render();return;}if(!roundId.equals(rewards.getString("lastRound",""))){rewards.edit().putInt("stars",rewards.getInt("stars",0)+5).putInt("rounds",rewards.getInt("rounds",0)+1).putString("lastRound",roundId).apply();}screen="done";render();}
    private void done(){text(body,"★ Hebat, kamu terus mencuba!",30);card("+5 bintang usaha","Lima perkataan selesai. Bintang diberi untuk usaha, termasuk apabila kamu menggunakan bantuan.",Color.rgb(255,231,170));if(mode!=0)text(body,independent+" / 5 jawapan tanpa bantuan. Ulang bila kamu bersedia.",20);text(body,"Masa untuk regangkan badan atau berehat.",20);button(body,"Lihat taman saya",()->{screen="map";render();});}
    private void settings(){text(body,"Selesa untuk membaca",28);toggle("Teks lebih besar",large,v->{large=v;prefs.edit().putBoolean("large",v).apply();render();});toggle("Jarak huruf tambahan",wide,v->{wide=v;prefs.edit().putBoolean("wide",v).apply();render();});toggle("Latar putih",white,v->{white=v;prefs.edit().putBoolean("white",v).apply();render();});text(body,"Panduan ibu bapa",24);text(body,"Bantu anak membaca arahan. Cuba satu pusingan pendek tanpa had masa. Raikan usaha dan beri rehat.\n\nAudio memerlukan suara Bahasa Melayu pada peranti. Bintang disimpan pada peranti ini untuk profil yang dipilih; tetamu menggunakan profil latihan bersama. Ganjaran taman tidak disegerakkan ke Firebase.\n\nAktiviti ini latihan perkataan, bukan diagnosis atau penilaian klinikal. Audio sintetik bukan rakaman fonem. Kandungan perlu disemak oleh guru sebelum kajian FYP.",18);button(body,"Ke taman",()->{screen="map";render();});}
    interface Change {void set(boolean value);}
    private void toggle(String label,boolean checked,Change change){Switch s=new Switch(this);s.setText(label);s.setTextSize(20);s.setMinHeight(dp(64));s.setChecked(checked);body.addView(s);s.setOnCheckedChangeListener((b,v)->change.set(v));}
    private void say(String word){if(ready)voice.speak(word,TextToSpeech.QUEUE_FLUSH,null,"word");else Toast.makeText(this,"Aktifkan suara Bahasa Melayu dalam tetapan teks ke pertuturan peranti.",Toast.LENGTH_LONG).show();}
    @Override public void onBackPressed(){if(screen.equals("play")){new AlertDialog.Builder(this).setTitle("Tinggalkan pusingan?").setMessage("Pusingan ini belum disimpan.").setNegativeButton("Sambung",null).setPositiveButton("Ke taman",(d,w)->{screen="map";render();}).show();}else if(!screen.equals("map")){screen="map";render();}else super.onBackPressed();}
    @Override protected void onDestroy(){if(voice!=null){voice.stop();voice.shutdown();}super.onDestroy();}
}
