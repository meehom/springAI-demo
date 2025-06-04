package com.meehom.springAI;

import reactor.core.publisher.Flux;

/**
 * @Author meehom
 * @Date 2025/6/4 21:31
 * @version 1.0
 */
public interface OllamaService {

    String getChatMessage(String question);

    Flux<String> steamChat(String msg);
}
