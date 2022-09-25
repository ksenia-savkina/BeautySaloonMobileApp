package com.example.BeautySaloonViewEmployee;

import android.app.FragmentTransaction;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class DistributionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distributions);
        Bundle args = new Bundle();
        args.putInt(AuthorizationFragment.ID, getIntent().getExtras().getInt(AuthorizationFragment.ID));
        DistributionsFragment distributionsFragment = new DistributionsFragment();
        distributionsFragment.setArguments(args);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.frgmContDistribution, distributionsFragment).commit();
    }
}