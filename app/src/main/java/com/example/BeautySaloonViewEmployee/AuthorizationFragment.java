package com.example.BeautySaloonViewEmployee;

import android.app.Fragment;
import android.content.Intent;
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
import com.example.BeautySaloonBusinessLogic.ViewModels.EmployeeViewModel;
import com.example.BeautySaloonDatabaseImplement.Implements.EmployeeStorage;
import com.example.BeautySaloonPostgresImplements.EmployeeStorageP;

import java.util.List;

public class AuthorizationFragment extends Fragment {

    private final EmployeeLogic logic;
    public final static String ID = "id";

    EditText editTextLogin;
    EditText editTextPassword;

    public AuthorizationFragment() {
        if (MainActivity.storage.equals("client-server"))
            logic = new EmployeeLogic(new EmployeeStorageP(PostgresDB.connection));
        else
            logic = new EmployeeLogic(new EmployeeStorage());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_authorization, container, false);

        editTextLogin = v.findViewById(R.id.username);
        editTextPassword = v.findViewById(R.id.password);
        Button buttonEnter = v.findViewById(R.id.login);
        Button buttonCancel = v.findViewById(R.id.cancel);

        View.OnClickListener oclBtn = v1 -> {
            switch (v1.getId()) {
                case R.id.login:
                    if (checkFields()) {
                        try {
                            EmployeeBindingModel employeeBindingModel = new EmployeeBindingModel();

                            employeeBindingModel.id = -1;
                            employeeBindingModel.login = editTextLogin.getText().toString();
                            employeeBindingModel.password = editTextPassword.getText().toString();

                            List<EmployeeViewModel> list = logic.read(employeeBindingModel);
                            if (list.size() > 0 && list != null) {
                                Intent intent = new Intent(getActivity(), HomeActivity.class);
                                intent.putExtra(ID, list.get(0).id);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getContext(), R.string.InvalidLogin, Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception ex) {
                            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
                case R.id.cancel:
                    InitalFragment initalFragment = new InitalFragment();
                    getActivity().getFragmentManager().beginTransaction()
                            .replace(R.id.frgmCont, initalFragment)
                            .commit();
                    break;
            }
        };

        buttonEnter.setOnClickListener(oclBtn);
        buttonCancel.setOnClickListener(oclBtn);

        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkFields() {
        if (editTextLogin.getText().length() == 0) {
            Toast.makeText(getContext(), R.string.EmptyEditTextLogin, Toast.LENGTH_LONG).show();
            return false;
        }
        if (editTextPassword.getText().length() == 0) {
            Toast.makeText(getContext(), R.string.EmptyEditTextPassword, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

}
