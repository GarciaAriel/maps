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
	private int distanceToThePointOnLockPoint=200;//verifica la cercania del nuevo punto medio a los puntos de bloqueo
	private int distanceToMoveAwayThePointLock = 200;
	
	public int postList = 1;
	
	GMapV2GetRouteDirection v2GetRouteDirection=new GMapV2GetRouteDirection();
	org.w3c.dom.Document document;
	ArrayList<LatLng> ContenedorTemporalPuntos = new ArrayList<LatLng>();
	 
	
	
	public getRouteTask2() {
		// TODO Auto-generated constructor stub
	}
	
	public ArrayList<LatLng> get_route(LatLng fromP,LatLng toP,ArrayList<LatLng> puntosBloqueo)
	{
		ArrayList<LatLng> res = new ArrayList<LatLng>();
		
		//mapear los puntos de bloqueo
		Map<LatLng,String> mapLockPoints = new LinkedHashMap<LatLng, String>();
		for (int i = 0; i < puntosBloqueo.size(); i++) {
			mapLockPoints.put(puntosBloqueo.get(i),"ok");
		}
		
		//pedir un camino y verificar si hay bloqueo serca /retorna los puntos de bloqueo {si} 
		Map<LatLng,String> respuesta = getRouteVerifyPointsLock(fromP,toP,mapLockPoints);
		
		//verifica si hay algun problema [true si hay algun problema]
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
        ArrayList<LatLng> camino = new ArrayList<LatLng>();
        
        Map<LatLng,String> mapPuntosBloqueo = LockPoints;
        if(document != null)
        {
        	directionPoint = v2GetRouteDirection.getDirection(document);
        	
        	//seleccionar un solo camino
        	
        	double distanceComparar = GMapV2GetRouteDirection.CalculationByDistance(toP.latitude,toP.longitude,directionPoint.get(0).latitude,directionPoint.get(0).longitude);
        	
        	if (postList == 1) {
        		for (int i = 1; i < directionPoint.size(); i++) {
        			double distance = GMapV2GetRouteDirection.CalculationByDistance(toP.latitude,toP.longitude,directionPoint.get(i).latitude,directionPoint.get(i).longitude);
            		if (distance == distanceComparar) {
            			System.out.println("1111111111111111111111111111111111");
            			break;
    				}
            		camino.add(directionPoint.get(i));
            		
            	}
			}
        	else{
        		if (postList == 2) {
        			int i = 1;
            		for (; i < directionPoint.size(); i++) {
                		double distance = GMapV2GetRouteDirection.CalculationByDistance(toP.latitude,toP.longitude,directionPoint.get(i).latitude,directionPoint.get(i).longitude);
                		if (distance == distanceComparar) {
                			System.out.println("22222222222222222222222222222222222222 break no uno");
                			break;
        				}
                	}
            		i+=1;
            		for (; i < directionPoint.size(); i++) {
            			double distance = GMapV2GetRouteDirection.CalculationByDistance(toP.latitude,toP.longitude,directionPoint.get(i).latitude,directionPoint.get(i).longitude);
                		if (distance == distanceComparar) {
                			System.out.println("22222222222222222222222222222222222222 break cargar dos");
                			break;
        				}
                		camino.add(directionPoint.get(i));
                	}
            		
    			}
        		else{
        			if (postList == 3) {
            			int i = 1;
                		for (; i < directionPoint.size(); i++) {
                    		double distance = GMapV2GetRouteDirection.CalculationByDistance(toP.latitude,toP.longitude,directionPoint.get(i).latitude,directionPoint.get(i).longitude);
                    		if (distance == distanceComparar) {
                    			System.out.println("3333333333333333333333333333333333 break no uno");
                    			break;
            				}
                    	}
                		i+=1;
                		for (; i < directionPoint.size(); i++) {
                    		double distance = GMapV2GetRouteDirection.CalculationByDistance(toP.latitude,toP.longitude,directionPoint.get(i).latitude,directionPoint.get(i).longitude);
                    		if (distance == distanceComparar) {
                    			System.out.println("3333333333333333333333333333333333 break no dos");
                    			break;
            				}
                    	}
                		i+=1;
                		if (i<directionPoint.size()) {
                			for (; i < directionPoint.size(); i++) {
                				double distance = GMapV2GetRouteDirection.CalculationByDistance(toP.latitude,toP.longitude,directionPoint.get(i).latitude,directionPoint.get(i).longitude);
                        		if (distance == distanceComparar) {
                        			System.out.println("3333333333333333333333333333333333 break add 333");
                        			break;
                				}
                        		camino.add(directionPoint.get(i));
                			}
						}
                		else{
                			for (i = 1; i < directionPoint.size(); i++) {
                        		double distance = GMapV2GetRouteDirection.CalculationByDistance(toP.latitude,toP.longitude,directionPoint.get(i).latitude,directionPoint.get(i).longitude);
                        		if (distance == distanceComparar) {
                        			System.out.println("3333333333333333333333333333333333 break add uno");
                        			break;
                				}
                        		camino.add(directionPoint.get(i));
                        	}
                			postList = 2;
                		}
                		
        			}
        		}
        	}
        	
        	ContenedorTemporalPuntos.clear();
        	ContenedorTemporalPuntos = camino;
      	  //if -> puntos de bloqueo >=0 tengo algo que comparar
      	  if (mapPuntosBloqueo != null || mapPuntosBloqueo.size() != 0)	  {
      		  for (int i = 0; i < camino.size(); i++) {
      			  
      			 for (Map.Entry<LatLng, String> entry : mapPuntosBloqueo.entrySet()) {
      				LatLng temporal = entry.getKey(); 
      				double distancia = v2GetRouteDirection.CalculationByDistance(temporal.latitude,temporal.longitude, camino.get(i).latitude, camino.get(i).longitude);
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
	public double finall(double lat1, double long1, double lat2, double long2)
    {
        return (_bearing(lat2, long2, lat1, long1) + 180.0) % 360;
    }
    private double _bearing(double lat1, double long1, double lat2, double long2)
    {
        double degToRad = Math.PI / 180.0;
        double phi1 = lat1 * degToRad;
        double phi2 = lat2 * degToRad;
        double lam1 = long1 * degToRad;
        double lam2 = long2 * degToRad;

        return Math.atan2(Math.sin(lam2-lam1)*Math.cos(phi2),
            Math.cos(phi1)*Math.sin(phi2) - Math.sin(phi1)*Math.cos(phi2)*Math.cos(lam2-lam1)
        ) * 180/Math.PI;
    }
}
