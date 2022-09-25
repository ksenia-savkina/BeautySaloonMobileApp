package com.example.BeautySaloonViewEmployee;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.BeautySaloonBusinessLogic.BindingModels.CosmeticBindingModel;
import com.example.BeautySaloonBusinessLogic.BusinessLogics.CosmeticLogic;
import com.example.BeautySaloonBusinessLogic.ViewModels.CosmeticViewModel;
import com.example.BeautySaloonDatabaseImplement.Implements.CosmeticStorage;
import com.example.BeautySaloonPostgresImplements.CosmeticStorageP;

import java.util.List;

public class SelectionCosmeticsFragment extends DialogFragment {

    public static final String TAG_RECEIPT_COSMETICS_ID = "receiptCosmeticsId";

    public static final String TAG_COSMETIC_NAME = "cosmeticName";

    public static final String TAG_COUNT = "count";

    private int id;

    private int employeeId;

    private int count;

    private final CosmeticLogic logic;

    private ArrayAdapter<CosmeticViewModel> adapter;

    List<CosmeticViewModel> list;

    EditText editText;

    Spinner spinner;

    public SelectionCosmeticsFragment() {
        if (MainActivity.storage.equals("client-server"))
            logic = new CosmeticLogic(new CosmeticStorageP(PostgresDB.connection));
        else
            logic = new CosmeticLogic(new CosmeticStorage());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle(R.string.selectionTitle);
        View v = inflater.inflate(R.layout.fragment_selectioncosmetics, null);

        spinner = v.findViewById(R.id.spinnerCosmeticName);
        editText = v.findViewById(R.id.editTextCountSC);

        Button buttonSave = v.findViewById(R.id.buttonSaveSC);
        Button buttonCancel = v.findViewById(R.id.buttonCancelSC);

        employeeId = getArguments().getInt(AuthorizationFragment.ID);
        id = getArguments().getInt(TAG_RECEIPT_COSMETICS_ID);
        count = getArguments().getInt(TAG_COUNT);

        CosmeticBindingModel cosmeticBindingModel = new CosmeticBindingModel();
        cosmeticBindingModel.id = -1;
        cosmeticBindingModel.employeeId = employeeId;


        list = logic.read(cosmeticBindingModel);
        if (list != null) {
            adapter = new ArrayAdapter<>(getContext(), R.layout.file_spinner, list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        }

        if (id != -1 && count != -1) {
            cosmeticBindingModel.id = id;
            CosmeticViewModel cosmeticViewModel = logic.read(cosmeticBindingModel).get(0);
            spinner.setSelection(getIndex(spinner, cosmeticViewModel.toString()));
            editText.setText(String.valueOf(count));
        }

        View.OnClickListener oclBtn = v1 -> {
            switch (v1.getId()) {
                case R.id.buttonSaveSC:
                    if (checkFields()) {
                        try {
                            Intent intent = new Intent();
                            CosmeticViewModel cosmeticViewModel = list.get(spinner.getSelectedItemPosition());
                            intent.putExtra(TAG_RECEIPT_COSMETICS_ID, cosmeticViewModel.id);
                            intent.putExtra(TAG_COSMETIC_NAME, cosmeticViewModel.cosmeticName);
                            intent.putExtra(TAG_COUNT, Integer.valueOf(editText.getText().toString()));
                            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                            dismiss();
                        } catch (Exception ex) {
                            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
                case R.id.buttonCancelSC:
                    dismiss();
                    break;
            }
        };

        buttonSave.setOnClickListener(oclBtn);
        buttonCancel.setOnClickListener(oclBtn);

        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkFields() {
        if (editText.getText().length() == 0) {
            Toast.makeText(getContext(), R.string.EmptyEditCount, Toast.LENGTH_LONG).show();
            return false;
        }
        if (spinner.getSelectedItem() == null) {
            Toast.makeText(getContext(), R.string.EmptySpinner, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private int getIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }
        return 0;
    }
}