package com.fahimkhan.smartpantry.activities;

import android.app.DatePickerDialog;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fahimkhan.smartpantry.R;
import com.fahimkhan.smartpantry.adapters.NoFilterArrayAdapter;
import com.fahimkhan.smartpantry.database.PantryDataSource;
import com.fahimkhan.smartpantry.models.PantryItem;
import com.fahimkhan.smartpantry.utils.FormatUtils;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Form for adding a new pantry item or editing an existing one.
 * Opened with no extras for Add mode, or with EXTRA_ITEM_ID for Edit mode.
 */
public class IngredientFormActivity extends AppCompatActivity {

    /** Intent extra key: the id of the pantry item to edit. */
    public static final String EXTRA_ITEM_ID = "com.fahimkhan.smartpantry.EXTRA_ITEM_ID";

    private static final String TAG = "IngredientFormActivity";
    private static final String KEY_EXPIRY_DATE = "expiry_date";
    private static final int NO_ITEM = -1;
    private static final String NAME_PATTERN = "[\\p{L} '\\-]+";  // Letters, spaces, ' and -

    private TextInputLayout layoutName;
    private TextInputLayout layoutQuantity;
    private TextInputEditText editName;
    private TextInputEditText editQuantity;
    private TextInputLayout layoutUnit;
    private MaterialAutoCompleteTextView autoCompleteUnit;
    private TextView textExpiryDate;
    private Button buttonClearDate;
    private List<String> validUnits;

