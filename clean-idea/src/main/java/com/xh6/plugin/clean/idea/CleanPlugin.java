package com.xh6.plugin.clean.idea;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

public class CleanPlugin implements Plugin<Project> {

    private static FilenameFilter filter = (dir, fileName) -> StringUtils.equalsAny(fileName, "build", "out", ".idea", ".gradle") ||
            StringUtils.endsWithAny(fileName, ".iml");

    @Override
    public void apply(Project project) {

        project.task("cleanIdea").doLast(new Action<Task>() {
            @Override
            public void execute(Task task) {
                List<File> list = new ArrayList<>();
                list.addAll(Arrays.stream(project.getRootDir().listFiles(filter)).collect(Collectors.toList()));
                Arrays.stream(project.getRootDir().listFiles(file -> file.isDirectory())).forEach(f -> {
                    list.addAll(Arrays.asList(f.listFiles(filter)));
                });
                list.forEach(System.out::println);
                list.forEach(f -> f.deleteOnExit());
            }
        });

    }
}
