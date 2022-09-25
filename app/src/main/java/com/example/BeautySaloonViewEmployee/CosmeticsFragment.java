package com.example.BeautySaloonViewEmployee;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.BeautySaloonBusinessLogic.BindingModels.CosmeticBindingModel;
import com.example.BeautySaloonBusinessLogic.BusinessLogics.CosmeticLogic;
import com.example.BeautySaloonBusinessLogic.ViewModels.CosmeticViewModel;
import com.example.BeautySaloonDatabaseImplement.Implements.CosmeticStorage;
import com.example.BeautySaloonPostgresImplements.CosmeticStorageP;

import java.util.List;

public class CosmeticsFragment extends Fragment {

    public final static String CosmeticID = "cosmeticId";

    private static final int REQUEST_DEL = 1;

    private final CosmeticLogic logic;

    private int id;

    private ArrayAdapter<CosmeticViewModel> adapter;

    private ListView listViewCosmetic;

    private List<CosmeticViewModel> list;

    public CosmeticsFragment() {
        if (MainActivity.storage.equals("client-server"))
            logic = new CosmeticLogic(new CosmeticStorageP(PostgresDB.connection));
        else
            logic = new CosmeticLogic(new CosmeticStorage());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cosmetics, container, false);
        super.onCreate(savedInstanceState);
        id = getArguments().getInt(AuthorizationFragment.ID);

        listViewCosmetic = v.findViewById(R.id.listViewCosmetic);
        listViewCosmetic.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        loadData();

        Button buttonAdd = v.findViewById(R.id.buttonAddC);
        Button buttonUpd = v.findViewById(R.id.buttonUpdC);
        Button buttonDel = v.findViewById(R.id.buttonDelC);
        Button buttonSync = v.findViewById(R.id.buttonSync);

        View.OnClickListener oclBtn = v1 -> {
            Bundle args;
            CosmeticViewModel cosmeticViewModel;
            CosmeticFragment cosmeticFragment = new CosmeticFragment();
            switch (v1.getId()) {
                case R.id.buttonAddC:
                    args = new Bundle();
                    args.putInt(AuthorizationFragment.ID, id);
                    args.putInt(CosmeticID, -1);
                    cosmeticFragment.setArguments(args);
                    getActivity().getFragmentManager().beginTransaction()
                            .replace(R.id.frgmContCosmetic, cosmeticFragment)
                            .addToBackStack(null)
                            .commit();
                    break;
                case R.id.buttonUpdC:
                    if (listViewCosmetic.getCheckedItemCount() != 0) {
                        args = new Bundle();
                        cosmeticViewModel = list.get(listViewCosmetic.getCheckedItemPosition());
                        args.putInt(AuthorizationFragment.ID, id);
                        args.putInt(CosmeticID, cosmeticViewModel.id);
                        cosmeticFragment.setArguments(args);
                        getActivity().getFragmentManager().beginTransaction()
                                .replace(R.id.frgmContCosmetic, cosmeticFragment)
                                .addToBackStack(null)
                                .commit();
                    } else {
                        Toast.makeText(getContext(), R.string.NotCheckedItems, Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.buttonDelC:
                    if (listViewCosmetic.getCheckedItemCount() != 0) {
                        DialogFragment dlgRemoval = new RemovalFragmentCosmetic();
                        args = new Bundle();
                        cosmeticViewModel = list.get(listViewCosmetic.getCheckedItemPosition());
                        args.putInt(CosmeticID, cosmeticViewModel.id);
                        dlgRemoval.setArguments(args);
                        dlgRemoval.setTargetFragment(this, REQUEST_DEL);
                        dlgRemoval.show(getFragmentManager(), "dlgRemoval");
                    } else {
                        Toast.makeText(getContext(), R.string.NotCheckedItems, Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.buttonSync:
                    try {
                        logic.sync(id);
                        Toast.makeText(getContext(), R.string.SuccessfulSyncing, Toast.LENGTH_LONG).show();
                        loadData();
                    } catch (Exception ex) {
                        Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        };

        buttonAdd.setOnClickListener(oclBtn);
        buttonUpd.setOnClickListener(oclBtn);
        buttonDel.setOnClickListener(oclBtn);
        buttonSync.setOnClickListener(oclBtn);
        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void loadData() {
        CosmeticBindingModel cosmeticBindingModel = new CosmeticBindingModel();
        cosmeticBindingModel.id = -1;
        cosmeticBindingModel.employeeId = id;
        try {
            list = logic.read(cosmeticBindingModel);
            if (list != null) {
                adapter = new ArrayAdapter<>(getContext(), R.layout.file_list, list);
                listViewCosmetic.setAdapter(adapter);
            }
        } catch (Exception ex) {
            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_DEL) {
                loadData();
            }
        }
    }
}