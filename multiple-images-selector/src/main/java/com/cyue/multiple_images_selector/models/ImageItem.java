package com.cyue.multiple_images_selector.models;

import android.util.Log;

import com.cyue.multiple_images_selector.SelectorSettings;

public class ImageItem {
    private static final String TAG = "ImageItem";
    public static final String CAMERA_PATH = "Camera";

    public String path;
    public String name;
    public long time;
    public String tPath;
    public int videoTime=0;
    public ImageItem(String name, String path, long time,String tPath,int videoTime){
        this.name = name;
        this.path = path;
        this.time = time;
        this.tPath = tPath;
        this.videoTime = videoTime;
    }

    public String getPath() {
        return path;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isCamera() {
        return this.path.equals(SelectorSettings.CAMERA_ITEM_PATH);
    }

    public boolean isVideo() {
        return this.path.equals(SelectorSettings.VIDEO_ITEM_PATH);
    }


    @Override
    public boolean equals(Object o) {
        try {
            ImageItem other = (ImageItem) o;
            return this.path.equalsIgnoreCase(other.path);
        }catch (ClassCastException e){
            Log.e(TAG, "equals: " + Log.getStackTraceString(e));
        }
        return super.equals(o);
    }

    @Override
    public String toString() {
        return "ImageItem{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", time=" + time +
                '}';
    }
}