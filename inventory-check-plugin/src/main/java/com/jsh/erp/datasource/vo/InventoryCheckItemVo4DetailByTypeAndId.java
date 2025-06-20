package com.jsh.erp.datasource.vo;

import com.jsh.erp.datasource.entities.InventoryCheckItem;

public class InventoryCheckItemVo4DetailByTypeAndId extends InventoryCheckItem {

    private String materialName;

    private String materialModel;

    private String materialStandard;

    private String materialColor;

    private String materialMfrs;

    private String materialOtherField1;

    private String materialOtherField2;

    private String materialOtherField3;

    private String materialUnit;

    private String depotName;

    private String anotherDepotName;

    private String barCode;

    // Getter和Setter方法
    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName == null ? null : materialName.trim();
    }

    public String getMaterialModel() {
        return materialModel;
    }

    public void setMaterialModel(String materialModel) {
        this.materialModel = materialModel == null ? null : materialModel.trim();
    }

    public String getMaterialStandard() {
        return materialStandard;
    }

    public void setMaterialStandard(String materialStandard) {
        this.materialStandard = materialStandard == null ? null : materialStandard.trim();
    }

    public String getMaterialColor() {
        return materialColor;
    }

    public void setMaterialColor(String materialColor) {
        this.materialColor = materialColor == null ? null : materialColor.trim();
    }

    public String getMaterialMfrs() {
        return materialMfrs;
    }

    public void setMaterialMfrs(String materialMfrs) {
        this.materialMfrs = materialMfrs == null ? null : materialMfrs.trim();
    }

    public String getMaterialOtherField1() {
        return materialOtherField1;
    }

    public void setMaterialOtherField1(String materialOtherField1) {
        this.materialOtherField1 = materialOtherField1 == null ? null : materialOtherField1.trim();
    }

    public String getMaterialOtherField2() {
        return materialOtherField2;
    }

    public void setMaterialOtherField2(String materialOtherField2) {
        this.materialOtherField2 = materialOtherField2 == null ? null : materialOtherField2.trim();
    }

    public String getMaterialOtherField3() {
        return materialOtherField3;
    }

    public void setMaterialOtherField3(String materialOtherField3) {
        this.materialOtherField3 = materialOtherField3 == null ? null : materialOtherField3.trim();
    }

    public String getDepotName() {
        return depotName;
    }

    public void setDepotName(String depotName) {
        this.depotName = depotName == null ? null : depotName.trim();
    }

    public String getAnotherDepotName() {
        return anotherDepotName;
    }

    public void setAnotherDepotName(String anotherDepotName) {
        this.anotherDepotName = anotherDepotName == null ? null : anotherDepotName.trim();
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode == null ? null : barCode.trim();
    }
}