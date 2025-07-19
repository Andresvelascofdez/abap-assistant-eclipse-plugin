package com.abap.assistant.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Handler to open the ABAP Assistant View
 */
public class OpenViewHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
        IWorkbenchPage page = window.getActivePage();
        
        try {
            // Open the ABAP Assistant View
            page.showView("com.abap.assistant.views.ABAPAssistantView");
        } catch (PartInitException e) {
            throw new ExecutionException("Failed to open ABAP Assistant View", e);
        }
        
        return null;
    }
}
