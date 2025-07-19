package com.abap.assistant.services;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.jface.text.IDocument;

/**
 * Service for capturing current development context
 * Analyzes workspace, open files, variables, and includes
 */
public class ContextCaptureService {
    
    /**
     * Capture comprehensive context from current workspace
     */
    public String captureCurrentContext() {
        StringBuilder context = new StringBuilder();
        
        try {
            // Current project information
            String projectInfo = captureProjectContext();
            if (projectInfo != null && !projectInfo.trim().isEmpty()) {
                context.append("=== PROJECT CONTEXT ===\n");
                context.append(projectInfo);
                context.append("\n\n");
            }
            
            // Current editor context
            String editorInfo = captureEditorContext();
            if (editorInfo != null && !editorInfo.trim().isEmpty()) {
                context.append("=== CURRENT FILE CONTEXT ===\n");
                context.append(editorInfo);
                context.append("\n\n");
            }
            
            // Workspace overview
            String workspaceInfo = captureWorkspaceOverview();
            if (workspaceInfo != null && !workspaceInfo.trim().isEmpty()) {
                context.append("=== WORKSPACE OVERVIEW ===\n");
                context.append(workspaceInfo);
                context.append("\n");
            }
            
        } catch (Exception e) {
            context.append("Error capturing context: ").append(e.getMessage());
        }
        
        return context.toString();
    }
    
    /**
     * Capture current project context
     */
    private String captureProjectContext() {
        try {
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            IProject[] projects = root.getProjects();
            
            if (projects.length == 0) {
                return "No projects in workspace";
            }
            
            StringBuilder projectInfo = new StringBuilder();
            for (IProject project : projects) {
                if (project.isOpen()) {
                    projectInfo.append("Project: ").append(project.getName());
                    projectInfo.append(" (").append(project.getLocation()).append(")\n");
                    
                    // Add project nature information if available
                    try {
                        String[] natures = project.getDescription().getNatureIds();
                        if (natures.length > 0) {
                            projectInfo.append("  Natures: ");
                            for (String nature : natures) {
                                projectInfo.append(nature).append(" ");
                            }
                            projectInfo.append("\n");
                        }
                    } catch (Exception e) {
                        // Ignore nature capture errors
                    }
                }
            }
            
            return projectInfo.toString();
            
        } catch (Exception e) {
            return "Error capturing project context: " + e.getMessage();
        }
    }
    
    /**
     * Capture current editor context
     */
    private String captureEditorContext() {
        try {
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            IEditorPart editor = page.getActiveEditor();
            
            if (editor == null) {
                return "No active editor";
            }
            
            StringBuilder editorInfo = new StringBuilder();
            editorInfo.append("Active Editor: ").append(editor.getTitle()).append("\n");
            editorInfo.append("Editor Type: ").append(editor.getClass().getSimpleName()).append("\n");
            
            if (editor instanceof ITextEditor) {
                ITextEditor textEditor = (ITextEditor) editor;
                try {
                    IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
                    if (document != null) {
                        int lineCount = document.getNumberOfLines();
                        editorInfo.append("Lines: ").append(lineCount).append("\n");
                        editorInfo.append("Length: ").append(document.getLength()).append(" characters\n");
                        
                        // Get first few lines as sample
                        if (lineCount > 0) {
                            String firstLines = getFirstLines(document, 5);
                            if (firstLines != null && !firstLines.trim().isEmpty()) {
                                editorInfo.append("Content Preview:\n").append(firstLines).append("\n");
                            }
                        }
                    }
                } catch (Exception e) {
                    editorInfo.append("Error reading document content\n");
                }
            }
            
            return editorInfo.toString();
            
        } catch (Exception e) {
            return "Error capturing editor context: " + e.getMessage();
        }
    }
    
