package com.example.lucas.paint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
public class ListFiles extends ListActivity {
    private List<String> directoryEntries = new ArrayList<String>();
    private List<ImageView> directoryImages = new ArrayList<ImageView>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        File directory = new File(i.getStringExtra("directory"));
        if (directory.isDirectory()){
            File[] files = directory.listFiles();
            //sort in descending date order
            Arrays.sort(files, new Comparator<File>(){
                public int compare(File f1, File f2) {
                    return -Long.valueOf(f1.lastModified())
                            .compareTo(f2.lastModified());
                }
            });
            //fill list with files
            this.directoryEntries.clear();
            for (File file : files){
                this.directoryEntries.add(file.getPath());
            }

            for (String chemin : this.directoryEntries) {
                File image = new File(chemin, "");
                ImageView aAjouter = new ImageView(this);
                Bitmap bitmap = this.decodeFile(image, 100, 500);
                aAjouter.setImageBitmap(bitmap);
                directoryImages.add(aAjouter);
            }

            ImageListViewAdapter imageList = new ImageListViewAdapter(this, R.layout.file_row, this.directoryImages);
            this.setListAdapter(imageList);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int pos, long id) {
        File clickedFile = new File(this.directoryEntries.get(pos));
        Intent i = getIntent();
        i.putExtra("clickedFile", clickedFile.toString());
        setResult(RESULT_OK, i);
        finish();
    }

    public static Bitmap decodeFile(File f,int WIDTH,int HIGHT){
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);

            //The new size we want to scale to
            final int REQUIRED_WIDTH=WIDTH;
            final int REQUIRED_HIGHT=HIGHT;
            //Find the correct scale value. It should be the power of 2.
            int scale=1;
            while(o.outWidth/scale/2>=REQUIRED_WIDTH && o.outHeight/scale/2>=REQUIRED_HIGHT)
                scale*=2;

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }
}