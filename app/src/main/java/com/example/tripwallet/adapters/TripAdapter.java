package com.example.tripwallet.adapters;

import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tripwallet.DatabaseHelper;
import com.example.tripwallet.R;
import com.example.tripwallet.TripDetailsActivity;
import java.text.NumberFormat;
import java.util.Locale;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {
    private Cursor cursor;
    private DatabaseHelper dbHelper;
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

    public TripAdapter(Cursor cursor, DatabaseHelper dbHelper) {
        this.cursor = cursor;
        this.dbHelper = dbHelper;
    }

    @Override
    public TripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trip, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TripViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {
            long tripId = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_TRIP_ID));
            String destination = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DESTINATION));
            String startDate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_START_DATE));
            String endDate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_END_DATE));
            double budget = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_BUDGET));
            double totalExpenses = dbHelper.getTotalExpenses(tripId);

            holder.destinationText.setText(destination);
            holder.datesText.setText(String.format("%s - %s", startDate, endDate));
            holder.budgetText.setText(currencyFormat.format(budget));
            holder.expensesText.setText(currencyFormat.format(totalExpenses));

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), TripDetailsActivity.class);
                intent.putExtra("trip_id", tripId);
                v.getContext().startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    static class TripViewHolder extends RecyclerView.ViewHolder {
        TextView destinationText, datesText, budgetText, expensesText;

        TripViewHolder(View view) {
            super(view);
            destinationText = view.findViewById(R.id.destinationText);
            datesText = view.findViewById(R.id.datesText);
            budgetText = view.findViewById(R.id.budgetText);
            expensesText = view.findViewById(R.id.expensesText);
        }
    }
} 