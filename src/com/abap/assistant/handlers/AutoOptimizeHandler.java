package com.abap.assistant.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import com.abap.assistant.services.EnhancedAutoModificationService;
import com.abap.assistant.utils.EditorUtils;

/**
 * Enhanced Handler for Auto Optimize command with ABAP modification markers
 * Automatically optimizes code with proper MOD markers and ticket tracking
 */
public class AutoOptimizeHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        
        String selectedCode = EditorUtils.getSelectedText();
        if (selectedCode == null || selectedCode.trim().isEmpty()) {
            selectedCode = EditorUtils.getCurrentFileContent();
            if (selectedCode == null || selectedCode.trim().isEmpty()) {
                EditorUtils.showMessage("Please select ABAP code or open an ABAP file to optimize");
                return null;
            }
        }
        
        // Show information about the enhanced process
        boolean userWantsEnhanced = MessageDialog.openQuestion(
            HandlerUtil.getActiveShell(event),
            "Auto Optimize with ABAP Markers",
            "ABAP Assistant will now optimize your code and add proper modification markers:\n\n" +
            "• Original code will be commented out with *\n" +
            "• Optimized code will be added with BEGIN MOD / END MOD markers\n" +
            "• You'll be asked for a ticket number for tracking\n" +
            "• Your system user (" + System.getProperty("user.name") + ") will be used automatically\n\n" +
            "Continue with enhanced auto optimization?"
        );
        
        if (!userWantsEnhanced) {
            return null; // User cancelled
        }
        
        // Show progress message
        EditorUtils.showMessage("ABAP Assistant is analyzing your code for optimizations with modification markers...");
        
        EnhancedAutoModificationService enhancedService = new EnhancedAutoModificationService();
        
        // Use enhanced auto optimize with modification markers
        boolean success = enhancedService.performEnhancedAutoOptimize(selectedCode, true);
        
        if (!success) {
            EditorUtils.showMessage("Enhanced auto optimization was cancelled or no optimizations were needed.");
        } else {
            EditorUtils.showMessage("Auto optimization completed successfully with ABAP modification markers!");
        }
        
        return null;
    }
}
