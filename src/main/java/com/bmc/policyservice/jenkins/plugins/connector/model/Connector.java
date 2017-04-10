package com.bmc.policyservice.jenkins.plugins.connector.model;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Created by Sudesh on 2/17/17.
 * The following code is licensed under the Apache 2.0 license
 */
public class Connector {
    private final String name;
    private final String path;
    private final String args;
    private Remote remote;



    @DataBoundConstructor
    public Connector (final String name, final String path, final String args,  Remote remote) {
        this.name = name;
        this.path = path;
        this.args = args;
        this.remote = remote;
    }

    public boolean isRemote() {
        return remote!=null;
    }

    public Remote getRemote() {
        return remote;
    }

    public String getHost() {
        return (remote!= null)?remote.getHost():null;
    }

    public int getPort() {
        return (remote!= null)?remote.getPort():22;
    }

    public String getUsername() {
        return (remote!= null)?remote.getUsername():null;
    }

    public String getPassword() {
        return (remote!= null)?remote.getPassword():null;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getArgs() {
        return args;
    }

    @Override
    public String toString() {
        return "{name: "+name+", path: "+path+", args: "+args+"}";
    }
}
