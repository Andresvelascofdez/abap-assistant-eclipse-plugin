package com.abap.assistant.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import com.abap.assistant.services.DocumentContextManager;

/**
 * Dialog for managing document context settings
 */
public class DocumentContextDialog extends Dialog {

    private DocumentContextManager contextManager;
    private List documentList;
    private Label statusLabel;

    public DocumentContextDialog(Shell parentShell, DocumentContextManager contextManager) {
        super(parentShell);
        this.contextManager = contextManager;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Document Context Manager");
        shell.setSize(500, 400);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        composite.setLayout(new GridLayout(2, false));

        // Header
        Label headerLabel = new Label(composite, SWT.NONE);
        headerLabel.setText("Manage documents used for AI context:");
        headerLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        // Document list
        documentList = new List(composite, SWT.BORDER | SWT.CHECK | SWT.V_SCROLL);
        GridData listData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        listData.heightHint = 200;
        documentList.setLayoutData(listData);

        // Buttons
        Composite buttonComposite = new Composite(composite, SWT.NONE);
        buttonComposite.setLayout(new GridLayout(1, false));
        buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));

        Button enableAllButton = new Button(buttonComposite, SWT.PUSH);
        enableAllButton.setText("Enable All");
        enableAllButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

        Button disableAllButton = new Button(buttonComposite, SWT.PUSH);
        disableAllButton.setText("Disable All");
        disableAllButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

        Button removeButton = new Button(buttonComposite, SWT.PUSH);
        removeButton.setText("Remove Selected");
        removeButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

        Button clearAllButton = new Button(buttonComposite, SWT.PUSH);
        clearAllButton.setText("Clear All");
        clearAllButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

        // Status label
        statusLabel = new Label(composite, SWT.NONE);
        statusLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        // Event handlers
        enableAllButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                enableAllDocuments();
            }
        });

        disableAllButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                disableAllDocuments();
            }
        });

        removeButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                removeSelectedDocuments();
            }
        });

        clearAllButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                clearAllDocuments();
            }
        });

        documentList.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                toggleDocumentEnabled();
            }
        });

        populateDocumentList();
        updateStatus();

        return composite;
    }

    private void populateDocumentList() {
        documentList.removeAll();
        for (String filePath : contextManager.getAvailableDocuments()) {
            String fileName = getFileName(filePath);
            documentList.add(fileName + " (" + filePath + ")");
        }
        
        // Update check states
        for (int i = 0; i < documentList.getItemCount(); i++) {
            String item = documentList.getItem(i);
            String filePath = extractFilePath(item);
            // Note: SWT List with SWT.CHECK doesn't support programmatic check setting
            // This would need to be implemented with a different widget in a full implementation
        }
    }

    private void enableAllDocuments() {
        for (String filePath : contextManager.getAvailableDocuments()) {
            contextManager.setDocumentEnabled(filePath, true);
        }
        updateStatus();
    }

    private void disableAllDocuments() {
        for (String filePath : contextManager.getAvailableDocuments()) {
            contextManager.setDocumentEnabled(filePath, false);
        }
        updateStatus();
    }

    private void removeSelectedDocuments() {
        int[] selections = documentList.getSelectionIndices();
        java.util.List<String> availableDocs = contextManager.getAvailableDocuments();
        
        for (int i = selections.length - 1; i >= 0; i--) {
            if (selections[i] < availableDocs.size()) {
                String filePath = availableDocs.get(selections[i]);
                contextManager.removeDocument(filePath);
            }
        }
        
        populateDocumentList();
        updateStatus();
    }

    private void clearAllDocuments() {
        contextManager.clearAllDocuments();
        populateDocumentList();
        updateStatus();
    }

    private void toggleDocumentEnabled() {
        int selection = documentList.getSelectionIndex();
        if (selection >= 0) {
            java.util.List<String> availableDocs = contextManager.getAvailableDocuments();
            if (selection < availableDocs.size()) {
                String filePath = availableDocs.get(selection);
                boolean currentState = contextManager.isDocumentEnabled(filePath);
                contextManager.setDocumentEnabled(filePath, !currentState);
                updateStatus();
            }
        }
    }

    private void updateStatus() {
        statusLabel.setText(contextManager.getContextSummary());
    }

    private String getFileName(String filePath) {
        int lastSeparator = Math.max(filePath.lastIndexOf('/'), filePath.lastIndexOf('\\'));
        return lastSeparator >= 0 ? filePath.substring(lastSeparator + 1) : filePath;
    }

    private String extractFilePath(String listItem) {
        int start = listItem.indexOf('(');
        int end = listItem.lastIndexOf(')');
        if (start >= 0 && end > start) {
            return listItem.substring(start + 1, end);
        }
        return listItem;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    }
}
