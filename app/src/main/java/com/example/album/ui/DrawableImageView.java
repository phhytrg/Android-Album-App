package com.example.album.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.album.data.Image;

import java.util.ArrayList;
public class DrawableImageView extends androidx.appcompat.widget.AppCompatImageView implements View.OnTouchListener
{
    float downx = 0;
    float downy = 0;
    float upx = 0;
    float upy = 0;

    Canvas canvas;
    Paint paint;
    Matrix matrix;
    float  bmWidth, bmHeight;
    private boolean erase=false;
    public DrawableImageView(Context context)
    {
        super(context);
        setOnTouchListener(this);
    }

    public DrawableImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setOnTouchListener(this);
    }

    public DrawableImageView(Context context, AttributeSet attrs,
                             int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        setOnTouchListener(this);
    }
    public void setImage(Image image){
        super.setImageURI(image.getImageUri());
        bmWidth = image.getWidth();
        bmHeight = image.getHeight();
    }
    public void setNewImage(Bitmap alteredBitmap, Bitmap bmp)
    {
        canvas = new Canvas(alteredBitmap );
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(1);
//        paint.setAntiAlias(true);
//        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        matrix = new Matrix();
        canvas.drawBitmap(bmp, matrix, paint);

        setImageBitmap(alteredBitmap);
    }
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        int action = event.getAction();
                float touchX = getPointerCoords(event)[0];//event.getX();
                float touchY = getPointerCoords(event)[1];//event.getY();
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                downx = getPointerCoords(event)[0];//event.getX();
                downy = getPointerCoords(event)[1];//event.getY();

                break;
            case MotionEvent.ACTION_MOVE:
                upx = getPointerCoords(event)[0];//event.getX();
                upy = getPointerCoords(event)[1];//event.getY();
                canvas.drawLine(downx, downy, upx, upy, paint);
                invalidate();
                downx = upx;
                downy = upy;
                break;
            case MotionEvent.ACTION_UP:
                upx = getPointerCoords(event)[0];//event.getX();
                upy = getPointerCoords(event)[1];//event.getY();
                if (!erase) {
                    canvas.drawLine(downx, downy, upx, upy, paint);

                }
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;

        }
        return true;
    }
    //set erase true or false
    public void setErase(boolean isErase){
        erase=isErase;
//        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//delete all

        if(erase) {
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//            canvas.drawCircle(50,50,5,paint);
        }
        else paint.setXfermode(null);
    }
    public void setSize(int size){
        paint.setStrokeWidth(size);
    }
    public void setColor(int color){
        paint.setColor(color);
    }

    final float[] getPointerCoords(MotionEvent e)
    {
        final int index = e.getActionIndex();
        final float[] coords = new float[] { e.getX(index), e.getY(index) };
        Matrix matrix = new Matrix();
        getImageMatrix().invert(matrix);
        matrix.postTranslate(getScrollX(), getScrollY());
        matrix.mapPoints(coords);
        return coords;
    }
}
