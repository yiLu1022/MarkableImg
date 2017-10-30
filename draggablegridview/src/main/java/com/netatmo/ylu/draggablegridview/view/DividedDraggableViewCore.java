package com.netatmo.ylu.draggablegridview.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * Created by andyken on 17/2/9.
 */
public class DividedDraggableViewCore extends LinearLayout implements View.OnTouchListener, View.OnClickListener, View.OnLongClickListener {

    private int draggedIndex = -1, lastX = -1, lastY = -1, lastTargetIndex = -1;
    private int xPadding, yPadding;//the x-axis and y-axis padding of the item
    private int rowHeight, itemWidth, itemHeight, colCount;
    private ArrayList<Integer> newPositions = new ArrayList<Integer>();
    private static int ANIM_DURATION = 150;
    private AdapterView.OnItemClickListener onItemClickListener;
    private OnRearrangeListener onRearrangeListener;
    private List<View> childList = new ArrayList<>();
    private ActionListener<MotionEvent> actionUpListener;
    private ActionListener<MotionEvent> actionMoveListener;
    private boolean usingGroup = false;
    private int groupGap, groupLineCount, groupItemCount;


    public DividedDraggableViewCore(Context context) {
        super(context);
        init();
    }

    private void init() {
        initData();
        initEventListener();
    }

    private void initData() {
        setChildrenDrawingOrderEnabled(true);
    }

    private void initEventListener() {
        super.setOnClickListener(this);
        setOnTouchListener(this);
        setOnLongClickListener(this);
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        childList.add(child);
        newPositions.add(-1);
    }

    /**
     * 增加不能进行拖动排序的类 目前的实现该不能拖动的item
     * 只能处于最后一个item
     * add undraggable view to the last child
     * @param child
     */
    public void addUnDraggableView(View child) {
        child.setTag("undraggable");
        addView(child);
    }

    @Override
    public void removeViewAt(int index) {
        super.removeViewAt(index);
        newPositions.remove(index);
    }

