package com.ezly.ezly_android.Data;

import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * Created by Johnnie on 1/04/16.
 */
public class ProgressRequestBody extends RequestBody {
    private File mFile;
    private String mPath;
    private OnUploadListener mListener;
    private MediaType contentType;
    private long totalUploadedSize;

    private static final int DEFAULT_BUFFER_SIZE = 2048;


    public ProgressRequestBody(final MediaType contentType, final File file, final OnUploadListener listener) {
        mFile = file;
        mListener = listener;
        this.contentType = contentType;
    }

    @Override
    public MediaType contentType() {
        return contentType;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        long fileLength = mFile.length();
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        FileInputStream in = new FileInputStream(mFile);
        try {
            int read;
            Handler handler = new Handler(Looper.getMainLooper());
            while ((read = in.read(buffer)) != -1) {

                //update progress on UI thread
                handler.post(new ProgressUpdater());

                sink.write(buffer, 0, read);
            }
        } finally {
            in.close();
        }
    }


    @Override
    public long contentLength() {
        return mFile.length();
    }

    private class ProgressUpdater implements Runnable {
        public ProgressUpdater() {
        }

        @Override
        public void run() {
            mListener.onUploading(totalUploadedSize);
        }
    }

    public interface OnUploadListener {
        void onUploading(long totalUploadedSize);
    }
}