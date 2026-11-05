package com.example.myproject;

import android.content.Context;
import androidx.test.core.app.ActivityScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.*;
import org.junit.runner.RunWith;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class MobileFlowTest {
    private void screenshot(String name){
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        android.os.SystemClock.sleep(350); // Let ScrollView's short scroll animation reach its final frame.
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        android.graphics.Bitmap bitmap=InstrumentationRegistry.getInstrumentation().getUiAutomation().takeScreenshot();assertNotNull(bitmap);
        Context c=InstrumentationRegistry.getInstrumentation().getTargetContext();
        try(java.io.OutputStream out=new java.io.FileOutputStream(new java.io.File(c.getExternalFilesDir(null),"qa-"+name+".png"))){bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG,100,out);}catch(java.io.IOException e){throw new AssertionError(e);}finally{bitmap.recycle();}
    }
    @Before public void cleanQaOnly(){Context c=InstrumentationRegistry.getInstrumentation().getTargetContext();assertTrue(c.getPackageName().endsWith(".qa"));for(String name:new String[]{"dyslearn_v2_local","dyslearn_display_v2","adventure_guest"})c.getSharedPreferences(name,0).edit().clear().commit();}
    @Test public void languageChoiceSurvivesRecreation(){
        try(ActivityScenario<KataBloomActivity> scenario=ActivityScenario.launch(KataBloomActivity.class)){
            onView(withText("KataBloom")).check(matches(isDisplayed()));
            onView(withText("English")).perform(click());onView(withText("Learn with confidence.")).check(matches(isDisplayed()));scenario.recreate();onView(withText("Learn with confidence.")).check(matches(isDisplayed()));screenshot("welcome-en");onView(withText("BM")).perform(click());onView(withText("Belajar dengan yakin.")).check(matches(isDisplayed()));
        }
    }
    @Test public void completeLetterRoundAndKeepRewardAfterRotation(){
        try(ActivityScenario<KataBloomActivity> scenario=ActivityScenario.launch(KataBloomActivity.class)){
            onView(withText("Teruskan tanpa akaun")).perform(scrollTo(),click());
            onView(withText("Cuba sebagai tetamu")).perform(scrollTo(),click());screenshot("lessons-ms");onView(withContentDescription("Mulakan Benih vokal")).perform(scrollTo(),click());onView(withText("Cari huruf awal")).perform(click());screenshot("practice-ms");
            String[] answers={"a","a","i","i","u"};
            for(int i=0;i<5;i++){onView(withText(answers[i])).perform(scrollTo(),click());if(i==0)scenario.recreate();onView(withText(i==4?"Selesaikan pengembaraan":"Perkataan seterusnya")).perform(scrollTo(),click());}
            onView(withText("+5 bintang usaha")).check(matches(isDisplayed()));scenario.recreate();onView(withText("+5 bintang usaha")).check(matches(isDisplayed()));screenshot("complete-ms");
            Context c=InstrumentationRegistry.getInstrumentation().getTargetContext();com.example.myproject.learning.LearnerStore store=new com.example.myproject.learning.LearnerStore(c,"local");assertEquals(5,store.selected().optInt("stars"));assertEquals(1,store.records().length());
            onView(withText("Kembali ke taman")).perform(scrollTo(),click());screenshot("home-ms");onView(withText("Kemajuan")).perform(click());onView(withText("Lihat betapa kamu berkembang")).check(matches(isDisplayed()));screenshot("progress-ms");onView(withContentDescription(org.hamcrest.Matchers.startsWith("Aktiviti tujuh hari: "))).perform(scrollTo());screenshot("week-ms");onView(withContentDescription("5 / 5 jawapan tanpa bantuan")).perform(scrollTo()).check(matches(isDisplayed()));screenshot("ring-ms");onView(withText("Tetapan")).perform(click());onView(withContentDescription("Tukar bahasa kepada English")).perform(click());onView(withText("Larger text")).perform(scrollTo(),click());screenshot("settings-en");
        }
    }
    @Test public void syllableRetryAndHintSurviveRotation(){
        try(ActivityScenario<KataBloomActivity> scenario=ActivityScenario.launch(KataBloomActivity.class)){
            onView(withText("Teruskan tanpa akaun")).perform(scrollTo(),click());
            onView(withText("Cuba sebagai tetamu")).perform(scrollTo(),click());onView(withContentDescription("Mulakan Benih vokal")).perform(scrollTo(),click());onView(withText("Bina perkataan")).perform(click());
            onView(withText("pi")).perform(scrollTo(),click());onView(withText("a")).perform(scrollTo(),click());
            onView(withText("Belum lagi. Dengar dan cuba susun semula.")).perform(scrollTo()).check(matches(isDisplayed()));
            onView(withText("a")).perform(scrollTo(),click());scenario.recreate();onView(withText("pi")).perform(scrollTo(),click());
            onView(withText("✓ Kamu berjaya membina api!")).check(matches(isDisplayed()));screenshot("build-ms");
            Context c=InstrumentationRegistry.getInstrumentation().getTargetContext();com.example.myproject.learning.LearnerStore store=new com.example.myproject.learning.LearnerStore(c,"local");assertEquals(0,store.selected().optInt("stars"));assertEquals(0,store.records().length());
        }
    }
    @Test public void addLearnerFromPhoneFormAndKeepSelection(){
        try(ActivityScenario<KataBloomActivity> scenario=ActivityScenario.launch(KataBloomActivity.class)){
            onView(withText("Teruskan tanpa akaun")).perform(scrollTo(),click());
            onView(withText("Tambah pelajar")).perform(scrollTo(),click());
            onView(withContentDescription("Nama panggilan (maksimum 40 aksara)")).perform(scrollTo(),click(),replaceText("Alya"),closeSoftKeyboard()).check(matches(withText("Alya")));
            onView(withContentDescription("Umur (7–11)")).perform(scrollTo(),click(),replaceText("8"),closeSoftKeyboard());
            onView(withText("Simpan")).perform(click());onView(withText("Hai, Alya!")).check(matches(isDisplayed()));scenario.recreate();onView(withText("Hai, Alya!")).check(matches(isDisplayed()));
        }
    }
    @Test public void landingHasAuthEntryAndSessionExit(){
        try(ActivityScenario<KataBloomActivity> scenario=ActivityScenario.launch(KataBloomActivity.class)){
            onView(withText("Log masuk")).perform(scrollTo(),click());onView(withText("E-mel ibu bapa")).check(matches(isDisplayed()));screenshot("signin-ms");onView(withText("Kembali ke halaman mula")).perform(scrollTo(),click());
            onView(withText("Daftar akaun")).perform(scrollTo(),click());onView(withText("Sahkan kata laluan")).perform(scrollTo()).check(matches(isDisplayed()));screenshot("signup-ms");onView(withText("Kembali ke halaman mula")).perform(scrollTo(),click());
            onView(withText("Teruskan tanpa akaun")).perform(scrollTo(),click());onView(withText("Tetapan")).perform(click());onView(withText("Keluar ke halaman mula")).perform(scrollTo(),click());onView(withText("Teruskan")).perform(click());onView(withText("Belajar dengan yakin.")).check(matches(isDisplayed()));
        }
    }
}
