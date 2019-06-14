package com.example.anle.demoggmap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomAdapterFindPlace extends ArrayAdapter<PlaceInfo> {
    private Context context;
    private int resource;
    private List<PlaceInfo> objects;
    public CustomAdapterFindPlace(Context context, int resource, List<PlaceInfo> objects) {
        super(context, resource, objects);
        this.context=context;
        this.resource=resource;
        this.objects=objects;
    }


    @Override
    public View getView(int position,View convertView,ViewGroup parent) {
        convertView=LayoutInflater.from(context).inflate(R.layout.custom_listplace,parent,false);
        PlaceInfo placeInfo=objects.get(position);
        TextView tv_name=convertView.findViewById(R.id.tv_placeName);
        TextView tv_address=convertView.findViewById(R.id.tv_placeAddress);
        tv_name.setText(placeInfo.getName());
        tv_address.setText(placeInfo.getFormatted_address());
        return convertView;
    }
}
