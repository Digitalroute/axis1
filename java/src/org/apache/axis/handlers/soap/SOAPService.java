/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.axis.handlers.soap;

import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.Handler;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.SimpleTargetedChain;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.enum.Style;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.providers.BasicProvider;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.LockableHashtable;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.w3c.dom.Document;

import javax.xml.namespace.QName;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;


/** A <code>SOAPService</code> is a Handler which encapsulates a SOAP
 * invocation.  It has an request chain, an response chain, and a pivot-point,
 * and handles the SOAP semantics when invoke()d.
 *
 * @author Glen Daniels (gdaniels@macromedia.com)
 * @author Doug Davis (dug@us.ibm.com)
 */
public class SOAPService extends SimpleTargetedChain
{
    protected static Log log =
        LogFactory.getLog(SOAPService.class.getName());

    /** Valid transports for this service
     * (server side only!)
     *
     * !!! For now, if this is null, we assume all
     * transports are valid.
     */
    private Vector validTransports = null;

    /**
     * Does this service require a high-fidelity SAX recording of messages?
     * (default is true)
     */
    private boolean highFidelityRecording = true;

    /**
     * Our ServiceDescription.  Holds pretty much all the interesting
     * metadata about this service.
     */
    private ServiceDesc serviceDescription = new ServiceDesc();
    private AxisEngine engine;

    /**
     * Actor list - these are just the service-specific ones
     */
    ArrayList actors = new ArrayList();

    /**
     * Get the service-specific actor list
     * @return
     */
    public ArrayList getServiceActors() {
        return actors;
    }

    /**
     * Get the merged actor list for this service, including engine-wide
     * actor URIs.
     *
     * @return
     */
    public ArrayList getActors() {
        ArrayList acts = (ArrayList)actors.clone();  // ??? cache this?

        // TODO: a SOAPService should always be associated with an engine,
        // so this should never be null.... check all paths to ensure that
        // constraint is true.
        if (engine != null) {
            acts.addAll(engine.getActorURIs());
        }
        return acts;
    }

    /**
     * SOAPRequestHandler is used to inject SOAP semantics just before
     * the pivot handler.
     */
    private class SOAPRequestHandler extends BasicHandler {
        public SOAPRequestHandler() {}

        public void invoke(MessageContext msgContext) throws AxisFault {
            // Do SOAP semantics here
            if (this.log.isDebugEnabled()) {
                this.log.debug( JavaUtils.getMessage("semanticCheck00"));
            }

            ArrayList acts = getActors();

            // 1. Check mustUnderstands
            SOAPEnvelope env = msgContext.getRequestMessage().getSOAPEnvelope();
            Vector headers = env.getHeadersByActor(acts);
            Vector misunderstoodHeaders = null;
            Enumeration enum = headers.elements();
            while (enum.hasMoreElements()) {
                SOAPHeaderElement header = (SOAPHeaderElement)enum.
                                               nextElement();
                if (header.getMustUnderstand() && !header.isProcessed()) {
                    if (misunderstoodHeaders == null)
                        misunderstoodHeaders = new Vector();
                    misunderstoodHeaders.addElement(header);
                }
            }

            // !!! we should indicate SOAP1.2 compliance via the
            // MessageContext, not a boolean here....
            boolean doMisunderstoodHeaders = true;

            if (misunderstoodHeaders != null) {
                // !!! If SOAP 1.2, insert misunderstood fault header here
                if (doMisunderstoodHeaders) {
                    Message respMsg = msgContext.getResponseMessage();
                    if (respMsg == null) {
                        respMsg = new Message(new SOAPEnvelope());
                        msgContext.setResponseMessage(respMsg);
                    }
                    env = respMsg.getSOAPEnvelope();
                    enum = misunderstoodHeaders.elements();
                    while (enum.hasMoreElements()) {
                        SOAPHeaderElement badHeader = (SOAPHeaderElement)enum.
                                                          nextElement();
                        QName badQName = new QName(badHeader.getNamespaceURI(),
                                                   badHeader.getName());
                        SOAPHeaderElement newHeader = new
                            SOAPHeaderElement(Constants.URI_SOAP12_FAULT,
                                              Constants.ELEM_MISUNDERSTOOD);
                        newHeader.addAttribute(null,
                                               Constants.ATTR_QNAME,
                                               badQName);

                        env.addHeader(newHeader);
                    }
                }

                throw new AxisFault(Constants.FAULT_MUSTUNDERSTAND,
                                    JavaUtils.getMessage("noUnderstand00"),
                                    null, null);
            }
        }
    }

