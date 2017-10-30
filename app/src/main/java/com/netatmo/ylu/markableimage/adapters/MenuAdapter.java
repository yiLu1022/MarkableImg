package com.netatmo.ylu.markableimage.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.netatmo.ylu.markableimage.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MenuAdapter extends RecyclerView.Adapter <MenuAdapter.TagHolder>{

    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private ArrayList<String> data;
    private int lastDownPosition;


    public MenuAdapter(final Context mContext) {
        this.mContext = mContext;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public int getLastDownPosition() {
        return lastDownPosition;
    }

    public void setData(ArrayList<String> data){
        this.data = data;
    }

    @Override
    public TagHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View layout = mLayoutInflater.inflate(R.layout.item_text,parent,false);

        return new TagHolder(layout);
    }

    @Override
    public void onBindViewHolder(final TagHolder holder, final int position) {
        holder.position = position;
        holder.textView.setText(data.get(position));

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    class TagHolder extends  RecyclerView.ViewHolder implements View.OnTouchListener{

        @BindView(R.id.textView)
        TextView textView;
        int position;

        TagHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(final View v, final MotionEvent event) {
            Log.e("MenuAdapter","onTouch");
            MenuAdapter.this.lastDownPosition = position;
            return false;
        }
    }
}
