package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {
    private ArrayList<Expense> expenseList;
    private final OnExpenseDeleteListener deleteListener;
    private final OnExpenseEditListener editListener;

    public ExpenseAdapter(ArrayList<Expense> expenseList, OnExpenseDeleteListener deleteListener, OnExpenseEditListener editListener) {
        this.expenseList = expenseList;
        this.deleteListener = deleteListener;
        this.editListener = editListener;
    }

    // Метод для обновления списка данных
    public void updateData(ArrayList<Expense> newExpenseList) {
        this.expenseList = newExpenseList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        holder.expenseName.setText(expense.getName());
        holder.expenseAmount.setText(String.format("%.2f ₽", expense.getAmount()));
        holder.btnDelete.setOnClickListener(v -> deleteListener.onDelete(expense));
        holder.btnEdit.setOnClickListener(v -> editListener.onEdit(expense));
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView expenseName, expenseAmount;
        Button btnDelete, btnEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            expenseName = itemView.findViewById(R.id.text_expense_name);
            expenseAmount = itemView.findViewById(R.id.text_expense_amount);
            btnDelete = itemView.findViewById(R.id.btn_delete_expense);
            btnEdit = itemView.findViewById(R.id.btn_edit_expense);
        }
    }

    public interface OnExpenseDeleteListener {
        void onDelete(Expense expense);
    }

    public interface OnExpenseEditListener {
        void onEdit(Expense expense);
    }
}
