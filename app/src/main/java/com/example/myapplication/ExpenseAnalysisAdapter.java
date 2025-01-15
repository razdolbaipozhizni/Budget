package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ExpenseAnalysisAdapter extends RecyclerView.Adapter<ExpenseAnalysisAdapter.ViewHolder> {
    private final ArrayList<PieChartView.PieEntry> expenseEntries;
    private final int[] colors;

    public ExpenseAnalysisAdapter(ArrayList<Expense> expenseList, int[] colors) {
        this.colors = colors;
        this.expenseEntries = createPieEntries(expenseList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense_analysis, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PieChartView.PieEntry entry = expenseEntries.get(position);
        holder.expenseName.setText(entry.getLabel());
        holder.expenseAmount.setText(String.format("%.2f ₽", entry.getValue()));
        holder.colorMarker.setBackgroundColor(entry.getColor());
    }

    @Override
    public int getItemCount() {
        return expenseEntries.size();
    }

    private ArrayList<PieChartView.PieEntry> createPieEntries(ArrayList<Expense> expenseList) {
        ArrayList<PieChartView.PieEntry> pieEntries = new ArrayList<>();
        int colorIndex = 0;

        for (Expense expense : expenseList) {
            int color = colors[colorIndex % colors.length];
            pieEntries.add(new PieChartView.PieEntry(expense.getName(), (float) expense.getAmount(), color));
            colorIndex++;
        }

        return pieEntries;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView expenseName, expenseAmount;
        View colorMarker;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            expenseName = itemView.findViewById(R.id.text_expense_name_analysis);
            expenseAmount = itemView.findViewById(R.id.text_expense_amount_analysis);
            colorMarker = itemView.findViewById(R.id.color_marker);
        }
    }
}