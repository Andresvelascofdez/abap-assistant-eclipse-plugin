package com.abap.assistant.utils;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Utility class for Eclipse editor operations
 */
public class EditorUtils {
    
    /**
     * Get the active editor part
     */
    public static IEditorPart getActiveEditor() {
        try {
            IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            if (window == null) return null;
            
            IWorkbenchPage page = window.getActivePage();
            if (page == null) return null;
            
            return page.getActiveEditor();
            
        } catch (Exception e) {
            // Log error but don't throw
            return null;
        }
    }
    
    /**
     * Get selected text from active editor
     */
    public static String getSelectedText() {
        try {
            IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            if (window == null) return null;
            
            IWorkbenchPage page = window.getActivePage();
            if (page == null) return null;
            
            IEditorPart editor = page.getActiveEditor();
            if (!(editor instanceof ITextEditor)) return null;
            
            ITextEditor textEditor = (ITextEditor) editor;
            ITextSelection selection = (ITextSelection) textEditor.getSelectionProvider().getSelection();
            
            if (selection.getLength() > 0) {
                return selection.getText();
            }
            
        } catch (Exception e) {
            // Log error but don't throw
        }
        return null;
    }
    
    /**
     * Get current file content from active editor
     */
    public static String getCurrentFileContent() {
        try {
            IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            if (window == null) return null;
            
            IWorkbenchPage page = window.getActivePage();
            if (page == null) return null;
            
            IEditorPart editor = page.getActiveEditor();
            if (!(editor instanceof ITextEditor)) return null;
            
            ITextEditor textEditor = (ITextEditor) editor;
            IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
            
            return document != null ? document.get() : null;
            
        } catch (Exception e) {
            // Log error but don't throw
        }
        return null;
    }
    
    /**
     * Replace text in active editor
     */
    public static boolean replaceText(String oldText, String newText) {
        try {
            IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            if (window == null) return false;
            
            IWorkbenchPage page = window.getActivePage();
            if (page == null) return false;
            
            IEditorPart editor = page.getActiveEditor();
            if (!(editor instanceof ITextEditor)) return false;
            
            ITextEditor textEditor = (ITextEditor) editor;
            IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
            
            if (document != null) {
                String content = document.get();
                String updatedContent = content.replace(oldText, newText);
                document.set(updatedContent);
                return true;
            }
            
        } catch (Exception e) {
            // Log error
        }
        return false;
    }
    
    /**
     * Insert text at current cursor position
     */
    public static boolean insertTextAtCursor(String text) {
        try {
            IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            if (window == null) return false;
            
            IWorkbenchPage page = window.getActivePage();
            if (page == null) return false;
            
            IEditorPart editor = page.getActiveEditor();
            if (!(editor instanceof ITextEditor)) return false;
            
            ITextEditor textEditor = (ITextEditor) editor;
            IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
            ITextSelection selection = (ITextSelection) textEditor.getSelectionProvider().getSelection();
            
            if (document != null && selection != null) {
                int offset = selection.getOffset();
                document.replace(offset, selection.getLength(), text);
                return true;
            }
            
        } catch (Exception e) {
            // Log error
        }
        return false;
    }
    
    /**
     * Get current editor title/filename
     */
    public static String getCurrentEditorTitle() {
        try {
            IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            if (window == null) return null;
            
            IWorkbenchPage page = window.getActivePage();
            if (page == null) return null;
            
            IEditorPart editor = page.getActiveEditor();
            return editor != null ? editor.getTitle() : null;
            
        } catch (Exception e) {
            // Log error
        }
        return null;
    }
    
    /**
     * Show information message to user
     */
    public static void showMessage(String message) {
        try {
            IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            if (window != null) {
                MessageDialog.openInformation(window.getShell(), "ABAP Assistant", message);
            }
        } catch (Exception e) {
            // Log error
        }
    }
    
    /**
     * Show error message to user
     */
    public static void showErrorMessage(String title, String message) {
        try {
            IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            if (window != null) {
                MessageDialog.openError(window.getShell(), title, message);
            }
        } catch (Exception e) {
            // Log error
        }
    }
    
