package com.example.anle.demoggmap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class SavePlaceSQLite extends SQLiteOpenHelper {

    private static final String dbName="PlaceInfo";
    private static final int dbVersion=1;

    private static final String tbName="Place";
    private static final String ID="Id";
    private static final String NAME="Name";
    private static final String ADDRESS="Address";
    private static final String LAT="Lat";
    private static final String LNG="Lng";
    private static final String PHOTO="Photo";

    private Context context;
    public SavePlaceSQLite(Context context) {
        super(context, dbName, null, dbVersion);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    public void addPlace(PlaceInfo placeInfo){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(LAT,placeInfo.getLat());
        values.put(LNG,placeInfo.getLng());
        values.put(NAME,placeInfo.getName());
        values.put(ADDRESS,placeInfo.getFormatted_address());
        values.put(PHOTO,placeInfo.getPhotoReference());

        db.insert(tbName,null,values);
        db.close();
    }

    public void createTable(SQLiteDatabase db){
        String sequenceCreateTB=String.format("create table %s (%s real, %s real, %s text, %s text, %s text, primary key (%s , %s))"
                ,tbName,LAT,LNG,NAME,ADDRESS,PHOTO,LAT,LNG);
        db.execSQL(sequenceCreateTB);
    }

    public boolean CheckAlredyExist(PlaceInfo placeInfo){
        SQLiteDatabase db=this.getWritableDatabase();
        String getPlace="select * from "+tbName+" where "+LAT+"="+placeInfo.getLat()+" and "+LNG+"="+placeInfo.getLng()+"";
        Cursor cursor=db.rawQuery(getPlace,null);
        if(cursor.moveToFirst()){
            db.close();
            return true;
        }
        db.close();
        return false;
    }

    public List<PlaceInfo> getAllPlace(){
        SQLiteDatabase db=this.getReadableDatabase();
        List<PlaceInfo> placeInfoList=new ArrayList<>();
        String selectQuery="SELECT * FROM "+tbName;
        Cursor cursor=db.rawQuery(selectQuery,null);
        if (cursor.moveToFirst()){
            do {
                PlaceInfo placeInfo=new PlaceInfo();
                placeInfo.setLat(cursor.getFloat(0));
                placeInfo.setLng(cursor.getFloat(1));
                placeInfo.setName(cursor.getString(2));
                placeInfo.setFormatted_address(cursor.getString(3));
                placeInfo.setPhotoReference(cursor.getString(4));
                placeInfoList.add(placeInfo);
            }while (cursor.moveToNext());
        }
        db.close();
        return placeInfoList;
    }
    @Override
    //Được gọi khi database nâng cấp, như chỉnh sửa cấu trúc các bảng, thêm những thay đổi cho database
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
