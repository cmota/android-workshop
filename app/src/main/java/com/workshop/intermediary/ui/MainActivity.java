package com.workshop.intermediary.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.workshop.intermediary.com.workshop.intermediary.utils.ImageUtils;
import com.workshop.intermediary.R;
import com.workshop.intermediary.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.GPUImagePixelationFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSepiaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageVignetteFilter;


public class MainActivity extends Activity {

    final int RESULT_OPEN_CAMERA   = 100;
    final int RESULT_OPEN_GALLERY  = 200;

    private ArrayList<Image> mImages;
    private Uri mImageUri;

    private ImageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();

        setContentView(R.layout.activity_main);

        setUIComponents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_take_picture:
                openCamera();
                return true;
            case R.id.action_gallery:
                openGallery();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_OPEN_CAMERA) {
            if(resultCode != RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Unable to retrieve image from camera", Toast.LENGTH_SHORT).show();
                return;
            }

            Image image = new Image(ImageUtils.decodeSampledBitmapFromPath(mImageUri.getPath(), 300, 300));
            image.setLikes(new Random().nextInt(100));
            image.setComments(new String[]{"No comments"});
            image.setImageType(Image.ImageType.NEW);

            mImages.add(0, image);
            mAdapter.notifyDataSetChanged();

            updateViewsVisibility();

        } else if (requestCode == RESULT_OPEN_GALLERY) {
            if(resultCode != RESULT_OK || data == null) {
                Toast.makeText(getApplicationContext(), "Unable to retrieve image from gallery", Toast.LENGTH_SHORT).show();
                return;
            }

            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            cursor.close();

            try {
                Image image = new Image(ImageUtils.scaleImage(getApplicationContext(), selectedImage, 300, 300));
                image.setLikes(new Random().nextInt(100));
                image.setComments(new String[]{"No comments"});
                image.setImageType(Image.ImageType.NEW);

                mImages.add(0, image);
                mAdapter.notifyDataSetChanged();

                updateViewsVisibility();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Unable to load image from gallery", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    private void openCamera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

        File photo = new File(Environment.getExternalStorageDirectory(),"image_"+String.valueOf(System.currentTimeMillis())+".jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
        mImageUri = Uri.fromFile(photo);

        startActivityForResult(intent, RESULT_OPEN_CAMERA);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_OPEN_GALLERY);
    }

    private void setUIComponents(){
        mImages  = new ArrayList<Image>();
        mAdapter = new ImageAdapter(getApplicationContext(), mImages);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                displayRowOptionsDialog(position);
            }
        });
    }

    private void updateViewsVisibility(){
        TextView noItems = (TextView) findViewById(R.id.text_no_items);
        ListView listView = (ListView) findViewById(R.id.listView);

        if(mImages.isEmpty()) {
            noItems.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            noItems.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    private void displayRowOptionsDialog(final int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Options");
        builder.setItems(new String[]{"Apply filter: Vignette",
                                      "Apply filter: Grayscale",
                                      "Apply filter: Pixelation"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position) {
                switch(position) {
                    //TODO add missing options
                    case 0:
                        PointF centerPoint = new PointF();
                        centerPoint.x = 0.5f;
                        centerPoint.y = 0.5f;
                        applyFilter(index, new GPUImageVignetteFilter(centerPoint, new float[] {0.0f, 0.0f, 0.0f}, 0.3f, 0.75f));
                        break;
                    case 1:
                        applyFilter(index, new GPUImageGrayscaleFilter());
                        break;
                    case 2:
                        applyFilter(index, new GPUImagePixelationFilter());
                        break;
                    default:
                        break;
                }
            }
        });
        builder.show();
    }

    private void applyFilter(int index, GPUImageFilter filter) {
        Image imageFilter = mImages.get(index);

        GPUImage gpuImage = new GPUImage(getApplicationContext());
        gpuImage.setFilter(filter);
        gpuImage.setImage(mImages.get(index).getImage());
        imageFilter.setImage(gpuImage.getBitmapWithFilterApplied());
        imageFilter.setImageType(Image.ImageType.FILTER);

        mImages.set(index, imageFilter);
        mAdapter.notifyDataSetChanged();
    }
}
