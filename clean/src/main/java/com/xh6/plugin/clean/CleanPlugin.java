package com.xh6.plugin.clean;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

public class CleanPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {

        project.task("cleanIdea").doLast(new Action<Task>() {
            @Override
            public void execute(Task task) {
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