    /** Standard, no-arg constructor.
     */
    public SOAPService()
    {
        initHashtable(true);

        // For now, always assume we're the ultimate destination.
        // TODO : Handle SOAP 1.2 ultimateDestination actor as well
        actors.add("");
    }

    /** Constructor with real or null request, pivot, and response
     *  handlers. A special request handler is specified to inject
     *  SOAP semantics.
     */
    public SOAPService(Handler reqHandler, Handler pivHandler,
                       Handler respHandler) {
        this();
        init(reqHandler, new SOAPRequestHandler(), pivHandler, null, respHandler);
    }

    public TypeMappingRegistry getTypeMappingRegistry()
    {
        return serviceDescription.getTypeMappingRegistry();
    }

    /** Convenience constructor for wrapping SOAP semantics around
     * "service handlers" which actually do work.
     */
    public SOAPService(Handler serviceHandler)
    {
        init(null, new SOAPRequestHandler(), serviceHandler, null, null);
    }

    /** Tell this service which engine it's deployed to.
     *
     */
    public void setEngine(AxisEngine engine)
    {
        this.engine = engine;
    }

    public AxisEngine getEngine() {
        return engine;
    }

    public boolean availableFromTransport(String transportName)
    {
        if (validTransports != null) {
            for (int i = 0; i < validTransports.size(); i++) {
                if (((String)validTransports.elementAt(i)).
                                                 equals(transportName))
                    return true;
            }
            return false;
        }

        return true;
    }

    public Style getStyle() {
        return serviceDescription.getStyle();
    }

    public void setStyle(Style style) {
        serviceDescription.setStyle(style);
    }

    public ServiceDesc getServiceDescription() {
        return serviceDescription;
    }

    /**
     * Returns a service description with the implementation class filled in.
     * Syncronized to prevent simutaneous modification of serviceDescription.
     */
    public synchronized ServiceDesc getInitializedServiceDesc(
                                                     MessageContext msgContext)
            throws AxisFault {

        if (serviceDescription.getImplClass() == null) {

            // Let the provider do the work of filling in the service
            // descriptor.  This is so that it can decide itself how best
            // to map the Operations.  In the future, we may want to support
            // providers which don't strictly map to Java class backends
            // (BSFProvider, etc.), and as such we hand off here.
            if (pivotHandler instanceof BasicProvider) {
                ((BasicProvider)pivotHandler).initServiceDesc(this, msgContext);
            }

        }

        return serviceDescription;
    }

    public void setServiceDescription(ServiceDesc serviceDescription) {
        if (serviceDescription == null) {
            // FIXME: Throw NPE?
            return;
        }
        this.serviceDescription = serviceDescription;
    }

    public void setPropertyParent(Hashtable parent)
    {
        if (options == null) {
            options = new LockableHashtable();
        }
        ((LockableHashtable)options).setParent(parent);
    }

    /**
     * Generate WSDL.  If we have a specific file configured in the
     * ServiceDesc, just return that.  Otherwise run through all the Handlers
     * (including the provider) and call generateWSDL() on them via our
     * parent's implementation.
     */
    public void generateWSDL(MessageContext msgContext) throws AxisFault {
        if (serviceDescription == null ||
                serviceDescription.getWSDLFile() == null) {
            super.generateWSDL(msgContext);
            return;
        }

        // Got a WSDL file in the service description, so try and read it
        try {
            Document doc = XMLUtils.newDocument(
                    new FileInputStream(serviceDescription.getWSDLFile()));
            msgContext.setProperty("WSDL", doc);
        } catch (Exception e) {
            throw AxisFault.makeFault(e);
        }
    }
    /*********************************************************************
     * Administration and management APIs
     *
     * These can get called by various admin adapters, such as JMX MBeans,
     * our own Admin client, web applications, etc...
     *
     *********************************************************************
     */

    /** Placeholder for "enable this service" method
     */
    public void start()
    {
    }

    /** Placeholder for "disable this service" method
     */
    public void stop()
    {
    }

    /**
     * Make this service available on a particular transport
     */
    public void enableTransport(String transportName)
    {
        if (log.isDebugEnabled()) {
            log.debug(JavaUtils.getMessage(
                "enableTransport00", "" + this, transportName));
        }

        if (validTransports == null)
            validTransports = new Vector();
        validTransports.addElement(transportName);
    }

    /**
     * Disable access to this service from a particular transport
     */
    public void disableTransport(String transportName)
    {
        if (validTransports != null) {
            validTransports.removeElement(transportName);
        }
    }

    public boolean needsHighFidelityRecording() {
        return highFidelityRecording;
    }

    public void setHighFidelityRecording(boolean highFidelityRecording) {
        this.highFidelityRecording = highFidelityRecording;
    }
}
