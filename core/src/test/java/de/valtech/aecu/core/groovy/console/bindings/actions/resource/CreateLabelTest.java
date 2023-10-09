package de.valtech.aecu.core.groovy.console.bindings.actions.resource;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.annotation.Nonnull;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CreateLabelTest {

    @Mock
    private Resource resource;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private Session session;

    @Mock
    private Node i18nNode;

    @Mock
    private Node labelNode;

    @Mock
    private Node node;

    @BeforeEach
    void setUp() throws RepositoryException {
        MockitoAnnotations.openMocks(this);
        when(resource.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
        when(session.getNode(any())).thenReturn(i18nNode);
        when(i18nNode.getNode(anyString())).thenReturn(node);
        when(i18nNode.addNode(anyString())).thenReturn(node);
        when(node.addNode(anyString(),anyString())).thenReturn(labelNode);
    }

    @Test
    void testDoAction() throws Exception {
        CreateLabel createLabel = new CreateLabel("en", "greeting", "Hello");

        String result = createLabel.doAction(resource);

        assertEquals("Label created in Language node en for key: greeting and value: Hello", result);
    }

    @Test
    void testDoActionExistingNode() throws Exception {
        CreateLabel createLabel = new CreateLabel("en", "greeting", "Hello");

        String result = createLabel.doAction(resource);

        assertNotNull(result);
        assertEquals("Label created in Language node en for key: greeting and value: Hello", result);
    }

    @Test
    void testGetLanguageNode() {
        CreateLabel createLabel = new CreateLabel("en", "greeting", "Hello");

        Node result = createLabel.getLanguageNode(i18nNode, "en");

        assertNotNull(result);
        assertEquals(node, result);
    }

    @Test
    void testGetLanguageNodeExistingNode() throws Exception {
        CreateLabel createLabel = new CreateLabel("en", "greeting", "Hello");

        Node existingNode = mock(Node.class);
        when(i18nNode.hasNode(anyString())).thenReturn(true);
        when(i18nNode.getNode(anyString())).thenReturn(existingNode);

        Node result = createLabel.getLanguageNode(i18nNode, "en");

        assertNotNull(result);
        assertEquals(existingNode, result);
    }

    @Test
    void testGetLanguageNodeException() throws Exception {
        CreateLabel createLabel = new CreateLabel("en", "greeting", "Hello");

        when(i18nNode.addNode(anyString())).thenThrow(new RepositoryException("Simulated exception"));

        assertThrows(RuntimeException.class, () -> createLabel.getLanguageNode(i18nNode, "en"));
    }
}