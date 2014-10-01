package com.example.maps;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.entropy.slidingmenu2.layout.MainLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ToggleButton;


public class MainActivity extends FragmentActivity 
{
	
	MainLayout mainLayout;
    
    // ListView menu
    private ListView lvMenu;
    private String[] lvMenuItems;
    private ListView lvMenuRight;
    private String[] lvMenuItemsRight;
    private boolean connectVerifyValue = false;
    
    // Menu button
    Button btMenu;
    Button btMenuRight;
    
    
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
    ArrayList<LatLng> puntosDeAlerta = new ArrayList<LatLng>();
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
    final Context context = this;
    final Handler handler=new Handler();
    
    //borrar
    int punteroRuta;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
    	//ocultar nombre app y dibujo
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        //mac address como codigo de dispositivo
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
        //final ArrayAdapter<String> array0 =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, lvMenuItems); 
        
     // Init menu right
        lvMenuItemsRight = getResources().getStringArray(R.array.menu_items_right);
        final ArrayAdapter<String> array = new ArrayAdapter<String>(this,
        		android.R.layout.simple_list_item_1, lvMenuItemsRight);
           
     // Get menu button
        btMenu = (Button) findViewById(R.id.activity_main_content_button_menu);
        btMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	//ocultar otro menu //mostrar otro
            	lvMenu = (ListView) findViewById(R.id.activity_main_menu_listview);
            	lvMenu.setVisibility(View.GONE);
            	
            	LinearLayout linear = (LinearLayout) findViewById(R.id.menu_options_1);
				linear.setVisibility(View.VISIBLE);
				LinearLayout linear1 = (LinearLayout) findViewById(R.id.menu_options_2);
				linear1.setVisibility(View.VISIBLE);
				LinearLayout linear2 = (LinearLayout) findViewById(R.id.menu_options_3);
				linear2.setVisibility(View.VISIBLE);
				
