package com.omv.mongo.util;

import java.util.List;

/**
 * Created by zwj on 2018/4/1.
 */
public class Page {
    private Integer page; // 当前页
    private Integer totalPages; // 总页数
    private Integer pageSize;// 每页10条数据
    private Long totalRows; // 总数据数
    private List content; // query condition

    public Page(Integer page, Integer pageSize, Long totalRows,Integer totalPages, List content) {
        this.page = page;
        this.pageSize = pageSize;
        this.totalRows = totalRows;
        this.content = content;
        this.totalPages = totalPages;
    }


    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(Long totalRows) {
        this.totalRows = totalRows;
    }

    public List getContent() {
        return content;
    }

    public void setContent(List content) {
        this.content = content;
    }
}