    @Override
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        xPadding = ((r - l) - (itemWidth * colCount)) / (colCount + 1);
        for (int i = 0; i < getChildCount(); i++) {
            if (i != draggedIndex) {
                Point xy = getCoorFromIndex(i);
                getChildAt(i).layout(xy.x, xy.y, xy.x + itemWidth, xy.y + itemHeight);
            }

        }
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        //将正在移动的item放在最后一个绘制 防止出现正在移动的item被遮住的问题
        //draw the moving item at last for purpose of resolving the problem of cover the moving item.
        if (draggedIndex == -1) {
            return i;
        } else if (i == childCount - 1) {
            return draggedIndex;
        } else if (i >= draggedIndex) {
            return i + 1;
        }
        return i;
    }

    /**
     * get index from coordinate
     *
     * @param x
     * @param y
     * @return
     */
    private int getIndexFromCoor(int x, int y) {
        int col = getColFromCoor(x);
        int row = getRowFromCoor(y);
        if (col == -1 || row == -1) {
            return -1;
        }
        int index = row * colCount + col;
        if (index >= getDraggedChildCount()) {
            return -1;
        }
        return index;
    }

    private int getColFromCoor(int coor) {
        coor -= xPadding;
        for (int i = 0; coor > 0; i++) {
            if (coor < itemWidth) { return i; }
            coor -= (itemWidth + xPadding);
        }
        return -1;
    }

    private int getRowFromCoor(int coor) {
        if (usingGroup) {
            //如果使用group 那么从顶端到低端进行计算
            int row = -1;//第一个while都没有进入 也就是当前高度比分组间隔高度和padding高度要小
            //从顶部的分组间隔高度加上第一行上面的padding作为开始的对比高度
            int tempHeight = groupGap + yPadding;
            while (tempHeight < coor) {
                tempHeight += itemHeight;
                //如果点击的区域在行之间的padding中 则返回-1
                if (coor > tempHeight && coor < tempHeight + yPadding) {
                    return -1;
                } else {
                    //如果不在其中 则将tempHeight加上行间隔高度
                    tempHeight += yPadding;
                }
                row++;
                //当row为分组的第一行时 需要先加上间隔分组的高度和下面的padding
                if ((row + 1) % groupLineCount == 0) {
                    tempHeight += groupGap + yPadding;
                }

            }
            return row;
        } else {
            coor -= yPadding;
            //不使用group 从低端到顶端进行计算
            for (int i = 0; coor > 0; i++) {
                if (coor < itemHeight) { return i; }
                coor -= (itemHeight + yPadding);
            }
        }
        return -1;
    }

    /**
     * 判断当前移动到的位置 当当前位置在另一个item区域时交换
     *
     * @param x
     * @param y
     * @return
     */
    private int getTargetFromCoor(int x, int y) {
        if (getRowFromCoor(y) == -1) {
            //touch is between rows
            return -1;
        }
        int target = getIndexFromCoor(x, y);
        //将item移动到最后的item之后
        if (target == getDraggedChildCount()) {
            target = getDraggedChildCount() - 1;
        }
        return target;
    }

    private Point getCoorFromIndex(int index) {
        int col = index % colCount;
        int row = index / colCount;
        if (usingGroup) {
            //如果为两行一个group 那么第五行之前有三个group  其高度为3*groupGap 注意 第一行上方还有一个group
            int prevGroupCount = row / groupLineCount + 1;//该行之前的group个数
            //行数所在的高度为之前的间隔分组区域的高度*间隔的分组个数+间隔分区下面的padding*间隔的分组个数+行数*每行高度和每行之间的区域之和
            return new Point(xPadding + (itemWidth + xPadding) * col,
                             prevGroupCount * groupGap + prevGroupCount * yPadding + row * (yPadding + itemHeight));
        } else {
            return new Point(xPadding + (itemWidth + xPadding) * col,
                             yPadding + (itemHeight + yPadding) * row);
        }

    }

    @Override
    public void onClick(View view) {
        if (onItemClickListener != null && getIndex() != -1) {
            onItemClickListener.onItemClick(null, getChildAt(getIndex()), getIndex(), getIndex() / colCount);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        int index = getIndex();
        if (index != -1) {
            //如果长按的位置在
            draggedIndex = index;
            animateActionDown();
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                lastX = (int) event.getX();
                lastY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = (int) event.getX() - lastX;
                int deltaY = (int) event.getY() - lastY;
                if (draggedIndex != -1) {
                    if (actionMoveListener != null) {
                        actionMoveListener.onAction(event);
                    }
                    int x = (int) event.getX(), y = (int) event.getY();
                    View draggedView = getChildAt(draggedIndex);
                    if (draggedView.getTag() != null && draggedView.getTag().equals("undraggable")) {
                        return false;
                    }
                    int itemLeft = draggedView.getLeft(), itemTop = draggedView.getTop();
                    draggedView.layout(itemLeft + deltaX, itemTop + deltaY, itemLeft + deltaX + itemWidth, itemTop + deltaY + itemHeight);
                    //得到当前点击位置所在的item的index
                    int targetIndex = getTargetFromCoor(x, y);
                    if (lastTargetIndex != targetIndex && targetIndex != -1) {
                        animateGap(targetIndex);
                        lastTargetIndex = targetIndex;
                    }
                }
                lastX = (int) event.getX();
                lastY = (int) event.getY();
                break;
            case MotionEvent.ACTION_UP:
                if (draggedIndex != -1) {
                    if (actionUpListener != null) {
                        actionUpListener.onAction(event);
                    }
                    //如果存在item交换 则重新排列子view
                    if (lastTargetIndex != -1) {
                        reorderChildren();
                    }
                    animateActionUp();
                    lastTargetIndex = -1;
                    draggedIndex = -1;
                }
                break;
        }
        //如果存在拖动item 并且需要消费掉该事件 则返回true
        if (draggedIndex != -1) {
            return true;
        }
        return false;
    }

    /**
     * actionDown动画
     */
    private void animateActionDown() {
        View v = getChildAt(draggedIndex);
        AnimationSet animSet = new AnimationSet(true);
        AlphaAnimation alpha = new AlphaAnimation(1, .5f);
        alpha.setDuration(ANIM_DURATION);
        animSet.addAnimation(alpha);
        animSet.setFillEnabled(true);
        animSet.setFillAfter(true);
        v.clearAnimation();
        v.startAnimation(animSet);
    }

    /**
     * actionUp动画
     */
    private void animateActionUp() {
        View v = getChildAt(draggedIndex);
        AlphaAnimation alpha = new AlphaAnimation(.5f, 1);
        alpha.setDuration(ANIM_DURATION);
        AnimationSet animSet = new AnimationSet(true);
        animSet.addAnimation(alpha);
        animSet.setFillEnabled(true);
        animSet.setFillAfter(true);
        v.clearAnimation();
        v.startAnimation(animSet);
    }

    /**
     * 拖动某个item时其他item的移动动画 不移动最后不能被拖动的item
     * animate the other item when the dragged item moving
     *
     * @param targetIndex
     */
    private void animateGap(int targetIndex) {
        for (int i = 0; i < getDraggedChildCount(); i++) {
            View v = getChildAt(i);
            if (i == draggedIndex) { continue; }
            int newPos = i;
            if (draggedIndex < targetIndex && i >= draggedIndex + 1 && i <= targetIndex) {
                newPos--;
            } else if (targetIndex < draggedIndex && i >= targetIndex && i < draggedIndex) { newPos++; }

            //animate
            int oldPos = i;
            if (newPositions.get(i) != -1) { oldPos = newPositions.get(i); }
            if (oldPos == newPos) { continue; }

            Point oldXY = getCoorFromIndex(oldPos);
            Point newXY = getCoorFromIndex(newPos);
            Point oldOffset = new Point(oldXY.x - v.getLeft(), oldXY.y - v.getTop());
            Point newOffset = new Point(newXY.x - v.getLeft(), newXY.y - v.getTop());

            TranslateAnimation translate = new TranslateAnimation(Animation.ABSOLUTE, oldOffset.x,
                                                                  Animation.ABSOLUTE, newOffset.x,
                                                                  Animation.ABSOLUTE, oldOffset.y,
                                                                  Animation.ABSOLUTE, newOffset.y);
            translate.setDuration(ANIM_DURATION);
            translate.setFillEnabled(true);
            translate.setFillAfter(true);
            v.clearAnimation();
            v.startAnimation(translate);

            newPositions.set(i, newPos);
        }
    }

    @SuppressLint("WrongCall")
    private void reorderChildren() {
        //FIGURE OUT HOW TO REORDER CHILDREN WITHOUT REMOVING THEM ALL AND RECONSTRUCTING THE LIST!!!
        ArrayList<View> children = new ArrayList<View>();
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).clearAnimation();
            children.add(getChildAt(i));
        }
        removeAllViews();
        childList.clear();
        int oldIndex = draggedIndex;
        int newIndex = lastTargetIndex;
        while (draggedIndex != lastTargetIndex) {
            if (lastTargetIndex == children.size()) {
                // dragged and dropped to the right of the last element
                children.add(children.remove(draggedIndex));
                draggedIndex = lastTargetIndex;
            } else if (draggedIndex < lastTargetIndex) {
                // shift to the right
                Collections.swap(children, draggedIndex, draggedIndex + 1);
                draggedIndex++;
            } else if (draggedIndex > lastTargetIndex) {
                // shift to the left
                Collections.swap(children, draggedIndex, draggedIndex - 1);
                draggedIndex--;
            }
        }
        for (int i = 0; i < children.size(); i++) {
            newPositions.set(i, -1);
            addView(children.get(i));
        }
        onLayout(true, getLeft(), getTop(), getRight(), getBottom());
        if (onRearrangeListener != null) {
            onRearrangeListener.onRearrange(oldIndex, newIndex);
        }
    }

    /**
     * get the index of dragging item
     *
     * @return
     */
    private int getIndex() {
        return getIndexFromCoor(lastX, lastY);
    }

    public void setOnRearrangeListener(OnRearrangeListener onRearrangeListener) {
        this.onRearrangeListener = onRearrangeListener;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setActionUpListener(ActionListener<MotionEvent> actionUpListener) {
        this.actionUpListener = actionUpListener;
    }

    public void setActionMoveListener(ActionListener<MotionEvent> actionMoveListener) {
        this.actionMoveListener = actionMoveListener;
    }

    public int getDraggedChildCount() {
        int childCount = 0;
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) != null
                    && getChildAt(i).getTag() != null
                    && getChildAt(i).getTag().equals("undraggable")) {

                continue;
            }
            childCount++;

        }
        return childCount;
    }

    public int getDraggedIndex() {
        return draggedIndex;
    }

    public List<View> getChildList() {
        return childList;
    }

    public void setColCount(int colCount) {
        this.colCount = colCount;
    }

    public void setItemHeight(int itemHeight) {
        this.itemHeight = itemHeight;
    }

    public void setyPadding(int yPadding) {
        this.yPadding = yPadding;
    }

    public void setItemWidth(int itemWidth) {
        this.itemWidth = itemWidth;
    }

    public void setGroupGap(int groupGap) {
        this.groupGap = groupGap;
    }

    public void setUsingGroup(boolean usingGroup) {
        this.usingGroup = usingGroup;
    }

    public void setGroupLineCount(int groupLineCount) {
        this.groupLineCount = groupLineCount;
    }

    public interface OnRearrangeListener {

        public abstract void onRearrange(int oldIndex, int newIndex);
    }

    public interface ActionListener<T> {

        public void onAction(T t);
    }
}