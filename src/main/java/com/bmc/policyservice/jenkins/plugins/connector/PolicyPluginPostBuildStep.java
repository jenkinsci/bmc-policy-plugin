
package com.bmc.policyservice.jenkins.plugins.connector;


import com.bmc.policyservice.jenkins.plugins.connector.model.Connector;
import com.bmc.policyservice.jenkins.plugins.connector.model.JenkinsContext;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Extension;
import hudson.model.*;
import hudson.tasks.*;
import hudson.util.ListBoxModel;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.inject.Inject;
import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


/**
 * Created by Sudesh on 2/25/17.
 * The following code is licensed under the Apache 2.0 license
 */
public class PolicyPluginPostBuildStep extends Notifier {

    public static final Logger LOGGER = Logger.getLogger(PolicyPluginPostBuildStep.class.getName());

    private final String con;
    private final String args;
    private final String pols;

    @DataBoundConstructor
    public PolicyPluginPostBuildStep(String con, String args, String pols) {
        this.con = con;
        this.args = args;
        this.pols = pols;
    }

    public String getCon() {
        return con;
    }
    public String getArgs() { return args; }
    public String getPols() { return pols; }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {

        int exitCode = -1;
        Connector selectedConnector= getSelectedConnector(con);

        if (selectedConnector == null) {
            listener.getLogger().println("No connector selected. Marking the build as unstable");
            build.setResult(Result.UNSTABLE);
            return false;
        }


        EnvVars vars = null;
        try {
            vars = build.getEnvironment(listener);
        } catch (Exception e) {
            e.printStackTrace();
            build.setResult(Result.UNSTABLE);
            return false;
        }

        String policy = null;
        if (!Utils.empty(pols)) {
            policy = "--policy="+pols;
        }
        String arguments = Utils.substituteParameterValues(vars, selectedConnector.getArgs(), args);

        boolean retValue = true;
        try {
            JenkinsContext ctx = new JenkinsContext(launcher, listener);
            exitCode = Utils.launch(ctx, selectedConnector, policy, arguments);
            listener.getLogger().println("exit code = "+exitCode);
            if (exitCode != 0 ) {
                build.setResult(Result.UNSTABLE);
                retValue = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            build.setResult(Result.UNSTABLE);
            retValue = false;
        }

        String content = "ExitCode="+exitCode;
        try {

            FilePath workspace = build.getWorkspace();
            if (workspace == null) {
                workspace= new FilePath(new File("."));
            }
            URI uri = URI.create(workspace.toURI().toString()+ File.separator+"results.properties");
            Files.write(Paths.get(uri), content.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
            build.setResult(Result.UNSTABLE);
            retValue = false;
        }
        return retValue;

    }


    Connector getSelectedConnector(String con) {
        List<Connector> cons = getDescriptor().config.getConnectors();
        Connector selectedConnector=null;

        for (Connector c : cons){
            if (c.getName().equals(getCon())) {
                selectedConnector = c;
                break;
            }
        }

        return selectedConnector;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    //public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        @Inject
        PolicyPluginGlobalConfiguration config = new PolicyPluginGlobalConfiguration();

        public DescriptorImpl() {
            super(PolicyPluginPostBuildStep.class);
            load();
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        public ListBoxModel doFillConItems() {
            ListBoxModel lb = new ListBoxModel();
            for (Connector con : config.getConnectors() ) {
                lb.add(con.getName());
            }
            return lb;

        }

        public String getDisplayName() {
            return "Invoke BMC Policy Service";
        }

    }
}
