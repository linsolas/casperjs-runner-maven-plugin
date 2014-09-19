package com.github.linsolas.casperjsrunner;

import static com.github.linsolas.casperjsrunner.CasperJsVersionRetriever.retrieveVersion;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyNoMoreInteractions;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@PrepareForTest({ Runtime.class, CasperJsVersionRetriever.class })
@RunWith(PowerMockRunner.class)
public class CasperJsVersionRetrieverTest {

    @Mock
    private Runtime runtime;

    @Mock
    private Process process;

    @Mock
    private InputStream stream;

    @Before
    public void initializeMocks() {
        initMocks(this);
        mockStatic(Runtime.class);

        LogUtils.setLog(mock(Log.class), false);

        when(Runtime.getRuntime()).thenReturn(runtime);
    }

    @Test
    public void testRetrieveVersion() throws Exception {
        when(runtime.exec("casperjsRuntime --version")).thenReturn(process);
        when(process.getInputStream()).thenReturn(stream);
        when(stream.read(any(byte[].class), anyInt(), anyInt())).then(new Answer<Integer>() {

            private final ByteArrayInputStream innerStream = new ByteArrayInputStream(
                    "1.2.3-qualifier".getBytes());

            @Override
            public Integer answer(final InvocationOnMock invocation) throws Throwable {
                return innerStream.read((byte[]) invocation.getArguments()[0],
                        (Integer) invocation.getArguments()[1], (Integer) invocation.getArguments()[2]);
            }
        });

        assertEquals(new DefaultArtifactVersion("1.2.3-qualifier"), retrieveVersion("casperjsRuntime", false));

        verify(stream).close();
    }

    @Test(expected = MojoFailureException.class)
    public void testRetrieveVersionFacingExWhileRunningCasper() throws Exception {
        when(runtime.exec("casperjsRuntime --version")).thenThrow(new IOException());

        try {
            retrieveVersion("casperjsRuntime", false);
        } finally {
            verifyNoMoreInteractions(stream);
        }
    }

    @Test(expected = MojoFailureException.class)
    public void testRetrieveVersionFacingExWhileParsingVersion() throws Exception {
        when(runtime.exec("casperjsRuntime --version")).thenReturn(process);
        when(process.getInputStream()).thenReturn(stream);
        when(stream.read(any(byte[].class), anyInt(), anyInt())).thenThrow(new IOException());

        try {
            retrieveVersion("casperjsRuntime", false);
        } finally {
            verify(stream).close();
        }
    }
}
