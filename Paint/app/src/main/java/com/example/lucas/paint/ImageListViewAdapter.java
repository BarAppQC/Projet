package com.example.lucas.paint;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Lucas on 24/11/2017.
 */

public class ImageListViewAdapter extends ArrayAdapter<ImageView> {

    Context context;

    public ImageListViewAdapter(Context context, int resourceId, //resourceId=your layout
                                 List<ImageView> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        convertView = mInflater.inflate(R.layout.file_row, null);
        convertView.setBackgroundDrawable(rowItem.getDrawable());

        return convertView;
    }
}
