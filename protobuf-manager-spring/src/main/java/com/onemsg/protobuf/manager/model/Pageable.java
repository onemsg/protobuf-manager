package com.onemsg.protobuf.manager.model;

import java.util.List;

import com.onemsg.protobuf.manager.exception.DataModelResponseException;

public class Pageable<T> {
    
    public List<T> data;
    public int pageIndex;
    public int pageSize;
    public int total;

    public static <T> Pageable<T> create(Totalable<T> totolable, int pageIndex, int pageSize) {
        var pageable = new Pageable<T>();
        pageable.data = totolable.data();
        pageable.total = totolable.total();
        pageable.pageIndex = pageIndex;
        pageable.pageSize = pageSize;
        return pageable;
    }

    public int getPages() {
        if (pageSize < 1) return 0;
        return (total + 1) / pageSize;
    }

    public static int toSkip(int pageIndex, int pageSize) {
        if (pageIndex < 0) return 0;
        return pageIndex * pageSize;
    }

    public static void valide(int pageIndex, int pageSize) throws DataModelResponseException {
        if (pageIndex < 0 || pageSize < 0) {
            throw new DataModelResponseException(400, 400, "Request params pageIndex or pageSize error");
        }
    }
}
