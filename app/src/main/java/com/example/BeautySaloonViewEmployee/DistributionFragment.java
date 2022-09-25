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
import com.example.BeautySaloonBusinessLogic.BindingModels.DistributionBindingModel;
import com.example.BeautySaloonBusinessLogic.BusinessLogics.CosmeticLogic;
import com.example.BeautySaloonBusinessLogic.BusinessLogics.DistributionLogic;
import com.example.BeautySaloonBusinessLogic.ViewModels.DistributionViewModel;
import com.example.BeautySaloonBusinessLogic.ViewModels.ListViewDistributionItemViewModel;
import com.example.BeautySaloonDatabaseImplement.Implements.CosmeticStorage;
import com.example.BeautySaloonDatabaseImplement.Implements.DistributionStorage;
import com.example.BeautySaloonPostgresImplements.CosmeticStorageP;
import com.example.BeautySaloonPostgresImplements.DistributionStorageP;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DistributionFragment extends Fragment {

    private static final int REQUEST_ADD = 1;

    private int id;

    private int employeeId;

    private final DistributionLogic logicD;

    private final CosmeticLogic logicC;

    EditText editTextIssueDate;

    private Map<Integer, Pair<String, Integer>> distributionCosmetics;

    private ArrayAdapter<ListViewDistributionItemViewModel> adapter;

    ListView listViewCosmetic;

    List<ListViewDistributionItemViewModel> list;

    SimpleDateFormat formatter;

    public DistributionFragment() {
        if (MainActivity.storage.equals("client-server")) {
            logicD = new DistributionLogic(new DistributionStorageP(PostgresDB.connection));
            logicC = new CosmeticLogic(new CosmeticStorageP(PostgresDB.connection));
        } else {
            logicD = new DistributionLogic(new DistributionStorage());
            logicC = new CosmeticLogic(new CosmeticStorage());
        }
        formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_distribution, container, false);

        editTextIssueDate = v.findViewById(R.id.editTextIssueDate);

        Button buttonAdd = v.findViewById(R.id.buttonAddD);
        Button buttonUpd = v.findViewById(R.id.buttonUpdD);
        Button buttonDel = v.findViewById(R.id.buttonDelD);
        Button buttonSave = v.findViewById(R.id.buttonSaveD);
        Button buttonCancel = v.findViewById(R.id.buttonCancelD);

        listViewCosmetic = v.findViewById(R.id.listViewCosmeticD);
        listViewCosmetic.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        employeeId = getArguments().getInt(AuthorizationFragment.ID);
        id = getArguments().getInt(DistributionsFragment.DistributionID);


        if (id > 0) {
            try {
                DistributionBindingModel distributionBindingModel = new DistributionBindingModel();
                distributionBindingModel.id = id;
                DistributionViewModel view = logicD.read(distributionBindingModel).get(0);
                if (view != null) {
                    editTextIssueDate.setText(formatter.format(view.issueDate));
                    distributionCosmetics = view.distributionCosmetics;
                    loadData();
                }
            } catch (Exception ex) {
                Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            distributionCosmetics = new HashMap<>();
        }

        View.OnClickListener oclBtn = v1 -> {
            DistributionsFragment distributionsFragment = new DistributionsFragment();
            Bundle args = new Bundle();
            args.putInt(AuthorizationFragment.ID, employeeId);
            distributionsFragment.setArguments(args);
            DialogFragment dlgSelection;
            switch (v1.getId()) {
                case R.id.buttonAddD:
                    dlgSelection = new SelectionCosmeticsFragment();
                    args = new Bundle();
                    args.putInt(AuthorizationFragment.ID, employeeId);
                    args.putInt(SelectionCosmeticsFragment.TAG_RECEIPT_COSMETICS_ID, -1);
                    args.putInt(SelectionCosmeticsFragment.TAG_COUNT, -1);
                    dlgSelection.setArguments(args);
                    dlgSelection.setTargetFragment(this, REQUEST_ADD);
                    dlgSelection.show(getFragmentManager(), "dlgSelection");
                    break;
                case R.id.buttonUpdD:
                    if (listViewCosmetic.getCheckedItemCount() != 0) {
                        dlgSelection = new SelectionCosmeticsFragment();
                        args = new Bundle();
                        ListViewDistributionItemViewModel selectedItem = list.get(listViewCosmetic.getCheckedItemPosition());
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
                case R.id.buttonDelD:
                    if (listViewCosmetic.getCheckedItemCount() != 0) {
                        try {
                            ListViewDistributionItemViewModel selectedItem = list.get(listViewCosmetic.getCheckedItemPosition());
                            distributionCosmetics.remove(selectedItem.id);
                        } catch (Exception ex) {
                            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getContext(), R.string.NotCheckedItems, Toast.LENGTH_LONG).show();
                    }
                    loadData();
                    break;
                case R.id.buttonSaveD:
                    if (!distributionCosmetics.isEmpty()) {
                        try {
                            DistributionBindingModel distributionBindingModel = new DistributionBindingModel();

                            distributionBindingModel.id = id;
                            distributionBindingModel.distributionCosmetics = distributionCosmetics;
                            distributionBindingModel.employeeId = employeeId;
                            logicD.createOrUpdate(distributionBindingModel);

                            Toast.makeText(getContext(), R.string.SuccessfulAdding, Toast.LENGTH_LONG).show();

                            getActivity().getFragmentManager().beginTransaction()
                                    .replace(R.id.frgmContDistribution, distributionsFragment)
                                    .commit();
                        } catch (Exception ex) {
                            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getContext(), R.string.FillInCosmetics, Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.buttonCancelD:
                    getActivity().getFragmentManager().beginTransaction()
                            .replace(R.id.frgmContDistribution, distributionsFragment)
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
            if (distributionCosmetics != null) {
                list = new ArrayList<>();
                for (Map.Entry<Integer, Pair<String, Integer>> dc : distributionCosmetics.entrySet()) {
                    ListViewDistributionItemViewModel itemViewModel = new ListViewDistributionItemViewModel();
                    itemViewModel.id = dc.getKey();
                    itemViewModel.cosmeticName = dc.getValue().first;

                    CosmeticBindingModel cosmeticBindingModel = new CosmeticBindingModel();
                    cosmeticBindingModel.id = dc.getKey();

                    itemViewModel.count = dc.getValue().second;

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
        editTextIssueDate.setText(formatter.format(new Date()));
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
                    distributionCosmetics.put(id, new Pair<>(cosmeticName, count));
                loadData();
            }
        }
    }
}