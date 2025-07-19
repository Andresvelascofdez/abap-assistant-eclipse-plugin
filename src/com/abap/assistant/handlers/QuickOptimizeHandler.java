package com.abap.assistant.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.abap.assistant.utils.EditorUtils;
import com.abap.assistant.services.ChatGPTService;
import com.abap.assistant.services.ContextCaptureService;

/**
 * Handler for Quick Optimize command
 */
public class QuickOptimizeHandler extends AbstractHandler {

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
        
        ContextCaptureService contextService = new ContextCaptureService();
        String context = contextService.captureCurrentContext();
        String abapContext = contextService.captureABAPContext(selectedCode);
        
        String prompt = ChatGPTService.ABAPPromptBuilder.buildOptimizePrompt(
            selectedCode, context + "\n" + abapContext);
        
        new Thread(() -> {
            try {
                ChatGPTService chatService = new ChatGPTService();
                chatService.sendMessage(prompt);
            } catch (Exception e) {
                EditorUtils.showErrorMessage("Quick Optimize Error", e.getMessage());
            }
        }).start();
        
        EditorUtils.showMessage("Quick Optimize request sent to AI Assistant");
        return null;
    }
}
