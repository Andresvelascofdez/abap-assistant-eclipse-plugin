package com.abap.assistant.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 * Service for processing various document formats
 * Supports PDF, DOC, DOCX, TXT, and ABAP files
 */
public class DocumentProcessorService {
    
    private static final int MAX_CONTENT_LENGTH = 10000; // Limit content to prevent token overflow
    
    /**
     * Process document and extract text content
     */
    public String processDocument(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File not found: " + filePath);
        }
        
        String extension = getFileExtension(filePath).toLowerCase();
        String content = "";
        
        switch (extension) {
            case "pdf":
                content = processPDF(filePath);
                break;
            case "doc":
                content = processDOC(filePath);
                break;
            case "docx":
                content = processDOCX(filePath);
                break;
            case "txt":
            case "abap":
            case "java":
            case "xml":
            case "json":
                content = processTextFile(filePath);
                break;
            default:
                // Try to process as text file
                content = processTextFile(filePath);
                break;
        }
        
        // Limit content length
        if (content.length() > MAX_CONTENT_LENGTH) {
            content = content.substring(0, MAX_CONTENT_LENGTH) + "\n\n... (content truncated)";
        }
        
        return content;
    }
    
    /**
     * Process PDF files using PDFBox
     */
    private String processPDF(String filePath) throws IOException {
        try (PDDocument document = PDDocument.load(new File(filePath))) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
    
    /**
     * Process DOC files using Apache POI
     */
    private String processDOC(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath);
             HWPFDocument document = new HWPFDocument(fis);
             WordExtractor extractor = new WordExtractor(document)) {
            return extractor.getText();
        }
    }
    
    /**
     * Process DOCX files using Apache POI
     */
    private String processDOCX(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath);
             XWPFDocument document = new XWPFDocument(fis);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText();
        }
    }
    
    /**
     * Process text-based files
     */
    private String processTextFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }
    
    /**
     * Get file extension from path
     */
    private String getFileExtension(String filePath) {
        int lastDotIndex = filePath.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filePath.length() - 1) {
            return "";
        }
        return filePath.substring(lastDotIndex + 1);
    }
    
    /**
     * Check if file type is supported
     */
    public boolean isSupportedFileType(String filePath) {
        String extension = getFileExtension(filePath).toLowerCase();
        return extension.equals("pdf") || 
               extension.equals("doc") || 
               extension.equals("docx") || 
               extension.equals("txt") || 
               extension.equals("abap") || 
               extension.equals("java") || 
               extension.equals("xml") || 
               extension.equals("json");
    }
    
    /**
     * Get supported file extensions for dialog filters
     */
    public static String[] getSupportedExtensions() {
        return new String[] {
            "*.pdf", "*.doc", "*.docx", "*.txt", "*.abap", "*.java", "*.xml", "*.json", "*.*"
        };
    }
    
    /**
     * Get supported file descriptions for dialog filters
     */
    public static String[] getSupportedDescriptions() {
        return new String[] {
            "PDF Files", "Word Documents (DOC)", "Word Documents (DOCX)", 
            "Text Files", "ABAP Files", "Java Files", "XML Files", "JSON Files", "All Files"
        };
    }
}
