package com.onemsg.authorization.model;

import java.time.LocalDateTime;

public record Role (
    int id,
    String name,
    String description,
    LocalDateTime createTime
) {

    


}
