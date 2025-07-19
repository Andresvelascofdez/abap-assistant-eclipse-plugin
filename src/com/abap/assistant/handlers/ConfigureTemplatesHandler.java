package com.abap.assistant.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import com.abap.assistant.dialogs.ModificationTemplatesDialog;

/**
 * Handler for opening the modification templates configuration dialog
 */
public class ConfigureTemplatesHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        try {
            ModificationTemplatesDialog dialog = new ModificationTemplatesDialog(
                HandlerUtil.getActiveShellChecked(event));
            
            int result = dialog.open();
            
            if (result == ModificationTemplatesDialog.OK) {
                MessageDialog.openInformation(
                    HandlerUtil.getActiveShell(event),
                    "ABAP Assistant",
                    "Modification templates have been saved successfully."
                );
            }
            
        } catch (Exception e) {
            MessageDialog.openError(
                HandlerUtil.getActiveShell(event),
                "ABAP Assistant Error",
                "Failed to open templates configuration: " + e.getMessage()
            );
        }
        
        return null;
    }
}
