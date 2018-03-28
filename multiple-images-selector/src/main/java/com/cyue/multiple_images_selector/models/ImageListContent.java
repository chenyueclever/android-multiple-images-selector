package com.cyue.multiple_images_selector.models;

import com.cyue.multiple_images_selector.SelectorSettings;

import java.util.ArrayList;

public class ImageListContent {
    // ImageRecyclerViewAdapter.OnClick will set it to true
    // Activity.OnImageInteraction will show the alert, and set it to false
    public static boolean bReachMaxNumber = false;

    public static final ArrayList<ImageItem> IMAGES = new ArrayList<ImageItem>();

    public static void clear()
    {
        IMAGES.clear();
    }
    public static void addItem(ImageItem item) {
        IMAGES.add(item);
    }

    public static final ArrayList<String> SELECTED_IMAGES = new ArrayList<>();

    public static boolean isImageSelected(String filename) {
        return SELECTED_IMAGES.contains(filename);
    }

    public static void toggleImageSelected(String filename) {
        if(SELECTED_IMAGES.contains(filename)) {
            SELECTED_IMAGES.remove(filename);
        } else {
            SELECTED_IMAGES.add(filename);
        }
    }

    public static final ImageItem cameraItem = new ImageItem("", SelectorSettings.CAMERA_ITEM_PATH, 1999999999,SelectorSettings.CAMERA_ITEM_PATH,-1);
    public static final ImageItem videoItem = new ImageItem("", SelectorSettings.VIDEO_ITEM_PATH, 1999999998,SelectorSettings.VIDEO_ITEM_PATH,-1);
}
