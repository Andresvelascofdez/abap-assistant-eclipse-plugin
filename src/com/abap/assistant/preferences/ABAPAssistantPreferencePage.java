package com.abap.assistant.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.abap.assistant.utils.ConfigurationManager;

/**
 * Preferences page for ABAP Assistant configuration
 */
public class ABAPAssistantPreferencePage extends FieldEditorPreferencePage 
    implements IWorkbenchPreferencePage {

    public ABAPAssistantPreferencePage() {
        super(GRID);
        setDescription("Configure ABAP Assistant settings\n\n" +
                      "Technical parameters (Max Tokens: 8000, Temperature: 0.0) are automatically optimized for ABAP development.");
    }

    @Override
    public void createFieldEditors() {
        
        // API Key configuration
        addField(new StringFieldEditor(
            "api_key",
            "OpenAI API Key:",
            getFieldEditorParent()
        ) {
            @Override
            protected boolean doCheckState() {
                String value = getStringValue();
                if (value == null || value.trim().isEmpty()) {
                    setErrorMessage("API Key is required");
                    return false;
                }
                if (!value.startsWith("sk-")) {
                    setErrorMessage("API Key should start with 'sk-'");
                    return false;
                }
                clearErrorMessage();
                return true;
            }
        });
        
        // Model selection
        String[][] models = {
            {"GPT-3.5 Turbo (Recommended)", "gpt-3.5-turbo"},
            {"GPT-3.5 Turbo 16K", "gpt-3.5-turbo-16k"},
            {"GPT-4", "gpt-4"},
            {"GPT-4 32K", "gpt-4-32k"},
            {"GPT-4 Turbo", "gpt-4-turbo-preview"}
        };
        
        addField(new ComboFieldEditor(
            "model",
            "AI Model:",
            models,
            getFieldEditorParent()
        ));
        
        // Enterprise features
        addField(new BooleanFieldEditor(
            "enterprise_audit",
            "Enable Enterprise Audit Trail",
            getFieldEditorParent()
        ));
        
        // Auto-save
        addField(new BooleanFieldEditor(
            "auto_save",
            "Enable Auto-Save (for Auto Fix/Optimize)",
            getFieldEditorParent()
        ));
    }

    @Override
    public void init(IWorkbench workbench) {
        // Initialize preference store
        setPreferenceStore(getPreferenceStore());
    }
    
    @Override
    public boolean performOk() {
        boolean result = super.performOk();
        
        if (result) {
            // Save preferences to ConfigurationManager
            ConfigurationManager config = ConfigurationManager.getInstance();
            config.setApiKey(getPreferenceStore().getString("api_key"));
            config.setModel(getPreferenceStore().getString("model"));
            
            // Use optimal defaults - no user configuration needed
            config.setMaxTokens(8000);  // Optimal for ABAP code responses
            config.setTemperature(0.0); // Deterministic responses for code
            
            config.setEnterpriseAuditEnabled(getPreferenceStore().getBoolean("enterprise_audit"));
            config.setAutoSaveEnabled(getPreferenceStore().getBoolean("auto_save"));
        }
        
        return result;
    }
    
    @Override
    protected void performDefaults() {
        super.performDefaults();
        
        // Set optimal default values for ABAP development
        getPreferenceStore().setDefault("model", "gpt-4");  // Best model for code
        getPreferenceStore().setDefault("enterprise_audit", true);
        getPreferenceStore().setDefault("auto_save", false);
        
        // Technical parameters are handled automatically with optimal values:
        // - Max Tokens: 8000 (allows complete ABAP code responses)
        // - Temperature: 0.0 (deterministic, precise responses for code)
    }
}
