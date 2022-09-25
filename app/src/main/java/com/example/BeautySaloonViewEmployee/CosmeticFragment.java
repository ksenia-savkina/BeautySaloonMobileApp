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

import com.example.BeautySaloonBusinessLogic.BindingModels.CosmeticBindingModel;
import com.example.BeautySaloonBusinessLogic.BusinessLogics.CosmeticLogic;
import com.example.BeautySaloonBusinessLogic.ViewModels.CosmeticViewModel;
import com.example.BeautySaloonDatabaseImplement.Implements.CosmeticStorage;
import com.example.BeautySaloonPostgresImplements.CosmeticStorageP;

public class CosmeticFragment extends Fragment {

    private int id;

    private int employeeId;

    private final CosmeticLogic logic;

    EditText editTextName;
    EditText editTextPrice;

    public CosmeticFragment() {
        if (MainActivity.storage.equals("client-server"))
            logic = new CosmeticLogic(new CosmeticStorageP(PostgresDB.connection));
        else
            logic = new CosmeticLogic(new CosmeticStorage());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cosmetic, container, false);
        editTextName = v.findViewById(R.id.editTextCosmeticName);
        editTextPrice = v.findViewById(R.id.editTextPrice);
        Button buttonSave = v.findViewById(R.id.buttonSaveCosmetic);
        Button buttonCancel = v.findViewById(R.id.buttonCancelFC);

        employeeId = getArguments().getInt(AuthorizationFragment.ID);
        id = getArguments().getInt(CosmeticsFragment.CosmeticID);

        if (id > 0) {
            try {
                CosmeticBindingModel cosmeticBindingModel = new CosmeticBindingModel();
                cosmeticBindingModel.id = id;
                CosmeticViewModel view = logic.read(cosmeticBindingModel).get(0);
                if (view != null) {
                    editTextName.setText(view.cosmeticName);
                    editTextPrice.setText(String.valueOf(view.price));
                }
            } catch (Exception ex) {
                Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        View.OnClickListener oclBtn = v1 -> {
            CosmeticsFragment cosmeticsFragment = new CosmeticsFragment();
            Bundle args = new Bundle();
            args.putInt(AuthorizationFragment.ID, employeeId);
            cosmeticsFragment.setArguments(args);
            switch (v1.getId()) {
                case R.id.buttonSaveCosmetic:
                    if (checkFields()) {
                        try {
                            CosmeticBindingModel cosmeticBindingModel = new CosmeticBindingModel();

                            cosmeticBindingModel.id = id;
                            cosmeticBindingModel.cosmeticName = editTextName.getText().toString();
                            cosmeticBindingModel.price = Double.parseDouble(editTextPrice.getText().toString());
                            cosmeticBindingModel.employeeId = employeeId;
                            logic.createOrUpdate(cosmeticBindingModel);

                            Toast.makeText(getContext(), R.string.SuccessfulAdding, Toast.LENGTH_LONG).show();

                            getActivity().getFragmentManager().beginTransaction()
                                    .replace(R.id.frgmContCosmetic, cosmeticsFragment)
                                    .commit();
                        } catch (Exception ex) {
                            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
                case R.id.buttonCancelFC:
                    getActivity().getFragmentManager().beginTransaction()
                            .replace(R.id.frgmContCosmetic, cosmeticsFragment)
                            .commit();
                    break;
            }
        };

        buttonSave.setOnClickListener(oclBtn);
        buttonCancel.setOnClickListener(oclBtn);

        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkFields() {
        if (editTextName.getText().length() == 0) {
            Toast.makeText(getContext(), R.string.EmptyEditTextName, Toast.LENGTH_LONG).show();
            return false;
        }
        if (editTextPrice.getText().length() == 0) {
            Toast.makeText(getContext(), R.string.EmptyEditTextPrice, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}