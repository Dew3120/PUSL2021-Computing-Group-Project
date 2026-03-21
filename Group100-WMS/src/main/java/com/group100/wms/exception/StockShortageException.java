package com.group100.wms.exception;

/**
 * Thrown when a GIN request exceeds available batch stock.
 * Carries itemId, requested quantity, and available quantity.
 */
public class StockShortageException extends Exception {

    private final int itemId;
    private final int requested;
    private final int available;

    public StockShortageException(int itemId, int requested, int available) {
        super("Stock shortage for itemId=" + itemId
                + ": requested=" + requested
                + ", available=" + available);
        this.itemId    = itemId;
        this.requested = requested;
        this.available = available;
    }

    public int getItemId()    { return itemId; }
    public int getRequested() { return requested; }
    public int getAvailable() { return available; }
}