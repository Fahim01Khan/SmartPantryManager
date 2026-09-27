package com.fahimkhan.smartpantry.database;

import static com.fahimkhan.smartpantry.database.PantryDBHelper.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.fahimkhan.smartpantry.models.PantryItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides all Create, Read, Update and Delete operations for pantry items.
 * Activities call these methods instead of writing SQL directly.
 */
public class PantryDataSource {

    private static final String TAG = "PantryDataSource";

    private final PantryDBHelper dbHelper;
    private SQLiteDatabase database;

    public PantryDataSource(Context context) {
        dbHelper = new PantryDBHelper(context);
    }

    /** Opens the database for reading and writing. Call before any other method. */
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    /** Closes the database. Call when finished, ideally in a finally block. */
    public void close() {
        dbHelper.close();
    }

    // CREATE

    /**
     * Inserts a new pantry item. On success, the item's id is updated to
     * the id the database assigned.
     * @return true if the row was inserted
     */
    public boolean insertPantryItem(PantryItem item) {
        long newId = database.insert(TABLE_PANTRY, null, toContentValues(item));
        if (newId == -1) {
            return false;   // insert() returns -1 when it fails, e.g. a CHECK constraint
        }
        item.setId((int) newId);
        return true;
    }

    // READ

    /** Returns every pantry item, sorted alphabetically (ignoring capitals). */
    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> items = new ArrayList<>();
        try (Cursor cursor = database.query(TABLE_PANTRY, null, null, null,
                null, null, COL_PANTRY_NAME + " COLLATE NOCASE ASC")) {
            while (cursor.moveToNext()) {
                items.add(cursorToPantryItem(cursor));
            }
        }
        return items;
    }

    /** Returns the pantry item with the given id, or null if it does not exist. */
    public PantryItem getPantryItemById(int id) {
        try (Cursor cursor = database.query(TABLE_PANTRY, null,
                COL_PANTRY_ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null)) {
            if (cursor.moveToFirst()) {
                return cursorToPantryItem(cursor);
            }
        }
        return null;
    }

    // UPDATE

    /**
     * Saves changes to an existing pantry item, matched by its id.
     * @return true if exactly one row was updated
     */
    public boolean updatePantryItem(PantryItem item) {
        try {
            int rowsUpdated = database.update(TABLE_PANTRY, toContentValues(item),
                    COL_PANTRY_ID + " = ?", new String[]{String.valueOf(item.getId())});
            return rowsUpdated == 1;
        } catch (SQLException e) {
            // Unlike insert(), update() throws on a constraint violation
            Log.e(TAG, "Failed to update pantry item " + item.getId(), e);
            return false;
        }
    }

    // DELETE

    /**
     * Deletes the pantry item with the given id.
     * @return true if exactly one row was deleted
     */
    public boolean deletePantryItem(int id) {
        int rowsDeleted = database.delete(TABLE_PANTRY,
                COL_PANTRY_ID + " = ?", new String[]{String.valueOf(id)});
        return rowsDeleted == 1;
    }

    // Helpers

    /** Packs a PantryItem into column/value pairs. The id is left out; SQLite assigns it. */
    private ContentValues toContentValues(PantryItem item) {
        ContentValues values = new ContentValues();
        values.put(COL_PANTRY_NAME, item.getName());
        values.put(COL_PANTRY_QUANTITY, item.getQuantity());
        values.put(COL_PANTRY_UNIT, item.getUnit());
        if (item.hasExpiryDate()) {
            values.put(COL_PANTRY_EXPIRY, item.getExpiryDate());
        } else {
            values.putNull(COL_PANTRY_EXPIRY);
        }
        return values;
    }

    /** Builds a PantryItem from the row the cursor is currently pointing at. */
    private PantryItem cursorToPantryItem(Cursor cursor) {
        int expiryIndex = cursor.getColumnIndexOrThrow(COL_PANTRY_EXPIRY);
        return new PantryItem(
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_PANTRY_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_NAME)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PANTRY_QUANTITY)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_UNIT)),
                cursor.isNull(expiryIndex) ? null : cursor.getString(expiryIndex));
    }
}
