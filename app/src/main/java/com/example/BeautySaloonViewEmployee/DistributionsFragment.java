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

import com.example.BeautySaloonBusinessLogic.BindingModels.DistributionBindingModel;
import com.example.BeautySaloonBusinessLogic.BusinessLogics.DistributionLogic;
import com.example.BeautySaloonBusinessLogic.ViewModels.DistributionViewModel;
import com.example.BeautySaloonDatabaseImplement.Implements.DistributionStorage;
import com.example.BeautySaloonPostgresImplements.DistributionStorageP;

import java.util.List;

public class DistributionsFragment extends Fragment {

    public final static String DistributionID = "distributionId";

    private static final int REQUEST_DEL = 1;

    private final DistributionLogic logic;

    private int id;

    private ArrayAdapter<DistributionViewModel> adapter;

    ListView listViewDistribution;

    List<DistributionViewModel> list;

    public DistributionsFragment() {
        if (MainActivity.storage.equals("client-server"))
            logic = new DistributionLogic(new DistributionStorageP(PostgresDB.connection));
        else
            logic = new DistributionLogic(new DistributionStorage());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_distributions, container, false);
        super.onCreate(savedInstanceState);
        id = getArguments().getInt(AuthorizationFragment.ID);

        listViewDistribution = v.findViewById(R.id.listViewDistributions);
        listViewDistribution.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        loadData();

        Button buttonAdd = v.findViewById(R.id.buttonAddDs);
        Button buttonUpd = v.findViewById(R.id.buttonUpdDs);
        Button buttonDel = v.findViewById(R.id.buttonDelDs);

        View.OnClickListener oclBtn = v1 -> {
            Bundle args;
            DistributionViewModel distributionViewModel;
            DistributionFragment distributionFragment = new DistributionFragment();
            switch (v1.getId()) {
                case R.id.buttonAddDs:
                    args = new Bundle();
                    args.putInt(AuthorizationFragment.ID, id);
                    args.putInt(DistributionID, -1);
                    distributionFragment.setArguments(args);
                    getActivity().getFragmentManager().beginTransaction()
                            .replace(R.id.frgmContDistribution, distributionFragment)
                            .addToBackStack(null)
                            .commit();
                    break;
                case R.id.buttonUpdDs:
                    if (listViewDistribution.getCheckedItemCount() != 0) {
                        args = new Bundle();
                        distributionViewModel = list.get(listViewDistribution.getCheckedItemPosition());
                        args.putInt(AuthorizationFragment.ID, id);
                        args.putInt(DistributionID, distributionViewModel.id);
                        distributionFragment.setArguments(args);
                        getActivity().getFragmentManager().beginTransaction()
                                .replace(R.id.frgmContDistribution, distributionFragment)
                                .addToBackStack(null)
                                .commit();
                    } else {
                        Toast.makeText(getContext(), R.string.NotCheckedItems, Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.buttonDelDs:
                    if (listViewDistribution.getCheckedItemCount() != 0) {
                        DialogFragment dlgRemoval = new RemovalFragmentDistribution();
                        args = new Bundle();
                        distributionViewModel = list.get(listViewDistribution.getCheckedItemPosition());
                        args.putInt(DistributionID, distributionViewModel.id);
                        dlgRemoval.setArguments(args);
                        dlgRemoval.setTargetFragment(this, REQUEST_DEL);
                        dlgRemoval.show(getFragmentManager(), "dlgRemoval");
                    } else {
                        Toast.makeText(getContext(), R.string.NotCheckedItems, Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        };

        buttonAdd.setOnClickListener(oclBtn);
        buttonUpd.setOnClickListener(oclBtn);
        buttonDel.setOnClickListener(oclBtn);
        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void loadData() {
        DistributionBindingModel distributionBindingModel = new DistributionBindingModel();
        distributionBindingModel.id = -1;
        distributionBindingModel.employeeId = id;
        try {
            list = logic.read(distributionBindingModel);
            if (list != null) {
                adapter = new ArrayAdapter<>(getContext(), R.layout.file_list, list);
                listViewDistribution.setAdapter(adapter);
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