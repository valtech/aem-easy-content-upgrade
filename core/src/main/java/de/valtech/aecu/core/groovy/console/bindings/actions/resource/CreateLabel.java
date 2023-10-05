package de.valtech.aecu.core.groovy.console.bindings.actions.resource;

import de.valtech.aecu.api.service.AecuException;
import de.valtech.aecu.core.groovy.console.bindings.actions.Action;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 *@author Ian Smets
 *@author Wietse Vandeput
**/
public class CreateLabel implements Action {
    private final String language;
    private final String key;
    private final String value;

    public CreateLabel(String language, String key, String value){
        this.language = language;
        this.key = key;
        this.value = value;
    }

    @Override
    public String doAction(@Nonnull Resource resource) throws PersistenceException, AecuException {
        Session session = resource.getResourceResolver().adaptTo(Session.class);
        try {
            Node i18nNode = session.getNode(resource.getPath());
            Node node = getLanguageNode(i18nNode, language);
            if (node != null) {
                try {
                    String PROPERTY_SLING_MESSAGE = "sling:message";
                    if (node.hasNode(key)) {
                        node.getNode(key).setProperty(PROPERTY_SLING_MESSAGE, value);
                    } else {
                        Node labelNode;
                        try {
                            labelNode = node.addNode(key, "nt:unstructured");
                        } catch (RepositoryException e) {
                            throw new RuntimeException(e);
                        }
                        String MIXIN_SLING_MESSAGE = "sling:Message";
                        labelNode.addMixin(MIXIN_SLING_MESSAGE);
                        labelNode.setProperty(PROPERTY_SLING_MESSAGE, value);
                        labelNode.setProperty("sling:MessageEntry", key);
                    }
                } catch (RepositoryException e) {
                    throw new RuntimeException(e);
                }
            } else return "No i18n node exists here.";

        } catch (RepositoryException ex) {
            throw new RuntimeException(ex);
        }
        return "Label created in Language node " + language + " for key: " + key + " and value: " + value;
    }

    protected Node getLanguageNode(Node i18nNode, String language) {
        Node node = null;
        try {
            if (!i18nNode.hasNode(language)) {
                node = i18nNode.addNode(language);
                String MIXIN_TYPE_MIX_LANGUAGE = "mix:language";
                node.addMixin(MIXIN_TYPE_MIX_LANGUAGE);
                String PROPERTY_JCR_LANGUAGE = "jcr:language";
                node.setProperty(PROPERTY_JCR_LANGUAGE, language);
            } else {
                node = i18nNode.getNode(language);
            }
        } catch (RepositoryException e ) {
            throw new RuntimeException(e);
        }
        return node;
    }

}