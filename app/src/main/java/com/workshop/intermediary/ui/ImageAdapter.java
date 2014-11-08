package com.workshop.intermediary.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.workshop.intermediary.R;
import com.workshop.intermediary.model.Image;

import java.util.ArrayList;


/**
 * Created by carlosmota on 20/03/14.
 */
public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Image> mImages;

    private ImageView.ScaleType[] mViewModesAvailable;
    private ImageView.ScaleType mScaleTypeSelected;

    public ImageAdapter(Context context, ArrayList<Image> images) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mImages  = images;

        mViewModesAvailable = new ImageView.ScaleType[] {ImageView.ScaleType.FIT_CENTER, ImageView.ScaleType.CENTER_CROP};
        mScaleTypeSelected = mViewModesAvailable[0];
    }

    public void switchScaleType() {
        if(mScaleTypeSelected == mViewModesAvailable[0]) {
            mScaleTypeSelected = mViewModesAvailable[1];
        } else {
            mScaleTypeSelected = mViewModesAvailable[0];
        }

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mImages.size();
    }

    @Override
    public Object getItem(int i) {
        return mImages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.listview_row, null);
        }

        Image object = mImages.get(position);

        ImageView image     = (ImageView) convertView.findViewById(R.id.iv_image);
        TextView  likes     = (TextView)  convertView.findViewById(R.id.tv_likes);
        TextView  comments  = (TextView)  convertView.findViewById(R.id.tv_comments);
        TextView  viewMode  = (TextView)  convertView.findViewById(R.id.tv_view_mode);

        image.setScaleType(mScaleTypeSelected);
        image.setImageBitmap(object.getImage());
        likes.setText(mContext.getString(R.string.image_likes, object.getLikes()));
        comments.setText(object.getComments()[0]);
        viewMode.setText(mScaleTypeSelected == mViewModesAvailable[0] ? "View mode: Fit center" :
                                                                        "View mode: Center crop");

        return convertView;
    }
}
