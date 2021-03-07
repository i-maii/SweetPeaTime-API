package com.sweetpeatime.sweetpeatime.dto;

public class DeleteStockDTO {
    private Integer flowerId;
    private Integer floristId;
    private Integer remainQuantity;
    private Integer deleteQuantity;
    private String flowerName;

    public DeleteStockDTO(Integer flowerId, Integer floristId, Integer remainQuantity, Integer deleteQuantity, String flowerName) {
        this.flowerId = flowerId;
        this.floristId = floristId;
        this.remainQuantity = remainQuantity;
        this.deleteQuantity = deleteQuantity;
        this.flowerName = flowerName;
    }

    public Integer getFlowerId() {
        return flowerId;
    }

    public void setFlowerId(Integer flowerId) {
        this.flowerId = flowerId;
    }

    public Integer getFloristId() {
        return floristId;
    }

    public void setFloristId(Integer floristId) {
        this.floristId = floristId;
    }

    public Integer getRemainQuantity() {
        return remainQuantity;
    }

    public void setRemainQuantity(Integer remainQuantity) {
        this.remainQuantity = remainQuantity;
    }

    public Integer getDeleteQuantity() {
        return deleteQuantity;
    }

    public void setDeleteQuantity(Integer deleteQuantity) {
        this.deleteQuantity = deleteQuantity;
    }

    public String getFlowerName() {
        return flowerName;
    }

    public void setFlowerName(String flowerName) {
        this.flowerName = flowerName;
    }
}
