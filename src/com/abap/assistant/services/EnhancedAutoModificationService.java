package com.abap.assistant.services;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

import com.abap.assistant.models.ChatMessage;
import com.abap.assistant.utils.EditorUtils;
import com.abap.assistant.utils.ABAPModificationMarker;
import com.abap.assistant.utils.ConfigurationManager;

/**
 * Enhanced Auto Code Modification Service with ABAP modification markers
 * Handles MOD (modifications) and INS (insertions) with proper ticket tracking
 */
public class EnhancedAutoModificationService {
    
    private ChatGPTService chatService;
    private ConfigurationManager config;
    private AuditService auditService;
    
    public EnhancedAutoModificationService() {
        this.chatService = new ChatGPTService();
        this.config = ConfigurationManager.getInstance();
        this.auditService = new AuditService();
    }
    
    /**
     * Perform automatic code fixing with ABAP modification markers
     */
    public boolean performEnhancedAutoFix(String originalCode, boolean showDiffPreview) {
        try {
            // Get ticket number from user
            String ticketNumber = promptForTicketNumber("Auto Fix");
            if (ticketNumber == null) {
                return false; // User cancelled
            }
            
            // Use system user automatically (no need to prompt)
            String userName = null; // Will use system user automatically
            
            // Create specific prompt for auto-fix
            String prompt = createAutoFixPrompt(originalCode);
            
            // Get AI response
            ChatMessage response = chatService.sendMessage(prompt);
            String fixedCode = extractCodeFromResponse(response.getContent());
            
            if (fixedCode != null && !fixedCode.trim().isEmpty()) {
                
                // Generate code with modification markers
                String markedCode = ABAPModificationMarker.wrapModification(
                    originalCode, fixedCode, ticketNumber, userName);
                
                if (showDiffPreview) {
                    // Show enhanced diff preview with markers
                    boolean userApproved = showEnhancedDiffPreview(originalCode, markedCode, "Auto Fix with Markers");
                    if (!userApproved) {
                        return false; // User cancelled
                    }
                }
                
                // Apply the changes to editor
                boolean success = EditorUtils.replaceSelectedTextOrCurrentFile(markedCode);
                
                if (success && config.isEnterpriseAuditEnabled()) {
                    // Record audit trail with ticket information
                    auditService.recordModificationWithTicket(
                        "AUTO_FIX_MARKED", 
                        originalCode, 
                        markedCode, 
                        EditorUtils.getCurrentEditorTitle(),
                        ticketNumber,
                        userName
                    );
                }
                
                return success;
            }
            
        } catch (Exception e) {
            EditorUtils.showErrorMessage("Enhanced Auto Fix Error",
                "Failed to perform auto fix with markers: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Perform automatic code optimization with ABAP modification markers
     */
    public boolean performEnhancedAutoOptimize(String originalCode, boolean showDiffPreview) {
        try {
            // Get ticket number from user
            String ticketNumber = promptForTicketNumber("Auto Optimize");
            if (ticketNumber == null) {
                return false; // User cancelled
            }
            
            // Use system user automatically (no need to prompt)
            String userName = null; // Will use system user automatically
            
            // Create specific prompt for optimization
            String prompt = createAutoOptimizePrompt(originalCode);
            
            // Get AI response
            ChatMessage response = chatService.sendMessage(prompt);
            String optimizedCode = extractCodeFromResponse(response.getContent());
            
            if (optimizedCode != null && !optimizedCode.trim().isEmpty()) {
                
                // Generate code with modification markers
                String markedCode = ABAPModificationMarker.wrapModification(
                    originalCode, optimizedCode, ticketNumber, userName);
                
                if (showDiffPreview) {
                    // Show enhanced diff preview with markers
                    boolean userApproved = showEnhancedDiffPreview(originalCode, markedCode, "Auto Optimize with Markers");
                    if (!userApproved) {
                        return false; // User cancelled
                    }
                }
                
                // Apply the changes to editor
                boolean success = EditorUtils.replaceSelectedTextOrCurrentFile(markedCode);
                
                if (success && config.isEnterpriseAuditEnabled()) {
                    // Record audit trail with ticket information
                    auditService.recordModificationWithTicket(
                        "AUTO_OPTIMIZE_MARKED", 
                        originalCode, 
                        markedCode, 
                        EditorUtils.getCurrentEditorTitle(),
                        ticketNumber,
                        userName
                    );
                }
                
                return success;
            }
            
        } catch (Exception e) {
            EditorUtils.showErrorMessage("Enhanced Auto Optimize Error",
                "Failed to perform auto optimization with markers: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Insert new code with ABAP insertion markers
     */
    public boolean insertCodeWithMarkers(String newCode, String insertionPoint) {
        try {
            // Get ticket number from user
            String ticketNumber = promptForTicketNumber("Code Insertion");
            if (ticketNumber == null) {
                return false; // User cancelled
            }
            
            // Use system user automatically (no need to prompt)
            String userName = null; // Will use system user automatically
            
            // Generate code with insertion markers
            String markedCode = ABAPModificationMarker.wrapInsertion(newCode, ticketNumber, userName);
            
            // Show preview
            boolean userApproved = showEnhancedDiffPreview("", markedCode, "Code Insertion with Markers");
            if (!userApproved) {
                return false; // User cancelled
            }
            
            // Insert the code at current cursor position or selected area
            boolean success = EditorUtils.insertTextAtCurrentPosition(markedCode);
            
            if (success && config.isEnterpriseAuditEnabled()) {
                // Record audit trail
                auditService.recordModificationWithTicket(
                    "CODE_INSERTION_MARKED",
                    "",
                    markedCode,
                    EditorUtils.getCurrentEditorTitle(),
                    ticketNumber,
                    userName
                );
            }
            
            return success;
            
        } catch (Exception e) {
            EditorUtils.showErrorMessage("Code Insertion Error",
                "Failed to insert code with markers: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Prompt user for ticket number
     */
    private String promptForTicketNumber(String operationType) {
        final String[] result = {null};
        
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                InputDialog dialog = new InputDialog(
                    Display.getDefault().getActiveShell(),
                    "Ticket Number Required",
                    String.format("Enter ticket number for %s operation:\n(Format: TICKET-XXX, INC-123, CHG-456, etc.)", operationType),
                    "TICKET-XXX",
                    input -> {
                        if (ABAPModificationMarker.isValidTicketNumber(input)) {
                            return null; // Valid
                        } else {
                            return "Invalid ticket format. Use: TICKET-XXX, INC-123, CHG-456, etc.";
                        }
                    }
                );
                
                if (dialog.open() == Window.OK) {
                    result[0] = dialog.getValue().toUpperCase();
                }
            }
        });
        
        return result[0];
    }
    
    /**
     * Show enhanced diff preview with modification markers
     */
    private boolean showEnhancedDiffPreview(String originalCode, String markedCode, String operationType) {
        try {
            final boolean[] result = {false};
            
            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    String message = String.format(
                        "%s Preview\n\n" +
                        "Original code: %d lines\n" +
                        "Modified code with markers: %d lines\n\n" +
                        "The following modification markers will be added:\n" +
                        "- BEGIN MOD / END MOD for code changes\n" +
                        "- BEGIN INS / END INS for code insertions\n" +
                        "- Original code will be commented out with *\n\n" +
                        "Preview (first 500 characters):\n" +
                        "========================================\n" +
                        "%s\n" +
                        "========================================\n\n" +
                        "Apply these changes to the code?",
                        operationType,
                        originalCode.split("\n").length,
                        markedCode.split("\n").length,
                        getCodePreview(markedCode, 500)
                    );
                    
                    result[0] = MessageDialog.openConfirm(
                        Display.getDefault().getActiveShell(),
                        operationType + " Confirmation",
                        message
                    );
                }
            });
            
            return result[0];
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Extract code from AI response (simplified version)
     */
    private String extractCodeFromResponse(String response) {
        if (response == null || response.trim().isEmpty()) {
            return null;
        }
        
        // Look for code blocks marked with ```
        if (response.contains("```")) {
            int start = response.indexOf("```");
            int end = response.lastIndexOf("```");
            if (start != -1 && end != -1 && end > start) {
                String codeBlock = response.substring(start + 3, end).trim();
                // Remove language identifier if present
                if (codeBlock.startsWith("abap") || codeBlock.startsWith("sql")) {
                    codeBlock = codeBlock.substring(codeBlock.indexOf('\n') + 1);
                }
                return codeBlock;
            }
        }
        
        // If no code blocks found, return the response as is
        return response.trim();
    }
    
    /**
     * Create prompt for auto-fix
     */
    private String createAutoFixPrompt(String code) {
        return "Please analyze this ABAP code and fix any syntax errors, logical issues, or performance problems. " +
               "Return only the corrected ABAP code without explanations:\n\n" + code;
    }
    
    /**
     * Create prompt for auto-optimize
     */
    private String createAutoOptimizePrompt(String code) {
        return "Please optimize this ABAP code for better performance, readability, and following best practices. " +
               "Return only the optimized ABAP code without explanations:\n\n" + code;
    }
    
    /**
     * Get preview of code (first N characters)
     */
    private String getCodePreview(String code, int maxLength) {
        if (code == null) return "";
        if (code.length() <= maxLength) return code;
        return code.substring(0, maxLength) + "...";
    }
}
