package com.ezly.ezly_android.UI.Widget;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Johnnie on 27/08/15.
 */
public class CircleImageView extends ImageView {

    public enum ImageMode{
        STRETCH_TO_FILL,
        DISPLAY_CENTER
    }

    private ImageMode mode = ImageMode.DISPLAY_CENTER;

    private Paint paint ;

    public CircleImageView(Context context) {
        this(context, null);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        paint = new Paint();

    }

    public ImageMode getMode() {
        return mode;
    }

    public void setMode(ImageMode mode) {
        this.mode = mode;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Drawable drawable = getDrawable();
        if (null != drawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Bitmap b = getCircleBitmap(bitmap);
            final Rect rectSrc = new Rect(0, 0, b.getWidth(), b.getHeight());
            final Rect rectDest = new Rect(0,0,getWidth(),getHeight());
            paint.reset();
            canvas.drawBitmap(b, rectSrc, rectDest, paint);

        } else {
            super.onDraw(canvas);
        }
    }


    private Bitmap getCircleBitmap(Bitmap bitmap) {

        int imageWidth = bitmap.getWidth();
        int imageHeight = bitmap.getHeight();
        int size = imageWidth;
        if (imageHeight < imageWidth) {
            size = imageHeight;
        }

        Bitmap scaledBitmap = null;
        switch (mode) {
            case STRETCH_TO_FILL:
                scaledBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);
                break;
            case DISPLAY_CENTER:
                scaledBitmap = Bitmap.createBitmap(bitmap, (imageWidth - size) / 2, (imageHeight - size) / 2, size, size);
                break;
        }


        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;

        Rect rect = new Rect(0, 0, size, size);
        final Rect disRect = new Rect(0, 0, size, size);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        canvas.drawCircle(size / 2, size / 2, size / 2, paint);
//        canvas.drawRect(rect, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(scaledBitmap, rect, disRect, paint);
        return output;
    }
}
