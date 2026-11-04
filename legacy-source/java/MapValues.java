package com.example.myproject;
import java.util.Map;
/** API 23 compatible lookup, including absent nested profile maps. */
public final class MapValues {
    private MapValues() {}
    public static <K,V> V get(Map<K,V> map,K key,V fallback){
        return map==null?fallback:map.containsKey(key)?map.get(key):fallback;
    }
}
