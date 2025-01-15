package com.example.myapplication;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class AnalysisActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "FinanceAppPrefs";
    private static final String KEY_INCOME_LIST = "IncomeList";
    private static final String KEY_EXPENSE_LIST = "ExpenseList";

    private TextView textTotalIncome, textTotalExpense, textBalance;
    private RecyclerView recyclerIncomes, recyclerExpenses;
    private IncomeAnalysisAdapter incomeAdapter;
    private ExpenseAnalysisAdapter expenseAdapter;
    private PieChartView pieChartIncomes, pieChartExpenses;

    // Массив цветов для использования в диаграммах и списках
    private static final int[] COLORS = new int[]{
            Color.parseColor("#FF5722"), // Оранжевый
            Color.parseColor("#4CAF50"), // Зеленый
            Color.parseColor("#3F51B5"), // Синий
            Color.parseColor("#E91E63"), // Розовый
            Color.parseColor("#9C27B0"), // Пурпурный
            Color.parseColor("#00BCD4"), // Циан
            Color.parseColor("#FFEB3B")  // Желтый
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        textTotalIncome = findViewById(R.id.text_total_income);
        textTotalExpense = findViewById(R.id.text_total_expense);
        textBalance = findViewById(R.id.text_balance);
        recyclerIncomes = findViewById(R.id.recycler_incomes);
        recyclerExpenses = findViewById(R.id.recycler_expenses);
        pieChartIncomes = findViewById(R.id.pie_chart_incomes);
        pieChartExpenses = findViewById(R.id.pie_chart_expenses);

        ArrayList<Income> incomeList = loadIncomeList();
        ArrayList<Expense> expenseList = loadExpenseList();

        double totalIncome = calculateTotalIncome(incomeList);
        double totalExpense = calculateTotalExpense(expenseList);
        double balance = totalIncome - totalExpense;

        textTotalIncome.setText(String.format("Общая сумма доходов: %.2f ₽", totalIncome));
        textTotalExpense.setText(String.format("Общая сумма расходов: %.2f ₽", totalExpense));
        textBalance.setText(String.format("Остаток средств: %.2f ₽", balance));

        incomeAdapter = new IncomeAnalysisAdapter(incomeList, COLORS);
        expenseAdapter = new ExpenseAnalysisAdapter(expenseList, COLORS);

        setupPieCharts(incomeList, expenseList, COLORS);

        // Устанавливаем адаптеры для RecyclerView
        recyclerIncomes.setLayoutManager(new LinearLayoutManager(this));
        recyclerExpenses.setLayoutManager(new LinearLayoutManager(this));

        recyclerIncomes.setAdapter(incomeAdapter);
        recyclerExpenses.setAdapter(expenseAdapter);
    }

    private void setupPieCharts(ArrayList<Income> incomeList, ArrayList<Expense> expenseList, int[] colors) {
        pieChartIncomes.setEntries(createPieEntries(incomeList, colors));
        pieChartExpenses.setEntries(createPieEntries(expenseList, colors));
    }

    private ArrayList<PieChartView.PieEntry> createPieEntries(ArrayList<?> entries, int[] colors) {
        ArrayList<PieChartView.PieEntry> pieEntries = new ArrayList<>();
        int colorIndex = 0;

        for (Object entry : entries) {
            String label;
            double value;

            if (entry instanceof Income) {
                Income income = (Income) entry;
                label = income.getName();
                value = income.getAmount();
            } else if (entry instanceof Expense) {
                Expense expense = (Expense) entry;
                label = expense.getName();
                value = expense.getAmount();
            } else {
                continue;
            }

            // Используем заранее определенные цвета
            int color = colors[colorIndex % colors.length];
            pieEntries.add(new PieChartView.PieEntry(label, (float) value, color));
            colorIndex++;
        }

        return pieEntries;
    }

    private double calculateTotalIncome(ArrayList<Income> incomeList) {
        double totalIncome = 0.0;
        for (Income income : incomeList) {
            totalIncome += income.getAmount();
        }
        return totalIncome;
    }

    private double calculateTotalExpense(ArrayList<Expense> expenseList) {
        double totalExpense = 0.0;
        for (Expense expense : expenseList) {
            totalExpense += expense.getAmount();
        }
        return totalExpense;
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