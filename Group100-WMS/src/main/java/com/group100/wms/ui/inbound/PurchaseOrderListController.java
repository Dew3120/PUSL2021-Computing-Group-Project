// Exports the currently displayed table data to a PDF file
    @FXML
    private void handleExportPdf() {
        String[] h = {"PO #", "Supplier", "Warehouse", "Order Date", "Expected", "Status"};
        List<String[]> d = new ArrayList<>();
        for (PoRow r : poTable.getItems()) d.add(new String[]{String.valueOf(r.poId), r.supplierName, r.warehouseName, r.orderDate, r.expectedDate, r.status});
        PdfExporter.export("Purchase Orders Report", h, d, poTable.getScene().getWindow());
    }

    // Exports the currently displayed table data to an Excel file
    @FXML
    private void handleExportExcel() {
        String[] h = {"PO #", "Supplier", "Warehouse", "Order Date", "Expected", "Status"};
        List<String[]> d = new ArrayList<>();
        for (PoRow r : poTable.getItems()) d.add(new String[]{String.valueOf(r.poId), r.supplierName, r.warehouseName, r.orderDate, r.expectedDate, r.status});
        ExcelExporter.export("Purchase Orders", h, d, poTable.getScene().getWindow());
    }

    // Data model class representing a single Purchase Order row
    public static class PoRow {

        // Stores purchase order ID
        public int poId;

        // Stores supplier name
        public String supplierName;

        // Stores warehouse name
        public String warehouseName;

        // Stores order date
        public String orderDate;

        // Stores expected delivery date
        public String expectedDate;

        // Stores current status of the purchase order
        public String status;
    }
}
