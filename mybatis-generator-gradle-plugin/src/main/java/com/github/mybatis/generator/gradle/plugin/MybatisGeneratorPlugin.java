package com.github.mybatis.generator.gradle.plugin;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

public class MybatisGeneratorPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {

        project.task("mybatisGenerator").doLast(new Action<Task>() {
            @Override
            public void execute(Task task) {
                try {
                    CodeGenerator.getInstance().createCode(project);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
