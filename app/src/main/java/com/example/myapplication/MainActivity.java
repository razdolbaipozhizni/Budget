package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "FinanceAppPrefs";
    private static final String KEY_INCOME_LIST = "IncomeList";
    private static final String KEY_EXPENSE_LIST = "ExpenseList";

    private TextView textBalance;
    private ArrayList<Income> incomeList;
    private ArrayList<Expense> expenseList;
    private double totalIncome = 0.0;
    private double totalExpense = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textBalance = findViewById(R.id.text_total_income_main);
        findViewById(R.id.btn_add_income_main).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, IncomeActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.btn_add_expense_main).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ExpenseActivity.class);
            intent.putExtra("currentBalance", totalIncome - totalExpense); // Передаем остаток
            startActivity(intent);
        });
        findViewById(R.id.btn_analysis).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AnalysisActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadIncomeList();
        loadExpenseList();
        calculateTotalIncome();
        calculateTotalExpense();
        updateBalance();
    }

    private void loadIncomeList() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String json = sharedPreferences.getString(KEY_INCOME_LIST, null);
        if (json == null) {
            incomeList = new ArrayList<>();
        } else {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Income>>() {}.getType();
            incomeList = gson.fromJson(json, type);
        }
    }

    private void loadExpenseList() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String json = sharedPreferences.getString(KEY_EXPENSE_LIST, null);
        if (json == null) {
            expenseList = new ArrayList<>();
        } else {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Expense>>() {}.getType();
            expenseList = gson.fromJson(json, type);
        }
    }

    private void calculateTotalIncome() {
        totalIncome = 0.0;
        for (Income income : incomeList) {
            totalIncome += income.getAmount();
        }
    }

    private void calculateTotalExpense() {
        totalExpense = 0.0;
        for (Expense expense : expenseList) {
            totalExpense += expense.getAmount();
        }
    }

    private void updateBalance() {
        double balance = totalIncome - totalExpense;
        textBalance.setText(String.format("Остаток средств: %.2f ₽", balance));
    }
}