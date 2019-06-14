package com.example.anle.demoggmap;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class GetNearbyPlaces extends AsyncTask<Object,String,String> {

    private String googlePlaceData,url;
    private GoogleMap mMap;
    private BitmapDescriptor iconPlaces;
    @Override
    protected String doInBackground(Object... objects) { //Được đối tượng Object transferData[] bên MyLocationActivity truyền vào
        Log.d("MyLocationActivity","StartProcess");                                                //khi một đối tượng của GetNearbyPlaces .execute(transferData)
        mMap=(GoogleMap) objects[0];       //Lấy GoogleMap trong Object transferData[] truyền vào
        url=(String)objects[1];             //Lấy String trong Object transferData[] truyền vào

        DownloadUrl downloadUrl=new DownloadUrl(); //Tạo mới một đối tượng DownloadUrl của lớp đã được mình xây dựng

        try {
            Log.d("MyLocationActivity","ReadTheUrl");
            googlePlaceData=downloadUrl.ReadTheUrl(url); //Gọi phương thức đọc HTTP URL để lấy chuỗi đọc trả về
        } catch (IOException e) {
            Log.d("MyLocationActivity","Fail: "+e.getMessage());
            e.printStackTrace();
        }
        Log.d("MyLocationActivity","execute: return googlePlaceData");
        return googlePlaceData;
    }

    @Override
    protected void onPostExecute(String s) { //Sau khi tiến trình kết thúc thì hàm được gọi
        List<HashMap<String,String>> nearByPlacesList=null; //Tạo một danh sách HashMap với Key và Value kiểu String
        DataParser dataParser=new DataParser(); //Khởi tạo một đối tượng từ lớp DataParser ta đã xây dựng
        nearByPlacesList=dataParser.parse(s); //String s sau khi tiến trình chạy xong sẽ trả về một chuỗi Json đã đọc được từ HTTP URL
        //Gọi phương thức parse truyền s vào để phân tích trả về một List<HashMap>> và truyền cho nearByPlacesList
        DisplayNearbyPlace(nearByPlacesList); // Sau khi sử lý Json và lưu vào HashMap và ListHashMap thì truyền vào cho DisplayNearbyPlaces để hiển thị các Marker địa điểm gần bạn
    }

    public void setIconPlaces(int resourceIcon){
        iconPlaces=BitmapDescriptorFactory.fromResource(resourceIcon);
    }

    private void DisplayNearbyPlace(List<HashMap<String,String>> nearByPlacesList){
        Log.d("MyLocationActivity","displayNearbyPlace");
        for (int k=0;k<nearByPlacesList.size();k++){
            MarkerOptions markerOptions=new MarkerOptions();
            HashMap<String,String> googleNearbyPlace=nearByPlacesList.get(k);
            String nameOfPlace=googleNearbyPlace.get("place_name");
            String vicinity=googleNearbyPlace.get("vicinity");
            double lat=Double.parseDouble(googleNearbyPlace.get("lat"));
            double lng=Double.parseDouble(googleNearbyPlace.get("lng"));

            LatLng latLng=new LatLng(lat,lng);
            markerOptions.position(latLng);
            markerOptions.title(nameOfPlace+" : "+vicinity);
            markerOptions.icon(iconPlaces);
            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
        }
    }
}
