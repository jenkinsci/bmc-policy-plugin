package com.bmc.policyservice.jenkins.plugins.connector;

import com.bmc.policyservice.jenkins.plugins.connector.model.Connector;
import com.bmc.policyservice.jenkins.plugins.connector.model.Remote;
import com.jcraft.jsch.Session;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.PrintStream;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by root on 3/29/17.
 */
public class PolicyPluginBuildStepTest {
    final String HOST = "localhost";
    final int PORT = 22;
    final String SSH_USER = "root";
    final String SSH_PASSWORD = "password";
    PolicyPluginBuildStep pbs;
    Connector selectedConnector = new Connector("Test", "/opt/bmc/connector/run.sh", "--user=a --password=b", null);
    Remote remote = new Remote(HOST, PORT, SSH_USER, SSH_PASSWORD);
    Connector remoteConnector = new Connector("Test", "/opt/bmc/connector/run.sh", "--user=a --password=b", remote);
    AbstractBuild<?, ?> mockedBuild = mock(AbstractBuild.class);
    Launcher mockedLauncher = mock(Launcher.class);
    BuildListener mockedListener = mock(BuildListener.class);
    PrintStream mockedPrintStream = new PrintStream(System.out);
    ProcStart mockedProc =  mock(ProcStart.class);
    Session mockedSession = mock(Session.class);

    @Before
    public void setUp() throws Exception {
        PolicyPluginBuildStep b = new PolicyPluginBuildStep("Test", "--user=blah", "PolicyName");
        pbs = spy(b);

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testBuildStep() throws Exception {

        when(mockedListener.getLogger()).thenReturn(mockedPrintStream);
        when(mockedProc.join()).thenReturn(0);
        doReturn(selectedConnector).when(pbs).getSelectedConnector(anyString());
        boolean result = pbs.perform(mockedBuild,mockedLauncher,mockedListener);

    }

    @Test
    public void testBuildRemote() throws Exception {
        Remote remote = new Remote(HOST, PORT, SSH_USER, SSH_PASSWORD);
        assertEquals(HOST, remote.getHost());
        assertEquals(PORT, remote.getPort());
        assertEquals(SSH_USER, remote.getUsername());
        assertEquals(SSH_PASSWORD, remote.getPassword());


    }
}