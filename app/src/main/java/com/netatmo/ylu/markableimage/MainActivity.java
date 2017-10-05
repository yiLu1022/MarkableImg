package com.netatmo.ylu.markableimage;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;
import com.netatmo.ylu.draggablegridview.view.DraggableGridView;
import com.netatmo.ylu.markableimage.adapters.GridAdapter;
import com.netatmo.ylu.markableimage.adapters.MenuAdapter;
import com.netatmo.ylu.markableimage.model.PhotoScanner;

import java.util.ArrayList;
import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.drawerlayout)
    FlowingDrawer mDrawer;
    @BindView(R.id.id_recyclerview)
    RecyclerView recyclerView;
/*    @BindView(R.id.grid_view)
    GridView gridView;*/
    @BindView(R.id.draggable)
    DraggableGridView draggableGridView;
/*    @BindView(R.id.content)
    ViewGroup mViewGroup;*/
    int xDelta;
    int yDelta;


    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ArrayList<String> list = new ArrayList<>();
        for(int i=0;i<20;i++){
            list.add("111");
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MenuAdapter adapter = new MenuAdapter(getApplicationContext());
        adapter.setData(list);

        adapter.setLongClickListener(new MenuAdapter.LongClickCallback() {
            @Override
            public void onLongClick(final DraggableGridView view) {
                //TODO create a new view based on the position of the current view.
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

        if(!checkPermissionREAD_EXTERNAL_STORAGE(this)){
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
                /*gridView.setAdapter(gridAdapter);*/
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

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                                                  Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context, Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }
    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                                       new DialogInterface.OnClickListener() {
                                           public void onClick(DialogInterface dialog, int which) {
                                               ActivityCompat.requestPermissions((Activity) context,
                                                                                 new String[] { permission },
                                                                                 MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                                           }
                                       });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "ALLOWED",
                                   Toast.LENGTH_SHORT).show();
                    showPics();
                } else {
                    Toast.makeText(MainActivity.this, "GET_ACCOUNTS Denied",
                                   Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                                                 grantResults);
        }
    }


}
