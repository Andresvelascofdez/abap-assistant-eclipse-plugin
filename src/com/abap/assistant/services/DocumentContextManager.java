package com.abap.assistant.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages document context for ChatGPT queries
 * Provides intelligent chunking, selective context, and token management
 */
public class DocumentContextManager {
    
    private static final int MAX_CONTEXT_TOKENS = 8000; // Conservative token limit
    private static final int CHARS_PER_TOKEN = 4; // Approximate chars per token
    private static final int MAX_CONTEXT_CHARS = MAX_CONTEXT_TOKENS * CHARS_PER_TOKEN;
    
    private Map<String, String> documentContents;
    private Map<String, Boolean> documentEnabled;
    private DocumentProcessorService docProcessor;
    
    public DocumentContextManager() {
        this.documentContents = new HashMap<>();
        this.documentEnabled = new HashMap<>();
        this.docProcessor = new DocumentProcessorService();
    }
    
    /**
     * Add document content to context manager
     */
    public void addDocument(String filePath, String content) {
        documentContents.put(filePath, content);
        documentEnabled.put(filePath, true); // Enable by default
    }
    
    /**
     * Remove document from context
     */
    public void removeDocument(String filePath) {
        documentContents.remove(filePath);
        documentEnabled.remove(filePath);
    }
    
    /**
     * Enable/disable document for context inclusion
     */
    public void setDocumentEnabled(String filePath, boolean enabled) {
        if (documentContents.containsKey(filePath)) {
            documentEnabled.put(filePath, enabled);
        }
    }
    
    /**
     * Get list of available documents
     */
    public List<String> getAvailableDocuments() {
        return new ArrayList<>(documentContents.keySet());
    }
    
    /**
     * Check if document is enabled for context
     */
    public boolean isDocumentEnabled(String filePath) {
        return documentEnabled.getOrDefault(filePath, false);
    }
    
    /**
     * Build context string from enabled documents
     */
    public String buildContext() {
        StringBuilder context = new StringBuilder();
        int totalChars = 0;
        
        // Add enabled documents to context
        for (Map.Entry<String, String> entry : documentContents.entrySet()) {
            String filePath = entry.getKey();
            String content = entry.getValue();
            
            if (!documentEnabled.getOrDefault(filePath, false)) {
                continue; // Skip disabled documents
            }
            
            // Check if adding this document exceeds token limit
            int documentChars = content.length();
            if (totalChars + documentChars > MAX_CONTEXT_CHARS) {
                // Smart truncation - try to fit what we can
                int remainingChars = MAX_CONTEXT_CHARS - totalChars;
                if (remainingChars > 500) { // Only add if meaningful content fits
                    content = smartTruncate(content, remainingChars);
                    documentChars = content.length();
                } else {
                    break; // Stop adding documents
                }
            }
            
            context.append("📄 Document: ").append(getFileName(filePath)).append("\n");
            context.append("─".repeat(50)).append("\n");
            context.append(content).append("\n\n");
            
            totalChars += documentChars + 100; // Add overhead for formatting
        }
        
        return context.toString();
    }
    
    /**
     * Build contextual query combining user prompt with document context
     */
    public String buildContextualQuery(String userPrompt, String selectedCode) {
        StringBuilder query = new StringBuilder();
        
        // Add document context if available
        String context = buildContext();
        if (!context.trim().isEmpty()) {
            query.append("📋 CONTEXT DOCUMENTATION:\n");
            query.append(context);
            query.append("🎯 TASK:\n");
        }
        
        query.append(userPrompt);
        
        // Add selected code if provided
        if (selectedCode != null && !selectedCode.trim().isEmpty()) {
            query.append("\n\n💻 ABAP CODE:\n");
            query.append("```abap\n");
            query.append(selectedCode);
            query.append("\n```");
        }
        
        // Add instruction to use context
        if (!context.trim().isEmpty()) {
            query.append("\n\n⚡ Please analyze the provided code considering the context documentation above. ");
            query.append("Use the specifications, requirements, and business logic described in the documents ");
            query.append("to provide more accurate and relevant suggestions.");
        }
        
        return query.toString();
    }
    
    /**
     * Smart truncation that tries to preserve meaningful content
     */
    private String smartTruncate(String content, int maxChars) {
        if (content.length() <= maxChars) {
            return content;
        }
        
        // Try to find a good break point (end of paragraph or sentence)
        int cutPoint = maxChars - 100; // Leave room for truncation message
        
        // Look for paragraph break
        int paragraphBreak = content.lastIndexOf("\n\n", cutPoint);
        if (paragraphBreak > maxChars / 2) {
            return content.substring(0, paragraphBreak) + "\n\n... [Document truncated for token limit]";
        }
        
        // Look for sentence break
        int sentenceBreak = content.lastIndexOf(". ", cutPoint);
        if (sentenceBreak > maxChars / 2) {
            return content.substring(0, sentenceBreak + 1) + " ... [Document truncated for token limit]";
        }
        
        // Fallback to hard cut
        return content.substring(0, cutPoint) + "... [Document truncated for token limit]";
    }
    
    /**
     * Extract filename from full path
     */
    private String getFileName(String filePath) {
        int lastSeparator = Math.max(filePath.lastIndexOf('/'), filePath.lastIndexOf('\\'));
        return lastSeparator >= 0 ? filePath.substring(lastSeparator + 1) : filePath;
    }
    
    /**
     * Clear all documents from context
     */
    public void clearAllDocuments() {
        documentContents.clear();
        documentEnabled.clear();
    }
    
    /**
     * Get summary of current context state
     */
    public String getContextSummary() {
        int totalDocs = documentContents.size();
        int enabledDocs = (int) documentEnabled.values().stream().mapToInt(b -> b ? 1 : 0).sum();
        int totalChars = buildContext().length();
        int approxTokens = totalChars / CHARS_PER_TOKEN;
        
        return String.format("📊 Context: %d/%d docs enabled, ~%d tokens used", 
                            enabledDocs, totalDocs, approxTokens);
    }
    
    /**
     * Check if we have any enabled context
     */
    public boolean hasContext() {
        return documentEnabled.containsValue(true) && !documentContents.isEmpty();
    }
    
    /**
     * Process and add document from file path
     */
    public void processAndAddDocument(String filePath) throws Exception {
        String content = docProcessor.processDocument(filePath);
        addDocument(filePath, content);
    }
}
