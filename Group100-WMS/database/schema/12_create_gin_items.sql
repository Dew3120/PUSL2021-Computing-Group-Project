USE group100_wms;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gin_items` (
  `gin_item_id` int NOT NULL AUTO_INCREMENT,
  `gin_id` int NOT NULL,
  `item_id` int NOT NULL,
  `batch_id` int NOT NULL,
  `quantity` int NOT NULL,
  PRIMARY KEY (`gin_item_id`),
  KEY `fk_ginitem_gin` (`gin_id`),
  KEY `fk_ginitem_batch` (`batch_id`),
  KEY `idx_ginitem_item` (`item_id`,`gin_id`),
  CONSTRAINT `fk_ginitem_batch` FOREIGN KEY (`batch_id`) REFERENCES `batches` (`batch_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_ginitem_gin` FOREIGN KEY (`gin_id`) REFERENCES `goods_issue_notes` (`gin_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_ginitem_item` FOREIGN KEY (`item_id`) REFERENCES `items` (`item_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=159 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
