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

import com.example.BeautySaloonBusinessLogic.BindingModels.CosmeticBindingModel;
import com.example.BeautySaloonBusinessLogic.BusinessLogics.CosmeticLogic;
import com.example.BeautySaloonBusinessLogic.ViewModels.CosmeticViewModel;
import com.example.BeautySaloonDatabaseImplement.Implements.CosmeticStorage;
import com.example.BeautySaloonPostgresImplements.CosmeticStorageP;

public class RemovalFragmentCosmetic extends DialogFragment implements DialogInterface.OnClickListener {

    private final CosmeticLogic logic;

    CosmeticBindingModel cosmeticBindingModel;

    public RemovalFragmentCosmetic() {
        if (MainActivity.storage.equals("client-server"))
            logic = new CosmeticLogic(new CosmeticStorageP(PostgresDB.connection));
        else
            logic = new CosmeticLogic(new CosmeticStorage());
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        StringBuilder message = new StringBuilder();
        int id = getArguments().getInt(CosmeticsFragment.CosmeticID);

        String item = "";

        cosmeticBindingModel = new CosmeticBindingModel();
        cosmeticBindingModel.id = id;
        CosmeticViewModel view = logic.read(cosmeticBindingModel).get(0);
        if (view != null) {
            item = view.cosmeticName;
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
                logic.delete(cosmeticBindingModel);
            } catch (Exception ex) {
                Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, new Intent());
    }
}