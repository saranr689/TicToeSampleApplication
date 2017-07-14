package com.saran.tictoesampleapplication.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.saran.tictoesampleapplication.R;
import com.saran.tictoesampleapplication.adapter.MyAdapter;
import com.saran.tictoesampleapplication.adapter.PopUpAdapter;
import com.saran.tictoesampleapplication.database.ResultDataBase;
import com.saran.tictoesampleapplication.location.CurrentLocationProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static pub.devrel.easypermissions.EasyPermissions.hasPermissions;

/**
 * Created by saran on 13/7/17.
 */

public class TictoeGameActivity extends Activity implements EasyPermissions.PermissionCallbacks {


    private static final String TABLE_NAME = "myresulttable";
    RecyclerView recyclerview;
    MyAdapter myAdapter;
    Button show_result_button;
    private String result;
    private PopupWindow mPopupWindow;
    private PopUpAdapter popupAdpter;
    private List<String> gameResultList = new ArrayList<>();
    TextView player_name_textview, username_txtview, location_textview;
    private String u_name;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tictoe_game_layout);

        SharedPreferences prefs = getSharedPreferences("pref_user_name", MODE_PRIVATE);
        String pref_name = prefs.getString("name", null);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && pref_name == null) {
            u_name = bundle.getString("name");
        } else {

            u_name = pref_name;
        }

        recyclerview = (RecyclerView) findViewById(R.id.recycler_view);
        show_result_button = (Button) findViewById(R.id.show_result_btn);
        username_txtview = (TextView) findViewById(R.id.name_textView);
        location_textview = (TextView) findViewById(R.id.location_textView);
        player_name_textview = (TextView) findViewById(R.id.player_name_textview);
        username_txtview.setText(u_name + " ");
        recyclerview.setLayoutManager(new GridLayoutManager(this, 3));
        myAdapter = new MyAdapter(TictoeGameActivity.this, new MyAdapter.I_Reset() {
            @Override
            public void i_resetGame(String player) {
                final com.saran.tictoesampleapplication.database.ResultDataBase resultDataBase = new ResultDataBase(TictoeGameActivity.this);
                resultDataBase.insert(player);
                showResultDialog(player);
            }

            @Override
            public void i_player_turn(String plyer_turn) {

                player_name_textview.setText(plyer_turn);
            }
        });
        recyclerview.setAdapter(myAdapter);

        show_result_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = new ResultDataBase(TictoeGameActivity.this).getReadableDatabase();
                gameResultList.clear();
                String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY id DESC LIMIT 5";
                Cursor cursor = db.rawQuery(query, null);
                if (cursor.moveToFirst()) {
                    do {
                        result = cursor.getString(cursor.getColumnIndex("result"));
                        int id = cursor.getInt(cursor.getColumnIndex("id"));
                        gameResultList.add(result);
                    } while (cursor.moveToNext());
                }
                showPopupWindow();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        new CurrentLocationProvider(TictoeGameActivity.this).checkGpsSettings(new CurrentLocationProvider.OnGPSSettingsChangeListener() {
            @Override
            public void onGPSEnabled() {

                if (hasPermissions(TictoeGameActivity.this, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    getCurrentLocation();
                } else {
                    EasyPermissions.requestPermissions(TictoeGameActivity.this, "we need location permission to get your current location", 100, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
                }

            }
        });
    }

    private void getCurrentLocation() {

        new CurrentLocationProvider(this).getCurrentLocation(new CurrentLocationProvider.OnCurrentLocationReceivedListener() {
            @Override
            public void onLocationReceived(Location location) {
                double g_latitude = location.getLatitude();
                double g_longitude = location.getLongitude();
                Log.d("location_D", "onLocationReceived: " + g_latitude + "  long: " + g_longitude);
                Geocoder geocoder = new Geocoder(TictoeGameActivity.this, Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(g_latitude, g_longitude, 1);
                    String countrycode = addresses.get(0).getCountryName();
                    String locality = addresses.get(0).getLocality();
                    Log.d("location_D", "onLocationReceived: " + countrycode + "  long: " + locality);
                    location_textview.setText(locality + " ");

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void showResultDialog(String player) {

        AlertDialog.Builder builder = new AlertDialog.Builder(TictoeGameActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.show_game_result, null);
        TextView gameresult_textView = (TextView) view.findViewById(R.id.gameresult_textView);
        TextView won_textview = (TextView) view.findViewById(R.id.won);
        gameresult_textView.setText(player);
        if (player.startsWith("Match")) {
            won_textview.setVisibility(View.GONE);
        }
        Button restart_btn = (Button) view.findViewById(R.id.restart);
        restart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRestart();
            }
        });

        Button exit_btn = (Button) view.findViewById(R.id.exit);
        exit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        builder.setCustomTitle(view);
        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
        alert.setCancelable(false);
        alert.show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent mIntent = getIntent();
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(mIntent);
    }

    private void showPopupWindow() {

        int[] location = new int[2];
        show_result_button.getLocationOnScreen(location);
        Point p = new Point();
        p.x = location[0];
        p.y = location[1];

        int OFFSET_Y = 10;

        View popupView = getLayoutInflater().inflate(R.layout.pop_up_view_layout, null);
        ImageView close_imageView = (ImageView) popupView.findViewById(R.id.close_image_view);
        RecyclerView recyclerView_popup = (RecyclerView) popupView.findViewById(R.id.pop_up_recycler_View);
        recyclerView_popup.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(TictoeGameActivity.this);
        recyclerView_popup.setLayoutManager(linearLayoutManager);
        popupAdpter = new PopUpAdapter(TictoeGameActivity.this, gameResultList);
        recyclerView_popup.setAdapter(popupAdpter);
        mPopupWindow = new PopupWindow(popupView, 500, 500, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.showAtLocation(show_result_button, Gravity.CENTER, 0, p.y + OFFSET_Y);

        close_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });

    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

        getCurrentLocation();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

        new AppSettingsDialog.Builder(this).build().show();

    }
}

