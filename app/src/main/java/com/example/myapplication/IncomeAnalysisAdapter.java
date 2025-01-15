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

public class IncomeAnalysisAdapter extends RecyclerView.Adapter<IncomeAnalysisAdapter.ViewHolder> {
    private final ArrayList<PieChartView.PieEntry> incomeEntries;
    private final int[] colors;

    public IncomeAnalysisAdapter(ArrayList<Income> incomeList, int[] colors) {
        this.colors = colors;
        this.incomeEntries = createPieEntries(incomeList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_income_analysis, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PieChartView.PieEntry entry = incomeEntries.get(position);
        holder.incomeName.setText(entry.getLabel());
        holder.incomeAmount.setText(String.format("%.2f ₽", entry.getValue()));
        holder.colorMarker.setBackgroundColor(entry.getColor());
    }

    @Override
    public int getItemCount() {
        return incomeEntries.size();
    }

    private ArrayList<PieChartView.PieEntry> createPieEntries(ArrayList<Income> incomeList) {
        ArrayList<PieChartView.PieEntry> pieEntries = new ArrayList<>();
        int colorIndex = 0;

        for (Income income : incomeList) {
            int color = colors[colorIndex % colors.length];
            pieEntries.add(new PieChartView.PieEntry(income.getName(), (float) income.getAmount(), color));
            colorIndex++;
        }

        return pieEntries;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView incomeName, incomeAmount;
        View colorMarker;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            incomeName = itemView.findViewById(R.id.text_income_name_analysis);
            incomeAmount = itemView.findViewById(R.id.text_income_amount_analysis);
            colorMarker = itemView.findViewById(R.id.color_marker);
        }
    }
}