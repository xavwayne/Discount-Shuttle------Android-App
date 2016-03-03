package com.example.jiyushi1.dis.activity.customer;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.jiyushi1.dis.R;

public class AboutUs extends Fragment {
    View view;
    Fragment frag;
    FragmentTransaction fragTran;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_about_us, container, false);

        ImageButton back = (ImageButton) view.findViewById(R.id.btAboutUsBack);
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                frag = new CustomerMainPage();
                fragTran = getFragmentManager().beginTransaction().replace(R.id.container, frag);
                fragTran.commit();
            }
        });

        return view;
    }
}
