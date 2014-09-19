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
	private int distanceToThePointOnLockRoad = 60;
	private int distanceToThePointOnLockPoint = 300;
	private int distanceToMoveAwayThePointLock = 220;
	
	
	public getRouteTask2() {
		// TODO Auto-generated constructor stub
	}
	GMapV2GetRouteDirection v2GetRouteDirection=new GMapV2GetRouteDirection();
	org.w3c.dom.Document document;
	ArrayList<LatLng> ContenedorTemporalPuntos = new ArrayList<LatLng>();
	
	
	
	public ArrayList<LatLng> get_route(LatLng fromP,LatLng toP,ArrayList<LatLng> puntosBloqueo)
	{
		ArrayList<LatLng> res = new ArrayList<LatLng>();
		
		//mapear los puntos de bloqueo
		Map<LatLng,String> mapLockPoints = new LinkedHashMap<LatLng, String>();;
		for (int i = 0; i < puntosBloqueo.size(); i++) {
			mapLockPoints.put(puntosBloqueo.get(i),"ok");
		}
		
		//pedir un camino y verificar si hay bloqueo serca 
		Map<LatLng,String> respuesta = getRouteVerifyPointsLock(fromP,toP,mapLockPoints);
		LatLng bloqueo = verificarPuntoConflicto(respuesta);
		
		//si el camino es bueno aniado los puntos
		if (bloqueo == null){
			for (int i = 0; i < ContenedorTemporalPuntos.size(); i++) {
				res.add(ContenedorTemporalPuntos.get(i));
			}
		}
		else
		{
			//calculo math resta perpendicular
			LatLng newPointMedio = getPointPerpendicular(fromP,toP,bloqueo,2);
//			System.out.println("=============================");
//			System.out.println("=============================");
//			System.out.println("Latitude: "+newPointMedio.latitude+" longitude: "+newPointMedio.longitude);
			
			//verificar si esta cerca de otro pundo de bloqueo
			int dir = 3;
			while (verificarNewPointNearBloqueo(newPointMedio,puntosBloqueo)) {
				newPointMedio = getPointPerpendicular(fromP,toP,bloqueo,dir);
				dir+=1;
			}
			System.out.println("=============================");
			System.out.println("punto nuevo:   x"+newPointMedio.latitude+" "+newPointMedio.longitude);
			
			
			res.addAll(get_route(fromP, newPointMedio, puntosBloqueo));
			res.addAll(get_route(newPointMedio, toP, puntosBloqueo));
			
		}
		return res;
	}
	private Map<LatLng,String> getRouteVerifyPointsLock(LatLng fromP,LatLng toP,Map<LatLng,String> LockPoints)
	{
		//obtener una ruta desde inicio a fin
		document = v2GetRouteDirection.getDocument(fromP, toP, GMapV2GetRouteDirection.MODE_DRIVING);
        ArrayList<LatLng> directionPoint;
        Map<LatLng,String> mapPuntosBloqueo = LockPoints;
        if(document != null)
        {
        	directionPoint = v2GetRouteDirection.getDirection(document);
        	ContenedorTemporalPuntos.clear();
        	ContenedorTemporalPuntos = directionPoint;
      	  //if -> puntos de bloqueo >=0 tengo algo que comparar
      	  if (mapPuntosBloqueo != null || mapPuntosBloqueo.size() != 0)	  {
      		  for (int i = 0; i < directionPoint.size(); i++) {
      			  
      			 for (Map.Entry<LatLng, String> entry : mapPuntosBloqueo.entrySet()) {
      				LatLng temporal = entry.getKey(); 
      				double distancia = v2GetRouteDirection.CalculationByDistance(temporal.latitude,temporal.longitude, directionPoint.get(i).latitude, directionPoint.get(i).longitude);
            		if (distancia < distanceToThePointOnLockRoad){
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
			if (distancia<distanceToThePointOnLockPoint) {
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
		while(distancia<distanceToMoveAwayThePointLock)
		{
			if (dir%2==0) {
				newX = newX+ParaSumar;// blo.latitude+(ParaSumar*(dir-1));
			}
			else{
				newX = newX-ParaSumar;// blo.latitude-(ParaSumar*(dir-1));
			}
			//newX = newX+ParaSumar;
			newY = ((pendiente*newX)+cons);
			distancia=(v2GetRouteDirection.CalculationByDistance(blo.latitude,blo.longitude,newX,newY));
		}
		LatLng medio = new LatLng(newX, newY);
		return medio;
	}
}
