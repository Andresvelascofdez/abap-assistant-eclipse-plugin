package com.abap.assistant.services;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.abap.assistant.models.ChatMessage;
import com.abap.assistant.utils.EditorUtils;

/**
 * Advanced ABAP Code Intelligence Service
 * Provides deep analysis and contextual suggestions for ABAP code
 */
public class ABAPIntelligenceService {
    
    private ChatGPTService chatService;
    
    public ABAPIntelligenceService() {
        this.chatService = new ChatGPTService();
    }
    
    /**
     * Perform comprehensive code analysis
     */
    public CodeAnalysisResult analyzeCode(String abapCode) {
        CodeAnalysisResult result = new CodeAnalysisResult();
        
        // 1. Detect SAP patterns and frameworks
        result.detectedPatterns = detectSAPPatterns(abapCode);
        
        // 2. Analyze performance issues
        result.performanceIssues = analyzePerformance(abapCode);
        
        // 3. Check for modernization opportunities
        result.modernizationSuggestions = checkModernization(abapCode);
        
        // 4. Detect architectural issues
        result.architectureIssues = checkArchitecture(abapCode);
        
        // 5. Generate intelligent suggestions
        result.intelligentSuggestions = generateIntelligentSuggestions(abapCode, result);
        
        return result;
    }
    
    /**
     * Detect SAP-specific patterns and frameworks
     */
    private List<String> detectSAPPatterns(String code) {
        List<String> patterns = new ArrayList<>();
        
        if (code.toUpperCase().contains("CL_SALV_")) {
            patterns.add("ALV Grid Display Pattern detected - Modern SAP List display");
        }
        
        if (code.toUpperCase().contains("CALL FUNCTION") && code.toUpperCase().contains("BAPI_")) {
            patterns.add("BAPI Usage Pattern detected - Business API integration");
        }
        
        if (code.toUpperCase().contains("CALL SCREEN")) {
            patterns.add("Screen Programming Pattern - Consider modern UI5 alternatives");
        }
        
        if (code.toUpperCase().contains("WRITE:") || code.toUpperCase().contains("WRITE ")) {
            patterns.add("Classical Report Pattern - Consider ALV for better presentation");
        }
        
        if (code.toUpperCase().contains("SELECT SINGLE") || code.toUpperCase().contains("SELECT *")) {
            patterns.add("Database Access Pattern detected - Performance review recommended");
        }
        
        if (code.toUpperCase().contains("SMARTFORMS") || code.toUpperCase().contains("SSF_")) {
            patterns.add("Smart Forms Pattern - Document generation framework");
        }
        
        if (code.toUpperCase().contains("ENHANCEMENT")) {
            patterns.add("Enhancement Framework Usage - Good extensibility practice");
        }
        
        return patterns;
    }
    