    /**
     * Capture workspace overview
     */
    private String captureWorkspaceOverview() {
        try {
            StringBuilder overview = new StringBuilder();
            
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            IEditorPart[] editors = page.getEditorReferences().length > 0 ? 
                new IEditorPart[page.getEditorReferences().length] : new IEditorPart[0];
            
            if (editors.length > 0) {
                overview.append("Open Editors: ");
                for (int i = 0; i < Math.min(editors.length, 5); i++) { // Limit to 5 for context size
                    try {
                        IEditorPart ed = page.getEditorReferences()[i].getEditor(false);
                        if (ed != null) {
                            overview.append(ed.getTitle()).append(" ");
                        }
                    } catch (Exception e) {
                        // Ignore individual editor errors
                    }
                }
                overview.append("\n");
            }
            
            return overview.toString();
            
        } catch (Exception e) {
            return "Error capturing workspace overview: " + e.getMessage();
        }
    }
    
    /**
     * Get first N lines from document
     */
    private String getFirstLines(IDocument document, int maxLines) {
        try {
            StringBuilder lines = new StringBuilder();
            int totalLines = Math.min(document.getNumberOfLines(), maxLines);
            
            for (int i = 0; i < totalLines; i++) {
                int offset = document.getLineOffset(i);
                int length = document.getLineLength(i);
                String line = document.get(offset, length);
                lines.append(line);
            }
            
            return lines.toString();
            
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Extract ABAP-specific context (variables, includes, etc.)
     */
    public String captureABAPContext(String code) {
        StringBuilder context = new StringBuilder();
        
        try {
            // Extract DATA declarations
            List<String> variables = extractABAPVariables(code);
            if (!variables.isEmpty()) {
                context.append("Variables found: ");
                for (String var : variables) {
                    context.append(var).append(" ");
                }
                context.append("\n");
            }
            
            // Extract INCLUDE statements
            List<String> includes = extractABAPIncludes(code);
            if (!includes.isEmpty()) {
                context.append("Includes found: ");
                for (String inc : includes) {
                    context.append(inc).append(" ");
                }
                context.append("\n");
            }
            
            // Extract FORM/FUNCTION definitions
            List<String> routines = extractABAPRoutines(code);
            if (!routines.isEmpty()) {
                context.append("Routines found: ");
                for (String routine : routines) {
                    context.append(routine).append(" ");
                }
                context.append("\n");
            }
            
        } catch (Exception e) {
            context.append("Error extracting ABAP context: ").append(e.getMessage());
        }
        
        return context.toString();
    }
    
    private List<String> extractABAPVariables(String code) {
        List<String> variables = new ArrayList<>();
        String[] lines = code.split("\n");
        
        for (String line : lines) {
            String trimmed = line.trim().toUpperCase();
            if (trimmed.startsWith("DATA ") || trimmed.startsWith("DATA:")) {
                // Simple extraction - could be enhanced
                String[] parts = trimmed.split("\\s+");
                if (parts.length > 1) {
                    String varName = parts[1].replaceAll("[,:]", "");
                    if (!varName.isEmpty()) {
                        variables.add(varName);
                    }
                }
            }
        }
        
        return variables;
    }
    
    private List<String> extractABAPIncludes(String code) {
        List<String> includes = new ArrayList<>();
        String[] lines = code.split("\n");
        
        for (String line : lines) {
            String trimmed = line.trim().toUpperCase();
            if (trimmed.startsWith("INCLUDE ")) {
                String[] parts = trimmed.split("\\s+");
                if (parts.length > 1) {
                    includes.add(parts[1].replace(".", ""));
                }
            }
        }
        
        return includes;
    }
    
    private List<String> extractABAPRoutines(String code) {
        List<String> routines = new ArrayList<>();
        String[] lines = code.split("\n");
        
        for (String line : lines) {
            String trimmed = line.trim().toUpperCase();
            if (trimmed.startsWith("FORM ") || trimmed.startsWith("FUNCTION ")) {
                String[] parts = trimmed.split("\\s+");
                if (parts.length > 1) {
                    String routineName = parts[1].replace(".", "");
                    if (!routineName.isEmpty()) {
                        routines.add(routineName);
                    }
                }
            }
        }
        
        return routines;
    }
}
