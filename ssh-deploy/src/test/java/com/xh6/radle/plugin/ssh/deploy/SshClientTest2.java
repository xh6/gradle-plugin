package com.xh6.radle.plugin.ssh.deploy;

import org.junit.Test;

public class SshClientTest2 {

    private String host     = "192.168.5.140";

    private String username = "root";

    private String password = "hengtian";

    void testExecute() {
    }

    void testExecuteWithNoReturn() {
    }

    void testExecuteWithPrint() {
    }

    void testProcessStdout() {
    }

    void testConnectLinux() {
    }

    void testScpGet() {
    }

    void testScpPut() {
    }

    void testGetJarPid() {
    }

    @Test
    public void testGetConn() {
       for(int i=0;i<10;i++){
           SshClient client = new SshClient(host, 22, username, password);
       }
    }

    @Test
    public void testClose() {
        SshClient client = new SshClient(host, 22, username, password);
    }
}
