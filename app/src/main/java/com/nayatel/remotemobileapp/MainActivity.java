package com.nayatel.remotemobileapp;

import static android.view.View.GONE;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.nayatel.remotemobileapp.client.SocketClient;

public class MainActivity extends AppCompatActivity {
    private EditText ipEditText, userNameEditText, passwordEditText, searchEditText;
    private TextView statusTextView;
    private SocketClient socketClient;
    private boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ipEditText = findViewById(R.id.ipEditText);
        userNameEditText = findViewById(R.id.userNameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        searchEditText = findViewById(R.id.searchEditText);
        statusTextView = findViewById(R.id.statusTextView);
        socketClient = new SocketClient();

        findViewById(R.id.connectButton).setOnClickListener(v -> {
            String ip = ipEditText.getText().toString().trim();
            if (!ip.isEmpty()) {
                new Thread(() -> {
                    boolean success = socketClient.connectToServer(ip, 8888);
                    Log.d("remote", "Activity, socketStatus " + success);
                    runOnUiThread(() -> {
                        if (success) {
                            isConnected = true;
//                            ipEditText.setVisibility(GONE);
                            statusTextView.setText("Connected to TV");
                        } else {
                            statusTextView.setText("Connection failed");
                        }
                    });
                }).start();
            }
        });

        userNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                setupTextField("userName", s.toString());
                Log.d("TextWatcher", "Text: " + s.toString());
            }
        });


        setupButton(R.id.volumeUpButton, "VOLUME_UP");
        setupButton(R.id.volumeDownButton, "VOLUME_DOWN");
        setupButton(R.id.dpadUpButton, "DPAD_UP");
        setupButton(R.id.dpadDownButton, "DPAD_DOWN");
        setupButton(R.id.dpadLeftButton, "DPAD_LEFT");
        setupButton(R.id.dpadRightButton, "DPAD_RIGHT");
        setupButton(R.id.dpadCenterButton, "DPAD_CENTER");
        setupButton(R.id.homeButton, "HOME");
        setupButton(R.id.backButton, "BACK");


    }

    private void setupButton(int buttonId, String command) {
        findViewById(buttonId).setOnClickListener(v -> {
            try {
                if (isConnected) {
                    new Thread(() -> {
                        try {
                            socketClient.sendCommand(command);
                            Log.d("remote", "Sent: " + command);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
    private void setupTextField(String fieldName, String command) {
            try {
                if (isConnected) {
                    new Thread(() -> {
                        try {
                            socketClient.sendCommand(command);
                            Log.d("remote", fieldName + "Sent: " + command);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isConnected = false;
        socketClient.closeConnection();
    }
}
