/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

package org.apache.axis.encoding;

import org.apache.axis.Constants;
import org.apache.axis.utils.Messages;

import java.util.HashMap;

/**
 * <p>
 * The TypeMappingRegistry keeps track of the individual TypeMappings.
 * </p>
 * <p>
 * The TypeMappingRegistry for axis contains a default type mapping
 * that is set for either SOAP 1.1 or SOAP 1.2
 * The default type mapping is a singleton used for the entire
 * runtime and should not have anything new registered in it.
 * </p>
 * <p>
 * Instead the new TypeMappings for the deploy and service are
 * made in a separate TypeMapping which is identified by
 * the soap encoding.  These new TypeMappings delegate back to 
 * the default type mapping when information is not found.
 * </p>
 * <p>
 * So logically we have:
 * <pre>
 *         TMR
 *         | |  
 *         | +---------------> DefaultTM 
 *         |                      ^
 *         |                      |
 *         +----> TM --delegate---+
 * </pre>
 *
 * But in the implementation, the TMR references
 * "delegate" TypeMappings (TM') which then reference the actual TM's
 * </p>
 * <p>
 * So the picture is really:
 * <pre>
 *         TMR
 *         | |  
 *         | +-----------TM'------> DefaultTM 
 *         |              ^
 *         |              |
 *         +-TM'-> TM ----+
 * </pre>
 *
 * This extra indirection is necessary because the user may want to 
 * change the default type mapping.  In such cases, the TMR
 * just needs to adjust the TM' for the DefaultTM, and all of the
 * other TMs will properly delegate to the new one.  Here's the picture:
 * <pre>
 *         TMR
 *         | |  
 *         | +-----------TM'--+     DefaultTM 
 *         |              ^   |
 *         |              |   +---> New User Defined Default TM
 *         +-TM'-> TM ----+
 * </pre>
 *
 * The other reason that it is necessary is when a deploy
 * has a TMR, and then TMR's are defined for the individual services
 * in such cases the delegate() method is invoked on the service
 * to delegate to the deploy TMR
 * <pre>
 *       Deploy TMR
 *         | |  
 *         | +-----------TM'------> DefaultTM 
 *         |              ^
 *         |              |
 *         +-TM'-> TM ----+
 *
 *       Service TMR
 *         | |  
 *         | +-----------TM'------> DefaultTM 
 *         |              ^
 *         |              |
 *         +-TM'-> TM ----+
 *
 *    ServiceTMR.delegate(DeployTMR)
 *
 *       Deploy TMR
 *         | |  
 *         | +------------TM'------> DefaultTM 
 *         |              ^ ^ 
 *         |              | |
 *         +-TM'-> TM ----+ |
 *           ^              |
 *   +-------+              |
 *   |                      |
 *   |   Service TMR        |
 *   |     | |              |
 *   |     | +----------TM'-+               
 *   |     |              
 *   |     |              
 *   |     +-TM'-> TM +
 *   |                |
 *   +----------------+
 * </pre>
 *
 * So now the service uses the DefaultTM of the Deploy TMR, and
 * the Service TM properly delegates to the deploy's TM.  And
 * if either the deploy defaultTM or TMs change, the links are not broken.
 * </p>
 * 
 * @author James Snell (jasnell@us.ibm.com)
 * @author Sam Ruby (rubys@us.ibm.com)
 * Re-written for JAX-RPC Compliance by
 * @author Rich Scheuerle (scheu@us.ibm.com
 */
public class TypeMappingRegistryImpl implements TypeMappingRegistry { 
    
    private HashMap mapTM;          // Type Mappings keyed with Namespace URI
    private TypeMapping defaultDelTM;  // Delegate to default Type Mapping 

    /**
     * Construct TypeMappingRegistry
     * @param tm
     */ 
    public TypeMappingRegistryImpl(TypeMapping tm) {
        mapTM = new HashMap();
        defaultDelTM = new TypeMappingDelegate(tm);
        register(Constants.URI_SOAP11_ENC, new DefaultSOAPEncodingTypeMappingImpl());
    }

    /**
     * Construct TypeMappingRegistry
     */
    public TypeMappingRegistryImpl() {
        mapTM = new HashMap();
        defaultDelTM = 
                new TypeMappingDelegate(DefaultTypeMappingImpl.getSingleton());
        register(Constants.URI_SOAP11_ENC, new DefaultSOAPEncodingTypeMappingImpl());
    }
    
