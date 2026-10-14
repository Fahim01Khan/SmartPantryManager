package com.fahimkhan.smartpantry.activities;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fahimkhan.smartpantry.R;
import com.fahimkhan.smartpantry.adapters.PantryAdapter;
import com.fahimkhan.smartpantry.database.PantryDataSource;
import com.fahimkhan.smartpantry.models.PantryItem;
import com.fahimkhan.smartpantry.utils.ExpiryChecker;
import com.fahimkhan.smartpantry.utils.NavigationHelper;
import com.fahimkhan.smartpantry.utils.SettingsManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Launch screen. Displays every pantry item from the database and
 * provides access to adding, editing and deleting items.
 */
public class PantryListActivity extends AppCompatActivity
        implements PantryAdapter.OnPantryItemActionListener {

    private static final String TAG = "PantryListActivity";

    private RecyclerView recyclerPantry;
    private TextView textEmptyPantry;
    private TextView textExpiryBanner;
    private PantryAdapter adapter;
    private AlertDialog deleteDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantry_list);
        // Root: top inset only. Content: side insets. Nav bar: full width; pads its own bottom.
        View content = findViewById(R.id.pantryContent);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            v.setPadding(0, bars.top, 0, 0);
            content.setPadding(bars.left, 0, bars.right, 0);
            return insets;
        });

        initRecyclerView();
        initAddButton();
        NavigationHelper.setUp(this, findViewById(R.id.bottomNav), R.id.nav_pantry);
    }

    /**
     * Reloads the pantry every time this screen becomes visible, so changes
     * made on other screens (add, edit) appear as soon as the user returns.
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadPantryItems();
    }

    /**
     * Closes any open dialogue so it is not leaked when the Activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        if (deleteDialog != null && deleteDialog.isShowing()) {
            deleteDialog.dismiss();
        }
        super.onDestroy();
    }

    /**
     * One-time setup: connects the RecyclerView to its LayoutManager and Adapter.
     */
    private void initRecyclerView() {
        recyclerPantry = findViewById(R.id.recyclerPantry);
        textEmptyPantry = findViewById(R.id.textEmptyPantry);

        recyclerPantry.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PantryAdapter(new ArrayList<>(), this);
        recyclerPantry.setAdapter(adapter);

        textExpiryBanner = findViewById(R.id.textExpiryBanner);
    }

    private void initAddButton() {
        FloatingActionButton fabAddItem = findViewById(R.id.fabAddItem);
        // No extras, so the form opens in Add mode
        fabAddItem.setOnClickListener(v ->
                startActivity(new Intent(this, IngredientFormActivity.class)));
    }

    /**
     * Reads all pantry items from the database and shows the list or the empty message.
     */
    private void loadPantryItems() {
        PantryDataSource dataSource = new PantryDataSource(this);
        List<PantryItem> items = new ArrayList<>();
        try {
            dataSource.open();
            items = dataSource.getAllPantryItems();
        } catch (SQLException e) {
            Log.e(TAG, "Failed to load pantry items", e);
            Toast.makeText(this, R.string.error_loading_pantry, Toast.LENGTH_LONG).show();
        } finally {
            dataSource.close();
        }

        // Settings may have changed on the Settings screen, so read them on every load
        boolean alertsEnabled = SettingsManager.isExpiryAlertEnabled(this);
        int warningDays = SettingsManager.getExpiryWarningDays(this);
        adapter.setExpirySettings(alertsEnabled, warningDays);
        adapter.setItems(items);
        updateExpiryBanner(items, alertsEnabled, warningDays);

        boolean isEmpty = items.isEmpty();
        textEmptyPantry.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerPantry.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    // ----- PantryAdapter.OnPantryItemActionListener -----

    @Override
    public void onEditItem(PantryItem item) {
        // Passing the id opens the form in Edit mode for this item
        Intent intent = new Intent(this, IngredientFormActivity.class);
        intent.putExtra(IngredientFormActivity.EXTRA_ITEM_ID, item.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteItem(PantryItem item) {
        deleteDialog = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(getString(R.string.dialog_delete_message, item.getName()))
                .setNegativeButton(R.string.cancel, null)   // null simply closes the dialogue
                .setPositiveButton(R.string.delete, (dialog, which) -> deleteItem(item))
                .show();
    }

    /**
     * Deletes the item from the database, then reloads the list so it matches.
     */
    private void deleteItem(PantryItem item) {
        PantryDataSource dataSource = new PantryDataSource(this);
        boolean deleted = false;
        try {
            dataSource.open();
            deleted = dataSource.deletePantryItem(item.getId());
        } catch (SQLException e) {
            Log.e(TAG, "Failed to delete pantry item " + item.getId(), e);
        } finally {
            dataSource.close();
        }

        if (deleted) {
            Toast.makeText(this, getString(R.string.item_deleted, item.getName()),
                    Toast.LENGTH_SHORT).show();
            loadPantryItems();   // Refreshes the list and shows the empty state if needed
        } else {
            Toast.makeText(this, R.string.error_deleting_item, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Shows a count of expired and expiring items, if alerts are on and there are any.
     */
    private void updateExpiryBanner(List<PantryItem> items, boolean alertsEnabled, int warningDays) {
        int count = 0;
        if (alertsEnabled) {
            String today = ExpiryChecker.today();
            for (PantryItem item : items) {
                ExpiryChecker.Status status =
                        ExpiryChecker.statusOf(item.getExpiryDate(), today, warningDays);
                if (status == ExpiryChecker.Status.EXPIRED
                        || status == ExpiryChecker.Status.EXPIRING_SOON) {
                    count++;
                }
            }
        }
        if (count > 0) {
            textExpiryBanner.setText(
                    getResources().getQuantityString(R.plurals.expiry_banner, count, count));
            textExpiryBanner.setVisibility(View.VISIBLE);
        } else {
            textExpiryBanner.setVisibility(View.GONE);
        }
    }
}
