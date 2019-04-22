package com.xh6.radle.plugin.ssh.deploy;

import java.io.File;

public class Deploy {

    private String user;

    private String host;

    private String password;

    private String remoteDir;

    private String jvmArg;

    private File   identity = new File(System.getProperty("user.home")+"/.ssh/id_rsa");

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public File getIdentity() {
        return identity;
    }

    public void setIdentity(File identity) {
        this.identity = identity;
    }

    public String getRemoteDir() {
        return remoteDir;
    }

    public void setRemoteDir(String remoteDir) {
        this.remoteDir = remoteDir;
    }

    public String getJvmArg() {
        return jvmArg;
    }

    public void setJvmArg(String jvmArg) {
        this.jvmArg = jvmArg;
    }

    @Override
    public String toString() {
        return "Deploy{" + "user='" + user + '\'' + ", host='" + host + '\'' + ", password='" + password + '\''+ ", identity='" + identity.getAbsolutePath() + '\''+ '}';
    }
}
