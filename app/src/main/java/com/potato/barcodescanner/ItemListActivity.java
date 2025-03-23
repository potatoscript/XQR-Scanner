package com.potato.barcodescanner;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import static com.potato.barcodescanner.MainActivity.sqLiteHelper;

//import com.opencsv.CSVReader;

//import android.widget.SearchView;

public class ItemListActivity extends AppCompatActivity {

    public static final String EXTRA_TEXT = "";
    public static final int REQUEST_CODE = 0;
    private static final int STORAGE_REQUEST_CODE_IMPORT = 1;
    private static final int STORAGE_REQUEST_CODE_EXPORT = 2;
    private String[] storagePermissions;

    GridView listView;
    ArrayList<ItemModel> list;
    ArrayList<String> barcodeArray;
    ItemListAdpter adapter = null;

    ImageView listPinchZoomImageView;;
    Animator mCurrentAnimator;
    int mLongAnimationDuration;

    // ImageButton btn_delete, btn_edit

    GridView bookList;

    private String barcode = "";
    private Context mContext;
    private Activity mActivity;
    private RelativeLayout mCLayout;
    private GridView mGridView;
    Cursor cursor;
    //private EditText searchKey;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemlist);

        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        listView = (GridView) findViewById(R.id.bookList);
        list = new ArrayList<>();
        barcodeArray = new ArrayList<String>();
        adapter = new ItemListAdpter(this, R.layout.item_model, list);
        listView.setAdapter(adapter);

        listPinchZoomImageView = findViewById(R.id.listPinchZoomImageView);

        mCLayout = (RelativeLayout) findViewById(R.id.booklist_layout);

        cursor = sqLiteHelper.getData("SELECT * FROM " + Constants.TABLE_NAME + " ORDER BY ID DESC LIMIT 200");
        list.clear();
        barcodeArray.clear();
        getListData(cursor);

        bookList = findViewById(R.id.bookList);

        //Get the application context
        mContext = getApplicationContext();
        mActivity = ItemListActivity.this;

        bookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                barcode = barcodeArray.get(i);
            }
        });

        bookList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                try{
                    zoomFromThumb(barcodeArray.get(position));
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }

                return true;

            }
        });

        listPinchZoomImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listPinchZoomImageView.setVisibility(View.INVISIBLE);
            }
        });

    }

    private  void zoomFromThumb(String filename){
        try{
            File imgFile = new File(MainActivity.dir, filename + ".jpg");
            if(imgFile.exists()){
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                listPinchZoomImageView.setImageBitmap(bitmap);
                listPinchZoomImageView.setVisibility(View.VISIBLE);
            }
        }catch(Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }


    }

    public void getListData(Cursor cursor) {
        try{
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String barcode = cursor.getString(2);

                byte[] image = cursor.getBlob(3);

                list.add(new ItemModel(id, name, barcode, image));

                barcodeArray.add(barcode);
            }
        }catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }


        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_itemlist, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search Item");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Cursor cursor = sqLiteHelper.getData("SELECT * FROM " +
                        Constants.TABLE_NAME + " WHERE " + Constants.C_REMARK + " LIKE '%" + newText + "%' ");
                list.clear();
                barcodeArray.clear();
                getListData(cursor);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        //menu item click handling
        if (id == R.id.action_edit) {
            if(barcode.length()>0){
                Intent intent = new Intent();
                intent.putExtra("barcode", barcode);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }else{
                Toast.makeText(this,"Please select data first",Toast.LENGTH_SHORT).show();
            }
        }
        if (id == R.id.action_delete) {
            if(barcode.length()>0) {
                new AlertDialog.Builder(ItemListActivity.this)
                        .setTitle("DELETE DATA")
                        .setMessage("Do you really want to delete it")
                        .setIcon(android.R.drawable.ic_delete)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sqLiteHelper.deleteData(barcode);
                                Cursor cursor = sqLiteHelper.getData("SELECT * FROM " + Constants.TABLE_NAME);
                                list.clear();
                                barcodeArray.clear();
                                getListData(cursor);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }else{
                Toast.makeText(this,"Please select data first",Toast.LENGTH_SHORT).show();
            }
        }
        if (id == R.id.action_backup) {
            //backup all records to csv file
            if (checkStoragePermission()) {
                //permission allowed
                new AlertDialog.Builder(ItemListActivity.this)
                        .setTitle("BACKUP DATA")
                        .setMessage("Do you want to backup your data ? \n It will overwrite your pervious backup data")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                backupDB();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();

            } else {
                //permission dnot allowed
                requestStoragePermissionExport();
            }
        }
        if(id== R.id.action_share){
            /*
            if(checkStoragePermission()){
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                File file = new File(getApplicationContext().getDatabasePath(databaseFile).getPath());
                Intent shareint;

                try {
                    FileOutputStream outputStream = new FileOutputStream(file);
                    outputStream.flush();
                    outputStream.close();
                    shareint = new Intent(Intent.ACTION_SEND);
                    shareint.setType("binary");
                    shareint.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                    shareint.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                startActivity(Intent.createChooser(shareint, "share image"));
            }
            //not working keep for future reference
             */
        }
        if (id == R.id.action_restore) {
            //restore all records from csv file
            if (checkStoragePermission()) {
                //permission allowed
                new AlertDialog.Builder(ItemListActivity.this)
                        .setTitle("RESTORE DATA")
                        .setMessage("Do you want to restore your data ? \n This will overwrite your current data")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                restoreDB();
                                onResume();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();

            } else {
                //permission dnot allowed
                requestStoragePermissionImport();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void backupDB() {
        try {
            File currentDB = new File(getApplicationContext().getDatabasePath(Constants.DB_NAME).getPath());
            //File copieDB = new File(Environment.getExternalStorageDirectory() + "/POTATOSCRIPT/QR Scanner X/DATA_DB.sqlite");
            //File copieDB = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
            File copieDB = new File(MainActivity.dir, Constants.DB_NAME);

            if (currentDB.exists()) {
                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(copieDB).getChannel();

                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

                Toast.makeText(this, "Database Backup Done! ", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void restoreDB() {
        try {
            File currentDB = new File(getApplicationContext().getDatabasePath(Constants.DB_NAME).getPath());
            //File copieDB = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/POTATOSCRIPT/QR Scanner X/DATA_DB.sqlite");
            File copieDB = new File(MainActivity.dir, Constants.DB_NAME);
            if (currentDB.exists()) {
                FileChannel src = new FileInputStream(copieDB).getChannel();
                FileChannel dst = new FileOutputStream(currentDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Cursor cursor = sqLiteHelper.getData("SELECT * FROM " + Constants.TABLE_NAME);
                list.clear();
                barcodeArray.clear();
                getListData(cursor);
                Toast.makeText(this, "Database Restore Done!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkStoragePermission() {
        //check if storage permission is enabled or not and return true/false
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermissionImport() {
        //request storage permission for import
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE_IMPORT);
    }

    private void requestStoragePermissionExport() {
        //request storage permission for export
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE_EXPORT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case STORAGE_REQUEST_CODE_EXPORT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission granted
                    backupDB();
                } else {
                    //permission denied
                    Toast.makeText(this, "Storage permission required...", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case STORAGE_REQUEST_CODE_IMPORT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission granted
                    restoreDB();
                } else {
                    //permission denied
                    Toast.makeText(this, "Storage permission required...", Toast.LENGTH_SHORT).show();
                }
            }
        }


    }


}



