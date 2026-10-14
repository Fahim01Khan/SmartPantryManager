package com.fahimkhan.smartpantry.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fahimkhan.smartpantry.R;
import com.fahimkhan.smartpantry.adapters.NoFilterArrayAdapter;
import com.fahimkhan.smartpantry.utils.NavigationHelper;
import com.fahimkhan.smartpantry.utils.SettingsManager;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Lets the user change expiry alerts and the default unit.
 * Each setting is saved to SharedPreferences the moment it changes.
 */
public class SettingsActivity extends AppCompatActivity {

    private MaterialSwitch switchExpiryAlerts;
    private Slider sliderExpiryDays;
    private TextView textExpiryDaysLabel;
    private MaterialAutoCompleteTextView autoCompleteDefaultUnit;
    private List<String> unitOptions;
    private String noneOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        // Root: top inset only. Content: side insets. Nav bar: full width; pads its own bottom.
        View content = findViewById(R.id.settingsContent);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            v.setPadding(0, bars.top, 0, 0);
            content.setPadding(bars.left, 0, bars.right, 0);
            return insets;
        });

        initViews();
        initUnitDropdown();
        loadSettings();
        initListeners();   // After loading, so displaying saved values doesn't trigger saves
        NavigationHelper.setUp(this, findViewById(R.id.bottomNav), R.id.nav_settings);
    }

    private void initViews() {
        switchExpiryAlerts = findViewById(R.id.switchExpiryAlerts);
        sliderExpiryDays = findViewById(R.id.sliderExpiryDays);
        textExpiryDaysLabel = findViewById(R.id.textExpiryDaysLabel);
        autoCompleteDefaultUnit = findViewById(R.id.autoCompleteDefaultUnit);
    }

    /** Options are "None" followed by every supported unit. */
    private void initUnitDropdown() {
        noneOption = getString(R.string.default_unit_none);
        unitOptions = new ArrayList<>();
        unitOptions.add(noneOption);
        unitOptions.addAll(Arrays.asList(getResources().getStringArray(R.array.units)));
        autoCompleteDefaultUnit.setAdapter(new NoFilterArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, unitOptions));
    }

    /** Shows the saved values. */
    private void loadSettings() {
        boolean alertsEnabled = SettingsManager.isExpiryAlertEnabled(this);
        int warningDays = SettingsManager.getExpiryWarningDays(this);

        switchExpiryAlerts.setChecked(alertsEnabled);
        sliderExpiryDays.setValue(warningDays);
        sliderExpiryDays.setEnabled(alertsEnabled);   // The window only matters while alerts are on
        updateDaysLabel(warningDays);

        String defaultUnit = SettingsManager.getDefaultUnit(this);
        autoCompleteDefaultUnit.setText(defaultUnit.isEmpty() ? noneOption : defaultUnit, false);
    }

    /** Saves each setting as soon as the user changes it. */
    private void initListeners() {
        switchExpiryAlerts.setOnCheckedChangeListener((button, isChecked) -> {
            SettingsManager.setExpiryAlertEnabled(this, isChecked);
            sliderExpiryDays.setEnabled(isChecked);
        });

        sliderExpiryDays.addOnChangeListener((slider, value, fromUser) -> {
            int days = Math.round(value);
            updateDaysLabel(days);
            if (fromUser) {
                SettingsManager.setExpiryWarningDays(this, days);
            }
        });

        autoCompleteDefaultUnit.setOnItemClickListener((parent, view, position, id) -> {
            String chosen = unitOptions.get(position);
            SettingsManager.setDefaultUnit(this, chosen.equals(noneOption) ? "" : chosen);
        });
    }

    private void updateDaysLabel(int days) {
        textExpiryDaysLabel.setText(
                getResources().getQuantityString(R.plurals.expiry_days_label, days, days));
    }

    /** Closes the dropdown so it isn't leaked if the screen is destroyed (e.g. rotation). */
    @Override
    protected void onDestroy() {
        if (autoCompleteDefaultUnit != null) {
            autoCompleteDefaultUnit.dismissDropDown();
        }
        super.onDestroy();
    }
}