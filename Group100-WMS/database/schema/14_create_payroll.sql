USE group100_wms;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payroll` (
  `payroll_id` int NOT NULL AUTO_INCREMENT,
  `employee_id` int NOT NULL,
  `month` tinyint NOT NULL,
  `year` smallint NOT NULL,
  `base_salary` decimal(10,2) NOT NULL,
  `overtime` decimal(10,2) NOT NULL DEFAULT '0.00',
  `deductions` decimal(10,2) NOT NULL DEFAULT '0.00',
  `epf_employer` decimal(10,2) NOT NULL DEFAULT '0.00',
  `etf` decimal(10,2) NOT NULL DEFAULT '0.00',
  `net_salary` decimal(10,2) NOT NULL,
  `generated_by` int DEFAULT NULL,
  `generated_date` date NOT NULL DEFAULT (curdate()),
  PRIMARY KEY (`payroll_id`),
  UNIQUE KEY `uq_payroll_emp_month` (`employee_id`,`month`,`year`),
  KEY `fk_payroll_user` (`generated_by`),
  KEY `idx_payroll_emp` (`employee_id`,`year`,`month`),
  CONSTRAINT `fk_payroll_emp` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_payroll_user` FOREIGN KEY (`generated_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1477 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
