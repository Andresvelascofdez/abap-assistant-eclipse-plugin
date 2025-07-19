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
        setDescription("Configure ABAP Assistant settings");
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
        
        // Max tokens
        IntegerFieldEditor maxTokensEditor = new IntegerFieldEditor(
            "max_tokens",
            "Max Tokens:",
            getFieldEditorParent()
        );
        maxTokensEditor.setValidRange(100, 8000);
        addField(maxTokensEditor);
        
        // Temperature
        addField(new StringFieldEditor(
            "temperature",
            "Temperature (0.0 - 2.0):",
            getFieldEditorParent()
        ) {
            @Override
            protected boolean doCheckState() {
                try {
                    double value = Double.parseDouble(getStringValue());
                    if (value < 0.0 || value > 2.0) {
                        setErrorMessage("Temperature must be between 0.0 and 2.0");
                        return false;
                    }
                } catch (NumberFormatException e) {
                    setErrorMessage("Temperature must be a valid number");
                    return false;
                }
                clearErrorMessage();
                return true;
            }
        });
        
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
            config.setMaxTokens(getPreferenceStore().getInt("max_tokens"));
            
            try {
                double temp = Double.parseDouble(getPreferenceStore().getString("temperature"));
                config.setTemperature(temp);
            } catch (NumberFormatException e) {
                // Use default temperature
            }
            
            config.setEnterpriseAuditEnabled(getPreferenceStore().getBoolean("enterprise_audit"));
            config.setAutoSaveEnabled(getPreferenceStore().getBoolean("auto_save"));
        }
        
        return result;
    }
    
    @Override
    protected void performDefaults() {
        super.performDefaults();
        
        // Set default values
        getPreferenceStore().setDefault("model", "gpt-3.5-turbo");
        getPreferenceStore().setDefault("max_tokens", 2000);
        getPreferenceStore().setDefault("temperature", "0.7");
        getPreferenceStore().setDefault("enterprise_audit", true);
        getPreferenceStore().setDefault("auto_save", false);
    }
}
