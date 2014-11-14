package com.example.maps;

import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;

import com.entropy.slidingmenu2.layout.MainLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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
import android.content.Intent;
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
    Map<LatLng, String> puntosDeBloqueo = new LinkedHashMap<LatLng, String>();
    Map<LatLng, String> puntosDeBloqueoPosibles = new LinkedHashMap<LatLng, String>();
    Map<LatLng, String> puntosDeAlerta = new LinkedHashMap<LatLng, String>();
    Map<LatLng, String> puntosDeAlertaCercanos = new LinkedHashMap<LatLng, String>();
    
    static LatLng before = null;
    static LatLng after = null;
    
    getRouteTask2 getRoute = new getRouteTask2();
    
    ArrayList<LatLng> puntosRuta = new ArrayList<LatLng>();
    //ArrayList<LatLng> puntosDeBloqueo = new ArrayList<LatLng>();
    //ArrayList<LatLng> puntosDeAlerta = new ArrayList<LatLng>();
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
    HashMap<String ,Marker> mapp = new HashMap<String ,Marker> ();
    final notifications notification = new notifications();
	
   // Stack < Marker > pila = new Stack < Marker > ();
    //final ArrayList<Marker> markersAlert = new ArrayList<Marker>();
    
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
            	toggleButton1 = (ToggleButton) findViewById(R.id.toggleButton1);
            	
            	if (toggleButton1.isChecked() && puntosDeLaRuta != null && puntosDeLaRuta.size()==0) {
					toggleButton1.setChecked(false);
				}
            	
            	
            	//ocultar otro menu //mostrar otro
            	LinearLayout linear3 = (LinearLayout) findViewById(R.id.menu_options_10);
				linear3.setVisibility(View.GONE);
				TextView text4 = (TextView) findViewById(R.id.TextView10);
				text4.setVisibility(View.GONE);
            	
            	LinearLayout linear = (LinearLayout) findViewById(R.id.menu_options_1);
				linear.setVisibility(View.GONE);
				
				LinearLayout linear1 = (LinearLayout) findViewById(R.id.menu_options_2);
				linear1.setVisibility(View.VISIBLE);
				LinearLayout linear2 = (LinearLayout) findViewById(R.id.menu_options_3);
				linear2.setVisibility(View.VISIBLE);
				
				TextView text1 = (TextView) findViewById(R.id.TextView01);
				text1.setVisibility(View.GONE);
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
        
     // DIBUJAR RUTA PUNTO INICIAL
        Button btMenuPointIni = (Button) findViewById(R.id.button31);
        btMenuPointIni.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	try {
            		onDrawRutePointClick(v);
				} catch (JSONException e) {
					e.printStackTrace();
				}
            	// Show/hide the menu
            	toggleMenu(v);
            }
        });
        
     // DIBUJAR RUTA DOS PUNTOS
        Button btMenuDosPoint = (Button) findViewById(R.id.button32);
        btMenuDosPoint.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	try {
            		onDrawRuteClick(v);
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
				linear.setVisibility(View.VISIBLE);
				LinearLayout linear1 = (LinearLayout) findViewById(R.id.menu_options_2);
				linear1.setVisibility(View.GONE);
				LinearLayout linear2 = (LinearLayout) findViewById(R.id.menu_options_3);
				linear2.setVisibility(View.GONE);
				
				TextView text1 = (TextView) findViewById(R.id.TextView01);
				text1.setVisibility(View.VISIBLE);
				TextView text2 = (TextView) findViewById(R.id.TextView02);
				text2.setVisibility(View.GONE);
				TextView text3 = (TextView) findViewById(R.id.TextView03);
				text3.setVisibility(View.GONE);
				
				LinearLayout linear3 = (LinearLayout) findViewById(R.id.menu_options_10);
				linear3.setVisibility(View.VISIBLE);
				TextView text4 = (TextView) findViewById(R.id.TextView10);
				text4.setVisibility(View.VISIBLE);
				
				
				// TODO Auto-generated method stub
				
            	if (toggleButton1.isChecked()) {
            		toggleMenu(v);
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
          mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
          mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
          markerOptions = new MarkerOptions();
          
//          marker = mGoogleMap.addMarker(new MarkerOptions()
//      	.position(new LatLng(-17.393827, -66.156991))
//      	.title("San Francisco")
//      	.snippet("Population: 776733"));
          
          //provando la conexion servicios
          
          connectVerify se = new connectVerify();
          se.execute();
          
//          checkNotifications ceh = new checkNotifications();
//	        ceh.execute();
          
    }
    	
//000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000

    private class checkNotifications extends AsyncTask<String, Void, String>
    {
    	private ProgressDialog Dialog;
//    	@Override
//    	protected void onPreExecute() {
//    		Dialog = new ProgressDialog(MainActivity.this);
//            Dialog.setMessage("una mierda no llega");
//            Dialog.show();
//        }
    	
		@Override
		protected String doInBackground(String... params) {
			
			final Runnable r = new Runnable()
				{
				    public void run() 
				    {
				    		controller help = new controller();
					    	puntosDeAlerta = help.getPuntosAlerta();//
					    	puntosDeBloqueo = help.getPuntosBloqueoPersistente();
					    	puntosDeBloqueoPosibles = help.getPuntosPosiblesBloqueoPersistente();
					    	llenarPuntosFavoritos();
					    	
					    	try {
								marcarPuntosAlerta();
								marcarPuntosBloqueo();
								marcarPuntosFavoritos();
								marcarPuntosPosibesBloqueo();
								marcarPuntosDeRuta();
							} catch (JSONException e) {
								e.printStackTrace();
							}
					    	//notificaciones
					    	
//					    	notification.checkNotificationsNew(getPointsMap(puntosDeAlerta));
//					    	double latitude = mGoogleMap.getMyLocation().getLatitude();
//		            		double longitude = mGoogleMap.getMyLocation().getLongitude();
//		            		
//					    	ArrayList<LatLng> list = notification.getNotificationNearToPoint(latitude,longitude);
//					    	
//					    	
//					    	if (list != null && list.size()>0) {
//					    		for (int i = 0; i < list.size(); i++) {
//						    		markerOptions.position(list.get(i));
//									markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
//									mGoogleMap.addMarker(markerOptions);
//						    	}
//							}
					    	handler.postDelayed(this, 10000);
					}
				};
				handler.postDelayed(r, 10000);
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
				Dialog.setMessage("Hola, conexion exitosa");
                Dialog.show();
                try {
                	checkNotifications ceh = new checkNotifications();
        	        ceh.execute();
        	        
        	        
        	        marcarPuntosAlerta();
        	        marcarPuntosBloqueo();
        	        marcarPuntosFavoritos();
        	        marcarPuntosPosibesBloqueo();
        	        
				} catch (Exception e) {
					e.getMessage();
				}
                
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
        		//mGoogleMap.clear();
          	  	rectLine = new PolylineOptions().width(10).color(Color.GREEN);
                Dialog = new ProgressDialog(MainActivity.this);
                Dialog.setMessage("Espere un momento");
                Dialog.show();
                //ayudaServicios help = new ayudaServicios();
    			//puntosDeBloqueo = help.getPuntosBloqueoPersistente();
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
				
				ArrayList<LatLng> listPoint;
				
				puntosDeLaRuta = getRoute.get_route(fromPosition,toPosition,getPointsMap(puntosDeBloqueo));
				if (getRoute.postList == 1) {
					getRoute.postList = 2;
				}
				else{
					if (getRoute.postList == 2) {
						getRoute.postList = 3;
					}
					else{
						if (getRoute.postList == 3) {
							getRoute.postList = 1;
						}
					}
				}
				
				if(puntosDeLaRuta!=null)
				{
					for (int i = 0; i < puntosDeLaRuta.size(); i++) {
						rectLine.add(puntosDeLaRuta.get(i));
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
				
//				for(int i=0 ; i<puntosDeLaRuta.size() ; i++)
//				{
//					markerOptions.position(puntosDeLaRuta.get(i));
//					markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
//					mGoogleMap.addMarker(markerOptions);
//				}
				markerOptions.position(fromPosition);
				markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.m_inicio));
				mGoogleMap.addMarker(markerOptions);
				
				markerOptions.position(toPosition);
				markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.m_fin));
				mGoogleMap.addMarker(markerOptions);
				
				mGoogleMap.addPolyline(rectLine);
		  	  	markerOptions.draggable(true);
		  	  	mGoogleMap.addMarker(markerOptions);
		  	  	
		  	  	Dialog.dismiss();
		  	  	
		  	  	Button start_rute = (Button)findViewById(R.id._ini_ruta);
	        	start_rute.setVisibility(View.VISIBLE);
	        	
	        	Button next_rute = (Button)findViewById(R.id.button1);
	        	next_rute.setVisibility(View.VISIBLE);
	        	
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
  private ArrayList<LatLng> getPointsMap(Map<LatLng, String> puntos)
  {
	  ArrayList<LatLng> result = new ArrayList<LatLng>();
	  for (Map.Entry<LatLng, String> entry : puntosDeBloqueo.entrySet()) {
		  result.add(entry.getKey());
	  }
	  return result;
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
            
            	//JSONArray puntoCasaJson= null;
//            	try {
//            		ayudaServicios servicios = new ayudaServicios();
//            		puntoCasaJson = servicios.getPunto("Casa", codigo_usuario);
//    			} catch (Exception e) {
//    				Toast.makeText(this,"menu get punto casa", Toast.LENGTH_LONG).show();
//    				super.onStop();
//    		        //finish();
//    			}
            	
            	if(home == null)
            	{
            		Toast.makeText(this,"Seleccione Punto a CASA", Toast.LENGTH_LONG).show();
            		homeFalse = "llenar";
            		toggleButton1 = (ToggleButton) findViewById(R.id.toggleButton1);
                	
                	if (toggleButton1.isChecked()) {
    					toggleButton1.setChecked(false);
    				}
            		hiloSecundarioMenu();
            		
            	}
            	else 
            	{
            		Toast.makeText(this,"camino a CASA", Toast.LENGTH_LONG).show();
            		double latitude = mGoogleMap.getMyLocation().getLatitude();
            		double longitude = mGoogleMap.getMyLocation().getLongitude();
            		LatLng Position = new LatLng(latitude, longitude);
            		fromPosition = Position;
            		
            		//double lat = puntoCasaJson.getJSONObject(0).getDouble("latitude");
            		//double lon = puntoCasaJson.getJSONObject(0).getDouble("longitude");
            		
            		toPosition = home;// new LatLng(lat, lon);
            		
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
            
//    		JSONArray puntoTrabajoJson=null;
//    		try {
//    			ayudaServicios servicios = new ayudaServicios();
//        		puntoTrabajoJson = servicios.getPunto("Trabajo", codigo_usuario);
//			} catch (Exception e) {
//				Toast.makeText(this,"menu get punto trabajo", Toast.LENGTH_LONG).show();
//				super.onStop();
//		      //  finish();
//			}
    		
		
    		if(work==null) 	
    		{
    			Toast.makeText(this,"Seleccione punto al TRABAJO", Toast.LENGTH_LONG).show();
    			workFalse = "llenar";
    			toggleButton1 = (ToggleButton) findViewById(R.id.toggleButton1);
            	
            	if (toggleButton1.isChecked()) {
					toggleButton1.setChecked(false);
				}
    			hiloSecundarioMenu();
    			
    		}
    		else 
    		{
    			Toast.makeText(this,"camino al TRABAJO", Toast.LENGTH_LONG).show();
    			double latitude = mGoogleMap.getMyLocation().getLatitude();
    			double longitude = mGoogleMap.getMyLocation().getLongitude();
    			LatLng Position = new LatLng(latitude, longitude);
    			fromPosition = Position;
    		
    			//double lat = puntoTrabajoJson.getJSONObject(0).getDouble("latitude");
    			//double lon = puntoTrabajoJson.getJSONObject(0).getDouble("longitude");
    		
    			toPosition = work;// new LatLng(lat, lon);
				
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
    
    
    private void onDrawRutePointClick( View view) throws JSONException {
    	if (connectVerifyValue) {
    		FragmentManager fm = MainActivity.this.getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment fragment = null;
            
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
                	options.icon(BitmapDescriptorFactory.fromResource(R.drawable.m_inicio));
                	//options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                	mGoogleMap.addMarker(options);
                	//marcarPuntos();
            	//put lisener para el segundo punto
            		methodChangeMode(view);
        }
    	else
        {
        	Toast.makeText(this,"Necesita conexion a internet", Toast.LENGTH_SHORT).show();
        }
    }
    private void onDrawRuteClick( View view) throws JSONException {
    	if (connectVerifyValue) {
    		FragmentManager fm = MainActivity.this.getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment fragment = null;
            
            Toast.makeText(this,"Seleccione el punto origen", Toast.LENGTH_LONG).show();
			methodChangeMode(view);
           
    	}
    	else
        {
        	Toast.makeText(this,"Necesita conexion a internet", Toast.LENGTH_SHORT).show();
        }
    }
    
    
//private void onMenuItemClickRight(AdapterView<?> parent, View view, int position, long id) throws JSONException {
//    	
//    	String selectedItem = lvMenuItemsRight[position];
//        String currentItem = tvTitle.getText().toString();
//        
//        // Do nothing if selectedItem is currentItem
//        if(selectedItem.compareTo(currentItem) == 0) {
//            mainLayout.toggleMenuRight();
//            return;
//        }
//            if (connectVerifyValue) {
//				
//			
//        FragmentManager fm = MainActivity.this.getSupportFragmentManager();
//        FragmentTransaction ft = fm.beginTransaction();
//        Fragment fragment = null;
//
//        
//        if(selectedItem.compareTo("Tomar mi punto como origen") == 0) 
//        {
//        	
//        	//poner punto inicio mi localizacion
//        		Toast.makeText(this,"Selecciones el punto de destino", Toast.LENGTH_LONG).show();
//        		double latitude = mGoogleMap.getMyLocation().getLatitude();
//        		double longitude = mGoogleMap.getMyLocation().getLongitude();
//        		LatLng Position = new LatLng(latitude, longitude);
//        		fromPosition = Position;
//        		markerPoints.add(Position);
//        	//marker
//        		MarkerOptions options = new MarkerOptions();
//            	options.position(Position);
//            	options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//            	mGoogleMap.addMarker(options);
//            	marcarPuntos();
//        	//put lisener para el segundo punto
//        		methodChangeMode(view);
//            	
//            		
//        } 
//        else 
//        	if(selectedItem.compareTo("Seleccionar dos puntos") == 0) 
//        		{
//        			Toast.makeText(this,"Seleccione el punto origen", Toast.LENGTH_LONG).show();
//        			methodChangeMode(view);
//        		}
//        
//        	if(fragment != null) 
//        	{
//            	tvTitle.setText(selectedItem);
//        	}
//        
//        // Hide menu anyway
//        mainLayout.toggleMenuRight();
//            }
//            else
//            {
//            	Toast.makeText(this,"Necesita conexion a internet", Toast.LENGTH_SHORT).show();
//            }
//    }

    
    
    
    
    
    
    
    
    
    
    
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
        					options.icon(BitmapDescriptorFactory.fromResource(R.drawable.m_inicio));
        				}
        				else{ 
        					if(markerPoints.size()==2){
        						options.icon(BitmapDescriptorFactory.fromResource(R.drawable.m_fin));
        					}
        				}
        					// Add marker al map
        				mGoogleMap.addMarker(options);
        				
        				if(markerPoints.size() >= 2)
        				{		
        					fromPosition = markerPoints.get(0);
        					toPosition = markerPoints.get(1);
        					
        					//reiniciar para nuevo ini y fin
        					getRoute = new getRouteTask2();
        					
        		        	obtainRoute help = new obtainRoute();
        		        	help.execute();
        		        	punteroRuta = 0;
        		        	mGoogleMap.setOnMapClickListener(null);
        		        	try {
								marcarPuntosAlerta();
								marcarPuntosBloqueo();
								marcarPuntosFavoritos();
								marcarPuntosPosibesBloqueo();
							} catch (JSONException e) {
								e.printStackTrace();
							}
        		        	
        		        	//boton visible iniciar ruta
        		        	Button start_rute = (Button)findViewById(R.id._ini_ruta);
        		        	start_rute.setVisibility(View.VISIBLE);
        		        	
        		        	Button next_rute = (Button)findViewById(R.id.button1);
        		        	next_rute.setVisibility(View.VISIBLE);
        		        }
        				
        				
        			}
        		});
    		}
        	else{
        		puntosDeLaRuta.clear();
        		markerPoints.clear();
        		//puntosDeLaRuta.clear();
        		//mGoogleMap.clear();
        		mGoogleMap.setOnMapClickListener(null);
        		
        		marcarPuntosAlerta();
        		marcarPuntosBloqueo();
        		marcarPuntosFavoritos();
        		marcarPuntosPosibesBloqueo();
        		
        		CameraPosition cameraPosition2 = new CameraPosition.Builder().target(new LatLng(-17.393792, -66.157110)).zoom(12).bearing(0).tilt(0).build();
	            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition2));
        		
                Button start_rute = (Button)findViewById(R.id._ini_ruta);
	        	start_rute.setVisibility(View.INVISIBLE);
	        	
	        	Button next_rute = (Button)findViewById(R.id.button1);
	        	next_rute.setVisibility(View.INVISIBLE);
	        	
	             //ver al inicio los puntos debloqueo

	          	  //ayudaServicios help = new ayudaServicios();
	                //puntosDeBloqueo = help.getPuntosBloqueoPersistente();
	        	
