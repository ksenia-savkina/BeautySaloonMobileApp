package com.example.BeautySaloonViewEmployee;

import android.app.FragmentTransaction;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class CosmeticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cosmetics);

        Bundle args = new Bundle();
        args.putInt(AuthorizationFragment.ID, getIntent().getExtras().getInt(AuthorizationFragment.ID));
        CosmeticsFragment cosmeticsFragment = new CosmeticsFragment();
        cosmeticsFragment.setArguments(args);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.frgmContCosmetic, cosmeticsFragment).commit();
    }
}