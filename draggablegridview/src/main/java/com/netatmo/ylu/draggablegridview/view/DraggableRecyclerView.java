package com.netatmo.ylu.draggablegridview.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

public class DraggableRecyclerView extends RecyclerView implements View.OnTouchListener{

    private boolean longClickEnabled, holding;
    private int lastX = -1,  lastY = -1;
    private static final int TOUCH_SLOP = 20;
    private int count ;
    private boolean isMoved;
    private Runnable longClickRunnable;
    private DraggableRecyclerView.OnLongClickListener longClickListener;


    public DraggableRecyclerView(final Context context) {
        super(context);
        init();
    }

    public DraggableRecyclerView(final Context context,
                                 @Nullable final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DraggableRecyclerView(final Context context,
                                 @Nullable final AttributeSet attrs,
                                 final int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        longClickRunnable = new Runnable() {
            @Override
            public void run() {
                //count 计数器可以避免快速频繁点击所导致的误命中
                //（例如第一次DOWN产生的runnable在判断下列条件时正好撞见了第n次的DOWN）
                count --;
                if(!isMoved && longClickListener!= null && holding && count <=0) {
                    Log.v("DraggableRecyclerView","LongClick");
                    longClickEnabled = true;
                    longClickListener.onLongClick(DraggableRecyclerView.this);
                }
            }
        };
        setOnTouchListener(this);
    }


    public void setLongClickListener(OnLongClickListener listener){
        this.longClickListener = listener;
    }

    public int getLastX(){
        return this.lastX;
    }

    public int getLastY(){
        return this.lastY;
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction() & MotionEvent.ACTION_MASK){

            case MotionEvent.ACTION_UP:

                if(longClickEnabled && longClickListener != null){
                    Log.v("DraggableRecyclerView", "LongClickReleased");
                    longClickListener.onReleased(this,x,y);
                }
                holding = false;
                isMoved = false;
                longClickEnabled = false;
                break;
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                holding = true;
                count ++;

                postDelayed(longClickRunnable, ViewConfiguration.getLongPressTimeout());
                break;
            case MotionEvent.ACTION_MOVE:

                if(isMoved && longClickEnabled){
                    longClickListener.onMoved(this,x,y);
                    return true;
                }
                if(Math.abs(lastX-x)> TOUCH_SLOP || Math.abs(lastY - y) > TOUCH_SLOP){
                    isMoved = true;
                    holding = false;
                }
                break;


        }
        return super.onTouchEvent(event);
    }

    //override onTouch, 无论如何都不拦截touch事件
    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        return false;
    }

    public interface OnLongClickListener {
        boolean onReleased(DraggableRecyclerView view,int rawX, int rawY);
        boolean onLongClick(DraggableRecyclerView view);
        boolean onMoved(DraggableRecyclerView view,int rawX, int rawY);
    }
}
