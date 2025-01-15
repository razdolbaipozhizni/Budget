package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class IncomeActivity extends AppCompatActivity {
    private static final int EDIT_INCOME_REQUEST_CODE = 1;

    private static final String PREFS_NAME = "FinanceAppPrefs";
    private static final String KEY_INCOME_LIST = "IncomeList";

    private TextView textTotalIncome;
    private RecyclerView recyclerIncomeHistory;
    private IncomeAdapter adapter;
    private ArrayList<Income> incomeList;
    private double totalIncome = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        textTotalIncome = findViewById(R.id.text_total_income);
        recyclerIncomeHistory = findViewById(R.id.recycler_income_history);

        incomeList = loadIncomeList();
        adapter = new IncomeAdapter(incomeList, this::deleteIncome, this::editIncome);
        recyclerIncomeHistory.setLayoutManager(new LinearLayoutManager(this));
        recyclerIncomeHistory.setAdapter(adapter);

        calculateTotalIncome();
        updateTotalIncome();

        findViewById(R.id.btn_add_income).setOnClickListener(v -> addIncome());
    }

    private void addIncome() {
        String name = ((EditText) findViewById(R.id.edit_income_name)).getText().toString();
        String amountStr = ((EditText) findViewById(R.id.edit_income_amount)).getText().toString();
        if (name.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }
        double amount = Double.parseDouble(amountStr);
        Income income = new Income(name, amount);
        incomeList.add(income);
        totalIncome += amount;
        saveIncomeList();
        updateTotalIncome();
        adapter.notifyDataSetChanged();
    }

    private void deleteIncome(Income income) {
        totalIncome -= income.getAmount();
        incomeList.remove(income);
        saveIncomeList();
        updateTotalIncome();
        adapter.notifyDataSetChanged();
    }

    private void editIncome(Income income) {
        Intent intent = new Intent(this, EditIncomeActivity.class);
        intent.putExtra("income", income);
        startActivityForResult(intent, EDIT_INCOME_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_INCOME_REQUEST_CODE && resultCode == RESULT_OK) {
            // Перезагружаем данные и обновляем UI
            incomeList = loadIncomeList();
            adapter.updateData(incomeList);  // Обновляем данные адаптера
            calculateTotalIncome();
            updateTotalIncome();
        }
    }

    private void updateTotalIncome() {
        textTotalIncome.setText(String.format("Общая сумма доходов: %.2f ₽", totalIncome));
    }

    private void calculateTotalIncome() {
        totalIncome = 0.0;
        for (Income income : incomeList) {
            totalIncome += income.getAmount();
        }
        updateTotalIncome();
    }

    private void saveIncomeList() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(incomeList);
        editor.putString(KEY_INCOME_LIST, json);
        editor.apply();
    }

    private ArrayList<Income> loadIncomeList() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String json = sharedPreferences.getString(KEY_INCOME_LIST, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Income>>() {}.getType();
        return gson.fromJson(json, type);
    }
}