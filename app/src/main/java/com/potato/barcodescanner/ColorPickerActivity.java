package com.potato.barcodescanner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ColorPickerActivity extends AppCompatActivity {

    ImageView colorPicker;
    TextView displayValues;
    View displayColor;
    Button btnSendColor;
    int r,g,b;

    Bitmap bitmap;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colorpicker);

        colorPicker =findViewById(R.id.colorPicker);
        displayColor =findViewById(R.id.displayColor);
        displayValues =  findViewById(R.id.displayValues);
        btnSendColor = findViewById(R.id.btnSendColor);

        colorPicker.setDrawingCacheEnabled(true);
        colorPicker.buildDrawingCache(true);

        colorPicker.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try{
                    if(event.getAction()==MotionEvent.ACTION_DOWN ||
                            event.getAction()==MotionEvent.ACTION_MOVE){
                        bitmap = colorPicker.getDrawingCache();
                        int pixel = bitmap.getPixel((int)event.getX(), (int)event.getY());
                        r= Color.red(pixel);
                        g = Color.green(pixel);
                        b = Color.blue(pixel);

                        String hex = "#"+ Integer.toHexString(pixel);
                        displayColor.setBackgroundColor(Color.rgb(r,g,b));
                        String s ="RGB: "+r+", "+g+", "+b+" \nHEX: "+ hex;
                        displayValues.setText(s);

                    }
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });

        btnSendColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                String sr =r+"";
                String sg =g+"";
                String sb =b+"";

                intent.putExtra("colorr", sr);
                intent.putExtra("colorg", sg);
                intent.putExtra("colorb", sb);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

    }
}
