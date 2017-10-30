package com.netatmo.ylu.draggablegridview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.netatmo.ylu.draggablegridview.R;

import java.util.ArrayList;
/**
 * Created by ylu on 10/30/17.
 */

public class DividedDraggableView extends ScrollView{

    private RelativeLayout rootView;
    private AttributeSet attributeSet;
    private int bgColor, gapColor, textInGapColor, groupBgColor;
    private String textInGap;
    private int itemCount;
    private int rowPadding;//the y-axis padding of the item
    private int rowHeight, itemWidth, itemHeight, colCount;
    private boolean usingGroup = false;
    private int groupGap, groupLineCount, groupItemCount;
    private DividedDraggableViewCore dividedDraggableViewCore;

    private static final int DEFAULT_ROW_HEIGHT = 60;
    private static final int DEFAULT_ITEM_WIDTH = 50;
    private static final int DEFAULT_ITEM_HEIGHT = 55;
    private static final int DEFAULT_Y_PADDING = 20;
    private static final boolean DEFAULT_USING_GROUP = false;//using group for default/默认是否使用group
    private static final int DEFAULT_GROUP_ITEM_COUNT = 10;
    private static final int DEFAULT_GROUP_GAP = 35;//default group gap is 35dp/35dp默认group之间的高度
    private static final int DEFAULT_BG_COLOR = Color.parseColor("#00ffffff");
    private static final int DEFAULT_GAP_COLOR = Color.parseColor("#f8f8f8");
    private static final int DEFAULT_TEXT_IN_GAP_COLOR = Color.parseColor("#999999");
    private static final int DEFAULT_GROUP_BG_COLOR = Color.parseColor("#ffffff");
    private static final String DEFAULT_TEXT_IN_GAP = "page %d";
    private static final int DEFAULT_GROUP_LINE_COUNT = 2;

    public DividedDraggableView(Context context) {
        super(context);
        init();
    }

