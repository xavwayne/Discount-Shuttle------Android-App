package com.example.jiyushi1.dis.activity.customer;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.jiyushi1.dis.R;

public class Help extends Fragment {

    View view;


    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_help, container, false);

        Button call = (Button)view.findViewById(R.id.btCall);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("cao");
                Intent callintent = new Intent(Intent.ACTION_CALL);
                callintent.setData(Uri.parse("tel:4124195800"));
                startActivity(callintent);
            }
        });
        return view;
    }
}
