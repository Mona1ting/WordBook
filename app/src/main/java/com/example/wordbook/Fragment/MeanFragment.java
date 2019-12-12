package com.example.wordbook.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.wordbook.R;


public class MeanFragment extends Fragment {
    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view=inflater.inflate(R.layout.mean_frag,container,false);
        return view;
    }

    public void refresh(String Word,String Mean,String Ep){
        View visibilityLayout=view.findViewById(R.id.visiblity_layout);
        visibilityLayout.setVisibility(View.VISIBLE);
        TextView WordText=(TextView)view.findViewById(R.id.word);
        TextView MeanText=(TextView)view.findViewById(R.id.mean);
        TextView EpText=(TextView)view.findViewById(R.id.ep);
        WordText.setText(Word);//刷新单词
        MeanText.setText("\n\n释义：\n  "+Mean);//刷新释义
        EpText.setText("\n\n例句：\n  "+Ep);//刷新释义
    }

}