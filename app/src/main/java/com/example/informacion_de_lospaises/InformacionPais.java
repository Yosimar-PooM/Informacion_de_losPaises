package com.example.informacion_de_lospaises;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import WebServices.Asynchtask;
import WebServices.WebService;

public class InformacionPais extends AppCompatActivity implements Asynchtask {

    private ImageView imgbandera;
    private TextView txtcodigo;
    private TextView txtpais;
    private TextView txtcapital;


    private double latitud, longitud;
    public LatLng posMapa;
    private String norte;
    private String sur;
    private String oeste;
    private String este;
    private String codigo;

    private GoogleMap mapa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion_pais);
        txtcapital = findViewById(R.id.txtcapital);
        imgbandera = findViewById(R.id.imgbandera);
        txtpais = findViewById(R.id.txtpais);
        Bundle bundle = this.getIntent().getExtras();
        Map<String, String> datos = new HashMap<String, String>();
        WebService ws = new WebService("http://www.geognos.com/api/en/countries/info/"+bundle.getString("codISO")+".json", datos, InformacionPais.this, InformacionPais.this);
        ws.execute("");
    }

    @Override
    public void processFinish(String result) throws JSONException {

        JSONObject jsonObject = new JSONObject(result);
        JSONObject jsonResults = jsonObject.getJSONObject("Results");
        txtpais.setText(jsonResults.getString("Name"));
        JSONObject jsonCapital = jsonResults.getJSONObject("Capital");
        txtcapital.setText(jsonCapital.getString("Name"));
        JSONObject jsonRectangulo = jsonResults.getJSONObject("GeoRectangle");
        norte = jsonRectangulo.getString("North");
        sur = jsonRectangulo.getString("South");
        este = jsonRectangulo.getString("East");
        oeste = jsonRectangulo.getString("West");
        JSONArray jsonGeoPt = jsonResults.getJSONArray("GeoPt");
        latitud = jsonGeoPt.getDouble(0);
        longitud = jsonGeoPt.getDouble(1);
        JSONObject jsonCiudades = jsonResults.getJSONObject("CountryCodes");

        Glide.with(this).load("http://www.geognos.com/api/en/countries/flag/"+jsonCiudades.getString("iso2")+".png").into(imgbandera);
        ObtenerMapa();
    }

    public void ObtenerMapa(){
        SupportMapFragment fragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.mapa);
        fragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mapa = googleMap;
                posMapa = new LatLng(latitud,longitud);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(posMapa, 5);
                mapa.moveCamera(cameraUpdate);
                DiseñoMarco();
            }
        });
    }
    public void DiseñoMarco(){
        PolylineOptions marco = new PolylineOptions()
                .add(new LatLng(Double.parseDouble(norte),Double.parseDouble(oeste)))
                .add(new LatLng(Double.parseDouble(norte),Double.parseDouble(este)))
                .add(new LatLng(Double.parseDouble(sur),Double.parseDouble(este)))
                .add(new LatLng(Double.parseDouble(sur),Double.parseDouble(oeste)))
                .add(new LatLng(Double.parseDouble(norte),Double.parseDouble(oeste)));

        marco.width(10);
        marco.color(Color.BLUE);
        mapa.addPolyline(marco);
    }
}