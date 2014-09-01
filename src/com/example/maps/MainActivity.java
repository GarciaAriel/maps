package com.example.maps;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;















import org.json.JSONArray;
import org.json.JSONException;

import com.entropy.slidingmenu2.fragment.FragmentListView;
import com.entropy.slidingmenu2.fragment.FragmentMain;
import com.entropy.slidingmenu2.layout.MainLayout;
import com.google.android.gms.internal.bl;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.Overlay;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class MainActivity extends FragmentActivity 
{
	
	// The MainLayout which will hold both the sliding menu and our main content
    // Main content will holds our Fragment respectively
    MainLayout mainLayout;
    
    // ListView menu
    private ListView lvMenu;
    private String[] lvMenuItems;
    
    // Menu button
    Button btMenu;
    
    // Title according to fragment
    TextView tvTitle;
	//=============================================
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
    LatLng work;
    String workFalse= "false";
    LatLng bloqueo;
    String bloqueoFalse= "false";
    String ok = "0";
    PolylineOptions rectLine = new PolylineOptions().width(10).color(Color.GREEN);
    String codigo_usuario="";
    
    ArrayList<LatLng> markerPoints;
    
    GoogleMap mGoogleMap;
    MarkerOptions markerOptions;
    Location location ;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
    	WifiManager wifiMan = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInf = wifiMan.getConnectionInfo();
		codigo_usuario = wifiInf.getMacAddress();
    	
    	
        super.onCreate(savedInstanceState);
        mainLayout = (MainLayout)this.getLayoutInflater().inflate(R.layout.activity_main, null);//new
        setContentView(mainLayout);//new
        //setContentView(R.layout.activity_main1); 
        
     // Init menu
        lvMenuItems = getResources().getStringArray(R.array.menu_items);
        lvMenu = (ListView) findViewById(R.id.activity_main_menu_listview);
        lvMenu.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, lvMenuItems));
        lvMenu.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
					onMenuItemClick(parent, view, position, id);
				} catch (JSONException e) {
					
					e.printStackTrace();
				}
            }
            
        });
        
     // Get menu button
        btMenu = (Button) findViewById(R.id.activity_main_content_button_menu);
        btMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show/hide the menu
                toggleMenu(v);
            }
        });
        
        // Get title textview
        tvTitle = (TextView) findViewById(R.id.activity_main_content_title);
        
        
        // Add FragmentMain as the initial fragment       
        FragmentManager fm = MainActivity.this.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
//        
//        FragmentMain fragment = new FragmentMain();
//        ft.add(R.id.activity_main_content_fragment, fragment);
//        ft.commit();
        
      //================================================
        
        
        markerPoints = new ArrayList<LatLng>();
//        
        v2GetRouteDirection = new GMapV2GetRouteDirection();
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
          mGoogleMap = supportMapFragment.getMap();
          

          //map = new google.maps.Map2(document.getElementById("map_canvas"));
          CameraUpdate center=CameraUpdateFactory.newLatLng(new LatLng(-17.393792, -66.157110));
          CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);
          
          mGoogleMap.moveCamera(center);
          mGoogleMap.animateCamera(zoom);
          
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
						servicios.guardarPunto("casa", codigo_usuario, home.latitude,home.longitude);
						return;
					}
					if(workFalse == "llenar")
					{
						markerPoints.clear();
						mGoogleMap.clear();
						work = point;
						MarkerOptions options = new MarkerOptions();
						options.position(point);
						options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
						mGoogleMap.addMarker(options);
						workFalse = "oto";
						
						ayudaServicios servicios = new ayudaServicios();
						servicios.guardarPunto("trabajo", codigo_usuario, work.latitude,work.longitude);
						return;
					}
					if(bloqueoFalse == "llenar")
					{
						markerPoints.clear();
						mGoogleMap.clear();
						bloqueo = point;
						MarkerOptions options = new MarkerOptions();
						options.position(point);
						options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
						mGoogleMap.addMarker(options);
						bloqueoFalse = "false";
						
						ayudaServicios servicios = new ayudaServicios();
						servicios.guardarPunto("bloque", codigo_usuario, bloqueo.latitude,bloqueo.longitude);
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
					if(markerPoints.size()==1){
						options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
					}
					else{ 
						if(markerPoints.size()==2){
							options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
						}
					}
						// Add marker al map
					mGoogleMap.addMarker(options);
					
					// Checks, whether start and end locations are captured
					if(markerPoints.size() >= 2)
					{					
						fromPosition = markerPoints.get(0);
						toPosition = markerPoints.get(1);
						
						ayudaServicios servicios = new ayudaServicios();
			        	JSONArray puntoBloqueoJson = servicios.getPunto("bloque", codigo_usuario);
			        	
			        	if (puntoBloqueoJson != null) {
			        		try {
			        			double lat = puntoBloqueoJson.getJSONObject(0).getDouble("latitude");
				        		double lon = puntoBloqueoJson.getJSONObject(0).getDouble("longitude");
				        		bloqueo = new LatLng(lat, lon);
				        	} catch (Exception e) {
				        		System.out.println("en captura de 2 puntos y call punto bloqueo"); 
							}
			        	}
			        	HelpRute help = new HelpRute();
			        	help.execute();
			        }
				}
			});
          
    }
    	
