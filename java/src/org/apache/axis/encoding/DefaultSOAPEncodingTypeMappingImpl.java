/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
import org.apache.axis.encoding.ser.Base64SerializerFactory;
import org.apache.axis.encoding.ser.Base64DeserializerFactory;

/**
 * @author Rich Scheuerle (scheu@us.ibm.com)
 * 
 * This is the implementation of the axis Default JAX-RPC SOAP 1.2 TypeMapping
 * See DefaultTypeMapping for more information.
 * 
 */
public class DefaultSOAPEncodingTypeMappingImpl extends DefaultTypeMappingImpl {
    
    private static DefaultSOAPEncodingTypeMappingImpl tm = null;
    /**
     * Construct TypeMapping
     */
    public static TypeMapping create() {
        if (tm == null) {
            tm = new DefaultSOAPEncodingTypeMappingImpl();
        }
        return tm;
    }
    
    public static TypeMapping createWithDelegate() {
        TypeMapping ret = new DefaultSOAPEncodingTypeMappingImpl();
        ret.setDelegate(DefaultTypeMappingImpl.getSingleton());
        return ret;
    }

    protected DefaultSOAPEncodingTypeMappingImpl() {
        registerSOAPTypes();        
    }

    /**
     * Register the SOAP encoding data types.  This is split out into a
     * method so it can happen either before or after the XSD mappings.
     */
    private void registerSOAPTypes() {
        // SOAP Encoded strings are treated as primitives.
        // Everything else is not.
        myRegisterSimple(Constants.SOAP_STRING, java.lang.String.class);
        myRegisterSimple(Constants.SOAP_BOOLEAN, java.lang.Boolean.class);
        myRegisterSimple(Constants.SOAP_DOUBLE, java.lang.Double.class);
        myRegisterSimple(Constants.SOAP_FLOAT, java.lang.Float.class);
        myRegisterSimple(Constants.SOAP_INT, java.lang.Integer.class);
        myRegisterSimple(Constants.SOAP_INTEGER, java.math.BigInteger.class);
        myRegisterSimple(Constants.SOAP_DECIMAL, java.math.BigDecimal.class);
        myRegisterSimple(Constants.SOAP_LONG, java.lang.Long.class);
        myRegisterSimple(Constants.SOAP_SHORT, java.lang.Short.class);
        myRegisterSimple(Constants.SOAP_BYTE, java.lang.Byte.class);
        myRegister(Constants.SOAP_BASE64,     byte[].class,
                   new Base64SerializerFactory(byte[].class,
                                               Constants.SOAP_BASE64 ),
                   new Base64DeserializerFactory(byte[].class,
                                                 Constants.SOAP_BASE64)
        );
        myRegister(Constants.SOAP_BASE64BINARY,     byte[].class,
                   new Base64SerializerFactory(byte[].class,
                                               Constants.SOAP_BASE64 ),
                   new Base64DeserializerFactory(byte[].class,
                                                 Constants.SOAP_BASE64)
        );
    }
}
