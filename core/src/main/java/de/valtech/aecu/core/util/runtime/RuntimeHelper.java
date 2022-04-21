package de.valtech.aecu.core.util.runtime;

import javax.jcr.Node;
import javax.jcr.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CLASS IS BASED ON RuntimeHelper from ACTOOL.  Used their way of determining if a composite node store is being used.
 * */
public final class RuntimeHelper {
    public static final Logger LOG = LoggerFactory.getLogger(RuntimeHelper.class);

    private RuntimeHelper(){
        //static methods only
    }

    /**
     * If a user has permissions on "/" but still does not have the capability to create nodes under "/apps",
     * it is assumed to be a composite node store.
     * @param session
     * @return
     */
    public static boolean isCompositeNodeStore(Session session) {
        try {
            String pathToCheck = "/apps";
            Node appsNode = session.getNode(pathToCheck);

            boolean hasPermission = session.hasPermission("/", Session.ACTION_SET_PROPERTY);
            if(!hasPermission) {
                // this can be ok for multitenancy cases that run with user of package installation (via install hook)
                LOG.info("Running with a session (userID: "+session.getUserID()+") that does not have permissions '"+ Session.ACTION_SET_PROPERTY+"' at "+pathToCheck);
            }

            // see https://issues.apache.org/jira/browse/OAK-6563
            boolean hasCapability = session.hasCapability("addNode", appsNode, new Object[] { "nt:folder" });

            return hasPermission && !hasCapability;
        } catch(Exception e) {
            throw new IllegalStateException("Could not check if session is connected to a composite node store: "+e, e);
        }
    }

}

