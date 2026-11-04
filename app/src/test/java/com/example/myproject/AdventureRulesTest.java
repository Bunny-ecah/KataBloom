package com.example.myproject;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.HashSet;
import com.example.myproject.core.RewardManager;
public class AdventureRulesTest {
    @Test public void absentProfileDataUsesFallback(){
        java.util.Map<String,Integer> values=new java.util.HashMap<>();
        assertEquals(Integer.valueOf(0),MapValues.get(values,"wrong",0));
        assertEquals(Integer.valueOf(0),MapValues.get(null,"wrong",0));
        values.put("wrong",4);assertEquals(Integer.valueOf(4),MapValues.get(values,"wrong",0));
    }
    @Test public void choicesContainOneAnswerAndNoDuplicates(){
        for(String answer:new String[]{"a","i","u","b","k","m","e"})for(int i=0;i<5;i++){
            String[] choices=AdventureRules.options(answer,i);
            assertEquals(3,new HashSet<>(Arrays.asList(choices)).size());
            assertEquals(answer,choices[i%3]);
        }
    }
    @Test public void streakBonusOnlyAtEveryThirdAnswer(){
        RewardManager r=new RewardManager();for(int i=0;i<4;i++)r.correctAnswer();assertEquals(11,r.getStars());
        r.wrongAnswer();assertEquals(0,r.getStreak());assertEquals(11,r.getStars());
        r.correctAnswer();assertEquals(13,r.getStars());
    }
}
