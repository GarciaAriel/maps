package com.example.maps;

import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
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
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CameraPositionCreator;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.Overlay;

import android.R.integer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ToggleButton;


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
    //LatLng bloqueo;
    ArrayList<LatLng> puntosDeBloqueo = new ArrayList<LatLng>();
    String bloqueoFalse= "false";
    String ok = "0";
    PolylineOptions rectLine = new PolylineOptions().width(10).color(Color.GREEN);
    ArrayList<LatLng> puntosDeLaRuta = new ArrayList<LatLng>();
    String codigo_usuario="";
    
    ArrayList<LatLng> markerPoints;
    
    GoogleMap mGoogleMap;
    MarkerOptions markerOptions;
    Location location ;
    ToggleButton toggleButton1;
    
    //borrar
    int punteroRuta;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
    	WifiManager wifiMan = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInf = wifiMan.getConnectionInfo();
		codigo_usuario = wifiInf.getMacAddress();
    	
    	toggleButton1 = (ToggleButton)findViewById(R.id.toggleButton1);
//        toggleButton1.setOnCheckedChangeListener(s);
    	
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
        
        toggleButton1 = (ToggleButton) findViewById(R.id.toggleButton1);
        toggleButton1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				methodChangeMode(v);
			}
		});
        
        // Get title textview
        //tvTitle = (TextView) findViewById(R.id.activity_main_content_title);
        
        
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
          CameraUpdate zoom=CameraUpdateFactory.zoomTo(12);
          //CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);
          
          mGoogleMap.moveCamera(center);
          mGoogleMap.animateCamera(zoom);
          
//00000000000000000000000
          
          //00000000000000000000
          
          
          // Enabling MyLocation in Google Map
          
          mGoogleMap.setMyLocationEnabled(true);
          mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
          mGoogleMap.getUiSettings().setCompassEnabled(true);
          mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
          mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
          mGoogleMap.setTrafficEnabled(true);
          mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
          markerOptions = new MarkerOptions();
          
          
          
          //provando la conexion servicios
          
          
//          ser se = new ser();
//          se.execute();
          
             //ver al inicio los puntos debloqueo
//          try {
//        	  ProgressDialog sddf;
//        	  sddf = new ProgressDialog(MainActivity.this);
//			  sddf.setMessage("No es posible iniciar la apppppppppppppp");
//			  sddf.show();
//        	  
//        	  //ayudaServicios help = new ayudaServicios();
//              //puntosDeBloqueo = help.getPuntosBloqueoPersistente();
//              for(int i=0 ; i<puntosDeBloqueo.size() ; i++)
//	  			{
//	  				markerOptions.position(puntosDeBloqueo.get(i));
//	  				markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//	  				mGoogleMap.addMarker(markerOptions);
//	  			}
//		  } catch (Exception e) {
//			  Context context = getApplicationContext();
//			  CharSequence text = "Hello toast!";
//			  int duration = Toast.LENGTH_SHORT;
//
//			  Toast toast = Toast.makeText(context, text, duration);
//			  toast.show();
//
//			    
//				System.out.println("ver puntos bloqueo inicio");
//				super.onStop();
//		        finish();
//		  }
          
    }
    	
