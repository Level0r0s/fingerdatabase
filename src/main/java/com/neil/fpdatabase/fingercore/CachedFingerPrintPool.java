package com.neil.fpdatabase.fingercore;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nhu on 4/18/2017.
 */
public class CachedFingerPrintPool {

    private static Map<Integer, CachedFingerPrint> cachePool = new HashMap<>();

    public void set(Integer index, CachedFingerPrint cachedFingerPrint) {
        cachePool.put(index, cachedFingerPrint);
    }

    public CachedFingerPrint get(Integer index) {
        return cachePool.get(index);
    }
}
