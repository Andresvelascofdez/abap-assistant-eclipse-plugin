package com.abap.assistant.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import com.abap.assistant.views.ABAPAssistantView;
import com.abap.assistant.services.ChatGPTService;
import com.abap.assistant.services.ContextCaptureService;
import com.abap.assistant.utils.EditorUtils;

/**
 * Handler for Quick Explain command
 * Provides instant ABAP code explanation
 */
public class QuickExplainHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
        IWorkbenchPage page = window.getActivePage();
        
        try {
            // Get or open the ABAP Assistant view
            ABAPAssistantView view = (ABAPAssistantView) page.showView(ABAPAssistantView.ID);
            
            // Get selected code from active editor
            String selectedCode = EditorUtils.getSelectedText();
            if (selectedCode == null || selectedCode.trim().isEmpty()) {
                selectedCode = EditorUtils.getCurrentFileContent();
                if (selectedCode == null || selectedCode.trim().isEmpty()) {
                    EditorUtils.showMessage("Please select ABAP code or open an ABAP file to explain");
                    return null;
                }
            }
            
            // Capture context
            ContextCaptureService contextService = new ContextCaptureService();
            String context = contextService.captureCurrentContext();
            String abapContext = contextService.captureABAPContext(selectedCode);
            
            // Create enhanced prompt
            String prompt = ChatGPTService.ABAPPromptBuilder.buildExplainPrompt(selectedCode, context + "\n" + abapContext);
            
            // Send to AI service
            ChatGPTService chatService = new ChatGPTService();
            
            // This would typically be handled asynchronously
            new Thread(() -> {
                try {
                    chatService.sendMessage(prompt);
                    // The view will handle displaying the response
                } catch (Exception e) {
                    EditorUtils.showErrorMessage("Quick Explain Error", e.getMessage());
                }
            }).start();
            
            EditorUtils.showMessage("Quick Explain request sent to AI Assistant");
            
        } catch (PartInitException e) {
            throw new ExecutionException("Error opening ABAP Assistant view", e);
        }
        
        return null;
    }
}
