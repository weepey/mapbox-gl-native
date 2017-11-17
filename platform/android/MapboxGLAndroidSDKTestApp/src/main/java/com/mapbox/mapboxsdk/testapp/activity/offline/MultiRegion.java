package com.mapbox.mapboxsdk.testapp.activity.offline;


import com.mapbox.mapboxsdk.geometry.LatLngBounds;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultiRegion implements Region {

  private static final String TAG = "TEST-OFFLINE";

  private final String name;
  private final List<Region> regions;
  private LatLngBounds bounds = null;

  public MultiRegion(List<Region> regionsList) {
    if (regionsList == null && regionsList.size() > 0) {
      regions = new ArrayList<>();
      name = "";
    } else {
      regions = new ArrayList<>(regionsList.size());
      this.name = (regionsList.get(0).getName());
      Collections.copy(regionsList, regions);
    }
  }

  public MultiRegion(String name, List<Region> regionsList) {
    this.name = name;
    regions = new ArrayList<>(regionsList == null ? 0 : regionsList.size());
    Collections.copy(regionsList, regions);
  }


  @Override
  public String getName() {
    return name;
  }

  @Override
  public void startDownload() {
    for (Region region : regions) {
      region.startDownload();
    }
  }

  @Override
  public void stopDownload() {
    for (Region region : regions) {
      region.stopDownload();
    }
  }

  @Override
  public void startTrackingStatus(final OnStatusChangeListener listener) {
    for (Region region : regions) {
      region.startTrackingStatus(new OnStatusChangeListener() {
        @Override
        public void onStatusChanged(boolean isDownloadStarted, boolean isComplete, long completedCount, long allCount, long completedSize) {

          listener.onStatusChanged(isDownloadStarted(), isComplete(), getCompletedResourceCount(), getRequiredResourceCount(), getCompletedResourceSize());
        }
      });
    }

  }

  @Override
  public void stopTrackingStatus() {
    for (Region region : regions) {
      region.stopTrackingStatus();
    }
  }

  public boolean isComplete() {
    for (Region region : regions) {
      if (!region.isComplete()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean isDownloadStarted() {
    for (Region region : regions) {
      if (region.isDownloadStarted()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public long getRequiredResourceCount() {
    long resCount = 0;
    for (Region region : regions) {
      resCount += region.getRequiredResourceCount();
    }
    return resCount;
  }

  @Override
  public long getCompletedResourceCount() {
    long resCount = 0;
    for (Region region : regions) {
      resCount += region.getCompletedResourceCount();
    }
    return resCount;
  }

  @Override
  public long getCompletedResourceSize() {
    long resCount = 0;
    for (Region region : regions) {
      resCount += region.getCompletedResourceSize();
    }
    return resCount;
  }

  @Override
  public LatLngBounds getBounds() {
    if (bounds == null) {
      LatLngBounds firstBounds = regions.get(0).getBounds();
      bounds = new LatLngBounds.Builder()
        .include(firstBounds.getNorthEast())
        .include(firstBounds.getSouthWest())
        .build();

      for(int i = 1; i < regions.size(); i++) {
        bounds.union(regions.get(i).getBounds());
      }
    }
    return bounds;
  }
}