    private int editingItemId = NO_ITEM;
    private String selectedExpiryDate;   // Stored format yyyy-MM-dd, or null
    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ingredient_form);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            // Pad for system bars, the camera cutout, and the keyboard
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.displayCutout()
                    | WindowInsetsCompat.Type.ime());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        initViews();
        initUnitDropdown();
        initButtons();

        editingItemId = getIntent().getIntExtra(EXTRA_ITEM_ID, NO_ITEM);
        TextView textTitle = findViewById(R.id.textFormTitle);
        textTitle.setText(isEditMode() ? R.string.title_edit_ingredient
                                       : R.string.title_add_ingredient);

        if (savedInstanceState != null) {
            // Recreated (e.g. after rotation): restore the date and keep the user's edits
            selectedExpiryDate = savedInstanceState.getString(KEY_EXPIRY_DATE);
        } else if (isEditMode()) {
            // First creation in Edit mode: fill the form from the database
            loadItemForEditing();
        }
        updateExpiryDisplay();
    }

    /** Saves values Android will not restore automatically before the Activity is destroyed. */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_EXPIRY_DATE, selectedExpiryDate);
    }

    /** Closes any open popups so they are not leaked when the Activity is destroyed. */
    @Override
    protected void onDestroy() {
        if (datePickerDialog != null && datePickerDialog.isShowing()) {
            datePickerDialog.dismiss();
        }
        if (autoCompleteUnit != null) {
            autoCompleteUnit.dismissDropDown();
        }
        super.onDestroy();
    }

    private boolean isEditMode() {
        return editingItemId != NO_ITEM;
    }

    // ---------- Setup ----------

    private void initViews() {
        layoutName = findViewById(R.id.layoutName);
        layoutQuantity = findViewById(R.id.layoutQuantity);
        editName = findViewById(R.id.editName);
        editQuantity = findViewById(R.id.editQuantity);
        layoutUnit = findViewById(R.id.layoutUnit);
        autoCompleteUnit = findViewById(R.id.autoCompleteUnit);
        textExpiryDate = findViewById(R.id.textExpiryDate);
        buttonClearDate = findViewById(R.id.buttonClearDate);
    }

    /** Attaches the unit options to the dropdown using an adapter that never filters. */
    private void initUnitDropdown() {
        validUnits = Arrays.asList(getResources().getStringArray(R.array.units));
        NoFilterArrayAdapter<String> unitAdapter = new NoFilterArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, validUnits);
        autoCompleteUnit.setAdapter(unitAdapter);

        // Clear the unit error as soon as the user picks a unit
        autoCompleteUnit.setOnItemClickListener((parent, view, position, id) ->
                layoutUnit.setError(null));
    }

    private void initButtons() {
        findViewById(R.id.buttonPickDate).setOnClickListener(v -> showDatePicker());
        buttonClearDate.setOnClickListener(v -> {
            selectedExpiryDate = null;
            updateExpiryDisplay();
        });
        findViewById(R.id.buttonCancel).setOnClickListener(v -> finish());
        findViewById(R.id.buttonSave).setOnClickListener(v -> saveItem());
    }

    // ---------- Edit mode ----------

    /** Loads the item being edited and fills every field with its current values. */
    private void loadItemForEditing() {
        PantryDataSource dataSource = new PantryDataSource(this);
        try {
            dataSource.open();
            PantryItem item = dataSource.getPantryItemById(editingItemId);
            if (item == null) {
                Toast.makeText(this, R.string.error_item_not_found, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            editName.setText(item.getName());
            editQuantity.setText(FormatUtils.formatQuantity(item.getQuantity()));
            // false = set the text without triggering the dropdown's filter
            autoCompleteUnit.setText(item.getUnit(), false);
            selectedExpiryDate = item.getExpiryDate();
        } catch (SQLException e) {
            Log.e(TAG, "Failed to load item " + editingItemId, e);
            Toast.makeText(this, R.string.error_item_not_found, Toast.LENGTH_SHORT).show();
            finish();
        } finally {
            dataSource.close();
        }
    }

    // ---------- Expiry date ----------

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        if (selectedExpiryDate != null) {
            // Open the picker on the currently chosen date
            String[] parts = selectedExpiryDate.split("-");
            calendar.set(Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1]) - 1,   // Calendar months start at 0
                    Integer.parseInt(parts[2]));
        }

        datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    // month is zero-based (January = 0), so add 1 for storage
                    selectedExpiryDate = String.format(Locale.US, "%04d-%02d-%02d",
                            year, month + 1, dayOfMonth);
                    updateExpiryDisplay();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        // New expiry dates cannot be in the past
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void updateExpiryDisplay() {
        if (selectedExpiryDate == null) {
            textExpiryDate.setText(R.string.no_expiry);
            buttonClearDate.setVisibility(View.GONE);
        } else {
            textExpiryDate.setText(FormatUtils.formatExpiryDate(selectedExpiryDate));
            buttonClearDate.setVisibility(View.VISIBLE);
        }
    }

    // ---------- Validation and saving ----------

    /**
     * Checks every field and shows all errors at once.
     * @return a PantryItem built from the form, or null if any field is invalid
     */
    private PantryItem readValidatedItem() {
        // Clear errors from the previous attempt first
        layoutName.setError(null);
        layoutQuantity.setError(null);
        layoutUnit.setError(null);

        boolean isValid = true;

        // Name: required, letters only; repeated spaces collapsed to one
        String name = textOf(editName).replaceAll("\\s+", " ");
        if (name.isEmpty()) {
            layoutName.setError(getString(R.string.error_name_required));
            isValid = false;
        } else if (!name.matches(NAME_PATTERN)) {
            layoutName.setError(getString(R.string.error_name_invalid));
            isValid = false;
        }

        // Quantity: required, numeric, greater than zero.
        // Commas are accepted as decimal separators ("0,5"), common in South Africa.
        String quantityText = textOf(editQuantity).replace(',', '.');
        double quantity = 0;
        if (quantityText.isEmpty()) {
            layoutQuantity.setError(getString(R.string.error_quantity_required));
            isValid = false;
        } else {
            try {
                quantity = Double.parseDouble(quantityText);
                if (quantity <= 0) {
                    layoutQuantity.setError(getString(R.string.error_quantity_positive));
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                layoutQuantity.setError(getString(R.string.error_quantity_invalid));
                isValid = false;
            }
        }

        // Unit: must be chosen, and must be one of the allowed units
        String unit = textOf(autoCompleteUnit);
        if (unit.isEmpty() || !validUnits.contains(unit)) {
            layoutUnit.setError(getString(R.string.error_unit_required));
            isValid = false;
        }

        if (!isValid) {
            return null;
        }
        return new PantryItem(editingItemId, name, quantity, unit, selectedExpiryDate);
    }

    /** Validates, checks for duplicates, then inserts or updates the item. */
    private void saveItem() {
        PantryItem item = readValidatedItem();
        if (item == null) {
            return;   // Errors are already showing on the form
        }

        PantryDataSource dataSource = new PantryDataSource(this);
        try {
            dataSource.open();

            // Block duplicates, but allow an item to keep its own name when edited
            PantryItem existing = dataSource.findPantryItemByName(item.getName());
            if (existing != null && existing.getId() != item.getId()) {
                layoutName.setError(getString(R.string.error_name_duplicate, existing.getName()));
                return;
            }

            boolean saved = isEditMode()
                    ? dataSource.updatePantryItem(item)
                    : dataSource.insertPantryItem(item);

            if (saved) {
                int message = isEditMode() ? R.string.item_updated : R.string.item_added;
                Toast.makeText(this, getString(message, item.getName()), Toast.LENGTH_SHORT).show();
                finish();   // Back to the pantry list, whose onResume reloads the data
            } else {
                Toast.makeText(this, R.string.error_saving_item, Toast.LENGTH_LONG).show();
            }
        } catch (SQLException e) {
            Log.e(TAG, "Failed to save pantry item", e);
            Toast.makeText(this, R.string.error_saving_item, Toast.LENGTH_LONG).show();
        } finally {
            dataSource.close();
        }
    }

    /** Returns the trimmed text of a field, never null. */
    private String textOf(EditText field) {
        return field.getText() == null ? "" : field.getText().toString().trim();
    }
}