package com.onemsg.protobuf.manager.application;

import java.util.List;
import java.util.NoSuchElementException;

import com.onemsg.protobuf.manager.model.Totalable;

public class ApplicationDetailService {
    
    private ApplicationDetailRepository repository = new ApplicationDetailRepository();

    public void create(String group, String name, String intro, String creator) {
        ApplicationDetail app = ApplicationDetail.create(group, name, intro, creator);
        repository.save(app);
    }

    public void updateIntro(long id, String intro) throws NoSuchElementException{
        ApplicationDetail app = repository.find(id).orElseThrow();
        app.intro = intro;
        repository.update(app);
    }

    public void delete(long id) {
        repository.remove(id);
    }

    public ApplicationDetail get(long id) throws NoSuchElementException {
        return repository.find(id).orElseThrow();
    }

    public Totalable<ApplicationDetail> get(int pageIndex, int pageSize) {
        int skip = (pageIndex - 1 ) * pageSize;
        List<ApplicationDetail> list = repository.find(skip, pageSize);
        return Totalable.of(list, repository.count());
    }

}
