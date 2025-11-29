package com.example.proyectomovil;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.concurrent.Executor;

public class SettingsActivity extends AppCompatActivity {

    private CardView cardBackButton;
    private CardView logoutCard;
    private TextView tvUserName;
    private SwitchMaterial switchBiometric;

    private SharedPreferences prefs;
    private static final String PREFS_NAME = "MiAppPrefs";
    private static final String BIOMETRIC_USER_ID = "BIOMETRIC_USER_ID";
    private static final String LOGGED_IN_USER_EMAIL = "LOGGED_IN_USER_EMAIL";

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        cardBackButton = findViewById(R.id.card_back_button);
        logoutCard = findViewById(R.id.logoutCard);
        tvUserName = findViewById(R.id.tv_user_name);
        switchBiometric = findViewById(R.id.switch_biometric);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        currentUserEmail = prefs.getString(LOGGED_IN_USER_EMAIL, "chofer@correo.com");

        tvUserName.setText(currentUserEmail);
        loadSwitchState();
        setupBiometrics();

        cardBackButton.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        logoutCard.setOnClickListener(v -> {
            promptLogout();
        });

        switchBiometric.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                authenticateToEnable();
            } else {
                saveBiometricLink(null);
                Toast.makeText(this, "Huella deshabilitada", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSwitchState() {
        String savedUser = prefs.getString(BIOMETRIC_USER_ID, null);
        switchBiometric.setChecked(savedUser != null && savedUser.equals(currentUserEmail));
    }

    private void setupBiometrics() {
        executor = ContextCompat.getMainExecutor(this);

        biometricPrompt = new BiometricPrompt(SettingsActivity.this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        Toast.makeText(SettingsActivity.this, "Error: " + errString, Toast.LENGTH_SHORT).show();
                        switchBiometric.setChecked(false);
                    }

                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        Toast.makeText(SettingsActivity.this, "¡Huella habilitada!", Toast.LENGTH_SHORT).show();
                        saveBiometricLink(currentUserEmail);
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(SettingsActivity.this, "Fallo de autenticación", Toast.LENGTH_SHORT).show();
                        switchBiometric.setChecked(false);
                    }
                });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Confirmar para habilitar")
                .setSubtitle("Confirma tu huella para activar el inicio de sesión")
                .setNegativeButtonText("Cancelar")
                .build();
    }

    private void authenticateToEnable() {
        String savedUser = prefs.getString(BIOMETRIC_USER_ID, null);

        if (savedUser != null && !savedUser.equals(currentUserEmail)) {

            new AlertDialog.Builder(this)
                    .setTitle("Advertencia")
                    .setMessage("La huella ya está enlazada a otro usuario (" + savedUser + "). ¿Deseas reemplazarla y enlazarla a " + currentUserEmail + "?")
                    .setPositiveButton("Reemplazar", (dialog, which) -> {
                        requestBiometricAuth();
                    })
                    .setNegativeButton("Cancelar", (dialog, which) -> {
                        switchBiometric.setChecked(false);
                    })
                    .show();

        } else {
            requestBiometricAuth();
        }
    }

    private void requestBiometricAuth() {
        BiometricManager biometricManager = BiometricManager.from(this);
        int authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG;

        if (biometricManager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS) {
            biometricPrompt.authenticate(promptInfo);
        } else {
            Toast.makeText(this, "No se puede usar la huella en este dispositivo.", Toast.LENGTH_SHORT).show();
            switchBiometric.setChecked(false);
        }
    }

    private void saveBiometricLink(String email) {
        SharedPreferences.Editor editor = prefs.edit();
        if (email == null) {
            editor.remove(BIOMETRIC_USER_ID);
        } else {
            editor.putString(BIOMETRIC_USER_ID, email);
        }
        editor.apply();
    }

    private void promptLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar Sesión")
                .setMessage("¿Estás seguro de que quieres cerrar la sesión?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    performLogout();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void performLogout() {
        prefs.edit()
                .remove(LOGGED_IN_USER_EMAIL)
                .remove(BIOMETRIC_USER_ID)
                .apply();

        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}