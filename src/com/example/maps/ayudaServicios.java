package com.example.maps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

import android.R.string;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class ayudaServicios {
	String url = "http://192.168.100.116:8080/com.maps/sample/puntos";
	
	public JSONArray guardarPunto(String tipo,String usuario,double latitude, double longitude) {
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		HttpClient httpclient = new DefaultHttpClient();
		String complementoURL = url+"/setPoint/"+tipo+"/"+usuario+"/"+latitude+"/"+longitude;
		JSONArray jsonArray = null;
		HttpGet httppost = new HttpGet(complementoURL);
		try 
		{
			HttpResponse response = httpclient.execute(httppost);
			String jsonResult = inputStreamToString(
					response.getEntity().getContent()).toString();

			jsonArray = new JSONArray(jsonResult);
			return jsonArray;
		} 
		catch (ClientProtocolException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonArray; 
	}
	
	
	public JSONArray guardarPuntoBloqueo(String tipo,String codigo,double latitude, double longitude) {
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		HttpClient httpclient = new DefaultHttpClient();
		String complementoURL = url+"/setLockPoint/"+tipo+"/"+codigo+"/"+latitude+"/"+longitude;
		JSONArray jsonArray = null;
		HttpGet httppost = new HttpGet(complementoURL);
		try 
		{
			HttpResponse response = httpclient.execute(httppost);
			String jsonResult = inputStreamToString(
					response.getEntity().getContent()).toString();

			jsonArray = new JSONArray(jsonResult);
			return jsonArray;
		} 
		catch (ClientProtocolException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonArray; 
	}
	
public JSONArray guardarPuntoAlerta(String tipo,String codigo,double latitude, double longitude) {
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		HttpClient httpclient = new DefaultHttpClient();
		String complementoURL = url+"/setAlertPoint/"+tipo+"/"+codigo+"/"+latitude+"/"+longitude;
		JSONArray jsonArray = null;
		HttpGet httppost = new HttpGet(complementoURL);
		try 
		{
			HttpResponse response = httpclient.execute(httppost);
			String jsonResult = inputStreamToString(
					response.getEntity().getContent()).toString();

			jsonArray = new JSONArray(jsonResult);
			return jsonArray;
		} 
		catch (ClientProtocolException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonArray; 
	}
	
	public JSONArray getPunto(String tipo,String usuario) {
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		HttpClient httpclient = new DefaultHttpClient();
		String complementoURL = url+"/getPoint/"+tipo+"/"+usuario;
		JSONArray jsonArray = null;
		HttpGet httppost = new HttpGet(complementoURL);
		try 
		{
			HttpResponse response = httpclient.execute(httppost);
			String jsonResult = inputStreamToString(
					response.getEntity().getContent()).toString();

			jsonArray = new JSONArray(jsonResult);
			return jsonArray;
		}
		catch (ClientProtocolException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonArray; 
	}
	//private static Map<String, puntosGeoDatosUsuario> mapPuntosGeoDatosUsuario = new LinkedHashMap<String, puntosGeoDatosUsuario>();
public Map<LatLng, String> getPuntosBloqueoPersistente() {
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		HttpClient httpclient = new DefaultHttpClient();
		String complementoURL = url+"/getPersistentLockPoints";
		JSONArray jsonArray = null;
		HttpGet httppost = new HttpGet(complementoURL);
		
		Map<LatLng, String> result = new LinkedHashMap<LatLng, String>();
		try 
		{
			HttpResponse response = httpclient.execute(httppost);
			String jsonResult = inputStreamToString(
					response.getEntity().getContent()).toString();

			jsonArray = new JSONArray(jsonResult);
			for(int i = 0 ; i<jsonArray.length() ; i++)
			{
				 JSONObject json_data = jsonArray.getJSONObject(i);
				 double lat = json_data.getDouble("latitude");
				 double lon = json_data.getDouble("longitude");
				 String tip = json_data.getString("tipo");
				 LatLng punto = new LatLng(lat, lon);
				 
				 result.put(punto, tip);
			}
			return result;
		} 
		catch (ClientProtocolException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return null; 
	}
public Map<LatLng, String> getPuntosPosiblesBloqueoPersistente() {
	
	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	StrictMode.setThreadPolicy(policy);
	HttpClient httpclient = new DefaultHttpClient();
	String complementoURL = url+"/getPersistentPossiblePoints";
	JSONArray jsonArray = null;
	HttpGet httppost = new HttpGet(complementoURL);
	
	Map<LatLng, String> result = new LinkedHashMap<LatLng, String>();
	try 
	{
		HttpResponse response = httpclient.execute(httppost);
		String jsonResult = inputStreamToString(
				response.getEntity().getContent()).toString();

		jsonArray = new JSONArray(jsonResult);
		for(int i = 0 ; i<jsonArray.length() ; i++)
		{
			 JSONObject json_data = jsonArray.getJSONObject(i);
			 double lat = json_data.getDouble("latitude");
			 double lon = json_data.getDouble("longitude");
			 String tip = json_data.getString("tipo");
			 LatLng punto = new LatLng(lat, lon);
			 
			 result.put(punto, tip);
		}
		return result;
	} 
	catch (ClientProtocolException e) 
	{
		e.printStackTrace();
	} 
	catch (IOException e) 
	{
		e.printStackTrace();
	} 
	catch (JSONException e) 
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	
	
	return null; 
}
public Map<LatLng, String> getPuntosAlerta() {
	
	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	StrictMode.setThreadPolicy(policy);
	HttpClient httpclient = new DefaultHttpClient();
	String complementoURL = url+"/getAlertLockPoints";
	JSONArray jsonArray = null;
	HttpGet httppost = new HttpGet(complementoURL);
	
	Map<LatLng, String> result = new LinkedHashMap<LatLng, String>();
	try 
	{
		HttpResponse response = httpclient.execute(httppost);
		String jsonResult = inputStreamToString(
				response.getEntity().getContent()).toString();

		jsonArray = new JSONArray(jsonResult);
		for(int i = 0 ; i<jsonArray.length() ; i++)
		{
			 JSONObject json_data = jsonArray.getJSONObject(i);
			 double lat = json_data.getDouble("latitude");
			 double lon = json_data.getDouble("longitude");
			 String tip = json_data.getString("tipo");
			 LatLng punto = new LatLng(lat, lon);
			 
			 result.put(punto, tip);
		}
		return result;
	} 
	catch (ClientProtocolException e) 
	{
		e.printStackTrace();
	} 
	catch (IOException e) 
	{
		e.printStackTrace();
	} 
	catch (JSONException e) 
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null; 
}
	private StringBuilder inputStreamToString(InputStream is) {
		String rLine = "";
		StringBuilder answer = new StringBuilder();

		InputStreamReader isr = new InputStreamReader(is);

		BufferedReader rd = new BufferedReader(isr);

		try 
		{
			while ((rLine = rd.readLine()) != null) 
			{
				answer.append(rLine);
			}
		}

		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return answer;
	}
}
