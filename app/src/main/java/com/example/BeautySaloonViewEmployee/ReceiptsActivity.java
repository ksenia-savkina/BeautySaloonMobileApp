package com.example.BeautySaloonViewEmployee;

import android.app.FragmentTransaction;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class ReceiptsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipts);
        Bundle args = new Bundle();
        args.putInt(AuthorizationFragment.ID, getIntent().getExtras().getInt(AuthorizationFragment.ID));
        ReceiptsFragment receiptsFragment = new ReceiptsFragment();
        receiptsFragment.setArguments(args);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.frgmContReceipt, receiptsFragment).commit();
    }
}