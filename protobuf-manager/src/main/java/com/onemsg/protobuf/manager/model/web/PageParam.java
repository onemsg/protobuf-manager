package com.onemsg.protobuf.manager.model.web;

public record PageParam(
    int pageIndex,
    int pageSize
) {
    
    public int offset(){
        return (pageIndex - 1 ) * pageSize;
    }

    public int limit(){
        return pageSize;
    }

    public static int pages(int pageSize, long total){
        if (total < 1) return 0;
        return (int) (total - 1) / pageSize + 1;
    }
}
