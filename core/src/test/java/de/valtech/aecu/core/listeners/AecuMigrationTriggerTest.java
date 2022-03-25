package de.valtech.aecu.core.listeners;
// COMMENTED BECAUSE THIS PROJECT DOES NOT USE IO.WCM, JUNIT5, ....


//import com.google.common.collect.ImmutableMap;
//import de.valtech.aecu.api.service.AecuException;
//import de.valtech.aecu.core.jmx.AecuServiceMBean;
//import io.wcm.testing.mock.aem.junit5.AemContext;
//import io.wcm.testing.mock.aem.junit5.AemContextExtension;
//import java.lang.annotation.Annotation;
//import java.util.List;
//import org.apache.jackrabbit.JcrConstants;
//import org.apache.sling.api.resource.ResourceResolverFactory;
//import org.apache.sling.api.resource.observation.ResourceChange;
//import org.apache.sling.api.resource.observation.ResourceChange.ChangeType;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.osgi.framework.Constants;
//
//import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.doReturn;
//import static org.mockito.Mockito.doThrow;
//import static org.mockito.Mockito.mock;
//
//@ExtendWith(AemContextExtension.class)
//@ExtendWith(MockitoExtension.class)
//public class AecuMigrationTriggerTest {
//
//    private final AemContext aemContext = new AemContext();
//
//    @Mock
//    private AecuServiceMBean aecuServiceMBean;
//
//    private AecuMigrationTrigger aecuMigrationTrigger;
//
//    @BeforeEach
//    public void setUp() throws Exception {
//        aemContext.registerService(AecuServiceMBean.class, aecuServiceMBean);
//        aecuMigrationTrigger = aemContext.registerInjectActivateService(AecuMigrationTrigger.class);
//    }
//
//    @Test
//    public void testInvalidTriggerName() {
//        String triggerPath = AecuMigrationTrigger.TRIGGER_LOCATION + "/invalid-trigger-name"; //incorrect name
//        aemContext.create().resource(triggerPath, ImmutableMap.<String, Object>builder()
//                .put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED)
//                .build());
//        assertFalse(aecuMigrationTrigger.isMigrationTrigger(
//                new ResourceChange(ChangeType.ADDED, triggerPath, false), aemContext.resourceResolver()));
//    }
//
//    @Test
//    public void testInvalidTriggerType() {
//        String triggerPath = AecuMigrationTrigger.TRIGGER_LOCATION + "/migration-trigger";
//        aemContext.create().resource(triggerPath, ImmutableMap.<String, Object>builder()
//                .put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_FILE) //incorrect type
//                .build());
//        assertFalse(aecuMigrationTrigger.isMigrationTrigger(
//                new ResourceChange(ChangeType.ADDED, triggerPath, false), aemContext.resourceResolver()));
//    }
//
//    @Test
//    public void testValidTrigger() {
//        String triggerPath = AecuMigrationTrigger.TRIGGER_LOCATION + "/migration-trigger";
//        aemContext.create().resource(triggerPath, ImmutableMap.<String, Object>builder()
//                .put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED)
//                .build());
//        assertTrue(aecuMigrationTrigger.isMigrationTrigger(
//                new ResourceChange(ChangeType.ADDED, triggerPath, false), aemContext.resourceResolver()));
//    }
//
//    @Test
//    public void testGetTriggerResource() {
//        String triggerPath = AecuMigrationTrigger.TRIGGER_LOCATION + "/migration-trigger";
//        assertNull(aecuMigrationTrigger.getTriggerResource(aemContext.resourceResolver()));
//        aemContext.create().resource(triggerPath, ImmutableMap.<String, Object>builder()
//                .put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED)
//                .build());
//        assertNotNull(aecuMigrationTrigger.getTriggerResource(aemContext.resourceResolver()));
//    }
//
//    @Test
//    public void testDeleteTrigger() {
//        String triggerPath = AecuMigrationTrigger.TRIGGER_LOCATION + "/migration-trigger";
//        aemContext.create().resource(triggerPath, ImmutableMap.<String, Object>builder()
//                .put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED)
//                .build());
//        assertNotNull(aecuMigrationTrigger.getTriggerResource(aemContext.resourceResolver()));
//
//        aecuMigrationTrigger.deleteTrigger(aemContext.resourceResolver());
//        assertNull(aemContext.resourceResolver().getResource(triggerPath));
//    }
//
//    @Test
//    public void testMigration() throws Exception {
//        String message = "AecuServiceMBean.executeWithHistory mock";
//        doThrow(new AecuException(message)).when(aecuServiceMBean).executeWithHistory(anyString());
//
//        //no scripts, no mock error thrown
//        assertDoesNotThrow(() -> aecuMigrationTrigger.startAecuMigration(aemContext.resourceResolver()));
//
//        doReturn(List.of("mock-script.groovy")).when(aecuServiceMBean).getFiles(anyString());
//
//        String triggerPath = AecuMigrationTrigger.TRIGGER_LOCATION + "/migration-trigger";
//        aemContext.create().resource(triggerPath, ImmutableMap.<String, Object>builder()
//                .put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED)
//                .build());
//        assertNotNull(aecuMigrationTrigger.getTriggerResource(aemContext.resourceResolver()));
//
//        assertTrue(aecuMigrationTrigger.isMigrationTrigger(
//                new ResourceChange(ChangeType.ADDED, triggerPath, false), aemContext.resourceResolver()));
//
//        aecuMigrationTrigger.startAecuMigration(aemContext.resourceResolver());
//
//        assertNull(aemContext.resourceResolver().getResource(triggerPath));
//    }
//
//    @Test
//    public void testMigrationOnChange() throws Exception {
//        registerAemContextResourceResolverFactory();
//
//        //mock an error while executing when there are no scripts
//        String message = "AecuServiceMBean.executeWithHistory mock";
//        doThrow(new AecuException(message)).when(aecuServiceMBean).executeWithHistory(anyString());
//
//        //no scripts, no mock error thrown
//        assertDoesNotThrow(() -> aecuMigrationTrigger.startAecuMigration(aemContext.resourceResolver()));
//
//        doReturn(List.of("mock-script.groovy")).when(aecuServiceMBean).getFiles(anyString());
//
//        String triggerPath = AecuMigrationTrigger.TRIGGER_LOCATION + "/migration-trigger";
//        aemContext.create().resource(triggerPath, ImmutableMap.<String, Object>builder()
//                .put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED)
//                .build());
//        aecuMigrationTrigger.onChange(
//                List.of(new ResourceChange(ChangeType.ADDED, triggerPath, false)));
//
//        assertNull(aemContext.resourceResolver().getResource(triggerPath));
//    }
//
//    @Test
//    public void testMigrationOnActivate() throws Exception {
//        registerAemContextResourceResolverFactory();
//
//        //mock an error while executing when there are no scripts
//        String message = "AecuServiceMBean.executeWithHistory mock";
//        doThrow(new AecuException(message)).when(aecuServiceMBean).executeWithHistory(anyString());
//
//        //no scripts, no mock error thrown
//        assertDoesNotThrow(() -> aecuMigrationTrigger.startAecuMigration(aemContext.resourceResolver()));
//
//        doReturn(List.of("mock-script.groovy")).when(aecuServiceMBean).getFiles(anyString());
//
//        String triggerPath = AecuMigrationTrigger.TRIGGER_LOCATION + "/migration-trigger";
//        aemContext.create().resource(triggerPath, ImmutableMap.<String, Object>builder()
//                .put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED)
//                .build());
//        aecuMigrationTrigger.activate(new AecuMigrationTrigger.Config() {
//            @Override
//            public String triggerConfig() {
//                return "migration-trigger";
//            }
//
//            @Override
//            public Class<? extends Annotation> annotationType() {
//                return AecuMigrationTrigger.Config.class;
//            }
//        });
//
//        assertNull(aemContext.resourceResolver().getResource(triggerPath));
//    }
//
//    /**
//     * Make sure the resourceResolvers are correctly started when running a test where the resourceResolvers cannot be passed through
//     * <ol>
//     *     <li>we make sure the aemContext resourceResolver is used throughout the code by providing a mock method</li>
//     *     <li>we register the ResourceResolverFactory with a higher service ranking so it is picked up</li>
//     *     <li>we re-register the {@link AecuMigrationTrigger} so it uses our mocked {@link ResourceResolverFactory}</li>
//     * </ol>
//     * @throws Exception
//     */
//    private void registerAemContextResourceResolverFactory() throws Exception {
//        ResourceResolverFactory resourceResolverFactory = mock(ResourceResolverFactory.class);
//        aemContext.registerService(ResourceResolverFactory.class, resourceResolverFactory, Constants.SERVICE_RANKING, 100);
//        doReturn(aemContext.resourceResolver()).when(resourceResolverFactory).getServiceResourceResolver(any());
//
//        aecuMigrationTrigger = aemContext.registerInjectActivateService(AecuMigrationTrigger.class, Constants.SERVICE_RANKING, 101);
//    }
//
//}

