package com.edusoho.manager.http;

import com.edusoho.cloud.core.entity.ResourceError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.HttpException;
import rx.Subscriber;

public class SubscriberProcessor<T> extends Subscriber<T> {

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        onResponseError(e);
    }

    @Override
    public void onNext(T t) {

    }

    private void onResponseError(Throwable e) {
        if (e instanceof HttpException) {
            ResponseBody body = ((HttpException) e).response().errorBody();
            try {
                String str = body.string();
                JSONObject jsonObject = new JSONObject(str);
                ResourceError playerError = new Gson().fromJson(jsonObject.getString("error"),
                        new TypeToken<ResourceError>() {
                        }.getType());
                onError(playerError);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void onError(ResourceError playerError) {

    }
}
