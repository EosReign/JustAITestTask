package ru.eosreign.vkbot;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.eosreign.vkbot.config.VkConfig;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class MessageSend {

    private final String accessToken;
    private final String callbackApiVersion;
    private static final String VK_API_URL = "https://api.vk.com/method/messages.send";

    public MessageSend(VkConfig vkConfig) {
        this.callbackApiVersion = vkConfig.getCallbackApiVersion();
        this.accessToken = vkConfig.getAccessToken();
    }

    public void sendVkMessage(JSONObject json) {
        try {
            int groupId = json
                    .getInt("group_id");

            JSONObject jsonMessage = json
                    .getJSONObject("object")
                    .getJSONObject("message");

            Integer userId = jsonMessage
                    .getInt("from_id");

            String message = "Вы сказали: " + jsonMessage
                    .getString("text");

            Map<String, String> params = new HashMap<>();
            params.put("peer_id", String.valueOf(userId));
            params.put("random_id", String.valueOf((int) (Math.random() * Integer.MAX_VALUE))); // Случайный ID
            params.put("message", message);
            params.put("group_id", String.valueOf(groupId));
            params.put("access_token", accessToken);
            params.put("v", callbackApiVersion);

            // Кодируем параметры
            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (!postData.isEmpty()) postData.append('&');
                postData.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
                postData.append('=');
                postData.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            }

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VK_API_URL))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(postData.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject responseJson = new JSONObject(response.body());

            log.info("ResponseJson: {}", responseJson);

        } catch (IOException | InterruptedException e) {
            log.warn(e.getMessage());
        }
    }
}
