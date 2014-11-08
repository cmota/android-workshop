package com.workshop.intermediary.model;

import android.graphics.Bitmap;

public class Image {

    public enum ImageType {
        NEW,
        FILTER,
        PLACES
    }

    Bitmap    mImage;
    int       mLikes;
    String[]  mComments;
    ImageType mImageType;

    public Image(Bitmap image) {
        this.mImage = image;
        this.mImageType = ImageType.NEW;
    }

    public void setImage(Bitmap image) {
        this.mImage = image;
    }

    public Bitmap getImage() {
        return mImage;
    }

    public int getLikes() {
        return mLikes;
    }

    public void setLikes(int value) {
        this.mLikes = value;
    }

    public String[] getComments() {
        return mComments;
    }

    public void setComments(String[] comments) {
        this.mComments = comments;
    }

    public void setImageType(ImageType type) {
        this.mImageType = type;
    }

    public ImageType getImageType() {
        return mImageType;
    }
}
