package com.nas2skupa.do12;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tomislav on 20.11.2014..
 */
public class ProviderClass implements Parcelable {
    String proID ,proName, proFav,catID;
    int favIcon,akcijaIcon;
    float rating;

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(proID);
        dest.writeString(proName);
        dest.writeString(proFav);
        dest.writeString(catID);
        dest.writeInt(favIcon);
        dest.writeInt(akcijaIcon);
        dest.writeFloat(rating);
    }

    public ProviderClass(String sproId, String sproName, String sproFav, String catID, int ifavIcon, int iakcijaIcon, float frating){
        this.proID = sproId;
        this.proName = sproName;
        this.proFav = sproFav;
        this.catID = catID;
        this.favIcon = ifavIcon;
        this.akcijaIcon = iakcijaIcon;
        this.rating = frating;

    }
    private ProviderClass(Parcel in){
        this.proID = in.readString();
        this.proName = in.readString();
        this.proFav = in.readString();
        this.catID = in.readString();
        this.favIcon = in.readInt();
        this.akcijaIcon = in.readInt();
        this.rating = in.readFloat();
    }
    public static final Parcelable.Creator<ProviderClass> CREATOR = new Parcelable.Creator<ProviderClass>() {

        @Override
        public ProviderClass createFromParcel(Parcel source) {
            return new ProviderClass(source);
        }

        @Override
        public ProviderClass[] newArray(int size) {
            return new ProviderClass[size];
        }
    };
}
