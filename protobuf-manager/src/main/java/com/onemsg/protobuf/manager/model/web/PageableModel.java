package com.onemsg.protobuf.manager.model.web;

import java.util.List;

import com.onemsg.protobuf.manager.model.Totalable;

public class PageableModel {
    
    public List<? extends Object> data;
    public int pageIndex;
    public int pageSize;
    public long total;
    public int pages;

    public static PageableModel of(Totalable<? extends Object> totalable, int pageIndex, int pageSize) {
        return of(totalable.list(), pageIndex, pageSize, totalable.total());
    }

    public static PageableModel of(List<? extends Object> data, int pageIndex, int pageSize, long total) {
        var model = new PageableModel();
        model.data = data;
        model.pageIndex = pageIndex;
        model.pageSize = pageSize;
        model.total = total;
        model.pages = total < 1 ? 0 : (int) (total - 1) / pageSize + 1;
        return model;
    }
}
