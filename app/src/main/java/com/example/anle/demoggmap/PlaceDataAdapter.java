package com.example.anle.demoggmap;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PlaceDataAdapter extends RecyclerView.Adapter<PlaceDataAdapter.PlaceViewHolder> {
    //extends RecyclerView.Adapter<PlaceDataAdapter.PlaceViewHolder> với Generic truyền vào < > là một class thừa kế từ RecyclerView.ViewHolder
    //bên dưới ta có tạo một pulic class PlaceViewHolder extends RecyclerView.ViewHolder với mục đích
    //tránh việc ánh xạ (findViewById) lại nhiều lần
    //Generic là gì có thể lên google để tra: "Generic trong java"
    private static final String APIKEY="AIzaSyDmbn2dGNxNpRz7sMz9SkJa9YUaXj4dsfI";
    public List<PlaceInfo> placeInfoList; //Đây là một List có Generic < > là class PlaceInfo để chứa các đối tượng của class PlaceInfo
    //dùng để đổ dữ liệu hiển thị vào RecyclerView

    //Một class PlaceViewHolder nằm bên trong class PlaceDataAdapter
    public class PlaceViewHolder extends RecyclerView.ViewHolder{ //Lớp quản lý các View tránh việc ánh xạ lại nhiều lần
        //Bên dưới là các View đã tạo trong file item.xml trong res/layout (thành phần của giao diện mỗi item(row) trong RecyclerView
        private ImageView img_recyImagePlace;
        private TextView tv_recyNamePlace,tv_recyAddressPlace,tv_recyLatLng;

        public PlaceViewHolder(View view){ //Constructor của class có đối số là một đối tượng View
            super(view);
            //Bên dưới là ánh xạ các View trong item.xml
            //dùng tham số view để findViewById tới id của các view đã tạo trong item.xml,
            //điều này với mục đích xác định những thứ được ánh xạ thuộc item.xml
            img_recyImagePlace=view.findViewById(R.id.img_recyImagePlace);
            tv_recyNamePlace=view.findViewById(R.id.tv_recyNamePlace);
            tv_recyAddressPlace=view.findViewById(R.id.tv_recyAddressPlace);
            tv_recyLatLng=view.findViewById(R.id.tv_recyLatLng);
        }
    }//Đóng class

    public PlaceDataAdapter(List<PlaceInfo> placeInfoList){ //Constructor của class PlaceDataAdapter có đối số là một
                                                            //List<PlaceInfo> truyền vào cho thuộc tính placeInfoList của class

        this.placeInfoList=placeInfoList; //Thuộc tính placeInfoList (một đối tượng của List<PlaceInfo>) của lớp được truyền vào một List<PlaceInfo>
    }

    @NonNull
    @Override
    //Phương thức callback bên dưới dùng để gán giao diện cho mỗi Item(row) trong RecyclerView
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.placeinfo_item_recyview,viewGroup,false);
                                                                        //R.layout.placeinfo_item_recyview là file item.xml trong res/layout (do ở đây đặt tên khác)
                                                                        //định nghĩa giao diện cho mỗi Item(row)
        return new PlaceViewHolder(itemView); //Trả về một đối tượng của class PlaceViewHolder sử dụng Constructor với các View đã được ánh xạ
    }

    public String getUrlPlacePhoto(String photoReference,int maxWidth){
        StringBuilder url=new StringBuilder("https://maps.googleapis.com/maps/api/place/photo?");
        url.append("maxwidth="+maxWidth);
        url.append("&photoreference="+photoReference);
        url.append("&key="+APIKEY);
        return url.toString();
    }

    //Phương thức callback bên dưới dùng để đổ dữ liệu cần hiển thị lên mỗi Item(row) trong RecyclerView
    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder placeViewHolder, int position) {
        PlaceInfo placeInfo=placeInfoList.get(position); //Một đối tượng của class PlaceInfo dùng để chứa, đổ dữ liệu vào.
                                                        //được truyền vào một đối tượng của List<PlaceInfo> chứa danh sách các đối tượng PlaceInfo
                                                        //và get ra một đối tượng PlaceInfo tại vị trí position

            Picasso.get().load(getUrlPlacePhoto(placeInfo.getPhotoReference(),500))
                    .placeholder(R.drawable.icon_null_image)
                    .into(placeViewHolder.img_recyImagePlace);


        placeViewHolder.tv_recyNamePlace.setText(placeInfo.getName()); //Một đối tượng của PlaceViewHolder gọi đến View đã được khai báo ánh xạ trong class PlaceViewHoler
                                                                        //và setText cho View đó với nội dung là đối tượng placeInfo mới được lấy ra từ câu lệnh phía trên
                                                                        //tại một position(vị trí) trong List<PlaceInfo> và gọi tới phương thức getName() của đối tượng thuộc
                                                                        //class PlaceInfo (dùng để chứa, đổ dữ liệu vào).
        //Hai câu lệnh bên dưới tương tự câu lệnh bên trên
        placeViewHolder.tv_recyAddressPlace.setText(placeInfo.getFormatted_address());
        placeViewHolder.tv_recyLatLng.setText("LatLng: "+String.valueOf(placeInfo.getLat())+", "+String.valueOf(placeInfo.getLng()));
    }

    //Phương thức callback bên dưới trả về số lượng Item(row) ta muốn hiển thị trong RecyclerView
    @Override
    public int getItemCount() { //Trả về số lượng Item(row) chúng ta muốn hiển thị
        return placeInfoList.size();
    }
}
