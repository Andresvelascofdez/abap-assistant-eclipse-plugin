package com.abap.assistant.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.ui.part.ViewPart;

import com.abap.assistant.services.ChatGPTService;
import com.abap.assistant.services.DocumentProcessorService;
import com.abap.assistant.services.ContextCaptureService;
import com.abap.assistant.services.DocumentContextManager;
import com.abap.assistant.models.ChatMessage;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.*;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import java.util.List;
import java.util.ArrayList;

/**
 * Main view for ABAP Assistant - provides chat interface and quick actions
 */
public class ABAPAssistantView extends ViewPart {
    
    public static final String ID = "com.abap.assistant.views.ABAPAssistantView";
    
    // Icon paths
    private static final String ICON_EXPLAIN = "icons/explain.png";
    private static final String ICON_OPTIMIZE = "icons/optimize.png";
    private static final String ICON_ERROR = "icons/error.png";
    private static final String ICON_ANALYSIS = "icons/analysis.png";
    private static final String ICON_ATTACH = "icons/attach.png";
    private static final String ICON_SEND = "icons/send.png";
    
    private StyledText chatDisplay;
    private Text inputText;
    private Button sendButton;
    private Button attachFileButton;
    private Button quickExplainButton;
    private Button quickOptimizeButton;
    private Button quickErrorCheckButton;
    private Button deepAnalysisButton;
    private Label statusLabel;
    private List<String> attachedFiles;
    
    private ChatGPTService chatService;
    private DocumentProcessorService docProcessor;
    private ContextCaptureService contextCapture;
    private DocumentContextManager contextManager;

    @Override
    public void createPartControl(Composite parent) {
        initializeServices();
        createUI(parent);
        setupDragAndDrop();
        attachedFiles = new ArrayList<>();
    }
    
    private void initializeServices() {
        chatService = new ChatGPTService();
        docProcessor = new DocumentProcessorService();
        contextCapture = new ContextCaptureService();
        contextManager = new DocumentContextManager();
    }
    
    /**
     * Load icon from Eclipse platform or system resources
     */
    private Image loadIcon(String iconName) {
        try {
            // Use Eclipse platform workbench icons instead of custom icons
            switch(iconName) {
                case "icons/explain.png":
                    return PlatformUI.getWorkbench().getSharedImages()
                        .getImage(org.eclipse.ui.ISharedImages.IMG_OBJ_ELEMENT);
                case "icons/optimize.png":
                    return PlatformUI.getWorkbench().getSharedImages()
                        .getImage(org.eclipse.ui.ISharedImages.IMG_TOOL_FORWARD);
                case "icons/error.png":
                    return PlatformUI.getWorkbench().getSharedImages()
                        .getImage(org.eclipse.ui.ISharedImages.IMG_OBJS_ERROR_TSK);
                case "icons/analysis.png":
                    return PlatformUI.getWorkbench().getSharedImages()
                        .getImage(org.eclipse.ui.ISharedImages.IMG_OBJ_FOLDER);
                case "icons/attach.png":
                    return PlatformUI.getWorkbench().getSharedImages()
                        .getImage(org.eclipse.ui.ISharedImages.IMG_OBJ_FILE);
                case "icons/send.png":
                    return PlatformUI.getWorkbench().getSharedImages()
                        .getImage(org.eclipse.ui.ISharedImages.IMG_TOOL_FORWARD);
                default:
                    return null;
            }
        } catch (Exception e) {
            // If icon loading fails, return null and button will show text only
            return null;
        }
    }
    
