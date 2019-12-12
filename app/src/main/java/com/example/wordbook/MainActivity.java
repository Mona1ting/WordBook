package com.example.wordbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wordbook.CRUD_activity.AddActivity;
import com.example.wordbook.CRUD_activity.ChangeActivity;
import com.example.wordbook.CRUD_activity.DeleteActivity;
import com.example.wordbook.CRUD_activity.SearchActivity;
import com.example.wordbook.Fragment.MeanFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //创建一个数据库
    private MyDatabaseHelper dbHelper=new MyDatabaseHelper(this,"words.db",null,1);
    private SQLiteDatabase db;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //增加单词
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("word", "robot");
        values.put("mean", "机器人");
        values.put("ep", "I'm a robot.");
        db.insert("words", null, values);
        values.clear();

        values.put("word", "computer");
        values.put("mean", "计算机");
        values.put("ep", "It's a computer.");
        db.insert("words", null, values);
        values.clear();

        db.close();
        //初始化listView
        refrshList();

        //左上角菜单
        ListView listView = (ListView) findViewById(R.id.word_list_view);
        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {//长按listview弹出增删选项
                contextMenu.add(Menu.NONE, 0, 0, "添加到生词本");
                contextMenu.add(Menu.NONE, 1, 0, "修改");
                contextMenu.add(Menu.NONE, 2, 0, "删除");
            }
        });

        //横屏，点击左列单词加载右边的碎片
        if (this.getResources().getConfiguration().orientation!= Configuration.ORIENTATION_PORTRAIT){

            //这个接口定义了当AdapterView中一元素被点击时，一个回调函数被调用。
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                /*AdapterView中一元素被点击时，回调方法被调用。
                  parent:发生点击动作的AdapterView。
　　　　　　　　    view:在AdapterView中被点击的视图(它是由adapter提供的一个视图)。
                  position　视图在adapter中的位置。
                  id:被点击元素的行id。*/
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String[]Word=getWord(parent.getItemAtPosition(position).toString());
                    MeanFragment meanFragment=(MeanFragment)getSupportFragmentManager().findFragmentById(R.id.mean_fragment);
                    meanFragment.refresh(Word[0],Word[1],Word[2]);//刷新右侧布局
                    }
                });
            }

        //查询触发
        EditText search=(EditText)findViewById(R.id.search);
        search.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
             if(hasFocus){
                 Intent intent=new Intent(MainActivity.this, SearchActivity.class);
                 startActivity(intent);
                 EditText et=(EditText)findViewById(R.id.search);
                 et.clearFocus();
             }
             else
             {}
            }
        });
    }

    //上下文菜单监听（添加到生词本，删除，修改）
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo;
        menuInfo =(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        ListView listView=(ListView)findViewById(R.id.word_list_view);
        String word=new String();//获取点击的单词
        //竖屏
        if(this.getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT) {
            //用getChildAt(postion)方法直接获取item的view，
            //获取当前选中的words_word(单词)
            TextView wordText=(TextView)listView.getChildAt(menuInfo.position).findViewById(R.id.words_word);
            word=wordText.getText().toString();
            Log.d("word", wordText.getText().toString());
        }
        //横屏
        else{
            word=listView.getItemAtPosition(menuInfo.position).toString();
        }
        //菜单项的选择
        switch (item.getItemId()){
            case 0:
                //添加单词到生词本
                db=dbHelper.getWritableDatabase();
                db.execSQL("update words set new=? where word=?",new String[]{"1",word});
                db.close();
                refrshList();//实时刷新listView
                Toast.makeText(MainActivity.this, "生词本添加成功："+word, Toast.LENGTH_SHORT).show();
                break;
            case 1:
                //修改单词
                ChangeActivity.actionStart(MainActivity.this,word);//调用ChangeActivity修改
                break;
            case 2:
                //删除单词
                DeleteActivity.actionStart(MainActivity.this,word);//调用DeleteActivity修改
                break;
        }
        return super.onContextItemSelected(item);
    }

    //Menu菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }
    //菜单切换
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.newWords_item://打开生词本

                break;
            case R.id.add_item://添加单词
                Toast.makeText(MainActivity.this, "生词本添加：", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent);

                break;
            case R.id.search_inter_item://联网查找
                Toast.makeText(MainActivity.this, "这是单词本", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }


    //根据数据库更新ListView内容
    public void refrshList(){
        List<Words> wordsList1=new ArrayList<>();
        List<String> wordsList2=new ArrayList<>();
        //将数据库内容读入wordList
        //其中getWritableDatabase() 方法以读写方式打开数据库，一旦数据库的磁盘空间满了，数据库就只能读而不能写，倘若使用的是getWritableDatabase() 方法就会出错。
        db=dbHelper.getWritableDatabase();
        Cursor cursor=db.query("words",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                //遍历Cursor对象，取出数据并打印
                String word=cursor.getString(cursor.getColumnIndex("word"));
                String mean=cursor.getString(cursor.getColumnIndex("mean"));
                if(this.getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){
                    //竖屏操作
                    Words words=new Words(word,mean);
                    wordsList1.add(words);
                }
                else{
                    wordsList2.add(word);
                }
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        //wordlist的内容通过Adapter添加到listview（横竖屏listview适配器不同）
        ListView listView=(ListView)findViewById(R.id.word_list_view);
        if(this.getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){
            //竖屏操作
            WordsAdapter adapter=new WordsAdapter(MainActivity.this,R.layout.words_item,wordsList1);
            listView.setAdapter(adapter);
        }
        else {
            ArrayAdapter<String> adapter=new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,wordsList2);
            listView.setAdapter(adapter);
        }
    }
    //已知word获取Word详情
    public String[] getWord(String word){
        db=dbHelper.getWritableDatabase();//依据word查询数据库
        Cursor cursor=db.rawQuery("select * from words where word=?",new String[]{word});
        cursor.moveToFirst();
        String mean=cursor.getString(cursor.getColumnIndex("mean"));
        String ep=cursor.getString(cursor.getColumnIndex("ep"));
        String _new=cursor.getString(cursor.getColumnIndex("new"));
        cursor.close();
        db.close();
        String[] Word=new String[]{word,mean,ep,_new};
        return Word;
    }
}
