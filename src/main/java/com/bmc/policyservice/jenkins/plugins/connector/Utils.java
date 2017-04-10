package com.bmc.policyservice.jenkins.plugins.connector;

import com.bmc.policyservice.jenkins.plugins.connector.model.Connector;
import com.bmc.policyservice.jenkins.plugins.connector.model.JenkinsContext;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import hudson.EnvVars;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.TaskListener;
import jenkins.model.Jenkins;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sudesh on 3/6/17.
 * The following code is licensed under the Apache 2.0 license
 */
public class Utils {

    /**
     *
     * Launches the executable with the supplied arguments.
     * It is expected that the command line args have options specified
     * with a leading '-' as in :
     * -P policyname
     * OR
     * --policyName=policyname
     *
     * @param ctx  JenkinsContext
     * @param con  Selected connector
     * @param policy policy name
     * @param arguments command arguments to the connector
     * @return     exit code
     * @throws Exception exception
     */
    public static int launch(JenkinsContext ctx, Connector con, String policy, String arguments) throws Exception {

        int exitCode = 1;
        String dir = con.getPath();
        dir = dir.substring(0, dir.lastIndexOf(File.separatorChar));

        if (con.isRemote()) {
            exitCode = launchRemote(ctx, con, policy, arguments, dir);
        } else {
            exitCode = launchLocal(ctx, con, policy, arguments, dir);
        }
        return exitCode;
    }

    private static int launchLocal(JenkinsContext ctx, Connector con, String policy, String arguments, String dir) throws IOException, InterruptedException {
        int exitCode;
        List<String> argList = new ArrayList<>();

        argList.add(con.getPath());
        if (!empty(policy)) {
            argList.add(policy);
        }

        if (!empty(arguments)) {
            arguments = " " + arguments.trim();
            String[] args = arguments.split(" -");

            for (String arg : args) {
                if (!empty(arg)) {
                    argList.add("-" + arg.trim());
                }
            }

        }

        Launcher.ProcStarter ps = ctx.getLauncher().launch();
        ProcStart p = new ProcStart();
        p.setProcStarter(ps);
        p.cmds(argList);
        p.stdout(ctx.getListener());
        p.pwd(dir);

        //ps.cmds(argList).stdout(ctx.getListener());
        //ps.pwd(dir);
        try {
            exitCode = p.join();
        } finally {
            ctx.getListener().getLogger().println("Program terminated!");
        }
        return exitCode;
    }

    private static int launchRemote(JenkinsContext ctx, Connector con, String policy, String arguments, String dir) throws JSchException, IOException {
        int exitCode;
        JSch jsch = new JSch();
        Session session = jsch.getSession(con.getRemote().getUsername(), con.getRemote().getHost(), con.getRemote().getPort());
        session.setConfig("StrictHostKeyChecking", "no");
        session.setPassword(con.getRemote().getPassword());
        session.connect();

        ChannelExec channelExec = (ChannelExec) session.openChannel("exec");

        InputStream in = channelExec.getInputStream();

        StringBuilder cmd = new StringBuilder();
        cmd.append("cd " + dir + ";");
        cmd.append(con.getPath());
        if (!empty(policy)) {
            cmd.append(" '" + policy + "'");
        }

        if (!empty(arguments)) {
            cmd.append(" " + arguments.trim());
        }
        ctx.getListener().getLogger().println("Executing cmd in remote machine: " + cmd);
        channelExec.setCommand(cmd.toString());

        channelExec.connect();

        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String line;

        while ((line = reader.readLine()) != null) {
            ctx.getListener().getLogger().println(line);
        }

        reader.close();

        channelExec.disconnect();
        session.disconnect();

        exitCode = channelExec.getExitStatus();
        return exitCode;
    }

    /**
     * Looks for ${var} or $var in the arguments and replaces it with the corresponding value
     *
     * @param envVars Environmental variables
     * @param connectorArgs connector arguments from global configuration
     * @param jobArgs job level arguments
     * @return  string with parameters substituted
     */
    public static String substituteParameterValues(final EnvVars envVars, String connectorArgs, String jobArgs) {

        if (envVars == null) {
            return " " + jobArgs + " " + connectorArgs;
        } else {
            return substituteParameters(envVars, jobArgs + " " + connectorArgs);
        }

    }

    public static boolean empty(final String str) {
        return str == null || str.trim().isEmpty();
    }

    private static String substituteParameters(final EnvVars envVars, String vars) {
        if (empty(vars)) {
            return null;
        }
        char[] chars = vars.toCharArray();
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            StringBuilder v = new StringBuilder();
            if (chars[i] == '$') {
                v.append(chars[i]);
                while (++i < chars.length && chars[i] != ' ') {
                    v.append(chars[i]);
                }
                if (v.toString().contains("{")) {
                    v.deleteCharAt(0).deleteCharAt(0).deleteCharAt(v.length() - 1);
                } else {
                    v.deleteCharAt(0);
                }
                String s = envVars.get(v.toString());
                if (s != null) {
                    res.append(s).append(" ");
                } else {
                    res.append(v).append(" ");
                }
            } else {
                res.append(chars[i]);
            }
        }

        return res.toString();

    }
}

// Class added to facilitate Junit/mocking
class ProcStart {

    Launcher.ProcStarter ps;

    public void setProcStarter(Launcher.ProcStarter ps) {
        this.ps = ps;

    }

    public void cmds(List<String> argList) {
        if (ps!=null) {
            ps.cmds(argList);
        }
    }

    public void stdout(BuildListener listener) {
        if (ps!=null) {
            ps.stdout(listener);
        }
    }

    public void pwd(String dir) {
        if (ps != null) {
            ps.pwd(dir);
        }
    }

    public int join() throws IOException, InterruptedException {
        if (ps != null) {
            return ps.join();
        }
        return 1;
    }
}
