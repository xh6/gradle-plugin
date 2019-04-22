package com.xh6.radle.plugin.ssh.deploy;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

public class PublishUtils {

    public static void send(PublishInfo info) throws Exception {
        send(info.getHostname(), info.getUsername(), info.getPassword(), info.getLocalDir(), info.getAppName(), info.getRemoteDir(),
                info.getJvmArg());
    }

    public static void send(String hostname, String username, String password, String localDir, String appName, String remoteDir,
            String jvmArg) throws Exception {
        if (StringUtils.isAnyBlank(hostname, username, localDir, appName, remoteDir)) {
            throw new RuntimeException("invalid params");
        }
        File[] listFiles = new File(localDir).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar") && name.contains(appName) && !name.endsWith("javadoc.jar") && !name.endsWith("sources.jar");
            }
        });
        if (null == listFiles || listFiles.length == 0) {
            throw new RuntimeException("not find publish jar in:" + localDir);
        }
        File jar = Arrays.stream(listFiles).max((o1, o2) -> {
            long l1 = o1.lastModified();
            long l2 = o2.lastModified();
            return (int) (l1 - l2);
        }).get();
        SshClient sshClient = StringUtils.isBlank(password) ? new SshClient(hostname, username) : new SshClient(hostname, username, password);
        sshClient.execute("mkdir -p " + remoteDir);
        System.out.println("发布文件:" + jar.getAbsolutePath());
        sshClient.scpPut(jar.getAbsolutePath(), remoteDir);
        String pid = sshClient.getJarPid(appName);
        String logFile = remoteDir + "/" + StringUtils.substringBeforeLast(jar.getName(), ".") + ".log";
        String startCmd = String
                .format("cd %s && nohup java %s -jar %s/%s > %s &", remoteDir, null == jvmArg ? "" : jvmArg, remoteDir, jar.getName(), logFile);
        if (StringUtils.isNotBlank(pid)) {
            sshClient.execute("kill -9 " + pid);
            while (StringUtils.isNotBlank(sshClient.getJarPid(appName))) {
                TimeUnit.SECONDS.sleep(3);
            }
        }
        sshClient.executeWithNoReturn(startCmd.toString());
        String logCmd = String.format("ssh %s@%s \"tail -fn 500 %s\"", username, hostname, logFile);
        System.out.println(logCmd);
        sshClient.executeWithPrint(String.format("tail -fn 500 %s", logFile));
    }

}
