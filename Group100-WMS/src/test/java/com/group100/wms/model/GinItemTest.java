package com.group100.wms.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GinItemTest {

    @Test
    void storesFifoBatchAllocation() {
        GinItem item = new GinItem(1, 2, 3, 4, 25, 12.50);

        assertEquals(1, item.getId());
        assertEquals(2, item.getGinId());
        assertEquals(3, item.getItemId());
        assertEquals(4, item.getBatchId());
        assertEquals(25, item.getQuantityIssued());
        assertEquals(12.50, item.getUnitCost(), 0.001);
    }
}
