package com.netatmo.ylu.draggablegridview.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.netatmo.ylu.draggablegridview.R;

public class DragRelativeLayout extends RelativeLayout {

    /**
     * WindowManager is used to create a draggable tag.
     */
    private WindowManager windowManager;

    /**
     * LayoutParams is used to manage the layout of the windowManager.
     */
    private WindowManager.LayoutParams layoutParams;

    /**
     * Tag view is the customize view which will be put into the tag.
     */
    private View tagView;

    private boolean isTagFloating ;


    public DragRelativeLayout(final Context context) {
        this(context, null);
    }

    public DragRelativeLayout(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragRelativeLayout(final Context context,
                              final AttributeSet attrs,
                              final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setTagView(View view){
        this.tagView = view;
    }

    @Override
    public void onFinishInflate(){
        super.onFinishInflate();
        for(int i = 0; i<getChildCount() ; i++){
            View view = getChildAt(i);
            if(view instanceof DraggableRecyclerView){
                ((DraggableRecyclerView) view).setLongClickListener(
                        new DraggableRecyclerView.OnLongClickListener() {
                            @Override
                            public boolean onReleased(final DraggableRecyclerView view,
                                                      final int rawX,
                                                      final int rawY) {
                                removeDraggableWindow();
                                return false;
                            }

                            @Override
                            public boolean onLongClick(final DraggableRecyclerView view) {
                                createTagView(view.getLastX(),view.getLastY());
                                return false;
                            }

                            @Override
                            public boolean onMoved(final DraggableRecyclerView view,
                                                   final int rawX,
                                                   final int rawY) {
                                updateDraggableWindow(rawX,rawY);
                                return false;
                            }
                        });
            }

            if(view instanceof ImageGridView){
                ((ImageGridView) view).setListener(new ImageGridView.OnMotionListener() {
                    @Override
                    public void onClick() {

                    }

                    @Override
                    public void onMove(int x, int y) {
                        Log.v("DragRelativeLayout","Move on the image");
                    }

                    @Override
                    public void onReleased(int x, int y) {
                        removeDraggableWindow();
/*                        View v = shootView(x, y);
                        Animation loadAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.scale);
                        if(v != null) {
                            Log.v("DragRelativeLayout","Tag drag on the Image");
                            v.startAnimation(loadAnimation);
                        }
                        Log.e("---------YEAH---------","UP");*/
                    }
                });
            }
        }
    }
    private void init(){
        windowManager = (WindowManager)this.getContext().getSystemService(Context.WINDOW_SERVICE);

    }

    private void createTagView(int x, int y){
        if(tagView == null){
            return;
        }
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        layoutParams.x = x;
        layoutParams.y = y;
        layoutParams.alpha = 0.55f;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager
                .LayoutParams.FLAG_NOT_TOUCHABLE;
        this.windowManager.addView(tagView,layoutParams);
        isTagFloating = true;
    }



    private void removeDraggableWindow(){
        if(tagView != null && isTagFloating){
            windowManager.removeView(tagView);
            isTagFloating = false;
        }
    }

    private void updateDraggableWindow(int x, int y){
        if(tagView != null && isTagFloating) {
            layoutParams.x = x;
            layoutParams.y = y;
            windowManager.updateViewLayout(tagView, layoutParams);
        }
    }

    //并不用手动去shootView, 默认的dispatchTouchEvent就是干这个的
/*    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        int x = (int) event.getX();
        int y = (int) event.getY();
        View view = shootView(x,y);
        switch (event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                if(view != null) {
                    view.dispatchTouchEvent(event);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(view != null) {
                    view.dispatchTouchEvent(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                if(view != null) {
                    return view.dispatchTouchEvent(event);
                }
                break;
        }

        return super.dispatchTouchEvent(event);
    }*/

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
