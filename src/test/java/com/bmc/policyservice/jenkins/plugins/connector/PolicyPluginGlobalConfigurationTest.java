package com.bmc.policyservice.jenkins.plugins.connector;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by root on 3/29/17.
 */
public class PolicyPluginGlobalConfigurationTest {

    final String NAME = "Test";
    final String PATH = "/opt/bmc/connector/test/run.sh";
    final String ARGS = "--user=admin@bmc.com --password=password";
    final String HOST = "localhost";
    final int PORT = 22;
    final String SSH_USER = "root";
    final String SSH_PASSWORD = "password";

    PolicyPluginGlobalConfiguration ppgc;
    @Before
    public void setUp() throws Exception {
       ppgc = new PolicyPluginGlobalConfiguration(NAME, PATH, ARGS, HOST, PORT, SSH_USER,SSH_PASSWORD);

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void runTest() throws Exception {

        assertEquals(ARGS, ppgc.getArgs());
        assertEquals(PATH, ppgc.getPath());
        assertEquals(NAME, ppgc.getName());
        assertEquals(HOST, ppgc.getHost());
        assertEquals(SSH_PASSWORD, ppgc.getPassword());
        assertEquals(PORT, ppgc.getPort());
        assertEquals(SSH_USER, ppgc.getUsername());



    }

}