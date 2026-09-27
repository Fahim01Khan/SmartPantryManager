package com.fahimkhan.smartpantry.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fahimkhan.smartpantry.R;
import com.fahimkhan.smartpantry.models.PantryItem;
import com.fahimkhan.smartpantry.utils.FormatUtils;

import java.util.List;

/**
 * Binds a list of PantryItems to rows in the pantry RecyclerView.
 * Edit and delete taps are passed back to the Activity through a listener,
 * so the adapter only handles display and never touches the database.
 */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    /** Implemented by the Activity to respond to the row's buttons. */
    public interface OnPantryItemActionListener {
        void onEditItem(PantryItem item);
        void onDeleteItem(PantryItem item);
    }

    private final List<PantryItem> items;
    private final OnPantryItemActionListener listener;

    public PantryAdapter(List<PantryItem> items, OnPantryItemActionListener listener) {
        this.items = items;
        this.listener = listener;
    }

    /** Builds a new, empty row from item_pantry.xml. */
    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    /** Fills an existing (possibly recycled) row with one item's data. */
    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = items.get(position);

        holder.textName.setText(item.getName());
        holder.textAmount.setText(FormatUtils.formatAmount(item.getQuantity(), item.getUnit()));

        // Both branches must set visibility: a recycled row may still be
        // showing the previous item's expiry date.
        if (item.hasExpiryDate()) {
            String expiry = FormatUtils.formatExpiryDate(item.getExpiryDate());
            holder.textExpiry.setText(
                    holder.itemView.getContext().getString(R.string.expires_on, expiry));
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

    /** Replaces the displayed list, e.g. after the pantry is reloaded from the database. */
    public void setItems(List<PantryItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    /** Holds references to one row's views so findViewById runs once per row, not per bind. */
    static class PantryViewHolder extends RecyclerView.ViewHolder {
        final TextView textName;
        final TextView textAmount;
        final TextView textExpiry;
        final ImageButton buttonEdit;
        final ImageButton buttonDelete;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textItemName);
            textAmount = itemView.findViewById(R.id.textItemAmount);
            textExpiry = itemView.findViewById(R.id.textItemExpiry);
            buttonEdit = itemView.findViewById(R.id.buttonEditItem);
            buttonDelete = itemView.findViewById(R.id.buttonDeleteItem);
        }
    }
}
