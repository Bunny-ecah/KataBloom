package com.example.myproject.learning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/** Bundled Malay word practice. Syllable cues are visual scaffolds, not phoneme audio. */
public final class LessonBank {
    public enum Mode { LISTEN, INITIAL, BUILD, SPEAK }
    public static final class Word {
        public final String text, syllables, picture;
        Word(String text,String syllables,String picture){this.text=text;this.syllables=syllables;this.picture=picture;}
        public String[] parts(){return syllables.split(" ");}
    }
    private static final Word[][] WORDS={
        {w("api","a pi","🔥"),w("ayam","a yam","🐔"),w("ikan","i kan","🐟"),w("ibu","i bu","👩"),w("ular","u lar","🐍"),w("itik","i tik","🦆"),w("ubi","u bi","🍠"),w("awan","a wan","☁"),w("ulat","u lat","🐛"),w("ubat","u bat","💊")},
        {w("buku","bu ku","📚"),w("bola","bo la","⚽"),w("susu","su su","🥛"),w("mata","ma ta","👁"),w("kaki","ka ki","🦶"),w("roti","ro ti","🍞"),w("gigi","gi gi","🦷"),w("baju","ba ju","👕"),w("nasi","na si","🍚"),w("topi","to pi","🧢")},
        {w("kelapa","ke la pa","🥥"),w("kereta","ke re ta","🚗"),w("kepala","ke pa la","🙂"),w("menara","me na ra","🏙"),w("pelangi","pe la ngi","🌈"),w("tomato","to ma to","🍅"),w("kamera","ka me ra","📷"),w("sekolah","se ko lah","🏫"),w("basikal","ba si kal","🚲"),w("harimau","ha ri mau","🐅")}
    };
    private static Word w(String text,String parts,String picture){return new Word(text,parts,picture);}
    public static Word word(int level,int round,int index){
        if(level<0||level>=3||round<0||index<0||index>=5)throw new IllegalArgumentException("Invalid lesson position");
        return WORDS[level][((round%2)*5+index)%WORDS[level].length];
    }
    public static List<String> initials(Word word,int seed){
        String answer=word.text.substring(0,1);
        List<String> choices=new ArrayList<>();choices.add(answer);
        for(String s:new String[]{"a","i","u","b","d","p","k","m","s","t"})if(!s.equals(answer)){choices.add(s);if(choices.size()==3)break;}
        Collections.rotate(choices,Math.floorMod(seed,3));return choices;
    }
    public static List<String> tiles(Word word,int seed){
        List<String> values=new ArrayList<>(Arrays.asList(word.parts()));
        Collections.rotate(values,1+Math.floorMod(seed,values.size()));
        if(String.join("",values).equals(word.text))Collections.reverse(values);
        return values;
    }
    public static String normalize(String value){return value==null?"":value.toLowerCase(Locale.ROOT).replaceAll("[^\\p{L}\\p{N}]","");}
    public static boolean matches(String target,List<String> candidates){
        if(candidates==null||normalize(target).isEmpty())return false;
        for(String candidate:candidates)if(normalize(target).equals(normalize(candidate)))return true;return false;
    }
    private LessonBank(){}
}
