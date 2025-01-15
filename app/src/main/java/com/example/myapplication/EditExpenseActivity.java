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

public class EditExpenseActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "FinanceAppPrefs";
    private static final String KEY_EXPENSE_LIST = "ExpenseList";

    private Expense expenseToEdit;
    private EditText editExpenseName, editExpenseAmount;
    private double currentBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);

        // Приводим тип к Expense
        expenseToEdit = (Expense) getIntent().getSerializableExtra("expense");
        currentBalance = getIntent().getDoubleExtra("currentBalance", 0.0);

        if (expenseToEdit == null) {
            Toast.makeText(this, "Ошибка: расход не найден", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        editExpenseName = findViewById(R.id.edit_expense_name);
        editExpenseAmount = findViewById(R.id.edit_expense_amount);

        editExpenseName.setText(expenseToEdit.getName());
        editExpenseAmount.setText(String.valueOf(expenseToEdit.getAmount()));

        findViewById(R.id.btn_save_expense).setOnClickListener(v -> saveExpense());
    }

    private void saveExpense() {
        String name = editExpenseName.getText().toString();
        String amountStr = editExpenseAmount.getText().toString();

        if (name.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        double newAmount = Double.parseDouble(amountStr);

        Expense updatedExpense = new Expense(name, newAmount);

        // Заменяем старый расход на новый
        ArrayList<Expense> expenseList = loadExpenseList();
        for (int i = 0; i < expenseList.size(); i++) {
            if (expenseList.get(i).getName().equals(expenseToEdit.getName())) {
                double oldAmount = expenseList.get(i).getAmount();
                expenseList.set(i, updatedExpense);

                // Корректируем текущий баланс
                double delta = oldAmount - newAmount;
                currentBalance += delta;

                // Сохраняем новый баланс в Intent
                Intent resultIntent = new Intent();
                resultIntent.putExtra("updatedCurrentBalance", currentBalance);
                setResult(RESULT_OK, resultIntent);

                break;
            }
        }

        saveExpenseList(expenseList);
        finish();
    }

    private void saveExpenseList(ArrayList<Expense> expenseList) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(expenseList);
        editor.putString(KEY_EXPENSE_LIST, json);
        editor.apply();
    }

    private ArrayList<Expense> loadExpenseList() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String json = sharedPreferences.getString(KEY_EXPENSE_LIST, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Expense>>() {}.getType();
        return gson.fromJson(json, type);
    }
}