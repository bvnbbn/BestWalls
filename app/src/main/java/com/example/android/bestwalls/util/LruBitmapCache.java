package com.example.android.bestwalls.util;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by Vikas on 6/27/2016.
 */


//This class used to keep the volley cached objects on the disk.
public class LruBitmapCache extends LruCache<String,Bitmap> implements
        ImageLoader.ImageCache
{
public static int getDefaultLruCacheSize()

{
    // Get max available VM memory, exceeding this amount will throw an
    // OutOfMemory exception. Stored in kilobytes as LruCache takes an
    // int in its constructor.
    final int maxMemory = (int)(Runtime.getRuntime().maxMemory()/1024);

    // Use 1/8th of the available memory for this memory cache.
    final int cacheSize = maxMemory/8;
    return  cacheSize;
}

    public LruBitmapCache() {
        this(getDefaultLruCacheSize());
    }

    public LruBitmapCache(int sizeInKiloBytes) {
        super(sizeInKiloBytes);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight() / 1024;
    }

    @Override
    public Bitmap getBitmap(String url) {
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }



}
