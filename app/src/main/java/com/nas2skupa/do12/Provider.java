package com.nas2skupa.do12;

public class Provider {
    public int favIcon;
    public int akcijaIcon;
    public String title;
    public String proID;
    public Provider(){
        super();
    }
   
    public Provider(int favIcon, String title, String proID, int akcijaIcon) {
        super();
        this.favIcon = favIcon;
        this.akcijaIcon = akcijaIcon;
        this.title = title;
        this.proID = proID;
    }
}