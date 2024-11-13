package com.example.tripwallet.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tripwallet.DatabaseHelper;
import com.example.tripwallet.R;
import java.text.NumberFormat;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
    private Cursor cursor;
    private Context context;
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
    private OnExpenseDeleteListener deleteListener;

    public interface OnExpenseDeleteListener {
        void onExpenseDelete(long expenseId);
    }

    public ExpenseAdapter(Context context, Cursor cursor, OnExpenseDeleteListener listener) {
        this.context = context;
        this.cursor = cursor;
        this.deleteListener = listener;
    }

    @Override
    public ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ExpenseViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {
            long expenseId = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_EXPENSE_ID));
            double amount = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_AMOUNT));
            String category = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY));
            String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE));
            String description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION));

            holder.amountText.setText(currencyFormat.format(amount));
            holder.categoryText.setText(category);
            holder.dateText.setText(date);
            holder.descriptionText.setText(description);

            holder.deleteButton.setOnClickListener(v -> deleteListener.onExpenseDelete(expenseId));
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
        notifyDataSetChanged();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView amountText, categoryText, dateText, descriptionText;
        ImageButton deleteButton;

        ExpenseViewHolder(View view) {
            super(view);
            amountText = view.findViewById(R.id.amountText);
            categoryText = view.findViewById(R.id.categoryText);
            dateText = view.findViewById(R.id.dateText);
            descriptionText = view.findViewById(R.id.descriptionText);
            deleteButton = view.findViewById(R.id.deleteButton);
        }
    }
} 