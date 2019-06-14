package com.example.anle.demoggmap;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadUrl {
    public String ReadTheUrl(String placeUrl) throws IOException { //Phương thức để đọc chuỗi HTTP URL truyền vào để tìm địa điểm gần bạn
        String Data=""; //Một chuỗi để hứng kết quả đọc return
        InputStream inputStream=null; //Tạo một luồng đọc một cái gì ấy
        HttpURLConnection httpURLConnection=null;
        try {
            URL url=new URL(placeUrl); //Tạo một url từ chuỗi placeUrl
            httpURLConnection=(HttpURLConnection)url.openConnection(); //Mở kết nối đến URL
            httpURLConnection.connect(); //Được kết nối

            inputStream=httpURLConnection.getInputStream(); //Mở một luồng đọc dữ liệu từ HTTP URL
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream)); //Đối tượng dùng để đọc luồng dữ liệu được mở
            StringBuffer stringBuffer=new StringBuffer(); //StringBuffer được sử dụng để tạo chuỗi có thể thay đổi bằng những phương thức của lớp StringBuffer

            String line="";
            while ((line=bufferedReader.readLine())!=null){ //Kiểm tra đọc từng dòng trong luồng dữ liệu
                stringBuffer.append(line); //Nối dữ liệu đọc được vào stringBuffer
            }

            Data=stringBuffer.toString(); //Chuỗi Data đã tạo được gán dữ liệu đã đọc được
            bufferedReader.close(); //Đóng luồng đọc
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            inputStream.close();
            httpURLConnection.disconnect();
        }
        return Data; // Trả về chuỗi dữ liệu đã đọc được từ HTTP URL
    }
}
