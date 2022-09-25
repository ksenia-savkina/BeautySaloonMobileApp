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

import com.example.BeautySaloonBusinessLogic.BindingModels.DistributionBindingModel;
import com.example.BeautySaloonBusinessLogic.BusinessLogics.DistributionLogic;
import com.example.BeautySaloonBusinessLogic.ViewModels.DistributionViewModel;
import com.example.BeautySaloonDatabaseImplement.Implements.DistributionStorage;
import com.example.BeautySaloonPostgresImplements.DistributionStorageP;

public class RemovalFragmentDistribution extends DialogFragment implements DialogInterface.OnClickListener {

    private final DistributionLogic logic;

    DistributionBindingModel distributionBindingModel;

    public RemovalFragmentDistribution() {
        if (MainActivity.storage.equals("client-server"))
            logic = new DistributionLogic(new DistributionStorageP(PostgresDB.connection));
        else
            logic = new DistributionLogic(new DistributionStorage());
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        StringBuilder message = new StringBuilder();
        int id = getArguments().getInt(DistributionsFragment.DistributionID);

        int item = 0;

        distributionBindingModel = new DistributionBindingModel();
        distributionBindingModel.id = id;
        DistributionViewModel view = logic.read(distributionBindingModel).get(0);
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
                logic.delete(distributionBindingModel);
            } catch (Exception ex) {
                Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, new Intent());
    }
}