package com.app.util;

public class SpinnerOption {
    private String itemName = "";
    private String itemCode = "";
    /**下拉菜单项的是否选中状态标记值*/
    private boolean selectedState;

    /**下拉菜单项的背景颜色值:例如：#C5C5C5*/
    private String checkColor = "noData";//默认值为noData，用来判断是否赋值过这个字段

    public SpinnerOption() {
        itemName = "";
        itemCode = "";
        selectedState = false;
        checkColor = "noData";
    }

    public SpinnerOption(String itemName, String itemCode) {
        this.itemName = itemName;
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public boolean isSelectedState() {
        return selectedState;
    }

    public void setSelectedState(boolean selectedState) {
        this.selectedState = selectedState;
    }

    public String getCheckColor() {
        return checkColor;
    }

    public void setCheckColor(String checkColor) {
        this.checkColor = checkColor;
    }

    @Override
    public String toString() {
        return itemName;
    }

}
