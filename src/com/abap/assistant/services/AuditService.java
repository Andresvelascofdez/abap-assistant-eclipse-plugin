package com.abap.assistant.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Enterprise audit service for tracking code modifications
 */
public class AuditService {
    
    private static final String AUDIT_LOG_FILE = "abap_assistant_audit.log";
    private List<AuditEntry> auditEntries;
    
    public AuditService() {
        this.auditEntries = new ArrayList<>();
    }
    
    /**
     * Record a code modification in the audit trail
     */
    public void recordModification(String modificationType, String originalCode, 
                                 String newCode, String fileName) {
        AuditEntry entry = new AuditEntry(
            modificationType,
            fileName,
            originalCode,
            newCode,
            System.getProperty("user.name"),
            LocalDateTime.now()
        );
        
        auditEntries.add(entry);
        writeToAuditLog(entry);
    }
    
    /**
     * Record a code modification with ticket information in the audit trail
     */
    public void recordModificationWithTicket(String modificationType, String originalCode, 
                                           String newCode, String fileName, 
                                           String ticketNumber, String userName) {
        String actualUserName = (userName != null) ? userName : System.getProperty("user.name");
        String modificationTypeWithTicket = ticketNumber != null ? 
            modificationType + " (Ticket: " + ticketNumber + ")" : modificationType;
            
        AuditEntry entry = new AuditEntry(
            modificationTypeWithTicket,
            fileName,
            originalCode,
            newCode,
            actualUserName,
            LocalDateTime.now()
        );
        
        auditEntries.add(entry);
        writeToAuditLog(entry);
    }
    
    /**
     * Get audit history
     */
    public List<AuditEntry> getAuditHistory() {
        return new ArrayList<>(auditEntries);
    }
    
    /**
     * Clear audit history
     */
    public void clearAuditHistory() {
        auditEntries.clear();
    }
    
    private void writeToAuditLog(AuditEntry entry) {
        try {
            File logFile = new File(AUDIT_LOG_FILE);
            try (FileWriter writer = new FileWriter(logFile, true)) {
                writer.write(entry.toLogString() + "\n");
            }
        } catch (IOException e) {
            // Log error but don't throw
            System.err.println("Failed to write to audit log: " + e.getMessage());
        }
    }
    
    /**
     * Audit entry class
     */
    public static class AuditEntry {
        private final String modificationType;
        private final String fileName;
        private final String originalCode;
        private final String newCode;
        private final String username;
        private final LocalDateTime timestamp;
        
        public AuditEntry(String modificationType, String fileName, String originalCode,
                         String newCode, String username, LocalDateTime timestamp) {
            this.modificationType = modificationType;
            this.fileName = fileName;
            this.originalCode = originalCode;
            this.newCode = newCode;
            this.username = username;
            this.timestamp = timestamp;
        }
        
        public String getModificationType() {
            return modificationType;
        }
        
        public String getFileName() {
            return fileName;
        }
        
        public String getOriginalCode() {
            return originalCode;
        }
        
        public String getNewCode() {
            return newCode;
        }
        
        public String getUsername() {
            return username;
        }
        
        public LocalDateTime getTimestamp() {
            return timestamp;
        }
        
        public String toLogString() {
            return String.format(
                "[%s] %s by %s on %s - Original: %s chars, New: %s chars",
                timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                modificationType,
                username,
                fileName != null ? fileName : "Unknown",
                originalCode.length(),
                newCode.length()
            );
        }
        
        @Override
        public String toString() {
            return toLogString();
        }
    }
}
