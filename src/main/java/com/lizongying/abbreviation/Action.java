package com.lizongying.abbreviation;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.lizongying.abbreviation.toolWindow.Abbreviation;
import com.lizongying.abbreviation.toolWindow.AbbreviationFactory;
import org.jetbrains.annotations.NotNull;


/**
 * Menu action to replace a selection of characters with a fixed string.
 *
 * @see com.intellij.openapi.actionSystem.AnAction
 */
public class Action extends AnAction {

    /**
     * Replaces the run of text selected by the primary caret with a fixed string.
     *
     * @param e Event related to this action
     */
    @Override
    public void actionPerformed(@NotNull final AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        AbbreviationFactory.ProjectService projectService = ServiceManager.getService(project, AbbreviationFactory.ProjectService.class);
        Abbreviation abbreviation = projectService.getAbbreviation();
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        final Document document = editor.getDocument();
        Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
        int start = primaryCaret.getSelectionStart();
        int end = primaryCaret.getSelectionEnd();
        primaryCaret.removeSelection();
        if (end > start) {
            String keyword = document.getText().substring(start, end).trim();
            if (keyword.length() > 0) {
                abbreviation.getData(keyword);
            }
        }
    }

    /**
     * Sets visibility and enables this action menu item if:
     * A project is open,
     * An editor is active,
     * Some characters are selected
     *
     * @param e Event related to this action
     */
    @Override
    public void update(@NotNull final AnActionEvent e) {
        final Project project = e.getProject();
        final Editor editor = e.getData(CommonDataKeys.EDITOR);
        e.getPresentation().setEnabledAndVisible(project != null && editor != null && editor.getSelectionModel().hasSelection());
    }
}
