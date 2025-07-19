package com.abap.assistant.services;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;
import com.abap.assistant.utils.EditorUtils;
import com.abap.assistant.models.CodeContext;
import com.abap.assistant.models.VariableInfo;
import com.abap.assistant.models.DependencyInfo;

/**
 * Servicio para análisis profundo de código ABAP
 * Proporciona capacidades de comprensión de código, tracking de variables y análisis de dependencias
 */
public class CodeAnalysisService {
    
    private static CodeAnalysisService instance;
    private Map<String, CodeContext> codeContextCache = new HashMap<>();
    
    // Patrones regex para ABAP
    private static final Pattern VARIABLE_DECLARATION_PATTERN = Pattern.compile(
        "(?i)(?:data|types|constants|field-symbols|parameters|select-options)\\s+([\\w-]+)(?:\\s+type\\s+([\\w\\(\\)\\s,-]+))?", 
        Pattern.MULTILINE);
    
    private static final Pattern METHOD_CALL_PATTERN = Pattern.compile(
        "(?i)(?:call\\s+method|call\\s+function|perform)\\s+([\\w-]+)", 
        Pattern.MULTILINE);
    
    private static final Pattern CLASS_DEFINITION_PATTERN = Pattern.compile(
        "(?i)class\\s+([\\w-]+)\\s+definition", 
        Pattern.MULTILINE);
    
    private static final Pattern INCLUDE_PATTERN = Pattern.compile(
        "(?i)include\\s+([\\w-]+)", 
        Pattern.MULTILINE);
    
    private static final Pattern VARIABLE_USAGE_PATTERN = Pattern.compile(
        "(?i)([\\w-]+)\\s*(?:=|into|from|to|\\+|\\-|\\*|\\/)", 
        Pattern.MULTILINE);

    public static CodeAnalysisService getInstance() {
        if (instance == null) {
            instance = new CodeAnalysisService();
        }
        return instance;
    }

    /**
     * Analiza el código en la ventana activa del editor
     */
    public CodeContext analyzeCurrentEditorContent() {
        try {
            IEditorPart activeEditor = EditorUtils.getActiveEditor();
            if (activeEditor instanceof ITextEditor) {
                ITextEditor textEditor = (ITextEditor) activeEditor;
                IDocument document = textEditor.getDocumentProvider()
                    .getDocument(textEditor.getEditorInput());
                
                String content = document.get();
                String fileName = textEditor.getEditorInput().getName();
                
                return performDeepAnalysis(fileName, content);
            }
        } catch (Exception e) {
            System.err.println("Error analyzing current editor content: " + e.getMessage());
        }
        return null;
    }

    /**
     * Analiza todo el proyecto para obtener contexto completo
     */
    public Map<String, CodeContext> analyzeCompleteProject() {
        Map<String, CodeContext> projectContext = new HashMap<>();
        
        try {
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            IProject[] projects = root.getProjects();
            
            for (IProject project : projects) {
                if (project.isOpen()) {
                    analyzeProjectRecursive(project, projectContext);
                }
            }
        } catch (Exception e) {
            System.err.println("Error analyzing complete project: " + e.getMessage());
        }
        
        return projectContext;
    }

    /**
     * Realiza análisis profundo de un archivo específico
     */
    public CodeContext performDeepAnalysis(String fileName, String content) {
        CodeContext context = new CodeContext(fileName);
        
        // 1. Code Understanding - Análisis estructural
        analyzeCodeStructure(content, context);
        
        // 2. Variable Tracking - Seguimiento de variables
        trackVariables(content, context);
        
        // 3. Dependency Analysis - Análisis de dependencias  
        analyzeDependencies(content, context);
        
        // Cache del resultado
        codeContextCache.put(fileName, context);
        
        return context;
    }

    /**
     * Analiza la estructura del código
     */
    private void analyzeCodeStructure(String content, CodeContext context) {
        // Detectar tipo de programa ABAP
        context.setProgramType(detectProgramType(content));
        
        // Encontrar clases
        Matcher classMatcher = CLASS_DEFINITION_PATTERN.matcher(content);
        while (classMatcher.find()) {
            context.addClassName(classMatcher.group(1));
        }
        
        // Encontrar includes
        Matcher includeMatcher = INCLUDE_PATTERN.matcher(content);
        while (includeMatcher.find()) {
            context.addInclude(includeMatcher.group(1));
        }
        
        // Encontrar llamadas a métodos/funciones
        Matcher methodMatcher = METHOD_CALL_PATTERN.matcher(content);
        while (methodMatcher.find()) {
            context.addMethodCall(methodMatcher.group(1));
        }
    }

    /**
     * Rastrea variables a través del código
     */
    private void trackVariables(String content, CodeContext context) {
        // Encontrar declaraciones de variables
        Matcher varDeclMatcher = VARIABLE_DECLARATION_PATTERN.matcher(content);
        while (varDeclMatcher.find()) {
            String varName = varDeclMatcher.group(1);
            String varType = varDeclMatcher.group(2);
            int lineNumber = getLineNumber(content, varDeclMatcher.start());
            
            VariableInfo varInfo = new VariableInfo(varName, varType, lineNumber);
            context.addVariable(varInfo);
        }
        
        // Rastrear uso de variables
        for (VariableInfo var : context.getVariables()) {
            Pattern usagePattern = Pattern.compile("(?i)\\b" + Pattern.quote(var.getName()) + "\\b");
            Matcher usageMatcher = usagePattern.matcher(content);
            
            while (usageMatcher.find()) {
                int lineNumber = getLineNumber(content, usageMatcher.start());
                var.addUsageLine(lineNumber);
            }
        }
    }

