package com.example.BeautySaloonViewEmployee;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.BeautySaloonBusinessLogic.BindingModels.EmployeeBindingModel;
import com.example.BeautySaloonBusinessLogic.BusinessLogics.EmployeeLogic;
import com.example.BeautySaloonBusinessLogic.ViewModels.EmployeeViewModel;
import com.example.BeautySaloonDatabaseImplement.Implements.EmployeeStorage;
import com.example.BeautySaloonPostgresImplements.EmployeeStorageP;

public class HomeActivity extends AppCompatActivity {

    private int id;

    private final EmployeeLogic logic;

    public HomeActivity() {
        if (MainActivity.storage.equals("client-server"))
            logic = new EmployeeLogic(new EmployeeStorageP(PostgresDB.connection));
        else
            logic = new EmployeeLogic(new EmployeeStorage());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Bundle arguments = getIntent().getExtras();
        id = arguments.getInt(AuthorizationFragment.ID);

        TextView textViewEmployee = findViewById(R.id.textViewEmployee);
        Button buttonCosmetic = findViewById(R.id.buttonCosmetic);
        Button buttonReceipt = findViewById(R.id.buttonReceipt);
        Button buttonDistribution = findViewById(R.id.buttonDistribution);
        Button buttonPurchaseList = findViewById(R.id.buttonPurchaseList);
        Button buttonReport = findViewById(R.id.buttonReport);

        EmployeeBindingModel employeeBindingModel = new EmployeeBindingModel();
        employeeBindingModel.id = id;

        EmployeeViewModel employee = logic.read(employeeBindingModel).get(0);
        textViewEmployee.setText("Сотрудник: " + employee.f_Name + " " + employee.l_Name);

        View.OnClickListener oclBtn = v1 -> {
            Intent intent;
            switch (v1.getId()) {
                case R.id.buttonCosmetic:
                    intent = new Intent(this, CosmeticsActivity.class);
                    intent.putExtra(AuthorizationFragment.ID, id);
                    startActivity(intent);
                    break;
                case R.id.buttonReceipt:
                    intent = new Intent(this, ReceiptsActivity.class);
                    intent.putExtra(AuthorizationFragment.ID, id);
                    startActivity(intent);
                    break;
                case R.id.buttonDistribution:
                    intent = new Intent(this, DistributionsActivity.class);
                    intent.putExtra(AuthorizationFragment.ID, id);
                    startActivity(intent);
                    break;
                case R.id.buttonPurchaseList:
                    intent = new Intent(this, PurchaseListActivity.class);
                    intent.putExtra(AuthorizationFragment.ID, id);
                    startActivity(intent);
                    break;
                case R.id.buttonReport:
                    intent = new Intent(this, ReportCosmeticsActivity.class);
                    intent.putExtra(AuthorizationFragment.ID, id);
                    startActivity(intent);
                    break;
            }
        };

        buttonCosmetic.setOnClickListener(oclBtn);
        buttonReceipt.setOnClickListener(oclBtn);
        buttonDistribution.setOnClickListener(oclBtn);
        buttonPurchaseList.setOnClickListener(oclBtn);
        buttonReport.setOnClickListener(oclBtn);
    }
}