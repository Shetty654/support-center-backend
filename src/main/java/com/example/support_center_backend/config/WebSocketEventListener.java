package com.example.support_center_backend.config;

import com.example.support_center_backend.services.impl.ChartStreamPublisher;
import io.socket.client.IO;
import io.socket.client.Socket;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.*;

@Service
public class WebSocketEventListener {

    private final ChartStreamPublisher publisher;
    private final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    public WebSocketEventListener(ChartStreamPublisher publisher) {
        this.publisher = publisher;
    }

    @EventListener
    public void connect(ApplicationReadyEvent event) throws URISyntaxException {
        IO.Options options = IO.Options.builder()
                .setTransports(new String[]{"websocket"})
                .setReconnection(true)
                .build();

        Socket socket = IO.socket("ws://192.168.1.44:5000", options);

        socket.on(Socket.EVENT_CONNECT, args->{
            logger.info("Connected to websocket");
            socket.emit("join_group", Map.of("groupName","1tag"));
        });

        socket.on("new_chart_data", args -> {
            JSONObject payload = (JSONObject) args[0];
            JSONArray rowsJSON = new JSONArray();
            try {
                rowsJSON = payload.getJSONArray("rows");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            List<Map<String, Object>> rows = new ArrayList<>();
            for(int i=0; i<rowsJSON.length(); i++){
                try {
                    JSONObject object = rowsJSON.getJSONObject(i);
                    Map<String, Object> map = new HashMap<>();
                    Iterator<String> keys = object.keys();
                    while(keys.hasNext()){
                        String key = keys.next();
                        map.put(key, object.get(key));
                    }
                    rows.add(map);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            publisher.publish("1tag", rows);
        });
        socket.connect();
    }
}