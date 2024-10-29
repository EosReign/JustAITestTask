package ru.eosreign.vkbot.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.eosreign.vkbot.MessageSend;
import ru.eosreign.vkbot.config.VkConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@Component
@Slf4j
public class VkHttpHandler {

    private final String confirmationCode;
    private final MessageSend messageSend;

    @Autowired
    public VkHttpHandler(VkConfig vkConfig,
                         MessageSend messageSend) {
        this.confirmationCode = vkConfig.getConfirmationCode();
        this.messageSend = messageSend;
    }

    public void handleRequest(HttpServletRequest request,
                              HttpServletResponse response) throws IOException {

        StringBuilder body = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
        }
        JSONObject json = new JSONObject(body.toString());

        log.info("Json {}", json);



        if (json.getString("type").equals("confirmation")) {
            sendResponse(response, confirmationCode);
        } else if (json.getString("type").equals("message_new")) {
            int userId = json
                    .getJSONObject("object")
                    .getJSONObject("message")
                    .getInt("from_id");

            String message = json
                    .getJSONObject("object")
                    .getJSONObject("message")
                    .getString("text");

            log.info("message_new: {}: {}", userId, message);

            messageSend.sendVkMessage(json);
            sendResponse(response, "ok");
        } else {
            sendResponse(response, "ok");
        }
    }

    private void sendResponse(HttpServletResponse response,
                              String responseBody) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        out.print(responseBody);
        out.flush();
    }

}