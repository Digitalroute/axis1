/*
 * The Apache Software License, Version 1.1
 *
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

package org.apache.axis.encoding.ser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.xml.rpc.namespace.QName;
import java.io.IOException;

import org.apache.axis.Constants;
import org.apache.axis.wsdl.fromJava.Types;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.SerializerFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.DeserializerFactory;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.utils.JavaUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

/**
 * A <code>VectorSerializer</code> is be used to serialize and
 * deserialize Vectors using the <code>SOAP-ENC</code>
 * encoding style.<p>
 *
 *  @author Rich Scheuerle (scheu@us.ibm.com)
 */

public class VectorSerializer implements Serializer
{
    protected static Log log =
        LogFactory.getLog(VectorSerializer.class.getName());

    /** Serialize a Vector
     *
     * Walk the collection of keys, serializing each key/value pair
     * inside an <item> element.
     *
     * @param name the desired QName for the element
     * @param attributes the desired attributes for the element
     * @param value the Object to serialize
     * @param context the SerializationContext in which to do all this
     * @exception IOException
     */
    public void serialize(QName name, Attributes attributes,
                          Object value, SerializationContext context)
        throws IOException
    {
        if (!(value instanceof Vector))
            throw new IOException(
                JavaUtils.getMessage("noVector00", "VectorSerializer", 
                                     value.getClass().getName()));

        Vector vector = (Vector)value;

        context.startElement(name, attributes);
        for (Iterator i = vector.iterator(); i.hasNext(); )
        {
            Object item = i.next();
            context.serialize(Constants.QNAME_LITERAL_ITEM,  null, item, (item!=null ? item.getClass(): null) );
        }
        context.endElement();
    }

    public String getMechanismType() { return Constants.AXIS_SAX; }

    /**
     * Return XML schema for the specified type, suitable for insertion into
     * the <types> element of a WSDL document.
     *
     * @param types the Java2WSDL Types object which holds the context
     *              for the WSDL being generated.
     * @return true if we wrote a schema, false if we didn't.
     * @see org.apache.axis.wsdl.fromJava.Types
     */
    public boolean writeSchema(Types types) throws Exception {
        Element complexType = types.createElement("complexType");
        complexType.setAttribute("name", "Vector");
        types.writeSchemaElement(types.getWsdlQName(Constants.SOAP_VECTOR),
                                 complexType);
        Element seq = types.createElement("sequence");
        complexType.appendChild(seq);

        Element element = types.createElement("element");
        element.setAttribute("name", "item");
        element.setAttribute("minOccurs", "0");
        element.setAttribute("maxOccurs", "unbounded");
        element.setAttribute("type", "xsd:anyType");
        seq.appendChild(element);

        return true;
    }
}