    /**
     * delegate
     *
     * Changes the contained type mappings to delegate to 
     * their corresponding types in the secondary TMR.
     */
    public void delegate(TypeMappingRegistry secondaryTMR) {

        if (secondaryTMR == null || secondaryTMR == this) {
            return;
        }
        String[]  keys = secondaryTMR.getRegisteredEncodingStyleURIs();
//        String[]  keys = null;
        if (keys != null) {
            for(int i=0; i < keys.length; i++) {
                try {
                    String nsURI = keys[i];
                    TypeMapping tm = (TypeMapping) getTypeMapping(nsURI);
                    if (tm == null || tm == getDefaultTypeMapping() ) {
                        tm = (TypeMapping) createTypeMapping();
                        tm.setSupportedEncodings(new String[] { nsURI });
                        register(nsURI, tm);
                    }
                    
                    if (tm != null) {
                        // Get the secondaryTMR's TM'
                        TypeMapping del = (TypeMapping)
                            ((TypeMappingRegistryImpl)
                             secondaryTMR).mapTM.get(nsURI);
                        tm.setDelegate(del);
                    }
                    
                } catch (Exception e) {
                }
            }
        }
        // Change our defaultDelTM to delegate to the one in 
        // the secondaryTMR
        if (defaultDelTM != null) {
            defaultDelTM.setDelegate(
            ((TypeMappingRegistryImpl)secondaryTMR).defaultDelTM);
        }
        
    }            
            


    /********* JAX-RPC Compliant Method Definitions *****************/
    
    /**
     * The method register adds a TypeMapping instance for a specific 
     * namespace                 
     *
     * @param namespaceURI 
     * @param mapping - TypeMapping for specific namespaces
     *
     * @return Previous TypeMapping associated with the specified namespaceURI,
     * or null if there was no TypeMapping associated with the specified namespaceURI
     *
     */
    public javax.xml.rpc.encoding.TypeMapping register(String namespaceURI,
                         javax.xml.rpc.encoding.TypeMapping mapping) {
//        namespaceURI = "";
        if (mapping == null || 
            !(mapping instanceof TypeMapping)) {
            throw new IllegalArgumentException(
                    Messages.getMessage("badTypeMapping"));
        } 
        if (namespaceURI == null) {
            throw new java.lang.IllegalArgumentException(
                    Messages.getMessage("nullNamespaceURI"));
        }
        // Get or create a TypeMappingDelegate and set it to 
        // delegate to the new mapping.
        TypeMappingDelegate del = (TypeMappingDelegate)
            mapTM.get(namespaceURI);
        if (del == null) {
            del = new TypeMappingDelegate((TypeMapping) mapping);
            mapTM.put(namespaceURI, del);
            ((TypeMapping)mapping).setDelegate(defaultDelTM.getDelegate());
        } else {
            ((TypeMapping)mapping).setDelegate(del.getDelegate());
            del.setDelegate((TypeMapping) mapping);
        }
        return null; // Needs works
    }
    
    /**
     * The method register adds a default TypeMapping instance.  If a specific
     * TypeMapping is not found, the default TypeMapping is used.  
     *
     * @param mapping - TypeMapping for specific type namespaces
     *
     * java.lang.IllegalArgumentException - 
     * if an invalid type mapping is specified or the delegate is already set
     */
    public void registerDefault(javax.xml.rpc.encoding.TypeMapping mapping) {
        if (mapping == null ||
            !(mapping instanceof TypeMapping)) {
            throw new IllegalArgumentException(
                    Messages.getMessage("badTypeMapping"));
        }

        /* Don't allow this call after the delegate() method since
         * the TMR's TypeMappings will be using the default type mapping
         * of the secondary TMR.
         */
        if (defaultDelTM.getDelegate() instanceof TypeMappingDelegate) {
            throw new IllegalArgumentException(
                    Messages.getMessage("defaultTypeMappingSet"));
        }

        defaultDelTM.setDelegate((TypeMapping) mapping);
    }

    /**
     * Set up the default type mapping (and the SOAP encoding type mappings)
     * as per the passed "version" option.
     *
     * @param version
     */
    public void doRegisterFromVersion(String version) {
        if (version == null || version.equals("1.0") || version.equals("1.2")) {
            // Do nothing, just register SOAPENC mapping
        } else if (version.equals("1.1")) {
            // Do nothing, no SOAPENC mapping
            return;
        } else if (version.equals("1.3")) {
            // Reset the default TM to the JAXRPC version, then register SOAPENC
            TypeMapping tm = DefaultJAXRPC11TypeMappingImpl.create();
            defaultDelTM = new TypeMappingDelegate(tm);
        } else {
            throw new RuntimeException(org.apache.axis.utils.Messages.getMessage("j2wBadTypeMapping00"));
        }
        registerSOAPENCDefault(DefaultSOAPEncodingTypeMappingImpl.getSingleton());
    }
    /**
     * Force registration of the given mapping as the SOAPENC default mapping
     * @param mapping
     */
    private void registerSOAPENCDefault(TypeMapping mapping) {
        registerForced(Constants.URI_SOAP11_ENC, mapping);
        registerForced(Constants.URI_SOAP12_ENC, mapping);
        mapping.setDelegate(defaultDelTM);
    }

