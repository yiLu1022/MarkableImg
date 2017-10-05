package com.netatmo.ylu.markableimage.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.netatmo.ylu.draggablegridview.view.DraggableGridView;
import com.netatmo.ylu.markableimage.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MenuAdapter extends RecyclerView.Adapter <MenuAdapter.TagHolder>{

    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private ArrayList<String> data;
    private LongClickCallback longClickCallback;

    public MenuAdapter(final Context mContext) {
        this.mContext = mContext;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public void setData(ArrayList<String> data){
        this.data = data;
    }

    public void setLongClickListener(LongClickCallback clickListener){
        this.longClickCallback = clickListener;
    }

    @Override
    public TagHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View layout = mLayoutInflater.inflate(R.layout.item_text,parent,false);
        layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                Log.e("TAG","TAG");
                longClickCallback.onLongClick((DraggableGridView)v);
                return false;
            }
        });

        return new TagHolder(layout);
    }

    @Override
    public void onBindViewHolder(final TagHolder holder, final int position) {
        holder.textView.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    public static class TagHolder extends  RecyclerView.ViewHolder{
        @BindView(R.id.textView)
        TextView textView;

        public TagHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public interface LongClickCallback{
        void onLongClick(DraggableGridView view);
    }
}
