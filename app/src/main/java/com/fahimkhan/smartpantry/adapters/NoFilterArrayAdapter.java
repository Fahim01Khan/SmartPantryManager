package com.fahimkhan.smartpantry.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * An ArrayAdapter whose filter always returns the full list.
 * Used for exposed dropdown menus, so every option stays visible even when
 * the field already contains text (e.g. in Edit mode or after rotation).
 */
public class NoFilterArrayAdapter<T> extends ArrayAdapter<T> {

    private final List<T> allItems;

    public NoFilterArrayAdapter(@NonNull Context context, int layoutResource,
                                @NonNull List<T> items) {
        super(context, layoutResource, items);
        this.allItems = new ArrayList<>(items);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                results.values = allItems;
                results.count = allItems.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                notifyDataSetChanged();   // The list itself never changes
            }
        };
    }
}