    private void createUI(Composite parent) {
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = 10;
        layout.marginHeight = 10;
        layout.verticalSpacing = 8;
        parent.setLayout(layout);
        
        // Header with title
        createHeaderPanel(parent);
        
        // Chat display area with modern styling
        chatDisplay = new StyledText(parent, SWT.BORDER | SWT.V_SCROLL | SWT.WRAP | SWT.READ_ONLY);
        GridData chatData = new GridData(SWT.FILL, SWT.FILL, true, true);
        chatData.heightHint = 300;
        chatDisplay.setLayoutData(chatData);
        
        // Set modern colors (similar to VS Code dark theme)
        chatDisplay.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        chatDisplay.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_BLACK));
        
        // Welcome message with better formatting and icons
        String welcomeMessage = "=== ABAP Assistant Ready ===\n\n" +
                               "Ask me anything about ABAP development\n" +
                               "Use Quick Actions for instant help\n" +
                               "Attach documents for analysis\n" +
                               "Select code and get suggestions\n\n" +
                               "Ready to assist with your SAP ABAP development!\n" +
                               "Configure your OpenAI API key in the ABAP Assistant menu.\n\n";
        
        chatDisplay.setText(welcomeMessage);
        
        // Quick Actions Panel
        createQuickActionsPanel(parent);
        
        // File attachment panel
        createFileAttachmentPanel(parent);
        
        // Input area
        createInputArea(parent);
        
        // Status bar with better styling
        createStatusBar(parent);
    }
    
    private void createHeaderPanel(Composite parent) {
        Composite headerComp = new Composite(parent, SWT.NONE);
        GridLayout headerLayout = new GridLayout(2, false);
        headerLayout.marginWidth = 0;
        headerLayout.marginHeight = 5;
        headerComp.setLayout(headerLayout);
        headerComp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        Label titleLabel = new Label(headerComp, SWT.NONE);
        titleLabel.setText("ABAP Programming Assistant");
        GridData titleData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        titleLabel.setLayoutData(titleData);
        
        // Set title font to bold
        org.eclipse.swt.graphics.Font boldFont = new org.eclipse.swt.graphics.Font(
            parent.getDisplay(), "Arial", 12, SWT.BOLD);
        titleLabel.setFont(boldFont);
        
        Label versionLabel = new Label(headerComp, SWT.NONE);
        versionLabel.setText("v1.0");
        versionLabel.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
    }
    
    private void createStatusBar(Composite parent) {
        Composite statusComp = new Composite(parent, SWT.NONE);
        GridLayout statusLayout = new GridLayout(1, false);
        statusLayout.marginWidth = 5;
        statusLayout.marginHeight = 5;
        statusComp.setLayout(statusLayout);
        statusComp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        statusLabel = new Label(statusComp, SWT.NONE);
        statusLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        statusLabel.setText("Status: Ready - Configure API key in ABAP Assistant menu");
        statusLabel.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
    }
    
    private void createQuickActionsPanel(Composite parent) {
        Composite quickActionsComp = new Composite(parent, SWT.NONE);
        GridLayout quickLayout = new GridLayout(4, true);
        quickLayout.marginWidth = 5;
        quickLayout.horizontalSpacing = 10;
        quickActionsComp.setLayout(quickLayout);
        quickActionsComp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        // Quick Explain Button
        quickExplainButton = new Button(quickActionsComp, SWT.PUSH);
        Image explainIcon = loadIcon(ICON_EXPLAIN);
        if (explainIcon != null) {
            quickExplainButton.setImage(explainIcon);
        }
        quickExplainButton.setText("Quick Explain");
        quickExplainButton.setToolTipText("Explain selected ABAP code or current file");
        quickExplainButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        quickExplainButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                performQuickExplain();
            }
        });
        
        // Quick Optimize Button  
        quickOptimizeButton = new Button(quickActionsComp, SWT.PUSH);
        Image optimizeIcon = loadIcon(ICON_OPTIMIZE);
        if (optimizeIcon != null) {
            quickOptimizeButton.setImage(optimizeIcon);
        }
        quickOptimizeButton.setText("Quick Optimize");
        quickOptimizeButton.setToolTipText("Optimize selected ABAP code with diff preview");
        quickOptimizeButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        quickOptimizeButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                performQuickOptimize();
            }
        });
        
        // Quick Error Check Button
        quickErrorCheckButton = new Button(quickActionsComp, SWT.PUSH);
        Image errorIcon = loadIcon(ICON_ERROR);
        if (errorIcon != null) {
            quickErrorCheckButton.setImage(errorIcon);
        }
        quickErrorCheckButton.setText("Error Check");
        quickErrorCheckButton.setToolTipText("Check for errors and issues in ABAP code");
        quickErrorCheckButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        quickErrorCheckButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                performQuickErrorCheck();
            }
        });
        
        // Deep Analysis Button (NEW)
        deepAnalysisButton = new Button(quickActionsComp, SWT.PUSH);
        Image analysisIcon = loadIcon(ICON_ANALYSIS);
        if (analysisIcon != null) {
            deepAnalysisButton.setImage(analysisIcon);
        }
        deepAnalysisButton.setText("Deep Analysis");
        deepAnalysisButton.setToolTipText("Comprehensive code analysis with variable tracking and dependency mapping");
        deepAnalysisButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        deepAnalysisButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                performDeepAnalysis();
            }
        });
    }
    
    private void createFileAttachmentPanel(Composite parent) {
        Composite fileComp = new Composite(parent, SWT.NONE);
        GridLayout fileLayout = new GridLayout(3, false);
        fileLayout.marginWidth = 5;
        fileLayout.horizontalSpacing = 10;
        fileComp.setLayout(fileLayout);
        fileComp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        attachFileButton = new Button(fileComp, SWT.PUSH);
        Image attachIcon = loadIcon(ICON_ATTACH);
        if (attachIcon != null) {
            attachFileButton.setImage(attachIcon);
        }
        attachFileButton.setText("Attach Document");
        attachFileButton.setToolTipText("Attach PDF, DOC, or TXT files for analysis");
        attachFileButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                attachDocument();
            }
        });
        
        Label dropLabel = new Label(fileComp, SWT.NONE);
        dropLabel.setText("Drag & drop files here (PDF, DOC, TXT)");
        dropLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        dropLabel.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
        
        // Add a separator line
        Label separator = new Label(fileComp, SWT.SEPARATOR | SWT.HORIZONTAL);
        separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
    }
    
    private void createInputArea(Composite parent) {
        Composite inputComp = new Composite(parent, SWT.NONE);
        inputComp.setLayout(new GridLayout(2, false));
        inputComp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        inputText = new Text(inputComp, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        GridData inputData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        inputData.heightHint = 60;
        inputText.setLayoutData(inputData);
        inputText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == SWT.CR && (e.stateMask & SWT.CTRL) != 0) {
                    sendMessage();
                }
            }
        });
        
        sendButton = new Button(inputComp, SWT.PUSH);
        Image sendIcon = loadIcon(ICON_SEND);
        if (sendIcon != null) {
            sendButton.setImage(sendIcon);
        }
        sendButton.setText("Send (Ctrl+Enter)");
        sendButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
        sendButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                sendMessage();
            }
        });
    }
    
    private void setupDragAndDrop() {
        DropTarget dropTarget = new DropTarget(chatDisplay, DND.DROP_COPY | DND.DROP_DEFAULT);
        dropTarget.setTransfer(new Transfer[] { FileTransfer.getInstance() });
        dropTarget.addDropListener(new DropTargetAdapter() {
            @Override
            public void drop(DropTargetEvent event) {
                if (FileTransfer.getInstance().isSupportedType(event.currentDataType)) {
                    String[] files = (String[]) event.data;
                    for (String file : files) {
                        processDroppedFile(file);
                    }
                }
            }
        });
    }
    
    private void performQuickExplain() {
        String selectedCode = getSelectedCodeFromEditor();
        if (selectedCode == null || selectedCode.trim().isEmpty()) {
            updateStatus("Warning: Please select ABAP code to explain");
            return;
        }
        
        String context = contextCapture.captureCurrentContext();
        String prompt = "Please explain this ABAP code with context:\n\nContext: " + context + "\n\nCode:\n" + selectedCode;
        
        sendAIRequest("🚀 Quick Explain", prompt);
    }
    
    private void performQuickOptimize() {
        String selectedCode = getSelectedCodeFromEditor();
        if (selectedCode == null || selectedCode.trim().isEmpty()) {
            updateStatus("Warning: Please select ABAP code to optimize");
            return;
        }
        
        String context = contextCapture.captureCurrentContext();
        String prompt = "Please analyze and provide optimization recommendations for this ABAP code:\n\nContext: " + context + "\n\nCode:\n" + selectedCode;
        
        sendAIRequest("⚡ Quick Optimize", prompt);
    }
    
    private void performQuickErrorCheck() {
        String selectedCode = getSelectedCodeFromEditor();
        if (selectedCode == null || selectedCode.trim().isEmpty()) {
            updateStatus("Warning: Please select ABAP code to check for errors");
            return;
        }
        
        String context = contextCapture.captureCurrentContext();
        String prompt = "Please check this ABAP code for potential errors, issues, and provide solutions:\n\nContext: " + context + "\n\nCode:\n" + selectedCode;
        
        sendAIRequest("🔍 Quick Error Check", prompt);
    }
    
    private void sendMessage() {
        String message = inputText.getText().trim();
        if (message.isEmpty()) return;
        
        appendToChat("You: " + message + "\n\n");
        inputText.setText("");
        
        // Use document context integration for better responses
        sendAIRequestWithContext("Chat", message);
    }
    
    private void sendAIRequestWithContext(String action, String prompt) {
        updateStatus("Sending request to ChatGPT with document context...");
        
        Job job = new Job("AI Request: " + action) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    String selectedCode = getSelectedCodeFromEditor();
                    ChatMessage response;
                    
                    // Use document context if available, otherwise use standard message
                    if (contextManager.hasContext()) {
                        response = chatService.sendMessageWithDocumentContext(prompt, selectedCode, contextManager);
                        
                        PlatformUI.getWorkbench().getDisplay().asyncExec(() -> {
                            appendToChat("🤖 AI Assistant (" + action + "):\n" + response.getContent() + "\n\n");
                            updateStatus("Response with document context (" + contextManager.getAvailableDocuments().size() + " docs)");
                        });
                    } else {
                        response = chatService.sendMessage(prompt);
                        
                        PlatformUI.getWorkbench().getDisplay().asyncExec(() -> {
                            appendToChat("🤖 AI Assistant (" + action + "):\n" + response.getContent() + "\n\n");
                            updateStatus("Response received");
                        });
                    }
                    
                } catch (Exception e) {
                    PlatformUI.getWorkbench().getDisplay().asyncExec(() -> {
                        appendToChat("❌ Error: " + e.getMessage() + "\n\n");
                        updateStatus("Error occurred");
                    });
                }
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }
    
    // Legacy method for Quick Actions - can be updated later to use context
    private void sendAIRequest(String action, String prompt) {
        sendAIRequestWithContext(action, prompt);
    }
    
    private void attachDocument() {
        FileDialog dialog = new FileDialog(getSite().getShell(), SWT.OPEN | SWT.MULTI);
        dialog.setFilterExtensions(new String[]{"*.pdf", "*.doc", "*.docx", "*.txt", "*.abap", "*.*"});
        dialog.setFilterNames(new String[]{"PDF Files", "Word Documents", "Word Documents", "Text Files", "ABAP Files", "All Files"});
        
        if (dialog.open() != null) {
            String[] fileNames = dialog.getFileNames();
            String filterPath = dialog.getFilterPath();
            
            for (String fileName : fileNames) {
                String fullPath = filterPath + System.getProperty("file.separator") + fileName;
                processDroppedFile(fullPath);
            }
        }
    }
    
    private void processDroppedFile(String filePath) {
        Job job = new Job("Processing document: " + filePath) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    String content = docProcessor.processDocument(filePath);
                    attachedFiles.add(filePath);
                    
                    // Add to context manager for AI integration
                    contextManager.addDocument(filePath, content);
                    
                    PlatformUI.getWorkbench().getDisplay().asyncExec(() -> {
                        String contextSummary = contextManager.getContextSummary();
                        appendToChat("📎 Attached: " + filePath + "\nContent preview: " + content.substring(0, Math.min(200, content.length())) + "...\n");
                        appendToChat("📊 " + contextSummary + "\n\n");
                        updateStatus("📄 Document processed and added to context: " + filePath);
                    });
                    
                } catch (Exception e) {
                    PlatformUI.getWorkbench().getDisplay().asyncExec(() -> {
                        updateStatus("❌ Error processing file: " + e.getMessage());
                    });
                }
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }
    
    private String getSelectedCodeFromEditor() {
        try {
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            IEditorPart editor = page.getActiveEditor();
            
            if (editor instanceof ITextEditor) {
                ITextEditor textEditor = (ITextEditor) editor;
                ITextSelection selection = (ITextSelection) textEditor.getSelectionProvider().getSelection();
                
                if (selection.getLength() > 0) {
                    return selection.getText();
                } else {
                    // If no selection, get entire document
                    IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
                    return document.get();
                }
            }
        } catch (Exception e) {
            // Handle exception
        }
        return null;
    }
    
    private void appendToChat(String text) {
        chatDisplay.append(text);
        chatDisplay.setTopIndex(chatDisplay.getLineCount() - 1);
    }
    
    private void updateStatus(String message) {
        statusLabel.setText(message);
    }
    
    /**
     * Perform deep analysis of current ABAP code
     */
    private void performDeepAnalysis() {
        try {
            // Import del nuevo servicio
            com.abap.assistant.services.CodeAnalysisService analysisService = 
                com.abap.assistant.services.CodeAnalysisService.getInstance();
            
            // Analizar código actual
            com.abap.assistant.models.CodeContext context = analysisService.analyzeCurrentEditorContent();
            
            if (context == null) {
                updateStatus("Warning: Please select an ABAP file for deep analysis");
                appendToChat("🚨 Deep Analysis Error: No ABAP code file is currently open.\n" +
                           "Please open an ABAP file and try again.\n\n");
                return;
            }
            
            updateStatus("Performing deep analysis...");
            appendToChat("🧠 === INICIANDO ANÁLISIS PROFUNDO ===\n");
            appendToChat("📄 Archivo: " + context.getFileName() + "\n");
            appendToChat("📝 Tipo: " + context.getProgramType() + "\n");
            appendToChat("🔍 Analizando estructura, variables y dependencias...\n\n");
            
            // Ejecutar análisis en un Job separado
            Job job = new Job("Deep Code Analysis: " + context.getFileName()) {
                @Override
                protected IStatus run(IProgressMonitor monitor) {
                    monitor.beginTask("Analyzing code structure and dependencies", 4);
                    
                    try {
                        // Obtener contexto completo del proyecto
                        monitor.subTask("Analyzing project context...");
                        java.util.Map<String, com.abap.assistant.models.CodeContext> projectContext = 
                            analysisService.analyzeCompleteProject();
                        monitor.worked(1);
                        
                        // Generar reporte detallado
                        monitor.subTask("Generating analysis report...");
                        StringBuilder report = new StringBuilder();
                        
                        // Información básica
                        report.append("📊 === RESULTADOS DEL ANÁLISIS PROFUNDO ===\n\n");
                        report.append("📄 Archivo: ").append(context.getFileName()).append("\n");
                        report.append("📝 Tipo de programa: ").append(context.getProgramType()).append("\n");
                        report.append("🏗️ Clases encontradas: ").append(context.getClassNames().size()).append("\n");
                        report.append("📚 Includes: ").append(context.getIncludes().size()).append("\n\n");
                        
                        // Variables
                        report.append("🔢 === ANÁLISIS DE VARIABLES (").append(context.getVariables().size()).append(") ===\n");
                        for (com.abap.assistant.models.VariableInfo var : context.getVariables()) {
                            report.append("• ").append(var.getName())
                                .append(" (").append(var.getType()).append(")")
                                .append(" - Línea ").append(var.getDeclarationLine())
                                .append(", Usos: ").append(var.getUsageLines().size())
                                .append(", Patrón: ").append(var.analyzeUsagePattern()).append("\n");
                        }
                        report.append("\n");
                        
                        // Dependencias
                        report.append("🔗 === ANÁLISIS DE DEPENDENCIAS (").append(context.getDependencies().size()).append(") ===\n");
                        for (com.abap.assistant.models.DependencyInfo dep : context.getDependencies()) {
                            report.append("• ").append(dep.getName())
                                .append(" (").append(dep.getType()).append(")")
                                .append(" - Criticidad: ").append(dep.getCriticality());
                            if (dep.isExternal()) {
                                report.append(" [EXTERNA]");
                            }
                            report.append("\n");
                        }
                        report.append("\n");
                        
                        // Contexto del proyecto
                        report.append("🌍 === CONTEXTO DEL PROYECTO ===\n");
                        report.append("📁 Archivos ABAP analizados: ").append(projectContext.size()).append("\n");
                        monitor.worked(1);
                        
                        // Análisis con IA (opcional)
                        monitor.subTask("Preparing AI analysis...");
                        String enhancedContext = analysisService.getEnhancedContextForAI(context.getFileName());
                        monitor.worked(1);
                        
                        // Mostrar resultados en UI
                        getSite().getShell().getDisplay().asyncExec(() -> {
                            try {
                                appendToChat(report.toString());
                                updateStatus("✅ Deep analysis completed successfully");
                                
                                // Mostrar diálogo detallado si las clases están disponibles
                                showDetailedAnalysisDialog(context, projectContext, enhancedContext);
                                
                            } catch (Exception e) {
                                appendToChat("❌ Error displaying results: " + e.getMessage() + "\n\n");
                                updateStatus("❌ Analysis completed with display errors");
                            }
                        });
                        
                        monitor.worked(1);
                        return Status.OK_STATUS;
                        
                    } catch (Exception e) {
                        getSite().getShell().getDisplay().asyncExec(() -> {
                            appendToChat("❌ Deep Analysis Error: " + e.getMessage() + "\n\n");
                            updateStatus("❌ Analysis failed: " + e.getMessage());
                        });
                        return Status.CANCEL_STATUS;
                    } finally {
                        monitor.done();
                    }
                }
            };
            
            job.setUser(true);
            job.schedule();
            
        } catch (Exception e) {
            appendToChat("❌ Deep Analysis Error: " + e.getMessage() + "\n\n");
            updateStatus("❌ Analysis failed: " + e.getMessage());
        }
    }
    
    /**
     * Muestra diálogo detallado de análisis (si las clases UI están disponibles)
     */
    private void showDetailedAnalysisDialog(com.abap.assistant.models.CodeContext context, 
            java.util.Map<String, com.abap.assistant.models.CodeContext> projectContext, 
            String enhancedContext) {
        try {
            // Intentar crear el diálogo avanzado
            appendToChat("💡 Tip: Para ver el análisis detallado en ventanas separadas, " +
                       "instale las dependencias UI de Eclipse completas.\n\n");
            
            // Opción para análisis con IA
            if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(getSite().getShell(),
                "🤖 Análisis con IA", 
                "¿Desea obtener análisis adicional con ChatGPT?\n\n" +
                "Esto enviará el contexto del código a la IA para obtener:\n" +
                "• Análisis de calidad del código\n" +
                "• Sugerencias de optimización\n" +
                "• Patrones de diseño identificados\n" +
                "• Recomendaciones de mejora")) {
                
                performAIAnalysis(context, enhancedContext);
            }
            
        } catch (Exception e) {
            // Si las clases UI no están disponibles, mostrar solo en chat
            appendToChat("💡 Análisis completo mostrado arriba. Para diálogos avanzados, " +
                       "verifique las dependencias de Eclipse UI.\n\n");
        }
    }
    
    /**
     * Realizar análisis con IA
     */
    private void performAIAnalysis(com.abap.assistant.models.CodeContext context, String enhancedContext) {
        appendToChat("🤖 Enviando código a ChatGPT para análisis avanzado...\n\n");
        updateStatus("🤖 ChatGPT analyzing...");
        
        Job aiJob = new Job("AI Code Analysis") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                monitor.beginTask("Getting AI analysis", 1);
                
                try {
                    String prompt = "Analiza este código ABAP en profundidad:\n\n" +
                                  enhancedContext +
                                  "\n\nProporciona:\n" +
                                  "1. Análisis de calidad del código\n" +
                                  "2. Posibles optimizaciones\n" +
                                  "3. Patrones de diseño identificados\n" +
                                  "4. Recomendaciones de mejora\n" +
                                  "5. Posibles problemas de rendimiento";
                    
                    ChatGPTService chatService = ChatGPTService.getInstance();
                    String aiResponse = chatService.sendSimpleMessage(prompt);
                    
                    getSite().getShell().getDisplay().asyncExec(() -> {
                        appendToChat("🤖 === ANÁLISIS CON INTELIGENCIA ARTIFICIAL ===\n\n");
                        appendToChat(aiResponse + "\n\n");
                        appendToChat("✨ === ANÁLISIS COMPLETADO ===\n\n");
                        updateStatus("✅ AI analysis completed successfully");
                    });
                    
                    return Status.OK_STATUS;
                    
                } catch (Exception e) {
                    getSite().getShell().getDisplay().asyncExec(() -> {
                        appendToChat("❌ AI Analysis Error: " + e.getMessage() + "\n\n");
                        updateStatus("❌ AI analysis failed");
                    });
                    return Status.CANCEL_STATUS;
                } finally {
                    monitor.done();
                }
            }
        };
        
        aiJob.setUser(true);
        aiJob.schedule();
    }

    @Override
    public void setFocus() {
        inputText.setFocus();
    }
}
