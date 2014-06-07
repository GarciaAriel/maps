package com.example.maps;

import java.util.ArrayList;
import java.util.List;



import com.entropy.slidingmenu2.fragment.FragmentListView;
import com.entropy.slidingmenu2.fragment.FragmentMain;
import com.entropy.slidingmenu2.layout.MainLayout;
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
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
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


public class MainActivity extends FragmentActivity {
	
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
                onMenuItemClick(parent, view, position, id);
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
    // MENU ==============================================================
    

    public void toggleMenu(View v){
        mainLayout.toggleMenu();
    }
    
    private void onMenuItemClick(AdapterView<?> parent, View view, int position, long id) {
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

        
        if(selectedItem.compareTo("Casa") == 0) {
            fragment = new FragmentMain();
        } else if(selectedItem.compareTo("Trabajo") == 0) {
            fragment = new FragmentListView();
        }
        
        
        if(fragment != null) {
            // Replace current fragment by this new one
//            ft.replace(R.id.activity_main_content_fragment, fragment);
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
