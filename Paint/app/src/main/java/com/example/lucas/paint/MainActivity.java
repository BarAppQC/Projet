package com.example.lucas.paint;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.hardware.Camera;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

import colorpicker.ColorPickerDialog;

import static android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK;
import static android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT;
import static android.hardware.Camera.getCameraInfo;
import static android.hardware.Camera.getNumberOfCameras;
import static android.hardware.Camera.open;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String DEBUG_TAG = "StillImageActivity";
    final private static String STILL_IMAGE_FILE = "cameraImage.jpg";

    //region LAYOUT
    private float smallBrush, mediumBrush, largeBrush;
    private ImageButton drawBtn, eraseBtn, newBtn, saveBtn, undoBtn, redoBtn, openBtn, appareil_photoBtn, shareBtn, pickColorBtn, switch_camera, rotateBtn;
    TextView texte_taille;
    ImageButton everySizeBtn;
    SeekBar barre_taille;
    static final String CAMERA_PIC_DIR = "/DCIM/100ANDRO/";
    int curColor = Color.BLUE;
    private String imgSaved;
    private String idImage;
    private boolean imageSauvegardee = false;
    CameraSurfaceView cameraView;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view){
        drawView.setDrawingCacheEnabled(true);
        if(view.getId()==R.id.draw_btn){
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setContentView(R.layout.brush_choose);
            brushDialog.setTitle("Taille du pinceau : ");

            //region ANCIEN CODE
            /*ImageButton smallBtn = (ImageButton) brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(smallBrush);
                    drawView.setLastBrushSize(smallBrush);
                    brushDialog.dismiss();
                }
            });

            ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(mediumBrush);
                    drawView.setLastBrushSize(mediumBrush);
                    brushDialog.dismiss();
                }
            });

            ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(largeBrush);
                    drawView.setLastBrushSize(largeBrush);
                    brushDialog.dismiss();
                }
            });*/
            //endregion ANCIEN CODE

            everySizeBtn = (ImageButton) brushDialog.findViewById(R.id.every_brush);
            barre_taille = (SeekBar) brushDialog.findViewById(R.id.barre_taille);
            texte_taille = (TextView) brushDialog.findViewById(R.id.texte_taille);

            barre_taille.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    texte_taille = (TextView) brushDialog.findViewById(R.id.texte_taille);
                    texte_taille.setText(String.valueOf(i));
                    ShapeDrawable brush_taille = new ShapeDrawable(new OvalShape());
                    brush_taille.setTint(Color.BLACK);
                    brush_taille.setIntrinsicHeight(i);
                    brush_taille.setIntrinsicWidth(i);
                    everySizeBtn = (ImageButton) brushDialog.findViewById(R.id.every_brush);
                    everySizeBtn.setImageDrawable(brush_taille);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            everySizeBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(barre_taille.getProgress());
                    drawView.setLastBrushSize(barre_taille.getProgress());
                    brushDialog.dismiss();
                }
            });

            drawView.setErase(false);
            drawView.setColor(curColor);
            brushDialog.show();

        } else if(view.getId()==R.id.erase_btn){
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Eraser size:");
            brushDialog.setContentView(R.layout.brush_choose);

            everySizeBtn = (ImageButton) brushDialog.findViewById(R.id.every_brush);
            barre_taille = (SeekBar) brushDialog.findViewById(R.id.barre_taille);
            texte_taille = (TextView) brushDialog.findViewById(R.id.texte_taille);

            barre_taille.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    texte_taille = (TextView) brushDialog.findViewById(R.id.texte_taille);
                    texte_taille.setText(String.valueOf(i));
                    ShapeDrawable brush_taille = new ShapeDrawable(new OvalShape());
                    brush_taille.setTint(Color.BLACK);
                    brush_taille.setIntrinsicHeight(i);
                    brush_taille.setIntrinsicWidth(i);
                    everySizeBtn = (ImageButton) brushDialog.findViewById(R.id.every_brush);
                    everySizeBtn.setImageDrawable(brush_taille);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            everySizeBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(barre_taille.getProgress());
                    brushDialog.dismiss();
                }
            });

            //region ANCIEN CODE
            /*ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(smallBrush);
                    brushDialog.dismiss();
                }
            });
            ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(mediumBrush);
                    brushDialog.dismiss();
                }
            });
            ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(largeBrush);
                    brushDialog.dismiss();
                }
            });*/
            //endregion ANCIEN CODE

            brushDialog.show();
        } else if(view.getId()==R.id.new_btn){
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("Nouveau dessin");
            newDialog.setMessage("Commencer un nouveau dessin (Tu vas perdre ton dessin) ?");
            newDialog.setPositiveButton("Oui", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    drawView.startNew();
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton("Annuler", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            newDialog.show();

        } else if (view.getId()==R.id.save_btn) {
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Sauver le dessin");
            saveDialog.setMessage("Sauver le dessin dans la galerie du téléphone ?");
            saveDialog.setPositiveButton("Oui", new DialogInterface.OnClickListener(){
                @RequiresApi(api = Build.VERSION_CODES.M)
                public void onClick(DialogInterface dialog, int which){

                    if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        saveImage();
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        saveImage();
                    }

                    drawView.destroyDrawingCache();
                }
            });
            saveDialog.setNegativeButton("Annuler", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            saveDialog.show();
        } else if(view.getId() == R.id.undo_btn) {
            drawView.onClickUndo();
        } else if(view.getId() == R.id.redo_btn) {
            drawView.onClickRedo();
        } else if(view.getId() == R.id.open_btn) {
            String ImageDir =Environment.getExternalStorageDirectory().getPath()+CAMERA_PIC_DIR;
            Intent i = new Intent(this, ListFiles.class);
            i.putExtra("directory", ImageDir);
            startActivityForResult(i,0);
        } else if (view.getId() == R.id.appareil_photo_btn) {
            // création de l'objet cameraView
            cameraView = new CameraSurfaceView( getApplicationContext());
            LinearLayout all = (LinearLayout) findViewById(R.id.all);
            all.addView(cameraView);

            //sauvegarde en local
            ImageButton capture = (ImageButton) findViewById(R.id.capture);
            capture.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    cameraView.capture(new Camera.PictureCallback() {
                        public void onPictureTaken(byte[] data, Camera camera) {
                            try {
                                // Fully qualified path name. In this case, we
                                // use the Files subdir
                                Drawable imagePrise = new BitmapDrawable(getResources(),BitmapFactory.decodeByteArray(data, 0, data.length));
                                drawView.setBackground(imagePrise);
                                cameraView.setVisibility(View.GONE);

                            } catch (Exception e) {
                            }
                        }
                    });
                }
            });


            //sauvegarde en local
            ImageButton switch_camera = (ImageButton) findViewById(R.id.switch_camera_btn);
            switch_camera.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (cameraView.camera != null) {
                        cameraView.camera.stopPreview();
                    }

                    CameraSurfaceView newCameraView = new CameraSurfaceView( getApplicationContext());
                    LinearLayout all = (LinearLayout) findViewById(R.id.all);

                    if(cameraView.cameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
                        newCameraView.cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                    }
                    else {
                        newCameraView.cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                    }
                    newCameraView.camera = Camera.open(cameraView.cameraId);

                    cameraView.setVisibility(View.GONE);

                    // sauvegarde de la nouvelle configuration pour checker en cas d'un nouvel appel sur le bouton
                    cameraView = newCameraView;
                    all.addView(newCameraView);
                }
            });

        } else if(view.getId()==R.id.share_btn){
            checkPermissionAndShareImage();


        }else if(view.getId()==R.id.save_btn){
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Sauver le dessin");
            saveDialog.setMessage("Sauver le dessin dans la galerie du téléphone ?");
            saveDialog.setPositiveButton("Oui", new DialogInterface.OnClickListener(){
                @RequiresApi(api = Build.VERSION_CODES.M)
                public void onClick(DialogInterface dialog, int which){

                    checkPermissionAndSaveImage();
                    drawView.destroyDrawingCache();
                }
            });
            saveDialog.setNegativeButton("Annuler", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            saveDialog.show();
        } else if(view.getId()==R.id.color_btn){
            ColorPickerDialog colorPickerDialog = new ColorPickerDialog(this, drawView.paintColor, new ColorPickerDialog.OnColorSelectedListener() {

                @Override
                public void onColorSelected(int color) {
                    drawView.setColor(color);
                    pickColorBtn.setBackgroundColor(color);
                    curColor = color;

                }

            });
            colorPickerDialog.show();
        } else if(view.getId()==R.id.rotate){

            BitmapDrawable drawable=  new  BitmapDrawable(Bitmap.createScaledBitmap(rotate(drawView.getDrawingCache(),-90), drawView.getWidth(), drawView.getHeight(), true));
            drawView.startNew();
            drawView.setBackground(drawable);
            drawView.invalidate();
        }

        drawView.setDrawingCacheEnabled(false);
    }

    public Bitmap rotate(Bitmap paramBitmap,int rotateAngle)
    {
        if (rotateAngle% 360 == 0) {
            return paramBitmap;
        }
        Matrix localMatrix = new Matrix();
        float f1 = paramBitmap.getWidth() / 2;
        float f2 = paramBitmap.getHeight() / 2;
        localMatrix.postTranslate(-paramBitmap.getWidth() / 2, -paramBitmap.getHeight() / 2);
        localMatrix.postRotate(rotateAngle);
        localMatrix.postTranslate(f1, f2);
        paramBitmap = Bitmap.createBitmap(paramBitmap, 0, 0, paramBitmap.getWidth(), paramBitmap.getHeight(), localMatrix, false);
        new Canvas(paramBitmap).drawBitmap(paramBitmap, 0.0F, 0.0F, null);
        return paramBitmap;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void shareImage() {
        if ( imageSauvegardee){
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            sharingIntent.setType("image/*");
            sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imgSaved));
            startActivity(Intent.createChooser(sharingIntent, "Share Image Using"));}
        else{
            Toast errorToast = Toast.makeText(getApplicationContext(),
                    "Le dessin doit être sauvegardé avant d'être partagé !", Toast.LENGTH_SHORT);
            errorToast.show();
        }
    }

    private void saveImage(){
        idImage = UUID.randomUUID().toString()+".jpg";
        drawView.setDrawingCacheEnabled(true);
        Bitmap img = drawView.getDrawingCache();
        imgSaved = MediaStore.Images.Media.insertImage(
                getContentResolver(), img, idImage
                , "dessin");

        if(imgSaved!=null){
            Toast savedToast = Toast.makeText(getApplicationContext(),
                    "Dessin sauvegardé dans la galerie !", Toast.LENGTH_SHORT);
            savedToast.show();
            imageSauvegardee = true;
        }
        else{
            Toast unsavedToast = Toast.makeText(getApplicationContext(),
                    "Oups, le dessin n'a pas pu être sauvegardé...", Toast.LENGTH_SHORT);
            unsavedToast.show();
        }

        drawView.setDrawingCacheEnabled(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissionAndSaveImage(){
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            saveImage();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            saveImage();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissionAndShareImage(){
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            shareImage();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            shareImage();
        }
    }

    //endregion LAYOUT

    //region VIEW
    private DrawingViewPro drawView;
    private ImageButton currPaint;

    //endregion VIEW

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //region VIEW
        drawView = (DrawingViewPro)findViewById(R.id.drawing);

        //endregion VIEW

        //region LAYOUT
        drawBtn = (ImageButton)findViewById(R.id.draw_btn);
        drawBtn.setOnClickListener(this);
        drawView.setBrushSize(mediumBrush);

        eraseBtn = (ImageButton)findViewById(R.id.erase_btn);
        eraseBtn.setOnClickListener(this);

        newBtn = (ImageButton)findViewById(R.id.new_btn);
        newBtn.setOnClickListener(this);

        saveBtn = (ImageButton)findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);
        //endregion LAYOUT

        undoBtn = (ImageButton)findViewById(R.id.undo_btn);
        undoBtn.setOnClickListener(this);

        redoBtn = (ImageButton)findViewById(R.id.redo_btn);
        redoBtn.setOnClickListener(this);

        openBtn = (ImageButton)findViewById(R.id.open_btn);
        openBtn.setOnClickListener(this);

        appareil_photoBtn = (ImageButton)findViewById(R.id.appareil_photo_btn);
        appareil_photoBtn.setOnClickListener(this);

        shareBtn = (ImageButton)findViewById(R.id.share_btn);
        shareBtn.setOnClickListener(this);

        pickColorBtn = (ImageButton)findViewById(R.id.color_btn);
        pickColorBtn.setOnClickListener(this);

        switch_camera = (ImageButton) findViewById(R.id.switch_camera_btn);
        switch_camera.setOnClickListener(this);

        rotateBtn = (ImageButton) findViewById(R.id.rotate);
        rotateBtn.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode==RESULT_OK) {
            String tmp = data.getExtras().getString("clickedFile");
            Drawable fond_ecran = Drawable.createFromPath(tmp);
            drawView.setBackgroundDrawable(fond_ecran);
        }
    }

    //region CAMERA

    public class CameraSurfaceView extends SurfaceView implements
            SurfaceHolder.Callback {
        public Camera camera = null;
        public SurfaceHolder mHolder = null;
        public int cameraId = 0;
        public Camera.Parameters params = null;

        public CameraSurfaceView(Context context) {
            super(context);
            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed
            mHolder = getHolder();
            mHolder.addCallback(this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

            // optional -- use the front facing camera
            Camera.CameraInfo info = new Camera.CameraInfo();
            for (int camIndex = 0; camIndex < Camera.getNumberOfCameras(); camIndex++) {
                getCameraInfo(camIndex, info);
                if (info.facing == CAMERA_FACING_FRONT) {
                    cameraId = camIndex;
                    break;
                }
            }
        }

        public void surfaceCreated(SurfaceHolder holder) {
            mHolder = holder;
            try {
                releaseCameraAndPreview();
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && cameraId == 0) {
                    camera = open(CAMERA_FACING_BACK);
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                    camera = open(CAMERA_FACING_FRONT);
                }

            } catch (Exception e) {
                Log.e(getString(R.string.app_name), "failed to open Camera");
                e.printStackTrace();
            }

            try {
                camera.setPreviewDisplay(mHolder);
            } catch (Exception e) {
                releaseCameraAndPreview();
                Log.e(DEBUG_TAG, "Failed to set camera preview display", e);
            }

            params = camera.getParameters();
        }

        private void releaseCameraAndPreview() {
            if (camera != null) {
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        }
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            try {
                if (params != null) {
                    params = camera.getParameters();
                }

                // not all cameras supporting setting arbitrary sizes
                List<Camera.Size> sizes = params.getSupportedPreviewSizes();
                Camera.Size pickedSize = getBestFit(sizes, width, height);
                if (pickedSize != null) {
                    params.setPreviewSize(pickedSize.width, pickedSize.height);
                    Log.d(DEBUG_TAG, "Preview size: (" + pickedSize.width + ","
                            + pickedSize.height + ")");
                    // even after setting a supported size, the preview size may
                    // still end up just being the surface size (supported or
                    // not)
                    camera.setParameters(params);
                }
                // set the orientation to standard portrait.
                // Do this only if you know the specific orientation (0,90,180,
                // etc.)
                // Only works on API Level 8+
                if  (pickedSize != null) {
                    setCameraDisplayOrientation(pickedSize.width, pickedSize.height);
                } else {
                    Camera.Size previewSize = camera.getParameters().getPreviewSize();
                    setCameraDisplayOrientation(previewSize.width, previewSize.height);
                }
                camera.startPreview();
            } catch (Exception e) {
                Log.e(DEBUG_TAG, "Failed to set preview size", e);
            }
        }

        private Camera.Size getBestFit(List<Camera.Size> sizes, int width, int height) {
            Camera.Size bestFit = null;
            ListIterator<Camera.Size> items = sizes.listIterator();
            while (items.hasNext()) {
                Camera.Size item = items.next();
                if (item.width <= width && item.height <= height) {
                    if (bestFit != null) {
                        // if our current best fit has a smaller area, then we
                        // want the new one (bigger area == better fit)
                        if (bestFit.width * bestFit.height < item.width
                                * item.height) {
                            bestFit = item;
                        }
                    } else {
                        bestFit = item;
                    }
                }
            }
            return bestFit;
        }

        private void setCameraDisplayOrientation(int width, int height) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            getCameraInfo(cameraId, info);
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            int degrees = rotation * 90;

            int result;
            if (info.facing == CAMERA_FACING_FRONT) {
                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360;
            } else {
                result = (info.orientation - degrees + 360) % 360;
            }
            camera.setDisplayOrientation(result);

            if (result == 90 || result == 270) {
                mHolder.setFixedSize(height, width);
            } else {
                mHolder.setFixedSize(width, height);

            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            if (camera != null) {
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        }

        // On capture l'image
        public boolean capture(Camera.PictureCallback jpegHandler) {
            if (camera != null) {
                camera.takePicture(null, null, jpegHandler);
                return true;
            } else {
                return false;
            }
        }
    }

    //endregion CAMERA
}
