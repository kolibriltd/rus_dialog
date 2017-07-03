package com.wearesputnik.istoria.helpers;

import android.content.Context;
import android.graphics.Typeface;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by admin on 23.06.17.
 */
public class Typefaces {
    private static final ConcurrentMap<String, Typeface> cache = new ConcurrentHashMap<String, Typeface>();

    public static Typeface get(Context c, String assetPath) {
        synchronized (cache) {
            if (!cache.containsKey(assetPath)) {
                try {
                    Typeface t = Typeface.createFromAsset(c.getAssets(),
                            assetPath);
                    cache.put(assetPath, t);
                } catch (Exception e) {
                    return null;
                }
            }
            return cache.get(assetPath);
        }
    }
}
