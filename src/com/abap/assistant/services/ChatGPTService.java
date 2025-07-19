package com.abap.assistant.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.abap.assistant.models.ChatMessage;
import com.abap.assistant.utils.ConfigurationManager;

/**
 * Service for integrating with ChatGPT API
 * Handles all communication with OpenAI's GPT models
 */
public class ChatGPTService {
    
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String DEFAULT_MODEL = "gpt-3.5-turbo";
    
    private List<ChatMessage> conversationHistory;
    private ConfigurationManager config;
    
    public ChatGPTService() {
        this.conversationHistory = new ArrayList<>();
        this.config = ConfigurationManager.getInstance();
        
        // Add system message for ABAP context
        ChatMessage systemMessage = new ChatMessage("system", 
            "You are an expert ABAP programming assistant. You help developers with SAP ABAP code analysis, " +
            "optimization, error detection, and best practices. Always provide clear, actionable advice with " +
            "code examples when appropriate. Focus on performance, maintainability, and SAP standards.");
        conversationHistory.add(systemMessage);
    }
    
    /**
     * Send message to ChatGPT and return response
     */
    public ChatMessage sendMessage(String userMessage) throws Exception {
        String apiKey = config.getApiKey();
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalStateException("OpenAI API key not configured. Please set it in preferences.");
        }
        
        // Add user message to history
        ChatMessage userMsg = new ChatMessage("user", userMessage);
        conversationHistory.add(userMsg);
        