    /**
     * Force registration of a particular mapping for a given namespace,
     * which will delegate back to the default.
     *
     * @param namespaceURI
     * @param mapping
     */
    private void registerForced(String namespaceURI, TypeMapping mapping) {
        mapTM.put(namespaceURI, new TypeMappingDelegate((TypeMapping) mapping));
        ((TypeMapping)mapping).setDelegate(defaultDelTM.getDelegate());
    }

    /**
     * Gets the TypeMapping for the namespace.  If not found, the default
     * TypeMapping is returned.
     *
     * @param namespaceURI - The namespace URI of a Web Service
     * @return The registered TypeMapping 
     * (which may be the default TypeMapping) or null.
     */
    public javax.xml.rpc.encoding.TypeMapping 
        getTypeMapping(String namespaceURI) {
//        namespaceURI = "";
        TypeMapping del = (TypeMapping) mapTM.get(namespaceURI);
        TypeMapping tm = null;
        if (del != null) {
            tm = del.getDelegate();
        }
        if (tm == null) {
            tm = (TypeMapping)getDefaultTypeMapping();
        }
        return tm;
    }
    
    /**
     * Obtain a type mapping for the given encodingStyle.  If no specific
     * mapping exists for this encodingStyle, we will create and register
     * one before returning it.
     * 
     * @param encodingStyle
     * @return a registered TypeMapping for the given encodingStyle
     */ 
    public TypeMapping getOrMakeTypeMapping(String encodingStyle) {
        TypeMapping del = (TypeMapping) mapTM.get(encodingStyle);
        TypeMapping tm = null;
        if (del != null) {
            tm = del.getDelegate();
        }
        if (tm == null || tm instanceof DefaultTypeMappingImpl) {
            tm = (TypeMapping)createTypeMapping();
            tm.setSupportedEncodings(new String[] {encodingStyle});
            register(encodingStyle, tm);
        }
        return tm;
    }

    /**
     * Unregisters the TypeMapping for the namespace.
     *
     * @param namespaceURI - The namespace URI
     * @return The registered TypeMapping .
     */
    public javax.xml.rpc.encoding.TypeMapping 
        unregisterTypeMapping(String namespaceURI) {
        TypeMapping del = (TypeMapping) mapTM.get(namespaceURI);
        TypeMapping tm = null;
        if (del != null) {
            tm = del.getDelegate();
            del.setDelegate(null);
        }
        return tm;
    }

    /**
     * Removes the TypeMapping for the namespace.
     *
     * @param mapping The type mapping to remove
     * @return true if found and removed
     */
    public boolean removeTypeMapping(
                                  javax.xml.rpc.encoding.TypeMapping mapping) {
        String[] ns = getRegisteredEncodingStyleURIs();
        boolean rc = false;
        for (int i=0; i < ns.length; i++) {
            if (getTypeMapping(ns[i]) == mapping) {
                rc = true;
                unregisterTypeMapping(ns[i]);
            }
        }
        return rc;
    }

    /**
     * Creates a new empty TypeMapping object for the specified
     * encoding style or XML schema namespace.
     *
     * @return An empty generic TypeMapping object
     */
    public javax.xml.rpc.encoding.TypeMapping createTypeMapping() {
        return new TypeMappingImpl(defaultDelTM);
    }
        

    /**
     * Gets a list of namespace URIs registered with this TypeMappingRegistry.
     *
     * @return String[] containing names of all registered namespace URIs
     */
    public String[] getRegisteredEncodingStyleURIs() {
        java.util.Set s = mapTM.keySet(); 
        if (s != null) { 
            String[] rc = new String[s.size()];
            int i = 0;
            java.util.Iterator it = s.iterator();
            while(it.hasNext()) {
                rc[i++] = (String) it.next();
            }
            return rc;
        }
        return null;
    } 


    /**
     * Removes all TypeMappings and namespaceURIs from this TypeMappingRegistry.
     */
    public void clear() {
        mapTM.clear();
    }

    /**
     * Return the default TypeMapping
     * @return TypeMapping or null
     **/
    public javax.xml.rpc.encoding.TypeMapping getDefaultTypeMapping() {
        TypeMapping defaultTM = defaultDelTM;
        while(defaultTM != null && defaultTM instanceof TypeMappingDelegate) {
            defaultTM = defaultTM.getDelegate();
        }
        return defaultTM;
    }

}