package com.netatmo.ylu.draggablegridview.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.GridView;


public class ImageGridView extends GridView {

    private OnMotionListener listener;
    private boolean animating;

    public ImageGridView(final Context context) {
        this(context, null);
    }

    public ImageGridView(final Context context, final AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ImageGridView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setListener(OnMotionListener listener){
        this.listener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                View view = shootView(x,y);
                startAnimation(view);
                break;
            case MotionEvent.ACTION_UP:
                if(listener != null){
                    animating = false;
                    listener.onReleased(x,y);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(listener != null){
                    listener.onMove(x,y);

                }
                break;
        }
        return super.onTouchEvent(event);
    }

    public interface OnMotionListener{
        void onClick();
        void onMove(int x, int y);
        void onReleased(int x, int y);
    }

    public void startAnimation(View view){
        if(!animating){
            animating = true;
            ObjectAnimator animator = ObjectAnimator.ofFloat(view,"alpha",1f, 0f, 1f);
            animator.setDuration(100);
            animator.start();
        }

    }

    @Nullable
    private View shootView( int x, int y){

        for(int i = 0;i<getChildCount();i++){
            View view = getChildAt(i);
            int top = view.getTop();
            int bottom = view.getBottom();
            int left = view.getLeft();
            int right = view.getRight();

            if(x>left && x< right && y > top && y<bottom){
                return view;
            }
        }
        return null;
    }

}
