package com.abap.assistant.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Handler to open API Configuration preferences
 */
public class ConfigureAPIHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
        
        // Open the preferences dialog at the ABAP Assistant page
        PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(
            window.getShell(),
            "com.abap.assistant.preferences.ABAPAssistantPreferencePage",
            null,
            null
        );
        
        if (dialog != null) {
            dialog.open();
        }
        
        return null;
    }
}
