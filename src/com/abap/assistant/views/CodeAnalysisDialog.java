package com.abap.assistant.views;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import com.abap.assistant.models.CodeContext;
import com.abap.assistant.models.VariableInfo;
import com.abap.assistant.models.DependencyInfo;

/**
 * Diálogo para mostrar resultados del análisis profundo de código
 */
public class CodeAnalysisDialog extends Dialog {
    private String title;
    private String analysisReport;
    private CodeContext codeContext;
    private Text reportText;
    private TabFolder tabFolder;

    public CodeAnalysisDialog(Shell parentShell, String title, String analysisReport, CodeContext codeContext) {
        super(parentShell);
        this.title = title;
        this.analysisReport = analysisReport;
        this.codeContext = codeContext;
        setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(title);
        shell.setSize(800, 600);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new GridLayout(1, false));

        // Crear TabFolder para organizar la información
        tabFolder = new TabFolder(container, SWT.NONE);
        tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // Tab 1: Reporte general
        createReportTab();
        
        // Tab 2: Análisis de variables
        createVariablesTab();
        
        // Tab 3: Análisis de dependencias  
        createDependenciesTab();
        
        // Tab 4: Recomendaciones
        createRecommendationsTab();

        return container;
    }

    private void createReportTab() {
        TabItem reportTab = new TabItem(tabFolder, SWT.NONE);
        reportTab.setText("📊 Reporte General");

        Composite reportComposite = new Composite(tabFolder, SWT.NONE);
        reportComposite.setLayout(new GridLayout(1, false));

        reportText = new Text(reportComposite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.READ_ONLY);
        reportText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        reportText.setText(analysisReport);
        reportText.setFont(new org.eclipse.swt.graphics.Font(Display.getCurrent(), "Consolas", 10, SWT.NORMAL));

        reportTab.setControl(reportComposite);
    }

    private void createVariablesTab() {
        TabItem variablesTab = new TabItem(tabFolder, SWT.NONE);
        variablesTab.setText("🔢 Variables (" + codeContext.getVariables().size() + ")");

        Composite variablesComposite = new Composite(tabFolder, SWT.NONE);
        variablesComposite.setLayout(new GridLayout(1, false));

        // Tabla de variables
        Table variablesTable = new Table(variablesComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
        variablesTable.setHeaderVisible(true);
        variablesTable.setLinesVisible(true);
        variablesTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // Columnas
        TableColumn nameCol = new TableColumn(variablesTable, SWT.LEFT);
        nameCol.setText("Nombre");
        nameCol.setWidth(150);

        TableColumn typeCol = new TableColumn(variablesTable, SWT.LEFT);
        typeCol.setText("Tipo");
        typeCol.setWidth(120);

        TableColumn declCol = new TableColumn(variablesTable, SWT.CENTER);
        declCol.setText("Declaración");
        declCol.setWidth(80);

        TableColumn usageCol = new TableColumn(variablesTable, SWT.CENTER);
        usageCol.setText("Usos");
        usageCol.setWidth(60);

        TableColumn patternCol = new TableColumn(variablesTable, SWT.LEFT);
        patternCol.setText("Patrón");
        patternCol.setWidth(120);

        TableColumn rangeCol = new TableColumn(variablesTable, SWT.LEFT);
        rangeCol.setText("Rango de Uso");
        rangeCol.setWidth(150);

        // Llenar datos
        for (VariableInfo var : codeContext.getVariables()) {
            TableItem item = new TableItem(variablesTable, SWT.NONE);
            item.setText(0, var.getName());
            item.setText(1, var.getType());
            item.setText(2, String.valueOf(var.getDeclarationLine()));
            item.setText(3, String.valueOf(var.getUsageLines().size()));
            item.setText(4, var.analyzeUsagePattern());
            item.setText(5, var.getUsageRange());
        }

        variablesTab.setControl(variablesComposite);
    }

    private void createDependenciesTab() {
        TabItem depsTab = new TabItem(tabFolder, SWT.NONE);
        depsTab.setText("🔗 Dependencias (" + codeContext.getDependencies().size() + ")");

        Composite depsComposite = new Composite(tabFolder, SWT.NONE);
        depsComposite.setLayout(new GridLayout(1, false));

        // Tabla de dependencias
        Table depsTable = new Table(depsComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
        depsTable.setHeaderVisible(true);
        depsTable.setLinesVisible(true);
        depsTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // Columnas
        TableColumn nameCol = new TableColumn(depsTable, SWT.LEFT);
        nameCol.setText("Nombre");
        nameCol.setWidth(200);

        TableColumn typeCol = new TableColumn(depsTable, SWT.LEFT);
        typeCol.setText("Tipo");
        typeCol.setWidth(120);

        TableColumn categoryCol = new TableColumn(depsTable, SWT.LEFT);
        categoryCol.setText("Categoría");
        categoryCol.setWidth(100);

        TableColumn usageCol = new TableColumn(depsTable, SWT.CENTER);
        usageCol.setText("Usos");
        usageCol.setWidth(60);

        TableColumn criticalCol = new TableColumn(depsTable, SWT.CENTER);
        criticalCol.setText("Criticidad");
        criticalCol.setWidth(80);

        TableColumn externalCol = new TableColumn(depsTable, SWT.CENTER);
        externalCol.setText("Externa");
        externalCol.setWidth(60);

        // Llenar datos
        for (DependencyInfo dep : codeContext.getDependencies()) {
            TableItem item = new TableItem(depsTable, SWT.NONE);
            item.setText(0, dep.getName());
            item.setText(1, dep.getType());
            item.setText(2, dep.getCategorizedType());
            item.setText(3, String.valueOf(dep.getUsageCount()));
            item.setText(4, dep.getCriticality());
            item.setText(5, dep.isExternal() ? "Sí" : "No");
            
            // Colorear filas críticas
            if (dep.isCritical()) {
                item.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW));
            }
        }

        depsTab.setControl(depsComposite);
    }

    private void createRecommendationsTab() {
        TabItem recsTab = new TabItem(tabFolder, SWT.NONE);
        recsTab.setText("💡 Recomendaciones");

        Composite recsComposite = new Composite(tabFolder, SWT.NONE);
        recsComposite.setLayout(new GridLayout(1, false));

        Text recsText = new Text(recsComposite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.WRAP | SWT.READ_ONLY);
        recsText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        StringBuilder recommendations = new StringBuilder();
        recommendations.append("💡 RECOMENDACIONES BASADAS EN EL ANÁLISIS\n\n");
        
        // Recomendaciones por variables no utilizadas
        long unusedVars = codeContext.getVariables().stream()
            .filter(v -> v.getUsageLines().isEmpty())
            .count();
        if (unusedVars > 0) {
            recommendations.append("🔢 VARIABLES:\n");
            recommendations.append("• Se encontraron ").append(unusedVars)
                .append(" variables no utilizadas - considere eliminarlas\n");
        }

        // Recomendaciones por variables frecuentemente utilizadas
        codeContext.getVariables().stream()
            .filter(VariableInfo::isFrequentlyUsed)
            .forEach(var -> {
                recommendations.append("• Variable '").append(var.getName())
                    .append("' usada ").append(var.getUsageLines().size())
                    .append(" veces - considere optimización\n");
            });

        recommendations.append("\n🔗 DEPENDENCIAS:\n");
        
        // Recomendaciones por dependencias
        for (DependencyInfo dep : codeContext.getDependencies()) {
            for (String rec : dep.generateRecommendations()) {
                recommendations.append("• ").append(dep.getName()).append(": ").append(rec).append("\n");
            }
        }

        // Recomendaciones generales
        recommendations.append("\n✨ GENERALES:\n");
        recommendations.append("• Considere documentar las dependencias críticas\n");
        recommendations.append("• Revise el acoplamiento entre módulos\n");
        recommendations.append("• Evalúe la necesidad de refactoring en secciones complejas\n");

        recsText.setText(recommendations.toString());
        recsTab.setControl(recsComposite);
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, "Cerrar", true);
        createButton(parent, 999, "Exportar", false);
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (buttonId == 999) {
            exportResults();
        } else {
            super.buttonPressed(buttonId);
        }
    }

    private void exportResults() {
        FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
        dialog.setFilterExtensions(new String[]{"*.txt", "*.*"});
        dialog.setFilterNames(new String[]{"Archivos de Texto", "Todos los archivos"});
        dialog.setFileName("analisis_codigo_" + codeContext.getFileName() + ".txt");

        String path = dialog.open();
        if (path != null) {
            try (java.io.FileWriter writer = new java.io.FileWriter(path)) {
                writer.write(analysisReport);
                org.eclipse.jface.dialogs.MessageDialog.openInformation(getShell(),
                    "Exportar", "Análisis exportado exitosamente a: " + path);
            } catch (Exception e) {
                org.eclipse.jface.dialogs.MessageDialog.openError(getShell(),
                    "Error", "Error al exportar: " + e.getMessage());
            }
        }
    }
}
