package com.edusoho.manager.http;

import java.util.HashMap;
import java.util.Map;

public class HttpUtils {

    private static final String              BASE_URL    = "http://es3.cloud-test.edusoho.cn/api/";
    private              Map<String, String> mHeaderMaps = new HashMap<>();

    public static HttpUtils newInstance() {
        HttpUtils mInstance = new HttpUtils();
        mInstance.mHeaderMaps.clear();
        return mInstance;
    }

    public <T> T createApi(final Class<T> clazz) {
        return RetrofitClient.getInstance(BASE_URL, mHeaderMaps).create(clazz);
    }
}
