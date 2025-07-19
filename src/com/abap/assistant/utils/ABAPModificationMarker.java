package com.abap.assistant.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for generating SAP ABAP modification markers
 * Uses configurable templates for BEGIN MOD, BEGIN INS, and END markers
 */
public class ABAPModificationMarker {
    
    private static final String DEFAULT_USER = System.getProperty("user.name", "USER").toUpperCase();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static ConfigurationManager config = ConfigurationManager.getInstance();
    
    /**
     * Generate BEGIN MOD marker using configurable template
     * @param ticketNumber Ticket number (e.g., "TICKET-XXX") 
     * @param user User making the change (optional, uses system user if null)
     * @return Formatted BEGIN MOD comment using configured template
     */
    public static String generateBeginModMarker(String ticketNumber, String user) {
        String actualUser = (user != null && !user.trim().isEmpty()) ? user.toUpperCase() : DEFAULT_USER;
        String currentDate = LocalDate.now().format(DATE_FORMAT);
        String template = config.getModBeginTemplate();
        
        return config.processTemplate(template, ticketNumber, actualUser, currentDate);
    }
    
    /**
     * Generate END MOD marker using configurable template
     * @param ticketNumber Ticket number (e.g., "TICKET-XXX")
     * @param user User making the change (optional, uses system user if null) 
     * @return Formatted END MOD comment using configured template
     */
    public static String generateEndModMarker(String ticketNumber, String user) {
        String actualUser = (user != null && !user.trim().isEmpty()) ? user.toUpperCase() : DEFAULT_USER;
        String currentDate = LocalDate.now().format(DATE_FORMAT);
        String template = config.getModEndTemplate();
        
        return config.processTemplate(template, ticketNumber, actualUser, currentDate);
    }
    
    /**
     * Generate BEGIN INS marker using configurable template
     * @param ticketNumber Ticket number (e.g., "TICKET-XXX")
     * @param user User making the change (optional, uses system user if null)
     * @return Formatted BEGIN INS comment using configured template
     */
    public static String generateBeginInsMarker(String ticketNumber, String user) {
        String actualUser = (user != null && !user.trim().isEmpty()) ? user.toUpperCase() : DEFAULT_USER;
        String currentDate = LocalDate.now().format(DATE_FORMAT);
        String template = config.getInsBeginTemplate();
        
        return config.processTemplate(template, ticketNumber, actualUser, currentDate);
    }
    
    /**
     * Generate END INS marker using configurable template
     * @param ticketNumber Ticket number (e.g., "TICKET-XXX")
     * @param user User making the change (optional, uses system user if null)
     * @return Formatted END INS comment using configured template
     */
    public static String generateEndInsMarker(String ticketNumber, String user) {
        String actualUser = (user != null && !user.trim().isEmpty()) ? user.toUpperCase() : DEFAULT_USER;
        String currentDate = LocalDate.now().format(DATE_FORMAT);
        String template = config.getInsEndTemplate();
        
        return config.processTemplate(template, ticketNumber, actualUser, currentDate);
    }
    
    /**
     * Comment out a line of code with ABAP comment syntax
     * @param codeLine The line of code to comment out
     * @return Commented line with * prefix
     */
    public static String commentOutLine(String codeLine) {
        if (codeLine == null || codeLine.trim().isEmpty()) {
            return codeLine;
        }
        
        // Remove leading whitespace, add *, then add back whitespace
        String trimmed = codeLine.trim();
        String leadingWhitespace = codeLine.substring(0, codeLine.indexOf(trimmed));
        
        return leadingWhitespace + "*" + trimmed;
    }
    
    /**
     * Wrap modified code block with BEGIN MOD / END MOD markers
     * @param originalCode Original code to be commented out
     * @param newCode New replacement code
     * @param ticketNumber Ticket number
     * @param user User making change
     * @return Complete modification block with markers
     */
    public static String wrapModification(String originalCode, String newCode, String ticketNumber, String user) {
        StringBuilder result = new StringBuilder();
        
        // Add BEGIN MOD marker
        result.append(generateBeginModMarker(ticketNumber, user)).append("\n");
        
        // Comment out original code
        String[] originalLines = originalCode.split("\n");
        for (String line : originalLines) {
            result.append(commentOutLine(line)).append("\n");
        }
        
        // Add new code
        result.append(newCode);
        if (!newCode.endsWith("\n")) {
            result.append("\n");
        }
        
        // Add END MOD marker
        result.append(generateEndModMarker(ticketNumber, user));
        
        return result.toString();
    }
    
    /**
     * Wrap inserted code block with BEGIN INS / END INS markers
     * @param newCode New code to be inserted
     * @param ticketNumber Ticket number
     * @param user User making change
     * @return Complete insertion block with markers
     */
    public static String wrapInsertion(String newCode, String ticketNumber, String user) {
        StringBuilder result = new StringBuilder();
        
        // Add BEGIN INS marker
        result.append(generateBeginInsMarker(ticketNumber, user)).append("\n");
        
        // Add new code
        result.append(newCode);
        if (!newCode.endsWith("\n")) {
            result.append("\n");
        }
        
        // Add END INS marker
        result.append(generateEndInsMarker(ticketNumber, user));
        
        return result.toString();
    }
    
    /**
     * Validate ticket number format
     * @param ticketNumber Ticket number to validate
     * @return true if valid format (TICKET-XXX or similar)
     */
    public static boolean isValidTicketNumber(String ticketNumber) {
        if (ticketNumber == null || ticketNumber.trim().isEmpty()) {
            return false;
        }
        
        // Accept formats like TICKET-XXX, INC-123, CHG-456, etc.
        String pattern = "^[A-Z]{3,10}-[A-Z0-9]{1,10}$";
        return ticketNumber.toUpperCase().matches(pattern);
    }
}
