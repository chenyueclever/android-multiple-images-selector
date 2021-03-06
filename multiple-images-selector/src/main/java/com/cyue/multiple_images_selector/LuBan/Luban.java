package com.cyue.multiple_images_selector.LuBan;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Luban implements Handler.Callback {
    private static final String TAG = "Luban";
    private static final String DEFAULT_DISK_CACHE_DIR = "luban_disk_cache";

    private static final int MSG_COMPRESS_SUCCESS = 0;

    private static final int MSG_COMPRESS_START = 1;
    private static final int MSG_COMPRESS_ERROR = 2;

    private String mTargetDir;
    private List<String> mPaths;
    private int mLeastCompressSize;
    private OnCompressListener mCompressListener;

    private Handler mHandler;

    private Luban(Builder builder) {
        this.mPaths = builder.mPaths;
        this.mTargetDir = builder.mTargetDir;
        this.mCompressListener = builder.mCompressListener;
        this.mLeastCompressSize = builder.mLeastCompressSize;
        mHandler = new Handler(Looper.getMainLooper(), this);
    }

    public static Builder with(Context context) {
        return new Builder(context);
    }


    private File getImageCacheFile(Context context, String suffix) {
        if (TextUtils.isEmpty(mTargetDir)) {
            mTargetDir = getImageCacheDir(context).getAbsolutePath();
        }

        String cacheBuilder = mTargetDir + "/" +
                System.currentTimeMillis() +
                (int) (Math.random() * 1000) +
                (TextUtils.isEmpty(suffix) ? ".jpg" : suffix);

        return new File(cacheBuilder);
    }


    @Nullable
    private File getImageCacheDir(Context context) {
        return getImageCacheDir(context, DEFAULT_DISK_CACHE_DIR);
    }

    @Nullable
    private File getImageCacheDir(Context context, String cacheName) {
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir != null) {
            File result = new File(cacheDir, cacheName);
            if (!result.mkdirs() && (!result.exists() || !result.isDirectory())) {
                // File wasn't able to create a directory, or the result exists but not a directory
                return null;
            }
            return result;
        }
        if (Log.isLoggable(TAG, Log.ERROR)) {
            Log.e(TAG, "default disk cache dir is null");
        }
        return null;
    }


    @UiThread
    private void launch(final Context context) {
        if (mPaths == null || mPaths.size() == 0 && mCompressListener != null) {
            mCompressListener.onError(new NullPointerException("image file cannot be null"), "");
        }

        Iterator<String> iterator = mPaths.iterator();
        while (iterator.hasNext()) {
            final String path = iterator.next();
            if (Checker.isImage(path)) {
                AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_START));

                            File result = Checker.isNeedCompress(mLeastCompressSize, path) ?
                                    new Engine(path, getImageCacheFile(context, Checker.checkSuffix(path))).compress() :
                                    new File(path);

                            FileResult fileResult = new FileResult();
                            fileResult.setFile(result);
                            fileResult.setUrl(path);
                            mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_SUCCESS, fileResult));
                        } catch (IOException e) {
                            mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_ERROR, path));
                        }
                    }
                });
            } else {
                mCompressListener.onError(new IllegalArgumentException("can not read the path : " + path), path);
            }
            iterator.remove();
        }
    }


    @WorkerThread
    private File get(String path, Context context) throws IOException {
        return new Engine(path, getImageCacheFile(context, Checker.checkSuffix(path))).compress();
    }

    @WorkerThread
    private List<File> get(Context context) throws IOException {
        List<File> results = new ArrayList<>();
        Iterator<String> iterator = mPaths.iterator();

        while (iterator.hasNext()) {
            String path = iterator.next();
            if (Checker.isImage(path)) {
                results.add(new Engine(path, getImageCacheFile(context, Checker.checkSuffix(path))).compress());
            }
            iterator.remove();
        }

        return results;
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (mCompressListener == null) return false;

        switch (msg.what) {
            case MSG_COMPRESS_START:
                mCompressListener.onStart();
                break;
            case MSG_COMPRESS_SUCCESS:
                mCompressListener.onSuccess(((FileResult) msg.obj).getFile(), ((FileResult) msg.obj).getUrl());
                break;
            case MSG_COMPRESS_ERROR:
                mCompressListener.onError(null, (String) msg.obj);
                break;
        }
        return false;
    }

    public static class Builder {
        private Context context;
        private String mTargetDir;
        private List<String> mPaths;
        private int mLeastCompressSize = 100;
        private OnCompressListener mCompressListener;

        Builder(Context context) {
            this.context = context;
            this.mPaths = new ArrayList<>();
        }

        private Luban build() {
            return new Luban(this);
        }

        public Builder load(File file) {
            this.mPaths.add(file.getAbsolutePath());
            return this;
        }

        public Builder load(String string) {
            this.mPaths.add(string);
            return this;
        }

        public Builder load(List<String> list) {
            this.mPaths.addAll(list);
            return this;
        }

        public Builder putGear(int gear) {
            return this;
        }

        public Builder setCompressListener(OnCompressListener listener) {
            this.mCompressListener = listener;
            return this;
        }

        public Builder setTargetDir(String targetDir) {
            this.mTargetDir = targetDir;
            return this;
        }


        public Builder ignoreBy(int size) {
            this.mLeastCompressSize = size;
            return this;
        }

        public void launch() {
            build().launch(context);
        }

        public File get(String path) throws IOException {
            return build().get(path, context);
        }


        public List<File> get() throws IOException {
            return build().get(context);
        }
    }

    public class FileResult {
        public String url;
        public File file;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }
    }
}