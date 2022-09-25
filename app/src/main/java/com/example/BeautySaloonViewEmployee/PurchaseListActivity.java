package com.example.BeautySaloonViewEmployee;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.BeautySaloonBusinessLogic.BindingModels.CosmeticBindingModel;
import com.example.BeautySaloonBusinessLogic.BindingModels.ReportBindingModelEmployee;
import com.example.BeautySaloonBusinessLogic.BusinessLogics.CosmeticLogic;
import com.example.BeautySaloonBusinessLogic.BusinessLogics.ReportLogicEmployee;
import com.example.BeautySaloonBusinessLogic.ViewModels.CosmeticViewModel;
import com.example.BeautySaloonDatabaseImplement.Implements.CosmeticStorage;
import com.example.BeautySaloonDatabaseImplement.Implements.ReportStorage;
import com.example.BeautySaloonPostgresImplements.CosmeticStorageP;
import com.example.BeautySaloonPostgresImplements.ReportStorageP;

import java.util.ArrayList;
import java.util.List;

public class PurchaseListActivity extends AppCompatActivity {

    private final int REQUEST_CODE_PERMISSION_DOCX = 1;

    private final int REQUEST_CODE_PERMISSION_XLSX = 2;

    private int id;

    private final CosmeticLogic logic;

    private final ReportLogicEmployee report;

    private List<CosmeticViewModel> list;

    private ArrayAdapter<CosmeticViewModel> adapter;

    private ListView listViewCosmeticPL;

    public PurchaseListActivity() {
        if (MainActivity.storage.equals("client-server")) {
            logic = new CosmeticLogic(new CosmeticStorageP(PostgresDB.connection));
            report = new ReportLogicEmployee(new ReportStorageP(PostgresDB.connection));
        } else {
            logic = new CosmeticLogic(new CosmeticStorage());
            report = new ReportLogicEmployee(new ReportStorage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_list);

        id = getIntent().getExtras().getInt(AuthorizationFragment.ID);

        listViewCosmeticPL = findViewById(R.id.listViewCosmeticPL);
        listViewCosmeticPL.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        loadData();

        Button buttonWord = findViewById(R.id.buttonWord);
        Button buttonExcel = findViewById(R.id.buttonExcel);
        Button buttonCancel = findViewById(R.id.buttonCancelPL);

        int permissionStatusRead = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionStatusWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);


        View.OnClickListener oclBtn = v1 -> {
            switch (v1.getId()) {
                case R.id.buttonWord:
                    if (permissionStatusRead == PackageManager.PERMISSION_GRANTED && permissionStatusWrite == PackageManager.PERMISSION_GRANTED) {
                        doWord();
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_CODE_PERMISSION_DOCX);
                    }
                    break;
                case R.id.buttonExcel:
                    if (permissionStatusRead == PackageManager.PERMISSION_GRANTED && permissionStatusWrite == PackageManager.PERMISSION_GRANTED) {
                        doExcel();
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_CODE_PERMISSION_XLSX);
                    }
                    break;
                case R.id.buttonCancelPL:
                    finish();
                    break;
            }
        };

        buttonWord.setOnClickListener(oclBtn);
        buttonExcel.setOnClickListener(oclBtn);
        buttonCancel.setOnClickListener(oclBtn);
    }

    private void loadData() {
        CosmeticBindingModel cosmeticBindingModel = new CosmeticBindingModel();
        cosmeticBindingModel.id = -1;
        cosmeticBindingModel.employeeId = id;
        try {
            list = logic.read(cosmeticBindingModel);
            if (list != null) {
                adapter = new ArrayAdapter<>(this, R.layout.file_list, list);
                listViewCosmeticPL.setAdapter(adapter);
            }
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_DOCX:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doWord();
                }
                return;
            case REQUEST_CODE_PERMISSION_XLSX:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doExcel();
                }
                return;
        }
    }

    private void doWord() {
        if (listViewCosmeticPL.getCheckedItemCount() != 0) {
            try {
                List<Integer> listCosmetic = new ArrayList<>();

                for (int i = 0; i < listViewCosmeticPL.getCount(); i++) {
                    if (listViewCosmeticPL.isItemChecked(i))
                        listCosmetic.add(list.get(i).id);
                }

                ReportBindingModelEmployee reportBindingModelEmployee = new ReportBindingModelEmployee();
                reportBindingModelEmployee.fileName = "Test.docx";
                reportBindingModelEmployee.purchaseCosmetics = listCosmetic;
                reportBindingModelEmployee.employeeId = id;
                report.savePurchaseListToWordFile(reportBindingModelEmployee, this);
                Toast.makeText(this, R.string.SuccessfulDoc, Toast.LENGTH_LONG).show();
            } catch (Exception ex) {
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, R.string.EmptySpinner, Toast.LENGTH_LONG).show();
        }
    }

    private void doExcel() {
        if (listViewCosmeticPL.getCheckedItemCount() != 0) {
            try {
                List<Integer> listCosmetic = new ArrayList<>();

                for (int i = 0; i < listViewCosmeticPL.getCount(); i++) {
                    if (listViewCosmeticPL.isItemChecked(i))
                        listCosmetic.add(list.get(i).id);
                }
                ReportBindingModelEmployee reportBindingModelEmployee = new ReportBindingModelEmployee();
                reportBindingModelEmployee.fileName = "Test.xlsx";
                reportBindingModelEmployee.purchaseCosmetics = listCosmetic;
                reportBindingModelEmployee.employeeId = id;
                report.savePurchaseListToExcelFile(reportBindingModelEmployee, this);
                Toast.makeText(this, R.string.SuccessfulDoc, Toast.LENGTH_LONG).show();
            } catch (Exception ex) {
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, R.string.EmptySpinner, Toast.LENGTH_LONG).show();
        }
    }
}