    public DividedDraggableView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.attributeSet = attributeSet;
        init();
    }

    public DividedDraggableView(Builder builder) {
        super(builder.context);
        initView();
        this.rowHeight = builder.rowHeight > 0 ? builder.rowHeight : DEFAULT_ROW_HEIGHT;
        this.itemWidth = builder.itemWidth > 0 ? builder.itemWidth : DEFAULT_ITEM_WIDTH;
        this.itemHeight = builder.itemHeight > 0 ? builder.itemHeight : DEFAULT_ITEM_HEIGHT;
        this.rowPadding = builder.yPadding > 0 ? builder.yPadding : DEFAULT_Y_PADDING;
        this.usingGroup = builder.usingGroup;
        this.groupGap = builder.groupGap > 0 ? builder.groupGap : DEFAULT_GROUP_GAP;
        this.groupLineCount = builder.groupLineCount > 0 ? builder.groupLineCount : DEFAULT_GROUP_LINE_COUNT;
        this.groupItemCount = builder.groupItemCount > 0 ? builder.groupItemCount : DEFAULT_GROUP_ITEM_COUNT;
        colCount = groupItemCount / groupLineCount;
        this.bgColor = builder.bgColor > 0 ? builder.bgColor : DEFAULT_BG_COLOR;
        this.gapColor = builder.gapColor > 0 ? builder.gapColor : DEFAULT_GAP_COLOR;
        this.textInGapColor = builder.textInGapColor > 0 ? builder.textInGapColor : DEFAULT_TEXT_IN_GAP_COLOR;
        this.groupBgColor = builder.groupBgColor > 0 ? builder.groupBgColor : DEFAULT_GROUP_BG_COLOR;
        this.textInGap = !TextUtils.isEmpty(builder.textInGap) ? builder.textInGap : DEFAULT_TEXT_IN_GAP;
        initEventListener();
    }

    private void init() {
        initView();
        initAttributes();
        initEventListener();
    }

    private void initAttributes() {
/*        TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.DividedDraggableView);
        try {
            rowHeight = (int) typedArray.getDimension(R.styleable.DividedDraggableView_rowHeight, dp2px(DEFAULT_ROW_HEIGHT));
            itemWidth = (int) typedArray.getDimension(R.styleable.DividedDraggableView_itemWidth, dp2px(DEFAULT_ITEM_WIDTH));
            itemHeight = (int) typedArray.getDimension(R.styleable.DividedDraggableView_itemHeight, dp2px(DEFAULT_ITEM_HEIGHT));
            rowPadding = (int) typedArray.getDimension(R.styleable.DividedDraggableView_yPadding, dp2px(DEFAULT_Y_PADDING));//20dp
            usingGroup = typedArray.getBoolean(R.styleable.DividedDraggableView_usingGroup, DEFAULT_USING_GROUP);
            groupGap = (int) typedArray.getDimension(R.styleable.DividedDraggableView_groupGap, dp2px(DEFAULT_GROUP_GAP));//35dp
            groupLineCount = typedArray.getInteger(R.styleable.DividedDraggableView_groupLineCount, DEFAULT_GROUP_LINE_COUNT);
            groupItemCount = typedArray.getInteger(R.styleable.DividedDraggableView_groupItemCount, DEFAULT_GROUP_ITEM_COUNT);
            colCount = groupItemCount / groupLineCount;
            bgColor = typedArray.getColor(R.styleable.DividedDraggableView_bgColor, DEFAULT_BG_COLOR);
            gapColor = typedArray.getColor(R.styleable.DividedDraggableView_gapColor, DEFAULT_GAP_COLOR);
            textInGapColor = typedArray.getColor(R.styleable.DividedDraggableView_bgColor, DEFAULT_TEXT_IN_GAP_COLOR);
            groupBgColor = typedArray.getColor(R.styleable.DividedDraggableView_groupBgColor, DEFAULT_GROUP_BG_COLOR);
            textInGap = typedArray.getString(R.styleable.DividedDraggableView_textInGap);
            if (TextUtils.isEmpty(textInGap)) {
                textInGap = DEFAULT_TEXT_IN_GAP;;
            }
        } finally {
            typedArray.recycle();
        }*/
    }

    private void initView(){
        LinearLayout.LayoutParams scrollViewParam = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(scrollViewParam);
        setFillViewport(true);

        LinearLayout linearLayout = new LinearLayout(getContext());
        ScrollView.LayoutParams params = new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(params);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        rootView = new RelativeLayout(getContext());
        linearLayout.addView(rootView);
        super.addView(linearLayout);

        dividedDraggableViewCore = new DividedDraggableViewCore(getContext());
    }


    private void initEventListener() {
        //解决和scrollview的上下滚动冲突问题
        dividedDraggableViewCore.setActionMoveListener(new DividedDraggableViewCore.ActionListener<MotionEvent>() {
            @Override
            public void onAction(MotionEvent motionEvent) {
                //一旦检测到Move动作，通知父控件不要再拦截接下来的动作
                requestDisallowInterceptTouchEvent(true);
                int scrollDistance = getScrollY();
                //motionEvent.getY 为其在scrollView中的子view的高度 而不是距离屏幕的高度 getRawY为距离屏幕左上角的高度 注意scrollView中的子view比屏幕的高度要大
                int y = Math.round(motionEvent.getY());
                int translatedY = y - scrollDistance;
                int threshold = 50;
                // scrollview 向下移动 将上面的内容显示出来
                if (translatedY < threshold) {
                    /*scrollBy(0, -30);*/
                }
                // scrollview 向上移动 将下面的内容显示出来
                if (translatedY + threshold > getHeight()) {
                    // make a scroll down by 30 px
                    scrollBy(0, 30);
                }
            }
        });

        dividedDraggableViewCore.setActionUpListener(new DividedDraggableViewCore.ActionListener<MotionEvent>() {
            @Override
            public void onAction(MotionEvent motionEvent) {
                //一旦检测到UP动作，通知父控件可以选择拦截接下来的动作。
                requestDisallowInterceptTouchEvent(false);
            }
        });
    }

    private void initDraggableView(){
        int groupCount = 0;
        int rowCount = 0;
        if (itemCount > 0) {
            groupCount = (int) Math.ceil((double) itemCount / groupItemCount);
            rowCount = (int) Math.ceil((double) itemCount / colCount);
        }
        //每个分组区域高度为 每行高度*行数 + 每行间隔高度*（行数+1) 这里 两行存在三个每行间隔高度
        int groupHeight = rowHeight * DEFAULT_GROUP_LINE_COUNT + rowPadding * (DEFAULT_GROUP_LINE_COUNT + 1);
        int pageLineHeight = groupGap;//“第一页 第二页” 所在行的line的高度
        ArrayList<Integer> topMarginArray = new ArrayList<>();//“第一页 第二页” 所在行距离顶部的margin集合
        //add 分组间隔区域
        if (groupCount > 0) {
            for (int i = 0; i < groupCount; i++) {
                LinearLayout linearLayout = getPageLineLayout();
                TextView textView = getTextViewInGapLayout();
                textView.setText(String.format(textInGap, i + 1));
                linearLayout.addView(textView);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, pageLineHeight);
                //每个间隔分组区域 距离顶部的距离 i为分组间隔区域个数
                //分组间隔区域个数*间隔区域高度 + 分组间隔区域个数*（分组中的行数*行高+（分组中的行数+1)*行间隔）两行存在三个行间隔
                int topMargin = i * groupGap + (i * (DEFAULT_GROUP_LINE_COUNT * rowHeight + (DEFAULT_GROUP_LINE_COUNT + 1) * rowPadding));
                topMarginArray.add(topMargin);
                params.setMargins(0, topMargin, 0, 0);
//				params.addRule(RelativeLayout.BELOW, R.id.categorySort_actionBar);
                linearLayout.setLayoutParams(params);
                rootView.addView(linearLayout);
            }
        }

        //由于可拖动区域是透明的 这里需要绘制其白底
        for (int topMargin : topMarginArray) {
            LinearLayout groupLayout = getGroupLayout();
            RelativeLayout.LayoutParams groupLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, groupHeight);
