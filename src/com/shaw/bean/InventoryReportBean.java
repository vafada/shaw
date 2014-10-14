package com.shaw.bean;

public class InventoryReportBean {
    private ProductBean product;

    private int[] lastSevenMonths;

    public InventoryReportBean(ProductBean product) {
        this.product = product;
        lastSevenMonths = new int[7];
    }

    public void addQuanity(int index, int quantity) {
        lastSevenMonths[index] += quantity;
    }

    public ProductBean getProduct() {
        return product;
    }

    public String getQuantity(int index) {
        int quantity = lastSevenMonths[index];
        return quantity == 0 ? "-" : String.valueOf(quantity);
    }

    public int getTotal() {
        int total = 0;
        for (int i = 1; i < lastSevenMonths.length; i++) {
            total += lastSevenMonths[i];
        }
        return total;
    }

    public int getFrequency() {
        int frequency = 0;
        for (int i = 1; i < lastSevenMonths.length; i++) {
            if (lastSevenMonths[i] > 0) {
                frequency++;
            }
        }
        return frequency;
    }


    public boolean isMAD() {
        int multiplier = 1;
        switch (getFrequency()) {
            case 0:
            case 1:
            case 2:
                multiplier = 1;
                break;
            case 3:
            case 4:
                multiplier = 2;
                break;
            case 5:
            case 6:
                multiplier = 3;
                break;
        }
        int mad = (getTotal() / 6) * multiplier;
        if (mad >= product.getQuantity()) {
            return true;
        } else {
            return false;
        }
    }

}
