package com.abap.assistant.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;

import com.abap.assistant.models.CodeContext;
import com.abap.assistant.models.VariableInfo;
import com.abap.assistant.models.DependencyInfo;
import com.abap.assistant.models.ChatMessage;
import com.abap.assistant.services.CodeAnalysisService;
import com.abap.assistant.services.ChatGPTService;
import com.abap.assistant.utils.EditorUtils;
import com.abap.assistant.views.CodeAnalysisDialog;
import com.abap.assistant.views.AIAnalysisDialog;

import java.util.Map;

/**
 * Handler para análisis profundo de código ABAP
 * Proporciona análisis completo con tracking de variables y dependencias
 */
public class DeepCodeAnalysisHandler extends AbstractHandler {
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        try {
            // Obtener el editor activo
            IEditorPart activeEditor = HandlerUtil.getActiveEditor(event);
            if (!(activeEditor instanceof ITextEditor)) {
                MessageDialog.openWarning(Display.getDefault().getActiveShell(),
                    "⚠️ ABAP Assistant", "Por favor, abra un archivo de código ABAP");
                return null;
            }

            ITextEditor textEditor = (ITextEditor) activeEditor;
            IDocument document = textEditor.getDocumentProvider()
                .getDocument(textEditor.getEditorInput());
            String content = document.get();
            String fileName = textEditor.getEditorInput().getName();

            // Realizar análisis profundo
            CodeAnalysisService analysisService = CodeAnalysisService.getInstance();
            CodeContext context = analysisService.performDeepAnalysis(fileName, content);

            // Obtener contexto completo del proyecto
            Map<String, CodeContext> projectContext = analysisService.analyzeCompleteProject();

            // Generar reporte de análisis
            String analysisReport = generateAnalysisReport(context, projectContext);

            // Mostrar diálogo con resultados
            showAnalysisDialog(analysisReport, context);

            // Opcional: Enviar contexto a ChatGPT para análisis adicional
            if (MessageDialog.openQuestion(Display.getDefault().getActiveShell(),
                "🤖 Análisis con IA", "¿Desea obtener análisis adicional con ChatGPT?")) {
                
                performAIAnalysis(context, analysisService);
            }

        } catch (Exception e) {
            MessageDialog.openError(Display.getDefault().getActiveShell(),
                "❌ Error", "Error en análisis de código: " + e.getMessage());
        }

        return null;
    }

    /**
     * Genera un reporte detallado del análisis
     */
    private String generateAnalysisReport(CodeContext context, Map<String, CodeContext> projectContext) {
        StringBuilder report = new StringBuilder();
        
        // Encabezado
        report.append("🔍 === ANÁLISIS PROFUNDO DE CÓDIGO ABAP ===\n\n");
        
        // Información básica del archivo
        report.append("📄 ARCHIVO: ").append(context.getFileName()).append("\n");
        report.append("📝 TIPO: ").append(context.getProgramType()).append("\n");
        report.append("🏗️ CLASES: ").append(context.getClassNames().size()).append("\n");
        report.append("📚 INCLUDES: ").append(context.getIncludes().size()).append("\n\n");

        // Análisis de variables
        report.append("🔢 === ANÁLISIS DE VARIABLES (").append(context.getVariables().size()).append(") ===\n");
        for (VariableInfo var : context.getVariables()) {
            report.append("• ").append(var.generateSummary()).append("\n");
        }
        report.append("\n");

        // Variables no utilizadas
        long unusedVars = context.getVariables().stream()
            .filter(v -> v.getUsageLines().isEmpty())
            .count();
        if (unusedVars > 0) {
            report.append("⚠️ VARIABLES NO UTILIZADAS: ").append(unusedVars).append("\n\n");
        }

        // Análisis de dependencias
        report.append("🔗 === ANÁLISIS DE DEPENDENCIAS (").append(context.getDependencies().size()).append(") ===\n");
        for (DependencyInfo dep : context.getDependencies()) {
            report.append("• ").append(dep.generateSummary()).append("\n");
            
            // Mostrar recomendaciones si las hay
            for (String rec : dep.generateRecommendations()) {
                report.append("  💡 ").append(rec).append("\n");
            }
        }
        report.append("\n");

        // Dependencias críticas
        long criticalDeps = context.getDependencies().stream()
            .filter(DependencyInfo::isCritical)
            .count();
        if (criticalDeps > 0) {
            report.append("🚨 DEPENDENCIAS CRÍTICAS: ").append(criticalDeps).append("\n\n");
        }

        // Contexto del proyecto
        report.append("🌍 === CONTEXTO DEL PROYECTO ===\n");
        report.append("📁 Archivos analizados: ").append(projectContext.size()).append("\n");
        
        // Estadísticas del proyecto
        int totalVariables = projectContext.values().stream()
            .mapToInt(ctx -> ctx.getVariables().size())
            .sum();
        int totalDependencies = projectContext.values().stream()
            .mapToInt(ctx -> ctx.getDependencies().size())
            .sum();
            
        report.append("🔢 Variables totales: ").append(totalVariables).append("\n");
        report.append("🔗 Dependencias totales: ").append(totalDependencies).append("\n");

        return report.toString();
    }

    /**
     * Muestra el diálogo con los resultados del análisis
     */
    private void showAnalysisDialog(String report, CodeContext context) {
        Display.getDefault().asyncExec(() -> {
            CodeAnalysisDialog dialog = new CodeAnalysisDialog(
                Display.getDefault().getActiveShell(), 
                "🔍 Análisis Profundo de Código", 
                report,
                context
            );
            dialog.open();
        });
    }

    /**
     * Realiza análisis adicional con IA
     */
    private void performAIAnalysis(CodeContext context, CodeAnalysisService analysisService) {
        try {
            // Preparar contexto para ChatGPT
            String enhancedContext = analysisService.getEnhancedContextForAI(context.getFileName());
            
            // Prompt especializado para análisis de código
            String prompt = "Analiza este código ABAP en profundidad:\n\n" +
                enhancedContext +
                "\nPor favor proporciona:\n" +
                "1. Análisis de calidad del código\n" +
                "2. Posibles optimizaciones\n" +
                "3. Patrones de diseño identificados\n" +
                "4. Recomendaciones de mejora\n" +
                "5. Posibles problemas de rendimiento";

            ChatGPTService chatService = ChatGPTService.getInstance();
            
            // Mostrar diálogo de progreso
            Display.getDefault().asyncExec(() -> {
                MessageDialog.openInformation(Display.getDefault().getActiveShell(),
                    "🤖 IA Analizando", "ChatGPT está analizando el código...");
            });

            // Realizar análisis con IA (en hilo separado para no bloquear UI)
            new Thread(() -> {
                try {
                    ChatMessage response = chatService.sendMessage(prompt);
                    String aiAnalysis = response.getContent();
                    
                    Display.getDefault().asyncExec(() -> {
                        AIAnalysisDialog aiDialog = new AIAnalysisDialog(
                            Display.getDefault().getActiveShell(),
                            "🤖 Análisis con Inteligencia Artificial",
                            aiAnalysis
                        );
                        aiDialog.open();
                    });
                    
                } catch (Exception e) {
                    Display.getDefault().asyncExec(() -> {
                        MessageDialog.openError(Display.getDefault().getActiveShell(),
                            "❌ Error IA", "Error en análisis con IA: " + e.getMessage());
                    });
                }
            }).start();

        } catch (Exception e) {
            MessageDialog.openError(Display.getDefault().getActiveShell(),
                "❌ Error", "Error preparando análisis IA: " + e.getMessage());
        }
    }
}