//000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000

    
    
    private class ser extends AsyncTask<String, Void, String>
    {
    	private ProgressDialog Dialog;
    	boolean res=false;
		@Override
		protected String doInBackground(String... params) {
	    	try{
	            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	            NetworkInfo netInfo = cm.getActiveNetworkInfo();

	            if (netInfo != null && netInfo.isConnected())
	            {
	                //URL url = new URL("http://www.Google.com/");
	            	URL url = new URL("http://127.0.0.1:8080/com.maps/sample/puntos/getPunto/no/no");
	                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
	                urlc.setRequestProperty("Connection", "close");
	                urlc.setConnectTimeout(3000); // Timeout 2 seconds.
	                urlc.connect();
	                
	                if (urlc.getResponseCode() == 200)  //Successful response.
	                {
	                	res = true;
	                } 
	            }
	        }
	        catch(Exception e)
	        {
	            e.printStackTrace();
	        }
			return null;
		}
		@Override
        protected void onPostExecute(String result) {
			if (res==false) {
				Dialog = new ProgressDialog(MainActivity.this);
                Dialog.setMessage("no conexion adios");
                Dialog.show();
			}
			else
			{
				Dialog = new ProgressDialog(MainActivity.this);
                Dialog.setMessage("ok conexion");
                Dialog.show();
			}
		}
    }
    private class HelpRute extends AsyncTask<String, Void, String>
    {
    	private ProgressDialog Dialog;
        String response = "";
        @Override
        protected void onPreExecute() {
        	try {
        		mGoogleMap.clear();
          	  	rectLine = new PolylineOptions().width(10).color(Color.GREEN);
                Dialog = new ProgressDialog(MainActivity.this);
                Dialog.setMessage("Espere un momento");
                Dialog.show();
                ayudaServicios help = new ayudaServicios();
    			puntosDeBloqueo = help.getPuntosBloqueoPersistente();
			} catch (Exception e) {
				Dialog = new ProgressDialog(MainActivity.this);
                Dialog.setMessage("en pre execute");
                
                finish();
			}
        	  
        }

		@Override
		protected String doInBackground(String... params) {
			try {
				getRouteTask2 getRoute = new getRouteTask2();
				ArrayList<LatLng> listPoint;
				
				listPoint = getRoute.get_route(fromPosition,toPosition,puntosDeBloqueo);
				puntosDeLaRuta = listPoint;
				if(listPoint!=null)
				{
					for (int i = 0; i < listPoint.size(); i++) {
						rectLine.add(listPoint.get(i));
					}
				}
			} catch (Exception e) {
				Dialog = new ProgressDialog(MainActivity.this);
                Dialog.setMessage("en background");
			}

					
			return null;
		}
		@Override
        protected void onPostExecute(String result) {
			//puntos bloqueo
			try {
				markerOptions = new MarkerOptions();
				for(int i=0 ; i<puntosDeBloqueo.size() ; i++)
				{
					markerOptions.position(puntosDeBloqueo.get(i));
					markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
					mGoogleMap.addMarker(markerOptions);
				}
				for(int i=0 ; i<puntosDeLaRuta.size() ; i++)
				{
					markerOptions.position(puntosDeLaRuta.get(i));
					markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
					mGoogleMap.addMarker(markerOptions);
				}
				markerOptions.position(fromPosition);
				markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
				mGoogleMap.addMarker(markerOptions);
				
				markerOptions.position(toPosition);
				markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
				mGoogleMap.addMarker(markerOptions);
				
				mGoogleMap.addPolyline(rectLine);
		  	  	markerOptions.draggable(true);
		  	  	mGoogleMap.addMarker(markerOptions);
		  	  Dialog.dismiss();
			} catch (Exception e) {
				Dialog = new ProgressDialog(MainActivity.this);
                Dialog.setMessage("en post execute");
			}
			
		}
	}

    
    
    
    
    
    
  @Override
  protected void onStop() {
        super.onStop();
        finish();
  }
    
  @Override
  protected void onRestart() {
      super.onRestart();  // Always call the superclass method first
      
      // Activity being restarted from stopped state    
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
    
    
    
    
    
    
    
    
    
    
    
//    public void onClickAddPoint(View view) {
//    	if(homeFalse == "false")
//    	{
//    		Toast.makeText(this,"Seleccione un punto para HOME", Toast.LENGTH_LONG).show();
//    		homeFalse = "llenar";
//    		hiloSecundarioMenu();
//    	}
//    	else
//    	{
//    		if (homeFalse == "oto") {
//    			Toast.makeText(this,"camino a home", Toast.LENGTH_LONG).show();
//    			//"hiloSecundarioMenu", "garcia"
//    			double latitude = mGoogleMap.getMyLocation().getLatitude();
//    			double longitude = mGoogleMap.getMyLocation().getLongitude();
//    			LatLng Position = new LatLng(latitude, longitude);
//    			fromPosition = Position;
//    			toPosition = home;
//    			
//    			HelpRute help = new HelpRute();
//	        	help.execute();
//			}
//    	}
//	}
    
    
    
    
    
    
    
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
        	JSONArray puntoCasaJson= null;
        	try {
        		ayudaServicios servicios = new ayudaServicios();
        		puntoCasaJson = servicios.getPunto("casa", codigo_usuario);
			} catch (Exception e) {
				Toast.makeText(this,"menu get punto casa", Toast.LENGTH_LONG).show();
				super.onStop();
		        finish();
			}
        	
        	if(puntoCasaJson.length() == 0) 	
        	{
        		Toast.makeText(this,"Seleccione Punto a CASA", Toast.LENGTH_LONG).show();
        		homeFalse = "llenar";
        		hiloSecundarioMenu();
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
        		JSONArray puntoTrabajoJson=null;
        		try {
        			ayudaServicios servicios = new ayudaServicios();
            		puntoTrabajoJson = servicios.getPunto("trabajo", codigo_usuario);
				} catch (Exception e) {
					Toast.makeText(this,"menu get punto trabajo", Toast.LENGTH_LONG).show();
					super.onStop();
			        finish();
				}
        		
			
        		if(puntoTrabajoJson.length() == 0) 	
        		{
        		
        			Toast.makeText(this,"Seleccione punto al TRABAJO", Toast.LENGTH_LONG).show();
        			workFalse = "llenar";
        			hiloSecundarioMenu();
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
        			try {
        				ayudaServicios servicios = new ayudaServicios();
            			puntosDeBloqueo = servicios.getPuntosBloqueoPersistente();
            			Toast.makeText(this,"Seleccione punto de Bloque=", Toast.LENGTH_LONG).show();
            			bloqueoFalse = "llenar";
            			hiloSecundarioMenu();
					} catch (Exception e) {
						Toast.makeText(this,"menu compartir punto bloqueo", Toast.LENGTH_LONG).show();
						super.onStop();
				        finish();
					}
        		}
        
        	if(fragment != null) 
        	{
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
    
    public void methodChangeMode(View v)
    {
    	try {
    		toggleButton1 = (ToggleButton)findViewById(R.id.toggleButton1);
    		if (toggleButton1.isChecked()) {
        		Toast.makeText(this,"Seleccione dos Puntos", Toast.LENGTH_LONG).show();
        		mGoogleMap.setOnMapClickListener(new OnMapClickListener() 
            	{
            	  
        			@Override
        			public void onMapClick(LatLng point) 
        			{
        				
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
        						options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        					}
        				}
        					// Add marker al map
        				mGoogleMap.addMarker(options);
        				
        				// Checks, whether start and end locations are captured
        				if(markerPoints.size() >= 2)
        				{					
        					fromPosition = markerPoints.get(0);
        					toPosition = markerPoints.get(1);
        					System.out.println("punto 1= "+fromPosition.latitude+"--"+fromPosition.longitude);
        					System.out.println("punto 1= "+toPosition.latitude+"--"+toPosition.longitude);
        					
        		        	HelpRute help = new HelpRute();
        		        	help.execute();
        		        	punteroRuta = 0;
        		        	mGoogleMap.setOnMapClickListener(null);
        		        	
        		        	//boton visible iniciar ruta
        		        	Button start_rute = (Button)findViewById(R.id._ini_ruta);
        		        	start_rute.setVisibility(View.VISIBLE);
        		        }
        				
        				
        			}
        		});
    		}
        	else{
        		puntosDeLaRuta.clear();
        		mGoogleMap.clear();
        		mGoogleMap.setOnMapClickListener(null);
        		
        		CameraPosition cameraPosition2 = new CameraPosition.Builder().target(new LatLng(-17.393792, -66.157110)).zoom(12).bearing(0).tilt(0).build();
	            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition2));
        		
                Button start_rute = (Button)findViewById(R.id._ini_ruta);
	        	start_rute.setVisibility(View.INVISIBLE);
        	}
		} catch (Exception e) {
			e.getMessage();
		}
    	
    }
    
    public void iniciar_recorrido(View v)
    {
    	//puntosDeLaRuta
    	if (puntosDeLaRuta.size() > 0) {
    	   	double latitude = mGoogleMap.getMyLocation().getLatitude();
			double longitude = mGoogleMap.getMyLocation().getLongitude();
			LatLng myPos = new LatLng(latitude, longitude);
			
    		getRouteTask2 help = new getRouteTask2();
    		
    		int res = punteroRuta;
    		
    		if (puntosDeLaRuta.size() > res+1) {
    			
	    		//int res = help.verificarMyPosCercaCamino(puntosDeLaRuta.get(puntosDeLaRuta.size()-4),puntosDeLaRuta);
	    		
	    		double x1 = puntosDeLaRuta.get(res).latitude;
	    		double y1 = puntosDeLaRuta.get(res).longitude;
	    		
	    		double x2 = puntosDeLaRuta.get(res+1).latitude;
	    		double y2 = puntosDeLaRuta.get(res+1).longitude;
	    		
	    		GMapV2GetRouteDirection route = new GMapV2GetRouteDirection();
	    		double distance = route.CalculationByDistance(x1, y1, x2,y2);
	    		
	    		while (distance<7 && puntosDeLaRuta.size() > res+2) {
					punteroRuta +=1;
					res +=1;
					x1 = puntosDeLaRuta.get(res).latitude;
		    		y1 = puntosDeLaRuta.get(res).longitude;
		    		
		    		x2 = puntosDeLaRuta.get(res+1).latitude;
		    		y2 = puntosDeLaRuta.get(res+1).longitude;
		    		
		    		distance = route.CalculationByDistance(x1, y1, x2,y2);
				}
	    		
	    		double m = ((y2 - y1)/(x2-x1));
	    		
	    		System.out.println("pendiente recta: "+m);
	    		
	    		double atamm = Math.atan(m);
	    		double grados = Math.toDegrees(atamm);
	    		
	    		System.out.println("atan: "+atamm);
	    		System.out.println("grados1: "+grados);
	    		
	    		
	    		int x = (int) (grados);
	    		System.out.println("grados2: "+x);
    			
	    		if (x1<x2 &&  y1<y2) {
	    			x = x;//primer cuadrante
	    		}
	    		else{
	    			if (x1>x2 &&  y1>y2) {
	    				x = (x)+180;//tercer cuadrante
	    			}
	    			else{
	    				if (x1<x2 &&  y1>y2) {
	    					System.out.println("llego: "+x1+" "+y1+" "+x2+" "+y2);
	    					x = 360+x;
	    				}
	    				else{
	    					if (x1>x2 &&  y1<y2) {
	    						System.out.println("llego: "+x1+" "+y1+" "+x2+" "+y2);
	    						x = (90+x)+90;//cuarto cuadrante
							}
	    					
	    				}
	    			}
	    		}
	    		try 
				{
					System.out.println("vamos: "+x);
					
					CameraPosition cameraPosition2 = new CameraPosition.Builder().target(puntosDeLaRuta.get(res)).zoom(17).bearing(x).tilt(30).build();
    	            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition2));
    	            punteroRuta+=1;
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println(e.getMessage());
				}
    		}
 }	
		
    	
    		 
    
    }
    
    public void hiloSecundarioMenu()
    {
    	mGoogleMap.setOnMapClickListener(new OnMapClickListener(){
			
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
					options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
					mGoogleMap.addMarker(options);
					homeFalse = "oto";
					
					try {
						ayudaServicios servicios = new ayudaServicios();
						servicios.guardarPunto("casa", codigo_usuario, home.latitude,home.longitude);
					} catch (Exception e) {
						System.out.println("hiloSecundarioMenu  home");
						//super.onStop();
				        finish();
					}
					
					return;
				}
				if(workFalse == "llenar")
				{
					markerPoints.clear();
					mGoogleMap.clear();
					work = point;
					MarkerOptions options = new MarkerOptions();
					options.position(point);
					options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
					mGoogleMap.addMarker(options);
					workFalse = "oto";
					
					try {
						ayudaServicios servicios = new ayudaServicios();
						servicios.guardarPunto("trabajo", codigo_usuario, work.latitude,work.longitude);
					} catch (Exception e) {
						System.out.println("hiloSecundarioMenu  work");
						finish();
					}
					
					return;
				}
				if(bloqueoFalse == "llenar")
				{
					markerPoints.clear();
					mGoogleMap.clear();
					//bloqueo = point;
					puntosDeBloqueo.add(point);
					
					for(int i=0 ; i<puntosDeBloqueo.size() ; i++)
					{
						markerOptions.position(puntosDeBloqueo.get(i));
						markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
						mGoogleMap.addMarker(markerOptions);
					}
					
					bloqueoFalse = "false";
					
					try {
						ayudaServicios servicios = new ayudaServicios();
						servicios.guardarPuntoBloqueo(codigo_usuario, point.latitude,point.longitude);
					} catch (Exception e) {
						System.out.println("hiloSecundarioMenu  bloqueo");
						finish();
					}
					
					return;
				}
			}
		
    	});
    }
    
}
