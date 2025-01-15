package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.ViewHolder> {
    private ArrayList<Income> incomeList;
    private final OnIncomeDeleteListener deleteListener;
    private final OnIncomeEditListener editListener;

    public IncomeAdapter(ArrayList<Income> incomeList, OnIncomeDeleteListener deleteListener, OnIncomeEditListener editListener) {
        this.incomeList = incomeList;
        this.deleteListener = deleteListener;
        this.editListener = editListener;
    }

    // Метод для обновления списка данных
    public void updateData(ArrayList<Income> newIncomeList) {
        this.incomeList = newIncomeList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_income, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Income income = incomeList.get(position);
        holder.incomeName.setText(income.getName());
        holder.incomeAmount.setText(String.format("%.2f ₽", income.getAmount()));
        holder.btnDelete.setOnClickListener(v -> deleteListener.onDelete(income));
        holder.btnEdit.setOnClickListener(v -> editListener.onEdit(income));
    }

    @Override
    public int getItemCount() {
        return incomeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView incomeName, incomeAmount;
        Button btnDelete, btnEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            incomeName = itemView.findViewById(R.id.text_income_name);
            incomeAmount = itemView.findViewById(R.id.text_income_amount);
            btnDelete = itemView.findViewById(R.id.btn_delete_income);
            btnEdit = itemView.findViewById(R.id.btn_edit_income);
        }
    }

    public interface OnIncomeDeleteListener {
        void onDelete(Income income);
    }

    public interface OnIncomeEditListener {
        void onEdit(Income income);
    }
}
