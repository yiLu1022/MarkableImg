package com.netatmo.ylu.markableimage.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.netatmo.ylu.markableimage.R;
import com.netatmo.ylu.markableimage.beans.MarkableImg;
import com.netatmo.ylu.markableimage.model.ImageLoader;

import java.util.List;

public class GridAdapter extends BaseAdapter implements View.OnTouchListener{

    int xDelta;
    int yDelta;
    private List<MarkableImg> data;
    private LayoutInflater inflater;
    private Context context;
    private ImageLoader loader;

    public GridAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.loader = new ImageLoader(context);
    }

    public void setData(final List<MarkableImg> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(final int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        Holder holder;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.photo_grid_item,null);
            holder = new Holder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(holder);

        }else{
            holder =(Holder) convertView.getTag();
        }
        loader.load(data.get(position).getPath(),holder.imageView);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = 50;
        layoutParams.topMargin = 50;
        convertView.setLayoutParams(layoutParams);
        convertView.setOnTouchListener(this);
        return convertView;
    }

    private class Holder {
        public ImageView imageView;
    }


    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        final int x = (int) event.getRawX();
        final int y = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v
                        .getLayoutParams();
                xDelta = x - params.leftMargin;
                yDelta = y - params.topMargin;
                break;
            case MotionEvent.ACTION_MOVE:
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) v
                        .getLayoutParams();
                int xDistance = x - xDelta;
                int yDistance = y - yDelta;

                layoutParams.leftMargin = xDistance;
                layoutParams.topMargin = yDistance;
                v.setLayoutParams(layoutParams);
                break;

        }
        //mViewGroup.invalidate();
        return true;
    }
}
