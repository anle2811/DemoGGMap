package com.example.anle.demoggmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {

    private HashMap<String,String> getPlace(JSONObject googlePlaceJSON){ //Phương thức lấy place từ JSONObject
        HashMap<String,String> googlePlaceMap =new HashMap<>(); //Tạo một HashMap để trả về gán cho NearbyPlaceMap trong phương thức getAllNearbyPlaces bên dưới
        String NameOfPlace="-NA-";
        String vicinity="-NA-";
        String latidude="";
        String longidude="";
        String reference="";

        try {
            if (!googlePlaceJSON.isNull("name")){ //Nếu tại key name của googlePlaceJSON tồn tại giá trị
                NameOfPlace=googlePlaceJSON.getString("name"); //Giá trị tại key name trong googlePlaceJSON được gán cho NameOfPlace
            }

            if (!googlePlaceJSON.isNull("vicinity")){ //Tương tự như trên
                NameOfPlace=googlePlaceJSON.getString("vicinity");
            }
            latidude=googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lat"); //Trong googlePlaceJSON có một JsonObject là geometry bên trong geometry cũng có một JsonObject là Location và có một Key là Latitude
            longidude=googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lng"); //Tương tự bên trên
            reference=googlePlaceJSON.getString("reference");

            googlePlaceMap.put("place_name",NameOfPlace); //Đặt các key và value vào HashMap
            googlePlaceMap.put("vicinity",vicinity);
            googlePlaceMap.put("lat",latidude);
            googlePlaceMap.put("lng",longidude);
            googlePlaceMap.put("reference",reference);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return googlePlaceMap;
    }

    private HashMap<String,String> getPlaceInfo(JSONObject googlePlaceJSON){
        HashMap<String,String> placeinfo=new HashMap<>();
        String name="-NA-";
        String formatted_address="-NA-";
        String photoReference="";
        String latidude="";
        String longidude="";
        JSONArray photoJsonArr = null;
        JSONObject photoJsonObj=null;
        try {
            photoJsonArr= googlePlaceJSON.getJSONArray("photos");
            photoJsonObj=(JSONObject)photoJsonArr.get(0);
            photoReference=photoJsonObj.getString("photo_reference");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try{
            if (!googlePlaceJSON.isNull("name")){
                name=googlePlaceJSON.getString("name");
            }

            latidude=googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longidude=googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lng");
            formatted_address=googlePlaceJSON.getString("formatted_address");

            placeinfo.put("name",name);
            placeinfo.put("formatted_address",formatted_address);
            placeinfo.put("lat",latidude);
            placeinfo.put("lng",longidude);
            placeinfo.put("photoReference",photoReference);

        }catch (JSONException e){
            e.printStackTrace();
        }
        return placeinfo;
    }


    private List<HashMap<String,String>> getAllNearbyPlaces(JSONArray jsonArray){
        int counter=jsonArray.length(); //Lấy độ dài của đối tượng JsonArray

        List<HashMap<String,String>> NearbyPlacesList=new ArrayList<>();

        HashMap<String,String>  NearbyPlaceMap=null; //Tạo một HashMap để lưu kết quả trả về từ getPlace() và truyền cho List<HashMap>>

        for (int k=0;k<counter;k++){
            try {
                NearbyPlaceMap =getPlace( (JSONObject)jsonArray.get(k) ); //Phương thức getPlace có một tham số là JSONObject ở đây ta truyền vào
                                                                            //một JSONObject được lấy từ JSONArray qua for duyệt JSONArray
                NearbyPlacesList.add(NearbyPlaceMap); //Truyền HashMap<String,String> vào trong List<HashMap>>
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return NearbyPlacesList; //Trả về List<HashMap<>>
    }

    public List<HashMap<String,String>> getAllFindPlace(JSONArray jsonArray){
        int counter=jsonArray.length();
        List<HashMap<String,String>> FindPlaceList=new ArrayList<>();
        HashMap<String,String> FindPlaceMap=null;
        for (int k=0;k<counter;k++){
            try{
                FindPlaceMap=getPlaceInfo((JSONObject)jsonArray.get(k));
                FindPlaceList.add(FindPlaceMap);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return FindPlaceList;
    }
    public List<HashMap<String,String>> parse(String jSONdata){ //Phương thức để phân tích chuỗi Json trả về
        JSONArray jsonArray=null; //Tạo một JSONArray mảng đối tượng JSONObject
        JSONObject jsonObject; //Khai báo một đối tượng JSON

        try {
            jsonObject=new JSONObject(jSONdata); //Bỏ chuỗi JSONdata vào jsonObject để lấy dữ liệu
            jsonArray=jsonObject.getJSONArray("results"); //Trả về giá trị tại key là results và nó là một JsonArray gán cho jsonArray
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getAllNearbyPlaces(jsonArray); //Truyền đối tượng JsonArray đã khởi tạo ở trên cho phương thức getAllNearbyPlaces để phâ
    }

    public List<HashMap<String,String>> parseFindPlace(String jSONdata){
        JSONArray jsonArray=null;
        JSONObject jsonObject;
        try{
            jsonObject=new JSONObject(jSONdata);
            jsonArray=jsonObject.getJSONArray("candidates");
        }catch (JSONException e){
            e.printStackTrace();
        }
        return getAllFindPlace(jsonArray);
    }
}
