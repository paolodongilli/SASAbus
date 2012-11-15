/**
 *
 * ScanCodeActivity.java
 * 
 * 
 * Copyright (C) 2012 Markus Windegger
 *
 * This file is part of SasaBus.

 * SasaBus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SasaBus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SasaBus.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package it.sasabz.android.sasabus;



import java.util.Iterator;

import it.sasabz.android.sasabus.classes.CameraPreview;
import it.sasabz.android.sasabus.classes.Palina;
import it.sasabz.android.sasabus.classes.PalinaList;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.Button;
import android.widget.Toast;

import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;

import android.widget.TextView;
import android.graphics.ImageFormat;

/* Import ZBar Class files */
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import net.sourceforge.zbar.Config;

public class ScanCodeActivity extends Activity
{
    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;


    ImageScanner scanner;

    private boolean previewing = true;
    
    private boolean barcodeScanned = false;

    static {
        System.loadLibrary("iconv");
    } 

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.standard_imagescan_layout);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        autoFocusHandler = new Handler();
        mCamera = getCameraInstance();

        /* Instance barcode scanner */
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
        FrameLayout preview = (FrameLayout)findViewById(R.id.cameraPreview);
        preview.addView(mPreview);

    }

    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e){
        }
        return c;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
            public void run() {
                if (previewing)
                    mCamera.autoFocus(autoFocusCB);
            }
        };

    private Context getContext()
    {
    	return this.getApplicationContext();
    }
        
        
    private PreviewCallback previewCb = new PreviewCallback() {
            public void onPreviewFrame(byte[] data, Camera camera) {
                Camera.Parameters parameters = camera.getParameters();
                Size size = parameters.getPreviewSize();

                Image barcode = new Image(size.width, size.height, "Y800");
                barcode.setData(data);

                int result = scanner.scanImage(barcode);
                
                if (result != 0) {
                    previewing = false;
                    mCamera.setPreviewCallback(null);
                    mCamera.stopPreview();
                    
                    SymbolSet syms = scanner.getResults();
                    
                    Iterator<Symbol> symbol = syms.iterator();
                    String last_data = "";
                    while(symbol.hasNext())
                    {
                    	barcodeScanned = true;
                    	Symbol item = symbol.next();
                    	last_data = item.getData();
                    }
                    Log.v("QRCODE", "DATA READ: " + last_data);
                    if(last_data.equals(""))
                    {
                    	Toast.makeText(getContext(), R.string.error_scan_text, Toast.LENGTH_LONG).show();
                    	previewing = true;
                        mCamera.setPreviewCallback(previewCb);
                        mCamera.startPreview();
                        barcodeScanned = false;
                    	
                    }
                    else if(last_data.indexOf("busstop") != -1 || last_data.indexOf("BUSSTOP") != -1)
                    {
                    	Log.v("QRCODE", "IN BUSSTOP");
                    	int start = last_data.indexOf("busstop");
                    	if(start == -1)
                    		start = last_data.indexOf("BUSSTOP"); 
                    	String busstopnr = last_data.substring(start + 8);
                    	int stop = busstopnr.indexOf("&");
                    	if(stop != -1)
                    		busstopnr = busstopnr.substring(0, stop);
                    	else
                    		busstopnr = busstopnr.substring(0);
                    	Palina partenza = PalinaList.getById(Integer.parseInt(busstopnr));
                    	if(partenza != null)
                    	{
                    		finish();
                    		//Intent selDest = new Intent(getContext(), SelectDestinazioneLocationActivity.class);
                    		//selDest.putExtra("partenza", partenza.getName_de());
                    		//startActivity(selDest);
                    	}
                    	else
                    	{
                    		Toast.makeText(getContext(), R.string.error_scan_text, Toast.LENGTH_LONG).show();
                    		previewing = true;
	                        mCamera.setPreviewCallback(previewCb);
	                        mCamera.startPreview();
	                        barcodeScanned = false;
                    	}
                    }
                    else if(last_data.indexOf("#") != -1)
                    {
                    	String data_id = last_data.substring(last_data.indexOf("#")+1);
                    	String daten[] = data_id.split("&");
                    	if(daten.length >= 2)
                    	{
                    		int index_stop = -1;
                    		int index_next = -1;
                    		int index_city = -1;
                    		for(int i = 0; i < daten.length; ++i)
                    		{
                    			if(daten[i].indexOf("stop=") != -1)
                    			{
                    				index_stop = i;
                    				daten[i] = daten[i].substring(5);
                    			}
                    			else if(daten[i].indexOf("next=") != -1)
                    			{
                    				index_next = i;
                    				daten[i] = daten[i].substring(5);
                    			}
                    			else if(daten[i].indexOf("city=") != -1)
                    			{
                    				index_city = i;
                    				daten[i] = daten[i].substring(5);
                    			}
                    		}
                    		Log.v("QRCODEREADER", "stop=" + daten[index_stop] + " | next=" + daten[index_next]);
                    		String name_de = daten[index_stop].substring(daten[index_stop].indexOf("-") + 1);
                    		Log.v("QRCODEREADER", "Gelesener name_de: " + name_de);
                    		Palina partenza = null;
                    		if (index_city == -1)
                    		{
                    			partenza = PalinaList.getTranslation(name_de, "de");
                    		}
                    		else
                    		{
                    			partenza = PalinaList.getTranslation(name_de, "de", daten[index_city]);
                    		}
                        	if(partenza != null)
                        	{
                        		finish();
                        		//Intent selDest = new Intent(getContext(), SelectDestinazioneLocationActivity.class);
                        		//selDest.putExtra("partenza", partenza.getName_de());
                        		//startActivity(selDest);
                        	}
                        	else
                        	{
                        		Toast.makeText(getContext(), R.string.error_scan_text, Toast.LENGTH_LONG).show();
                        		previewing = true;
    	                        mCamera.setPreviewCallback(previewCb);
    	                        mCamera.startPreview();
    	                        barcodeScanned = false;
                        	}
                        	/*
                    		Toast.makeText(getContext(), R.string.scan_text_not_implemented, Toast.LENGTH_LONG).show();
                    		previewing = true;
	                        mCamera.setPreviewCallback(previewCb);
	                        mCamera.startPreview();
	                        barcodeScanned = false;
	                        */
                    	}
                    	else
                    	{
                    		Toast.makeText(getContext(), R.string.error_scan_text, Toast.LENGTH_LONG).show();
                    		previewing = true;
	                        mCamera.setPreviewCallback(previewCb);
	                        mCamera.startPreview();
	                        barcodeScanned = false;
                    	}
                    }
                    else
                    {
                    	Toast.makeText(getContext(), R.string.error_scan_text, Toast.LENGTH_LONG).show();
                    	previewing = true;
                        mCamera.setPreviewCallback(previewCb);
                        mCamera.startPreview();
                        barcodeScanned = false;
                    }
                   
                }
            }
        };

    // Mimic continuous auto-focusing
    AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
            public void onAutoFocus(boolean success, Camera camera) {
                autoFocusHandler.postDelayed(doAutoFocus, 1000);
            }
        };
}
