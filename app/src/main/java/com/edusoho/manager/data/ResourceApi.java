package com.edusoho.manager.data;

import com.edusoho.manager.entity.Resource;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface ResourceApi {

    @POST("resources/{no}/update")
    @FormUrlEncoded
    Observable<Resource> update(@Path("no") String no, @Field("name") String name);

    @POST("resources/{no}/delete")
    Observable<String> delete(@Path("no") String no);

    @GET("resources/{no}")
    Observable<Resource> getResource(@Path("no") String no);

    @GET("resources")
    Observable<List<Resource>> getResources(@Query("type") String type,
                                            @Query("processStatus") String processStatus,
                                            @Query("start") int start,
                                            @Query("limit") int limit);
}
