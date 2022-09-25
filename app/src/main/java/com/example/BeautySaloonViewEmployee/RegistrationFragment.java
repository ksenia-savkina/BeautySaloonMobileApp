package com.example.BeautySaloonViewEmployee;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.BeautySaloonBusinessLogic.BindingModels.EmployeeBindingModel;
import com.example.BeautySaloonBusinessLogic.BusinessLogics.EmployeeLogic;
import com.example.BeautySaloonDatabaseImplement.Implements.EmployeeStorage;
import com.example.BeautySaloonPostgresImplements.EmployeeStorageP;

public class RegistrationFragment extends Fragment {

    private final EmployeeLogic logic;
    EditText editTextF_Name;
    EditText editTextL_Name;
    EditText editTextLogin;
    EditText editTextPassword;
    EditText editTextEmail;
    EditText editTextPhone;

    public RegistrationFragment() {
        if (MainActivity.storage.equals("client-server"))
            logic = new EmployeeLogic(new EmployeeStorageP(PostgresDB.connection));
        else
            logic = new EmployeeLogic(new EmployeeStorage());
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_registration, container, false);

        editTextF_Name = v.findViewById(R.id.editTextF_Name);
        editTextL_Name = v.findViewById(R.id.editTextL_Name);
        editTextLogin = v.findViewById(R.id.editTextLogin);
        editTextPassword = v.findViewById(R.id.editTextPassword);
        editTextEmail = v.findViewById(R.id.editTextEmail);
        editTextPhone = v.findViewById(R.id.editTextPhone);

        Button buttonRegisterFR = v.findViewById(R.id.buttonRegisterFR);
        Button buttonCancelFR = v.findViewById(R.id.buttonCancelFR);

        View.OnClickListener oclBtn = v1 -> {
            InitalFragment initalFragment = new InitalFragment();
            switch (v1.getId()) {
                case R.id.buttonRegisterFR:
                    if (checkFields()) {
                        try {
                            EmployeeBindingModel employeeBindingModel = new EmployeeBindingModel();
                            employeeBindingModel.id = -1;
                            employeeBindingModel.f_Name = editTextF_Name.getText().toString();
                            employeeBindingModel.l_Name = editTextL_Name.getText().toString();
                            employeeBindingModel.login = editTextLogin.getText().toString();
                            employeeBindingModel.password = editTextPassword.getText().toString();
                            employeeBindingModel.eMail = editTextEmail.getText().toString();
                            employeeBindingModel.phoneNumber = editTextPhone.getText().toString();
                            logic.createOrUpdate(employeeBindingModel);
                            Toast.makeText(getContext(), R.string.SuccessfulRegistration, Toast.LENGTH_LONG).show();
                            getActivity().getFragmentManager().beginTransaction()
                                    .replace(R.id.frgmCont, initalFragment)
                                    .commit();
                        } catch (Exception ex) {
                            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
                case R.id.buttonCancelFR:
                    getActivity().getFragmentManager().beginTransaction()
                            .replace(R.id.frgmCont, initalFragment)
                            .commit();
                    break;
            }
        };

        buttonRegisterFR.setOnClickListener(oclBtn);
        buttonCancelFR.setOnClickListener(oclBtn);

        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkFields() {
        if (editTextF_Name.getText().length() == 0) {
            Toast.makeText(getContext(), R.string.EmptyEditTextF_Name, Toast.LENGTH_LONG).show();
            return false;
        }
        if (editTextL_Name.getText().length() == 0) {
            Toast.makeText(getContext(), R.string.EmptyEditTextL_Name, Toast.LENGTH_LONG).show();
            return false;
        }
        if (editTextLogin.getText().length() == 0) {
            Toast.makeText(getContext(), R.string.EmptyEditTextLogin, Toast.LENGTH_LONG).show();
            return false;
        }
        if (editTextPassword.getText().length() == 0) {
            Toast.makeText(getContext(), R.string.EmptyEditTextPassword, Toast.LENGTH_LONG).show();
            return false;
        }
        if (editTextEmail.getText().length() == 0) {
            Toast.makeText(getContext(), R.string.EmptyEditTextEmail, Toast.LENGTH_LONG).show();
            return false;
        }
        if (editTextPhone.getText().length() == 0) {
            Toast.makeText(getContext(), R.string.EmptyEditTextPhone, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}