package com.xinling.app.domain.model;

/**
 * 订单列表查询参数
 */
public class OrderListQuery {

    private String status = "all";
    private int page = 1;
    private int size = 10;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
}
