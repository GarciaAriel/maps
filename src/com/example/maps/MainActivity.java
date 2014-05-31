package com.example.maps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.Overlay;

import android.R.bool;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.Toast;
import android.os.Build;
import android.provider.DocumentsContract.Document;


public class MainActivity extends FragmentActivity {
	
	List<Overlay> mapOverlays;
    GeoPoint point1, point2;
    LocationManager locManager;
    Drawable drawable;
    org.w3c.dom.Document document;
    GMapV2GetRouteDirection v2GetRouteDirection;
    LatLng fromPosition;
    LatLng toPosition;
    LatLng home;
    String homeFalse = "false";
    LatLng university;
    String universityFalse= "false";
    
    ArrayList<LatLng> markerPoints;
    
    GoogleMap mGoogleMap;
    MarkerOptions markerOptions;
    Location location ;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        markerPoints = new ArrayList<LatLng>();
        
        v2GetRouteDirection = new GMapV2GetRouteDirection();
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
          mGoogleMap = supportMapFragment.getMap();

          // Enabling MyLocation in Google Map
          mGoogleMap.setMyLocationEnabled(true);
          mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
          mGoogleMap.getUiSettings().setCompassEnabled(true);
          mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
          mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
          mGoogleMap.setTrafficEnabled(true);
          mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
          markerOptions = new MarkerOptions();
          
          mGoogleMap.setOnMapClickListener(new OnMapClickListener() {
				
        	  
				@Override
				public void onMapClick(LatLng point) 
				{
					if(homeFalse == "llenar")
					{
						markerPoints.clear();
						mGoogleMap.clear();
						home = point;
						MarkerOptions options = new MarkerOptions();
						options.position(point);
						options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
						mGoogleMap.addMarker(options);
						homeFalse = "oto";
						
						ayudaServicios servicios = new ayudaServicios();
						servicios.guardarPunto("nose", "garcia", home.latitude,home.longitude);
						return;
					}
					// tam 2 o mayor LIMPIAR			
					if(markerPoints.size()>1)
					{
						markerPoints.clear();
						mGoogleMap.clear();					
					}
					// adicionar punto
					markerPoints.add(point);
					
					
					// crear MarkerOptions
					MarkerOptions options = new MarkerOptions();
					
					// ajustar la pos del marker
					options.position(point);
					
					//color marker
					if(markerPoints.size()==1)
					{
						options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
					}
					else 
						if(markerPoints.size()==2)
						{
							options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
						}
								
					// Add marker al map
					mGoogleMap.addMarker(options);
					
					// Checks, whether start and end locations are captured
					if(markerPoints.size() >= 2)
					{					
						fromPosition = markerPoints.get(0);
						toPosition = markerPoints.get(1);
						
						GetRouteTask getRoute = new GetRouteTask();
				        getRoute.execute();
					}
				}
			});
    }
    	
//000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000

    private class GetRouteTask extends AsyncTask<String, Void, String> 
    {
        
        private ProgressDialog Dialog;
        String response = "";
        @Override
        protected void onPreExecute() {
              Dialog = new ProgressDialog(MainActivity.this);
              Dialog.setMessage("Loading route...");
              Dialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
              //Get All Route values
              document = v2GetRouteDirection.getDocument(fromPosition, toPosition, GMapV2GetRouteDirection.MODE_DRIVING);
              response = "Success";
              return response;

        }

        @Override
        protected void onPostExecute(String result) {
              mGoogleMap.clear();
              if(response.equalsIgnoreCase("Success")){
              ArrayList<LatLng> directionPoint = v2GetRouteDirection.getDirection(document);
              PolylineOptions rectLine = new PolylineOptions().width(10).color(
                          Color.GREEN);

              for (int i = 0; i < directionPoint.size(); i++) {
                    rectLine.add(directionPoint.get(i));
              }
              // Adding route on the map
              mGoogleMap.addPolyline(rectLine);
              markerOptions.position(toPosition);
              markerOptions.draggable(true);
              mGoogleMap.addMarker(markerOptions);

              }
             
              Dialog.dismiss();
        }
  }  
    
  @Override
  protected void onStop() {
        super.onStop();
        finish();
  }
    
//000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
		case R.id.item1:
			Toast.makeText(getApplicationContext(), "problemas permanentes", 3000).show();
			break;
		case R.id.item2:
			Toast.makeText(getApplicationContext(), "problemas pasajeros", 3000).show();
			break;	

		default:
			break;
		}
          
        return super.onOptionsItemSelected(item);
    }
    public void onClickAddPoint(View view) {
    	if(homeFalse == "false")
    	{
    		Toast.makeText(this,"Seleccione un punto para HOME", Toast.LENGTH_LONG).show();
    		homeFalse = "llenar";
    	}
    	else
    	{
    		if (homeFalse == "oto") {
    			Toast.makeText(this,"camino a home", Toast.LENGTH_LONG).show();
    			//"nose", "garcia"
    			double latitude = mGoogleMap.getMyLocation().getLatitude();
    			double longitude = mGoogleMap.getMyLocation().getLongitude();
    			LatLng Position = new LatLng(latitude, longitude);
    			fromPosition = Position;
    			toPosition = home;
				
				GetRouteTask getRoute = new GetRouteTask();
		        getRoute.execute();
			}
    		
    	}
		
	}

}
