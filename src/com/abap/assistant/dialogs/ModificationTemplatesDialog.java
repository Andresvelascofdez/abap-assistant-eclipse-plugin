package com.abap.assistant.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.abap.assistant.utils.ConfigurationManager;

/**
 * Dialog for configuring ABAP modification marker templates
 * Allows users to customize BEGIN MOD, END MOD, BEGIN INS, END INS formats
 */
public class ModificationTemplatesDialog extends Dialog {
    
    private Text modBeginText;
    private Text modEndText;
    private Text insBeginText;
    private Text insEndText;
    
    private ConfigurationManager config;
    
    public ModificationTemplatesDialog(Shell parentShell) {
        super(parentShell);
        this.config = ConfigurationManager.getInstance();
    }
    
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("ABAP Modification Templates Configuration");
    }
    
    @Override
    protected Point getInitialSize() {
        return new Point(600, 450);
    }
    
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = 10;
        layout.marginWidth = 10;
        layout.verticalSpacing = 8;
        container.setLayout(layout);
        
        // Title
        Label titleLabel = new Label(container, SWT.NONE);
        titleLabel.setText("Customize ABAP Modification Marker Templates");
        titleLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        
        // Info text
        Label infoLabel = new Label(container, SWT.WRAP);
        infoLabel.setText("Use placeholders: {TICKET} = ticket number, {USER} = username, {DATE} = current date");
        GridData infoData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        infoData.widthHint = 500;
        infoLabel.setLayoutData(infoData);
        
        // Separator
        Label separator1 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
        separator1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        
        // BEGIN MOD Template
        Label modBeginLabel = new Label(container, SWT.NONE);
        modBeginLabel.setText("BEGIN MOD Template:");
        modBeginLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        
        modBeginText = new Text(container, SWT.BORDER);
        modBeginText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        modBeginText.setText(config.getModBeginTemplate());
        
        // END MOD Template
        Label modEndLabel = new Label(container, SWT.NONE);
        modEndLabel.setText("END MOD Template:");
        modEndLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        
        modEndText = new Text(container, SWT.BORDER);
        modEndText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        modEndText.setText(config.getModEndTemplate());
        
        // BEGIN INS Template
        Label insBeginLabel = new Label(container, SWT.NONE);
        insBeginLabel.setText("BEGIN INS Template:");
        insBeginLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        
        insBeginText = new Text(container, SWT.BORDER);
        insBeginText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        insBeginText.setText(config.getInsBeginTemplate());
        
        // END INS Template
        Label insEndLabel = new Label(container, SWT.NONE);
        insEndLabel.setText("END INS Template:");
        insEndLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        
        insEndText = new Text(container, SWT.BORDER);
        insEndText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        insEndText.setText(config.getInsEndTemplate());
        
        // Separator
        Label separator2 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
        separator2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        
        // Preview section
        Label previewLabel = new Label(container, SWT.NONE);
        previewLabel.setText("Preview (with TICKET-123, AVELASC, current date):");
        previewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        
        Text previewText = new Text(container, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.READ_ONLY);
        GridData previewData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
        previewData.heightHint = 100;
        previewText.setLayoutData(previewData);
        
        // Update preview initially
        updatePreview(previewText);
        
        // Update preview when templates change
        modBeginText.addModifyListener(e -> updatePreview(previewText));
        modEndText.addModifyListener(e -> updatePreview(previewText));
        insBeginText.addModifyListener(e -> updatePreview(previewText));
        insEndText.addModifyListener(e -> updatePreview(previewText));
        
        // Reset to defaults button
        Button resetButton = new Button(container, SWT.PUSH);
        resetButton.setText("Reset to Defaults");
        resetButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        resetButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                resetToDefaults();
                updatePreview(previewText);
            }
        });
        
        return container;
    }
    
    private void updatePreview(Text previewText) {
        String sampleTicket = "TICKET-123";
        String sampleUser = "AVELASC";  
        String sampleDate = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        
        StringBuilder preview = new StringBuilder();
        preview.append("MODIFICATION EXAMPLE:\n");
        preview.append(processTemplate(modBeginText.getText(), sampleTicket, sampleUser, sampleDate)).append("\n");
        preview.append("*old code line commented out\n");
        preview.append("new code line added\n");
        preview.append(processTemplate(modEndText.getText(), sampleTicket, sampleUser, sampleDate)).append("\n\n");
        
        preview.append("INSERTION EXAMPLE:\n");
        preview.append(processTemplate(insBeginText.getText(), sampleTicket, sampleUser, sampleDate)).append("\n");
        preview.append("new code inserted\n");
        preview.append(processTemplate(insEndText.getText(), sampleTicket, sampleUser, sampleDate));
        
        previewText.setText(preview.toString());
    }
    
    private String processTemplate(String template, String ticket, String user, String date) {
        return template
            .replace("{TICKET}", ticket)
            .replace("{USER}", user)
            .replace("{DATE}", date);
    }
    
    private void resetToDefaults() {
        modBeginText.setText("*BEGIN MOD {TICKET} {USER} {DATE}");
        modEndText.setText("*END MOD {TICKET} {USER} {DATE}");
        insBeginText.setText("*BEGIN INS {TICKET} {USER} {DATE}");
        insEndText.setText("*END INS {TICKET} {USER} {DATE}");
    }
    
    @Override
    protected void okPressed() {
        // Save configurations
        config.setModBeginTemplate(modBeginText.getText());
        config.setModEndTemplate(modEndText.getText());
        config.setInsBeginTemplate(insBeginText.getText());
        config.setInsEndTemplate(insEndText.getText());
        
        super.okPressed();
    }
    
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, "Save", true);
        createButton(parent, IDialogConstants.CANCEL_ID, "Cancel", false);
    }
}
