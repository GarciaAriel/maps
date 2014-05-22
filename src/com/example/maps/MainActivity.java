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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
          //fromPosition = new LatLng(-17.433737, -66.160973);
          //toPosition = new LatLng(-17.438057, -66.159256);
          //GetRouteTask getRoute = new GetRouteTask();
          //getRoute.execute();
          
          
          mGoogleMap.setOnMapClickListener(new OnMapClickListener() {
				
				@Override
				public void onMapClick(LatLng point) 
				{
					// Already two locations				
					if(markerPoints.size()>1)
					{
						markerPoints.clear();
						mGoogleMap.clear();					
					}
					
					// Adding new item to the ArrayList
					markerPoints.add(point);				
					
					// Creating MarkerOptions
					MarkerOptions options = new MarkerOptions();
					
					// Setting the position of the marker
					options.position(point);
					
					/** 
					 * For the start location, the color of marker is GREEN and
					 * for the end location, the color of marker is RED.
					 */
					if(markerPoints.size()==1)
					{
						options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
					}
					else 
						if(markerPoints.size()==2)
						{
							options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
						}
								
					
					// Add new marker to the Google Map Android API V2
					mGoogleMap.addMarker(options);
					
					// Checks, whether start and end locations are captured
					if(markerPoints.size() >= 2){					
						fromPosition = markerPoints.get(0);
						toPosition = markerPoints.get(1);
						
						GetRouteTask getRoute = new GetRouteTask();
				        getRoute.execute();
						
						// Getting URL to the Google Directions API
							//String url = getDirectionsUrl(origin, dest);				
						
							//DownloadTask downloadTask = new DownloadTask();
						
						// Start downloading json data from Google Directions API
							//downloadTask.execute(url);
					}
					
				}
			});
          
          
    }
    
 		
//000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000

    private class GetRouteTask extends AsyncTask<String, Void, String> {
        
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
