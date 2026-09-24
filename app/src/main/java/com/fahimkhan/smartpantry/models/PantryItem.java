package com.fahimkhan.smartpantry.models;

/**
 * Represents one ingredient the user currently has in their pantry.
 * An id of -1 means the item has not yet been saved to the database.
 */
public class PantryItem {

    private int id;
    private String name;
    private double quantity;
    private String unit;
    private String expiryDate; // Stored as yyyy-MM-dd; null when not supplied

    /** Creates an empty, unsaved pantry item (used by the Add form). */
    public PantryItem() {
        this.id = -1;
    }

    /** Creates a fully populated pantry item (used when reading from the database). */
    public PantryItem(int id, String name, double quantity, String unit, String expiryDate) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }

    /** Returns true if the user supplied an expiry date for this item. */
    public boolean hasExpiryDate() {
        return expiryDate != null && !expiryDate.isEmpty();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
}
