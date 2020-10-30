package com.dandyhacks.gpstracker;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

interface GetRequestListener{
    String onSuccess(String response);
    String onFailure(String errorMessage);
}

public class HttpUtils {

    OkHttpClient client = new OkHttpClient();

    private GetRequestListener mListener;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final String BASE_URL = "http://18.222.146.208/";

    // setting the listener
    public void registerOnGetRequestEventListener(GetRequestListener mListener)
    {
        this.mListener = mListener;
    }

    String newUserJSON(String uuid){ //uuid is 32 digits long (no hyphens)
        return "{\"uuid\":\""+uuid+"\"}";
    }

    String newLocationJSON(String uuid, String address, String city, String state, String zip_code){
        return "{\"uuid\":\""+uuid+"\","
                + "\"address\":\""+address+"\","
                + "\"city\":\""+city+"\","
                + "\"state\":\""+state+"\","
                + "\"zip_code\":\""+zip_code+"\"}";
    }

    String newSymptomsJSON(String uuid, int score, boolean close_contact){
        return "{\"uuid\":\""+uuid+"\","
                + "\"score\":"+score+","
                + "\"close_contact\":"+close_contact+"}";
    }

    String newAtRiskJSON(String address, String city, String state, String zip_code, int risk_level){
        return "{\"address\":\""+address+"\","
                + "\"city\":\""+city+"\","
                + "\"state\":\""+state+"\","
                + "\"zip_code\":\""+zip_code+"\","
                + "\"risk_level\""+risk_level+"}";
    }

    void getRequest(String url){
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback(){
            @Override
            public void onFailure(Call call, IOException e) {
                System.err.println(e.getMessage());
                if (mListener != null) {
                    mListener.onFailure(e.getMessage());
                }
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                System.out.println(res);
                if (mListener != null) {
                    mListener.onSuccess(res);
                }
            }
        });

    }

    void postRequest(String url, String json){
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(JSON, json))
                .build();

        client.newCall(request).enqueue(new Callback(){
            @Override
            public void onFailure(Call call, IOException e) {
                System.err.println(e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                System.out.println(res);
            }
        });
    }

    void buildGetRequest(String type, String a1, String a2) { //type is 'user','gps','symptoms', or 'at_risk'
        if(type.equals("at_risk")){
            getRequest(BASE_URL+type+"/"+a1+"/"+a2+"/");
        }else if (type.equals("user") || type.equals("gps") || type.equals("symptoms")){
            getRequest(BASE_URL+type+"/"+a1+"/");
        }
    }

    void buildUserPostRequest(String uuid) {
        postRequest(BASE_URL+"user/", newUserJSON(uuid));
    }

    void buildLocationPostRequest(String uuid, String address, String city, String state, String zip_code) {
        postRequest(BASE_URL+"gps/", newLocationJSON(uuid, address, city, state, zip_code));
    }

    void buildSymptomsPostRequest(String uuid, int score, boolean close_contact) {
        postRequest(BASE_URL+"symptoms/", newSymptomsJSON(uuid, score, close_contact));
    }

    void buildAtRiskPostRequest(String address, String city, String state, String zip_code, int risk_level) {
        postRequest(BASE_URL+"at_risk/", newAtRiskJSON(address, city, state, zip_code, risk_level));
    }
}
