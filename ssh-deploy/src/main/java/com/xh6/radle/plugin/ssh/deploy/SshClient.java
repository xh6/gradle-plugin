package com.xh6.radle.plugin.ssh.deploy;

import java.io.*;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SshClient {

    private static final Logger     logger          = LoggerFactory.getLogger(SshClient.class);

    private              Connection conn;

    private              String     hostname;

    private              String     username;

    private static       String     DEFAULTCHARTSET = "UTF-8";

    public SshClient(String hostname, Integer port, String username, String password) {
        try {
            if (null == port) {
                port = 22;
            }
            this.hostname = hostname;
            this.username = username;
            conn = new Connection(hostname, port);
            conn.connect();
            boolean loginStatus = conn.authenticateWithPassword(username, password);
            if (loginStatus) {
                logger.info("ssh login by password success,hostname:{},username:{}", hostname, username);
            } else {
                logger.error("ssh login by password fail,invalid username or password,hostname:{},username:{}", hostname, username);
                throw new RuntimeException("ssh login by password fail,invalid username or password");
            }
        } catch (IOException e) {
            logger.error("ssh login by password fail,hostname:{},username:{}", hostname, username, e);
            throw new RuntimeException("ssh login by password fail");
        }
    }

    public SshClient(String hostname, Integer port, String username, File publicKey, String password) {
        try {
            if (null == port) {
                port = 22;
            }
            this.hostname = hostname;
            this.username = username;
            conn = new Connection(hostname, port);
            conn.connect();
            boolean loginStatus = conn.authenticateWithPublicKey(username, publicKey, password);
            if (loginStatus) {
                logger.info("ssh login by publicKey success,hostname:{},username:{},publicKey:{}", hostname, username, publicKey);
            } else {
                logger.info("ssh login by publicKey fail,hostname:{},username:{},publicKey:{}", hostname, username, publicKey);
                throw new RuntimeException("ssh login by password fail,invalid username or publicKey");
            }
        } catch (IOException e) {
            logger.error("ssh login by publicKey fail,hostname:{},username:{},publicKey:{}", hostname, username, publicKey, e);
            throw new RuntimeException("ssh login by password fail");
        }
    }

    public SshClient(String hostname, Integer port, String username) {
        this(hostname, port, username, new File(System.getProperties().getProperty("user.home") + "/.ssh/id_rsa"), null);
    }

    /**
     * @param cmd 脚本命令
     * @exception
     * @Title: execute
     * @Description: 远程执行shll脚本或者命令
     * @return: result 命令执行完毕返回结果
     */
    public String execute(String cmd) {
        System.out.println(cmd);
        String result = "";
        try {
            Session session = conn.openSession();// 打开一个会话
            session.execCommand(cmd);// 执行命令
            result = processStdout(session.getStdout(), DEFAULTCHARTSET);
            // 如果为得到标准输出为空，说明脚本执行出错了
            if (StringUtils.isBlank(result)) {
                result = processStdout(session.getStderr(), DEFAULTCHARTSET);
            }
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.trim();
    }

    public void executeWithNoReturn(String cmd) {
        System.out.println(cmd);
        String result = "";
        try {
            StringBuilder newCmd = new StringBuilder(" bash -lc '").append(cmd).append("'");
            Session session = conn.openSession();// 打开一个会话
            session.execCommand(newCmd.toString());// 执行命令
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void executeWithPrint(String cmd) {
        System.out.println("************************************************************************************************************************");
        System.out.println("SSH执行命令:");
        System.out.println(cmd);
        String result = "";
        try {
            StringBuilder newCmd = new StringBuilder(" bash -lc '").append(cmd).append("'");
            Session session = conn.openSession();// 打开一个会话
            session.execCommand(newCmd.toString());// 执行命令
            InputStream stdout = new StreamGobbler(session.getStdout());
            StringBuffer buffer = new StringBuffer();
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout, DEFAULTCHARTSET));
            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            session.close();
            System.out.println("************************************************************************************************************************");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param in      输入流对象
     * @param charset 编码
     * @return String 以纯文本的格式返回
     * @exception
     * @Title: processStdout
     * @Description: 解析脚本执行的返回结果
     */
    public static String processStdout(InputStream in, String charset) {
        InputStream stdout = new StreamGobbler(in);
        StringBuffer buffer = new StringBuffer();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout, charset));
            String line = null;
            while ((line = br.readLine()) != null) {
                buffer.append(line + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    /**
     * @return String
     * @exception
     * @Title: ConnectLinux
     * @Description: 通过用户名和密码关联linux服务器
     */
    public static boolean connectLinux(String hostname, Integer port, String username, String password, String command) {
        SshClient executeCommand = new SshClient(hostname, port, username, password);
        String result = executeCommand.execute(command);
        return StringUtils.isNotBlank(result);
    }

    /**
     * @param remoteFile 文件位置(其他服务器)
     * @param localDir   本服务器目录
     * @return void
     * @exception IOException
     * @exception
     * @Title: scpGet
     * @Description: 从其他服务器获取文件到本服务器指定目录
     */
    public void scpGet(String remoteFile, String localDir) throws IOException {
        SCPClient scpClient = new SCPClient(conn);
        scpClient.get(remoteFile, localDir);
    }

    /**
     * @param localFile
     * @param remoteDir
     * @return void
     * @exception IOException
     * @exception
     * @Title: scpPut
     * @Description: 将文件复制到其他计算机中
     */
    public void scpPut(String localFile, String remoteDir) throws IOException {
        logger.info("start upload file:{},to server:{}", localFile, hostname);
        long startTime = System.currentTimeMillis();
        SCPClient scpClient = new SCPClient(conn);
        scpClient.put(localFile, remoteDir);
        logger.info("scp upload success host:{},local:{},remote:{},ustime={}", hostname, localFile, remoteDir, DateUtils.getUseTime(startTime));
    }

    public String getJarPid(String application) {
        String cmd = new StringBuilder(" bash -lc 'jps -l'  | grep ").append(application).append(" | awk '{print $1}'").toString();
        String result = execute(cmd);
        if (StringUtils.isBlank(result)) {
            logger.warn("application pid not found::{}", application);
        } else {
            logger.info("application pid found:{}({})", application, result);
        }
        return result;
    }

    public Connection getConn() {
        return conn;
    }

    public void close() {
        conn.close();
    }
}
