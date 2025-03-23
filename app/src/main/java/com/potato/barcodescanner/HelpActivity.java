package com.potato.barcodescanner;

import android.graphics.Color;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class HelpActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        setupHyperlink();

    }

    private void setupHyperlink(){
        TextView tvInfo = findViewById(R.id.tvInfo);
        tvInfo.setMovementMethod(LinkMovementMethod.getInstance());
        tvInfo.setLinkTextColor(Color.BLUE);
    }

}