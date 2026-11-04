package com.example.myproject.phonics;

import java.util.ArrayList;
import java.util.List;

public class PhonicsDatabase {

    public static List<PhonicsItem> getAll() {

        List<PhonicsItem> list = new ArrayList<>();

        list.add(new PhonicsItem("A", "/æ/", "Apple"));
        list.add(new PhonicsItem("B", "/b/", "Ball"));
        list.add(new PhonicsItem("C", "/k/", "Cat"));
        list.add(new PhonicsItem("D", "/d/", "Dog"));
        list.add(new PhonicsItem("E", "/e/", "Egg"));
        list.add(new PhonicsItem("F", "/f/", "Fish"));
        list.add(new PhonicsItem("G", "/g/", "Goat"));
        list.add(new PhonicsItem("H", "/h/", "Hat"));
        list.add(new PhonicsItem("I", "/ɪ/", "Ice"));
        list.add(new PhonicsItem("J", "/ʤ/", "Jam"));
        list.add(new PhonicsItem("K", "/k/", "Key"));
        list.add(new PhonicsItem("L", "/l/", "Lion"));
        list.add(new PhonicsItem("M", "/m/", "Monkey"));
        list.add(new PhonicsItem("N", "/n/", "Nest"));
        list.add(new PhonicsItem("O", "/ɒ/", "Orange"));
        list.add(new PhonicsItem("P", "/p/", "Pen"));
        list.add(new PhonicsItem("Q", "/kw/", "Queen"));
        list.add(new PhonicsItem("R", "/r/", "Rabbit"));
        list.add(new PhonicsItem("S", "/s/", "Sun"));
        list.add(new PhonicsItem("T", "/t/", "Tiger"));
        list.add(new PhonicsItem("U", "/ʌ/", "Umbrella"));
        list.add(new PhonicsItem("V", "/v/", "Van"));
        list.add(new PhonicsItem("W", "/w/", "Water"));
        list.add(new PhonicsItem("X", "/ks/", "Box"));
        list.add(new PhonicsItem("Y", "/j/", "Yoyo"));
        list.add(new PhonicsItem("Z", "/z/", "Zoo"));

        return list;
    }
}