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

    private final String MIXIN_SLING_MESSAGE = "sling:Message";
    private final String PROPERTY_SLING_MESSAGE = "sling:message";
    private final String MIXIN_TYPE_MIX_LANGUAGE = "mix:language";
    private final String PROPERTY_JCR_LANGUAGE = "jcr:language";
    private final String FOLDER_PRIMARY_TYPE = "nt:folder";
    private String language;
    private String key;
    private String value;

    public CreateLabel(String language, String key, String value){
        this.language = language;
        this.key = key;
        this.value = value;
    }





    @Override
    public String doAction(@Nonnull Resource resource) throws PersistenceException, AecuException {
        Session session = resource.getResourceResolver().adaptTo(Session.class);

        Node i18nNode = null;
        try {
            i18nNode = session.getNode(resource.getPath());
            Node node = getLanguageNode(i18nNode, language);
            if (node != null) {
                try {
                    if (node.hasNode(key)) {
                        node.getNode(key).setProperty(PROPERTY_SLING_MESSAGE, value);
                    } else {
                        Node labelNode = null;
                        try {
                            labelNode = node.addNode(key, "nt:unstructured");
                        } catch (RepositoryException e) {
                            throw new RuntimeException(e);
                        }
                        labelNode.addMixin(MIXIN_SLING_MESSAGE);
                        labelNode.setProperty(PROPERTY_SLING_MESSAGE, value);
                        labelNode.setProperty("sling:MessageEntry", key);
                    }
                } catch (RepositoryException e) {
                    throw new RuntimeException(e);
                }
            }

        } catch (RepositoryException ex) {
            throw new RuntimeException(ex);
        }
//        finally {
//            if (session != null) {
//                session.logout();
//            }
//        }
        return "Bibbedy, bobbedy, boe";
    }


    private Node getLanguageNode(Node i18nNode,String language) {
        Node node;
        try {
            if (!i18nNode.hasNode(language)) {
                node = i18nNode.addNode(language);
                node.addMixin(MIXIN_TYPE_MIX_LANGUAGE);
                node.setProperty(PROPERTY_JCR_LANGUAGE, language);
            } else {
                node = i18nNode.getNode(language);
            }
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        return node;
    }

}