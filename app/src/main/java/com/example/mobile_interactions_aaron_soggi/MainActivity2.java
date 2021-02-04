package com.example.mobile_interactions_aaron_soggi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    Spinner spType;
    Button btFind;
    SupportMapFragment supportMapFragment;
    GoogleMap map;
    FusedLocationProviderClient fusedLocationProviderClient;
    double currentLat = 0, currentLong = 0;
    private static final String KEY_ = "AIzaSyCmPGQIMAu8f2tF3epPb4mr1C3t-CTuadI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        spType = findViewById(R.id.sp_type);
        btFind = findViewById(R.id.bt_find);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);

        //initialize array of place type
        String[] placeTypeList = {"pharmacy", "hospital"};
        //initialize array of place name
        String[] placeNameList = {"Pharmacy", "Hospital"};

        //Set adapter on spinner
        spType.setAdapter(new ArrayAdapter<>(MainActivity2.this, android.R.layout.simple_spinner_dropdown_item, placeNameList));

        //initialize fused location provider client
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //check permission
        if (ActivityCompat.checkSelfPermission(MainActivity2.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // when permission granted , call method
            getCurrentLocation();
        } else {
            // when permission denied
            //request permission
            ActivityCompat.requestPermissions(MainActivity2.this
                    , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);

        }
        btFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get selected position of spinner
                int i = spType.getSelectedItemPosition();
                // initialize url
                String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"+ //url
                "?location=" + currentLat + "," + currentLong + // Location latitude and longitude
                "&radius=5000" + //Nearby radius
                "&types=" + placeTypeList[i] + // place type
                "&sensor=true" + //Sensor
                "&key" + KEY_; //

                //execute place task method to download json data
                new PlaceTask().execute(url);
            }
        });
    }

    private void getCurrentLocation() {
        //initialize task location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // when success
                if(location != null)
                {
                    //when location is not equal to null
                    //get current latitude
                    currentLat = location.getLatitude();
                    //get current longitude
                    currentLong = location.getLongitude();
                    //sync map
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            //when map is ready
                            map = googleMap;
                            //Zoom current location on map
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLat, currentLong), 10));
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 44){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // when permission granted
                //call method
                getCurrentLocation();
            }
        }
    }

    private class PlaceTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... strings) {
            String data = null;
            try {
                //initialize data
               data = downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            //execute parser task
            new ParserTask().execute(s);
        }
    }

    private String downloadUrl(String string) throws IOException {
        //initialize url
        URL url = new URL(string);
        //initialize connection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // connect connection
        connection.connect();
        //initialize input stream
        InputStream stream = connection.getInputStream();
        //initialize buffer reader
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        //initialize string builder
        StringBuilder builder = new StringBuilder();
        //Initialize string variable
        String line = "";
        //use while loop
        while((line = reader.readLine()) != null) {
            //Append line
            builder.append(line);
        }

        //get append data
        String data = builder.toString();
        //close reader
        reader.close();
        //return data
        return data;
    }

    private class ParserTask extends AsyncTask<String,Integer, List<HashMap<String,String>>> {
        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
            // create json parser
            JsonParserr jsonParser = new JsonParserr();
            //initialize hash map list
            List<HashMap<String, String>> mapList= null;

            JSONObject object = null;

            try {
                // initialize json object
               object = new JSONObject(strings[0]);
               // parse json object
                mapList = jsonParser.parseResult(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Return map List
            return mapList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            // clear map
            map.clear();
            //use for loop
            for(int i = 0 ; i<hashMaps.size(); i++ ){
                // initialize hash map
                HashMap<String, String> hashMapList = hashMaps.get(i);
                // get latitude
                double lat = Double.parseDouble(hashMapList.get("lat"));
                // get longitude
                double lng = Double.parseDouble(hashMapList.get("lng"));
                // get name
                String name = hashMapList.get("name");
                //concat latitude and longitude
                LatLng latLng = new LatLng(lat, lng);
                //Initialize marker options
                MarkerOptions options = new MarkerOptions();
                // set position
                options.position(latLng);
                // set title
                options.title(name);
                //add marker on map
                map.addMarker(options);

            }
        }
    }
}