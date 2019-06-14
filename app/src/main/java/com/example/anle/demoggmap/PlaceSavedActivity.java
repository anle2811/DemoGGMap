package com.example.anle.demoggmap;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class PlaceSavedActivity extends AppCompatActivity {
    private PlaceDataAdapter placeDataAdapter; //Là đối tượng của file Adapter .java đã tạo để quản lý View và Đổ dữ liệu
    private RecyclerView recyclerView; //Đây là đối tượng để tương tác với RecyclerView đã tạo trong xml giao diện của Activity
    private SwipeController swipeController;
    private List<PlaceInfo> placeInfoList; //Danh sách các đối tượng của class PlaceInfo, dùng để đổ dữ liệu vào RecyclerView
    private SavePlaceSQLite savePlaceSQLite;
    private static final String TAG=PlaceSavedActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_saved);

        setPlaceDataAdapter();
        setupRecyView();
    }

    public void setPlaceDataAdapter(){
        savePlaceSQLite=new SavePlaceSQLite(this);
        placeInfoList=new ArrayList<>(); //Khởi tạo danh sách đối tượng PlaceInfo
        placeInfoList=savePlaceSQLite.getAllPlace();
        placeDataAdapter=new PlaceDataAdapter(placeInfoList);
    }

    public void setupRecyView(){
        Log.d(TAG,"setupRecyclerView");
        //Khởi tạo một đối tượng của class SwipeController với đối số là class Abstract SwipeControllerAction
        swipeController=new SwipeController(new SwipeControllerAction() {
            //Sử lý sự kiện cho các Button tạo ra ở mỗi Item ở class Abstract SwipeControllerAction
            @Override
            public void onXoaClicked(int position) {
                placeDataAdapter.placeInfoList.remove(position);
                placeDataAdapter.notifyItemRemoved(position);
                placeDataAdapter.notifyItemRangeChanged(position,placeDataAdapter.getItemCount());
            }
            @Override
            public void onSuaClicked(int position) {

            }
        }
        );
        recyclerView=findViewById(R.id.recy_listPlace); //Ánh xạ nó với thẻ RecyclerView đã đặt id trong file giao diện của Activity

        //setLayoutManager thiết lập bố cục Scroll các Item theo chiều ngang hay chiều dọc cho RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        //setAdapter truyền vào đối tượng placeDataAdapter đã khởi tạo ở phương thức setPlaceDataAdapter()
        recyclerView.setAdapter(placeDataAdapter);

        attachHelperSwipe();

        //addItemDecoration cho phép vẽ thêm vào thứ gì đó cho mỗi Item view trong RecyclerView
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            //Phương thức onDraw được gọi để vẽ thêm Button vào cho Item
            //Phương thức này được vẽ trước các Item View nên nó sẽ nằm bên dưới mỗi Item View (Button nằm bên dưới)
            public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                Log.d(TAG,"onDraw is called");
                swipeController.onDraw(c);
            }
        });
    }

    public void attachHelperSwipe(){ //Thêm trình trợ giúp quản lý thao tác kéo mỗi Item vào RecyclerView
        Log.d(TAG,"attachHelperSwipe");
        ItemTouchHelper itemTouchHelper=new ItemTouchHelper(swipeController); //Khởi tạo một đối tượng ItemTouchHelper
        //truyền vào cho nó một đối tượng của class SwipeController mà mình đã tạo
        itemTouchHelper.attachToRecyclerView(recyclerView); //Đính kèm trình trợ giúp vào đối tượng recyclerView
        //mà ta đã ánh xạ
    }
}
