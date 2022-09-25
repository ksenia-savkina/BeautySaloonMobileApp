package com.example.BeautySaloonViewEmployee;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    public DBHelper dbHelper;
    public static SQLiteDatabase db;
    PostgresDB postgresDB;

    public static String storage;
    SharedPreferences sp;

    private final int REQUEST_CODE_PERMISSION_INTERNET = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = PreferenceManager.getDefaultSharedPreferences(this);

        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        int permissionStatusInternet = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        int permissionStatusAccessNetworkState = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE);

        if (permissionStatusInternet == PackageManager.PERMISSION_GRANTED && permissionStatusAccessNetworkState == PackageManager.PERMISSION_GRANTED) {
            connectPostgres();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE},
                    REQUEST_CODE_PERMISSION_INTERNET);
        }

        InitalFragment initalFragment = new InitalFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.frgmCont, initalFragment).commit();
    }

    private void connectPostgres() {
        postgresDB = new PostgresDB();
    }

    protected void onResume() {
        super.onResume();
        storage = sp.getString("list", "local");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
        postgresDB.destroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_INTERNET:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    connectPostgres();
                }
                return;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem mi = menu.add(0, 1, 0, getText(R.string.preferences));
        mi.setIntent(new Intent(this, PrefActivity.class));
        return super.onCreateOptionsMenu(menu);
    }
}