package com.abap.assistant.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.contentmergeviewer.TextMergeViewer;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.abap.assistant.utils.EditorUtils;

/**
 * Handler to show code differences before applying changes
 */
public class ShowDiffHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
        
        // Get the current editor content
        String currentCode = EditorUtils.getCurrentEditorText();
        if (currentCode == null || currentCode.trim().isEmpty()) {
            MessageDialog.openInformation(window.getShell(), 
                "ABAP Assistant", 
                "No active editor with code content found.");
            return null;
        }
        
        // For demonstration, create a sample optimized version
        // In real implementation, this would come from ChatGPT
        String optimizedCode = generateSampleOptimization(currentCode);
        
        // Show diff dialog
        showDiffDialog(window.getShell(), currentCode, optimizedCode);
        
        return null;
    }
    
    /**
     * Generate sample optimization for demonstration
     */
    private String generateSampleOptimization(String originalCode) {
        // Simple example transformation
        String optimized = originalCode;
        
        // Replace some common ABAP patterns with optimized versions
        optimized = optimized.replaceAll("DO\\s+(\\d+)\\s+TIMES", "DO $1 TIMES  \" Optimized loop");
        optimized = optimized.replaceAll("IF\\s+(.+)\\s+EQ\\s+", "IF $1 = ");  // Use = instead of EQ
        optimized = optimized.replaceAll("DATA:\\s*", "DATA: \" Optimized declaration\\n    ");
        
        // Add optimization comment at the top
        optimized = "* === ABAP Assistant Optimized Code ===\\n" + optimized;
        
        return optimized;
    }
    
    /**
     * Show a dialog with code comparison
     */
    private void showDiffDialog(Shell parent, String original, String modified) {
        CodeDiffDialog dialog = new CodeDiffDialog(parent, original, modified);
        dialog.open();
    }
    
    /**
     * Dialog to show code differences with apply/cancel options
     */
    private class CodeDiffDialog extends Dialog {
        private String originalCode;
        private String modifiedCode;
        
        public CodeDiffDialog(Shell parentShell, String originalCode, String modifiedCode) {
            super(parentShell);
            this.originalCode = originalCode;
            this.modifiedCode = modifiedCode;
            setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
        }
        
        @Override
        protected void configureShell(Shell newShell) {
            super.configureShell(newShell);
            newShell.setText("ABAP Assistant - Code Changes Preview");
            newShell.setSize(800, 600);
        }
        
        @Override
        protected Control createDialogArea(Composite parent) {
            Composite composite = (Composite) super.createDialogArea(parent);
            composite.setLayout(new GridLayout(1, false));
            
            // Title label
            Label titleLabel = new Label(composite, SWT.NONE);
            titleLabel.setText("Review the proposed code changes:");
            titleLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            
            // Create a simple text comparison area
            Composite compareComposite = new Composite(composite, SWT.NONE);
            compareComposite.setLayout(new GridLayout(2, true));
            compareComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            
            // Original code section
            Label originalLabel = new Label(compareComposite, SWT.NONE);
            originalLabel.setText("Original Code:");
            originalLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            
            // Modified code section
            Label modifiedLabel = new Label(compareComposite, SWT.NONE);
            modifiedLabel.setText("Optimized Code:");
            modifiedLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            
            // Text areas for comparison
            org.eclipse.swt.widgets.Text originalText = new org.eclipse.swt.widgets.Text(
                compareComposite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
            originalText.setText(originalCode);
            originalText.setEditable(false);
            originalText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            
            org.eclipse.swt.widgets.Text modifiedText = new org.eclipse.swt.widgets.Text(
                compareComposite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
            modifiedText.setText(modifiedCode);
            modifiedText.setEditable(false);
            modifiedText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            
            return composite;
        }
        
        @Override
        protected void createButtonsForButtonBar(Composite parent) {
            createButton(parent, 100, "Apply Changes", true);  // Apply button
            createButton(parent, CANCEL, "Cancel", false);     // Cancel button
        }
        
        @Override
        protected void buttonPressed(int buttonId) {
            if (buttonId == 100) {  // Apply button
                // Apply the changes to the editor
                boolean success = EditorUtils.replaceCurrentEditorText(modifiedCode);
                if (success) {
                    MessageDialog.openInformation(getShell(), 
                        "ABAP Assistant", 
                        "Code changes applied successfully!");
                } else {
                    MessageDialog.openError(getShell(), 
                        "ABAP Assistant", 
                        "Failed to apply code changes.");
                }
                close();
            } else {
                super.buttonPressed(buttonId);
            }
        }
    }
}
