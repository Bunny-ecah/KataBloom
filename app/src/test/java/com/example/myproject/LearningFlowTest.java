package com.example.myproject;

import com.example.myproject.learning.*;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

public class LearningFlowTest {
    @Test public void everyBundledWordHasCorrectSyllablesAndUniqueLetterChoices(){
        Set<String> words=new HashSet<>();
        for(int level=0;level<3;level++)for(int batch=0;batch<2;batch++)for(int index=0;index<5;index++){
            LessonBank.Word w=LessonBank.word(level,batch,index);assertTrue(words.add(w.text));assertEquals(w.text,String.join("",w.parts()));
            List<String> choices=LessonBank.initials(w,index);assertEquals(3,new HashSet<>(choices).size());assertTrue(choices.contains(w.text.substring(0,1)));
            List<String> tiles=LessonBank.tiles(w,index);List<String> expected=new ArrayList<>(Arrays.asList(w.parts()));Collections.sort(tiles);Collections.sort(expected);assertEquals(expected,tiles);
        }
        assertEquals(30,words.size());
    }
    @Test public void onlySolvedQuestionsAdvanceAndEachIsScoredOnce(){
        PracticeRound r=new PracticeRound();r.child="child";assertTrue(r.valid());assertFalse(r.next());
        for(int i=0;i<5;i++){assertEquals(i,r.index);assertTrue(r.solve());assertFalse(r.solve());assertTrue(r.next());}
        assertTrue(r.finished);assertEquals(5,r.independent);assertFalse(r.next());assertFalse(r.solve());assertTrue(r.valid());
    }
    @Test public void hintsAndRetriesDoNotCountAsIndependent(){
        PracticeRound r=new PracticeRound();r.child="child";r.help();r.solve();assertEquals(0,r.independent);r.next();assertFalse(r.helped);r.solve();assertEquals(1,r.independent);
    }
    @Test public void speechRequiresAnExactNormalizedWordAndChecksAlternatives(){
        assertTrue(LessonBank.matches("ikan",Arrays.asList("bukan","Ikan!")));assertFalse(LessonBank.matches("ikan",Arrays.asList("ikan besar")));assertFalse(LessonBank.matches("",Arrays.asList("")));assertFalse(LessonBank.matches("api",null));
    }
    @Test public void streakCanIncludeYesterdayWithoutPenalizingToday(){
        Calendar today=Calendar.getInstance();today.set(2026,Calendar.NOVEMBER,4,12,0,0);long now=today.getTimeInMillis();Set<String> dates=new HashSet<>();dates.add("2026-11-03");dates.add("2026-11-02");assertEquals(2,ProgressStats.streak(dates,now));dates.add("2026-11-04");assertEquals(3,ProgressStats.streak(dates,now));dates.remove("2026-11-03");assertEquals(1,ProgressStats.streak(dates,now));assertEquals(0,ProgressStats.streak(Collections.emptySet(),now));
    }
    @Test public void invalidRestoredPositionsAreRejected(){PracticeRound r=new PracticeRound();r.child="child";r.index=5;assertFalse(r.valid());r.index=0;r.finished=true;assertFalse(r.valid());}
}