				TextView text1 = (TextView) findViewById(R.id.TextView01);
				text1.setVisibility(View.VISIBLE);
				TextView text2 = (TextView) findViewById(R.id.TextView02);
				text2.setVisibility(View.VISIBLE);
				TextView text3 = (TextView) findViewById(R.id.TextView03);
				text3.setVisibility(View.VISIBLE);
            	// Show/hide the menu
            	toggleMenu(v);
            }
        });
        
     // PUNTO FAVORITO CASA
        Button btMenuHome10 = (Button) findViewById(R.id.button10);
        btMenuHome10.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	try {
					onHomeClick(v);
				} catch (JSONException e) {
					e.printStackTrace();
				}
            	// Show/hide the menu
            	toggleMenu(v);
            }
        });
     // PUNTO FAVORITO TRABAJO
        Button btMenuWork11 = (Button) findViewById(R.id.button11);
        btMenuWork11.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	try {
            		onWorkClick(v);
				} catch (JSONException e) {
					e.printStackTrace();
				}
            	// Show/hide the menu
            	toggleMenu(v);
            }
        });
     // NOTIFICACION ACCIDENTE
        Button btMenuAccident = (Button) findViewById(R.id.button13);
        btMenuAccident.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	try {
            		onAccidentClick(v);
				} catch (JSONException e) {
					e.printStackTrace();
				}
            	// Show/hide the menu
            	toggleMenu(v);
            }
        });
     // NOTIFICACION TRAFICO
        Button btMenuTraffic = (Button) findViewById(R.id.button14);
        btMenuTraffic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	try {
            		onTrafficClick(v);
				} catch (JSONException e) {
					e.printStackTrace();
				}
            	// Show/hide the menu
            	toggleMenu(v);
            }
        });   
     // NOTIFICACION REDADA
        Button btMenuRaid = (Button) findViewById(R.id.button15);
        btMenuRaid.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	try {
            		onRaidClick(v);
				} catch (JSONException e) {
					e.printStackTrace();
				}
            	// Show/hide the menu
            	toggleMenu(v);
            }
        });   
        
     // NOTIFICACION BLOQUEO MERCADO
        Button btMenuMarket = (Button) findViewById(R.id.button16);
        btMenuMarket.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	try {
            		onMarketClick(v);
				} catch (JSONException e) {
					e.printStackTrace();
				}
            	// Show/hide the menu
            	toggleMenu(v);
            }
        });
     // NOTIFICACION BLOQUEO COLEGIO
        Button btMenuSchool = (Button) findViewById(R.id.button17);
        btMenuSchool.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	try {
            		onSchoolClick(v);
				} catch (JSONException e) {
					e.printStackTrace();
				}
            	// Show/hide the menu
            	toggleMenu(v);
            }
        });
        
        
     // Get title textview
        tvTitle = (TextView) findViewById(R.id.activity_main_content_title);
        
        toggleButton1 = (ToggleButton) findViewById(R.id.toggleButton1);
        toggleButton1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//ocultar otro menu // ver otro
				LinearLayout linear = (LinearLayout) findViewById(R.id.menu_options_1);
				linear.setVisibility(View.GONE);
				LinearLayout linear1 = (LinearLayout) findViewById(R.id.menu_options_2);
				linear1.setVisibility(View.GONE);
				LinearLayout linear2 = (LinearLayout) findViewById(R.id.menu_options_3);
				linear2.setVisibility(View.GONE);
				
				TextView text1 = (TextView) findViewById(R.id.TextView01);
				text1.setVisibility(View.GONE);
				TextView text2 = (TextView) findViewById(R.id.TextView02);
				text2.setVisibility(View.GONE);
				TextView text3 = (TextView) findViewById(R.id.TextView03);
				text3.setVisibility(View.GONE);
				
				lvMenu = (ListView) findViewById(R.id.activity_main_menu_listview);
            	lvMenu.setVisibility(View.VISIBLE);
				// TODO Auto-generated method stub
				
            	if (toggleButton1.isChecked()) {
            		TextView text = (TextView)findViewById(R.id.TextView01);
                	text.setText("OPCIONES DE DIBUJO");
            		toggleMenuRight(v);
            		lvMenuRight = (ListView) findViewById(R.id.activity_main_menu_listview);
                    lvMenuRight.setAdapter(array);
                    lvMenuRight.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            try {
            					onMenuItemClickRight(parent, view, position, id);
            				} catch (JSONException e) {
            					
            					e.printStackTrace();
            				}
                        }
                        
                    });
				}
            	else
            	{
            		methodChangeMode(v);
            	}
            	//methodChangeMode(v);
			}
		});
        
        // Add FragmentMain as the initial fragment       
        FragmentManager fm = MainActivity.this.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        
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
          
//0000000000000000000000000000000000000000000
          
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
          
          
          connectVerify se = new connectVerify();
          se.execute();
          
          
          checkNotifications ceh = new checkNotifications();
          ceh.execute();
          
          
		  
          

    }
    	
