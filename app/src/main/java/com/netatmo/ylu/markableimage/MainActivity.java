package com.netatmo.ylu.markableimage;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;
import com.netatmo.ylu.draggablegridview.view.DragRelativeLayout;
import com.netatmo.ylu.draggablegridview.view.DraggableRecyclerView;
import com.netatmo.ylu.markableimage.adapters.GridAdapter;
import com.netatmo.ylu.markableimage.adapters.MenuAdapter;
import com.netatmo.ylu.markableimage.model.PhotoScanner;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.drawerlayout)
    FlowingDrawer mDrawer;
    @BindView(R.id.recycle_view)
    DraggableRecyclerView recyclerView;
    @BindView(R.id.photo_grid)
    GridView gridView;
    @BindView(R.id.content)
    DragRelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ArrayList<String> list = new ArrayList<>();
        for(int i=0;i<60;i++){
            list.add("item " + String.valueOf(i));
        }
        final Button btn = new Button(MainActivity.this);
        btn.setText("GO!GO!GO");
        relativeLayout.setTagView(btn);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final MenuAdapter adapter = new MenuAdapter(getApplicationContext());
        adapter.setData(list);

        recyclerView.setAdapter(adapter);
        mDrawer.setTouchMode(ElasticDrawer.TOUCH_MODE_BEZEL);



        mDrawer.setOnDrawerStateChangeListener(new ElasticDrawer.OnDrawerStateChangeListener() {
            @Override
            public void onDrawerStateChange(final int oldState, final int newState) {
                Log.i("MainActivity", "ElasticDrawer.STATE_CLOSED");
            }

            @Override
            public void onDrawerSlide(final float openRatio, final int offsetPixels) {
                Log.i("MainActivity", "openRatio=" + openRatio + " ,offsetPixels=" + offsetPixels);
            }
        });

        if(!PermissionManager.checkPermissionREAD_EXTERNAL_STORAGE(this)){
            return;
        }


        showPics();
    }

    public void showPics(){
        final GridAdapter gridAdapter = new GridAdapter(getApplicationContext());
        final PhotoScanner scanner = new PhotoScanner(getApplicationContext());
        scanner.scan(new PhotoScanner.ScanListener() {
            @Override
            public void onDone() {
                gridAdapter.setData(scanner.getResult());
                gridView.setAdapter(gridAdapter);
            }

            @Override
            public void onError() {
                //TODO
            }

            @Override
            public void onProgress(final int count, final int total) {
                Toast.makeText(getApplicationContext(),String.valueOf(count) + " : "+ String
                        .valueOf(total),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if(!PermissionManager.resolvePermissionResult(requestCode,permissions,grantResults)){
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


}