        // Prepare API request
        JSONObject requestBody = createRequestBody();
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(OPENAI_API_URL);
            request.setHeader("Authorization", "Bearer " + apiKey);
            request.setHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(requestBody.toString()));
            
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                
                if (response.getStatusLine().getStatusCode() == 200) {
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    String assistantReply = extractAssistantReply(jsonResponse);
                    
                    ChatMessage assistantMessage = new ChatMessage("assistant", assistantReply);
                    conversationHistory.add(assistantMessage);
                    
                    return assistantMessage;
                } else {
                    throw new RuntimeException("API Error: " + response.getStatusLine().getStatusCode() + " - " + responseBody);
                }
            }
        }
    }
    
    /**
     * Send message with specific context (for Quick Actions)
     */
    public ChatMessage sendMessageWithContext(String userMessage, String context) throws Exception {
        String enhancedMessage = "Context:\n" + context + "\n\nRequest:\n" + userMessage;
        return sendMessage(enhancedMessage);
    }
    
    /**
     * Send message for deep code analysis with enhanced context
     */
    public String sendMessageForCodeAnalysis(String analysisPrompt, String codeContext) throws Exception {
        String enhancedPrompt = buildCodeAnalysisPrompt(analysisPrompt, codeContext);
        ChatMessage response = sendMessage(enhancedPrompt);
        return response.getContent();
    }
    
    /**
     * Get specific analysis from AI based on code context
     */
    public String getCodeInsights(String fileName, String codeContent, String analysisContext) throws Exception {
        String prompt = buildInsightsPrompt(fileName, codeContent, analysisContext);
        ChatMessage response = sendMessage(prompt);
        return response.getContent();
    }
    
    /**
     * Simple method to send message and get string response (different signature)
     */
    public String sendSimpleMessage(String message) throws Exception {
        ChatMessage response = sendMessage(message);
        return response.getContent();
    }
    
    private JSONObject createRequestBody() {
        JSONObject request = new JSONObject();
        request.put("model", config.getModel() != null ? config.getModel() : DEFAULT_MODEL);
        request.put("max_tokens", config.getMaxTokens());
        request.put("temperature", config.getTemperature());
        
        JSONArray messages = new JSONArray();
        for (ChatMessage msg : conversationHistory) {
            JSONObject message = new JSONObject();
            message.put("role", msg.getRole());
            message.put("content", msg.getContent());
            messages.put(message);
        }
        request.put("messages", messages);
        
        return request;
    }
    
    private String extractAssistantReply(JSONObject response) {
        JSONArray choices = response.getJSONArray("choices");
        if (choices.length() > 0) {
            JSONObject firstChoice = choices.getJSONObject(0);
            JSONObject message = firstChoice.getJSONObject("message");
            return message.getString("content");
        }
        throw new RuntimeException("No response content found");
    }
    
    /**
     * Clear conversation history (keep system message)
     */
    public void clearHistory() {
        ChatMessage systemMessage = conversationHistory.get(0);
        conversationHistory.clear();
        conversationHistory.add(systemMessage);
    }
    
    /**
     * Get conversation history
     */
    public List<ChatMessage> getHistory() {
        return new ArrayList<>(conversationHistory);
    }
    
    /**
     * Generate ABAP-specific prompts for better responses
     */
    public static class ABAPPromptBuilder {
        
        public static String buildExplainPrompt(String code, String context) {
            return String.format(
                "Please explain this ABAP code in detail. Include:\n" +
                "1. What the code does (functionality)\n" +
                "2. Key ABAP concepts used\n" +
                "3. Data flow and logic\n" +
                "4. Any SAP-specific patterns\n" +
                "5. Potential improvements\n\n" +
                "Context: %s\n\nCode:\n%s", 
                context, code
            );
        }
        
        public static String buildOptimizePrompt(String code, String context) {
            return String.format(
                "Please analyze this ABAP code for optimization opportunities. Focus on:\n" +
                "1. Performance improvements\n" +
                "2. Memory usage optimization\n" +
                "3. Database query optimization\n" +
                "4. Modern ABAP syntax usage\n" +
                "5. SAP best practices\n" +
                "6. Code readability and maintainability\n\n" +
                "Provide specific recommendations with improved code examples.\n\n" +
                "Context: %s\n\nCode:\n%s", 
                context, code
            );
        }
        
        public static String buildErrorCheckPrompt(String code, String context) {
            return String.format(
                "Please check this ABAP code for potential issues and errors. Look for:\n" +
                "1. Syntax errors\n" +
                "2. Logic errors\n" +
                "3. Runtime issues\n" +
                "4. Security vulnerabilities\n" +
                "5. Performance problems\n" +
                "6. SAP coding standards violations\n" +
                "7. Memory leaks\n" +
                "8. Exception handling issues\n\n" +
                "Provide solutions for each issue found.\n\n" +
                "Context: %s\n\nCode:\n%s", 
                context, code
            );
        }
    }
    
    /**
     * Build prompt for deep code analysis
     */
    private String buildCodeAnalysisPrompt(String analysisPrompt, String codeContext) {
        return String.format(
            "Realiza un análisis profundo del siguiente código ABAP:\n\n" +
            "%s\n\n" +
            "CONTEXTO DEL ANÁLISIS:\n%s\n\n" +
            "Por favor proporciona:\n" +
            "1. Análisis de calidad del código\n" +
            "2. Identificación de patrones de diseño\n" +
            "3. Evaluación de rendimiento\n" +
            "4. Sugerencias de refactoring\n" +
            "5. Cumplimiento de estándares SAP\n" +
            "6. Análisis de mantenibilidad\n" +
            "7. Detección de código duplicado\n" +
            "8. Evaluación de acoplamiento y cohesión",
            analysisPrompt, codeContext
        );
    }
    
    /**
     * Build prompt for code insights
     */
    private String buildInsightsPrompt(String fileName, String codeContent, String analysisContext) {
        return String.format(
            "ARCHIVO: %s\n\n" +
            "CONTENIDO DEL CÓDIGO:\n%s\n\n" +
            "CONTEXTO DE ANÁLISIS:\n%s\n\n" +
            "Proporciona insights específicos sobre:\n" +
            "1. Arquitectura y estructura del código\n" +
            "2. Flujo de datos y lógica de negocio\n" +
            "3. Dependencias críticas identificadas\n" +
            "4. Variables no utilizadas o subutilizadas\n" +
            "5. Oportunidades de optimización\n" +
            "6. Riesgos potenciales\n" +
            "7. Recomendaciones de mejora prioritarias\n" +
            "8. Impacto en el rendimiento del sistema",
            fileName, codeContent, analysisContext
        );
    }
    
    /**
     * Get singleton instance
     */
    public static ChatGPTService getInstance() {
        if (instance == null) {
            instance = new ChatGPTService();
        }
        return instance;
    }
    
    private static ChatGPTService instance;
}