//000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000

    private class checkNotifications extends AsyncTask<String, Void, String>
    {
    	
		@Override
		protected String doInBackground(String... params) {
			final boolean killMe = false;
			final notifications notification = new notifications();
			
				final Runnable r = new Runnable()
				{
				    public void run() 
				    {
				    	//if (connectVerifyValue) {
				    		ayudaServicios help = new ayudaServicios();
					    	puntosDeAlerta = help.getPuntosAlerta();//
					    	
					    	//notificaciones
					    	
					    	notification.checkNotificationsNew(puntosDeAlerta);
					    	double latitude = mGoogleMap.getMyLocation().getLatitude();
		            		double longitude = mGoogleMap.getMyLocation().getLongitude();
		            		
					    	ArrayList<LatLng> list = notification.getNotificationNearToPoint(latitude,longitude);
					    	
					    	if (list != null && list.size()>0) {
					    		for (int i = 0; i < list.size(); i++) {
						    		markerOptions.position(list.get(i));
									markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
									mGoogleMap.addMarker(markerOptions);
						    		Toast.makeText(getApplicationContext(), "notificaion text"+list.get(i).latitude, 1000).show();
						    		handler.postDelayed(this, 3000);
								}
							}
					    	
						//}
				    }
				};

				handler.postDelayed(r, 1000);
			return null;
	    }
		
	}
    
    private class connectVerify extends AsyncTask<String, Void, String>
    {
    	private ProgressDialog Dialog;
    	boolean res=false;
    	
    	@Override
        protected void onPreExecute() {
    		Dialog = new ProgressDialog(MainActivity.this);
            Dialog.setMessage("Verificando conexion a internet.");
            Dialog.show();
//    		new Handler().postDelayed(new Runnable() {
//              @Override
//              public void run() {
//            	  Dialog.dismiss();
//              }
//          }, 3000);
    		
        }
    	
		@Override
		protected String doInBackground(String... params) {
	    	try{
	            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	            NetworkInfo netInfo = cm.getActiveNetworkInfo();

	            if (netInfo != null && netInfo.isConnected())
	            {
	                URL url = new URL("http://www.Google.com/");
	            	//URL url = new URL("http://127.0.0.1:8080/com.maps/sample/puntos/getPunto/no/no");
	                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
	                urlc.setRequestProperty("Connection", "close");
	                urlc.setConnectTimeout(3000); // Timeout 2 seconds.
	                urlc.connect();
	                
	                if (urlc.getResponseCode() == 200)  //Successful response.
	                {
	                	res = true;
	                	connectVerifyValue = true;
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
				
				//Dialog = new ProgressDialog(MainActivity.this);
                Dialog.setMessage("No se tiene conexion a Internet.");
                Dialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                    	Dialog.dismiss();
                    }
                }, 4000);
                
			}
			else
			{
				try {
					marcarPuntos();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				//Dialog = new ProgressDialog(MainActivity.this);
                Dialog.setMessage("Hola, conexion exitosa");
                Dialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                    	Dialog.dismiss();
                    }
                }, 3000);
			}
		}
    }
    
    private class obtainRoute extends AsyncTask<String, Void, String>
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
                onStop();
                //finish();
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
//				for(int i=0 ; i<puntosDeLaRuta.size() ; i++)
//				{
//					markerOptions.position(puntosDeLaRuta.get(i));
//					markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
//					mGoogleMap.addMarker(markerOptions);
//				}
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
        //finish();
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
    
    
    
    
    
    
    
    
    
    
    
    
    // MENU ==============================================================
    

    public void toggleMenu(View v){
    	
        mainLayout.toggleMenu();
        
    }
    public void toggleMenuRight(View v){
        mainLayout.toggleMenu();
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    private void onHomeClick( View view) throws JSONException {
    	if (connectVerifyValue) {
    		FragmentManager fm = MainActivity.this.getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment fragment = null;
            
            	JSONArray puntoCasaJson= null;
            	try {
            		ayudaServicios servicios = new ayudaServicios();
            		puntoCasaJson = servicios.getPunto("Casa", codigo_usuario);
    			} catch (Exception e) {
    				Toast.makeText(this,"menu get punto casa", Toast.LENGTH_LONG).show();
    				super.onStop();
    		        //finish();
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
            		
            		obtainRoute help = new obtainRoute();
    	        	help.execute(ok);

        		}
           
    	}
    	else
        {
        	Toast.makeText(this,"Necesita conexion a internet", Toast.LENGTH_SHORT).show();
        }
    }
    private void onWorkClick( View view) throws JSONException {
    	if (connectVerifyValue) {
    		FragmentManager fm = MainActivity.this.getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment fragment = null;
            
    		JSONArray puntoTrabajoJson=null;
    		try {
    			ayudaServicios servicios = new ayudaServicios();
        		puntoTrabajoJson = servicios.getPunto("Trabajo", codigo_usuario);
			} catch (Exception e) {
				Toast.makeText(this,"menu get punto trabajo", Toast.LENGTH_LONG).show();
				super.onStop();
		      //  finish();
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
				
    			obtainRoute help = new obtainRoute();
    			help.execute(ok);
    		}
           
    	}
    	else
        {
        	Toast.makeText(this,"Necesita conexion a internet", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void onAccidentClick( View view) throws JSONException {
    	if (connectVerifyValue) {
    		FragmentManager fm = MainActivity.this.getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment fragment = null;
            
    		try {
				Toast.makeText(this,"Seleccione el punto del ACCIDENTE", Toast.LENGTH_LONG).show();
				double latitude = mGoogleMap.getMyLocation().getLatitude();
        		double longitude = mGoogleMap.getMyLocation().getLongitude();
        		
        		CameraPosition cameraPosition2 = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(17).bearing(0).tilt(20).build();
	            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition2));
	            hiloSecundarioMenuAlertaAccident();
    		} catch (Exception e) {
				Toast.makeText(this,"menu compartir punto bloqueo", Toast.LENGTH_LONG).show();
				super.onStop();
		    //    finish();
			}
           
    	}
    	else
        {
        	Toast.makeText(this,"Necesita conexion a internet", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void onRaidClick( View view) throws JSONException {
    	if (connectVerifyValue) {
    		FragmentManager fm = MainActivity.this.getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment fragment = null;
            
    		try {
				Toast.makeText(this,"Seleccione el punto de REDADA", Toast.LENGTH_LONG).show();
				double latitude = mGoogleMap.getMyLocation().getLatitude();
        		double longitude = mGoogleMap.getMyLocation().getLongitude();
        		
        		CameraPosition cameraPosition2 = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(17).bearing(0).tilt(20).build();
	            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition2));
	            hiloSecundarioMenuAlertaRaid();
    		} catch (Exception e) {
				Toast.makeText(this,"menu compartir punto bloqueo", Toast.LENGTH_LONG).show();
				super.onStop();
		    //    finish();
			}
           
    	}
    	else
        {
        	Toast.makeText(this,"Necesita conexion a internet", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void onMarketClick( View view) throws JSONException {
    	if (connectVerifyValue) {
    		FragmentManager fm = MainActivity.this.getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment fragment = null;
            
    		try {
				Toast.makeText(this,"Seleccione el punto de MERCADO", Toast.LENGTH_LONG).show();
				hiloSecundarioMenuMarket();
    		} catch (Exception e) {
				Toast.makeText(this,"menu compartir punto bloqueo", Toast.LENGTH_LONG).show();
				super.onStop();
		    //    finish();
			}
           
    	}
    	else
        {
        	Toast.makeText(this,"Necesita conexion a internet", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void onSchoolClick( View view) throws JSONException {
    	if (connectVerifyValue) {
    		FragmentManager fm = MainActivity.this.getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment fragment = null;
            
    		try {
				Toast.makeText(this,"Seleccione el punto de COLEGIO", Toast.LENGTH_LONG).show();
				hiloSecundarioMenuSchool();
    		} catch (Exception e) {
				Toast.makeText(this,"menu compartir punto bloqueo", Toast.LENGTH_LONG).show();
				super.onStop();
		    }
           
    	}
    	else
        {
        	Toast.makeText(this,"Necesita conexion a internet", Toast.LENGTH_SHORT).show();
        }
    }
    private void onTrafficClick( View view) throws JSONException {
    	if (connectVerifyValue) {
    		FragmentManager fm = MainActivity.this.getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment fragment = null;
            
    		try {
				Toast.makeText(this,"Seleccione el punto de TRAFICO", Toast.LENGTH_LONG).show();
				double latitude = mGoogleMap.getMyLocation().getLatitude();
        		double longitude = mGoogleMap.getMyLocation().getLongitude();
        		
        		CameraPosition cameraPosition2 = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(17).bearing(0).tilt(20).build();
	            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition2));
    			hiloSecundarioMenuAlertaTraffic();
    		} catch (Exception e) {
				Toast.makeText(this,"menu compartir punto bloqueo", Toast.LENGTH_LONG).show();
				super.onStop();
		    //    finish();
			}
           
    	}
    	else
        {
        	Toast.makeText(this,"Necesita conexion a internet", Toast.LENGTH_SHORT).show();
        }
    }
    
    
    
private void onMenuItemClickRight(AdapterView<?> parent, View view, int position, long id) throws JSONException {
    	
    	String selectedItem = lvMenuItemsRight[position];
        String currentItem = tvTitle.getText().toString();
        
        // Do nothing if selectedItem is currentItem
        if(selectedItem.compareTo(currentItem) == 0) {
            mainLayout.toggleMenuRight();
            return;
        }
            if (connectVerifyValue) {
				
			
        FragmentManager fm = MainActivity.this.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = null;

        
        if(selectedItem.compareTo("Tomar mi punto como origen") == 0) 
        {
        	
        	//poner punto inicio mi localizacion
        		Toast.makeText(this,"Selecciones el punto de destino", Toast.LENGTH_LONG).show();
        		double latitude = mGoogleMap.getMyLocation().getLatitude();
        		double longitude = mGoogleMap.getMyLocation().getLongitude();
        		LatLng Position = new LatLng(latitude, longitude);
        		fromPosition = Position;
        		markerPoints.add(Position);
        	//marker
        		MarkerOptions options = new MarkerOptions();
            	options.position(Position);
            	options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            	mGoogleMap.addMarker(options);
            	marcarPuntos();
        	//put lisener para el segundo punto
        		methodChangeMode(view);
            	
            		
        } 
        else 
        	if(selectedItem.compareTo("Seleccionar dos puntos") == 0) 
        		{
        			Toast.makeText(this,"Seleccione el punto origen", Toast.LENGTH_LONG).show();
        			methodChangeMode(view);
        		}
        
        	if(fragment != null) 
        	{
            	tvTitle.setText(selectedItem);
        	}
        
        // Hide menu anyway
        mainLayout.toggleMenuRight();
            }
            else
            {
            	Toast.makeText(this,"Necesita conexion a internet", Toast.LENGTH_SHORT).show();
            }
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
        					Toast.makeText(context, "Seleccione el punto destino", Toast.LENGTH_LONG).show();
        					
        					options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        				}
        				else{ 
        					if(markerPoints.size()==2){
        						options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        					}
        				}
        					// Add marker al map
        				mGoogleMap.addMarker(options);
        				
        				if(markerPoints.size() >= 2)
        				{		
        					
        					//Toast.makeText(this,"Seleccione el punto destino", Toast.LENGTH_LONG).show();
        					fromPosition = markerPoints.get(0);
        					toPosition = markerPoints.get(1);
        					
        		        	obtainRoute help = new obtainRoute();
        		        	help.execute();
        		        	punteroRuta = 0;
        		        	mGoogleMap.setOnMapClickListener(null);
        		        	try {
								marcarPuntos();
							} catch (JSONException e) {
								e.printStackTrace();
							}
        		        	
        		        	//boton visible iniciar ruta
        		        	Button start_rute = (Button)findViewById(R.id._ini_ruta);
        		        	start_rute.setVisibility(View.VISIBLE);
        		        }
        				
        				
        			}
        		});
    		}
        	else{
        		markerPoints.clear();
        		puntosDeLaRuta.clear();
        		mGoogleMap.clear();
        		mGoogleMap.setOnMapClickListener(null);
        		
        		CameraPosition cameraPosition2 = new CameraPosition.Builder().target(new LatLng(-17.393792, -66.157110)).zoom(12).bearing(0).tilt(0).build();
	            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition2));
        		
                Button start_rute = (Button)findViewById(R.id._ini_ruta);
	        	start_rute.setVisibility(View.INVISIBLE);
	        	
	             //ver al inicio los puntos debloqueo

	          	  //ayudaServicios help = new ayudaServicios();
	                //puntosDeBloqueo = help.getPuntosBloqueoPersistente();
	                for(int i=0 ; i<puntosDeBloqueo.size() ; i++)
	  	  			{
	  	  				markerOptions.position(puntosDeBloqueo.get(i));
	  	  				markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
	  	  				mGoogleMap.addMarker(markerOptions);
	  	  			}

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
					
					CameraPosition cameraPosition2 = new CameraPosition.Builder().target(puntosDeLaRuta.get(res)).zoom(19).bearing(x).tilt(45).build();
    	            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition2));
    	            punteroRuta+=1;
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println(e.getMessage());
				}
    		}
    	}	
	}
    
    public void hiloSecundarioMenuAlertaAccident()
    {
    	mGoogleMap.setOnMapClickListener(new OnMapClickListener(){
			
			@Override
			public void onMapClick(LatLng point) 
			{
    				markerPoints.clear();
					mGoogleMap.clear();
					MarkerOptions options = new MarkerOptions();
					options.position(point);
					options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
					mGoogleMap.addMarker(options);
					
					double latitude = mGoogleMap.getMyLocation().getLatitude();
            		double longitude = mGoogleMap.getMyLocation().getLongitude();
            		
            		GMapV2GetRouteDirection auxiliary = new GMapV2GetRouteDirection();
            		double distancia = auxiliary.CalculationByDistance(latitude, longitude, point.latitude, point.longitude);
					if (distancia < 300) {
						try {
							ayudaServicios servicios = new ayudaServicios();
							servicios.guardarPuntoAlerta("Accidente", codigo_usuario, point.latitude,point.longitude);
							marcarPuntos();
							
						} catch (Exception e) {
							System.out.println("hiloSecundarioMenu  home");
							onStop();
					        //finish();
						}
						mGoogleMap.setOnMapClickListener(null);
					}
					else
					{
						mGoogleMap.clear();
						Toast.makeText(context,"Necesita estar cercano al ACCIDENTE", Toast.LENGTH_LONG).show();
					}
					
			}
		});
    }
    public void hiloSecundarioMenuAlertaRaid()
    {
    	mGoogleMap.setOnMapClickListener(new OnMapClickListener(){
			
			@Override
			public void onMapClick(LatLng point) 
			{
    				markerPoints.clear();
					mGoogleMap.clear();
					MarkerOptions options = new MarkerOptions();
					options.position(point);
					options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
					mGoogleMap.addMarker(options);
					
					double latitude = mGoogleMap.getMyLocation().getLatitude();
            		double longitude = mGoogleMap.getMyLocation().getLongitude();
            		
            		GMapV2GetRouteDirection auxiliary = new GMapV2GetRouteDirection();
            		double distancia = auxiliary.CalculationByDistance(latitude, longitude, point.latitude, point.longitude);
					if (distancia < 300) {
						try {
							ayudaServicios servicios = new ayudaServicios();
							servicios.guardarPuntoAlerta("Redada", codigo_usuario, point.latitude,point.longitude);
							marcarPuntos();
						} catch (Exception e) {
							System.out.println("hiloSecundarioMenu  home");
							onStop();
					        //finish();
						}
						mGoogleMap.setOnMapClickListener(null);
					}
					else
					{
						mGoogleMap.clear();
						Toast.makeText(context,"Necesita estar cercano a la REDADA", Toast.LENGTH_LONG).show();
					}
					
			}
		});
    }
    
    public void hiloSecundarioMenuMarket()
    {
    	mGoogleMap.setOnMapClickListener(new OnMapClickListener(){
			
			@Override
			public void onMapClick(LatLng point) 
			{
    				markerPoints.clear();
					mGoogleMap.clear();
					MarkerOptions options = new MarkerOptions();
					options.position(point);
					options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
					mGoogleMap.addMarker(options);
					
						try {
							ayudaServicios servicios = new ayudaServicios();
							servicios.guardarPuntoBloqueo(codigo_usuario, point.latitude,point.longitude);
							marcarPuntos();
						} catch (Exception e) {
							System.out.println("hiloSecundarioMenu  home");
							onStop();
					    }
						mGoogleMap.setOnMapClickListener(null);
			}
		});
    }
    
    public void hiloSecundarioMenuSchool()
    {
    	mGoogleMap.setOnMapClickListener(new OnMapClickListener(){
			
			@Override
			public void onMapClick(LatLng point) 
			{
    				markerPoints.clear();
					mGoogleMap.clear();
					MarkerOptions options = new MarkerOptions();
					options.position(point);
					options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
					mGoogleMap.addMarker(options);
						try {
							ayudaServicios servicios = new ayudaServicios();
							servicios.guardarPuntoBloqueo(codigo_usuario, point.latitude,point.longitude);
							marcarPuntos();
						} catch (Exception e) {
							System.out.println("hiloSecundarioMenu  home");
							onStop();
						}
						mGoogleMap.setOnMapClickListener(null);
			}
		});
    }
    public void hiloSecundarioMenuAlertaTraffic()
    {
    	mGoogleMap.setOnMapClickListener(new OnMapClickListener(){
			
			@Override
			public void onMapClick(LatLng point) 
			{
    				markerPoints.clear();
					mGoogleMap.clear();
					MarkerOptions options = new MarkerOptions();
					options.position(point);
					options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
					mGoogleMap.addMarker(options);
					
					double latitude = mGoogleMap.getMyLocation().getLatitude();
            		double longitude = mGoogleMap.getMyLocation().getLongitude();
            		
            		GMapV2GetRouteDirection auxiliary = new GMapV2GetRouteDirection();
            		double distancia = auxiliary.CalculationByDistance(latitude, longitude, point.latitude, point.longitude);
					if (distancia < 300) {
						try {
							ayudaServicios servicios = new ayudaServicios();
							servicios.guardarPuntoAlerta("Trafico", codigo_usuario, point.latitude,point.longitude);
							marcarPuntos();
						} catch (Exception e) {
							System.out.println("hiloSecundarioMenu  home");
							onStop();
					        //finish();
						}
						mGoogleMap.setOnMapClickListener(null);
					}
					else
					{
						mGoogleMap.clear();
						Toast.makeText(context,"Necesita estar cercano al TRAFICO", Toast.LENGTH_LONG).show();
					}
					
			}
		});
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
						servicios.guardarPunto("Casa", codigo_usuario, home.latitude,home.longitude);
						marcarPuntos();
					} catch (Exception e) {
						System.out.println("hiloSecundarioMenu  home");
						onStop();
				        //finish();
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
						servicios.guardarPunto("Trabajo", codigo_usuario, work.latitude,work.longitude);
						marcarPuntos();
					} catch (Exception e) {
						System.out.println("hiloSecundarioMenu  work");
						onStop();
						//finish();
					}
					return;
				}
			}
		});
    }
    //------------------------------PRIVATE REFACTORIZACION----------------------
    
    private void marcarPuntos() throws JSONException
    {
    	ayudaServicios aux = new ayudaServicios();
		puntosDeAlerta = aux.getPuntosAlerta();
		puntosDeBloqueo = aux.getPuntosBloqueoPersistente();
		
		if (puntosDeAlerta!=null && puntosDeAlerta.size()>0) {
			for(int i=0 ; i<puntosDeAlerta.size() ; i++)
			{
				markerOptions.position(puntosDeAlerta.get(i));
				markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
				mGoogleMap.addMarker(markerOptions);
			}
		}
		if (puntosDeBloqueo!=null && puntosDeBloqueo.size()>0) {
			for(int i=0 ; i<puntosDeBloqueo.size() ; i++)
			{
				markerOptions.position(puntosDeBloqueo.get(i));
				markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
				mGoogleMap.addMarker(markerOptions);
			}
		}
		
		JSONArray puntoCasaJson= aux.getPunto("Casa", codigo_usuario);
		JSONArray puntoTrabajoJson= aux.getPunto("Trabajo", codigo_usuario);
		if(puntoCasaJson!=null && puntoCasaJson.length() != 0) 	
    	{
			double lat = puntoCasaJson.getJSONObject(0).getDouble("latitude");
    		double lon = puntoCasaJson.getJSONObject(0).getDouble("longitude");
    		
    		markerOptions.position(new LatLng(lat, lon));
			markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
			mGoogleMap.addMarker(markerOptions);
    	}
		if(puntoTrabajoJson!=null && puntoTrabajoJson.length() != 0) 	
    	{
			double lat = puntoTrabajoJson.getJSONObject(0).getDouble("latitude");
    		double lon = puntoTrabajoJson.getJSONObject(0).getDouble("longitude");
    		
    		markerOptions.position(new LatLng(lat, lon));
			markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
			mGoogleMap.addMarker(markerOptions);
    	}
		
    }
    
}
