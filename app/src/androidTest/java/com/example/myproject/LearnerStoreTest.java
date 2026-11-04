package com.example.myproject;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.myproject.learning.*;
import org.json.*;
import org.junit.*;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import java.util.UUID;

@RunWith(AndroidJUnit4.class)
public class LearnerStoreTest {
    private Context context;private String owner;private LearnerStore store;
    @Before public void setup(){context=InstrumentationRegistry.getInstrumentation().getTargetContext();assertTrue(context.getPackageName().endsWith(".qa"));owner="test_"+UUID.randomUUID();store=new LearnerStore(context,owner);}
    @After public void cleanup(){context.getSharedPreferences("dyslearn_v2_"+owner,0).edit().clear().commit();}
    private PracticeRound finished(String child){PracticeRound r=new PracticeRound();r.child=child;r.mode=1;for(int i=0;i<5;i++){r.solve();r.next();}return r;}
    @Test public void completionIsDurableIdempotentAndChildSpecific(){
        String first=store.add("Alya",8),second=store.add("Adam",9);PracticeRound r=finished(first);
        assertTrue(store.complete(r));assertFalse(store.complete(r));assertEquals(5,store.child(first).optInt("stars"));assertEquals(0,store.child(second).optInt("stars"));
        LearnerStore reopened=new LearnerStore(context,owner);assertEquals(1,reopened.child(first).optInt("rounds"));assertEquals(second,reopened.selectedId());assertEquals(1,reopened.records().length());
    }
    @Test public void deletingLearnerOnlyDeletesTheirResults(){String a=store.add("Alya",8),b=store.add("Adam",9);store.complete(finished(a));store.complete(finished(b));store.delete(a);assertNull(store.child(a));assertNotNull(store.child(b));assertEquals(1,store.records().length());assertEquals(b,store.records().optJSONObject(0).optString("child"));}
    @Test public void invalidRestoreLeavesExistingDataIntact() throws Exception {
        String id=store.add("Alya",8),before=store.snapshot();
        try{store.restore("{\"version\":2,\"children\":[{}],\"records\":[]}");fail("Invalid backup accepted");}catch(JSONException|IllegalArgumentException expected){}
        assertEquals(before,store.snapshot());assertNotNull(store.child(id));
    }
    @Test public void restorePreservesProfilesAndResults() throws Exception {String id=store.add("Alya",8);store.complete(finished(id));String snapshot=store.snapshot();store.delete(id);store.restore(snapshot);assertEquals(5,store.child(id).optInt("stars"));assertEquals(1,store.records().length());}
    @Test public void separateAccountsDoNotShareProfiles(){store.add("Alya",8);String another="test_"+UUID.randomUUID();LearnerStore other=new LearnerStore(context,another);assertEquals(0,other.children().length());context.getSharedPreferences("dyslearn_v2_"+another,0).edit().clear().commit();}
}
