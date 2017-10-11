package com.netatmo.ylu.draggablegridview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

public class DraggableGridView extends ViewGroup{

    private boolean longClickEnabled, holding;
    private int lastX = -1,  lastY = -1;
    private boolean isMoved;
    private static final int TOUCH_SLOP = 20;
    private Runnable longClickRunnable;
    private OnLongClickListener longClickListener;
    private View movableView;

    public DraggableGridView(final Context context) {
        super(context);
        init();
    }

    public DraggableGridView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        longClickRunnable = new Runnable() {
            @Override
            public void run() {
                if(!isMoved && longClickListener!= null &&  holding) {
                    Log.v("DraggableGridView","LongClick");
                    longClickEnabled = true;
                    longClickListener.onLongClick(DraggableGridView.this);
                }
            }
        };
    }

    public void setLongClickListener(OnLongClickListener listener){
        this.longClickListener = listener;
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


    public int getLastX(){
        return this.lastX;
    }

    public int getLastY(){
        return this.lastY;
    }
/*
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

        return false;
    }
}*/



    @Override
    public boolean onInterceptTouchEvent(MotionEvent event){
        switch (event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                return false;
            case MotionEvent.ACTION_MOVE:
                if(!longClickEnabled){
                    return false;
                }else {
                    return true;
                }
            default: return true;
        }
    }

    //override onTouchEvent will not work if embeded by a recycleview,
    //maybe because the event has been consumed by onTouchEvent in recycleview.
    //so I have to override onInterceptTouchEvent to cooperate with this method
    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction() & MotionEvent.ACTION_MASK){

            case MotionEvent.ACTION_UP:

                if(longClickEnabled && longClickListener != null){
                    Log.v("DraggableGridView","LongClickReleased");
                    longClickListener.onReleased(this);
                }
                holding = false;
                isMoved = false;
                longClickEnabled = false;
                break;
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                holding = true;
                postDelayed(longClickRunnable, ViewConfiguration.getLongPressTimeout());
                break;
            case MotionEvent.ACTION_MOVE:
                if(isMoved && longClickEnabled){
                    longClickListener.onMoved(this,x,y);
                    break;
                }
                if(Math.abs(lastX-x)> TOUCH_SLOP || Math.abs(lastY - y) > TOUCH_SLOP){
                    holding = false;
                    //Not a ACTION_MOVE after long click
                    if(longClickEnabled){
                        isMoved = true;
                    }
                }
                break;


        }
        return true;
    }

    public interface OnLongClickListener {
        boolean onReleased(DraggableGridView view);
        boolean onLongClick(DraggableGridView view);
        boolean onMoved(DraggableGridView view,int rawX, int rawY);
    }
}
