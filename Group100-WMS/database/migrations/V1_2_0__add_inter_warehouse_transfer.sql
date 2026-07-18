-- ============================================================
--  Group 100 - Centralized Apparel WMS
--  Migration: V1_2_0 - Inter-Warehouse Transfer Tracking
--  Description: Adds an audit-friendly table for stock transfers
--               between warehouse locations.
-- ============================================================

USE group100_wms;

CREATE TABLE IF NOT EXISTS stock_transfers (
    transfer_id INT PRIMARY KEY AUTO_INCREMENT,
    source_warehouse_id INT NOT NULL,
    destination_warehouse_id INT NOT NULL,
    source_item_id INT NOT NULL,
    destination_item_id INT NOT NULL,
    quantity INT NOT NULL,
    transferred_by INT NULL,
    transfer_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED',
    notes VARCHAR(255),
    FOREIGN KEY (source_warehouse_id) REFERENCES warehouses(warehouse_id),
    FOREIGN KEY (destination_warehouse_id) REFERENCES warehouses(warehouse_id),
    FOREIGN KEY (source_item_id) REFERENCES items(item_id),
    FOREIGN KEY (destination_item_id) REFERENCES items(item_id),
    FOREIGN KEY (transferred_by) REFERENCES users(user_id),
    INDEX idx_transfer_date (transfer_date),
    INDEX idx_transfer_source (source_warehouse_id),
    INDEX idx_transfer_destination (destination_warehouse_id)
) ENGINE=InnoDB;
