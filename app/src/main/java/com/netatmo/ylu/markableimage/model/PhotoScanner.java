package com.netatmo.ylu.markableimage.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.netatmo.ylu.markableimage.beans.MarkableImg;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PhotoScanner {

    private HashMap<String,List<String>> mGroupMap;

    private Context context;

    public PhotoScanner(final Context context) {
        this.context = context;
        mGroupMap = new HashMap<>();
    }

    public void scan(ScanListener listener){
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = context.getContentResolver();
        Cursor mCursor = mContentResolver.query(mImageUri,null,MediaStore.Images.Media.MIME_TYPE + "=? or "
                                                        + MediaStore.Images.Media.MIME_TYPE + "=?",
                                                new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED);
        if(mCursor == null){
            return;
        }
        int total = mCursor.getCount();
        int i= 1;
        while(mCursor.moveToNext()){
            listener.onProgress(i++,total);
            String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            String parentName = new File(path).getParentFile().getName();

            if(!mGroupMap.containsKey(parentName)){
                List<String> chileList = new ArrayList<>();
                chileList.add(path);
                mGroupMap.put(parentName,chileList);
            }else{
                mGroupMap.get(parentName).add(path);
            }
        }
        mCursor.close();
        listener.onDone();

    }

    public List<MarkableImg> getResult(){
        List<MarkableImg> list = new ArrayList<>();
        Iterator<Map.Entry<String, List<String>>> it = mGroupMap.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<String,List<String>> entry = it.next();
            List<String> tmpList = entry.getValue();
            for(int i= 0; i<tmpList.size();i++){
                list.add(new MarkableImg(tmpList.get(i)));
            }
        }
        return list;
    }


    public interface ScanListener{
        void onDone();
        void onError();
        void onProgress(int count, int total);
    }
}
