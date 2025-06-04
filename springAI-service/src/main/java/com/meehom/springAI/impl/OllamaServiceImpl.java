package com.meehom.springAI.impl;

import com.meehom.springAI.OllamaService;
import io.github.lnyocly.ai4j.listener.SseListener;
import io.github.lnyocly.ai4j.platform.openai.chat.entity.ChatCompletion;
import io.github.lnyocly.ai4j.platform.openai.chat.entity.ChatCompletionResponse;
import io.github.lnyocly.ai4j.platform.openai.chat.entity.ChatMessage;
import io.github.lnyocly.ai4j.service.IChatService;
import io.github.lnyocly.ai4j.service.PlatformType;
import io.github.lnyocly.ai4j.service.factor.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * @version 1.0
 * @Author meehom
 * @Date 2025/6/4 21:33
 */
@Service
public class OllamaServiceImpl implements OllamaService{

    // 注入Ai服务
    @Autowired
    private AiService aiService;
    @Override
    public String getChatMessage(String question) {
        // 获取OLLAMA的聊天服务
        IChatService chatService = aiService.getChatService(PlatformType.OLLAMA);

        // 创建请求参数
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model("qwen2:0.5b")
                .message(ChatMessage.withUser(question))
                .build();

        System.out.println(chatCompletion);

        // 发送chat请求
        ChatCompletionResponse chatCompletionResponse = null;
        try {
            chatCompletionResponse = chatService.chatCompletion(chatCompletion);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 获取聊天内容和token消耗
        String content = chatCompletionResponse.getChoices().get(0).getMessage().getContent();
        long totalTokens = chatCompletionResponse.getUsage().getTotalTokens();
        System.out.println("总token消耗: " + totalTokens);

        return content;
    }

    @Override
    public Flux<String> steamChat(String msg) {
        return Flux.create(sink -> {
            // 获取chat服务实例
            IChatService chatService = aiService.getChatService(PlatformType.OLLAMA);

            // 构造请求参数
            ChatCompletion chatCompletion = ChatCompletion.builder()
                    .model("qwen2:0.5b")
                    .message(ChatMessage.withUser(msg))
                    .build();

            // 自定义监听器，用于将数据推送到 Flux Sink 中
            SseListener sseListener = new SseListener() {
                @Override
                protected void send() {
                    String currentData = this.getCurrStr();
                    sink.next(currentData);  // 发射当前片段
                }
            };
            sseListener.setShowToolArgs(true);

            // 发送SSE请求
            try {
                chatService.chatCompletionStream(chatCompletion, sseListener);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            System.out.println(sseListener.getOutput());
        });
    }
}