    /**
     * Analiza dependencias entre componentes
     */
    private void analyzeDependencies(String content, CodeContext context) {
        // Analizar dependencias internas
        for (String methodCall : context.getMethodCalls()) {
            DependencyInfo dep = new DependencyInfo(methodCall, "METHOD_CALL");
            context.addDependency(dep);
        }
        
        // Analizar dependencias de includes
        for (String include : context.getIncludes()) {
            DependencyInfo dep = new DependencyInfo(include, "INCLUDE");
            context.addDependency(dep);
        }
        
        // Analizar dependencias de tablas de base de datos
        Pattern tableDependency = Pattern.compile("(?i)(?:select|insert|update|delete).*?from\\s+([\\w-]+)", Pattern.MULTILINE);
        Matcher tableMatcher = tableDependency.matcher(content);
        while (tableMatcher.find()) {
            DependencyInfo dep = new DependencyInfo(tableMatcher.group(1), "DATABASE_TABLE");
            context.addDependency(dep);
        }
    }

    /**
     * Obtiene el contexto completo para ChatGPT incluyendo dependencias
     */
    public String getEnhancedContextForAI(String currentFileName) {
        StringBuilder contextBuilder = new StringBuilder();
        
        // Contexto del archivo actual
        CodeContext currentContext = codeContextCache.get(currentFileName);
        if (currentContext != null) {
            contextBuilder.append("=== ANÁLISIS DEL ARCHIVO ACTUAL: ").append(currentFileName).append(" ===\n");
            contextBuilder.append("Tipo de programa: ").append(currentContext.getProgramType()).append("\n");
            contextBuilder.append("Clases encontradas: ").append(String.join(", ", currentContext.getClassNames())).append("\n");
            contextBuilder.append("Variables declaradas: ").append(currentContext.getVariables().size()).append("\n");
            contextBuilder.append("Dependencias: ").append(currentContext.getDependencies().size()).append("\n\n");
            
            // Detalles de variables
            contextBuilder.append("=== VARIABLES RASTREADAS ===\n");
            for (VariableInfo var : currentContext.getVariables()) {
                contextBuilder.append("- ").append(var.getName())
                    .append(" (Tipo: ").append(var.getType())
                    .append(", Declarada línea: ").append(var.getDeclarationLine())
                    .append(", Usada en líneas: ").append(var.getUsageLines())
                    .append(")\n");
            }
            contextBuilder.append("\n");
            
            // Dependencias
            contextBuilder.append("=== ANÁLISIS DE DEPENDENCIAS ===\n");
            for (DependencyInfo dep : currentContext.getDependencies()) {
                contextBuilder.append("- ").append(dep.getName())
                    .append(" (Tipo: ").append(dep.getType()).append(")\n");
            }
            contextBuilder.append("\n");
        }
        
        // Contexto del proyecto completo
        Map<String, CodeContext> projectContext = analyzeCompleteProject();
        contextBuilder.append("=== CONTEXTO COMPLETO DEL PROYECTO ===\n");
        contextBuilder.append("Archivos analizados: ").append(projectContext.size()).append("\n");
        
        return contextBuilder.toString();
    }

    // Métodos auxiliares
    private void analyzeProjectRecursive(IContainer container, Map<String, CodeContext> projectContext) {
        try {
            IResource[] members = container.members();
            for (IResource member : members) {
                if (member instanceof IFile) {
                    IFile file = (IFile) member;
                    if (isABAPFile(file.getName())) {
                        String content = readFileContent(file);
                        CodeContext context = performDeepAnalysis(file.getName(), content);
                        projectContext.put(file.getName(), context);
                    }
                } else if (member instanceof IContainer) {
                    analyzeProjectRecursive((IContainer) member, projectContext);
                }
            }
        } catch (CoreException e) {
            System.err.println("Error analyzing project recursive: " + e.getMessage());
        }
    }

    private String detectProgramType(String content) {
        if (content.matches("(?i).*report\\s+\\w+.*")) return "REPORT";
        if (content.matches("(?i).*class\\s+\\w+\\s+definition.*")) return "CLASS";
        if (content.matches("(?i).*function\\s+\\w+.*")) return "FUNCTION";
        if (content.matches("(?i).*interface\\s+\\w+.*")) return "INTERFACE";
        return "PROGRAM";
    }

    private int getLineNumber(String content, int position) {
        return content.substring(0, position).split("\n").length;
    }

    private boolean isABAPFile(String fileName) {
        String[] abapExtensions = {".abap", ".txt", ".inc"};
        for (String ext : abapExtensions) {
            if (fileName.toLowerCase().endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    private String readFileContent(IFile file) {
        try (Scanner scanner = new Scanner(file.getContents())) {
            return scanner.useDelimiter("\\A").next();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Limpia la cache de contexto
     */
    public void clearCache() {
        codeContextCache.clear();
    }
}
