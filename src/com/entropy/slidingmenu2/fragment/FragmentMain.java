package com.entropy.slidingmenu2.fragment;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.entropy.slidingmenu2.*;
import com.example.maps.R;

public class FragmentMain extends Fragment {
    TextView textView;
    
    public FragmentMain() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, null);
        
        textView = (TextView) view.findViewById(R.id.fragment_main_textview);
        
        return view;
    }
}
