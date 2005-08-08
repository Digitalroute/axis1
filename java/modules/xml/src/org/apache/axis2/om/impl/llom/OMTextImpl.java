/*
 * Copyright 2004,2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.axis2.om.impl.llom;

import org.apache.axis2.attachments.Base64;
import org.apache.axis2.attachments.ByteArrayDataSource;
import org.apache.axis2.attachments.utils.IOUtils;
import org.apache.axis2.om.*;
import org.apache.axis2.om.impl.MIMEOutputUtils;
import org.apache.axis2.om.impl.OMOutputImpl;
import org.apache.axis2.om.impl.llom.mtom.MTOMStAXSOAPModelBuilder;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Random;

public class OMTextImpl extends OMNodeImpl implements OMText, OMConstants {
    protected String value = null;

    protected String mimeType;

    protected boolean optimize = false;

    protected boolean isBinary = false;

    private static Random rnd = new Random(new Date().getTime());

    /**
     * Field contentID for the mime part used when serialising Binary stuff as
     * MTOM optimised
     */
    private String contentID = null;

    /**
     * Field dataHandler
     */
    private DataHandler dataHandler = null;

    /**
     * Field nameSpace used when serialising Binary stuff as MTOM optimised
     */
    protected OMNamespace ns = new OMNamespaceImpl(
            "http://www.w3.org/2004/08/xop/include", "xop");

    /**
     * Field localName used when serialising Binary stuff as MTOM optimised
     */
    protected String localName = "Include";

    /**
     * Field attributes used when serialising Binary stuff as MTOM optimised
     */
    protected OMAttribute attribute;

    /**
     * Constructor OMTextImpl
     * 
     * @param s
     */
    public OMTextImpl(String s) {
        this.value = s;
        this.nodeType = TEXT_NODE;
    }

    /**
     * Constructor OMTextImpl
     * 
     * @param parent
     * @param text
     */
    public OMTextImpl(OMElement parent, String text) {
        super(parent);
        this.value = text;
        done = true;
        this.nodeType = TEXT_NODE;
    }

    /**
     * @param s -
     *            base64 encoded String representation of Binary
     * @param mimeType
     *            of the Binary
     */
    public OMTextImpl(String s, String mimeType, boolean optimize) {
        this(null, s, mimeType, optimize);
    }

    /**
     * @param parent
     * @param s -
     *            base64 encoded String representation of Binary
     * @param mimeType
     *            of the Binary
     */
    public OMTextImpl(OMElement parent, String s, String mimeType,
            boolean optimize) {
        this(parent, s);
        this.mimeType = mimeType;
        this.optimize = optimize;
        done = true;
        this.nodeType = TEXT_NODE;
    }

    /**
     * @param dataHandler
     *            To send binary optimised content Created programatically.
     */
    public OMTextImpl(DataHandler dataHandler) {
        this(dataHandler, true);
    }

    /**
     * @param dataHandler
     * @param optimize
     *            To send binary content. Created progrmatically.
     */
    public OMTextImpl(DataHandler dataHandler, boolean optimize) {
        this.dataHandler = dataHandler;
        this.isBinary = true;
        this.optimize = optimize;
        done = true;
        this.nodeType = TEXT_NODE;
    }

    /**
     * @param contentID
     * @param parent
     * @param builder
     *            Used when the builder is encountered with a XOP:Include tag
     *            Stores a reference to the builder and the content-id. Supports
     *            deffered parsing of MIME messages
     */
    public OMTextImpl(String contentID, OMElement parent,
            OMXMLParserWrapper builder) {
        super(parent);
        this.contentID = contentID;
        this.optimize = true;
        this.isBinary = true;
        this.builder = builder;
        this.nodeType = TEXT_NODE;
    }

    /**
     * @param omOutput
     * @throws XMLStreamException
     */
    public void serializeWithCache(
            org.apache.axis2.om.impl.OMOutputImpl omOutput)
            throws XMLStreamException {
        XMLStreamWriter writer = omOutput.getXmlStreamWriter();
        int type = getType();
        if (type == TEXT_NODE) {
            writer.writeCharacters(this.value);
        } else if (type == CDATA_SECTION_NODE) {
            writer.writeCData(this.value);
        }
        OMNode nextSibling = this.getNextSibling();
        if (nextSibling != null) {
            nextSibling.serializeWithCache(omOutput);
        }
    }

    /**
     * Returns the value
     */
    public String getText() throws OMException {
        if (this.value != null) {
            return this.value;
        } else {
            try {
                InputStream inStream;
                inStream = this.getInputStream();
                //int x = inStream.available();
                byte[] data;
                StringBuffer text= new StringBuffer();
                // There are times, this inStream reports the Available bytes
                // incorrectly.
                // Reading the First byte & then getting the available number of
                // bytes fixed it.
                do {
                    data = new byte[3];
                    IOUtils.readFully(inStream, data, 0, 3);
                    text.append(Base64.encode(data));                    
                }while (inStream.available()>0);
                return Base64.encode(data);
            } catch (Exception e) {
                throw new OMException(e);
            }
        }
    }

    public boolean isOptimized() {
        return optimize;
    }

    public void setOptimize(boolean value) {
        this.optimize = value;
    }

    /**
     * @return
     * @throws org.apache.axis2.om.OMException
     * 
     * @throws OMException
     */
    public DataHandler getDataHandler() {
        /*
         * this should return a DataHandler containing the binary data
         * reperesented by the Base64 strings stored in OMText
         */
        if (value != null) {
            ByteArrayDataSource dataSource;
            byte[] data = Base64.decode(value);
            if (mimeType != null) {
                dataSource = new ByteArrayDataSource(data, mimeType);
            } else {
                // Assumes type as application/octet-stream
                dataSource = new ByteArrayDataSource(data);
            }
            return new DataHandler(dataSource);
        } else {

            if (dataHandler == null) {
                if (contentID == null) {
                    throw new RuntimeException("ContentID is null");
                }
                dataHandler = ((MTOMStAXSOAPModelBuilder) builder)
                        .getDataHandler(contentID);
            }
            return dataHandler;
        }
    }

    public String getLocalName() {
        return localName;
    }

    public java.io.InputStream getInputStream() throws OMException {
        if (isBinary) {
            if (dataHandler == null) {
                getDataHandler();
            }
            InputStream inStream;
            try {
                inStream = dataHandler.getDataSource().getInputStream();
            } catch (IOException e) {
                throw new OMException(
                        "Cannot get InputStream from DataHandler." + e);
            }
            return inStream;
        } else {
            throw new OMException("Unsupported Operation");
        }
    }

    public String getContentID() {
        if (contentID == null) {
            contentID = MIMEOutputUtils.getRandomStringOf18Characters()
                    + "@apache.org";
        }
        return this.contentID;
    }

    public boolean isComplete() {
        return done;
    }

    public void serialize(org.apache.axis2.om.impl.OMOutputImpl omOutput)
            throws XMLStreamException {
        if (!this.isBinary) {
            serializeWithCache(omOutput);
        } else {
            if (omOutput.isOptimized()) {
                if (contentID == null) {
                    contentID = omOutput.getNextContentId();
                }
                // send binary as MTOM optimised
                this.attribute = new OMAttributeImpl("href",
                        new OMNamespaceImpl("", ""), "cid:" + getContentID());
                this.serializeStartpart(omOutput);
                omOutput.writeOptimized(this);
                omOutput.getXmlStreamWriter().writeEndElement();
            } else {
                omOutput.getXmlStreamWriter().writeCharacters(this.getText());
            }
            // TODO do we need these
            OMNode nextSibling = this.getNextSibling();
            if (nextSibling != null) {
                // serilize next sibling
                nextSibling.serialize(omOutput);
            } else {
                // TODO : See whether following part is really needed
                if (parent != null && !parent.isComplete()) {
                    // do the special serialization
                    // Only the push serializer is left now
                    builder.next();
                }
            }
        }
    }

    /*
     * Methods to copy from OMSerialize utils
     */
    private void serializeStartpart(OMOutputImpl omOutput)
            throws XMLStreamException {
        String nameSpaceName;
        String writer_prefix;
        String prefix;
        XMLStreamWriter writer = omOutput.getXmlStreamWriter();
        if (this.ns != null) {
            nameSpaceName = this.ns.getName();
            writer_prefix = writer.getPrefix(nameSpaceName);
            prefix = this.ns.getPrefix();
            if (nameSpaceName != null) {
                if (writer_prefix != null) {
                    writer
                            .writeStartElement(nameSpaceName, this
                                    .getLocalName());
                } else {
                    if (prefix != null) {
                        writer.writeStartElement(prefix, this.getLocalName(),
                                nameSpaceName);
                        //TODO FIX ME
                        //writer.writeNamespace(prefix, nameSpaceName);
                        writer.setPrefix(prefix, nameSpaceName);
                    } else {
                        writer.writeStartElement(nameSpaceName, this
                                .getLocalName());
                        writer.writeDefaultNamespace(nameSpaceName);
                        writer.setDefaultNamespace(nameSpaceName);
                    }
                }
            } else {
                writer.writeStartElement(this.getLocalName());
            }
        } else {
            writer.writeStartElement(this.getLocalName());
        }
        // add the elements attribute "href"
        serializeAttribute(this.attribute, omOutput);
        // add the namespace
        serializeNamespace(this.ns, omOutput);
    }

    /**
     * Method serializeAttribute
     * 
     * @param attr
     * @param omOutput
     * @throws XMLStreamException
     */
    static void serializeAttribute(OMAttribute attr, OMOutputImpl omOutput)
            throws XMLStreamException {
        XMLStreamWriter writer = omOutput.getXmlStreamWriter();
        // first check whether the attribute is associated with a namespace
        OMNamespace ns = attr.getNamespace();
        String prefix;
        String namespaceName;
        if (ns != null) {
            // add the prefix if it's availble
            prefix = ns.getPrefix();
            namespaceName = ns.getName();
            if (prefix != null) {
                writer.writeAttribute(prefix, namespaceName, attr
                        .getLocalName(), attr.getValue());
            } else {
                writer.writeAttribute(namespaceName, attr.getLocalName(), attr
                        .getValue());
            }
        } else {
            writer.writeAttribute(attr.getLocalName(), attr.getValue());
        }
    }

    /**
     * Method serializeNamespace
     * 
     * @param namespace
     * @param omOutput
     * @throws XMLStreamException
     */
    static void serializeNamespace(OMNamespace namespace,
            org.apache.axis2.om.impl.OMOutputImpl omOutput)
            throws XMLStreamException {
        XMLStreamWriter writer = omOutput.getXmlStreamWriter();
        if (namespace != null) {
            String uri = namespace.getName();
            String ns_prefix = namespace.getPrefix();
            writer.writeNamespace(ns_prefix, namespace.getName());
            writer.setPrefix(ns_prefix, uri);
        }
    }

    /**
     * Slightly different implementation of the discard method
     * 
     * @throws OMException
     */
    public void discard() throws OMException {
        if (done) {
            this.detach();
        } else {
            builder.discard((OMElement) this.parent);
        }
    }
}