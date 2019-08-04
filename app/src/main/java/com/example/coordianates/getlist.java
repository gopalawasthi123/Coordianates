package com.example.coordianates;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class getlist extends AppCompatActivity {

    MyDBHelper myDBHelper;
    SQLiteDatabase database;
    ListView listView;
    ArrayAdapter myadapter;
    ArrayList<String> mylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getlist);
        mylist = new ArrayList<>();
        listView= findViewById(R.id.listview);
        myadapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,mylist);

        myDBHelper = MyDBHelper.getInstance(this);
        database = myDBHelper.getReadableDatabase();
        Cursor cursor = database.query(MyDBHelper.TABLE_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String task = cursor.getString(cursor.getColumnIndex(MyDBHelper.COORDIANATES));
            String date = cursor.getString(cursor.getColumnIndex(MyDBHelper.TIME_STAMP));
            mylist.add("Location => "+ task + "Timestamp =>"+ date);

        }
        listView.setAdapter(myadapter);
        myadapter.notifyDataSetChanged();
    }
}
