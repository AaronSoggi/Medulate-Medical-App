package com.example.mobile_interactions_aaron_soggi;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonParserr {

    private HashMap<String,String> parseJsonObject(JSONObject object) {
        //initialize hash map
        HashMap<String,String> dataList = new HashMap<>();

        try {
            //get name from object
            String name = object.getString("name");
            //get latitude from object
            String latitude = object.getJSONObject("geometry")
                    .getJSONObject("location").getString("lat");
            // get longitude from object
            String longitude = object.getJSONObject("geometry")
                    .getJSONObject("location").getString("lng");
            // put all value in hash map
            dataList.put("name", name);
            dataList.put("lat", latitude);
            dataList.put("lng", longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //return has map
        return dataList;
    }
    private List<HashMap<String, String>> parseJsonArray(JSONArray jsonArray){

        List<HashMap<String, String>> dataList = new ArrayList<>();
        for(int i= 0; i<jsonArray.length(); i++){
            try {
                //initialize hash map list
                HashMap<String,String> data = parseJsonObject((JSONObject) jsonArray.get(i));
                //Add data in Hash map list
                dataList.add(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // Return hash map list
        return dataList;
    }
    public List<HashMap<String,String>> parseResult(JSONObject object){
        // initialize json array
        JSONArray jsonArray = null;
        //get result array
        try{
            jsonArray = object.getJSONArray("results");
        } catch(JSONException e)
        {
            e.printStackTrace();
        }
        // return array
        return parseJsonArray(jsonArray);
    }
}

