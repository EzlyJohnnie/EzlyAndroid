package com.ezly.ezly_android.Utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ExifInterface;

import com.ezly.ezly_android.Data.EzlyImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Johnnie on 16/01/17.
 */

public class ImageUtils {

    private static final int MAX_WIDTH = 1280;
    private static final int MAX_HEIGHT = 920;
    private static final int MAX_IMAGE_SIZE = MAX_WIDTH * MAX_HEIGHT;

    public static void processImage(final Activity activity, final EzlyImage image, final OnImageProcessedListener listener){
        ArrayList<EzlyImage> images = new ArrayList<>();
        images.add(image);
        processImage(activity, images, listener);
    }

    public static void processImage(final Activity activity, final ArrayList<EzlyImage> images, final OnImageProcessedListener listener){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<EzlyImage> processedImages = new ArrayList<>();
                for(EzlyImage image : images){
                    if(isImageTooBig(image)){
                        File originalFile = new File(image.getPath());
                        final File scaledFile = new File(activity.getCacheDir(), image.getName());

                        Bitmap bmp = createScaledBitmap(image.getPath(), ScalingLogic.FIT);
                        if (bmp != null) {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            try {
                                bmp = rotateBitmap(bmp, getImageShouldRotationAngle(originalFile));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                            scaledFile.getParentFile().mkdirs();
                            try {
                                scaledFile.createNewFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            saveToFile(scaledFile, baos.toByteArray());
                            image.setPath(scaledFile.getAbsolutePath());
                        }

                        processedImages.add(image);
                    }
                    else{
                        processedImages.add(image);
                    }


                    if(images.size() == processedImages.size()){
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(listener != null){
                                    listener.onImageProcessed(processedImages);
                                }
                            }
                        });
                    }

                }
            }
        });
        thread.start();
    }

    private static void saveToFile(File file, byte[] data) {
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(data);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isImageTooBig(EzlyImage image){
        File file = new File(image.getPath());
        return file.length() > MAX_IMAGE_SIZE;
    }


    /**
     * Utility function for decoding an image resource. The decoded bitmap will
     * be optimized for further scaling to the requested destination dimensions
     * and scaling logic.
     *
     * Modified for PuttiForms by Jake Laurie
     * @param path The path to the image (optional)
     * @return Decoded bitmap
     */
    private static Bitmap decodeResource(String path, ScalingLogic scalingLogic) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        if(path != null) {
            BitmapFactory.decodeFile(path, options);
        }
        float destWidth = MAX_WIDTH;
        int dstHeight = (int)(!(destWidth > 0) ? (options.outHeight / options.outWidth) * (float)destWidth : MAX_HEIGHT);
        options.inJustDecodeBounds = false;
        options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight, MAX_WIDTH,
                dstHeight, scalingLogic);

        Bitmap unscaledBitmap = null;

        if(path != null) {
            unscaledBitmap = BitmapFactory.decodeFile(path, options);
        }

        return unscaledBitmap;
    }

    /**
     * Calculate optimal down-sampling factor given the dimensions of a source
     * image, the dimensions of a destination area and a scaling logic.
     *
     * @param srcWidth Width of source image
     * @param srcHeight Height of source image
     * @param dstWidth Width of destination area
     * @param dstHeight Height of destination area
     * @param scalingLogic Logic to use to avoid image stretching
     * @return Optimal down scaling sample size for decoding
     */
    private static int calculateSampleSize(int srcWidth, int srcHeight, int dstWidth, int dstHeight,
                                          ScalingLogic scalingLogic) {
        if (scalingLogic == ScalingLogic.FIT) {
            final float srcAspect = (float)srcWidth / (float)srcHeight;
            final float dstAspect = (float)dstWidth / (float)dstHeight;

            if (srcAspect > dstAspect) {
                return srcWidth / dstWidth;
            } else {
                return srcHeight / dstHeight;
            }
        } else {
            final float srcAspect = (float)srcWidth / (float)srcHeight;
            final float dstAspect = (float)dstWidth / (float)dstHeight;

            if (srcAspect > dstAspect) {
                return srcHeight / dstHeight;
            } else {
                return srcWidth / dstWidth;
            }
        }
    }

    public static enum ScalingLogic {
        CROP, FIT
    }

    /**
     * Utility function for creating a scaled version of an existing bitmap
     *
     * @param path original file path
     * @param scalingLogic Logic to use to avoid image stretching
     * @return New scaled bitmap object
     */
    private static Bitmap createScaledBitmap(String path, ScalingLogic scalingLogic) {
        Bitmap unscaledBitmap = decodeResource(path, ScalingLogic.FIT);
        int dstWidth = MAX_WIDTH;
        int dstHeight = (int)(!(dstWidth > 0) ? (unscaledBitmap.getWidth() / unscaledBitmap.getWidth()) * (float)dstWidth : MAX_HEIGHT);

        Rect srcRect = calculateSrcRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(),
                dstWidth, dstHeight, scalingLogic);
        Rect dstRect = calculateDstRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(),
                dstWidth, dstHeight, scalingLogic);
        Bitmap scaledBitmap = Bitmap.createBitmap(dstRect.width(), dstRect.height(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.drawBitmap(unscaledBitmap, srcRect, dstRect, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;
    }

    /**
     * Calculates source rectangle for scaling bitmap
     *
     * @param srcWidth Width of source image
     * @param srcHeight Height of source image
     * @param dstWidth Width of destination area
     * @param dstHeight Height of destination area
     * @param scalingLogic Logic to use to avoid image stretching
     * @return Optimal source rectangle
     */
    private static Rect calculateSrcRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight,
                                        ScalingLogic scalingLogic) {
        if (scalingLogic == ScalingLogic.CROP) {
            final float srcAspect = (float)srcWidth / (float)srcHeight;
            final float dstAspect = (float)dstWidth / (float)dstHeight;

            if (srcAspect > dstAspect) {
                final int srcRectWidth = (int)(srcHeight * dstAspect);
                final int srcRectLeft = (srcWidth - srcRectWidth) / 2;
                return new Rect(srcRectLeft, 0, srcRectLeft + srcRectWidth, srcHeight);
            } else {
                final int srcRectHeight = (int)(srcWidth / dstAspect);
                final int scrRectTop = (int)(srcHeight - srcRectHeight) / 2;
                return new Rect(0, scrRectTop, srcWidth, scrRectTop + srcRectHeight);
            }
        } else {
            return new Rect(0, 0, srcWidth, srcHeight);
        }
    }

    /**
     * Calculates destination rectangle for scaling bitmap
     *
     * @param srcWidth Width of source image
     * @param srcHeight Height of source image
     * @param dstWidth Width of destination area
     * @param dstHeight Height of destination area
     * @param scalingLogic Logic to use to avoid image stretching
     * @return Optimal destination rectangle
     */
    private static Rect calculateDstRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight,
                                        ScalingLogic scalingLogic) {
        if (scalingLogic == ScalingLogic.FIT) {
            final float srcAspect = (float)srcWidth / (float)srcHeight;
            final float dstAspect = (float)dstWidth / (float)dstHeight;

            if (srcAspect > dstAspect) {
                return new Rect(0, 0, dstWidth, (int)(dstWidth / srcAspect));
            } else {
                return new Rect(0, 0, (int)(dstHeight * srcAspect), dstHeight);
            }
        } else {
            return new Rect(0, 0, dstWidth, dstHeight);
        }
    }

    private static Bitmap rotateBitmap(Bitmap bm, int rotationAngle){
        Bitmap rotatedBitmap = null;

        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);

        return rotatedBitmap;
    }

    private static int getImageShouldRotationAngle(File file) throws IOException {
        ExifInterface exif = new ExifInterface(file.getAbsolutePath());
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) :  ExifInterface.ORIENTATION_NORMAL;

        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        return rotationAngle;
    }

    public interface OnImageProcessedListener{
        void onImageProcessed(ArrayList<EzlyImage> images);
    }
}