    /**
     * Show warning message to user
     */
    public static void showWarningMessage(String title, String message) {
        try {
            IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            if (window != null) {
                MessageDialog.openWarning(window.getShell(), title, message);
            }
        } catch (Exception e) {
            // Log error
        }
    }
    
    /**
     * Check if current file is ABAP file
     */
    public static boolean isCurrentFileABAP() {
        String title = getCurrentEditorTitle();
        return title != null && (title.toLowerCase().endsWith(".abap") || 
                                title.toLowerCase().contains("abap"));
    }
    
    /**
     * Get current editor text content (alias for getCurrentFileContent)
     */
    public static String getCurrentEditorText() {
        return getCurrentFileContent();
    }
    
    /**
     * Replace entire content of current editor
     */
    public static boolean replaceCurrentEditorText(String newContent) {
        try {
            IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            if (window == null) return false;
            
            IWorkbenchPage page = window.getActivePage();
            if (page == null) return false;
            
            IEditorPart editor = page.getActiveEditor();
            if (!(editor instanceof ITextEditor)) return false;
            
            ITextEditor textEditor = (ITextEditor) editor;
            IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
            
            if (document != null) {
                document.set(newContent);
                return true;
            }
            
        } catch (Exception e) {
            // Log error but don't throw
            showErrorMessage("ABAP Assistant Error", "Failed to replace editor content: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Replace selected text in editor or entire content if nothing selected
     */
    public static boolean replaceSelectedTextOrAll(String newText) {
        try {
            IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            if (window == null) return false;
            
            IWorkbenchPage page = window.getActivePage();
            if (page == null) return false;
            
            IEditorPart editor = page.getActiveEditor();
            if (!(editor instanceof ITextEditor)) return false;
            
            ITextEditor textEditor = (ITextEditor) editor;
            IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
            ITextSelection selection = (ITextSelection) textEditor.getSelectionProvider().getSelection();
            
            if (document != null && selection != null) {
                if (selection.getLength() > 0) {
                    // Replace selected text
                    int offset = selection.getOffset();
                    int length = selection.getLength();
                    document.replace(offset, length, newText);
                } else {
                    // Replace entire document
                    document.set(newText);
                }
                return true;
            }
            
        } catch (Exception e) {
            showErrorMessage("ABAP Assistant Error", "Failed to replace text: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Replace selected text or current file content with new text
     * Enhanced version for modification service
     */
    public static boolean replaceSelectedTextOrCurrentFile(String newText) {
        try {
            IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            if (window == null) return false;

            IWorkbenchPage page = window.getActivePage();
            if (page == null) return false;

            IEditorPart editor = page.getActiveEditor();
            if (!(editor instanceof ITextEditor)) return false;

            ITextEditor textEditor = (ITextEditor) editor;
            IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
            ITextSelection selection = (ITextSelection) textEditor.getSelectionProvider().getSelection();

            if (document != null) {
                if (selection != null && selection.getLength() > 0) {
                    // Replace selected text
                    document.replace(selection.getOffset(), selection.getLength(), newText);
                } else {
                    // Replace entire document
                    document.set(newText);
                }
                return true;
            }

        } catch (Exception e) {
            showErrorMessage("ABAP Assistant Error", "Failed to replace text: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Insert text at current cursor position
     * Enhanced version for insertion service
     */
    public static boolean insertTextAtCurrentPosition(String text) {
        try {
            IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            if (window == null) return false;

            IWorkbenchPage page = window.getActivePage();
            if (page == null) return false;

            IEditorPart editor = page.getActiveEditor();
            if (!(editor instanceof ITextEditor)) return false;

            ITextEditor textEditor = (ITextEditor) editor;
            IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
            ITextSelection selection = (ITextSelection) textEditor.getSelectionProvider().getSelection();

            if (document != null && selection != null) {
                int offset = selection.getOffset();
                document.replace(offset, 0, text); // Insert without replacing
                return true;
            }

        } catch (Exception e) {
            showErrorMessage("ABAP Assistant Error", "Failed to insert text: " + e.getMessage());
        }
        return false;
    }
}
