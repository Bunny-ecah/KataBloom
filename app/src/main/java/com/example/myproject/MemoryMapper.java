package com.example.myproject;

import java.util.HashMap;
import java.util.Map;

public class MemoryMapper {

    public static Map<String, Integer> fromFirestore(
            int pc, int pw,
            int qc, int qw,
            int sc, int sw
    ) {
        Map<String, Integer> memory = new HashMap<>();

        memory.put("phonics_wrong", pw);
        memory.put("quiz_wrong", qw);
        memory.put("speech_wrong", sw);

        memory.put("phonics_total", pc + pw);
        memory.put("quiz_total", qc + qw);
        memory.put("speech_total", sc + sw);

        return memory;
    }
}