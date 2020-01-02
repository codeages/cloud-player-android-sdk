package com.edusoho.manager.biz;

import com.edusoho.manager.data.ResourceApi;
import com.edusoho.manager.entity.Resource;
import com.edusoho.manager.http.HttpUtils;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ResourceServiceImpl implements ResourceService {

    private HttpUtils httpUtils = HttpUtils.newInstance();

    @Override
    public Observable<Resource> update(String no, String name) {
        return httpUtils.createApi(ResourceApi.class).update(no, name).compose(switch2Main());
    }

    @Override
    public Observable<String> delete(String no) {
        return httpUtils.createApi(ResourceApi.class).delete(no).compose(switch2Main());
    }

    @Override
    public Observable<Resource> getResource(String no) {
        return httpUtils.createApi(ResourceApi.class).getResource(no).compose(switch2Main());
    }

    @Override
    public Observable<List<Resource>> getResources(String type, String processStatus,
                                                   int start, int limit) {
        return httpUtils.createApi(ResourceApi.class).getResources(type,
                processStatus, start, limit).compose(switch2Main());
    }

    <T> Observable.Transformer<T, T> switch2Main() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
