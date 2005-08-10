/*
 * Copyright 2004,2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.axis2.deployment.listener;

import org.apache.axis2.deployment.DeploymentConstants;
import org.apache.axis2.deployment.DeploymentEngine;
import org.apache.axis2.deployment.repository.util.WSInfoList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RepositoryListenerImpl implements RepositoryListener,
        DeploymentConstants {

    /**
     * to store curreently checking jars
     */
    private List currentJars;
    /**
     * Referance to a WSInfoList
     */
    private WSInfoList wsinfoList;

    /**
     * The parent directory of the modules and services directories
     * taht the listentner should listent
     */
    private String folderName;

    /**
     * This constructor take two argumnets folder name and referance to Deployment Engine
     * Fisrt it initilize the syetm , by loading all the modules in the /modules directory
     * and also create a WSInfoList to keep infor about available modules and services
     *
     * @param folderName    path to parent directory that the listener should listent
     * @param deploy_engine refearnce to engine registry  inorder to inform the updates
     */
    public RepositoryListenerImpl(String folderName,
                                  DeploymentEngine deploy_engine) {
        this.folderName = folderName;
        wsinfoList = new WSInfoList(deploy_engine);
        init();
    }

    /**
     * this method ask serachWS to serch for the folder to caheck
     * for updates
     */
    public void checkModules() {
        String modulepath = folderName + MODULE_PATH;
        String files[];
        currentJars = new ArrayList();
        File root = new File(modulepath);
        // adding the root folder to the vector
        currentJars.add(root);

        while (currentJars.size() > 0) {        // loop until empty
            File dir = (File) currentJars.get(0); // get first dir
            currentJars.remove(0);       // remove it
            files = dir.list();              // get list of files
            if (files == null) {
                continue;
            }
            for (int i = 0; i < files.length; i++) { // iterate
                File f = new File(dir, files[i]);
                if (f.isDirectory()) {        // see if it's a directory
                    currentJars.add(0, f);
                } // add dir to start of agenda
                else if (isModuleArchiveFile(f.getName())) {
                    wsinfoList.addWSInfoItem(f, MODULE);
                }
            }
        }
    }

    /**
     * this method ask serachWS to serch for the folder to caheck
     * for updates
     */
    public void checkServices() {
        String modulepath = folderName + SERVICE_PATH;
        searchWS(modulepath, SERVICE);
    }

    /**
     * call to update method of WSInfoList object
     */
    public void update() {
        //todo completet this
        // this call the update method of WSInfoList
        wsinfoList.update();
    }

    /**
     * First it call to initalize method of WSInfoList to initilizat that
     * then it call to checkModules to load all the module.jar s
     * and then it call to update() method inorder to update the Deployment engine and
     * engine regsitry
     */
    public void init() {
        wsinfoList.init();
        checkModules();
        checkServices();
        update();
    }

    /**
     * this is the actual method that is call from scheduler
     */
    public void startListent() {
        // checkModules();
        checkServices();
        update();
    }

    /**
     * This method is to search a given folder  for jar files
     * and added them to a list wich is in the WSInfolist class
     */
    private void searchWS(String folderName, int type) {
        String files[];
        currentJars = new ArrayList();
        File root = new File(folderName);
        // adding the root folder to the vector
        currentJars.add(root);

        while (currentJars.size() > 0) {        // loop until empty
            File dir = (File) currentJars.get(0); // get first dir
            currentJars.remove(0);       // remove it
            files = dir.list();              // get list of files
            if (files == null) {
                continue;
            }
            for (int i = 0; i < files.length; i++) { // iterate
                File f = new File(dir, files[i]);
                if (f.isDirectory()) {        // see if it's a directory
                    currentJars.add(0, f);
                } // add dir to start of agenda
                else if (isServiceArchiveFile(f.getName())) {
                    wsinfoList.addWSInfoItem(f, type);
                }
            }
        }
    }

    /**
     * to check whthere a given file is  a  jar file
     *
     * @param filename
     * @return
     */
    private boolean isServiceArchiveFile(String filename) {
        return ((filename.endsWith(".jar")) | (filename.endsWith(".aar")));
    }

    private boolean isModuleArchiveFile(String filename) {
        return ((filename.endsWith(".jar")) || (filename.endsWith(".mar")));
    }

}
