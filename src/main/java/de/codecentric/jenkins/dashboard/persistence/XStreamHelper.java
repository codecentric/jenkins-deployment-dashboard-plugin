/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.codecentric.jenkins.dashboard.persistence;

import de.codecentric.jenkins.dashboard.DashboardView;
import hudson.PluginWrapper;
import hudson.util.XStream2;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jenkins.model.Jenkins;

/**
 *
 * @author Andreas Houben
 */
public class XStreamHelper {
    
    private final static Logger LOGGER = Logger.getLogger(XStreamHelper.class.getName());
    
    private static XStreamHelper instance;
    private XStream2 xStream2;
    private FileOutputStream fileOutputStream;
    private String filePath;
    private String filename = "deploymentdashboard.xml";
    

    
    private XStreamHelper(){
        String jenkinsHome = Jenkins.getInstance().getRootDir().getAbsolutePath();
        filePath = jenkinsHome + "/" + filename;

       
        try {
            File f = new File(filePath);
            if (!f.exists()){
                try {
                    f.createNewFile();
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, "Could not create file {} for configuration.", f.getPath());
                }
            }
            fileOutputStream = new FileOutputStream(f);
        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "File not found: {0}", filePath.toString());
        }
        xStream2 = new XStream2();
    }
    
    public static XStreamHelper getInstance(){
        if (instance == null){
            instance = new XStreamHelper();
        }
        return instance;
    }
    
    public void toXML(Object o){
        try {
            xStream2.toXMLUTF8(o, fileOutputStream);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Could not write object to configuration file.");
        }
    }
    

    
}
