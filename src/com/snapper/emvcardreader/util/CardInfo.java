package com.snapper.emvcardreader.util;

import java.io.IOException;

import android.nfc.tech.IsoDep;
import android.os.AsyncTask;
import android.util.Log;

public class CardInfo extends AsyncTask<IsoDep, Integer, CardInfo> implements EMVReader.CardReader{
	private IsoDep isoDep;
	private byte[] adfInfo;

	@Override
	public byte[] transceive(byte[] apdu) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public CardInfo(){
		
	}
	
	private CardInfo(byte[] rb){
		adfInfo = rb;
	}

	@Override
	protected CardInfo doInBackground(IsoDep... arg0) {
      try{
    	isoDep = arg0[0];
    	isoDep.connect();
    	byte[] resp = isoDep.transceive(EMVReader.SELECT_PPSE);
    	
    	if(resp != null){
    		if((resp.length == 2) && (resp[0] == 0x61)){
    			byte[] getData = new byte[]{
    					0x00, (byte) 0xC0, 0x00, 0x00, resp[1]
    			};
    			
    			resp = isoDep.transceive(getData);
    		}
    		
    		if(resp != null){
        		if((resp.length >= 2)
        				&& (resp[resp.length - 2] == (byte) 0x90)
        				&& (resp[resp.length - 1] == (byte) 0x00)){
        			CardInfo card = new CardInfo(resp);
        			
        			return card;
        		}
    		}
    	}
    }catch(Exception err){
    	Log.e("Error", err.toString());
    }
		
		return null;
	}
	
	public byte[]getADF(){
		return adfInfo;
	}

}
