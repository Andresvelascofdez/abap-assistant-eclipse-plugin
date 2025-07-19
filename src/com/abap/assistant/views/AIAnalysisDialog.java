package com.abap.assistant.views;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;

/**
 * Diálogo para mostrar análisis con IA
 */
public class AIAnalysisDialog extends Dialog {
    private String title;
    private String aiAnalysis;

    public AIAnalysisDialog(Shell parentShell, String title, String aiAnalysis) {
        super(parentShell);
        this.title = title;
        this.aiAnalysis = aiAnalysis;
        setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(title);
        shell.setSize(700, 500);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new GridLayout(1, false));

        Label headerLabel = new Label(container, SWT.NONE);
        headerLabel.setText("🤖 Análisis realizado con ChatGPT:");
        headerLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Text analysisText = new Text(container, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.WRAP | SWT.READ_ONLY);
        analysisText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        analysisText.setText(aiAnalysis);
        analysisText.setFont(new org.eclipse.swt.graphics.Font(Display.getCurrent(), "Segoe UI", 10, SWT.NORMAL));

        return container;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, "Cerrar", true);
        createButton(parent, 999, "Copiar", false);
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (buttonId == 999) {
            Clipboard clipboard = new Clipboard(Display.getCurrent());
            clipboard.setContents(new Object[]{aiAnalysis}, new Transfer[]{TextTransfer.getInstance()});
            clipboard.dispose();
            
            org.eclipse.jface.dialogs.MessageDialog.openInformation(getShell(),
                "Copiado", "Análisis copiado al portapapeles");
        } else {
            super.buttonPressed(buttonId);
        }
    }
}
