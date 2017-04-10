package com.bmc.policyservice.jenkins.plugins.connector;

import com.bmc.policyservice.jenkins.plugins.connector.model.Connector;
import hudson.Extension;
import jenkins.model.GlobalConfiguration;
import org.kohsuke.stapler.StaplerRequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sudesh on 3/5/17.
 * The following code is licensed under the Apache 2.0 license
 */
@Extension
public class PolicyPluginGlobalConfiguration extends GlobalConfiguration {

    private String name;
    private String path;
    private String args;
    private String host;
    private int port;
    private String username;
    private String password;
    private List<Connector> connectors = new ArrayList<>();


    public PolicyPluginGlobalConfiguration(String name, String path, String args, String host, int port, String username, String password) {
        this.name = name;
        this.path = path;
        this.args = args;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public PolicyPluginGlobalConfiguration() {
        load();
    }



    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public List<Connector> getConnectors() {
        return connectors;
    }

    @Override
    public boolean configure(StaplerRequest req, net.sf.json.JSONObject formData) throws FormException {


        connectors.clear();
        connectors.addAll(req.bindJSONToList(Connector.class, formData.get("connector")));
        save();
        return super.configure(req, formData);
    }

}
