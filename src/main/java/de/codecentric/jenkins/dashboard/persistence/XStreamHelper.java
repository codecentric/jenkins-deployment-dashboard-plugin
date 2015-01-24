package de.codecentric.jenkins.dashboard.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jenkins.model.Jenkins;

import com.thoughtworks.xstream.XStream;

import de.codecentric.jenkins.dashboard.DashboardView;
import de.codecentric.jenkins.dashboard.persistence.converter.DateTimeConverter;
import de.codecentric.jenkins.dashboard.persistence.xmlwrapper.ServerInstances;

/**
 * @author Andreas Houben
 */
public class XStreamHelper {

    private final static Logger LOGGER = Logger.getLogger(DashboardView.class.getName());

    private static XStreamHelper instance;
    private final File f;
    private final String filename = "deploymentdashboard.xml";
    private XStream xStream;
    private String filePath;

    private XStreamHelper() {
        String jenkinsHome = Jenkins.getInstance().getRootDir().getAbsolutePath();
        filePath = jenkinsHome + "/plugins/jenkins-deployment-dashboard";
        filePath = filePath + "/" + filename;

        f = new File(filePath);
        if (!f.exists()) {
            try {
                f.getParentFile().mkdirs();
                f.createNewFile();
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Could not create file {} for configuration.", f.getPath());
            }
        }
    }

    public static XStreamHelper getInstance() {
        if (instance == null) {
            instance = new XStreamHelper();
        }
        return instance;
    }

    private void writeXML(Object o) {
        try {
            FileOutputStream fos = new FileOutputStream(f);
            xStream.toXML(o, fos);
            fos.close();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Could not write object to configuration file.");
        }
    }

    private Object readXML() {
        Object o = null;
        try {
            FileInputStream fis = new FileInputStream(f);
            o = xStream.fromXML(fis);
            fis.close();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Could not read object from configuration file.");
        }
        return o;
    }

    // methods for ServerInstances
    private void prepareXStreamForServerInstances() {
        xStream = new XStream();
        xStream.registerConverter(new DateTimeConverter());
        xStream.processAnnotations(new Class[] { ServerInstances.class, ServerInstances.class });
    }

    public void toXML(ServerInstances instances) {
        prepareXStreamForServerInstances();
        writeXML(instances);
    }

    public ServerInstances serverInstancesfromXML() {
        prepareXStreamForServerInstances();
        return (ServerInstances) readXML();
    }

}
