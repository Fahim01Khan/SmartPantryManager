package com.fahimkhan.smartpantry.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fahimkhan.smartpantry.R;
import com.fahimkhan.smartpantry.models.PantryItem;
import com.fahimkhan.smartpantry.utils.ExpiryChecker;
import com.fahimkhan.smartpantry.utils.FormatUtils;
import com.google.android.material.color.MaterialColors;

import java.util.List;

/**
 * Binds a list of PantryItems to rows in the pantry RecyclerView.
 * Edit and delete taps are passed back to the Activity through a listener,
 * so the adapter only handles display and never touches the database.
 */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    /**
     * Implemented by the Activity to respond to the row's buttons.
     */
    public interface OnPantryItemActionListener {
        void onEditItem(PantryItem item);

        void onDeleteItem(PantryItem item);
    }

    private final List<PantryItem> items;
    private final OnPantryItemActionListener listener;
    private boolean expiryAlertsEnabled;
    private int warningDays;
    private String today;

    public PantryAdapter(List<PantryItem> items, OnPantryItemActionListener listener) {
        this.items = items;
        this.listener = listener;
    }

    /**
     * Builds a new, empty row from item_pantry.xml.
     */
    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    /**
     * Fills an existing (possibly recycled) row with one item's data.
     */
    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = items.get(position);

        holder.textName.setText(item.getName());
        holder.textAmount.setText(FormatUtils.formatAmount(item.getQuantity(), item.getUnit()));

        // Every branch sets visibility, text AND colour: a recycled row may still be
        // showing the previous item's expiry date, possibly in red
        if (item.hasExpiryDate()) {
            Context context = holder.itemView.getContext();
            String date = FormatUtils.formatExpiryDate(item.getExpiryDate());
            ExpiryChecker.Status status = expiryAlertsEnabled
                    ? ExpiryChecker.statusOf(item.getExpiryDate(), today, warningDays)
                    : ExpiryChecker.Status.FINE;

            if (status == ExpiryChecker.Status.EXPIRED || status == ExpiryChecker.Status.EXPIRING_SOON) {
                int message = status == ExpiryChecker.Status.EXPIRED
                        ? R.string.expiry_expired : R.string.expiry_soon;
                holder.textExpiry.setText(context.getString(message, date));
                holder.textExpiry.setTextColor(MaterialColors.getColor(
                        holder.textExpiry, com.google.android.material.R.attr.colorError));
            } else {
                holder.textExpiry.setText(context.getString(R.string.expires_on, date));
                holder.textExpiry.setTextColor(holder.defaultExpiryColors);
            }
            holder.textExpiry.setVisibility(View.VISIBLE);
        } else {
            holder.textExpiry.setVisibility(View.GONE);
        }

        holder.buttonEdit.setOnClickListener(v -> listener.onEditItem(item));
        holder.buttonDelete.setOnClickListener(v -> listener.onDeleteItem(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Replaces the displayed list, e.g. after the pantry is reloaded from the database.
     */
    public void setItems(List<PantryItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    /**
     * Applies the user's expiry settings; call before setItems so rows are drawn with them.
     */
    public void setExpirySettings(boolean alertsEnabled, int warningDays) {
        this.expiryAlertsEnabled = alertsEnabled;
        this.warningDays = warningDays;
        this.today = ExpiryChecker.today();
    }

    /**
     * Holds references to one row's views so findViewById runs once per row, not per bind.
     */
    public static class PantryViewHolder extends RecyclerView.ViewHolder {
        final TextView textName;
        final TextView textAmount;
        final TextView textExpiry;
        final ImageButton buttonEdit;
        final ImageButton buttonDelete;
        final ColorStateList defaultExpiryColors;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textItemName);
            textAmount = itemView.findViewById(R.id.textItemAmount);
            textExpiry = itemView.findViewById(R.id.textItemExpiry);
            buttonEdit = itemView.findViewById(R.id.buttonEditItem);
            buttonDelete = itemView.findViewById(R.id.buttonDeleteItem);
            defaultExpiryColors = textExpiry.getTextColors();
        }
    }
}
