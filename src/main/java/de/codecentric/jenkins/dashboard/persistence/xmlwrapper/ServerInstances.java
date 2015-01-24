package de.codecentric.jenkins.dashboard.persistence.xmlwrapper;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import de.codecentric.jenkins.dashboard.persistence.ServerInstance;

/**
 * 
 * @author Andreas Houben
 */
@XStreamAlias("instances")
public class ServerInstances {

    public ServerInstances() {
        instances = new ArrayList<>();
    }

    @XStreamImplicit(itemFieldName = "instance")
    private List<ServerInstance> instances;

    public void add(ServerInstance instance) {
        instances.add(instance);
    }

    public ServerInstance get(int i) {
        return instances.get(i);
    }

    public int size() {
        return instances.size();
    }

}
