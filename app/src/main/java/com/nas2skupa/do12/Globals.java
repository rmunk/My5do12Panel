package com.nas2skupa.do12;

import java.lang.reflect.Array;

import android.app.Application;

public class Globals extends Application{
	
	private String[] catSettings = new String[2];
	
	public String[] userDetails = new String[10];
	
	public void setUserDetails(String id, String uname, String name, String sname, String birth, String homeadd, String businessadd, String cell, String email, String gender){	
		userDetails[0]=id;	
		userDetails[1]=uname;	
		userDetails[2]=name;	
		userDetails[3]=sname;	
		userDetails[4]=birth;	
		userDetails[5]=homeadd;	
		userDetails[6]=businessadd;	
		userDetails[7]=cell;	
		userDetails[8]=email;	
		userDetails[9]=gender;
		
	}
	
	public String[] getUser(){
		return userDetails;		
	}
	
	
	public String[] getCatSettings(int CatId) {
		
		switch (CatId) {

		case 1:
			catSettings[0]="zdrastvene usluge";
			catSettings[1]="#7ec5c4";
			return catSettings;
		case 2:
			catSettings[0]="servis i održavanje";
			catSettings[1]="#8c7c66";
			return catSettings;
		case 3:
			catSettings[0]="ljepota";
			catSettings[1]="#f5b2b5";
			return catSettings;
		case 4:
			catSettings[0]="intelektualne usluge";
			catSettings[1]="#fad12f";
			return catSettings;
		case 5:
			catSettings[0]="dom i obitelj";
			catSettings[1]="#687f95";
			return catSettings;
		case 6:
			catSettings[0]="sport i rekreacija";
			catSettings[1]="#7dad91";
			return catSettings;
		case 7:
			catSettings[0]="turizam i ugostiteljstvo";
			catSettings[1]="#e2a217";
			return catSettings;
		case 8:
			catSettings[0]="prijevoz";
			catSettings[1]="#919294";
			return catSettings;
		case 9:
			catSettings[0]="kultura i zabava";
			catSettings[1]="#826882";
			return catSettings;

		

		default:
			return catSettings;
		}
    }

}
