package es.uca.tfg_ismaelsantos.asyncTasks;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Okhttp
 * Created by Ismael Santos Cabaña on 24/11/15.
 */
public class OkHttp {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    /**
     * Método POST de Okhttp, recibe la url (String url)y el body del POST (String json).
     * @param url
     * @param json
     * @return
     * @throws IOException
     */
    public String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

}
