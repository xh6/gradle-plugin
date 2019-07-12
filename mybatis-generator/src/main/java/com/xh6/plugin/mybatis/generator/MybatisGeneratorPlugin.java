package com.xh6.plugin.mybatis.generator;

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
                    MybatisCodeGenerator.getInstance().createCode(project);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
