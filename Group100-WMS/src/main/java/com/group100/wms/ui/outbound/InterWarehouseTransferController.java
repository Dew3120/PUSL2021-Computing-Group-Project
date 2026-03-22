Batch batch = new Batch();
            batch.setItemId(item.getId());
            batch.setPoId(0);
            batch.setQuantity(qty);
            batch.setAvailableQty(qty);
            batch.setUnitCost(0.0);
            batch.setReceiptDate(LocalDate.now());
            batchRepository.save(batch);

            statusLabel.setText("Transferred " + qty + " units of "
                    + item.getName() + " successfully.");
            quantityField.clear();
        } catch (StockShortageException e) {
            statusLabel.setText("Stock shortage: available=" + e.getAvailable());
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid quantity.");
        } catch (DatabaseException e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    // Clears all input fields and resets the form
    @FXML
    private void handleClear() {
        fromWarehouseCombo.setValue(null);
        toWarehouseCombo.setValue(null);
        itemCombo.setValue(null);
        quantityField.clear();
        statusLabel.setText("");
    }
}
