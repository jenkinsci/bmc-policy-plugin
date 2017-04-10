package com.bmc.policyservice.jenkins.plugins.connector.model;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Created by Sudesh on 3/8/17.
 * The following code is licensed under the Apache 2.0 license
 */
public class Remote {
    private final String host;
    private final int port;
    private final String username;
    private final String password;



    @DataBoundConstructor
    public Remote (String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
