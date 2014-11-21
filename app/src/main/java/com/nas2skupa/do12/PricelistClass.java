package com.nas2skupa.do12;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tomislav on 20.11.2014..
 */
public class PricelistClass implements Parcelable {
    String plID ,plName, plPrice;
    int akcijaIcon;

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(plID);
        dest.writeString(plName);
        dest.writeString(plPrice);
        dest.writeInt(akcijaIcon);
    }

    public PricelistClass(String splID, String splName, String splPrice, int iakcijaIcon){
        this.plID = splID;
        this.plName = splName;
        this.plPrice = splPrice;
        this.akcijaIcon = iakcijaIcon;

    }
    private PricelistClass(Parcel in){
        this.plID = in.readString();
        this.plName = in.readString();
        this.plPrice = in.readString();
        this.akcijaIcon = in.readInt();
    }
    public static final Creator<PricelistClass> CREATOR = new Creator<PricelistClass>() {

        @Override
        public PricelistClass createFromParcel(Parcel source) {
            return new PricelistClass(source);
        }

        @Override
        public PricelistClass[] newArray(int size) {
            return new PricelistClass[size];
        }
    };
}
