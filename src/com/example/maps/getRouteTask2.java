package com.example.maps;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import android.R.integer;
import android.provider.DocumentsContract.Document;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class getRouteTask2 {
	
	public getRouteTask2() {
		// TODO Auto-generated constructor stub
	}
	GMapV2GetRouteDirection v2GetRouteDirection=new GMapV2GetRouteDirection();
	org.w3c.dom.Document document;
	ArrayList<LatLng> ContenedorTemporalPuntos = new ArrayList<LatLng>();
	
	
	
	public ArrayList<LatLng> get_route(LatLng fromP,LatLng toP,ArrayList<LatLng> puntosBloqueo)
	{
		ArrayList<LatLng> res = new ArrayList<LatLng>();
		
		//map los puntos
		Map<LatLng,String> directionPoint = new LinkedHashMap<LatLng, String>();;
		for (int i = 0; i < puntosBloqueo.size(); i++) {
			directionPoint.put(puntosBloqueo.get(i),"ok");
		}
		
		Map<LatLng,String> respuesta = method(fromP,toP,directionPoint);
		LatLng bloqueo = verificarPuntoConflicto(respuesta);
		if (bloqueo == null){
			//
			for (int i = 0; i < ContenedorTemporalPuntos.size(); i++) {
				res.add(ContenedorTemporalPuntos.get(i));
			}
		}
		else
		{
			//calculo math resta perpendicular
			LatLng newPointMedio = getPointPerpendicular(fromP,toP,bloqueo,2);
			
			//verificar si esta cerca de otro pundo de bloqueo
			int dir = 3;
			while (verificarNewPointNearBloqueo(newPointMedio,puntosBloqueo)) {
				newPointMedio = getPointPerpendicular(fromP,toP,bloqueo,dir);
				dir+=1;
			}
			
			res.addAll(get_route(fromP, newPointMedio, puntosBloqueo));
			res.addAll(get_route(newPointMedio, toP, puntosBloqueo));
			
		}
		return res;
	}
	private Map<LatLng,String> method(LatLng fromP,LatLng toP,Map<LatLng,String> puntosBloqueo)
	{
		document = v2GetRouteDirection.getDocument(fromP, toP, GMapV2GetRouteDirection.MODE_DRIVING);
        ArrayList<LatLng> directionPoint;
        Map<LatLng,String> mapPuntosBloqueo = puntosBloqueo;
        if(document != null)
        {
      	  directionPoint = v2GetRouteDirection.getDirection(document);
      	  ContenedorTemporalPuntos.clear();
      	ContenedorTemporalPuntos = directionPoint;
      	  //tengo puntos de bloqueo entro y camparo
      	  if (mapPuntosBloqueo != null || mapPuntosBloqueo.size() != 0)	  {
      		  for (int i = 0; i < directionPoint.size(); i++) {
      			  
      			 for (Map.Entry<LatLng, String> entry : mapPuntosBloqueo.entrySet()) {
      				LatLng temporal = entry.getKey(); 
      				double distancia = v2GetRouteDirection.CalculationByDistance(temporal.latitude,temporal.longitude, directionPoint.get(i).latitude, directionPoint.get(i).longitude);
            		if (distancia < 130){
            			entry.setValue("no");
            			return mapPuntosBloqueo;
					}
      			  }
              }
      	  }
      	}
       return mapPuntosBloqueo;
	}
	private LatLng verificarPuntoConflicto(Map<LatLng,String> map) {
		for (Map.Entry<LatLng, String> entry : map.entrySet()) {
			if (entry.getValue().equals("no")){
				return entry.getKey();
			}
		}
		return null;		
	}
	public boolean verificarNewPointNearBloqueo(LatLng newPointMedio,ArrayList<LatLng> puntosBloqueo)
	{
		for (int i = 0; i < puntosBloqueo.size(); i++) {
			double distancia=(v2GetRouteDirection.CalculationByDistance(newPointMedio.latitude,newPointMedio.longitude,puntosBloqueo.get(i).latitude,puntosBloqueo.get(i).longitude));
			if (distancia<120) {
				return true;
			}
		}
		return false;
	}
	public int verificarMyPosCercaCamino(LatLng newPointMedio,ArrayList<LatLng> puntosBloqueo)
	{
		for (int i = 0; i < puntosBloqueo.size(); i++) {
			double distancia=(v2GetRouteDirection.CalculationByDistance(newPointMedio.latitude,newPointMedio.longitude,puntosBloqueo.get(i).latitude,puntosBloqueo.get(i).longitude));
			if (distancia<120) {
				return i;
			}
		}
		return -1;
	}
	private LatLng getPointPerpendicular(LatLng from,LatLng to,LatLng blo,int dir)
	{
		double pendiente = (-1)/((from.longitude-to.longitude)/(from.latitude-to.latitude));
		double cons = (pendiente*(-blo.latitude))+blo.longitude;
		double ParaSumar= (Math.abs(from.latitude-to.latitude)+Math.abs(from.longitude-to.longitude))/100;
		double newX;
		if (dir%2==0) {
			newX = blo.latitude+(ParaSumar*(dir-1));	
		}
		else{
			newX = blo.latitude-(ParaSumar*(dir-1));
		}
		
		double newY = ((pendiente*newX)+cons);
		
		double distancia=(v2GetRouteDirection.CalculationByDistance(blo.latitude,blo.longitude,newX,newY));
		while(distancia<200)
		{
			newX = newX+ParaSumar;
			newY = ((pendiente*newX)+cons);
			distancia=(v2GetRouteDirection.CalculationByDistance(blo.latitude,blo.longitude,newX,newY));
		}
		LatLng medio = new LatLng(newX, newY);
		return medio;
	}
	
	
}
