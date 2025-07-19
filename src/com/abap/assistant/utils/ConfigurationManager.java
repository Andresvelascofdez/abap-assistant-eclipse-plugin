package com.abap.assistant.utils;

import java.util.prefs.Preferences;

/**
 * Manages configuration settings for the ABAP Assistant plugin
 */
public class ConfigurationManager {
    
    private static ConfigurationManager instance;
    private Preferences prefs;
    
    // Configuration keys
    private static final String API_KEY = "openai_api_key";
    private static final String MODEL = "gpt_model";
    private static final String MAX_TOKENS = "max_tokens";
    private static final String TEMPERATURE = "temperature";
    private static final String ENTERPRISE_AUDIT = "enterprise_audit";
    private static final String AUTO_SAVE = "auto_save";
    
    // ABAP Modification Marker Templates
    private static final String MOD_BEGIN_TEMPLATE = "mod_begin_template";
    private static final String MOD_END_TEMPLATE = "mod_end_template";
    private static final String INS_BEGIN_TEMPLATE = "ins_begin_template";
    private static final String INS_END_TEMPLATE = "ins_end_template";
    
    // Default values
    private static final String DEFAULT_MODEL = "gpt-3.5-turbo";
    private static final int DEFAULT_MAX_TOKENS = 2000;
    private static final double DEFAULT_TEMPERATURE = 0.7;
    
    // Default ABAP modification templates with placeholders
    // {TICKET} = ticket number, {USER} = username, {DATE} = current date
    private static final String DEFAULT_MOD_BEGIN = "*BEGIN MOD {TICKET} {USER} {DATE}";
    private static final String DEFAULT_MOD_END = "*END MOD {TICKET} {USER} {DATE}";
    private static final String DEFAULT_INS_BEGIN = "*BEGIN INS {TICKET} {USER} {DATE}";
    private static final String DEFAULT_INS_END = "*END INS {TICKET} {USER} {DATE}";
    
    private ConfigurationManager() {
        prefs = Preferences.userNodeForPackage(ConfigurationManager.class);
    }
    
    public static ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }
    
    // API Key management
    public String getApiKey() {
        return prefs.get(API_KEY, null);
    }
    
    public void setApiKey(String apiKey) {
        if (apiKey != null && !apiKey.trim().isEmpty()) {
            prefs.put(API_KEY, apiKey.trim());
        } else {
            prefs.remove(API_KEY);
        }
    }
    
    // Model configuration
    public String getModel() {
        return prefs.get(MODEL, DEFAULT_MODEL);
    }
    
    public void setModel(String model) {
        prefs.put(MODEL, model);
    }
    
    // Token limit
    public int getMaxTokens() {
        return prefs.getInt(MAX_TOKENS, DEFAULT_MAX_TOKENS);
    }
    
    public void setMaxTokens(int maxTokens) {
        prefs.putInt(MAX_TOKENS, maxTokens);
    }
    
    // Temperature setting
    public double getTemperature() {
        return prefs.getDouble(TEMPERATURE, DEFAULT_TEMPERATURE);
    }
    
    public void setTemperature(double temperature) {
        prefs.putDouble(TEMPERATURE, temperature);
    }
    
    // Enterprise features
    public boolean isEnterpriseAuditEnabled() {
        return prefs.getBoolean(ENTERPRISE_AUDIT, true);
    }
    
    public void setEnterpriseAuditEnabled(boolean enabled) {
        prefs.putBoolean(ENTERPRISE_AUDIT, enabled);
    }
    
    // Auto-save feature
    public boolean isAutoSaveEnabled() {
        return prefs.getBoolean(AUTO_SAVE, false);
    }
    
    public void setAutoSaveEnabled(boolean enabled) {
        prefs.putBoolean(AUTO_SAVE, enabled);
    }
    
    // ABAP Modification Templates
    public String getModBeginTemplate() {
        return prefs.get(MOD_BEGIN_TEMPLATE, DEFAULT_MOD_BEGIN);
    }
    
    public void setModBeginTemplate(String template) {
        if (template != null && !template.trim().isEmpty()) {
            prefs.put(MOD_BEGIN_TEMPLATE, template.trim());
        } else {
            prefs.remove(MOD_BEGIN_TEMPLATE);
        }
    }
    
    public String getModEndTemplate() {
        return prefs.get(MOD_END_TEMPLATE, DEFAULT_MOD_END);
    }
    
    public void setModEndTemplate(String template) {
        if (template != null && !template.trim().isEmpty()) {
            prefs.put(MOD_END_TEMPLATE, template.trim());
        } else {
            prefs.remove(MOD_END_TEMPLATE);
        }
    }
    
    public String getInsBeginTemplate() {
        return prefs.get(INS_BEGIN_TEMPLATE, DEFAULT_INS_BEGIN);
    }
    
    public void setInsBeginTemplate(String template) {
        if (template != null && !template.trim().isEmpty()) {
            prefs.put(INS_BEGIN_TEMPLATE, template.trim());
        } else {
            prefs.remove(INS_BEGIN_TEMPLATE);
        }
    }
    
    public String getInsEndTemplate() {
        return prefs.get(INS_END_TEMPLATE, DEFAULT_INS_END);
    }
    
    public void setInsEndTemplate(String template) {
        if (template != null && !template.trim().isEmpty()) {
            prefs.put(INS_END_TEMPLATE, template.trim());
        } else {
            prefs.remove(INS_END_TEMPLATE);
        }
    }
    
    /**
     * Process template with placeholders
     * @param template Template string with {TICKET}, {USER}, {DATE} placeholders
     * @param ticketNumber Ticket number to substitute
     * @param userName User name to substitute  
     * @param date Date string to substitute
     * @return Processed template string
     */
    public String processTemplate(String template, String ticketNumber, String userName, String date) {
        if (template == null) return "";
        
        return template
            .replace("{TICKET}", ticketNumber != null ? ticketNumber : "UNKNOWN")
            .replace("{USER}", userName != null ? userName : "USER")
            .replace("{DATE}", date != null ? date : "");
    }
    
    /**
     * Validate configuration
     */
    public boolean isValidConfiguration() {
        String apiKey = getApiKey();
        return apiKey != null && !apiKey.trim().isEmpty() && apiKey.startsWith("sk-");
    }
    
    /**
     * Reset to defaults
     */
    public void resetToDefaults() {
        prefs.remove(MODEL);
        prefs.remove(MAX_TOKENS);
        prefs.remove(TEMPERATURE);
        prefs.remove(ENTERPRISE_AUDIT);
        prefs.remove(AUTO_SAVE);
        prefs.remove(MOD_BEGIN_TEMPLATE);
        prefs.remove(MOD_END_TEMPLATE);
        prefs.remove(INS_BEGIN_TEMPLATE);
        prefs.remove(INS_END_TEMPLATE);
        // Note: We don't reset API key for security
    }
    
    /**
     * Get available GPT models
     */
    public static String[] getAvailableModels() {
        return new String[] {
            "gpt-3.5-turbo",
            "gpt-3.5-turbo-16k",
            "gpt-4",
            "gpt-4-32k",
            "gpt-4-turbo-preview"
        };
    }
}
