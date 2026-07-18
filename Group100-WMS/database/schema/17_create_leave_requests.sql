USE group100_wms;

CREATE TABLE IF NOT EXISTS leave_requests (
    request_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL,
    request_date DATE NOT NULL,
    leave_type VARCHAR(20) NOT NULL DEFAULT 'HALF_DAY',
    reason VARCHAR(255),
    status VARCHAR(15) NOT NULL DEFAULT 'PENDING',
    created_by INT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    reviewed_at DATETIME NULL,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id),
    FOREIGN KEY (created_by) REFERENCES users(user_id)
) ENGINE=InnoDB;
