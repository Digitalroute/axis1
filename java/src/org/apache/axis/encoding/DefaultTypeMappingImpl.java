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

package org.apache.axis.encoding;

import org.apache.axis.Constants;

import javax.xml.rpc.namespace.QName;
import javax.xml.rpc.JAXRPCException;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.axis.encoding.ser.ArraySerializerFactory;
import org.apache.axis.encoding.ser.ArrayDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.DateSerializerFactory;
import org.apache.axis.encoding.ser.DateDeserializerFactory;
import org.apache.axis.encoding.ser.Base64SerializerFactory;
import org.apache.axis.encoding.ser.Base64DeserializerFactory;
import org.apache.axis.encoding.ser.MapSerializerFactory;
import org.apache.axis.encoding.ser.MapDeserializerFactory;
import org.apache.axis.encoding.ser.HexSerializerFactory;
import org.apache.axis.encoding.ser.HexDeserializerFactory;
import org.apache.axis.encoding.ser.ElementSerializerFactory;
import org.apache.axis.encoding.ser.ElementDeserializerFactory;
import org.apache.axis.encoding.ser.VectorDeserializerFactory;
import org.apache.axis.encoding.ser.SimpleDeserializerFactory;
import org.apache.axis.encoding.ser.SimplePrimitiveSerializerFactory;
import org.apache.axis.encoding.ser.SimpleNonPrimitiveSerializerFactory;
import java.util.Vector;
import java.util.Hashtable;
import java.util.List;

/**
 * @author Rich Scheuerle (scheu@us.ibm.com)
 * 
 * This is the implementation of the axis Default TypeMapping (which extends
 * the JAX-RPC TypeMapping interface).
 * 
 * A TypeMapping is obtained from the singleton TypeMappingRegistry using
 * the namespace of the webservice.  The TypeMapping contains the tuples
 * {Java type, SerializerFactory, DeserializerFactory, Type QName)
 *
 * So if you have a Web Service with the namespace "XYZ", you call 
 * the TypeMappingRegistry.getTypeMapping("XYZ").
 *
 * The wsdl in your web service will use a number of types.  The tuple
 * information for each of these will be accessed via the TypeMapping.
 *
 * Because every web service uses the soap, schema, wsdl primitives, we could 
 * pre-populate the TypeMapping with these standard tuples.  Instead, if requested
 * namespace/class matches is not found in the TypeMapping but matches one these
 * known primitives, the request is delegated to this Default TypeMapping.
 * 
 */
public class DefaultTypeMappingImpl extends TypeMappingImpl { 
    
    private static DefaultTypeMappingImpl tm = null;
    /**
     * Construct TypeMapping
     */
    public static TypeMapping create() {
        if (tm == null) {
            tm = new DefaultTypeMappingImpl();
        }
        return tm;
    }

