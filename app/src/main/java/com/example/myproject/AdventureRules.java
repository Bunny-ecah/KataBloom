package com.example.myproject;

public final class AdventureRules {
    private AdventureRules() {}
    public static String[] options(String answer,int index){
        String[] result=new String[3];int slot=Math.floorMod(index,3);result[slot]=answer;
        String[] pool={"a","i","u","b","d","p","k","m"};int count=0;
        for(String letter:pool)if(!letter.equals(answer)){result[(slot+1+count)%3]=letter;if(++count==2)break;}
        return result;
    }
}
