package com.potato.barcodescanner;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.mazenrashed.printooth.Printooth;
import com.mazenrashed.printooth.data.printable.ImagePrintable;
import com.mazenrashed.printooth.data.printable.Printable;
import com.mazenrashed.printooth.data.printable.RawPrintable;
import com.mazenrashed.printooth.data.printable.TextPrintable;
import com.mazenrashed.printooth.data.printer.DefaultPrinter;
import com.mazenrashed.printooth.ui.ScanningActivity;
import com.mazenrashed.printooth.utilities.Printing;
import com.mazenrashed.printooth.utilities.PrintingCallback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class QRcodeActivity extends AppCompatActivity implements PrintingCallback {

    //initialize variable
    EditText etQRCode,txtSize;
    ImageView ivQRCode;
    Button btnQRcolor;

    int r=0,g=0,b=0;

    OutputStream outputStream;

    Printing printing = null;

    public static final int LAUNCH_COLORPICKER_ACTIVITY = 11;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        //Assign variable
        etQRCode = findViewById(R.id.et_qrcode);
        ivQRCode = findViewById(R.id.iv_qrcode);
        btnQRcolor  = findViewById(R.id.btn_qrcolor);
        txtSize = findViewById(R.id.txtSize);

        final Bitmap[] bitmap = new Bitmap[1];

        btnQRcolor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QRcodeActivity.this, ColorPickerActivity.class);
                startActivityForResult(intent, LAUNCH_COLORPICKER_ACTIVITY);//because want to send back data later
            }
        });

        txtSize.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {

           }

           @Override
           public void afterTextChanged(Editable s) {
               try{
                   int h = Integer.parseInt(txtSize.getText().toString());
                   if(h>0){
                       ivQRCode.getLayoutParams().height =h;
                       ivQRCode.requestLayout();
                   }
               }catch(Exception e){

               }


           }
       });

                etQRCode.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String sText = etQRCode.getText().toString().trim();
                        if (sText.length() > 0) {
                            //Initialize multi format writer
                            MultiFormatWriter writer = new MultiFormatWriter();
                            try {
                                //Initialize bit matrix
                                BitMatrix matrix = writer.encode(sText, BarcodeFormat.QR_CODE, 350, 350);
                                //Initialize barcode encoder
                                BarcodeEncoder encoder = new BarcodeEncoder();
                                //Initialize bitmap
                                bitmap[0] = encoder.createBitmap(matrix);
                                //Set bitmap on image view
                                //ivQRCode.setMaxHeight(500);

                                /*
                                ivQRCode.setColorFilter(
                                        ActivityCompat.getColor(getApplicationContext(),
                                                android.R.color.holo_green_light),
                                        PorterDuff.Mode.ADD);
                                 */
                                ivQRCode.setColorFilter(
                                        Color.rgb(r,g,b),
                                        PorterDuff.Mode.ADD);

                                ivQRCode.setImageBitmap(bitmap[0]);
                            /*
                            //Initialize input manager
                            InputMethodManager manager = (InputMethodManager) getSystemService(
                                    Context.INPUT_METHOD_SERVICE
                            );
                            //Hide soft keyboard
                            manager.hideSoftInputFromWindow(etQRCode.getApplicationWindowToken(), 0);

                             */
                            } catch (WriterException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Enter Text Please", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_qrcode, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_qrcodesave) {
            if(etQRCode.length()>0){
                //Hide soft keyboard
                InputMethodManager manager = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE
                );
                manager.hideSoftInputFromWindow(etQRCode.getApplicationWindowToken(), 0);
                if (etQRCode.length() > 0) {
                    CreateFolder();

                } else {
                    Toast.makeText(getApplicationContext(), "No QR Code found", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this,"Please create qr code first",Toast.LENGTH_SHORT).show();
            }

        }
        if (id == R.id.action_pairing) {
            if (printing != null) {
                printing.setPrintingCallback(this);
                //Event
                if (Printooth.INSTANCE.hasPairedPrinter()) {
                    Printooth.INSTANCE.removeCurrentPrinter();
                } else {
                    startActivityForResult(new Intent(QRcodeActivity.this, ScanningActivity.class), ScanningActivity.SCANNING_FOR_PRINTER);
                    changePairAndUnpair();
                }
            }
        }
        if (id == R.id.action_qrcodeprint) {
            if(etQRCode.length()>0){
                if (!Printooth.INSTANCE.hasPairedPrinter()) {
                    startActivityForResult(new Intent(QRcodeActivity.this, ScanningActivity.class),
                            ScanningActivity.SCANNING_FOR_PRINTER);
                } else {
                    printImages();
                }
            }else{
                Toast.makeText(this,"Please create qr code first",Toast.LENGTH_SHORT).show();
            }

        }
        if (id == R.id.action_qrcodeshare) {
            if(etQRCode.length()>0){
                shareimage();
            }else{
                Toast.makeText(this,"Please create qr code first",Toast.LENGTH_SHORT).show();
            }

        }

        return super.onOptionsItemSelected(item);
    }

    //Create Share method

    private void shareimage() {

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        BitmapDrawable drawable = (BitmapDrawable) ivQRCode.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        File file = new File(getExternalCacheDir() + "/" + getResources().getString(R.string.app_name) + ".jpg");
        Intent shareint;

        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            shareint = new Intent(Intent.ACTION_SEND);
            shareint.setType("image/*");
            shareint.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            shareint.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        startActivity(Intent.createChooser(shareint, "share image"));

    }

    public static Bitmap tintImage(Bitmap bitmap, int color){
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        Bitmap bitmapResult = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapResult);
        canvas.drawBitmap(bitmap,0,0,paint);
        return bitmapResult;
    }

    public void CreateFolder() {
        if (ActivityCompat.checkSelfPermission(QRcodeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {


            if (MainActivity.dir.isDirectory()) {
                //When directory is created
                try {

                    ivQRCode.setColorFilter(
                            Color.rgb(r,g,b),
                            PorterDuff.Mode.ADD);

                    File file = new File(MainActivity.dir, etQRCode.getText() + ".jpg");
                    //BitmapDrawable draw = (BitmapDrawable) ivQRCode.getDrawable();
                    //Bitmap bitmap = draw.getBitmap();
                    ivQRCode.buildDrawingCache();
                    Bitmap bitmap = ivQRCode.getDrawingCache();

                    outputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    Toast.makeText(getApplicationContext(), "Image Saved", Toast.LENGTH_SHORT).show();

                    outputStream.flush();
                    outputStream.close();


                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                //If directory is not created
                AlertDialog.Builder builder = new AlertDialog.Builder(QRcodeActivity.this);
                String sMessage = "Message : failed to create directory" +
                        "\nPath :" + Environment.getExternalStorageDirectory() +
                        "\nmkdirs :" + MainActivity.dir.mkdirs();
                builder.setMessage(sMessage);
                builder.show();
            }

        } else {
            //When permission is not granted
            //Request permission
            ActivityCompat.requestPermissions(QRcodeActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 500);
        }

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 500 && (grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            //WHen permission is granted
            CreateFolder();
        } else {
            //WHen permission is denied
            Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void changePairAndUnpair() {
        if (Printooth.INSTANCE.hasPairedPrinter()) {
            Toast.makeText(getApplicationContext(), new StringBuilder("Unpair")
                    .append(Printooth.INSTANCE.getPairedPrinter().getName()).toString(), Toast.LENGTH_SHORT).show();
            /*
            btn_unpair_pair.setText(new StringBuilder("Unpair")
                    .append(Printooth.INSTANCE.getPairedPrinter().getName()).toString());

             */
        } else {
            Toast.makeText(getApplicationContext(), "Paired with Printer", Toast.LENGTH_SHORT).show();
            //btn_unpair_pair.setText("Pair with Printer");
        }
    }

    private void printImages() {
        ArrayList<Printable> printables = new ArrayList<>();

        BitmapDrawable draw = (BitmapDrawable) ivQRCode.getDrawable();
        Bitmap bitmap = draw.getBitmap();

        //Load image from INTERNET
        Picasso.get()
                .load(String.valueOf(bitmap))
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        printables.add(new ImagePrintable.Builder(bitmap).build());
                        printing.print(printables);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
    }

    //keep for reference
    private void printText() {
        ArrayList<Printable> printables = new ArrayList<>();
        printables.add(new RawPrintable.Builder(new byte[]{27, 100, 4}).build());

        //Add text
        printables.add(new TextPrintable.Builder()
                .setText("Hello World")
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setNewLinesAfter(1)
                .build());

        //custom text
        printables.add(new TextPrintable.Builder()
                .setText("Hello World")
                .setLineSpacing(DefaultPrinter.Companion.getLINE_SPACING_60())
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASIZED_MODE_BOLD())
                .setUnderlined(DefaultPrinter.Companion.getUNDERLINED_MODE_ON())
                .setNewLinesAfter(1)
                .build());

        printing.print(printables);

    }

    @Override
    public void connectingWithPrinter() {
        Toast.makeText(this, "Connecting to printer", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void connectionFailed(@NotNull String s) {
        Toast.makeText(this, "Failed: " + s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(@NotNull String s) {
        Toast.makeText(this, "Error: " + s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMessage(@NotNull String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void printingOrderSentSuccessfully() {
        Toast.makeText(this, "Order sent to printer", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_COLORPICKER_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    //txt_barcode.setText(data.getStringExtra("barcode"));
                    String sr = data.getStringExtra("colorr");
                    String sg = data.getStringExtra("colorg");
                    String sb = data.getStringExtra("colorb");
                    //int g = Integer.parseInt(data.getStringExtra("color_g"));
                    //int b = Integer.parseInt(data.getStringExtra("color_b"));
                    r = Integer.parseInt(sr);
                    g = Integer.parseInt(sg);
                    b = Integer.parseInt(sb);

                    ivQRCode.setColorFilter(
                            Color.rgb(r,g,b),
                            PorterDuff.Mode.ADD);



                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there is no result
            }

        }

        if (requestCode == ScanningActivity.SCANNING_FOR_PRINTER &&
                resultCode == Activity.RESULT_OK) {
            initPrinting();

            changePairAndUnpair();
        }

    }

    private void initPrinting() {
        if (!Printooth.INSTANCE.hasPairedPrinter()) {
            printing = Printooth.INSTANCE.printer();
        }
        if (printing != null) {
            printing.setPrintingCallback(this);
        }
    }


}
