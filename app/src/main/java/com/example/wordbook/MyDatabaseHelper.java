package com.example.wordbook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_WORDS="create table words" +
                                            "(word text primary key," +
                                            "mean text not null," +
                                            "ep text," +
                                            "new blob default 0)";
    private Context mContext;
    //context：上下文
    /*构造方法：第一个参数Context
    ，第二个参数数据库名
    ，第三个参数cursor允许我们在查询数据的时候返回一个自定义的光标位置
    ，一般传入的都是null，第四个参数表示目前库的版本号（用于对库进行升级）*/
    public MyDatabaseHelper(Context context,String name,SQLiteDatabase.CursorFactory factory,int version){
        super(context, name, factory, version);
        mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_WORDS);
        Toast.makeText(mContext,"Create succeed",Toast.LENGTH_SHORT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