    private DefaultTypeMappingImpl() {
        super(null);
        delegateIfNotFound = false;

        // The XSD Primitives are mapped to java primitives.
        // The corresponding SOAP-ENC primitives are mapped to the wrapper classes.
        myRegister(Constants.XSD_BOOLEAN,    boolean.class,              null, null);
        myRegister(Constants.XSD_DOUBLE,     double.class,               null, null);
        myRegister(Constants.XSD_FLOAT,      float.class,                null, null);
        myRegister(Constants.XSD_INT,        int.class,                  null, null);
        myRegister(Constants.XSD_LONG,       long.class,                 null, null);
        myRegister(Constants.XSD_SHORT,      short.class,                null, null);
        myRegister(Constants.XSD_BYTE,       byte.class,                 null, null);
        myRegister(Constants.XSD_STRING,     java.lang.String.class,     null, null);
        myRegister(Constants.XSD_INTEGER,    java.math.BigInteger.class, null, null);
        myRegister(Constants.XSD_DECIMAL,    java.math.BigDecimal.class, null, null);
        myRegister(Constants.XSD_QNAME,      javax.xml.rpc.namespace.QName.class,
                   new BeanSerializerFactory(javax.xml.rpc.namespace.QName.class,
                                             Constants.XSD_QNAME),
                   new BeanDeserializerFactory(javax.xml.rpc.namespace.QName.class,
                                               Constants.XSD_QNAME));
        myRegister(Constants.XSD_ANYTYPE,    java.lang.Object.class,     null, null);
        myRegister(Constants.XSD_DATE,       java.util.Date.class,                           
                   new DateSerializerFactory(java.util.Date.class,Constants.XSD_DATE),
                   new DateDeserializerFactory(java.util.Date.class,Constants.XSD_DATE));
        myRegister(Constants.XSD_DATE2,      java.util.Date.class,                           
                   new DateSerializerFactory(java.util.Date.class, Constants.XSD_DATE2),
                   new DateDeserializerFactory(java.util.Date.class, Constants.XSD_DATE2));
        myRegister(Constants.XSD_DATE3,      java.util.Date.class,                           
                   new DateSerializerFactory(java.util.Date.class, Constants.XSD_DATE3),
                   new DateDeserializerFactory(java.util.Date.class, Constants.XSD_DATE3));
        myRegister(Constants.XSD_BASE64,     byte[].class,                                   
                   new Base64SerializerFactory(),
                   new Base64DeserializerFactory());
        myRegister(Constants.XSD_HEXBIN,     Hex.class,   
                   new HexSerializerFactory(),
                   new HexDeserializerFactory());

        // Use the Map Serialization for both Hashtables and HashMap objects
        myRegister(Constants.SOAP_MAP,       java.util.HashMap.class,    
                   new MapSerializerFactory(java.util.HashMap.class,Constants.SOAP_MAP),
                   new MapDeserializerFactory(java.util.HashMap.class,Constants.SOAP_MAP));
        myRegister(Constants.SOAP_MAP,       java.util.Hashtable.class,    
                   new MapSerializerFactory(java.util.Hashtable.class,Constants.SOAP_MAP),
                   new MapDeserializerFactory(java.util.Hashtable.class,Constants.SOAP_MAP));

        // Use the Element Serializeration for elements
        myRegister(Constants.SOAP_ELEMENT,   org.w3c.dom.Element.class,    
                   new ElementSerializerFactory(),
                   new ElementDeserializerFactory());
        myRegister(Constants.SOAP_VECTOR,    java.util.Vector.class,             
                   null,
                   new VectorDeserializerFactory(java.util.Vector.class,Constants.SOAP_VECTOR));

        // All array objects automatically get associated with the SOAP_ARRAY.  There
        // is no way to do this with a hash table, so it is done directly in getTypeQName.
        // Internally the runtime uses ArrayList objects to hold arrays...which is 
        // the reason that ArrayList is associated with SOAP_ARRAY.  In addition, handle
        // all objects that implement the List interface as a SOAP_ARRAY
        myRegister(Constants.SOAP_ARRAY,     java.util.List.class,               
                   new ArraySerializerFactory(),
                   new ArrayDeserializerFactory());
        myRegister(Constants.SOAP_ARRAY,     java.util.ArrayList.class,               
                   new ArraySerializerFactory(),
                   new ArrayDeserializerFactory());

        myRegister(Constants.SOAP_STRING,     java.lang.String.class,     null, null); 
        myRegister(Constants.SOAP_BOOLEAN,    java.lang.Boolean.class,    null, null);
        myRegister(Constants.SOAP_DOUBLE,     java.lang.Double.class,     null, null);
        myRegister(Constants.SOAP_FLOAT,      java.lang.Float.class,      null, null);
        myRegister(Constants.SOAP_INT,        java.lang.Integer.class,    null, null);
        myRegister(Constants.SOAP_INTEGER,    java.math.BigInteger.class, null, null);
        myRegister(Constants.SOAP_DECIMAL,    java.math.BigDecimal.class, null, null);
        myRegister(Constants.SOAP_LONG,       java.lang.Long.class,       null, null);
        myRegister(Constants.SOAP_SHORT,      java.lang.Short.class,      null, null);
        myRegister(Constants.SOAP_BYTE,       java.lang.Byte.class,       null, null);

        // Note that a SOAP_BASE64 is mapped to a Byte[] not a byte[].  This is 
        // the reason why the array serialization is used.
        myRegister(Constants.SOAP_BASE64,     java.lang.Byte[].class,       
                   new ArraySerializerFactory(),
                   new ArrayDeserializerFactory());
    }

    /**
     * Construct TypeMapping for all the [xmlType, javaType] for all of the
     * known xmlType namespaces
     */   
    protected void myRegister(QName xmlType, Class javaType,
                              SerializerFactory sf, DeserializerFactory df) {
        // If the type is truly a primitive, then the serializer can
        // be shared.  Otherwise the serializers/deserializers can't be shared.
        boolean primitive = (javaType.isPrimitive() || javaType == java.lang.String.class);

        // If factories are not specified, use the Simple serializer/deserializer factories.
        if (sf == null && df == null) {
            if (primitive) {
                sf = new SimplePrimitiveSerializerFactory(javaType, xmlType);
            } else {
                sf = new SimpleNonPrimitiveSerializerFactory(javaType, xmlType);
            }
            df = new SimpleDeserializerFactory(javaType, xmlType);
        }

        // Register all known flavors of the namespace.
        try {
            if (xmlType.getNamespaceURI().equals(Constants.URI_CURRENT_SCHEMA_XSD)) {
                for (int i=0; i < Constants.URIS_SCHEMA_XSD.length; i++) {
                    QName qName = new QName(Constants.URIS_SCHEMA_XSD[i], xmlType.getLocalPart());
                    register(javaType, qName, sf, df);
                }
            }
            else if (xmlType.getNamespaceURI().equals(Constants.URI_CURRENT_SOAP_ENC)) {
                for (int i=0; i < Constants.URIS_SOAP_ENC.length; i++) {
                    QName qName = new QName(Constants.URIS_SOAP_ENC[i], xmlType.getLocalPart());
                    register(javaType, qName, sf, df);
                }
            }
            // Register with the specified xmlType.  This is the prefered mapping and
            // the last registered mapping wins.
            register(javaType, xmlType, sf, df);
        } catch (Exception e) {}
    }

    /**
     * Gets the QName for the type mapped to Class.
     * @param javaType class or type
     * @return xmlType qname or null
     */
    public QName getTypeQName(Class javaType) {
        QName xmlType = super.getTypeQName(javaType);

        // Can only detect arrays via code
        if (xmlType == null &&
            (javaType.isArray() ||
             javaType == List.class ||
             List.class.isAssignableFrom(javaType))) {
            xmlType = Constants.SOAP_ARRAY;
        }
        return xmlType;
    }
}
