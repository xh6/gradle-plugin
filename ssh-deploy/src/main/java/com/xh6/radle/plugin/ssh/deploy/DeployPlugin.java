package com.xh6.radle.plugin.ssh.deploy;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

public class DeployPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {

        Deploy deploy = project.getExtensions().create("deploy", Deploy.class);
        project.task("deploy").doLast(new Action<Task>() {
            @Override
            public void execute(Task task) {
                System.out.println("执行前配置:deploy_01=" + deploy);
                System.out.println("项目名称:" + project.getName() + " \n版本:" + project.getVersion() + "\n路径:" + project.getProjectDir() + "\n文件:" +
                        project.getBuildDir());
                PublishInfo publish = new PublishInfo();
                publish.setHostname(deploy.getHost());
                publish.setUsername(deploy.getUser());
                publish.setPassword(deploy.getPassword());
                publish.setAppName(project.getName());
                publish.setLocalDir(project.getBuildDir().getAbsolutePath() + "/libs");
                publish.setRemoteDir(deploy.getRemoteDir());
                publish.setJvmArg(deploy.getJvmArg());
                publish.setPort(deploy.getPort());
                try {
                    PublishUtils.send(publish);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Deploy deploy_01 = project.getExtensions().create("deploy_01", Deploy.class);
        project.task("deploy_01").doLast(new Action<Task>() {
            @Override
            public void execute(Task task) {
                System.out.println("执行前配置:deploy_01=" + deploy_01);
                System.out.println("项目名称:" + project.getName() + " \n版本:" + project.getVersion() + "\n路径:" + project.getProjectDir() + "\n文件:" +
                        project.getBuildDir());
                PublishInfo publish = new PublishInfo();
                publish.setHostname(deploy_01.getHost());
                publish.setUsername(deploy_01.getUser());
                publish.setPassword(deploy_01.getPassword());
                publish.setAppName(project.getName());
                publish.setLocalDir(project.getBuildDir().getAbsolutePath() + "/libs");
                publish.setRemoteDir(deploy_01.getRemoteDir());
                publish.setJvmArg(deploy_01.getJvmArg());
                try {
                    PublishUtils.send(publish);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Deploy deploy_02 = project.getExtensions().create("deploy_02", Deploy.class);
        project.task("deploy_02").doLast(new Action<Task>() {
            @Override
            public void execute(Task task) {
                System.out.println("执行前配置:deploy_01=" + deploy_02);
                System.out.println("项目名称:" + project.getName() + " \n版本:" + project.getVersion() + "\n路径:" + project.getProjectDir() + "\n文件:" +
                        project.getBuildDir());
                PublishInfo publish = new PublishInfo();
                publish.setHostname(deploy_02.getHost());
                publish.setUsername(deploy_02.getUser());
                publish.setPassword(deploy_02.getPassword());
                publish.setAppName(project.getName());
                publish.setLocalDir(project.getBuildDir().getAbsolutePath() + "/libs");
                publish.setRemoteDir(deploy_02.getRemoteDir());
                publish.setJvmArg(deploy_02.getJvmArg());
                try {
                    PublishUtils.send(publish);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
