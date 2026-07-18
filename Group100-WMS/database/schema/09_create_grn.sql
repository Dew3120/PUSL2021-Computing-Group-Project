USE group100_wms;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `goods_received_notes` (
  `grn_id` int NOT NULL AUTO_INCREMENT,
  `po_id` int NOT NULL,
  `warehouse_id` int NOT NULL,
  `supplier_id` int NOT NULL,
  `receipt_date` date NOT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING',
  `received_by` int DEFAULT NULL,
  PRIMARY KEY (`grn_id`),
  KEY `fk_grn_po` (`po_id`),
  KEY `fk_grn_warehouse` (`warehouse_id`),
  KEY `fk_grn_supplier` (`supplier_id`),
  KEY `fk_grn_user` (`received_by`),
  CONSTRAINT `fk_grn_po` FOREIGN KEY (`po_id`) REFERENCES `purchase_orders` (`po_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_grn_supplier` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`supplier_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_grn_user` FOREIGN KEY (`received_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_grn_warehouse` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouses` (`warehouse_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
