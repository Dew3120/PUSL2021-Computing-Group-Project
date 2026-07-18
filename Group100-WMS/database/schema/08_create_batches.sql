USE group100_wms;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `batches` (
  `batch_id` int NOT NULL AUTO_INCREMENT,
  `po_id` int NOT NULL,
  `item_id` int NOT NULL,
  `quantity` int NOT NULL,
  `available_qty` int NOT NULL,
  `unit_cost` decimal(10,2) DEFAULT NULL,
  `receipt_date` date NOT NULL,
  PRIMARY KEY (`batch_id`),
  KEY `fk_batch_po` (`po_id`),
  KEY `idx_batches_fifo` (`item_id`,`receipt_date`),
  CONSTRAINT `fk_batch_item` FOREIGN KEY (`item_id`) REFERENCES `items` (`item_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_batch_po` FOREIGN KEY (`po_id`) REFERENCES `purchase_orders` (`po_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `chk_available` CHECK ((`available_qty` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=137 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
