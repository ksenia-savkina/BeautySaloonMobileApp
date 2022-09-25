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

import com.example.BeautySaloonBusinessLogic.BindingModels.ReceiptBindingModel;
import com.example.BeautySaloonBusinessLogic.BusinessLogics.ReceiptLogic;
import com.example.BeautySaloonBusinessLogic.ViewModels.ReceiptViewModel;
import com.example.BeautySaloonDatabaseImplement.Implements.ReceiptStorage;
import com.example.BeautySaloonPostgresImplements.ReceiptStorageP;

import java.util.List;

public class ReceiptsFragment extends Fragment {

    public final static String ReceiptID = "receiptId";

    private static final int REQUEST_DEL = 1;

    private final ReceiptLogic logic;

    private int id;

    private ArrayAdapter<ReceiptViewModel> adapter;

    ListView listViewReceipt;

    List<ReceiptViewModel> list;

    public ReceiptsFragment() {
        if (MainActivity.storage.equals("client-server"))
            logic = new ReceiptLogic(new ReceiptStorageP(PostgresDB.connection));
        else
            logic = new ReceiptLogic(new ReceiptStorage());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_receipts, container, false);
        super.onCreate(savedInstanceState);
        id = getArguments().getInt(AuthorizationFragment.ID);

        listViewReceipt = v.findViewById(R.id.listViewReceipts);
        listViewReceipt.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        loadData();

        Button buttonAdd = v.findViewById(R.id.buttonAddRs);
        Button buttonUpd = v.findViewById(R.id.buttonUpdRs);
        Button buttonDel = v.findViewById(R.id.buttonDelRs);

        View.OnClickListener oclBtn = v1 -> {
            Bundle args;
            ReceiptViewModel receiptViewModel;
            ReceiptFragment receiptFragment = new ReceiptFragment();
            switch (v1.getId()) {
                case R.id.buttonAddRs:
                    args = new Bundle();
                    args.putInt(AuthorizationFragment.ID, id);
                    args.putInt(ReceiptID, -1);
                    receiptFragment.setArguments(args);
                    getActivity().getFragmentManager().beginTransaction()
                            .replace(R.id.frgmContReceipt, receiptFragment)
                            .addToBackStack(null)
                            .commit();
                    break;
                case R.id.buttonUpdRs:
                    if (listViewReceipt.getCheckedItemCount() != 0) {
                        args = new Bundle();
                        receiptViewModel = list.get(listViewReceipt.getCheckedItemPosition());
                        args.putInt(AuthorizationFragment.ID, id);
                        args.putInt(ReceiptID, receiptViewModel.id);
                        receiptFragment.setArguments(args);
                        getActivity().getFragmentManager().beginTransaction()
                                .replace(R.id.frgmContReceipt, receiptFragment)
                                .addToBackStack(null)
                                .commit();
                    } else {
                        Toast.makeText(getContext(), R.string.NotCheckedItems, Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.buttonDelRs:
                    if (listViewReceipt.getCheckedItemCount() != 0) {
                        DialogFragment dlgRemoval = new RemovalFragmentReceipt();
                        args = new Bundle();
                        receiptViewModel = list.get(listViewReceipt.getCheckedItemPosition());
                        args.putInt(ReceiptID, receiptViewModel.id);
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
        ReceiptBindingModel receiptBindingModel = new ReceiptBindingModel();
        receiptBindingModel.id = -1;
        receiptBindingModel.employeeId = id;
        try {
            list = logic.read(receiptBindingModel);
            if (list != null) {
                adapter = new ArrayAdapter<>(getContext(), R.layout.file_list, list);
                listViewReceipt.setAdapter(adapter);
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