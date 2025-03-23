package com.potato.barcodescanner;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ErrorCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.google.zxing.Result;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_PERMISSION = 10;
    public static final int REQUEST_CODE_CAMERA = 100;
    public static final int REQUEST_CODE_GALLERY = 200;
    public static final int REQUEST_CODE_UPDATE = 999;

    //file directory to backup file
    public static File dir;

    OutputStream outputStream;
    private String currentPhotoPath;

    //arrays of permissions
    private String[] arrayPermissions;

    //variables (contain data to save)
    private Uri imageUri = null;

    public static final int LAUNCH_BOOKLIST_ACTIVITY = 1;
    public static SQLiteHelper sqLiteHelper = null;
    public CodeScanner codeScanner;

    //references to buttons and other controls on the layout
    Switch onoff;
    TextView txt_barcode, txt_remark, txt_number;
    ImageView imageView;
    ImageView imageView2;

    Button btnRotate;
    int rotate = 0;
    Bitmap imageViewBitmap;
    CodeScannerView scannerView;

    Matrix matrix = new Matrix();
    Float scale = 1f;
    ScaleGestureDetector objScaleGestureDetector;
    final static int NONE = 0;
    final static int PAN = 1;
    final static int ZOOM = 2;
    int mEventState;
    float mStartX = 0;
    float mStartY = 0;
    float mTranslateX = 0;
    float mTranslateY = 0;

    int mImageWidth;
    int mImageHeight;
    final static float mMinZoom =1.f;
    final static float mMaxZoom =3.f;
    float mScaleFactor = 1.f;



    //to check taking camera or not
    int TAKECAMERA = 0;

    //views
    private FloatingActionButton addRecordBtn;
    private RecyclerView recordsRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setTitle("V.7.4");

        //Creates instance of the manager
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
        //Returns an intent object that you use to check for an update
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        // Checks that the platform will allow the specified type of update
        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {
                if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                        result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(result, AppUpdateType.IMMEDIATE,
                                MainActivity.this, REQUEST_CODE_UPDATE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        setContentView(R.layout.activity_main);

        //create dir to save data file
        try{
            File filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            dir = new File(filepath, Constants.potatoDir);
            if (dir.exists()) {

            } else {
                dir.mkdirs();
            }


        imageView = findViewById(R.id.imageView);

        final ImageView zoom =(ImageView) findViewById(R.id.imageView);
        final Animation zoomAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom);
        imageView.startAnimation(zoomAnimation);


        imageView2 = findViewById(R.id.imageView2);
        onoff = findViewById(R.id.swOnOff);
        scannerView = findViewById(R.id.scanner_view);
        txt_barcode = findViewById(R.id.txt_barcode);
        txt_remark = findViewById(R.id.txt_remark);
        btnRotate = findViewById(R.id.btnRotate);
        //zoomControls =findViewById(R.id.zoom_controls);
        //zoomControls.hide();
        //objScaleGestureDetector = new ScaleGestureDetector(this,new PinchZoomListener());
        objScaleGestureDetector = new ScaleGestureDetector(this,new ScaleListener());


        imageView.setImageResource(R.drawable.xqrscanner);
        imageView.setScaleType(ImageView.ScaleType.MATRIX);
        matrix.setScale(0.4f, 0.4f);
        imageView.setImageMatrix(matrix);

        txt_remark.setPadding(10,10,10,10);
        txt_remark.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                    displayKeyboard(txt_remark);
            }
        });
        txt_barcode.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                    displayKeyboard(txt_barcode);
            }
        });

        setupPermissions();

        codeScanner();

        //create the database
        sqLiteHelper = new SQLiteHelper(this);

        onoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    codeScanner.startPreview();
                } else {
                    codeScanner.releaseResources();
                }
            }
        });

        btnRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotate+=90;
                rotateImage(imageView);
            }
        });

        txt_barcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                imageView.setImageResource(R.drawable.xqrscanner);
                imageView2.setImageResource(R.drawable.xqrscanner);
                txt_remark.setText("");
                try {
                    Cursor cursor = sqLiteHelper.getData("SELECT * FROM " + Constants.TABLE_NAME +
                            " WHERE " + Constants.C_BARCODE + " = '" + txt_barcode.getText() + "' ");
                    while (cursor.moveToNext()) {
                        int id = cursor.getInt(0);
                        String name = cursor.getString(1);

                        //Show Image View from file path
                        File imgFile = new File(MainActivity.dir, txt_barcode.getText() + ".jpg");
                        if(imgFile.exists()){
                            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                            imageView.setImageBitmap(bitmap);
                            imageViewBitmap=bitmap;
                            matrix.setScale(0.4f, 0.4f);
                            imageView.setImageMatrix(matrix);
                            TAKECAMERA = 2;

                        }

                        byte[] image = cursor.getBlob(3);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                        imageView2.setImageBitmap(bitmap);

                        txt_remark.setText(name);


                    }
                } catch (Exception e) {
                    //Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        });

        }catch(Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    private void displayKeyboard(TextView txt){
           InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
           imm.showSoftInput(txt,0);
           // imm.toggleSoftInputFromWindow(txt.getApplicationWindowToken(),InputMethodManager.SHOW_FORCED,0);
    }

    /*
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()& MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                mEventState = PAN;
                mStartX = event.getX();
                mStartY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                mEventState = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                mTranslateX = event.getX()-mStartX;
                mTranslateY = event.getY()-mStartY;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mEventState = ZOOM;
                break;
        }
        objScaleGestureDetector.onTouchEvent(event);
        if((mEventState== PAN ) || mEventState==ZOOM){
            //invalidate();
            //requestLayout();
        }
        return true;
    }
*/
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        objScaleGestureDetector.onTouchEvent(event);
        return true;
    }

    public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public boolean onScale(ScaleGestureDetector detector) {

           // mScaleFactor *=detector.getScaleFactor();

            scale = scale*detector.getScaleFactor();
            //scale = Math.max(mMinZoom, Math.min(mMaxZoom,mScaleFactor));
            scale = Math.max(0.3f, Math.min(scale,3f));

            //invalidate();
            //requestLayout();
            matrix.setScale(scale, scale);
            imageView.setImageMatrix(matrix);
            return true;
        }
    }

    private byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();

        //reduce large image size to thumbnail size
        int h = 100;
        int w = 100;
        Bitmap scaled =Bitmap.createScaledBitmap(bitmap,h,w,true);


        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        scaled.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    protected void onResume() {
        super.onResume();
        codeScanner.startPreview();

    }

    @Override
    protected void onPause() {
        super.onPause();
        codeScanner.releaseResources();
    }

    private void codeScanner() {

        codeScanner = new CodeScanner(this, scannerView);

        codeScanner.setCamera(CodeScanner.CAMERA_BACK);
        codeScanner.setFormats(CodeScanner.ALL_FORMATS);
        codeScanner.setAutoFocusMode(AutoFocusMode.SAFE);
        codeScanner.setScanMode(ScanMode.CONTINUOUS);
        codeScanner.setAutoFocusEnabled(true);
        codeScanner.setFlashEnabled(false);

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {

                //use runnable we can get the data and asign it to the textview
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (onoff.isChecked()) {

                            if (result.getText().contains("http")) {
                                goToUrl(result.getText());
                            } else if (result.getText().contains("_")) {
                                String s[] = result.getText().split("_");
                                txt_barcode.setText(s[0]);
                                //txt_number.setText(s[2]);
                                txt_remark.setText(s[1]);
                            } else {
                                reset();
                                txt_barcode.setText(result.getText());
                            }
                        } else {
                            onPause();
                        }

                    }
                });


            }
        });

        codeScanner.setErrorCallback(new ErrorCallback() {
            @Override
            public void onError(@NonNull Exception error) {
                Log.e("Main", "Camera initialization error:" + error.getMessage());
            }
        });

        //if you did not set the scanmode to continuous
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeScanner.startPreview();
            }
        });

    }

    private void goToUrl(String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }

    private void setupPermissions() {

        String[] permission = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), permission[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(), permission[1]) == PackageManager.PERMISSION_GRANTED
        ) {


        } else {
            ActivityCompat.requestPermissions(MainActivity.this, permission,
                    REQUEST_CODE_PERMISSION);
        }
        /*
        Integer permission = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
        );
        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest();
        }

         */
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length < 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "You need the camera & stroge permission", Toast.LENGTH_SHORT)
                        .show();
            } else {
                //succcessful

            }
        }

        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            } else {
                Toast.makeText(getApplicationContext(), "You don't have permission to access file location", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void reset() {
        //reset the imageview rotation to 0 deg
        //rotate=0;
        //rotateImage(imageView);
        TAKECAMERA = 0;

        txt_barcode.setText("");
        txt_remark.setText("");
        //txt_number.setText("0.0");
        imageView.setImageResource(R.drawable.xqrscanner);
        imageView2.setImageResource(R.drawable.xqrscanner);
        onoff.setChecked(true);
        onResume();

        //Initialize input manager
        InputMethodManager manager = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE
        );
        //Hide soft keyboard
        manager.hideSoftInputFromWindow(txt_remark.getApplicationWindowToken(), 0);
        //manager.hideSoftInputFromWindow(txt_number.getApplicationWindowToken(), 0);
    }

    private boolean checkStoragePermission() {
        //check if storage permission is enabled or not and return true/false
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_camera) {

            if(txt_barcode.length()>0){
                try {
                String fileName = txt_barcode.getText().toString();
                File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_DCIM);

                    File imageFile = File.createTempFile(fileName,".jpg",storageDirectory);
                    currentPhotoPath = imageFile.getAbsolutePath();//to get the bitmap

                    Uri imageUri = FileProvider.getUriForFile(MainActivity.this,
                            "com.potato.barcodescanner.fileprovider", imageFile);

                    Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    camera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                    if(camera.resolveActivity(getPackageManager())!=null){
                        startActivityForResult(camera, REQUEST_CODE_CAMERA);
                    }else{
                        Toast.makeText(MainActivity.this, "There is no app that support this action",Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(this,"Please scan something first",Toast.LENGTH_SHORT).show();
            }

        }
        if (id == R.id.action_save) {
            if(txt_barcode.length()>0){
                String barcode = txt_barcode.getText().toString();
                if (barcode == "") barcode = "-";
                String name = txt_remark.getText().toString();
                if (name == "") name = "-";

                try {
                    if(TAKECAMERA!=2){
                            File file = new File(MainActivity.dir, txt_barcode.getText() + ".jpg");
                            outputStream = new FileOutputStream(file);
                            imageViewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
                            outputStream.flush();
                            outputStream.close();
                    }

                    sqLiteHelper.insertData(name,barcode,imageViewToByte(imageView2));

                    Toast.makeText(getApplicationContext(), "Added Successfully", Toast.LENGTH_SHORT).show();
                    reset();
                } catch (Exception e) {
                    if (e.toString().contains("graphics")) {
                        Toast.makeText(getApplicationContext(), "Please take a photo", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            }else{
                Toast.makeText(this,"Please scan something first",Toast.LENGTH_SHORT).show();
            }

        }
        if (id == R.id.action_data) {
            Intent intent = new Intent(MainActivity.this, ItemListActivity.class);
            //startActivity(intent)
            startActivityForResult(intent, LAUNCH_BOOKLIST_ACTIVITY);//because want to send back data later
        }
        if (id == R.id.action_reload) {
            reset();
        }
        if (id == R.id.action_qrcode) {
            Intent intent = new Intent(MainActivity.this, QRcodeActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_help) {
            //goToUrl("https://play.google.com/store/apps/details?id=com.potato.barcodescanner");
            Intent intent = new Intent(MainActivity.this, HelpActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void rotateImage(ImageView imageView){

        Matrix matrix = new Matrix();
        imageView.setScaleType(ImageView.ScaleType.MATRIX);
        matrix.postRotate(rotate,
                imageView.getDrawable().getBounds().width()/2,
                imageView.getDrawable().getBounds().height()/2);
        imageView.setImageMatrix(matrix);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == REQUEST_CODE_UPDATE) {
            Toast.makeText(MainActivity.this, "Start Download", Toast.LENGTH_SHORT).show();
            if (resultCode != RESULT_OK) {
                Log.d("", "Update flow failed" + resultCode);
                //Toast.makeText(getApplicationContext(),resultCode,Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == REQUEST_CODE_CAMERA && resultCode==RESULT_OK) {

            //Bundle bundle = data.getExtras();
            //Bitmap image =(Bitmap)bundle.get("data");//get the thumview;
            //Bitmap image = (Bitmap) data.getExtras().get("data");


            imageViewBitmap = BitmapFactory.decodeFile(currentPhotoPath);

            imageView.setImageBitmap(imageViewBitmap);
            //mPinchZoomImageView.setImageBitmap(imageViewBitmap);
            imageView2.setImageBitmap(imageViewBitmap);

            TAKECAMERA = 1;
            /*
            try{
                //imageUri = data.getData();
                Uri uri = data.getData();
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);
                //imageView.setImageURI(data.getData());
            }catch(Exception e){
                Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
            }

             */


        }


        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }

        if (requestCode == LAUNCH_BOOKLIST_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    txt_barcode.setText(data.getStringExtra("barcode"));
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there is no result
            }

        }

        if (requestCode == REQUEST_CODE_UPDATE && resultCode == Activity.RESULT_CANCELED) {
            finish();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


}



