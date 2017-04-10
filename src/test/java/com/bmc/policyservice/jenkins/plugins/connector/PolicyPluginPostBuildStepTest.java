package com.bmc.policyservice.jenkins.plugins.connector;


import com.bmc.policyservice.jenkins.plugins.connector.model.Connector;
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

public class PolicyPluginPostBuildStepTest {

    PolicyPluginPostBuildStep pbs;
    Connector selectedConnector = new Connector("Test", "/opt/bmc/connector/run.sh", "--user=a --password=b", null);
    AbstractBuild mockedBuild = mock(AbstractBuild.class);
    Launcher mockedLauncher = mock(Launcher.class);
    BuildListener mockedListener = mock(BuildListener.class);
    PrintStream mockedPrintStream = new PrintStream(System.out);
    ProcStart mockedProc =  mock(ProcStart.class);

    @Before
    public void setUp() throws Exception {
        PolicyPluginPostBuildStep b = new PolicyPluginPostBuildStep("Test", "--user=blah", "PolicyName");
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

}
