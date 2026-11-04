package com.example.myproject;

public class ChildSessionManager {

    public static String userId;
    public static String childId;
    public static String childName;
    public static String childAge;

    public static void setChild(String uid, String cid, String name, String age) {
        userId = uid;
        childId = cid;
        childName = name;
        childAge = age;
    }

    public static boolean hasChild() {
        return childId != null && userId != null;
    }

    public static void clear() {
        userId = null;
        childId = null;
        childName = null;
        childAge = null;
    }
}