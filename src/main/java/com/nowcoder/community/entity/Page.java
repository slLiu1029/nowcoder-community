package com.nowcoder.community.entity;

public class Page {

    // 当前页码
    private int current;
    // 最多显示多少页
    private int limit;
    // 所有数据数（行数）
    private int rows;
    // 此页的链接地址（复用每页的链接）
    private String path;

    public Page() {
        current = 1;
        limit = 10;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current >= 1)
            this.current = current;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit >= 1 && limit <= 100)
            this.limit = limit;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows >= 0)
            this.rows = rows;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "Page{" +
                "current=" + current +
                ", limit=" + limit +
                ", rows=" + rows +
                ", path='" + path + '\'' +
                '}';
    }

    /**
     * 获取当前页的起始行
     * @return 当前页面的起始行行数
     */
    public int getOffset() {
        return (current - 1) * limit;
    }

    /**
     * 获取总页数
     * @return 总页数
     */
    public int getTotal() {
        if (rows % limit == 0) {
            return rows / limit;
        } else {
            return rows / limit + 1;
        }
    }

    /**
     * 获取前端显示的起始页码
     * @return 起始页码
     */
    public int getFrom() {
        return Math.max(1, current - 2);
    }

    /**
     * 获取前端显示的终止页码
     * @return 终止页码
     */
    public int getTo() {
        return Math.min(getTotal(), current + 2);
    }
}
