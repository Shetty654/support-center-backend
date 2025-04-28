package com.example.support_center_backend.config;

import io.socket.client.IO;
import io.socket.client.Socket;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.*;

@Service
public class PythonSocketIOClient {

    private final ChartDataPublisher publisher;

    public PythonSocketIOClient(ChartDataPublisher publisher) {
        this.publisher = publisher;
    }

    @EventListener
    public void connect(ApplicationReadyEvent ev) throws URISyntaxException {
        IO.Options opts = IO.Options.builder()
                .setTransports(new String[]{"websocket"})
                .setReconnection(true)
                .build();

        Socket socket = IO.socket("http://192.168.1.42:5000", opts);

        socket.on(Socket.EVENT_CONNECT, args -> {
            System.out.println("✅ Connected to Python WS");
            socket.emit("join_group", Map.of("groupName","1second"));
        });

        socket.on("new_chart_data", args -> {
            JSONObject payload = (JSONObject) args[0];
            JSONArray rowsJson = null;
            try {
                rowsJson = payload.getJSONArray("rows");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            List<Map<String,Object>> rows = new ArrayList<>();
            for (int i = 0; i < rowsJson.length(); i++) {
                JSONObject obj = null;
                try {
                    obj = rowsJson.getJSONObject(i);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                Map<String,Object> map = new HashMap<>();

                Iterator<String> keys = obj.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    try {
                        map.put(key, obj.get(key));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }

                rows.add(map);
            }

            System.out.println("🔃 publishing rows: " + rows);
            publisher.publish("1second", rows);
        });

        socket.connect();
    }
}