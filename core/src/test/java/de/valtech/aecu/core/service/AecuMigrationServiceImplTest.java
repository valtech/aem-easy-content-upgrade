package de.valtech.aecu.core.service;

import de.valtech.aecu.api.service.AecuException;
import de.valtech.aecu.api.service.AecuService;
import de.valtech.aecu.core.serviceuser.ServiceResourceResolverService;
import java.util.Collections;
import javax.jcr.Session;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AecuMigrationServiceImplTest {

    @InjectMocks
    @Spy
    private AecuMigrationServiceImpl aecuMigrationService;

    @Mock
    private AecuService aecuService;

    @Mock
    private ServiceResourceResolverService resolverService;

    @Mock
    private ResourceResolver resolver;

    @Mock
    private Session session;

    @BeforeEach
    public void setUp() throws Exception {
        when(resolverService.getServiceResourceResolver()).thenReturn(resolver);
        when(resolverService.getContentMigratorResourceResolver()).thenReturn(resolver);
        when(resolver.adaptTo(Session.class)).thenReturn(session);
    }

    @Test
    public void testMigration() throws Exception {
        doReturn(Collections.singletonList("dummy-script.groovy")).when(aecuService).getFiles(anyString());

        //mock our sessions permissions to simulate composite node store
        //other option is to use MockStatic for RuntimeHelper but this requires the mockito-inline maven dependency instead of mockito-core
        doReturn(true).when(session).hasPermission(anyString(), anyString());
        doReturn(false).when(session).hasCapability(anyString(), any(), any());

        aecuMigrationService.activate();
    }

    @Test
    public void testIncorrectNodeStore() throws Exception {
        doThrow(new AecuException("Should not be called")).when(aecuService).getFiles(anyString());

        //mock our sessions permissions to simulate NOT a composite node store
        //other option is to use MockStatic for RuntimeHelper but this requires the mockito-inline maven dependency instead of mockito-core
        doReturn(true).when(session).hasPermission(anyString(), anyString());
        doReturn(true).when(session).hasCapability(anyString(), any(), any());

        aecuMigrationService.activate();
    }

}
