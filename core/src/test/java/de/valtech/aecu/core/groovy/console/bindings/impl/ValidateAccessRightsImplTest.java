package de.valtech.aecu.core.groovy.console.bindings.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.condition.OS.WINDOWS;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintStream;
import java.security.Principal;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.security.Privilege;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlManager;
import org.apache.jackrabbit.api.security.principal.PrincipalIterator;
import org.apache.jackrabbit.api.security.principal.PrincipalManager;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.Replicator;
import be.orbinson.aem.groovy.console.api.context.ScriptContext;

/**
 * Tests ValidateAccessRightsImpl
 * Few tests are marked with DisabledOnOs(windows) as they fail because of different linebreak-handling.
 * Windows Users are encouraged to use WSL.
 * @author Roland Gruber
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ValidateAccessRightsImplTest {

    private static final String TESTPATH = "/test";

    private static final String TESTGROUP = "testgroup";

    @Mock
    private ResourceResolverFactory resourceResolverFactory;

    @Mock
    private ResourceResolver resolver;

    @Mock
    private JackrabbitSession adminSession;

    @Mock
    private JackrabbitAccessControlManager aclManager;

    @Mock
    private Privilege privilege;

    @Mock
    private Replicator replicator;

    @Mock
    private ScriptContext scriptContext;

    @Mock
    private PrintStream stream;

    @Mock
    private UserManager userManager;

    @Mock
    private Group group;

    @Mock
    private Resource resource;

    @Mock
    private PrincipalManager principalManager;

    @Mock
    private PrincipalIterator principalIterator;

    @Mock
    private Principal principal;

    @Mock
    private Node node;

    @Mock
    private NodeType nodeType;

    private ValidateAccessRightsImpl validateRights;

    @BeforeEach
    public void setup() throws RepositoryException {
        when(resolver.adaptTo(Session.class)).thenReturn(adminSession);
        when(adminSession.getAccessControlManager()).thenReturn(aclManager);
        when(aclManager.privilegeFromName(Mockito.anyString())).thenReturn(privilege);
        when(scriptContext.getPrintStream()).thenReturn(stream);
        when(resolver.adaptTo(UserManager.class)).thenReturn(userManager);
        when(userManager.getAuthorizable(TESTGROUP)).thenReturn(group);
        when(group.isGroup()).thenReturn(true);
        when(resolver.getResource(TESTPATH)).thenReturn(resource);
        when(adminSession.getPrincipalManager()).thenReturn(principalManager);
        when(principalManager.getGroupMembership(Mockito.any())).thenReturn(principalIterator);
        when(principalIterator.hasNext()).thenReturn(true, false);
        when(principalIterator.nextPrincipal()).thenReturn(principal);
        when(aclManager.getPrivileges(Mockito.anyString(), Mockito.any())).thenReturn(new Privilege[0]);
        when(resource.getPath()).thenReturn(TESTPATH);
        when(adminSession.getNode(TESTPATH)).thenReturn(node);
        when(node.getPrimaryNodeType()).thenReturn(nodeType);
        when(nodeType.getChildNodeDefinitions()).thenReturn(new NodeDefinition[0]);
        when(group.getID()).thenReturn(TESTGROUP);

        validateRights = new ValidateAccessRightsImpl(resourceResolverFactory, resolver, replicator, scriptContext);
    }

    @Test
    @DisabledOnOs(WINDOWS)
    public void validate() {
        validateRights.forPaths(new String[] {TESTPATH});
        validateRights.forGroups(new String[] {TESTGROUP});
        assertNotNull(validateRights.cannotRead());
        validateRights.validate();

        verify(stream).append("\n┌─────────┬─────┬───────────────┐\n" + "│Group    │Path │Rights         │\n"
                + "├─────────┼─────┼───────────────┤\n" + "│testgroup│/test│OK: Cannot Read│\n"
                + "└─────────┴─────┴───────────────┘\n\n");
    }

    @Test
    @DisabledOnOs(WINDOWS)
    public void validate_failedCheck() {
        validateRights.forPaths(new String[] {TESTPATH});
        validateRights.forGroups(new String[] {TESTGROUP});
        assertNotNull(validateRights.canRead());
        validateRights.validate();

        verify(stream).append("\n┌─────────┬─────┬───────────────────────┐\n" + "│Group    │Path │Rights                 │\n"
                + "├─────────┼─────┼───────────────────────┤\n" + "│testgroup│/test│FAIL: Read             │\n"
                + "═════════════════════════════════════════\n" + "│             Issue details             │\n"
                + "═════════════════════════════════════════\n" + "│Group    │Path │Issue                  │\n"
                + "├─────────┼─────┼───────────────────────┤\n" + "│testgroup│/test│Read: Wrong permissions│\n"
                + "└─────────┴─────┴───────────────────────┘\n" + "\n");
    }

    @Test
    @DisabledOnOs(WINDOWS)
    public void simulate() {
        assertNotNull(validateRights.cannotRead());
        validateRights.simulate();

        verify(stream).append(
                "\n┌─────┬────┬──────┐\n" + "│Group│Path│Rights│\n" + "├┬┬┬┬┬┼┬┬┬┬┼┬┬┬┬┬┬┤\n" + "└┴┴┴┴┴┴┴┴┴┴┴┴┴┴┴┴┴┘\n\n");
    }

    @Test
    public void testOtherMethods() {
        assertNotNull(validateRights.canCreate());
        assertNotNull(validateRights.canCreatePage("test"));
        assertNotNull(validateRights.canDelete());
        assertNotNull(validateRights.canDeletePage());
        assertNotNull(validateRights.canModify());
        assertNotNull(validateRights.canModifyPage());
        assertNotNull(validateRights.canRead());
        assertNotNull(validateRights.canReadAcl());
        assertNotNull(validateRights.canReadPage());
        assertNotNull(validateRights.canReplicate());
        assertNotNull(validateRights.canReplicatePage());
        assertNotNull(validateRights.canReplicatePage(ReplicationActionType.ACTIVATE));
        assertNotNull(validateRights.canWriteAcl());
        assertNotNull(validateRights.cannotCreate());
        assertNotNull(validateRights.cannotCreatePage("test"));
        assertNotNull(validateRights.cannotDelete());
        assertNotNull(validateRights.cannotDeletePage());
        assertNotNull(validateRights.cannotModify());
        assertNotNull(validateRights.cannotModifyPage());
        assertNotNull(validateRights.cannotRead());
        assertNotNull(validateRights.cannotReadPage());
        assertNotNull(validateRights.cannotReplicate());
        assertNotNull(validateRights.cannotReplicatePage());
        assertNotNull(validateRights.cannotReplicatePage(ReplicationActionType.ACTIVATE));
        assertNotNull(validateRights.cannotWriteAcl());
    }

}
