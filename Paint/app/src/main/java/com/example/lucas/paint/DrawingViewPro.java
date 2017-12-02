package com.example.lucas.paint;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.TypedValue;
import android.widget.ImageButton;

import java.util.ArrayList;

/**
 * Created by Lucas on 15/11/2017.
 */

public class DrawingViewPro extends View {

    //drawing path
    public Path drawPath;
    //drawing and canvas paint
    public Paint drawPaint, canvasPaint;
    //initial color
    public int paintColor = Color.BLUE;
    //canvas
    public Canvas drawCanvas;
    //canvas bitmap
    public Bitmap canvasBitmap;
    private float brushSize, lastBrushSize;
    private boolean erase=false;

    private ArrayList<Path> paths = new ArrayList<Path>();
    private ArrayList<Path> undonePaths = new ArrayList<Path>();

    private ArrayList<Paint> paints = new ArrayList<Paint>();
    private ArrayList<Paint> undonePaints = new ArrayList<Paint>();

    public DrawingViewPro(Context context, AttributeSet attrs){
        super(context, attrs);
        setupDrawing();
    }

    public void setupDrawing() {
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
        brushSize = getResources().getInteger(R.integer.medium_size);
        lastBrushSize = brushSize;
        //paints.add(drawPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (h==0) {
            super.onSizeChanged(w, 1, oldw, oldh);
            canvasBitmap = Bitmap.createBitmap(w, 1, Bitmap.Config.ARGB_8888);
            drawCanvas = new Canvas(canvasBitmap);
        } else {
            super.onSizeChanged(w, h, oldw, oldh);
            canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            drawCanvas = new Canvas(canvasBitmap);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int i = 0;
        for (Path p : paths){
            //canvas.drawPath(p, drawPaint);
            canvas.drawPath(p, paints.get(i));
            i++;
        }

        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                paths.add(drawPath);
                paints.add(drawPaint);
                drawPaint = new Paint();
                drawPaint.setColor(paintColor);
                drawPaint.setAntiAlias(true);
                drawPaint.setStrokeWidth(brushSize);
                drawPaint.setStyle(Paint.Style.STROKE);
                drawPaint.setStrokeJoin(Paint.Join.ROUND);
                drawPaint.setStrokeCap(Paint.Cap.ROUND);
                drawPath = new Path();
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    public void setColor(String newColor){
        invalidate();
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
    }

    public void setColor(int newColor){
        invalidate();
        paintColor = newColor;
        drawPaint.setColor(paintColor);
    }

    public void setBrushSize(float newSize){
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        brushSize=pixelAmount;
        drawPaint.setStrokeWidth(brushSize);
    }

    public void setLastBrushSize(float lastSize){
        lastBrushSize=lastSize;
    }
    public float getLastBrushSize(){
        return lastBrushSize;
    }

    public void setErase(boolean isErase){
        erase=isErase;
        if(erase) {
            this.setColor("#FFFFFFFF");
        }
    }

    public void startNew() {
        this.setBackgroundColor(Color.WHITE);
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        paths.clear();
        paints.clear();
        undonePaths.clear();
        invalidate();
    }

    public void onClickUndo () {
        if (paths.size()>0)
        {
            undonePaths.add(paths.remove(paths.size()-1));
            undonePaints.add(paints.remove(paints.size()-1));
            invalidate();
        }
        else
        {

        }
        //toast the user
    }

    public void onClickRedo (){
        if (undonePaths.size()>0)
        {
            paths.add(undonePaths.remove(undonePaths.size()-1));
            paints.add(undonePaints.remove(undonePaints.size()-1));
            invalidate();
        }
        else
        {

        }
        //toast the user
    }
}
