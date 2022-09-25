package com.example.BeautySaloonViewEmployee;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.BeautySaloonBusinessLogic.BindingModels.CosmeticBindingModel;
import com.example.BeautySaloonBusinessLogic.BindingModels.ReceiptBindingModel;
import com.example.BeautySaloonBusinessLogic.BusinessLogics.CosmeticLogic;
import com.example.BeautySaloonBusinessLogic.BusinessLogics.ReceiptLogic;
import com.example.BeautySaloonBusinessLogic.ViewModels.ListViewReceiptItemViewModel;
import com.example.BeautySaloonBusinessLogic.ViewModels.ReceiptViewModel;
import com.example.BeautySaloonDatabaseImplement.Implements.CosmeticStorage;
import com.example.BeautySaloonDatabaseImplement.Implements.ReceiptStorage;
import com.example.BeautySaloonPostgresImplements.CosmeticStorageP;
import com.example.BeautySaloonPostgresImplements.ReceiptStorageP;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReceiptFragment extends Fragment {

    private static final int REQUEST_ADD = 1;

    private int id;

    private int employeeId;

    private final ReceiptLogic logicR;

    private final CosmeticLogic logicC;

    EditText editTextTotalCost;
    EditText editTextPurchaseDate;

    private Map<Integer, Pair<String, Integer>> receiptCosmetics;

    private ArrayAdapter<ListViewReceiptItemViewModel> adapter;

    ListView listViewCosmetic;

    List<ListViewReceiptItemViewModel> list;

    SimpleDateFormat formatter;

    public ReceiptFragment() {
        if (MainActivity.storage.equals("client-server")) {
            logicR = new ReceiptLogic(new ReceiptStorageP(PostgresDB.connection));
            logicC = new CosmeticLogic(new CosmeticStorageP(PostgresDB.connection));
        } else {
            logicR = new ReceiptLogic(new ReceiptStorage());
            logicC = new CosmeticLogic(new CosmeticStorage());
        }
        formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_receipt, container, false);
        editTextTotalCost = v.findViewById(R.id.editTextTotalCost);
        editTextPurchaseDate = v.findViewById(R.id.editTextPurchaseDate);

        Button buttonAdd = v.findViewById(R.id.buttonAddR);
        Button buttonUpd = v.findViewById(R.id.buttonUpdR);
        Button buttonDel = v.findViewById(R.id.buttonDelR);
        Button buttonSave = v.findViewById(R.id.buttonSaveR);
        Button buttonCancel = v.findViewById(R.id.buttonCancelR);

        listViewCosmetic = v.findViewById(R.id.listViewCosmeticR);
        listViewCosmetic.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        employeeId = getArguments().getInt(AuthorizationFragment.ID);
        id = getArguments().getInt(ReceiptsFragment.ReceiptID);

        if (id > 0) {
            try {
                ReceiptBindingModel receiptBindingModel = new ReceiptBindingModel();
                receiptBindingModel.id = id;
                ReceiptViewModel view = logicR.read(receiptBindingModel).get(0);
                if (view != null) {
                    editTextTotalCost.setText(String.valueOf(view.totalCost));
                    editTextPurchaseDate.setText(formatter.format(view.purchaseDate));
                    receiptCosmetics = view.receiptCosmetics;
                    loadData();
                }
            } catch (Exception ex) {
                Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            receiptCosmetics = new HashMap<>();
        }

        View.OnClickListener oclBtn = v1 -> {
            ReceiptsFragment receiptsFragment = new ReceiptsFragment();
            Bundle args = new Bundle();
            args.putInt(AuthorizationFragment.ID, employeeId);
            receiptsFragment.setArguments(args);
            DialogFragment dlgSelection;
            switch (v1.getId()) {
                case R.id.buttonAddR:
                    dlgSelection = new SelectionCosmeticsFragment();
                    args = new Bundle();
                    args.putInt(AuthorizationFragment.ID, employeeId);
                    args.putInt(SelectionCosmeticsFragment.TAG_RECEIPT_COSMETICS_ID, -1);
                    args.putInt(SelectionCosmeticsFragment.TAG_COUNT, -1);
                    dlgSelection.setArguments(args);
                    dlgSelection.setTargetFragment(this, REQUEST_ADD);
                    dlgSelection.show(getFragmentManager(), "dlgSelection");
                    break;
                case R.id.buttonUpdR:
                    if (listViewCosmetic.getCheckedItemCount() != 0) {
                        dlgSelection = new SelectionCosmeticsFragment();
                        args = new Bundle();
                        ListViewReceiptItemViewModel selectedItem = list.get(listViewCosmetic.getCheckedItemPosition());
                        args.putInt(AuthorizationFragment.ID, employeeId);
                        args.putInt(SelectionCosmeticsFragment.TAG_RECEIPT_COSMETICS_ID, selectedItem.id);
                        args.putInt(SelectionCosmeticsFragment.TAG_COUNT, selectedItem.count);
                        dlgSelection.setArguments(args);
                        dlgSelection.setTargetFragment(this, REQUEST_ADD);
                        dlgSelection.show(getFragmentManager(), "dlgSelection");
                    } else {
                        Toast.makeText(getContext(), R.string.NotCheckedItems, Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.buttonDelR:
                    if (listViewCosmetic.getCheckedItemCount() != 0) {
                        try {
                            ListViewReceiptItemViewModel selectedItem = list.get(listViewCosmetic.getCheckedItemPosition());
                            receiptCosmetics.remove(selectedItem.id);
                        } catch (Exception ex) {
                            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getContext(), R.string.NotCheckedItems, Toast.LENGTH_LONG).show();
                    }
                    loadData();
                    calcTotalCost();
                    break;
                case R.id.buttonSaveR:
                    if (!receiptCosmetics.isEmpty()) {
                        try {
                            ReceiptBindingModel receiptBindingModel = new ReceiptBindingModel();

                            receiptBindingModel.id = id;
                            receiptBindingModel.totalCost = Double.parseDouble(editTextTotalCost.getText().toString());
                            receiptBindingModel.receiptCosmetics = receiptCosmetics;
                            receiptBindingModel.employeeId = employeeId;
                            logicR.createOrUpdate(receiptBindingModel);

                            Toast.makeText(getContext(), R.string.SuccessfulAdding, Toast.LENGTH_LONG).show();

                            getActivity().getFragmentManager().beginTransaction()
                                    .replace(R.id.frgmContReceipt, receiptsFragment)
                                    .commit();
                        } catch (Exception ex) {
                            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getContext(), R.string.FillInCosmetics, Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.buttonCancelR:
                    getActivity().getFragmentManager().beginTransaction()
                            .replace(R.id.frgmContReceipt, receiptsFragment)
                            .commit();
                    break;
            }
        };

        buttonAdd.setOnClickListener(oclBtn);
        buttonUpd.setOnClickListener(oclBtn);
        buttonDel.setOnClickListener(oclBtn);
        buttonSave.setOnClickListener(oclBtn);
        buttonCancel.setOnClickListener(oclBtn);

        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void loadData() {
        try {
            if (receiptCosmetics != null) {
                list = new ArrayList<>();
                for (Map.Entry<Integer, Pair<String, Integer>> rc : receiptCosmetics.entrySet()) {
                    ListViewReceiptItemViewModel itemViewModel = new ListViewReceiptItemViewModel();
                    itemViewModel.id = rc.getKey();
                    itemViewModel.cosmeticName = rc.getValue().first;

                    CosmeticBindingModel cosmeticBindingModel = new CosmeticBindingModel();
                    cosmeticBindingModel.id = rc.getKey();

                    itemViewModel.price = logicC.read(cosmeticBindingModel).get(0).price;
                    itemViewModel.count = rc.getValue().second;

                    list.add(itemViewModel);
                }

                if (list != null) {
                    adapter = new ArrayAdapter<>(getContext(), R.layout.file_list, list);
                    listViewCosmetic.setAdapter(adapter);
                }
            }
        } catch (Exception ex) {
            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
        editTextPurchaseDate.setText(formatter.format(new Date()));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_ADD) {
                int id = data.getIntExtra(SelectionCosmeticsFragment.TAG_RECEIPT_COSMETICS_ID, -1);
                String cosmeticName = data.getStringExtra(SelectionCosmeticsFragment.TAG_COSMETIC_NAME);
                int count = data.getIntExtra(SelectionCosmeticsFragment.TAG_COUNT, -1);
                if (id != -1 && count != -1)
                    receiptCosmetics.put(id, new Pair<>(cosmeticName, count));
                loadData();
                calcTotalCost();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void calcTotalCost() {
        try {
            int totalCost = 0;
            for (Map.Entry<Integer, Pair<String, Integer>> rc : receiptCosmetics.entrySet()) {
                CosmeticBindingModel cosmeticBindingModel = new CosmeticBindingModel();
                cosmeticBindingModel.id = rc.getKey();
                totalCost += rc.getValue().second * logicC.read(cosmeticBindingModel).get(0).price;
            }
            editTextTotalCost.setText(String.valueOf(totalCost));
        } catch (Exception ex) {
            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}