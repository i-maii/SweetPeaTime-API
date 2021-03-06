package com.sweetpeatime.sweetpeatime.wrapper;

public class StockWrapper {
    private Integer flowerId;
    private Integer floristId;
    private Integer quantity;

    public StockWrapper(Integer flowerId, Integer floristId, Integer quantity) {
        this.flowerId = flowerId;
        this.floristId = floristId;
        this.quantity = quantity;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
