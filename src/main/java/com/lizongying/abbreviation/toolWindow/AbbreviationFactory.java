package com.lizongying.abbreviation.toolWindow;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

public class AbbreviationFactory implements ToolWindowFactory {
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        Abbreviation abbreviation = new Abbreviation(toolWindow);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(abbreviation.getContent(), "", false);
        toolWindow.getContentManager().addContent(content);

        ProjectService projectService = ServiceManager.getService(project, ProjectService.class);
        projectService.setAbbreviation(abbreviation);
    }

    public static class ProjectService {
        private Abbreviation abbreviation;

        public Abbreviation getAbbreviation() {
            return abbreviation;
        }

        void setAbbreviation(Abbreviation abbreviation) {
            this.abbreviation = abbreviation;
        }
    }
}
