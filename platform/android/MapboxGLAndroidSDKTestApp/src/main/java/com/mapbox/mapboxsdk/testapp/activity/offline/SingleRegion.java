package com.mapbox.mapboxsdk.testapp.activity.offline;

import android.util.Log;

import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;

import java.util.concurrent.TimeUnit;

public class SingleRegion implements Region {

  private static final String TAG = "TEST-OFFLINE";

  private final String name;
  private final OfflineRegion offlineRegion;
  private OfflineRegionStatus lastReportedStatus = null;
  private OnStatusChangeListener listener = null;

  public SingleRegion(OfflineRegion offlineRegion) {
    this.name = Utils.getRegionNameFromMetaData(offlineRegion);
    this.offlineRegion = offlineRegion;
  }

  public SingleRegion(String name, OfflineRegion offlineRegion) {
    this.name = name;
    this.offlineRegion = offlineRegion;
  }

  @Override
  public  String getName() {
    return name;
  }

  @Override
  public void startDownload() {
    offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);
    startMeasureDownload();
  }

  @Override
  public void stopDownload() {
    offlineRegion.setDownloadState(OfflineRegion.STATE_INACTIVE);
    stopMeasuringDownload();
  }

  @Override
  public void startTrackingStatus(final OnStatusChangeListener listener) {

    Log.d(TAG, ">>>>> Start Tracking name=" +name);
    this.listener = listener;
    // Get observing offline region's status.
   offlineRegion.getStatus(new OfflineRegion.OfflineRegionStatusCallback() {
      @Override
      public void onStatus(OfflineRegionStatus status) {

          lastReportedStatus = status;

        if (SingleRegion.this.listener != null) {
          SingleRegion.this.listener.onStatusChanged(status.getDownloadState() == OfflineRegion.STATE_ACTIVE,
            status.isComplete(), status.getCompletedResourceCount(), status.getRequiredResourceCount(),
            status.getCompletedResourceSize());
        }
      }

      @Override
      public void onError(String error) {
        Log.d(TAG, " Errro  adding region : "+ error);
      }
    });

    //Start observing offline region's status
    offlineRegion.setObserver(new OfflineRegion.OfflineRegionObserver() {
      @Override
      public void onStatusChanged(OfflineRegionStatus status) {

        lastReportedStatus = status;

        // Stop measuring downlaod !
        if (status.isComplete()) {
          stopMeasuringDownload();
        }

        if (SingleRegion.this.listener != null) {
          SingleRegion.this.listener.onStatusChanged(status.getDownloadState() == OfflineRegion.STATE_ACTIVE,
            status.isComplete(), status.getCompletedResourceCount(), status.getRequiredResourceCount(),
            status.getCompletedResourceSize());
        }
      }

      @Override
      public void onError(OfflineRegionError error) {
        Log.d(TAG, ">>>>> OfflineRegionError : reason=" +error.getReason() +" msg=" +error.getMessage());

      }

      @Override
      public void mapboxTileCountLimitExceeded(long limit) {
        Log.d(TAG, ">>>>> mapboxTileCountLimitExceeded " + limit);

      }
    });
  }

  @Override
  public void stopTrackingStatus() {
    Log.d(TAG, ">>>>> STOP Tracking name=" +name);
    this.listener = null;
    offlineRegion.setObserver(null);
  }

  @Override
  public LatLngBounds getBounds() {
    return offlineRegion.getDefinition().getBounds();
  }


  @Override
  public boolean isComplete() {
    if (lastReportedStatus != null) {
      return lastReportedStatus.isComplete();
    }

    return false;
  }

  @Override
  public boolean isDownloadStarted() {
    if (lastReportedStatus != null) {
      return lastReportedStatus.getDownloadState() == OfflineRegion.STATE_ACTIVE;
    }

    return false;
  }

  @Override
  public long getRequiredResourceCount() {
    return lastReportedStatus == null ? 0 : lastReportedStatus.getRequiredResourceCount();
  }

  @Override
  public long getCompletedResourceCount() {
    return lastReportedStatus == null ? 0 : lastReportedStatus.getCompletedResourceCount();
  }

  @Override
  public long getCompletedResourceSize() {
    return lastReportedStatus == null ? 0 : lastReportedStatus.getCompletedResourceSize();
  }


  private long time = 0;
  private void startMeasureDownload() {
    time = System.currentTimeMillis();
  }

  private void stopMeasuringDownload() {
    if (time > 0) {
      time = System.currentTimeMillis() - time;
    }
    Log.d("TEST-OFFLINE", " >>>>> It took " +
      TimeUnit.MILLISECONDS.toMinutes(time) + " minutes to load " + name + " map");
    time = 0;
  }
}
