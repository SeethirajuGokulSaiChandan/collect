package com.geoodk.collect.android.activities;

import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.tileprovider.modules.MapTileModuleProviderBase;
import org.osmdroid.views.overlay.TilesOverlay;

import android.util.Log;

/**
 * A map tile is distributed using the observer pattern. The tile is delivered by a tile provider
 * (i.e. a descendant of {@link MapTileModuleProviderBase} or
 * {@link MapTileProviderBase} to a consumer of tiles (e.g. descendant of
 * {@link TilesOverlay}). Tiles are typically images (e.g. png or jpeg).
 */
public class MapTile {

	public static final int MAPTILE_SUCCESS_ID = 0;
	public static final int MAPTILE_FAIL_ID = MAPTILE_SUCCESS_ID + 1;
	
	private final static double ORIGIN_SHIFT = Math.PI * 6378137;

	// This class must be immutable because it's used as the key in the cache hash map
	// (ie all the fields are final).
	private final int x;
	private final int y;
	private final int zoomLevel;

	public MapTile(final int zoomLevel, final int tileX, final int tileY) {
		this.zoomLevel = zoomLevel;
		this.x = tileX;
		this.y = tileY;
	}

	public int getZoomLevel() {
		return zoomLevel;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public String toString() {
		return "/" + zoomLevel + "/" + x + "/" + y;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof MapTile))
			return false;
		final MapTile rhs = (MapTile) obj;
		return zoomLevel == rhs.zoomLevel && x == rhs.x && y == rhs.y;
	}

	@Override
	public int hashCode() {
		int code = 17;
		code *= 37 + zoomLevel;
		code *= 37 + x;
		code *= 37 + y;
		return code;
	}
	
    /**
     * WMS requires the bounding box to be defined as the point (west, south)
     * to the point (east, north).
     * 
     * @return The WMS string defining the bounding box values.
     */
    public String wmsTileCoordinates() {
        
        BoundingBox newTile = tile2boundingBox(this.x, this.y, this.zoomLevel);
        
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(newTile.west);
        stringBuffer.append(",");
        stringBuffer.append(newTile.south);
        stringBuffer.append(",");
        stringBuffer.append(newTile.east);
        stringBuffer.append(",");
        stringBuffer.append(newTile.north);
        
        return stringBuffer.toString();
        
    }
    
    /**
     * A simple class for holding the NSEW lat and lon values.
     */
    class BoundingBox {
        double north;
        double south;
        double east;
        double west;   
    }
    
    /**
     * This method converts tile xyz values to a WMS bounding box.
     * 
     * @param x    The x tile coordinate.
     * @param y    The y tile coordinate.
     * @param zoom The zoom level.
     * 
     * @return The completed bounding box.
     */
    BoundingBox tile2boundingBox(final int x, final int y, final int zoom) {
        
        Log.d("MapTile", "--------------- x = " + x);
        Log.d("MapTile", "--------------- y = " + y);
        Log.d("MapTile", "--------------- zoom = " + zoom);
        
        BoundingBox bb = new BoundingBox();
        
        bb.north = yToWgs84toEPSGLat(y, zoom);
        bb.south = yToWgs84toEPSGLat(y + 1, zoom);
        bb.west = xToWgs84toEPSGLon(x, zoom);
        bb.east = xToWgs84toEPSGLon(x + 1, zoom);
        
        return bb;
    }
    
    /**
     * Converts X tile number to EPSG value.
     * 
     * @param tileX the x tile being requested.
     * @param zoom  The current zoom level.
     * 
     * @return EPSG longitude value.
     */
    static double xToWgs84toEPSGLon(int tileX, int zoom) {
        
        // convert x tile position and zoom to wgs84 longitude
        double value = tileX / Math.pow(2.0, zoom) * 360.0 - 180;
        
        // apply the shift to get the EPSG longitude
        return value * ORIGIN_SHIFT / 180.0;
        
    }
    
    /**
     * Converts Y tile number to EPSG value.
     * 
     * @param tileY the y tile being requested.
     * @param zoom  The current zoom level.
     * 
     * @return EPSG latitude value.
     */
    static double yToWgs84toEPSGLat(int tileY, int zoom) {
        
        // convert x tile position and zoom to wgs84 latitude
        double value = Math.PI - (2.0 * Math.PI * tileY) / Math.pow(2.0, zoom);
        value = Math.toDegrees(Math.atan(Math.sinh(value)));
        
        value = Math.log(Math.tan((90 + value) * Math.PI / 360.0)) / (Math.PI / 180.0);
        
        // apply the shift to get the EPSG latitude
        return value * ORIGIN_SHIFT / 180.0;
        
    }
	
}
