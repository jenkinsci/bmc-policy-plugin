package com.bmc.policyservice.jenkins.plugins.connector.model;

import hudson.Launcher;
import hudson.model.BuildListener;

/**
 * Created by Sudesh on 3/8/17.
 * The following code is licensed under the Apache 2.0 license
 */
public class JenkinsContext {

    Launcher launcher;
    BuildListener listener;

    public JenkinsContext(Launcher launcher, BuildListener listener) {
        this.launcher = launcher;
        this.listener = listener;
    }

    public Launcher getLauncher() {
        return launcher;
    }

    public BuildListener getListener() {
        return listener;
    }

}
