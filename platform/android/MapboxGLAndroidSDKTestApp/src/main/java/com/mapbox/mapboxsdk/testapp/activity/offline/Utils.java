package com.mapbox.mapboxsdk.testapp.activity.offline;

import android.util.Log;

import com.mapbox.mapboxsdk.offline.OfflineRegion;

import org.json.JSONObject;


public class Utils {
  // JSON encoding/decoding
  static final String JSON_CHARSET = "UTF-8";
  static final String JSON_FIELD_REGION_NAME = "FIELD_REGION_NAME";

  public static String getRegionNameFromMetaData(OfflineRegion offlineRegion) {
    // Get the region name from the offline region metadata
    String regionName;

    try {
      byte[] metadata = offlineRegion.getMetadata();
      String json = new String(metadata, JSON_CHARSET);
      JSONObject jsonObject = new JSONObject(json);
      regionName = jsonObject.getString(JSON_FIELD_REGION_NAME);
    } catch (Exception exception) {
      Log.e(">>>", "Failed to decode metadata: " + exception.getMessage());
      regionName = "SingleRegion id:" +offlineRegion.getID();
    }
    return regionName;
  }

  public static byte[] createMetaData(String regionName) {
    // Build a JSONObject using the user-defined offline region title,
    // convert it into string, and use it to create a metadata variable.
    // The metadata variable will later be passed to createOfflineRegion()
    byte[] metadata;
    try {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put(JSON_FIELD_REGION_NAME, regionName);
      String json = jsonObject.toString();
      metadata = json.getBytes(JSON_CHARSET);
    } catch (Exception exception) {
      Log.e(">>>>>>", "Failed to encode metadata: " + exception.getMessage());
      metadata = null;

    }
    return metadata;
  }
}
