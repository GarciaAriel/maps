package com.example.maps;

import java.util.ArrayList;

import android.provider.DocumentsContract.Document;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class getRouteTask2 {
	
	public getRouteTask2() {
		// TODO Auto-generated constructor stub
	}
	GMapV2GetRouteDirection v2GetRouteDirection=new GMapV2GetRouteDirection();
	org.w3c.dom.Document document;
	
	public ArrayList<LatLng> get_route(LatLng fromP,LatLng toP,LatLng bloqueo)
	{
		ArrayList<LatLng> res = new ArrayList<LatLng>();
		ArrayList<LatLng> respuesta = method(fromP,toP,bloqueo);
		if (respuesta != null){
			//
			for (int i = 0; i < respuesta.size(); i++) {
				res.add(respuesta.get(i));
			}
		}
		else{
			//calculo math resta perpendicular
			double pendiente = (-1)/((fromP.longitude-toP.longitude)/(fromP.latitude-toP.latitude));
			double cons = (pendiente*(-bloqueo.latitude))+bloqueo.longitude;
			double ParaSumar= (Math.abs(fromP.latitude-toP.latitude)+Math.abs(fromP.longitude-toP.longitude))/40;
			double newX = bloqueo.latitude+ParaSumar;
			double newY = ((pendiente*newX)+cons);
			
			while((v2GetRouteDirection.CalculationByDistance(bloqueo.latitude,bloqueo.longitude,newX,newY))<170)
			{
				newX = newX+ParaSumar;
				newY = ((pendiente*newX)+cons);
			}
			LatLng medio = new LatLng(newX, newY);
			
			res.addAll(get_route(fromP, medio, bloqueo));
			res.addAll(get_route(medio, toP, bloqueo));
			
		}
		return res;
	}
	private ArrayList<LatLng> method(LatLng fromP,LatLng toP,LatLng bloqueo)
	{
		document = v2GetRouteDirection.getDocument(fromP, toP, GMapV2GetRouteDirection.MODE_DRIVING);
        ArrayList<LatLng> directionPoint;
        if(document != null)
        {
      	  directionPoint = v2GetRouteDirection.getDirection(document);
      	  
      	  int auxxx=0;
      	  if (bloqueo != null)	  {
      		  for (int i = 0; i < directionPoint.size(); i++) {
            		double distancia = v2GetRouteDirection.CalculationByDistance(bloqueo.latitude,bloqueo.longitude, directionPoint.get(i).latitude, directionPoint.get(i).longitude);
            		if (distancia < 90)	{
            			auxxx = 1;
						break;
					}
      		  }
      	  }
      	  if (auxxx == 0)  	  {
      		return directionPoint;
      	  }
        }
       return null;
	}
	
}
