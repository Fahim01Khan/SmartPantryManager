package com.fahimkhan.smartpantry.activities;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
    private PantryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantry_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            // Pad for system bars and the camera cutout (it sits at the side in landscape)
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.displayCutout());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initRecyclerView();
        initAddButton();
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

    /** One-time setup: connects the RecyclerView to its LayoutManager and Adapter. */
    private void initRecyclerView() {
        recyclerPantry = findViewById(R.id.recyclerPantry);
        textEmptyPantry = findViewById(R.id.textEmptyPantry);

        recyclerPantry.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PantryAdapter(new ArrayList<>(), this);
        recyclerPantry.setAdapter(adapter);
    }

    private void initAddButton() {
        FloatingActionButton fabAddItem = findViewById(R.id.fabAddItem);
        // No extras, so the form opens in Add mode
        fabAddItem.setOnClickListener(v ->
                startActivity(new Intent(this, IngredientFormActivity.class)));
    }

    /** Reads all pantry items from the database and shows the list or the empty message. */
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

        adapter.setItems(items);

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
        Toast.makeText(this, "Delete " + item.getName() + " (Step 3D)", Toast.LENGTH_SHORT).show();
    }
}
