package com.abap.assistant.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

import com.abap.assistant.dialogs.DocumentContextDialog;
import com.abap.assistant.services.DocumentContextManager;

/**
 * Handler for managing document context settings
 */
public class ManageDocumentContextHandler extends AbstractHandler {

    private static DocumentContextManager contextManager;
    
    /**
     * Set the context manager instance (called from ABAPAssistantView)
     */
    public static void setContextManager(DocumentContextManager manager) {
        contextManager = manager;
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        if (contextManager == null) {
            contextManager = new DocumentContextManager();
        }
        
        DocumentContextDialog dialog = new DocumentContextDialog(
            HandlerUtil.getActiveShell(event), 
            contextManager
        );
        
        dialog.open();
        return null;
    }
}
