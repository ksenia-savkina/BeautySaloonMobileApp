package com.example.BeautySaloonViewEmployee;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.BeautySaloonBusinessLogic.BindingModels.ReportBindingModelEmployee;
import com.example.BeautySaloonBusinessLogic.BusinessLogics.ReportLogicEmployee;
import com.example.BeautySaloonDatabaseImplement.Implements.ReportStorage;
import com.example.BeautySaloonPostgresImplements.ReportStorageP;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ReportCosmeticsActivity extends AppCompatActivity {
    private final int REQUEST_CODE_PERMISSION_PDF = 1;

    private int id;

    private final ReportLogicEmployee report;

    EditText editTextDateFrom;

    EditText editTextDateTo;

    SimpleDateFormat formatter;

    public ReportCosmeticsActivity() {
        if (MainActivity.storage.equals("client-server"))
            report = new ReportLogicEmployee(new ReportStorageP(PostgresDB.connection));
        else
            report = new ReportLogicEmployee(new ReportStorage());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_cosmetics);

        formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);

        id = getIntent().getExtras().getInt(AuthorizationFragment.ID);

        Button buttonPdf = findViewById(R.id.buttonPdf);
        Button buttonCancel = findViewById(R.id.buttonCancelRC);
        editTextDateFrom = findViewById(R.id.editTextDateFrom);
        editTextDateTo = findViewById(R.id.editTextDateTo);

        int permissionStatusRead = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionStatusWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        View.OnClickListener oclBtn = v1 -> {
            switch (v1.getId()) {
                case R.id.buttonPdf:
                    if (permissionStatusRead == PackageManager.PERMISSION_GRANTED && permissionStatusWrite == PackageManager.PERMISSION_GRANTED) {
                        doPdf();
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_CODE_PERMISSION_PDF);
                    }
                    break;
                case R.id.buttonCancelRC:
                    finish();
                    break;
            }
        };

        buttonPdf.setOnClickListener(oclBtn);
        buttonCancel.setOnClickListener(oclBtn);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_PDF:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doPdf();
                }
                return;
        }
    }

    private void doPdf() {
        if (checkFields()) {
            try {
                ReportBindingModelEmployee reportBindingModelEmployee = new ReportBindingModelEmployee();
                reportBindingModelEmployee.fileName = "Test.pdf";
                reportBindingModelEmployee.dateFrom = formatter.parse(editTextDateFrom.getText().toString());
                reportBindingModelEmployee.dateTo = formatter.parse(editTextDateTo.getText().toString());
                reportBindingModelEmployee.employeeId = id;
                report.saveCosmeticsToPdfFile(reportBindingModelEmployee, this);
                Toast.makeText(this, R.string.SuccessfulDoc, Toast.LENGTH_LONG).show();
            } catch (Exception ex) {
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkFields() {
        if (editTextDateFrom.getText().length() == 0) {
            Toast.makeText(this, R.string.EmptyEditDateFrom, Toast.LENGTH_LONG).show();
            return false;
        }
        if (editTextDateTo.getText().length() == 0) {
            Toast.makeText(this, R.string.EmptyEditDateTo, Toast.LENGTH_LONG).show();
            return false;
        }
        try {
            if (formatter.parse(editTextDateTo.getText().toString()).before(formatter.parse(editTextDateFrom.getText().toString()))) {
                Toast.makeText(this, R.string.ErrorDate, Toast.LENGTH_LONG).show();
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }
}