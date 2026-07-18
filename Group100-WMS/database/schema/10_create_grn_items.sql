USE group100_wms;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `grn_items` (
  `grn_item_id` int NOT NULL AUTO_INCREMENT,
  `grn_id` int NOT NULL,
  `batch_id` int NOT NULL,
  `item_id` int NOT NULL,
  `quantity` int NOT NULL,
  `unit_cost` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`grn_item_id`),
  KEY `fk_grnitem_grn` (`grn_id`),
  KEY `fk_grnitem_batch` (`batch_id`),
  KEY `fk_grnitem_item` (`item_id`),
  CONSTRAINT `fk_grnitem_batch` FOREIGN KEY (`batch_id`) REFERENCES `batches` (`batch_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_grnitem_grn` FOREIGN KEY (`grn_id`) REFERENCES `goods_received_notes` (`grn_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_grnitem_item` FOREIGN KEY (`item_id`) REFERENCES `items` (`item_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=137 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
