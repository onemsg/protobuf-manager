package com.onemsg.protobuf.manager.application;

import java.util.ArrayList;
import java.util.List;

/**
 * 应用
 */
public class ApplicationDetail {

    public long id;
    public String group;
    public String name;
    public String intro;
    public String creator;
    public List<String> owners;

    private ApplicationDetail() {}

    public static ApplicationDetail create(String group, String name, String intro, String creator) {
        ApplicationDetail app = new ApplicationDetail();
        app.group = group;
        app.name = name;
        app.intro = intro;
        app.creator = creator;
        app.owners = new ArrayList<>(List.of(creator));
        return app;
    }

    public void addOwners(List<Long> owners) {
        owners.stream()
            .filter(o -> !owners.contains(o))
            .forEach(owners::add);
    }

    public void chagneIntro(String intro) {
        this.intro = intro;
    }


}
