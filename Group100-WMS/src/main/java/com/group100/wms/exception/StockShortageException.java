package com.group100.wms.exception;

/**
 * Thrown when a GIN request exceeds available batch stock.
 * Carries itemId, requested quantity, and available quantity.
 *
 * OOP Concepts Used:
 * - Inheritance: This class extends the built-in Exception class.
 * - Encapsulation: Fields (itemId, requested, available) are private and accessed via getter methods.
 * - Abstraction: Represents a specific business exception for stock shortage scenarios.
 * - Polymorphism: Method overriding via Exception class and use of multiple methods (getters).
 */
public class StockShortageException extends Exception {

    // Stores the ID of the item that has insufficient stock
    private final int itemId;

    // Stores the quantity requested by the user
    private final int requested;

    // Stores the quantity currently available in stock
    private final int available;

    // Constructor that initializes the exception with item details and generates a custom message
    public StockShortageException(int itemId, int requested, int available) {
        super("Stock shortage for itemId=" + itemId
                + ": requested=" + requested
                + ", available=" + available);
        this.itemId    = itemId;
        this.requested = requested;
        this.available = available;
    }

    // Returns the item ID associated with the stock shortage
    public int getItemId()    { return itemId; }

    // Returns the requested quantity
    public int getRequested() { return requested; }

    // Returns the available stock quantity
    public int getAvailable() { return available; }
}
