package com.example.BeautySaloonViewEmployee;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class InitalFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_inital, container, false);

        Button buttonRegister = v.findViewById(R.id.buttonRegister);
        Button buttonAuthorization = v.findViewById(R.id.buttonAuthorization);

        View.OnClickListener oclBtn = v1 -> {
            switch (v1.getId()) {
                case R.id.buttonRegister:
                    RegistrationFragment registrationFragment = new RegistrationFragment();
                    getActivity().getFragmentManager().beginTransaction()
                            .replace(R.id.frgmCont, registrationFragment)
                            .addToBackStack(null)
                            .commit();
                    break;
                case R.id.buttonAuthorization:
                    AuthorizationFragment authorizationFragment = new AuthorizationFragment();
                    getActivity().getFragmentManager().beginTransaction()
                            .replace(R.id.frgmCont, authorizationFragment)
                            .addToBackStack(null)
                            .commit();
                    break;
            }
        };

        buttonRegister.setOnClickListener(oclBtn);
        buttonAuthorization.setOnClickListener(oclBtn);

        return v;
    }
}