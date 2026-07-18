USE group100_wms;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `forecasts` (
  `forecast_id` int NOT NULL AUTO_INCREMENT,
  `item_id` int NOT NULL,
  `warehouse_id` int NOT NULL,
  `predicted_qty` decimal(10,2) NOT NULL,
  `confidence` decimal(5,4) DEFAULT NULL,
  `generated_date` date NOT NULL,
  `method` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'ARIMA',
  PRIMARY KEY (`forecast_id`),
  KEY `fk_fc_warehouse` (`warehouse_id`),
  KEY `idx_forecast_item` (`item_id`,`warehouse_id`,`generated_date`),
  CONSTRAINT `fk_fc_item` FOREIGN KEY (`item_id`) REFERENCES `items` (`item_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_fc_warehouse` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouses` (`warehouse_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
