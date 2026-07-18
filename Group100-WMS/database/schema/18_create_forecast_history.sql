USE group100_wms;

CREATE TABLE IF NOT EXISTS forecast_history (
    history_id INT PRIMARY KEY AUTO_INCREMENT,
    item_id INT NOT NULL,
    warehouse_id INT NOT NULL,
    forecast_month TINYINT NOT NULL,
    forecast_year SMALLINT NOT NULL,
    predicted_qty DECIMAL(10,2) NOT NULL,
    actual_qty DECIMAL(10,2) NULL,
    accuracy DECIMAL(5,2) NULL,
    result VARCHAR(10) NULL,
    confidence DECIMAL(5,4) NULL,
    method VARCHAR(50) DEFAULT 'ARIMA',
    generated_date DATE NOT NULL,
    FOREIGN KEY (item_id) REFERENCES items(item_id),
    FOREIGN KEY (warehouse_id) REFERENCES warehouses(warehouse_id)
) ENGINE=InnoDB;
