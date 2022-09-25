package com.example.BeautySaloonViewEmployee;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.BeautySaloonBusinessLogic.BindingModels.ReceiptBindingModel;
import com.example.BeautySaloonBusinessLogic.BusinessLogics.ReceiptLogic;
import com.example.BeautySaloonBusinessLogic.ViewModels.ReceiptViewModel;
import com.example.BeautySaloonDatabaseImplement.Implements.ReceiptStorage;
import com.example.BeautySaloonPostgresImplements.ReceiptStorageP;

public class RemovalFragmentReceipt extends DialogFragment implements DialogInterface.OnClickListener {

    private final ReceiptLogic logic;

    ReceiptBindingModel receiptBindingModel;

    public RemovalFragmentReceipt() {
        if (MainActivity.storage.equals("client-server"))
            logic = new ReceiptLogic(new ReceiptStorageP(PostgresDB.connection));
        else
            logic = new ReceiptLogic(new ReceiptStorage());
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        StringBuilder message = new StringBuilder();
        int id = getArguments().getInt(ReceiptsFragment.ReceiptID);

        int item = 0;

        receiptBindingModel = new ReceiptBindingModel();
        receiptBindingModel.id = id;
        ReceiptViewModel view = logic.read(receiptBindingModel).get(0);
        if (view != null) {
            item = view.id;
        }

        message.append(getText(R.string.removalText)).append(" ").append(item).append("?");
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.removalTitle).setPositiveButton(R.string.buttonOk, this)
                .setNegativeButton(R.string.buttonCancel, this)
                .setMessage(message);
        return adb.create();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onClick(DialogInterface dialog, int which) {
        if (which == Dialog.BUTTON_POSITIVE) {
            try {
                logic.delete(receiptBindingModel);
            } catch (Exception ex) {
                Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, new Intent());
    }
}