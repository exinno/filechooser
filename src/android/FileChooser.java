package com.cesidiodibenedetto.filechooser;

import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.io.IOException;

import org.apache.cordova.CordovaActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import android.content.Context;
import android.content.ContentResolver;
import android.webkit.MimeTypeMap;


import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaResourceApi;
import org.apache.cordova.PluginResult;

import com.ipaulpro.afilechooser.utils.FileUtils;
import io.odoc.R;

/**
 * FileChooser is a PhoneGap plugin that acts as polyfill for Android KitKat and web
 * applications that need support for <input type="file">
 * 
 */
public class FileChooser extends CordovaPlugin {
    
    private CallbackContext callbackContext = null;
    private static final String TAG = "FileChooser";
    private static final int REQUEST_CODE = 6666; // onActivityResult request code

    private void showFileChooser() {
        // Use the GET_CONTENT intent from the utility class
        Intent target = FileUtils.createGetContentIntent();
        // Create the chooser Intent
        Intent intent = Intent.createChooser(
                target, this.cordova.getActivity().getString(R.string.chooser_title));
        intent.setType("*/*");
        try {
            this.cordova.startActivityForResult((CordovaPlugin) this, intent, REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            // The reason for the existence of aFileChooser
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == REQUEST_CODE) {
                // If the file selection was successful
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        
                        Log.d(TAG, "11 fileType:" + data.getType());
                        
                        

                        
                        // Get the URI of the selected file
                        final Uri uri = data.getData();
                        Log.i(TAG, "Uri = " + uri.toString());
                        JSONObject obj = new JSONObject();
                        
                        
                        //Context context = data.getApplicationContext();
                        
                        //ContentResolver cR = uri.getContentResolver();
                        //MimeTypeMap mime = MimeTypeMap.getSingleton();
                        //String type = mime.getExtensionFromMimeType(cR.getType(uri));
                        
                        //Log.d(TAG, "22 fileType:" + type);
                        
                        try {
                            // Get the file path from the URI
                            final String path = FileUtils.getPath(this.cordova.getActivity(), uri);
                            File file = new File(path);
                            long size = 0L;
                            if (file.exists()) {
                                size = file.length();
                            }
                            else {
                                size = 0L;
                            }
                            obj.put("filepath", path);
                            obj.put("filesize", size);
                            obj.put("filetype", data.getType());
                            Log.d(TAG, "fileType:" + data.getType());
                            this.callbackContext.success(obj);
                        } catch (Exception e) {
                            Log.e("FileChooser", "File select error", e);
                            this.callbackContext.error(e.getMessage());
                        }
                    }
                }
        }
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
        if (action.equals("open")) {
            showFileChooser();
            return true;
        } else if (action.equals("isActionGetContent")) {
            try {
                Intent intent = ((CordovaActivity)this.cordova.getActivity()).getIntent();
                String intentAction = intent.getAction();
                String intentType = intent.getType();
            
                JSONObject obj = new JSONObject();
                JSONObject extra = new JSONObject();
            
                if (Intent.ACTION_GET_CONTENT.equals(intentAction) && intentType != null) {
                    Log.d(TAG, "ACTION_GET_CONTENT");
                    Log.d(TAG, "intentType : " + intentType);
                    extra.put("type", intentType);
                    obj.put("extras", extra);
                } else {
                    callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR));
                    return false;
                }
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, obj.toString()));
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
                String errorMessage=e.getMessage();
                //return new PluginResult(PluginResult.Status.JSON_EXCEPTION);
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.JSON_EXCEPTION,errorMessage));
                return false;
            }
        }
        else {
            return false;
        }
    }
}