//	                for(int i=0 ; i<puntosDeBloqueo.size() ; i++)
//	  	  			{
//	  	  				markerOptions.position(puntosDeBloqueo.get(i));
//	  	  				markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//	  	  				mGoogleMap.addMarker(markerOptions);
//	  	  			}

        	}
		} catch (Exception e) {
			e.getMessage();
		}
    	
    }
    
    public void siguiente_camino(final View v)
    {
    	obtainRoute help = new obtainRoute();
    	help.execute();
    	punteroRuta = 0;
//    	mGoogleMap.setOnMapClickListener(null);
    	try {
			marcarPuntosAlerta();
			marcarPuntosBloqueo();
			marcarPuntosFavoritos();
			marcarPuntosPosibesBloqueo();
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	
    	//boton visible iniciar ruta
    	Button start_rute = (Button)findViewById(R.id._ini_ruta);
    	start_rute.setVisibility(View.VISIBLE);
    	
    	Button next_rute = (Button)findViewById(R.id.button1);
    	next_rute.setVisibility(View.VISIBLE);
    }
    
    public void iniciar_recorrido(final View v)
    {
    	puntosRuta.clear();
    	puntosRuta.add(puntosDeLaRuta.get(0));
		double distance7;
		double x11,x22,y11,y22;
		GMapV2GetRouteDirection route = new GMapV2GetRouteDirection();
		for (int i = 0; i < puntosDeLaRuta.size()-1; i++) {
			x11 = puntosDeLaRuta.get(i).latitude;
			y11 = puntosDeLaRuta.get(i).longitude;
    		
			x22 = puntosDeLaRuta.get(i+1).latitude;
			y22 = puntosDeLaRuta.get(i+1).longitude;
			distance7 = route.CalculationByDistance(x11, y11, x22,y22);
			if (distance7>15) {
				puntosRuta.add(puntosDeLaRuta.get(i+1));
			}
		}
		
		double latitude = mGoogleMap.getMyLocation().getLatitude();
		double longitude = mGoogleMap.getMyLocation().getLongitude();
		LatLng myPos = new LatLng(latitude, longitude);
		CameraPosition cameraPosition2 = new CameraPosition.Builder().target(myPos).zoom(19).bearing(0).tilt(45).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition2));
		
    	mGoogleMap.setOnMapClickListener(new OnMapClickListener(){
			
    		
    		
			@Override
			public void onMapClick(LatLng point) 
			{
				
				if (puntosRuta.size() > 0) {
					
					
					
					before = null;
					after = null;
					GMapV2GetRouteDirection route = new GMapV2GetRouteDirection();

					LatLng punto2=puntosRuta.get(0);

					double distance1 = route.CalculationByDistance(point.latitude,point.longitude, punto2.latitude,punto2.longitude);
					int pos = 0;
					for (int i = 1; i < puntosRuta.size(); i++) {
						double distance = route.CalculationByDistance(point.latitude,point.longitude,puntosRuta.get(i).latitude,puntosRuta.get(i).longitude);
						
						if(distance<distance1)
						{	
							pos = i;
							punto2 = puntosRuta.get(i);
							distance1 = distance;
						}
					}
					if (puntosRuta.size()>pos+1) {
						after = puntosRuta.get(pos+1);
					}
					if (pos>0) {
						before = puntosRuta.get(pos-1);
					}
					
					//getRouteTask2 aux = new getRouteTask2();

					if (before == null) {
						servicePlayAudioStraight(v);
						punto2 = after;
						Handler handler = new Handler();
				        handler.postDelayed(new Runnable() {
				            public void run() {
				                // acciones que se ejecutan tras los milisegundos
				                serviceStopAudioStraight(v);;
				            }
				        }, 5000);
					}
					else
						if (after == null) {
							servicePlayAudioStraight(v);
							Handler handler = new Handler();
					        handler.postDelayed(new Runnable() {
					            public void run() {
					                // acciones que se ejecutan tras los milisegundos
					                serviceStopAudioStraight(v);;
					            }
					        }, 5000);
						}
						else
						{
							double angulo1 = getRoute.finall(before.latitude,before.longitude,punto2.latitude,punto2.longitude);
							double angulo2 = getRoute.finall(punto2.latitude, punto2.longitude,after.latitude, after.longitude);
							double angulo3 = getRoute.finall(point.latitude, point.longitude,punto2.latitude,punto2.longitude);
							
							double resp = angulo1 - angulo2;
							double resp2 = angulo3 - angulo2;
							
							if ( (resp<10 && resp>-10) ||  (resp>350 && resp<-350)){//mismo sentido
								if ( (resp2<10 && resp2>-10) ||  (resp2>350 && resp2<-350)){//mismo sentido
									servicePlayAudioStraight(v);
								}
								else{//puede existir otro if pero creo que no ayudaria
									servicePlayAudioStraight(v);
									punto2 = after;
								}
								Handler handler = new Handler();
						        handler.postDelayed(new Runnable() {
						            public void run() {
						                // acciones que se ejecutan tras los milisegundos
						                serviceStopAudioStraight(v);;
						            }
						        }, 5000);
							}
							else{
								if((resp>-100 && resp<-80)||(resp>260 && resp<280)) { //giro derecha
									if((resp2>-100 && resp2<-80)||(resp2>260 && resp2<280)) { //giro derecha
										servicePlayAudioRight(v);
										Handler handler = new Handler();
								        handler.postDelayed(new Runnable() {
								            public void run() {
								                // acciones que se ejecutan tras los milisegundos
								                serviceStopAudioRight(v);;
								            }
								        }, 5000);
									}
									else{
										servicePlayAudioStraight(v);
										punto2 = after;
										Handler handler = new Handler();
								        handler.postDelayed(new Runnable() {
								            public void run() {
								                // acciones que se ejecutan tras los milisegundos
								                serviceStopAudioStraight(v);;
								            }
								        }, 5000);
									}
								}
								else{
									if ((resp>80 && resp<100)||(resp<-260 && resp>-280)) {//giro izq
										if ((resp2>80 && resp2<100)||(resp2<-260 && resp2>-280)) {//giro izq
											servicePlayAudioLeft(v);
											Handler handler = new Handler();
									        handler.postDelayed(new Runnable() {
									            public void run() {
									                // acciones que se ejecutan tras los milisegundos
									                serviceStopAudioLeft(v);;
									            }
									        }, 5000);
										}
										else{
											servicePlayAudioStraight(v);
											punto2 = after;
											Handler handler = new Handler();
									        handler.postDelayed(new Runnable() {
									            public void run() {
									                // acciones que se ejecutan tras los milisegundos
									                serviceStopAudioStraight(v);;
									            }
									        }, 5000);
										}
									}
								}
							}
						}
					
//					markerOptions.position(punto2);
//					markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue_home));
//					mGoogleMap.addMarker(markerOptions);
					
					if (before !=null) {
//						markerOptions.position(before);
//						markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_yellow_policia));
//			    		mGoogleMap.addMarker(markerOptions);
					}
					
					if (after !=null) {
//						markerOptions.position(after);
//						markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_red_school));
//						mGoogleMap.addMarker(markerOptions);
					}
					
					markerOptions.position(point);
		    		markerOptions.icon(BitmapDescriptorFactory.defaultMarker());
					mGoogleMap.addMarker(markerOptions);
		    		
					double x1 = point.latitude;
					double y1 = point.longitude;
					
					double x2 = punto2.latitude;
					double y2 = punto2.longitude;
					
					double m = ((y2 - y1)/(x2-x1));
					
		    		double atamm = Math.atan(m);
		    		double grados = Math.toDegrees(atamm);
		    	
		    		int x = (int) (grados);
		    		
		    		if (x1<x2 &&  y1<y2) {
		    			x = x;//primer cuadrante
		    		}
		    		else{
		    			if (x1>x2 &&  y1>y2) {
		    				x = (x)+180;//tercer cuadrante
		    			}
		    			else{
		    				if (x1<x2 &&  y1>y2) {
		    					x = 360+x;
		    				}
		    				else{
		    					if (x1>x2 &&  y1<y2) {
		    						x = (90+x)+90;//cuarto cuadrante
								}
		    					
		    				}
		    			}
		    		}
		    		try 
					{
						CameraPosition cameraPosition2 = new CameraPosition.Builder().target(point).zoom(19).bearing(x).tilt(45).build();
	    	            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition2));
	    	            punteroRuta+=1;
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println(e.getMessage());
					}
		    	}
				else
					mGoogleMap.setOnMapClickListener(null);
			}
		});
    	
    	//puntosDeLaRuta
    		
	}
    