//			pageLineGapParams.addRule(RelativeLayout.BELOW, R.id.categorySort_actionBar);
            groupLayoutParams.setMargins(0, topMargin + groupGap, 0, 0);
            groupLayout.setLayoutParams(groupLayoutParams);
            rootView.addView(groupLayout);
        }
        //add 可拖动区域
        //可拖动区域高度为 间隔分组的高度*分组个数 +该高度下面的一个padding*分组个数 +每行高度*行数 + 每行间隔高度*行数
//		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//				rowheight * rowCount + groupGap + rowPadding * groupCount + rowCount * rowPadding + groupCount * groupGap);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                                               groupGap * groupCount + rowPadding * groupCount + rowHeight * rowCount + rowPadding * rowCount);
//		layoutParams.addRule(RelativeLayout.BELOW, R.id.categorySort_actionBar);
        dividedDraggableViewCore.setLayoutParams(layoutParams);
        dividedDraggableViewCore.setItemHeight(rowHeight);
        dividedDraggableViewCore.setItemWidth(itemWidth);
        dividedDraggableViewCore.setColCount(colCount);
        dividedDraggableViewCore.setyPadding(rowPadding);
        dividedDraggableViewCore.setGroupGap(groupGap);
        dividedDraggableViewCore.setUsingGroup(true);
        dividedDraggableViewCore.setGroupLineCount(groupLineCount);
        dividedDraggableViewCore.setBackgroundColor(bgColor);
        rootView.addView(dividedDraggableViewCore);
    }

    /**
     * should init first
     * @param itemCount
     */
    public void setItemCount(int itemCount){
        this.itemCount = itemCount;
        initDraggableView();
    }

    public void addChildView(View child) {
        if (dividedDraggableViewCore != null) {
            dividedDraggableViewCore.addView(child);
        }
    }

    public LinearLayout getPageLineLayout() {
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setBackgroundColor(gapColor);
        linearLayout.setGravity(Gravity.CENTER_VERTICAL);
        return linearLayout;
    }

    public TextView getTextViewInGapLayout() {
        TextView textView = new TextView(getContext());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, dp2px(13));
        textView.setTextColor(textInGapColor);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(dp2px(15), 0, 0, 0);
        textView.setLayoutParams(layoutParams);
        return textView;
    }

    public LinearLayout getGroupLayout() {
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setBackgroundColor(groupBgColor);
        return linearLayout;
    }

    /**
     * dp转pixel
     */
    public int dp2px(double dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5);
    }

    public static class Builder{
        private Context context;
        private int rowHeight;
        private int itemWidth;
        private int itemHeight;
        private int yPadding;
        private boolean usingGroup = DEFAULT_USING_GROUP;
        private int groupGap;
        private int groupLineCount;
        private int groupItemCount;
        private int bgColor;
        private int gapColor;
        private int textInGapColor;
        private int groupBgColor;
        private String textInGap;

        public Builder setRowHeight(Context context) {
            this.context = context;
            return this;
        }

        public Builder setRowHeight(int rowHeight) {
            this.rowHeight = rowHeight;
            return this;
        }

        public Builder setItemWidth(int itemWidth) {
            this.itemWidth = itemWidth;
            return this;
        }

        public Builder setItemHeight(int itemHeight) {
            this.itemHeight = itemHeight;
            return this;
        }

        public Builder setyPadding(int yPadding) {
            this.yPadding = yPadding;
            return this;
        }

        public Builder setUsingGroup(boolean usingGroup) {
            this.usingGroup = usingGroup;
            return this;
        }

        public Builder setGroupGap(int groupGap) {
            this.groupGap = groupGap;
            return this;
        }

        public Builder setGroupLineCount(int groupLineCount) {
            this.groupLineCount = groupLineCount;
            return this;
        }

        public Builder setGroupItemCount(int groupItemCount) {
            this.groupItemCount = groupItemCount;
            return this;
        }

        public Builder setBgColor(int bgColor) {
            this.bgColor = bgColor;
            return this;
        }

        public Builder setGapColor(int gapColor) {
            this.gapColor = gapColor;
            return this;
        }

        public Builder setTextInGapColor(int textInGapColor) {
            this.textInGapColor = textInGapColor;
            return this;
        }

        public Builder setGroupBgColor(int groupBgColor) {
            this.groupBgColor = groupBgColor;
            return this;
        }

        public Builder setTextInGap(String textInGap) {
            this.textInGap = textInGap;
            return this;
        }
    }
}