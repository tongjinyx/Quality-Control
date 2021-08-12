package com.example.QualityControlDemo.Service.ServiceImpl;


import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.util.JSONPObject;
import netscape.javascript.JSObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.stereotype.Service;

import javax.swing.text.html.parser.Entity;
import java.io.IOException;
import java.io.UnsupportedEncodingException;


@Service
public class ApiServiceImpl {
    public JSONObject getEntity(String sentence) {
        String urlPath = "http://172.16.0.137/api/nlp/dev/ner";
        CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(urlPath);
        post.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.81 Safari/537.36");
        post.setHeader("Content-Type", "application/json;charset=utf-8");
        post.setHeader("Accept", "application/json;charset=utf-8");

        JSONObject param = new JSONObject();
        param.put("sentence",sentence);
        StringEntity se = new StringEntity(param.toString(),"utf-8");
        post.setEntity(se);
        CloseableHttpResponse response = null;
        String result = null;
        try {
            response = closeableHttpClient.execute(post);
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                HttpEntity entity = response.getEntity();
                if (entity!=null) {
                    result = EntityUtils.toString(entity, "UTF-8");
                    EntityUtils.consume(entity);
                    JSONObject jsonResult = JSONObject.parseObject(result);
                    return jsonResult;

                }
            }
            else {
                System.out.println("connection faild 错误代码:"+response.getStatusLine().getStatusCode());
                return null;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(response != null){
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(closeableHttpClient != null){
                try {
                    closeableHttpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public JSONObject getRelation(String param) {
        String urlPath = "http://172.16.0.137/api/nlp/dev/nre";
        CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(urlPath);
        post.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.81 Safari/537.36");
        post.setHeader("Content-Type", "application/json");
        post.setHeader("Accept", "application/json");

        JSONObject jsonParam = JSONObject.parseObject(param);

        StringEntity se = new StringEntity(jsonParam.toString(), "utf-8");
        post.setEntity(se);
        CloseableHttpResponse response = null;
        String result = null;
        try {
            response = closeableHttpClient.execute(post);
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity!=null) {
                    result = EntityUtils.toString(entity, "UTF-8");
                    EntityUtils.consume(entity);
                    JSONObject jsonResult = JSONObject.parseObject(result);
                    return jsonResult;
                }

            }
            else {
                System.out.println("connection faild 错误代码:"+response.getStatusLine().getStatusCode());
                return null;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(response != null){
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(closeableHttpClient != null){
                try {
                    closeableHttpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
