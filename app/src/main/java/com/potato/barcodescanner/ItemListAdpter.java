package com.potato.barcodescanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ItemListAdpter extends BaseAdapter {

    private final Context context;
    private final int layout;
    private final ArrayList<ItemModel> bookList;

    public ItemListAdpter(Context context, int layout, ArrayList<ItemModel> bookList) {
        this.context = context;
        this.layout = layout;
        this.bookList = bookList;
    }

    @Override
    public int getCount() {
        return bookList.size();
    }

    @Override
    public Object getItem(int position) {
        return bookList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView txtName, txtBarcode, txtNumber;
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {

        View row = view;
        ViewHolder holder = new ViewHolder();

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.txtName = (TextView) row.findViewById(R.id.Remark);
            holder.txtBarcode = (TextView) row.findViewById(R.id.Barcode);
            holder.imageView = (ImageView) row.findViewById(R.id.BookImage);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        ItemModel book = bookList.get(position);

        holder.txtName.setText(book.getName());
        holder.txtBarcode.setText(book.getBarcode());


        byte[] bookImage = book.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(bookImage, 0, bookImage.length);
        holder.imageView.setImageBitmap(bitmap);


        return row;
    }
}
