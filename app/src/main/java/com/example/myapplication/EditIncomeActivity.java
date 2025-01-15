package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class EditIncomeActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "FinanceAppPrefs";
    private static final String KEY_INCOME_LIST = "IncomeList";

    private Income incomeToEdit;
    private EditText editIncomeName, editIncomeAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_income);

        // Приводим тип к Income
        incomeToEdit = (Income) getIntent().getSerializableExtra("income");

        if (incomeToEdit == null) {
            Toast.makeText(this, "Ошибка: доход не найден", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        editIncomeName = findViewById(R.id.edit_income_name);
        editIncomeAmount = findViewById(R.id.edit_income_amount);

        editIncomeName.setText(incomeToEdit.getName());
        editIncomeAmount.setText(String.valueOf(incomeToEdit.getAmount()));

        findViewById(R.id.btn_save_income).setOnClickListener(v -> saveIncome());
    }

    private void saveIncome() {
        String name = editIncomeName.getText().toString();
        String amountStr = editIncomeAmount.getText().toString();

        if (name.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);

        Income updatedIncome = new Income(name, amount);

        // Заменяем старый доход на новый
        ArrayList<Income> incomeList = loadIncomeList();
        for (int i = 0; i < incomeList.size(); i++) {
            if (incomeList.get(i).getName().equals(incomeToEdit.getName())) {
                incomeList.set(i, updatedIncome);
                break;
            }
        }

        saveIncomeList(incomeList);
        setResult(RESULT_OK); // Устанавливаем результат для обновления данных
        finish();
    }

    private void saveIncomeList(ArrayList<Income> incomeList) {
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