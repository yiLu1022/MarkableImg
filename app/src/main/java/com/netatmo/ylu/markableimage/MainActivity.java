package com.netatmo.ylu.markableimage;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final RelativeLayout layout = (RelativeLayout)findViewById(R.id.content) ;
        ButterKnife.bind(this);

        ArrayList<String> list = new ArrayList<>();
        for(int i=0;i<60;i++){
            list.add("item " + String.valueOf(i));
        }
        final Button btn = new Button(MainActivity.this);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final MenuAdapter adapter = new MenuAdapter(getApplicationContext());
        adapter.setData(list);

        recyclerView.setLongClickListener(new DraggableRecyclerView.OnLongClickListener() {
            @Override
            public boolean onReleased(final DraggableRecyclerView view,int rawX,int rawY) {

                Log.v("MainActivity","onReleased");
                layout.removeView(btn);
                return false;
            }

            @Override
            public boolean onLongClick(final DraggableRecyclerView view) {
                Log.v("MainActivity","onLongClick");
                btn.setText(String.valueOf(adapter.getLastDownPosition()));
                int x =  view.getLastX();
                int y =  view.getLastY();
                addButton(btn,x,y,layout);
                return false;
            }

            @Override
            public boolean onMoved(final DraggableRecyclerView view, final int rawX, final int rawY) {
                Log.v("MainActivity","onMove");
                for(int i = 0;i <layout.getChildCount(); i++) {
                    View child = layout.getChildAt(i);
                    if(child instanceof Button){
                        child.layout(rawX,rawY,rawX + child.getWidth(), rawY + child.getHeight());
                    }
                }
                return false;
            }
        });


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


    public void addButton(View view,int x, int y,ViewGroup layout){

        view.setBackgroundColor(Color.GRAY) ;
        RelativeLayout.LayoutParams btn_params = new RelativeLayout.LayoutParams
                (RelativeLayout.LayoutParams.WRAP_CONTENT,
                 RelativeLayout.LayoutParams.WRAP_CONTENT);
        btn_params.setMargins(x,y,0,0);

        view.setLayoutParams(btn_params);
        layout.addView(view);
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
