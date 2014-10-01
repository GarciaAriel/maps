package com.example.maps;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import android.database.CursorJoiner.Result;

import com.google.android.gms.maps.model.LatLng;

public class notifications {
	Map<LatLng,String> mapLockPoints = new LinkedHashMap<LatLng, String>();
	private double distanceToThePoint= 500; 
	
	public notifications(ArrayList<LatLng> puntos) {
		for (int i = 0; i < puntos.size(); i++) {
			mapLockPoints.put(puntos.get(i),"no");
		}
	}
	public notifications() {
		
	}
	
	public void checkNotificationsNew(ArrayList<LatLng> puntos)
	{
		if (puntos != null && puntos.size()>0) {
			for (int i = 0; i < puntos.size(); i++) {
				boolean auxiliary=true;
				for (Map.Entry<LatLng, String> entry : mapLockPoints.entrySet()) {
					if (puntos.get(i).equals(entry.getKey())) 
					{
						auxiliary = false;
						break;
					}
				}
				if (auxiliary) {
					mapLockPoints.put(puntos.get(i),"no");//add new point
				}
			}
		}
	}
	
	public ArrayList<LatLng> getNotificationNearToPoint(double latCurrent,double lonCurrent)
	{
		ArrayList<LatLng> result = new ArrayList<LatLng>();;
		if(mapLockPoints != null )
		{
			for (Map.Entry<LatLng, String> entry : mapLockPoints.entrySet()) {
				if (entry.getValue() == "no") {
					LatLng auxiliary = entry.getKey(); 
	  				double lat = auxiliary.latitude;
	  				double lon = auxiliary.longitude;
	  				double distancia = Distance(lat,lon,latCurrent,lonCurrent);
	  				
	        		if (distancia < distanceToThePoint){
	        			entry.setValue("si");
	        			result.add(auxiliary);
	        		}
				}
			}
		}
		return result;
	}
	private double Distance(double lat1,double lon1,double lat2,double lon2) {
    	
    	double R = 6371; // km
    	double o1 = Math.toRadians(lat1);
    	double o2 = Math.toRadians(lat2);
    	double tri_o = Math.toRadians(lat2-lat1); //
    	double tri_l = Math.toRadians(lon2-lon1); //

    	double a = Math.sin(tri_o/2)*Math.sin(tri_o/2)+Math.cos(o1)*Math.cos(o2)*Math.sin(tri_l/2)*Math.sin(tri_l/2);
    	double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

    	double d = R * c;
    	return (d*1000);
    }
	
}