    /**
     * Analyze performance-related issues
     */
    private List<String> analyzePerformance(String code) {
        List<String> issues = new ArrayList<>();
        
        // Check for nested SELECTs
        Pattern nestedSelectPattern = Pattern.compile("SELECT.*SELECT", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        if (nestedSelectPattern.matcher(code).find()) {
            issues.add("🔴 CRITICAL: Nested SELECT statements detected - Use JOIN instead");
        }
        
        // Check for SELECT * usage
        if (code.toUpperCase().contains("SELECT *")) {
            issues.add("⚠️ WARNING: SELECT * usage - Specify required fields only");
        }
        
        // Check for loops with database access
        Pattern loopSelectPattern = Pattern.compile("LOOP.*SELECT", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        if (loopSelectPattern.matcher(code).find()) {
            issues.add("🔴 CRITICAL: Database access inside loop - Use FOR ALL ENTRIES");
        }
        
        // Check for missing WHERE clause
        Pattern selectPattern = Pattern.compile("SELECT\\s+[^WHERE]*FROM\\s+\\w+(?!.*WHERE)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        if (selectPattern.matcher(code).find()) {
            issues.add("⚠️ WARNING: SELECT without WHERE clause - Performance risk");
        }
        
        // Check for SORT inside loop
        if (code.toUpperCase().contains("LOOP") && code.toUpperCase().contains("SORT")) {
            issues.add("🔴 PERFORMANCE: SORT inside loop - Sort before loop");
        }
        
        return issues;
    }
    
    /**
     * Check for modernization opportunities
     */
    private List<String> checkModernization(String code) {
        List<String> suggestions = new ArrayList<>();
        
        // Old-style LOOP syntax
        if (code.toUpperCase().contains("LOOP AT") && !code.toUpperCase().contains("INTO")) {
            suggestions.add("✨ MODERNIZE: Use 'LOOP AT table INTO wa' instead of field-symbols");
        }
        
        // Old IF-ELSE syntax
        if (code.toUpperCase().contains("IF") && code.toUpperCase().contains("EQ")) {
            suggestions.add("✨ MODERNIZE: Use '=' instead of 'EQ' for better readability");
        }
        
        // String operations
        if (code.toUpperCase().contains("CONCATENATE")) {
            suggestions.add("✨ MODERNIZE: Use string templates |{ }| instead of CONCATENATE");
        }
        
        // Old data declaration
        if (code.toUpperCase().contains("DATA:") && code.toUpperCase().contains("TYPE I")) {
            suggestions.add("✨ MODERNIZE: Consider using specific integer types (INT1, INT2, INT4)");
        }
        
        // Internal table operations
        if (code.toUpperCase().contains("READ TABLE") && !code.toUpperCase().contains("BINARY SEARCH")) {
            suggestions.add("✨ OPTIMIZE: Add BINARY SEARCH to READ operations on sorted tables");
        }
        
        return suggestions;
    }
    
    /**
     * Check architectural best practices
     */
    private List<String> checkArchitecture(String code) {
        List<String> issues = new ArrayList<>();
        
        // Mixed concerns (UI + Logic + Data)
        boolean hasUICode = code.toUpperCase().contains("WRITE") || code.toUpperCase().contains("MESSAGE");
        boolean hasDBCode = code.toUpperCase().contains("SELECT") || code.toUpperCase().contains("INSERT");
        boolean hasLogicCode = code.toUpperCase().contains("LOOP") || code.toUpperCase().contains("IF");
        
        if (hasUICode && hasDBCode && hasLogicCode) {
            issues.add("🏗️ ARCHITECTURE: Mixed concerns detected - Consider separating UI, Logic, and Data layers");
        }
        
        // Hard-coded values
        Pattern hardcodedPattern = Pattern.compile("'[A-Z0-9]{2,}'", Pattern.CASE_INSENSITIVE);
        Matcher matcher = hardcodedPattern.matcher(code);
        if (matcher.find()) {
            issues.add("🏗️ ARCHITECTURE: Hard-coded values detected - Use constants or customizing");
        }
        
        // Missing error handling
        if (code.toUpperCase().contains("SELECT") && !code.toUpperCase().contains("SY-SUBRC")) {
            issues.add("🏗️ ARCHITECTURE: Missing error handling after database operations");
        }
        
        return issues;
    }
    
    /**
     * Generate AI-powered intelligent suggestions
     */
    private String generateIntelligentSuggestions(String code, CodeAnalysisResult analysis) {
        try {
            StringBuilder prompt = new StringBuilder();
            prompt.append("You are an expert SAP ABAP consultant. Analyze this ABAP code and provide 3-5 specific, actionable improvement suggestions.\\n\\n");
            prompt.append("Code patterns detected: ").append(String.join(", ", analysis.detectedPatterns)).append("\\n");
            prompt.append("Performance issues: ").append(String.join(", ", analysis.performanceIssues)).append("\\n");
            prompt.append("\\nABAP Code to analyze:\\n").append(code);
            prompt.append("\\n\\nProvide specific suggestions in this format:\\n");
            prompt.append("1. [Category] Suggestion title: Detailed explanation\\n");
            prompt.append("2. [Category] Another suggestion: Explanation\\n");
            prompt.append("Focus on: Performance, Maintainability, SAP Best Practices, Modern ABAP syntax");
            
            ChatMessage response = chatService.sendMessage(prompt.toString());
            return response != null ? response.getContent() : "AI suggestions temporarily unavailable.";
            
        } catch (Exception e) {
            return "Unable to generate AI suggestions: " + e.getMessage();
        }
    }
    
    /**
     * Generate optimized code with explanations
     */
    public String generateOptimizedCode(String originalCode) {
        try {
            String prompt = String.format(
                "You are an expert SAP ABAP performance consultant. Optimize this ABAP code for better performance, " +
                "maintainability, and modern ABAP best practices. Return the optimized code with inline comments " +
                "explaining each improvement.\\n\\n" +
                "Original ABAP Code:\\n%s\\n\\n" +
                "Optimized ABAP Code with explanations:",
                originalCode
            );
            
            ChatMessage response = chatService.sendMessage(prompt);
            return response != null ? response.getContent() : null;
            
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Result class for code analysis
     */
    public static class CodeAnalysisResult {
        public List<String> detectedPatterns = new ArrayList<>();
        public List<String> performanceIssues = new ArrayList<>();
        public List<String> modernizationSuggestions = new ArrayList<>();
        public List<String> architectureIssues = new ArrayList<>();
        public String intelligentSuggestions = "";
        
        public String getSummaryReport() {
            StringBuilder report = new StringBuilder();
            report.append("=== ABAP CODE ANALYSIS REPORT ===\\n\\n");
            
            if (!detectedPatterns.isEmpty()) {
                report.append("📋 DETECTED PATTERNS:\\n");
                for (String pattern : detectedPatterns) {
                    report.append("• ").append(pattern).append("\\n");
                }
                report.append("\\n");
            }
            
            if (!performanceIssues.isEmpty()) {
                report.append("⚡ PERFORMANCE ISSUES:\\n");
                for (String issue : performanceIssues) {
                    report.append("• ").append(issue).append("\\n");
                }
                report.append("\\n");
            }
            
            if (!modernizationSuggestions.isEmpty()) {
                report.append("🚀 MODERNIZATION OPPORTUNITIES:\\n");
                for (String suggestion : modernizationSuggestions) {
                    report.append("• ").append(suggestion).append("\\n");
                }
                report.append("\\n");
            }
            
            if (!architectureIssues.isEmpty()) {
                report.append("🏗️ ARCHITECTURE RECOMMENDATIONS:\\n");
                for (String issue : architectureIssues) {
                    report.append("• ").append(issue).append("\\n");
                }
                report.append("\\n");
            }
            
            if (!intelligentSuggestions.isEmpty()) {
                report.append("🤖 AI-POWERED SUGGESTIONS:\\n");
                report.append(intelligentSuggestions).append("\\n");
            }
            
            return report.toString();
        }
    }
}
