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
    String PROPERTY_SLING_MESSAGE = "sling:message";
    String MIXIN_SLING_MESSAGE = "sling:Message";
    String PROPERTY_SLING_MESSAGE_ENTRY = "sling:MessageEntry";
    String MIXIN_TYPE_MIX_LANGUAGE = "mix:language";
    String PROPERTY_JCR_LANGUAGE = "jcr:language";

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
                    if (node.hasNode(key)) {
                        node.getNode(key).setProperty(PROPERTY_SLING_MESSAGE, value);
                    } else {
                        Node labelNode;
                        try {
                            labelNode = node.addNode(key, PROPERTY_SLING_MESSAGE_ENTRY);
                        } catch (RepositoryException e) {
                            throw new RuntimeException(e);
                        }
                        labelNode.addMixin(MIXIN_SLING_MESSAGE);
                        labelNode.setProperty(PROPERTY_SLING_MESSAGE, value);
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
                node.addMixin(MIXIN_TYPE_MIX_LANGUAGE);
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