//000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000

    private class HelpRute extends AsyncTask<String, Void, String>
    {
    	private ProgressDialog Dialog;
        String response = "";
        @Override
        protected void onPreExecute() {
        	  rectLine = new PolylineOptions().width(10).color(Color.GREEN);
        	
              Dialog = new ProgressDialog(MainActivity.this);
              Dialog.setMessage("Espere un momento");
              Dialog.show();
        }

		@Override
		protected String doInBackground(String... params) {
			getRouteTask2 getRoute = new getRouteTask2();
			ArrayList<LatLng> listPoint = getRoute.get_route(fromPosition,toPosition,bloqueo);
			if(listPoint!=null)
			{
				for (int i = 0; i < listPoint.size(); i++) {
					rectLine.add(listPoint.get(i));
				}
			}
					
			return null;
		}
		@Override
        protected void onPostExecute(String result) {
			mGoogleMap.addPolyline(rectLine);
	  	  	markerOptions.position(toPosition);
	  	  	markerOptions.draggable(true);
	  	  	mGoogleMap.addMarker(markerOptions);
	  	  Dialog.dismiss();
		}
	}
    private class GetRouteTasksss extends AsyncTask<String, Void, String> 
    {
        
        private ProgressDialog Dialog;
        String response = "";
        @Override
        protected void onPreExecute() {
              Dialog = new ProgressDialog(MainActivity.this);
              Dialog.setMessage("Calculando la ruta...");
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
              if(response.equalsIgnoreCase("Success"))
              {
            	  ArrayList<LatLng> directionPoint = v2GetRouteDirection.getDirection(document);
            	  
            	  int auxxx=0;
            	  if (bloqueo != null) 
            	  {
            		  for (int i = 0; i < directionPoint.size(); i++) {
                  		double distancia = v2GetRouteDirection.CalculationByDistance(bloqueo.latitude,bloqueo.longitude, directionPoint.get(i).latitude, directionPoint.get(i).longitude);
                  		if (distancia < 50)
      					{
                  			auxxx = 1;
      						break;
      					}
      				  }
            	  }
            	  if (auxxx == 0) 
            	  {
            		  ok = "1";
					for (int j = 0; j < directionPoint.size(); j++) 
	            	  {
	                    rectLine.add(directionPoint.get(j));
	            	  }
					// Adding route on the map afuera para add todos los puntos si existe bloqueo
//	            	  mGoogleMap.addPolyline(rectLine);
//	            	  markerOptions.position(toPosition);
//	            	  markerOptions.draggable(true);
//	            	  mGoogleMap.addMarker(markerOptions);
            	  }
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
    			
    			HelpRute help = new HelpRute();
	        	help.execute();
				
//				GetRouteTask getRoute = new GetRouteTask();
//		        getRoute.execute();
			}
    		
    	}
		
	}
    
    
    
    
    
    
    
    // MENU ==============================================================
    

    public void toggleMenu(View v){
        mainLayout.toggleMenu();
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    private void onMenuItemClick(AdapterView<?> parent, View view, int position, long id) throws JSONException {
    	
    	String selectedItem = lvMenuItems[position];
        String currentItem = tvTitle.getText().toString();
        
        // Do nothing if selectedItem is currentItem
        if(selectedItem.compareTo(currentItem) == 0) {
            mainLayout.toggleMenu();
            return;
        }
            
        FragmentManager fm = MainActivity.this.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = null;

        
        if(selectedItem.compareTo("Dirigirse a Casa") == 0) 
        {
        	ayudaServicios servicios = new ayudaServicios();
        	JSONArray puntoCasaJson = servicios.getPunto("casa", codigo_usuario);
			
        	if(puntoCasaJson.length() == 0) 	
        	{
        		Toast.makeText(this,"Seleccione Punto a CASA", Toast.LENGTH_LONG).show();
        		homeFalse = "llenar";
        	}
        	else 
        	{
        		Toast.makeText(this,"camino a CASA", Toast.LENGTH_LONG).show();
        		double latitude = mGoogleMap.getMyLocation().getLatitude();
        		double longitude = mGoogleMap.getMyLocation().getLongitude();
        		LatLng Position = new LatLng(latitude, longitude);
        		fromPosition = Position;
        		
        		double lat = puntoCasaJson.getJSONObject(0).getDouble("latitude");
        		double lon = puntoCasaJson.getJSONObject(0).getDouble("longitude");
        		
        		toPosition = new LatLng(lat, lon);
        		
        		HelpRute help = new HelpRute();
	        	help.execute(ok);

    		}
        } 
        else 
        	if(selectedItem.compareTo("Dirigirse al Trabajo") == 0) 
        	{
        		ayudaServicios servicios = new ayudaServicios();
        		JSONArray puntoTrabajoJson = servicios.getPunto("trabajo", codigo_usuario);
			
        		if(puntoTrabajoJson.length() == 0) 	
        		{
        		
        			Toast.makeText(this,"Seleccione punto al TRABAJO", Toast.LENGTH_LONG).show();
        			workFalse = "llenar";
        		}
        		else 
        		{
        			Toast.makeText(this,"camino al TRABAJO", Toast.LENGTH_LONG).show();
        			double latitude = mGoogleMap.getMyLocation().getLatitude();
        			double longitude = mGoogleMap.getMyLocation().getLongitude();
        			LatLng Position = new LatLng(latitude, longitude);
        			fromPosition = Position;
        		
        			double lat = puntoTrabajoJson.getJSONObject(0).getDouble("latitude");
        			double lon = puntoTrabajoJson.getJSONObject(0).getDouble("longitude");
        		
        			toPosition = new LatLng(lat, lon);
    				
        			HelpRute help = new HelpRute();
        			help.execute(ok);
        		}
        	}
        	else 
        		if(selectedItem.compareTo("Compartir punto de Bloqueo") == 0) 
        		{
        			ayudaServicios servicios = new ayudaServicios();
        			JSONArray puntoBloqueoJson = servicios.getPunto("bloque", codigo_usuario);
        	
        			if (puntoBloqueoJson.length() > 0) 
        			{
		        		double lat = puntoBloqueoJson.getJSONObject(0).getDouble("latitude");
		        		double lon = puntoBloqueoJson.getJSONObject(0).getDouble("longitude");
		        		
		        		bloqueo = new LatLng(lat, lon);
        			}
        			
        			
        			
        			Toast.makeText(this,"Seleccione punto de Bloque=", Toast.LENGTH_LONG).show();
        			bloqueoFalse = "llenar";
        		
        		}
        
        	if(fragment != null) 
        	{
            // Replace current fragment by this new one
        		//           ft.replace(R.id.activity_main_content_fragment, fragment);
//            ft.commit();
            
            // Set title accordingly
        		tvTitle.setText(selectedItem);
        	}
        
        // Hide menu anyway
        mainLayout.toggleMenu();
    }

    
    
    
    
    
    
    
    
    
    
    
    @Override
    public void onBackPressed() {
        if (mainLayout.isMenuShown()) {
            mainLayout.toggleMenu();
        }
        else {
            super.onBackPressed();
        }
    }
    
}
