package de.valtech.aecu.core.groovy.console.bindings.actions.resource;

import com.mongodb.client.model.EstimatedDocumentCountOptions;
import de.valtech.aecu.api.service.AecuException;
import de.valtech.aecu.core.groovy.console.bindings.actions.Action;
import jdk.internal.org.jline.utils.Log;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Iterator;
import java.util.Map;

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
    private Map<String,String>[] map;

    private String[] i18nPaths;
    private static final Logger logger = LoggerFactory.getLogger(CreateLabel.class);

    public CreateLabel(@Nonnull Map<String, String>[] map, String[] i18nPaths){
        this.map = map;
        this.i18nPaths = i18nPaths;
    }



    @Override
    public String doAction(@Nonnull Resource resource) throws PersistenceException, AecuException {
        Session session = resource.getResourceResolver().adaptTo(Session.class);

        for(String resourcePath : i18nPaths){
            logger.info("logger werkt yeey");
            try {
                Node i18nNode = session.getNode(resourcePath);
                for (Map<String, String> labels : map) {
                    Iterator<Map.Entry<String, String>> translations = labels.entrySet().iterator();
                    String labelName = translations.next().getValue();

                    while (translations.hasNext()) {
                        Map.Entry<String, String> translation = translations.next();
                        Node node = getLanguageNode(i18nNode, translation.getKey());
                        if (node != null) {
                            try {
                                if (node.hasNode(labelName)) {
                                    node.getNode(labelName).setProperty(PROPERTY_SLING_MESSAGE, translation.getValue());
                                } else {
                                    Node labelNode = null;
                                    try {
                                        labelNode = node.addNode(labelName, FOLDER_PRIMARY_TYPE);
                                    } catch (RepositoryException e) {
                                        throw new RuntimeException(e);
                                    }
                                    labelNode.addMixin(MIXIN_SLING_MESSAGE);
                                    labelNode.setProperty(PROPERTY_SLING_MESSAGE, translation.getValue());
                                }
                            } catch (RepositoryException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            } catch (RepositoryException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            session.save();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        } finally {
            if (session != null) {
                session.logout();
            }
        }

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