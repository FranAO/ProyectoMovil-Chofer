package com.example.proyectomovil;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyectomovil.Models.HistorialTrip;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RouteActivity extends BaseNavigationActivity {
    private RecyclerView recyclerViewHistorial;
    private HistorialAdapter adapter;
    private List<HistorialTrip> listaHistorial;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_route);

        setupNavigation();

        recyclerViewHistorial = findViewById(R.id.recyclerViewHistorial);
        recyclerViewHistorial.setLayoutManager(new LinearLayoutManager(this));

        listaHistorial = new ArrayList<>();
        adapter = new HistorialAdapter(listaHistorial);
        recyclerViewHistorial.setAdapter(adapter);

        cargarHistorial();
    }

    @Override
    protected int getNavigationIndex() {
        return 1;
    }

    private void cargarHistorial() {
        new Thread(() -> {
            try {
                SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                String email = prefs.getString("LOGGED_IN_USER_EMAIL", "");

                URL urlDriver = new URL("http://10.0.2.2:5090/api/driver/email/" + email);
                HttpURLConnection connDriver = (HttpURLConnection) urlDriver.openConnection();
                connDriver.setRequestMethod("GET");

                BufferedReader readerDriver = new BufferedReader(new InputStreamReader(connDriver.getInputStream()));
                StringBuilder responseDriver = new StringBuilder();
                String lineDriver;
                while ((lineDriver = readerDriver.readLine()) != null) {
                    responseDriver.append(lineDriver);
                }
                readerDriver.close();

                JSONObject driverJson = new JSONObject(responseDriver.toString());
                String driverId = driverJson.getString("Id");

                URL urlTrip = new URL("http://10.0.2.2:5090/api/trip/driver/" + driverId);
                HttpURLConnection connTrip = (HttpURLConnection) urlTrip.openConnection();
                connTrip.setRequestMethod("GET");

                BufferedReader readerTrip = new BufferedReader(new InputStreamReader(connTrip.getInputStream()));
                StringBuilder responseTrip = new StringBuilder();
                String lineTrip;
                while ((lineTrip = readerTrip.readLine()) != null) {
                    responseTrip.append(lineTrip);
                }
                readerTrip.close();

                JSONArray tripsArray = new JSONArray(responseTrip.toString());
                for (int i = 0; i < tripsArray.length(); i++) {
                    JSONObject tripJson = tripsArray.getJSONObject(i);
                    String busId = tripJson.getString("BusId");
                    String routeId = tripJson.getString("RouteId");
                    Date startTime = dateFormatter.parse(tripJson.getString("StartTime"));
                    Date endTime = dateFormatter.parse(tripJson.getString("EndTime"));

                    URL urlBus = new URL("http://10.0.2.2:5090/api/bus/" + busId);
                    HttpURLConnection connBus = (HttpURLConnection) urlBus.openConnection();
                    connBus.setRequestMethod("GET");

                    BufferedReader readerBus = new BufferedReader(new InputStreamReader(connBus.getInputStream()));
                    StringBuilder responseBus = new StringBuilder();
                    String lineBus;
                    while ((lineBus = readerBus.readLine()) != null) {
                        responseBus.append(lineBus);
                    }
                    readerBus.close();

                    JSONObject busJson = new JSONObject(responseBus.toString());
                    String codigoBus = busJson.getString("BusCode");

                    URL urlRoute = new URL("http://10.0.2.2:5090/api/route/" + routeId);
                    HttpURLConnection connRoute = (HttpURLConnection) urlRoute.openConnection();
                    connRoute.setRequestMethod("GET");

                    BufferedReader readerRoute = new BufferedReader(new InputStreamReader(connRoute.getInputStream()));
                    StringBuilder responseRoute = new StringBuilder();
                    String lineRoute;
                    while ((lineRoute = readerRoute.readLine()) != null) {
                        responseRoute.append(lineRoute);
                    }
                    readerRoute.close();

                    JSONObject routeJson = new JSONObject(responseRoute.toString());
                    String nombreRuta = routeJson.getString("RouteName");

                    HistorialTrip historialTrip = new HistorialTrip(codigoBus, nombreRuta, startTime, endTime);
                    listaHistorial.add(historialTrip);
                }

                runOnUiThread(() -> adapter.notifyDataSetChanged());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
