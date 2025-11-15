package com.curr_convert.currency_converter.service.api;

import com.curr_convert.currency_converter.utils.ParameterStringBuilder;
import com.google.gson.Gson;
import lombok.Data;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Data
public  class APIConnector {
    @Value("${currency.api.url}")
    private String url;
    @Value("${currency.api.api_key}")
    private String API_KEY;
    private HttpURLConnection con;

    public boolean establishConnection(String path){
        try {
            URL urlInstance = new URL(url+path);
            this.con = (HttpURLConnection)urlInstance.openConnection();
            con.setRequestMethod("GET");
            return true;
        }catch (MalformedURLException e){
            e.printStackTrace();
        } catch (IOException ignored) {
        }
        return false;
    }

    private void prepareHeaders(HashMap<String,String> parameters){
        parameters.put("access_key", this.API_KEY);
        try {

            con.setRequestProperty("Accept", "application/json");
        }catch (Exception e){
            e.printStackTrace();
        }
        con.setDoOutput(true);
        try(DataOutputStream out = new DataOutputStream(con.getOutputStream())) {
            String params=ParameterStringBuilder.getParamsString(parameters);
            System.out.println(params);
            out.writeBytes(params);

        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private void prepareHeaders(){
        prepareHeaders(new HashMap<>());
    }


    public List<String> getCurrencies() {
        boolean connEstablished=establishConnection("symbols" +
                "?access_key="+this.getAPI_KEY());
        if(!connEstablished)
            return null;
        prepareHeaders();
        try{
            int status= this.con.getResponseCode();
            if(status !=HttpStatus.OK.value()){
                this.con.disconnect();
                return null;
            }
            String jsonResponse= getResponseBody();
            Map response=new Gson().fromJson(jsonResponse,Map.class);
            if(response.get("success").toString().equals("true")) {
                System.out.println((response.get("symbols")));
                response = (Map) response.get("symbols");
                System.out.println(response);
                List<String> currencyList= new ArrayList<>();
                for(Object curr : response.keySet())
                    currencyList.add(curr.toString());
                return currencyList;
            }
            this.con.disconnect();

            return null;
        }
        catch (IOException e){
            e.printStackTrace();
            return null;
        }



    }

    private String getResponseBody() {
        try(BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            return content.toString();
        }catch (IOException ignored){}
        return null;
    }

}
