package com.example.myproject;

import java.util.ArrayList;
import java.util.List;

public class PhonicsData {

    public static List<PhonicsModel> getAZ() {

        List<PhonicsModel> list = new ArrayList<>();

        list.add(new PhonicsModel("A", "/æ/", "Apple", false));
        list.add(new PhonicsModel("B", "/b/", "Ball", false));
        list.add(new PhonicsModel("C", "/k/", "Cat", false));
        list.add(new PhonicsModel("D", "/d/", "Dog", false));
        list.add(new PhonicsModel("E", "/e/", "Egg", false));
        list.add(new PhonicsModel("F", "/f/", "Fish", false));
        list.add(new PhonicsModel("G", "/g/", "Goat", false));
        list.add(new PhonicsModel("H", "/h/", "Hat", false));
        list.add(new PhonicsModel("I", "/ɪ/", "Ice", false));
        list.add(new PhonicsModel("J", "/j/", "Jam", false));
        list.add(new PhonicsModel("K", "/k/", "Kite", false));
        list.add(new PhonicsModel("L", "/l/", "Lion", false));
        list.add(new PhonicsModel("M", "/m/", "Monkey", false));
        list.add(new PhonicsModel("N", "/n/", "Nest", false));
        list.add(new PhonicsModel("O", "/ɒ/", "Orange", false));
        list.add(new PhonicsModel("P", "/p/", "Pen", false));
        list.add(new PhonicsModel("Q", "/kw/", "Queen", false));
        list.add(new PhonicsModel("R", "/r/", "Rabbit", false));
        list.add(new PhonicsModel("S", "/s/", "Sun", false));
        list.add(new PhonicsModel("T", "/t/", "Tiger", false));
        list.add(new PhonicsModel("U", "/ʌ/", "Umbrella", false));
        list.add(new PhonicsModel("V", "/v/", "Van", false));
        list.add(new PhonicsModel("W", "/w/", "Water", false));
        list.add(new PhonicsModel("X", "/ks/", "Box", false));
        list.add(new PhonicsModel("Y", "/j/", "Yoyo", false));
        list.add(new PhonicsModel("Z", "/z/", "Zebra", false));

        return list;
    }
}