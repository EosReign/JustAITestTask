package ru.eosreign.vkbot.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.eosreign.vkbot.handler.VkHttpHandler;

import java.io.IOException;

@RestController
@Slf4j
public class VkController {

    private final VkHttpHandler vkHttpHandler;

    @Autowired
    public VkController(VkHttpHandler vkHttpHandler) {
        this.vkHttpHandler = vkHttpHandler;
    }

    @PostMapping("/callback")
    public void handleVkRequest(HttpServletRequest request,
                                HttpServletResponse response) throws IOException {
        log.info("Start RestQuery {}", request.getPathInfo());
        vkHttpHandler.handleRequest(request, response);
    }
}
