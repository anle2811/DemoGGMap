package com.example.anle.demoggmap;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class MyLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleMap map;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION; //Quyền truy cập vị trí chính xác
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION; //Quyền truy cập vị trí gần đúng
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234; //Code để kiểm tra quyền được cấp trong phương thức onRequestPermissionsResult()
    private boolean LocationPemissionsGranted = false; //Dùng để kiểm tra quyền vị trí đã được cấp chưa
    private static final float DEFAULT_ZOOM=16f;
    private EditText edt_search;
    private ImageButton ibtn_mylocation;
    private double latitude,longitude;
    private Object transferData[];
    private GetNearbyPlaces getNearbyPlaces;
    private String hospital="hospital",market="market",school="school",restaurant="restaurant";
    private int ProximityRadius=10000;
    private BottomNavigationView bottomNavigation;


    private Dialog popuplistPlace;
    private ListView lv_placehadfound;
    private List<HashMap<String,String>> listFindPlace;
    private List<PlaceInfo> placeInfoList;
    private static ProgressBar progressBar;

    private BottomSheetDialog bottomSheetDialog;
    private ImageView img_Place;
    private TextView tv_NamePlace,tv_AddressPlace;
    private Button btn_SavePlace,btn_goPlace;
    private int positionSelectedPlace;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView iv_opennav;

    private SavePlaceSQLite savePlaceSQLite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mylocation_activity);

        initWidgets();
        getLocationPermission();
        initChooseNearbyPlaces();
    }
    public void initWidgets(){
        edt_search=findViewById(R.id.edt_search);
        ibtn_mylocation=findViewById(R.id.ibtn_mylocation);
        bottomNavigation=findViewById(R.id.nav_itemPlaces);
        popuplistPlace=new Dialog(this);
        progressBar=findViewById(R.id.processBar);
        initNavMenu();
        initBottomSheetPlaceInfo();
    }
    public void initNavMenu(){
        iv_opennav=findViewById(R.id.iv_opennav);
        drawerLayout=findViewById(R.id.draw_layout);
        navigationView=findViewById(R.id.naviDraw_menu);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()){
                    case R.id.nav_home:break;
                    case R.id.nav_placesaved: startActivity(new Intent(MyLocationActivity.this,PlaceSavedActivity.class)); break;
                }
                return true;
            }
        });
        iv_opennav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }
    public void initBottomSheetPlaceInfo(){
        bottomSheetDialog=new BottomSheetDialog(MyLocationActivity.this);
        View bottomDialog=getLayoutInflater().inflate(R.layout.nestedscrollview_infoplace,null);
        bottomSheetDialog.setContentView(bottomDialog);
        img_Place=bottomDialog.findViewById(R.id.img_place);
        tv_NamePlace=bottomDialog.findViewById(R.id.tv_NamePlace);
        tv_AddressPlace=bottomDialog.findViewById(R.id.tv_AddressPlace);
        btn_goPlace=bottomDialog.findViewById(R.id.btn_moveCameraPlace);
        btn_SavePlace=bottomDialog.findViewById(R.id.btn_savePlace);

        eventButtonBottomSheetInfoPlace();
    }
    public void initPopupListPlace(){
        View view=LayoutInflater.from(MyLocationActivity.this).inflate(R.layout.popup_placelist,(ViewGroup)findViewById(R.id.relLayout_popup),false);
        popuplistPlace.setContentView(view);
        popuplistPlace.setCanceledOnTouchOutside(false);
        Button btn_close=view.findViewById(R.id.btn_dong);
        lv_placehadfound=view.findViewById(R.id.lv_placehadfound);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popuplistPlace.dismiss();
            }
        });
        popuplistPlace.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }


    public void initChooseNearbyPlaces(){

        transferData=new Object[2]; //Tạo một mảng Object kích thước là 2 phần tử

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.item_market: map.clear(); initNearbyPlaces(latitude,longitude,market,"Đang tìm chợ gần bạn...",R.drawable.icon_market);break;

                    case R.id.item_school: map.clear(); initNearbyPlaces(latitude,longitude,school,"Đang tìm trường học gần bạn...",R.drawable.icon_school);break;

                    case R.id.item_hospital: map.clear(); initNearbyPlaces(latitude,longitude,hospital,"Đang tìm bệnh viện gần bạn...",R.drawable.icon_hospital);break;

                    case R.id.item_restaurant: map.clear(); initNearbyPlaces(latitude,longitude,restaurant,"Đang tìm nhà hàng gần bạn...",R.drawable.icon_restaurant);break;

                    default:break;
                }
                return true;
            }
        });

    }
    public void initNearbyPlaces(double latitude,double longitude,String typeofplace,String messageToast,int resIcon){
        getNearbyPlaces=new GetNearbyPlaces(); //Một đối tượng lớp đã được mình định nghĩa
        String url=getUrlNearbyPlace(latitude,longitude,typeofplace); //Lấy chuỗi truy vấn http://... địa điểm gần bạn của google
        transferData[0]=map; //Đối tượng GoogleMap dùng để truyền cho doInBackground của getNearbyPlaces khi execute()
        transferData[1]=url; //Đối tượng chuỗi String url đã được lấy và truyền cho __________________________________
        Log.d("MyLocationActivity","attach for transferData");
        getNearbyPlaces.setIconPlaces(resIcon);
        getNearbyPlaces.execute(transferData); //Thực thi tiến trình để hiển thị địa điểm gần chúng ta
        Toast.makeText(MyLocationActivity.this,messageToast,Toast.LENGTH_LONG).show();
    }
    public String getUrlNearbyPlace(double latitude,double longitude,String typeofplace){ //Phương thức tạo chuỗi request HTTP URL để tìm các địa điểm gần bạn
        StringBuilder googleUrl= new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleUrl.append("location="+latitude+","+longitude);
        googleUrl.append("&radius="+ProximityRadius);
        googleUrl.append("&type="+typeofplace);
        googleUrl.append("&sensor=true");
        googleUrl.append("&key="+getString(R.string.mapKey));
        Log.d("MyLocationActivity","URL: "+googleUrl.toString());
        return googleUrl.toString();
    }
    public String getUrlFindPlace(String input){
        StringBuilder httpRequest=new StringBuilder("https://maps.googleapis.com/maps/api/place/findplacefromtext/json?");
        httpRequest.append("input="+input);
        httpRequest.append("&inputtype=textquery");
        httpRequest.append("&fields=photos,formatted_address,name,rating,opening_hours,geometry");
        httpRequest.append("&language=vi");
        httpRequest.append("&key="+getString(R.string.mapKey));
        Log.d("MyLocationActivity","URLFindPlace: "+httpRequest.toString());
        return httpRequest.toString();
    }
    public void initFindPlace(String input){
        String httpUrl=getUrlFindPlace(input);
        String[] url=new String[1];
        url[0]=httpUrl;
        new GetFindPlace().execute(url);
        Toast.makeText(MyLocationActivity.this,"Đang tìm...",Toast.LENGTH_SHORT).show();
    }
    public void addPlaceInfoToList(){
        placeInfoList=new ArrayList<>();
        for (int k=0;k<listFindPlace.size();k++){
            HashMap<String,String> hashMap=listFindPlace.get(k);
            String name=hashMap.get("name");
            String formatted_address=hashMap.get("formatted_address");
            String photoReference=hashMap.get("photoReference");
            double lat=Double.parseDouble(hashMap.get("lat"));
            double lng=Double.parseDouble(hashMap.get("lng"));

            placeInfoList.add(new PlaceInfo(name,formatted_address,photoReference,lat,lng));
        }
    }
    public void initListViewFindPlace(){
        CustomAdapterFindPlace adapterFindPlace=new CustomAdapterFindPlace(MyLocationActivity.this,R.layout.custom_listplace,placeInfoList);
        lv_placehadfound.setAdapter(adapterFindPlace);
        lv_placehadfound.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                positionSelectedPlace=position;
                Picasso.get().load(getUrlPlacePhoto(placeInfoList.get(position).getPhotoReference(),500))
                            .placeholder(R.drawable.icon_null_image)
                            .into(img_Place);
                tv_NamePlace.setText(placeInfoList.get(position).getName());
                tv_AddressPlace.setText(placeInfoList.get(position).getFormatted_address());
                bottomSheetDialog.show();
            }
        });
    }
    public void eventButtonBottomSheetInfoPlace(){
            btn_goPlace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveCamera(new LatLng(placeInfoList.get(positionSelectedPlace).getLat(),
                            placeInfoList.get(positionSelectedPlace).getLng()),
                            DEFAULT_ZOOM,placeInfoList.get(positionSelectedPlace).getName());
                    bottomSheetDialog.cancel();
                    popuplistPlace.dismiss();
                }
            });
            btn_SavePlace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    savePlaceInSQLite(placeInfoList.get(positionSelectedPlace));
                }
            });
    }
    public String getUrlPlacePhoto(String photoReference,int maxWidth){
        StringBuilder url=new StringBuilder("https://maps.googleapis.com/maps/api/place/photo?");
        url.append("maxwidth="+maxWidth);
        url.append("&photoreference="+photoReference);
        url.append("&key="+getString(R.string.mapKey));
        return url.toString();
    }

    public void initSearch(){ //Khởi tạo chức năng tìm kiếm vị trí
        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() { //Khi nhấp nút done hoặc enter sau khi gõ xong trong edittext
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId==EditorInfo.IME_ACTION_SEARCH
                        ||actionId==EditorInfo.IME_ACTION_DONE
                        ||event.getAction()==KeyEvent.ACTION_DOWN
                        ||event.getAction()==KeyEvent.KEYCODE_ENTER)
                {
                    //timkiem(); //Gọi tới method tìm kiếm vị trí
                    initFindPlace(edt_search.getText().toString());

                }
                return false;
            }
        });

        ibtn_mylocation.setOnClickListener(new View.OnClickListener() { //Nút để di chuyển camera tới vị trí hiện tại của bạn
            @Override
            public void onClick(View v) {
                getDeviceLocation(); //Lấy vị trí hiện tại
            }
        });
    }
    public void timkiem(){
        String searchString=edt_search.getText().toString(); //Lấy chuỗi đã nhập vào để tìm vị trí
        Geocoder geocoder=new Geocoder(MyLocationActivity.this); //Một lớp để xử lý mã hóa địa lý và mã hóa địa lý ngược
        List<Address> list=new ArrayList<>();

        try{
            list=geocoder.getFromLocationName(searchString,10);  //Trả về một mảng Địa chỉ được biết để mô tả vị trí được đặt tên
        }catch (IOException e){
        }
        for (int k=0;k<list.size();k++){
            Address abc=list.get(k);
            Log.d("MyLocationActivity",abc.toString());
        }
        if (list.size()>0){
            Address address=list.get(0); //Lấy địa chỉ đầu tiên trong mảng
            Log.d("MyLocationActivity",address.toString());
            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM,address.getAddressLine(0)); //Di chuyển camera tới vị trí vừa tìm thấy
        }
    }
    public void moveCamera(LatLng latLng, float zoom,String title) {
        latitude=latLng.latitude;
        longitude=latLng.longitude;

        Log.d("MyLocationActivity", "movaCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom)); //Di chuyển tầm nhìn tới địa điểm vị trí hiện tại

        if (!title.equals("Vị Trí Của Tôi")){ //Không gắn Marker cho vị trí của bạn
            MarkerOptions markerOptions=new MarkerOptions(); //Tạo một Marker
            markerOptions.position(latLng); //Với vị trí được truyền vào
            markerOptions.title(title); //Tiêu đề của Marker
            map.addMarker(markerOptions); //Thêm Marker vào bản đồ
        }
    }

    public void getDeviceLocation() { //Phương thức lấy vị trí hiện tại của thiết bị
        Log.d("MyLocationActivity", "getDeviceLocation: getting the devices current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this); //Dùng để tương tác với vị trí
        try {
            Task location = mFusedLocationProviderClient.getLastLocation(); //Lấy vị trí hiện tại được gán cho Task để kiểm tra có lấy thành công hay không
            location.addOnCompleteListener(new OnCompleteListener() {  //Xử lý khi tác vụ hoàn thành
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()&& task.getResult() != null) { //Nếu thành công
                        Log.d("MyLocationActivity", "onComplete: found location!");
                        Location currentLocation = (Location) task.getResult();  //Lấy kết quả sau khi tác vụ thành công, trả về vị trí hiện tại vào đối tượng Location
                        Log.d("MyLocationActivity", "onComplete: movecamera");
                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM,"Vị Trí Của Tôi"); //Mức độ thu phóng càng lớn càng xem được nhiều chi tiết hơn
                        Log.d("MyLocationActivity", "onComplete: finish");
                    } else {
                        Log.d("MyLocationActivity", "onComplete: current location is null");
                    }
                }
            });
        } catch (SecurityException e) {
            Log.e("MyLocationActivity", "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    public void initMap() { //Khỏi tạo bản đồ trên một View
        Log.d("MyLocationActivity", "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        //SupportMapFragment dùng để đặt bản đồ vào ứng dụng, với một thẻ <fragment> có android:name="com.google.android.gms.maps.SupportMapFragment" và id=@id/map
        mapFragment.getMapAsync(this); //Khởi tạo hệ thống bản đồ và View trên mapFragment
    }

    @Override
    public void onMapReady(GoogleMap googleMap) { //GỌi Khi bản đồ được khỏi tạo đã sẳn sàng để sử dụng
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT);
        Log.d("MyLocationActivity", "onMapReady: map is ready");
        map = googleMap;

        if (LocationPemissionsGranted) { //Quyền đã được cấp hết
            getDeviceLocation(); //Lấy vị trí hiện tại của thiết bị
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            map.setMyLocationEnabled(true); //Cho phép người dùng tương tác với vị trí của họ
            initSearch();
        }
    }

    public void getLocationPermission(){ //Kiểm tra các thứ về việc cấp quyền truy cập vị trí
        Log.d("MyLocationActivity","getLocationPermission: getting location permissions");
        String[] permissions={Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}; //Một mảng 2 quyền cho phép truy cập vị trí chính xác và cho phép truy cập vị trí gần đúng
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){ //Kiểm tra đã được cấp phép Fine_Location(truy cập vị trí chính xác) chưa
            //PackageManager.PERMISSION_GRANTED: Đã được cấp phép, PackageManager class để truy xuất các loại thông tin khác nhau về các gói ứng dụng hiện đang được cài đặt trên thiết bị
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),COURSE_LOCATION)==PackageManager.PERMISSION_GRANTED){ //Kiểm tra đã được cấp phép COAR_LOCATION chưa
                LocationPemissionsGranted=true; //Quyền vị trí đã được cấp
                initMap();
            }else {
                ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE); //Yêu cầu cấp quyền cho ứng dụng nếu chưa cấp
            }
        }else {
            ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE); //Yêu cầu cấp quyền cho ứng dụng nếu chưa cấp
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) { //Kết quả từ việc yêu cầu quyền, được gọi mỗi khi requestPermissions() được gọi
        Log.d("MyLocationActivity","onRequestPermissionsResult: called");
        LocationPemissionsGranted=false; //Quyền truy cập chưa được cấp
        switch (requestCode){ //Tương ứng với LOCATION_PERMISSION_REQUEST_CODE được đặt trong requestPermissions() khi yêu cầu quyền
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length>0){ //grantResults: Kết quả tương ứng với các quyền được cấp PERMISSION_GRANTED hoặc PERMISSION_DENIED. Ở đây length>0 là có kết quả trả về trong mảng int[] grantResults
                    for(int k=0;k<grantResults.length;k++){ // Kiểm tra kết quả trả về của việc cấp quyền
                        if(grantResults[k]!=PackageManager.PERMISSION_GRANTED){ // Nếu kết quả trả về của quyền trong mảng tại kết quả grantResults[0] khác với "quyền đã được cấp"
                            LocationPemissionsGranted=false; //Quyền chưa được cấp
                            Log.d("MyLocationActivity","onRequestPermissionsResult: permission failed");
                            return; //Thoát khỏi phương thức
                        }
                    }
                    //Không xảy ra if thì quyền được cấp
                    Log.d("MyLocationActivity","onRequestPermissionsResult: permission granted");
                    LocationPemissionsGranted=true; //Quyền được cấp
                    initMap(); // Bắt đầu khởi tạo bản đồ
                }
            }
        }
    }

    private class GetFindPlace extends AsyncTask<String,String,String> {
        private String url,httpRequestData;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MyLocationActivity.progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            url=strings[0];
            DownloadUrl downloadUrl=new DownloadUrl();
            try{
                httpRequestData=downloadUrl.ReadTheUrl(url);
            }catch (IOException e){
                e.printStackTrace();
            }
            Log.d("MyLocationActivity","Url: "+url);
            Log.d("MyLocationActivity","Data: "+httpRequestData);
            return httpRequestData;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("MyLocationActivity","onPostExecute: "+s);
            listFindPlace=null;
            DataParser dataParser=new DataParser();
            listFindPlace=dataParser.parseFindPlace(s);
            Log.d("MyLocationActivity","ListPlace: "+listFindPlace);
            try{
                initPopupListPlace();
                addPlaceInfoToList();
                initListViewFindPlace();
                MyLocationActivity.progressBar.setVisibility(View.GONE);
                popuplistPlace.show();
            }catch (Exception e){
                Log.d("MyLocationActivity","Error: "+e.getMessage());
                MyLocationActivity.progressBar.setVisibility(View.GONE);
            }

        }

    }

    public void savePlaceInSQLite(PlaceInfo placeInfo){
        savePlaceSQLite=new SavePlaceSQLite(this);
        if (savePlaceSQLite.CheckAlredyExist(placeInfo)){ //Kiểm tra địa điểm bạn lưu đã có trong Database chưa
            Toast.makeText(MyLocationActivity.this,"Địa điểm này đã được lưu trước kia!!",Toast.LENGTH_LONG).show();
        }else {
            savePlaceSQLite.addPlace(placeInfo);
            Toast.makeText(MyLocationActivity.this,"Lưu thành công",Toast.LENGTH_LONG).show();
        }

    }
}
