package com.fahimkhan.smartpantry.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Creates and upgrades the Smart Pantry SQLite database.
 * Holds the table and column names as constants so every query
 * in the app refers to them the same way.
 */
public class PantryDBHelper extends SQLiteOpenHelper {

    private static final String TAG = "PantryDBHelper";
    private static final String DATABASE_NAME = "smartpantry.db";
    private static final int DATABASE_VERSION = 1;

    // ----- pantry_items table -----
    public static final String TABLE_PANTRY = "pantry_items";
    public static final String COL_PANTRY_ID = "_id";
    public static final String COL_PANTRY_NAME = "name";
    public static final String COL_PANTRY_QUANTITY = "quantity";
    public static final String COL_PANTRY_UNIT = "unit";
    public static final String COL_PANTRY_EXPIRY = "expiry_date";

    // ----- recipes table -----
    public static final String TABLE_RECIPES = "recipes";
    public static final String COL_RECIPE_ID = "_id";
    public static final String COL_RECIPE_NAME = "name";
    public static final String COL_RECIPE_INSTRUCTIONS = "instructions";

    // ----- recipe_ingredients table -----
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";
    public static final String COL_RI_ID = "_id";
    public static final String COL_RI_RECIPE_ID = "recipe_id";
    public static final String COL_RI_NAME = "name";
    public static final String COL_RI_QUANTITY = "quantity";
    public static final String COL_RI_UNIT = "unit";

    private static final String CREATE_TABLE_PANTRY =
            "CREATE TABLE " + TABLE_PANTRY + " ("
                    + COL_PANTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_PANTRY_NAME + " TEXT NOT NULL, "
                    + COL_PANTRY_QUANTITY + " REAL NOT NULL CHECK (" + COL_PANTRY_QUANTITY + " > 0), "
                    + COL_PANTRY_UNIT + " TEXT NOT NULL, "
                    + COL_PANTRY_EXPIRY + " TEXT)";   // Nullable: expiry is optional

    private static final String CREATE_TABLE_RECIPES =
            "CREATE TABLE " + TABLE_RECIPES + " ("
                    + COL_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_RECIPE_NAME + " TEXT NOT NULL, "
                    + COL_RECIPE_INSTRUCTIONS + " TEXT NOT NULL)";

    private static final String CREATE_TABLE_RECIPE_INGREDIENTS =
            "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " ("
                    + COL_RI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_RI_RECIPE_ID + " INTEGER NOT NULL, "
                    + COL_RI_NAME + " TEXT NOT NULL, "
                    + COL_RI_QUANTITY + " REAL NOT NULL CHECK (" + COL_RI_QUANTITY + " > 0), "
                    + COL_RI_UNIT + " TEXT NOT NULL, "
                    + "FOREIGN KEY (" + COL_RI_RECIPE_ID + ") REFERENCES "
                    + TABLE_RECIPES + "(" + COL_RECIPE_ID + ") ON DELETE CASCADE)";

    public PantryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /** Runs every time the database is opened; SQLite needs foreign keys switched on. */
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    /** Runs once, when the database file is first created. */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PANTRY);
        db.execSQL(CREATE_TABLE_RECIPES);
        db.execSQL(CREATE_TABLE_RECIPE_INGREDIENTS);
    }

    /**
     * Runs when DATABASE_VERSION is increased. During development we simply
     * rebuild the tables; this deletes existing data.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion
                + " to " + newVersion + "; existing data will be lost");
        // Drop the child table first, because it references recipes
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        onCreate(db);
    }
}
