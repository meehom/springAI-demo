package com.meehom.springAI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;

/**
 * @Author meehom
 * @Date 2025/6/4 21:15
 * @version 1.0
 */
@RestController
@ResponseBody
public class OllamaController {

    @Autowired
    private OllamaService ollamaService;

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello, World!";
    }

    @GetMapping("/chat")
    public String getChatMessage(@RequestParam String question){
        String chatMessage = ollamaService.getChatMessage(question);
        return chatMessage;
    }

    @GetMapping(value = "/steamChat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> steamChat(@RequestParam String msg) {
        return ollamaService.steamChat(msg);
    }


}
