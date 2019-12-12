package com.example.wordbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;


//数组适配器
public class WordsAdapter extends ArrayAdapter<Words> {
    int resourceId;

    /*context：（传递Context对象，一般为this）
    resource：该布局资源在res/layout/Xxx.xml中定义后会自动在R.java文件中生成对应的ID
                ，然后利用R.layout.Xxx调用即可
                ，也可以使用系统已定义好的布局文件
     textViewResourceId： 对应的是resource布局文件中TextView组件的ID；
      object：数组或者List集合，用于为多个列表项提供数据
            从中可知：textViewResourceId用于控制每个列表项的TextView组件（控制布局形式显示风格）
            ，而objects（数组或者List集合）为每个列表项的TextView组件提供显示数据。
            数组或者List集合有多少个元素，将会生成多少个列表项。

    */
    public WordsAdapter(@NonNull Context context, int textViewResourceId, @NonNull List<Words> objects) {
        super(context, textViewResourceId, objects);
        resourceId=textViewResourceId;
    }

    /*
    int position：判断当前显示的项目在屏幕上的位置，然后通过position在定义的集合中取值显示在屏幕上
    View convertView：缓存被滚动到界面之外的项目，提高效率
    ViewGroup parent：存放被加载出来的每一个项目视图
    * */
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Words words=getItem(position);
        /*将xml定义的布局文件实例化为view控件对象；
         LayoutInflater.inflate是加载一个布局文件；
         findViewById则是从布局文件中查找一个控件；

          reSource：View的layout的ID
          root：需要附加到resource资源文件的根控件，inflate()会返回一个View对象
                如果第三个参数attachToRoot为true，就将这个root作为根对象返回
                否则仅仅将这个root对象的LayoutParams属性附加到resource对象的根布局对象上，也就是布局文件resource的最外层的View上。
                如果root为null则会忽略view根对象的
          attachToRoot：是否将root附加到布局文件的根视图上。
         */
        View view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        ////////可以考虑弄一个tablelayout
        TextView wordsWord=(TextView)view.findViewById(R.id.words_word);
        TextView wordsMean=(TextView)view.findViewById(R.id.words_mean);
        wordsWord.setText(words.getWord());
        wordsMean.setText(words.getMean());
        return view;
    }
}
