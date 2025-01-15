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

public class ExpenseActivity extends AppCompatActivity {
    private static final int EDIT_EXPENSE_REQUEST_CODE = 1;

    private static final String PREFS_NAME = "FinanceAppPrefs";
    private static final String KEY_EXPENSE_LIST = "ExpenseList";

    private TextView textTotalBalance;
    private RecyclerView recyclerExpenseHistory;
    private ExpenseAdapter adapter;
    private ArrayList<Expense> expenseList;
    private double currentBalance; // Исходный баланс переданный из MainActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        textTotalBalance = findViewById(R.id.text_total_income);
        recyclerExpenseHistory = findViewById(R.id.recycler_expense_history);

        currentBalance = getIntent().getDoubleExtra("currentBalance", 0.0);
        updateTotalBalanceDisplay();

        expenseList = loadExpenseList();
        adapter = new ExpenseAdapter(expenseList, this::deleteExpense, this::editExpense);
        recyclerExpenseHistory.setLayoutManager(new LinearLayoutManager(this));
        recyclerExpenseHistory.setAdapter(adapter);

        findViewById(R.id.btn_add_expense).setOnClickListener(v -> addExpense());
    }

    private void addExpense() {
        String name = ((EditText) findViewById(R.id.edit_expense_name)).getText().toString();
        String amountStr = ((EditText) findViewById(R.id.edit_expense_amount)).getText().toString();
        if (name.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }
        double amount = Double.parseDouble(amountStr);
        if (amount > currentBalance) {
            Toast.makeText(this, "Недостаточно средств!", Toast.LENGTH_SHORT).show();
            return;
        }
        Expense expense = new Expense(name, amount);
        expenseList.add(expense);
        currentBalance -= amount;
        saveExpenseList();
        updateTotalBalanceDisplay();
        adapter.notifyDataSetChanged();
    }

    private void deleteExpense(Expense expense) {
        currentBalance += expense.getAmount();
        expenseList.remove(expense);
        saveExpenseList();
        updateTotalBalanceDisplay();
        adapter.notifyDataSetChanged();
    }

    private void editExpense(Expense expense) {
        Intent intent = new Intent(this, EditExpenseActivity.class);
        intent.putExtra("expense", expense);
        intent.putExtra("currentBalance", currentBalance); // Передаем текущий баланс
        startActivityForResult(intent, EDIT_EXPENSE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_EXPENSE_REQUEST_CODE && resultCode == RESULT_OK) {
            // Перезагружаем данные и обновляем UI
            expenseList = loadExpenseList();
            adapter.updateData(expenseList);  // Обновляем данные адаптера

            // Если есть обновленный баланс, используем его
            if (data != null) {
                currentBalance = data.getDoubleExtra("updatedCurrentBalance", currentBalance);
            }

            // Пересчитываем текущий баланс и обновляем его отображение
            calculateTotalExpense();
            updateTotalBalanceDisplay();
        }
    }

    private void calculateTotalExpense() {
        double totalExpense = 0.0;
        for (Expense expense : expenseList) {
            totalExpense += expense.getAmount();
        }
        // Не нужно пересчитывать баланс здесь, так как он уже был обновлен в onActivityResult
    }

    private void updateTotalBalanceDisplay() {
        textTotalBalance.setText(String.format("Остаток средств: %.2f ₽", currentBalance));
    }

    private void saveExpenseList() {
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