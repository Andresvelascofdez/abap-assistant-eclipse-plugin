package com.abap.assistant.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import com.abap.assistant.models.ChatMessage;
import com.abap.assistant.utils.EditorUtils;
import com.abap.assistant.utils.ConfigurationManager;

/**
 * Service for automatic code modification with diff preview
 * Handles Auto Fix and Auto Optimize functionality with user confirmation
 */
public class AutoCodeModificationService {
    
    private ChatGPTService chatService;
    private ConfigurationManager config;
    private AuditService auditService;
    
    public AutoCodeModificationService() {
        this.chatService = new ChatGPTService();
        this.config = ConfigurationManager.getInstance();
        this.auditService = new AuditService();
    }
    
    /**
     * Perform automatic code fixing with diff preview
     */
    public boolean performAutoFix(String originalCode, boolean showDiffPreview) {
        try {
            // Create specific prompt for auto-fix
            String prompt = createAutoFixPrompt(originalCode);
            
            // Get AI response
            ChatMessage response = chatService.sendMessage(prompt);
            String fixedCode = extractCodeFromResponse(response.getContent());
            
            if (fixedCode != null && !fixedCode.trim().isEmpty()) {
                
                if (showDiffPreview) {
                    // Show diff preview and get user confirmation
                    boolean userApproved = showDiffPreviewDialog(originalCode, fixedCode, "Auto Fix");
                    if (!userApproved) {
                        return false; // User cancelled
                    }
                }
                
                // Apply changes to editor
                boolean success = applyCodeChanges(originalCode, fixedCode, "AUTO_FIX");
                
                if (success && config.isEnterpriseAuditEnabled()) {
                    // Record audit trail
                    auditService.recordModification(
                        "AUTO_FIX", 
                        originalCode, 
                        fixedCode, 
                        EditorUtils.getCurrentEditorTitle()
                    );
                }
                
                return success;
            }
            
        } catch (Exception e) {
            EditorUtils.showErrorMessage("Auto Fix Error",
                "Failed to perform auto fix: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Perform automatic code optimization
     */
    public boolean performAutoOptimize(String originalCode, boolean showDiffPreview) {
        try {
            // Create specific prompt for optimization
            String prompt = createOptimizePrompt(originalCode);
            
            // Get AI response
            ChatMessage response = chatService.sendMessage(prompt);
            String optimizedCode = extractCodeFromResponse(response.getContent());
            
            if (optimizedCode != null && !optimizedCode.trim().isEmpty()) {
                
                if (showDiffPreview) {
                    // Show diff preview and get user confirmation
                    boolean userApproved = showDiffPreviewDialog(originalCode, optimizedCode, "Auto Optimize");
                    if (!userApproved) {
                        return false; // User cancelled
                    }
                }
                
                // Apply changes to editor
                boolean success = applyCodeChanges(originalCode, optimizedCode, "AUTO_OPTIMIZE");
                
                if (success && config.isEnterpriseAuditEnabled()) {
                    // Record audit trail
                    auditService.recordModification(
                        "AUTO_OPTIMIZE", 
                        originalCode, 
                        optimizedCode, 
                        EditorUtils.getCurrentEditorTitle()
                    );
                }
                
                return success;
            }
            
        } catch (Exception e) {
            EditorUtils.showErrorMessage("Auto Optimize Error",
                "Failed to perform auto optimization: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Show diff preview dialog and get user confirmation
     */
    private boolean showDiffPreviewDialog(String originalCode, String modifiedCode, String operationType) {
        try {
            Display display = Display.getDefault();
            
            if (display != null && !display.isDisposed()) {
                final boolean[] result = {false};
                
                display.syncExec(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // Create a simple confirmation dialog with preview
                            String message = String.format(
                                "ABAP Assistant has generated %s suggestions.\n\n" +
                                "Original code length: %d characters\n" +
                                "Modified code length: %d characters\n\n" +
                                "Do you want to apply these changes?\n\n" +
                                "Preview of changes:\n" +
                                "==================\n" +
                                "%s\n" +
                                "==================\n",
                                operationType.toLowerCase(),
                                originalCode.length(),
                                modifiedCode.length(),
                                getCodePreview(modifiedCode, 300) // Show first 300 chars
                            );
                             
                            result[0] = MessageDialog.openConfirm(
                                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                                "ABAP Assistant - " + operationType,
                                message
                            );
                        } catch (Exception e) {
                            result[0] = false;
                        }
                    }
                });
                
                return result[0];
            }
        } catch (Exception e) {
            // If dialog fails, default to auto-apply for non-interactive mode
            return true;
        }
        return false;
    }
    
    /**
     * Get a preview of code (first N characters)
     */
    private String getCodePreview(String code, int maxLength) {
        if (code == null) return "";
        
        if (code.length() <= maxLength) {
            return code;
        }
        
        return code.substring(0, maxLength) + "...";
    }
    
    /**
     * Apply code changes to editor
     */
    private boolean applyCodeChanges(String originalCode, String newCode, String modificationType) {
        try {
            // Add audit comment if enterprise audit is enabled
            if (config.isEnterpriseAuditEnabled()) {
                newCode = addAuditComment(originalCode, newCode, modificationType);
            }
            
            // Replace code in editor - use new method that can replace all text
            boolean success = EditorUtils.replaceSelectedTextOrAll(newCode);
            
            if (success) {
                EditorUtils.showMessage("Code modified successfully with " + modificationType + ".");
            }
            
            return success;
            
        } catch (Exception e) {
            EditorUtils.showErrorMessage("Code Modification Error", 
                "Failed to apply " + modificationType + ": " + e.getMessage());
            return false;
        }
    }
    
    private String createAutoFixPrompt(String code) {
        return String.format(
            "You are an ABAP programming expert. Analyze the following ABAP code and fix any errors, bugs, or issues. " +
            "Return ONLY the corrected ABAP code without explanations or markdown formatting.\n\n" +
            "Code to fix:\n%s\n\n" +
            "Fixed code:",
            code
        );
    }
    
    private String createOptimizePrompt(String code) {
        return String.format(
            "You are an ABAP performance optimization expert. Optimize the following ABAP code for better performance, " +
            "readability, and maintainability. Follow SAP best practices. " +
            "Return ONLY the optimized ABAP code without explanations or markdown formatting.\n\n" +
            "Code to optimize:\n%s\n\n" +
            "Optimized code:",
            code
        );
    }
    
    private String extractCodeFromResponse(String response) {
        if (response == null || response.trim().isEmpty()) {
            return null;
        }
        
        // Remove markdown code blocks if present
        String cleaned = response.trim();
        if (cleaned.startsWith("```")) {
            cleaned = cleaned.replaceFirst("```[a-zA-Z]*\\n?", "");
            cleaned = cleaned.replaceFirst("```\\s*$", "");
        }
        
        // Basic validation - check if it looks like ABAP code
        if (isValidABAPCode(cleaned)) {
            return cleaned.trim();
        }
        
        return null;
    }
    
    private boolean isValidABAPCode(String code) {
        if (code == null || code.trim().isEmpty()) return false;
        
        String upper = code.toUpperCase();
        // Basic ABAP keywords check
        return upper.contains("DATA") || 
               upper.contains("LOOP ") || 
               upper.contains("IF ") || 
               upper.contains("ENDIF") ||
               upper.contains("ENDLOOP") ||
               upper.contains("WRITE ") ||
               upper.matches(".*\\.$"); // Ends with period (common in ABAP)
    }
    
    private String addAuditComment(String originalCode, String newCode, String modificationType) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String username = System.getProperty("user.name");
        
        String auditComment = String.format(
            "* ABAP Assistant %s - %s by %s\n* Original code preserved below as comment\n",
            modificationType, timestamp, username
        );
        
        // Comment out original code
        String[] originalLines = originalCode.split("\n");
        StringBuilder commentedOriginal = new StringBuilder();
        for (String line : originalLines) {
            commentedOriginal.append("* ORIG: ").append(line).append("\n");
        }
        
        return auditComment + newCode + "\n\n" + commentedOriginal.toString();
    }
}
