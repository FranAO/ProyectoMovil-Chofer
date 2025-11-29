package com.example.proyectomovil;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyectomovil.Models.Student;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PassengersActivity extends BaseNavigationActivity {
    private RecyclerView recyclerViewPasajeros;
    private PassengerAdapter adapter;
    private List<Student> listaPasajeros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_passengers);

        setupNavigation();

        recyclerViewPasajeros = findViewById(R.id.recyclerViewPasajeros);
        recyclerViewPasajeros.setLayoutManager(new LinearLayoutManager(this));

        listaPasajeros = new ArrayList<>();
        adapter = new PassengerAdapter(listaPasajeros);
        recyclerViewPasajeros.setAdapter(adapter);

        cargarPasajeros();
    }

    @Override
    protected int getNavigationIndex() {
        return 2;
    }

    private void cargarPasajeros() {
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
                if (tripsArray.length() > 0) {
                    JSONObject tripActivo = tripsArray.getJSONObject(0);
                    String tripId = tripActivo.getString("Id");

                    URL urlPassengers = new URL("http://10.0.2.2:5090/api/passengerintrip/" + tripId);
                    HttpURLConnection connPassengers = (HttpURLConnection) urlPassengers.openConnection();
                    connPassengers.setRequestMethod("GET");

                    BufferedReader readerPassengers = new BufferedReader(new InputStreamReader(connPassengers.getInputStream()));
                    StringBuilder responsePassengers = new StringBuilder();
                    String linePassengers;
                    while ((linePassengers = readerPassengers.readLine()) != null) {
                        responsePassengers.append(linePassengers);
                    }
                    readerPassengers.close();

                    JSONArray passengersArray = new JSONArray(responsePassengers.toString());
                    for (int i = 0; i < passengersArray.length(); i++) {
                        JSONObject passengerJson = passengersArray.getJSONObject(i);
                        String studentId = passengerJson.getString("StudentId");

                        URL urlStudent = new URL("http://10.0.2.2:5090/api/student/" + studentId);
                        HttpURLConnection connStudent = (HttpURLConnection) urlStudent.openConnection();
                        connStudent.setRequestMethod("GET");

                        BufferedReader readerStudent = new BufferedReader(new InputStreamReader(connStudent.getInputStream()));
                        StringBuilder responseStudent = new StringBuilder();
                        String lineStudent;
                        while ((lineStudent = readerStudent.readLine()) != null) {
                            responseStudent.append(lineStudent);
                        }
                        readerStudent.close();

                        JSONObject studentJson = new JSONObject(responseStudent.toString());
                        Student student = new Student(
                            studentJson.getString("Id"),
                            studentJson.getString("FirstName"),
                            studentJson.getString("LastName"),
                            studentJson.getString("Email"),
                            studentJson.optString("Phone", ""),
                            studentJson.optString("PasswordHash", ""),
                            studentJson.optString("Role", "student"),
                            null
                        );
                        listaPasajeros.add(student);
                    }

                    runOnUiThread(() -> adapter.notifyDataSetChanged());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
