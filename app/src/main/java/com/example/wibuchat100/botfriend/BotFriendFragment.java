package com.example.wibuchat100.botfriend;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wibuchat100.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BotFriendFragment extends Fragment {

    RecyclerView recyclerMensajes;
    EditText inputMensaje;
    ImageButton btnEnviar;
    ProgressBar progressBar;

    List<MensajeBot> listaMensajes = new ArrayList<>();
    BotMensajeAdapter adapter;

    // Historial para que el bot recuerde el contexto
    JSONArray historial = new JSONArray();

    // ← PON AQUÍ TU API KEY DE GOOGLE AI STUDIO
    private static final String API_KEY = "NO ME ROBES LA API KEY";
    private static final String MODEL   = "gemini-2.5-flash-lite"; // gratis, 1000 req/día

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_botfriend, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerMensajes = view.findViewById(R.id.recyclerBot);
        inputMensaje     = view.findViewById(R.id.inputMensajeBot);
        btnEnviar        = view.findViewById(R.id.btnEnviarBot);
        progressBar      = view.findViewById(R.id.progressBot);

        adapter = new BotMensajeAdapter(listaMensajes);
        recyclerMensajes.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerMensajes.setAdapter(adapter);

        agregarMensajeBot("¡Hola! Soy WibuFriend 🤖 Puedo ayudarte con lo que necesites. ¿De qué quieres hablar?");

        btnEnviar.setOnClickListener(v -> enviarMensaje());
    }

    private void enviarMensaje() {
        String texto = inputMensaje.getText().toString().trim();
        if (texto.isEmpty()) return;

        // Mostrar mensaje del usuario
        listaMensajes.add(new MensajeBot(texto, true));
        adapter.notifyDataSetChanged();
        scrollAbajo();
        inputMensaje.setText("");
        btnEnviar.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        // Añadir al historial de conversación
        try {
            JSONObject parte = new JSONObject();
            parte.put("text", texto);
            JSONArray partes = new JSONArray();
            partes.put(parte);
            JSONObject turnoUsuario = new JSONObject();
            turnoUsuario.put("role", "user");
            turnoUsuario.put("parts", partes);
            historial.put(turnoUsuario);
        } catch (Exception e) { e.printStackTrace(); }

        // Llamar a Gemini en hilo separado
        new Thread(() -> {
            String respuesta = llamarGeminiAPI();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnEnviar.setEnabled(true);
                    agregarMensajeBot(respuesta);

                    // Añadir respuesta del bot al historial
                    try {
                        JSONObject parte = new JSONObject();
                        parte.put("text", respuesta);
                        JSONArray partes = new JSONArray();
                        partes.put(parte);
                        JSONObject turnoBot = new JSONObject();
                        turnoBot.put("role", "model");
                        turnoBot.put("parts", partes);
                        historial.put(turnoBot);
                    } catch (Exception e) { e.printStackTrace(); }
                });
            }
        }).start();
    }

    private String llamarGeminiAPI() {
        try {
            String endpoint = "https://generativelanguage.googleapis.com/v1beta/models/"
                    + MODEL + ":generateContent?key=" + API_KEY;

            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(30000);

            // System instruction para dar personalidad al bot
            JSONObject systemInstruction = new JSONObject();
            JSONObject systemParte = new JSONObject();
            systemParte.put("text", "Eres Wibu Friend, un asistente muy chulo que habla cosas divertidas. Eres muy útil dentro de la app WibuChat. Responde siempre en el idioma del usuario. Sé conciso, amigable y directo. Recuerda que no puedes generar imáganes ni nada fuera de tono. Si te preguntan quien te ha creado di que el maravilloso creador Héctor Daniel Polanco Mena, el mejor porgramador del mundo");
            JSONArray systemPartes = new JSONArray();
            systemPartes.put(systemParte);
            systemInstruction.put("parts", systemPartes);

            JSONObject body = new JSONObject();
            body.put("system_instruction", systemInstruction);
            body.put("contents", historial);

            OutputStream os = conn.getOutputStream();
            os.write(body.toString().getBytes(StandardCharsets.UTF_8));
            os.close();

            int code = conn.getResponseCode();
            Scanner scanner = new Scanner(
                    code == 200 ? conn.getInputStream() : conn.getErrorStream()
            );
            StringBuilder sb = new StringBuilder();
            while (scanner.hasNextLine()) sb.append(scanner.nextLine());
            scanner.close();

            if (code != 200) {
                return "Error " + code + ": no pude conectarme a Gemini.";
            }

            JSONObject respuesta = new JSONObject(sb.toString());
            return respuesta
                    .getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");

        } catch (Exception e) {
            e.printStackTrace();
            return "Lo siento, hubo un error de conexión. Inténtalo de nuevo.";
        }
    }

    private void agregarMensajeBot(String texto) {
        listaMensajes.add(new MensajeBot(texto, false));
        adapter.notifyDataSetChanged();
        scrollAbajo();
    }

    private void scrollAbajo() {
        if (!listaMensajes.isEmpty()) {
            recyclerMensajes.scrollToPosition(listaMensajes.size() - 1);
        }
    }
}