//    public void iniciar_recorrido(View v)
//    {
//    	//puntosDeLaRuta
//    	if (puntosDeLaRuta.size() > 0) {
//    	   	double latitude = mGoogleMap.getMyLocation().getLatitude();
//			double longitude = mGoogleMap.getMyLocation().getLongitude();
//			LatLng myPos = new LatLng(latitude, longitude);
//			
//    		getRouteTask2 help = new getRouteTask2();
//    		
//    		int res = punteroRuta;
//    		
//    		if (puntosDeLaRuta.size() > res+1) {
//    			
//	    		//int res = help.verificarMyPosCercaCamino(puntosDeLaRuta.get(puntosDeLaRuta.size()-4),puntosDeLaRuta);
//	    		
//	    		double x1 = puntosDeLaRuta.get(res).latitude;
//	    		double y1 = puntosDeLaRuta.get(res).longitude;
//	    		
//	    		double x2 = puntosDeLaRuta.get(res+1).latitude;
//	    		double y2 = puntosDeLaRuta.get(res+1).longitude;
//	    		
//	    		GMapV2GetRouteDirection route = new GMapV2GetRouteDirection();
//	    		double distance = route.CalculationByDistance(x1, y1, x2,y2);
//	    		
//	    		while (distance<7 && puntosDeLaRuta.size() > res+2) {
//					punteroRuta +=1;
//					res +=1;
//					x1 = puntosDeLaRuta.get(res).latitude;
//		    		y1 = puntosDeLaRuta.get(res).longitude;
//		    		
//		    		x2 = puntosDeLaRuta.get(res+1).latitude;
//		    		y2 = puntosDeLaRuta.get(res+1).longitude;
//		    		
//		    		distance = route.CalculationByDistance(x1, y1, x2,y2);
//				}
//	    		
//	    		double m = ((y2 - y1)/(x2-x1));
//	    		
//	    		System.out.println("pendiente recta: "+m);
//	    		
//	    		double atamm = Math.atan(m);
//	    		double grados = Math.toDegrees(atamm);
//	    		
//	    		System.out.println("atan: "+atamm);
//	    		System.out.println("grados1: "+grados);
//	    		
//	    		
//	    		int x = (int) (grados);
//	    		System.out.println("grados2: "+x);
//    			
//	    		if (x1<x2 &&  y1<y2) {
//	    			x = x;//primer cuadrante
//	    		}
//	    		else{
//	    			if (x1>x2 &&  y1>y2) {
//	    				x = (x)+180;//tercer cuadrante
//	    			}
//	    			else{
//	    				if (x1<x2 &&  y1>y2) {
//	    					System.out.println("llego: "+x1+" "+y1+" "+x2+" "+y2);
//	    					x = 360+x;
//	    				}
//	    				else{
//	    					if (x1>x2 &&  y1<y2) {
//	    						System.out.println("llego: "+x1+" "+y1+" "+x2+" "+y2);
//	    						x = (90+x)+90;//cuarto cuadrante
//							}
//	    					
//	    				}
//	    			}
//	    		}
//	    		try 
//				{
//					System.out.println("vamos: "+x);
//					
//					CameraPosition cameraPosition2 = new CameraPosition.Builder().target(puntosDeLaRuta.get(res)).zoom(19).bearing(x).tilt(45).build();
//    	            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition2));
//    	            punteroRuta+=1;
//				} catch (Exception e) {
//					// TODO: handle exception
//					System.out.println(e.getMessage());
//				}
//    		}
//    	}	
//	}
    
    public void hiloSecundarioMenuAlertaAccident()
    {
    	mGoogleMap.setOnMapClickListener(new OnMapClickListener(){
			
			@Override
			public void onMapClick(LatLng point) 
			{
    				markerPoints.clear();
					//mGoogleMap.clear();
					MarkerOptions options = new MarkerOptions();
					options.position(point);
					options.icon(BitmapDescriptorFactory.fromResource(R.drawable.m_a_n_accidente));
					mGoogleMap.addMarker(options);
					
					double latitude = mGoogleMap.getMyLocation().getLatitude();
            		double longitude = mGoogleMap.getMyLocation().getLongitude();
            		
            		GMapV2GetRouteDirection auxiliary = new GMapV2GetRouteDirection();
            		double distancia = auxiliary.CalculationByDistance(latitude, longitude, point.latitude, point.longitude);
					if (distancia < 300) {
						try {
							controller servicios = new controller();
							servicios.guardarPuntoAlerta("Accidente", codigo_usuario, point.latitude,point.longitude);
							//marcarPuntos();
							
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
					//mGoogleMap.clear();
					MarkerOptions options = new MarkerOptions();
					options.position(point);
					options.icon(BitmapDescriptorFactory.fromResource(R.drawable.m_a_n_redada));
					mGoogleMap.addMarker(options);
					
					double latitude = mGoogleMap.getMyLocation().getLatitude();
            		double longitude = mGoogleMap.getMyLocation().getLongitude();
            		
            		GMapV2GetRouteDirection auxiliary = new GMapV2GetRouteDirection();
            		double distancia = auxiliary.CalculationByDistance(latitude, longitude, point.latitude, point.longitude);
					if (distancia < 300) {
						try {
							controller servicios = new controller();
							servicios.guardarPuntoAlerta("Redada", codigo_usuario, point.latitude,point.longitude);
							//marcarPuntos();
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
					//mGoogleMap.clear();
					MarkerOptions options = new MarkerOptions();
					options.position(point);
					options.icon(BitmapDescriptorFactory.fromResource(R.drawable.m_r_mercado));
//					options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
					mGoogleMap.addMarker(options);
					
						try {
							controller servicios = new controller();
							servicios.guardarPuntoBloqueo("Mercado",codigo_usuario, point.latitude,point.longitude);
							//marcarPuntos();
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
					//mGoogleMap.clear();
					MarkerOptions options = new MarkerOptions();
					options.position(point);
					options.icon(BitmapDescriptorFactory.fromResource(R.drawable.m_r_colegio));
//					options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
					mGoogleMap.addMarker(options);
						try {
							controller servicios = new controller();
							servicios.guardarPuntoBloqueo("Colegio",codigo_usuario, point.latitude,point.longitude);
							//marcarPuntos();
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
					//mGoogleMap.clear();
					MarkerOptions options = new MarkerOptions();
					options.position(point);
					options.icon(BitmapDescriptorFactory.fromResource(R.drawable.m_a_n_trafico));
//					options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
					mGoogleMap.addMarker(options);
					
					double latitude = mGoogleMap.getMyLocation().getLatitude();
            		double longitude = mGoogleMap.getMyLocation().getLongitude();
            		
            		GMapV2GetRouteDirection auxiliary = new GMapV2GetRouteDirection();
            		double distancia = auxiliary.CalculationByDistance(latitude, longitude, point.latitude, point.longitude);
					if (distancia < 300) {
						try {
							controller servicios = new controller();
							servicios.guardarPuntoAlerta("Trafico", codigo_usuario, point.latitude,point.longitude);
							//marcarPuntos();
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
					//mGoogleMap.clear();
					home = point;
					MarkerOptions options = new MarkerOptions();
					options.position(point);
					options.icon(BitmapDescriptorFactory.fromResource(R.drawable.m_f_casa));
					mGoogleMap.addMarker(options);
					homeFalse = "oto";
					
					try {
						controller servicios = new controller();
						servicios.guardarPunto("Casa", codigo_usuario, home.latitude,home.longitude);
						//marcarPuntos();
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
					//mGoogleMap.clear();
					work = point;
					MarkerOptions options = new MarkerOptions();
					options.position(point);
					options.icon(BitmapDescriptorFactory.fromResource(R.drawable.m_f_trabajo));
					mGoogleMap.addMarker(options);
					workFalse = "oto";
					
					try {
						controller servicios = new controller();
						servicios.guardarPunto("Trabajo", codigo_usuario, work.latitude,work.longitude);
						//marcarPuntos();
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
    
    private void marcarPuntosFavoritos() throws JSONException
    {
    	markerOptions.title("Favorito");
		
    	if(home!=null ) 	
    	{
			markerOptions.position(home);
    		markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.m_f_casa));
    		markerOptions.snippet("Casa");
			mGoogleMap.addMarker(markerOptions);
		}
		if(work!=null ) 	
    	{
			markerOptions.position(work);
			markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.m_f_trabajo));
			markerOptions.snippet("Trabajo");
			mGoogleMap.addMarker(markerOptions);
    	}
		
    }
    private void marcarPuntosBloqueo() throws JSONException
    {
    	if (puntosDeBloqueo!=null && puntosDeBloqueo.size()>0) {
			for (Map.Entry<LatLng, String> entry : puntosDeBloqueo.entrySet()) {
				markerOptions.position(entry.getKey());
				if (entry.getValue().equals("Colegio")) 
				{
					markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.m_b_colegio));
					markerOptions.title("Bloqueo");
					markerOptions.snippet("Colegio");
					mGoogleMap.addMarker(markerOptions);
				}
				else
				{
					markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.m_b_mercado));
					markerOptions.title("Bloqueo");
					markerOptions.snippet("Mercado");
					mGoogleMap.addMarker(markerOptions);
				}
			}
		}
	}
    private void marcarPuntosPosibesBloqueo() throws JSONException
    {
    	if (puntosDeBloqueoPosibles!=null && puntosDeBloqueoPosibles.size()>0) {
			for (Map.Entry<LatLng, String> entry : puntosDeBloqueoPosibles.entrySet()) {
				markerOptions.position(entry.getKey());
				if (entry.getValue().equals("Colegio")) 
				{
					markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.m_r_colegio));
					markerOptions.title("Bloqueo");
					markerOptions.snippet("Colegio");
					mGoogleMap.addMarker(markerOptions);
				}
				else
				{
					markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.m_r_mercado));
					markerOptions.title("Bloqueo");
					markerOptions.snippet("Mercado");
					mGoogleMap.addMarker(markerOptions);
				}
			}
		}
	}
    private void marcarPuntosAlerta() throws JSONException
    {
    	//borrar los puntos anteriores
    	Map<LatLng, String> list = null;
    	try {
    		notification.checkNotificationsNew(puntosDeAlerta);
        	double latitude = mGoogleMap.getMyLocation().getLatitude();
    		double longitude = mGoogleMap.getMyLocation().getLongitude();
    		
    		list = notification.getNotificationNearToPoint(latitude, longitude);
        } catch (Exception e) {
			// TODO: handle exception
		}
    	
    	mGoogleMap.clear();
		
		if (puntosDeAlerta!=null && puntosDeAlerta.size()>0) {
			for (Map.Entry<LatLng, String> entry : puntosDeAlerta.entrySet()) {
				
				
				MarkerOptions marke = new MarkerOptions();
				marke.position(entry.getKey());
				marke.title("Alerta");
					if (entry.getValue().equals("Accidente")) 
					{
						if (list!=null && list.size()>0 && list.containsKey(entry.getKey())) {
							marke.icon(BitmapDescriptorFactory.fromResource(R.drawable.m_a_n_accidente));
						}
						else
						{
							marke.icon(BitmapDescriptorFactory.fromResource(R.drawable.m_a_accidente));
						}
						marke.snippet("Accidente");
					}
					else
					{
						if (entry.getValue().equals("Trafico")) {
							if (list!=null && list.size()>0 && list.containsKey(entry.getKey())) {
								marke.icon(BitmapDescriptorFactory.fromResource(R.drawable.m_a_n_trafico));
							}
							else
							{
								marke.icon(BitmapDescriptorFactory.fromResource(R.drawable.m_a_trafico));
							}
							
							marke.snippet("Trafico");
						}
						else
						{
							if (entry.getValue().equals("Redada")){
								if (list!=null && list.size()>0 && list.containsKey(entry.getKey())) {
									marke.icon(BitmapDescriptorFactory.fromResource(R.drawable.m_a_n_redada));
								}
								else
								{
									marke.icon(BitmapDescriptorFactory.fromResource(R.drawable.m_a_redada));
								}
							}
							marke.snippet("Redada");
						}
					}
					
					try {
						mGoogleMap.addMarker(marke);
					} catch (Exception e) {
						e.getMessage();
						e.getMessage();
						// TODO: handle exception
					}
			}
		}
	}
    
    private void marcarPuntosDeRuta()
    {
    	rectLine = new PolylineOptions().width(10).color(Color.GREEN);

    	if(puntosRuta != null && puntosRuta.size()>0)
		{
			for (int i = 0; i < puntosRuta.size(); i++) {
				rectLine.add(puntosRuta.get(i));
				
				markerOptions.position(puntosRuta.get(i));
				markerOptions.title("pos: "+i);
				markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
				mGoogleMap.addMarker(markerOptions);
			}
			
			mGoogleMap.addPolyline(rectLine);
			markerOptions.position(fromPosition);
			markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.m_inicio));
    		mGoogleMap.addMarker(markerOptions);
			
			markerOptions.position(toPosition);
    		markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.m_fin));
			mGoogleMap.addMarker(markerOptions);
		}
		else{
			if(puntosDeLaRuta != null && puntosDeLaRuta.size()>0){
				
				
				for (int i = 0; i < puntosDeLaRuta.size(); i++) {

					rectLine.add(puntosDeLaRuta.get(i));
					
					markerOptions.position(puntosDeLaRuta.get(i));
					markerOptions.title("pos: "+i);
					markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
					mGoogleMap.addMarker(markerOptions);
				}
				mGoogleMap.addPolyline(rectLine);
				markerOptions.position(fromPosition);
				markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.m_inicio));
	    		mGoogleMap.addMarker(markerOptions);
				
				markerOptions.position(toPosition);
	    		markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.m_fin));
				mGoogleMap.addMarker(markerOptions);
			}
		}
    }
    private void llenarPuntosFavoritos()
    {
    	JSONArray puntoCasaJson= null;
    	JSONArray puntoTrabajoJson= null;
    	
    	controller aux = new controller();
    	puntoCasaJson = aux.getPunto("Casa", codigo_usuario);
    	puntoTrabajoJson = aux.getPunto("Trabajo", codigo_usuario);
    	
    	if(puntoCasaJson!=null && puntoCasaJson.length() > 0) 	
    	{
    		double lat=0 ,lon = 0;
			try {
				lat = puntoCasaJson.getJSONObject(0).getDouble("latitude");
				lon = puntoCasaJson.getJSONObject(0).getDouble("longitude");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		home = new LatLng(lat, lon);
    	}
    	if(puntoTrabajoJson!=null && puntoTrabajoJson.length() > 0) 	
    	{
    		double lat=0 ,lon = 0;
			try {
				lat = puntoTrabajoJson.getJSONObject(0).getDouble("latitude");
				lon = puntoTrabajoJson.getJSONObject(0).getDouble("longitude");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		work = new LatLng(lat, lon);
    	}
    }
    public void servicePlayAudioLeft(View view) {
		Intent objIntent = new Intent(this, PlayAudioLeft.class);
		startService(objIntent);
	}

	public void serviceStopAudioLeft(View view) {
		Intent objIntent = new Intent(this, PlayAudioLeft.class);
		stopService(objIntent);    
	}
	public void servicePlayAudioRight(View view) {
		Intent objIntent = new Intent(this, PlayAudioRight.class);
		startService(objIntent);
	}

	public void serviceStopAudioRight(View view) {
		Intent objIntent = new Intent(this, PlayAudioRight.class);
		stopService(objIntent);    
	}
	public void servicePlayAudioStraight(View view) {
			Intent objIntent = new Intent(this, PlayAudio.class);
			startService(objIntent);
	}

	public void serviceStopAudioStraight(View view) {
		Intent objIntent = new Intent(this, PlayAudio.class);
		stopService(objIntent);    
	}
	    
}
