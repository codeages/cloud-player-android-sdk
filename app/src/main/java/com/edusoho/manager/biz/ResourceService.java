package com.edusoho.manager.biz;

import com.edusoho.manager.entity.Resource;

import java.util.List;

import rx.Observable;

public interface ResourceService {

    Observable<Resource> update(String no, String name);

    Observable<String> delete(String no);

    Observable<Resource> getResource(String no);

    Observable<List<Resource>> getResources(String type,
                                            String processStatus,
                                            int start,
                                            int limit);
}
