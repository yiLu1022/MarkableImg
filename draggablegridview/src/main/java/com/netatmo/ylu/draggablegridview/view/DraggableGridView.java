package com.netatmo.ylu.draggablegridview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.UNSPECIFIED;

public class DraggableGridView extends ViewGroup implements View.OnTouchListener{

    private boolean enabled, touching;
    private int x = -1,  y = -1;


    public DraggableGridView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
    }

    @Override
    protected void onLayout(final boolean changed,
                            final int l,
                            final int t,
                            final int r,
                            final int b) {
        for(int i=0;i<getChildCount();i++) {
            View childView = getChildAt(i);
            childView.layout(0, i*200, childView.getMeasuredWidth(),childView. getMeasuredHeight());
        }
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
    }

/*
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        //get the measure mode and size from the parent.
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);


        //the spec from this view to child
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        int width = 0;
        int height = 0;

*//*        if(modeHeight==AT_MOST && modeWidth == AT_MOST) {*//*

            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                measureChild(child, widthMeasureSpec, heightMeasureSpec);

                height += child.getMeasuredHeight();
                height = height == 0? child.getMeasuredHeight() :
                        Math.min(height ,child.getMeasuredHeight());

                width = width == 0? child.getMeasuredWidth() :
                        Math.min(width ,child.getMeasuredWidth());

            }
            setMeasuredDimension(width,height);
*//*        }else{
            setMeasuredDimension(200,200);
        }*//*






    }*/

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                x = (int) event.getX();
                y = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = (int) event.getX() - x;
                int deltaY = (int) event.getY() - y;

                v.layout(v.getLeft() + deltaX,
                         v.getTop() + deltaY,
                         v.getRight() + deltaX,
                         v.getBottom() + deltaY);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return false;
    }


    @Override
    public Object clone(){
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
