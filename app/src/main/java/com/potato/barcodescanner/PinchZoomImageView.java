package com.potato.barcodescanner;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import java.io.IOException;

@SuppressLint("AppCompatCustomView")
public class PinchZoomImageView extends ImageView {

    private Bitmap mBitmap;
    private  int mImageWidth;
    private  int mImageHeight;
    private final static float mMinZoom = 1.f;
    private final static float mMaxZoom = 3.f;
    private float mScaleFactor =1.f;
    private ScaleGestureDetector scaleGestureDetector;

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public boolean onScale(ScaleGestureDetector detector){
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(mMinZoom, Math.min(mMaxZoom,mScaleFactor));
            invalidate();
            requestLayout();
            return super.onScale(detector);
        }
    }

    public PinchZoomImageView(Context context, AttributeSet attrs) {
        super(context,attrs);

        scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleListener());

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    protected  void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);

        int imageWidth = MeasureSpec.getSize(widthMeasureSpec);
        int imageHeight = MeasureSpec.getSize(heightMeasureSpec);
        int scaleWidth = Math.round(mImageWidth*mScaleFactor);
        int scaleHeight = Math.round(mImageHeight*mScaleFactor);

        setMeasuredDimension(
                Math.min(imageWidth, scaleWidth),
                Math.min(imageHeight, scaleHeight)
        );

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.scale(
                mScaleFactor,
                mScaleFactor,
                scaleGestureDetector.getFocusX(),
                scaleGestureDetector.getFocusY()
                );
        canvas.drawBitmap(mBitmap,0,0,null);
        canvas.restore();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void setImageUri(Uri uri){
        try{
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),uri);
            float aspecRatio = (float) bitmap.getHeight()/(float) bitmap.getWidth();
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            mImageWidth = displayMetrics.widthPixels;
            mImageHeight = Math.round(mImageWidth*aspecRatio);
            mBitmap = Bitmap.createScaledBitmap(bitmap,mImageWidth,mImageHeight,false);
            invalidate();
            requestLayout();
        }catch(IOException e){
            e.printStackTrace();
        }

    }

}
