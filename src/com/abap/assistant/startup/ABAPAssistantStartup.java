package com.abap.assistant.startup;

import org.eclipse.ui.IStartup;

import com.abap.assistant.utils.ConfigurationManager;

/**
 * Startup class for ABAP Assistant plugin
 */
public class ABAPAssistantStartup implements IStartup {

    @Override
    public void earlyStartup() {
        // Initialize configuration
        ConfigurationManager.getInstance();
        
        // Register any necessary listeners
        
        System.out.println("ABAP Assistant Plugin started successfully!");
    }
}
