package com.miikaah.kitarakauppaclient.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
 







import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 







import android.util.Log;
 
public class JSONParser {
 
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    
    private final static String TAG = "JSONParser";
    
    // constructor
    public JSONParser() {
 
    }
 
    /**
     * Makes HTTP request to server.
     * @param url target URL
     * @param method GET or POST
     * @param params Basic name value pairs
     * @param pids Product ids for POST
     * @return HTTP Response as JSONObject 
     */
    public JSONObject makeHttpRequest(String url, String method,
            List<NameValuePair> params, JSONArray jArray) {
 
        // Making HTTP request
        try {
 
            // check for request method
            if (method == "POST"){
                // request method is POST
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                HashMap<String, Object> valuePairs = new HashMap<String, Object>();
                
                for (NameValuePair nvp : params) {                	
                	valuePairs.put(nvp.getName(), nvp.getValue());
				}
                
                JSONObject jObject = new JSONObject(valuePairs);
                try {
    				jObject.put("pids", jArray);
    			} catch (JSONException e1) {
    				Log.e(TAG, e1.getMessage());;
    			}
                
                AbstractHttpEntity entity = new ByteArrayEntity(jObject.toString().getBytes("UTF8"));
                entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                httpPost.setEntity(entity);
                Log.d(TAG, "json: " + jObject.toString());
                
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();                
                is = httpEntity.getContent();
 
            } else if (method == "GET"){
                // request method is GET
                DefaultHttpClient httpClient = new DefaultHttpClient();
                String paramString = URLEncodedUtils.format(params, "UTF-8");
                url += "?" + paramString;
                Log.d(TAG, "url: " + url);
                HttpGet httpGet = new HttpGet(url);
 
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            }          
 
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
 
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            Log.d(TAG, "response: " + sb.toString());
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
 
        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data: " + e.toString());
        }
 
        // return JSON String
        return jObj;
 
    }
}
