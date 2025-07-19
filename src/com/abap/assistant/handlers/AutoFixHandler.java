package com.abap.assistant.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import com.abap.assistant.services.EnhancedAutoModificationService;
import com.abap.assistant.utils.EditorUtils;

/**
 * Enhanced Handler for Auto Fix command with ABAP modification markers
 * Automatically fixes code issues with proper MOD markers and ticket tracking
 */
public class AutoFixHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        
        String selectedCode = EditorUtils.getSelectedText();
        if (selectedCode == null || selectedCode.trim().isEmpty()) {
            selectedCode = EditorUtils.getCurrentFileContent();
            if (selectedCode == null || selectedCode.trim().isEmpty()) {
                EditorUtils.showMessage("Please select ABAP code or open an ABAP file to auto-fix");
                return null;
            }
        }
        
        // Show information about the enhanced process
        boolean userWantsEnhanced = MessageDialog.openQuestion(
            HandlerUtil.getActiveShell(event),
            "Auto Fix with ABAP Markers",
            "ABAP Assistant will now fix your code and add proper modification markers:\n\n" +
            "• Original code will be commented out with *\n" +
            "• Fixed code will be added with BEGIN MOD / END MOD markers\n" +
            "• You'll be asked for a ticket number for tracking\n" +
            "• Your system user (" + System.getProperty("user.name") + ") will be used automatically\n\n" +
            "Continue with enhanced auto fix?"
        );
        
        if (!userWantsEnhanced) {
            return null; // User cancelled
        }
        
        // Show progress message
        EditorUtils.showMessage("ABAP Assistant is analyzing your code for fixes with modification markers...");
        
        EnhancedAutoModificationService enhancedService = new EnhancedAutoModificationService();
        
        // Use enhanced auto fix with modification markers
        boolean success = enhancedService.performEnhancedAutoFix(selectedCode, true);
        
        if (!success) {
            EditorUtils.showMessage("Enhanced auto fix was cancelled or no fixes were needed.");
        } else {
            EditorUtils.showMessage("Auto fix completed successfully with ABAP modification markers!");
        }
        
        return null;
    }
}
