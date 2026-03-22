// Handles adding an item to the GRN item list with validation
    @FXML
    private void handleAddItem() {
        Item item = itemCombo.getValue();
        String qtyText = quantityField.getText().trim();
        String costText = unitCostField.getText().trim();
        if (item == null  qtyText.isBlank()  costText.isBlank()) {
            statusLabel.setText("Fill item, quantity and cost.");
            return;
        }
        try {
            int qty = Integer.parseInt(qtyText);
            double cost = Double.parseDouble(costText);
            GrnItem grnItem = new GrnItem();
            grnItem.setItemId(item.getId());
            grnItem.setQuantity(qty);
            grnItem.setUnitCost(cost);
            grnItems.add(grnItem);
            itemListView.getItems().add(item.getName() + " x" + qty + " @ " + cost);
            quantityField.clear();
            unitCostField.clear();
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid quantity or cost.");
        }
    }

    // Handles saving the GRN and its items to the system
    @FXML
    private void handleSave() {
        PurchaseOrder po = poCombo.getValue();
        if (po == null || grnItems.isEmpty()) {
            statusLabel.setText("Select a PO and add at least one item.");
            return;
        }
        try {
            GoodsReceivedNote grn = new GoodsReceivedNote();
            grn.setPoId(po.getId());
            grn.setWarehouseId(po.getWarehouseId());
            grn.setSupplierId(po.getSupplierId());
            grn.setReceivedBy(SessionManager.getCurrentUser().getId());
            inboundService.receiveGoods(grn, grnItems);
            statusLabel.setText("GRN saved successfully.");
            grnItems.clear();
            itemListView.getItems().clear();
        } catch (DatabaseException e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    // Clears all input fields and resets the form
    @FXML
    private void handleClear() {
        poCombo.setValue(null);
        itemCombo.setValue(null);
        quantityField.clear();
        unitCostField.clear();
        if (notesArea != null) notesArea.clear();
        grnItems.clear();
        itemListView.getItems().clear();
        statusLabel.setText("");
    }
}
