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

package org.apache.axis;

import org.apache.axis.schema.SchemaVersion1999;
import org.apache.axis.schema.SchemaVersion2000;
import org.apache.axis.schema.SchemaVersion2001;
import org.apache.axis.soap.SOAPConstants;
import org.xml.sax.Attributes;

import javax.xml.namespace.QName;

public class Constants {
    // Some common Constants that should be used in local handler options
    // (Not all implementations will have these concepts - for example
    //  not all Engines will have notion of registries but defining these
    //  here should allow people to ask if they exist)
    //////////////////////////////////////////////////////////////////////////

    // Namespace Prefix Constants
    //////////////////////////////////////////////////////////////////////////
    public static final String NS_PREFIX_SOAP_ENV   = "soapenv";
    public static final String NS_PREFIX_SOAP_ENC   = "soapenc";
    public static final String NS_PREFIX_SCHEMA_XSI = "xsi" ;
    public static final String NS_PREFIX_SCHEMA_XSD = "xsd" ;
    public static final String NS_PREFIX_WSDL       = "wsdl" ;
    public static final String NS_PREFIX_WSDL_SOAP  = "wsdlsoap";
    public static final String NS_PREFIX_XMLSOAP    = "apachesoap";
    public static final String NS_PREFIX_XML        = "xml";

    // Axis Namespaces
    public static final String NS_URI_AXIS = "http://xml.apache.org/axis/";
    public static final String NS_URI_XMLSOAP = "http://xml.apache.org/xml-soap";

    // Special namespace URI to indicate an "automatically" serialized Java
    // type.  This allows us to use types without needing explicit mappings,
    // such that Java classes like "org.foo.Bar" map to QNames like
    // {http://xml.apache.org/axis/java}org.foo.Bar
    public static final String NS_URI_JAVA = "http://xml.apache.org/axis/java";

    //
    // Default SOAP version
    //
    public static final SOAPConstants DEFAULT_SOAP_VERSION =
        SOAPConstants.SOAP11_CONSTANTS;

    //
    // SOAP-ENV Namespaces
    //
    public static final String URI_SOAP11_ENV =
                                "http://schemas.xmlsoap.org/soap/envelope/" ;
    public static final String URI_SOAP12_ENV =
                                   "http://www.w3.org/2002/12/soap-envelope";
    public static final String URI_DEFAULT_SOAP_ENV =
        DEFAULT_SOAP_VERSION.getEnvelopeURI();

    public static final String[] URIS_SOAP_ENV = {
        URI_SOAP11_ENV,
        URI_SOAP12_ENV,
    };

    // Constant name of the enterprise-style logging category.
    // The enterprise category is for stuff that an enterprise product might
    // want to track, but in a simple environment (like the AXIS build) would
    // be nothing more than a nuisance.
    public static final String ENTERPRISE_LOG_CATEGORY = "org.apache.axis.enterprise";

    /**
     * time logged stuff
     */
    public static final String TIME_LOG_CATEGORY = "org.apache.axis.TIME";

    /**
     * servlet exceptions. Axis faults are logged at debug level here.
     */
    public static final String EXCEPTION_LOG_CATEGORY = "org.apache.axis.EXCEPTIONS";

    /** The name of the field which accepts xsd:any content in Beans */
    public static final String ANYCONTENT = "_any";

    /**
     * Returns true if SOAP_ENV Namespace
     */
    public static boolean isSOAP_ENV(String s) {
        for (int i=0; i<URIS_SOAP_ENV.length; i++) {
            if (URIS_SOAP_ENV[i].equals(s)) {
                return true;
            }
        }
        return false;
    }


    public static final String URI_LITERAL_ENC = "";

    //
    // SOAP-ENC Namespaces
    //
    public static final String URI_SOAP11_ENC =
                                "http://schemas.xmlsoap.org/soap/encoding/" ;
    public static final String URI_SOAP12_ENC =
                                   "http://www.w3.org/2002/12/soap-encoding";
    public static final String URI_SOAP12_NOENC =
                     "http://www.w3.org/2002/12/soap-envelope/encoding/none";
    public static final String URI_DEFAULT_SOAP_ENC =
        DEFAULT_SOAP_VERSION.getEncodingURI();

    public static final String[] URIS_SOAP_ENC = {
        URI_SOAP11_ENC,
        URI_SOAP12_ENC,
    };

    /**
     * Returns true if SOAP_ENC Namespace
     */
    public static boolean isSOAP_ENC(String s) {
        for (int i=0; i<URIS_SOAP_ENC.length; i++) {
            if (URIS_SOAP_ENC[i].equals(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This utility routine returns the value of an attribute which might
     * be in one of several namespaces.
     *
     * @param attributes the attributes to search
     * @param search an array of namespace URI strings to search
     * @param localPart is the local part of the attribute name
     * @return the value of the attribute or null
     */
    public static String getValue(Attributes attributes,
                                  String [] search,
                                  String localPart) {
        if (attributes == null || attributes.getLength() == 0 || search == null || localPart == null)
            return null;

        int len = attributes.getLength();

        for (int i=0; i < len; i++) {
            if (attributes.getLocalName(i).equals(localPart)) {
                String uri = attributes.getURI(i);
                for (int j=0; j<search.length; j++) {
                    if (search[j].equals(uri)) return attributes.getValue(i);
                }
            }
        }

        return null;
    }

    /**
     * Search an attribute collection for a list of QNames, returning
     * the value of the first one found, or null if none were found.
     *
     * @param attributes
     * @param search
     * @return the value of the attribute
     */
    public static String getValue(Attributes attributes,
                                  QName [] search) {
        if (attributes == null || search == null)
            return null;

        if (attributes.getLength() == 0) return null;

        String value = null;
        for (int i=0; (value == null) && (i < search.length); i++) {
            value = attributes.getValue(search[i].getNamespaceURI(),
                                        search[i].getLocalPart());
        }

        return value;
    }

    /**
     * equals
     * The first QName is the current version of the name.  The second qname is compared
     * with the first considering all namespace uri versions.
     * @param first Currently supported QName
     * @param second any qname
     * @return true if the qnames represent the same qname (paster namespace uri versions considered
     */
    public static boolean equals(QName first, QName second) {
        if (first == second) {
            return true;
        }
        if (first==null || second==null) {
            return false;
        }
        if (first.equals(second)) {
            return true;
        }
        if (!first.getLocalPart().equals(second.getLocalPart())) {
            return false;
        }

        String namespaceURI = first.getNamespaceURI();
        String[] search = null;
        if (namespaceURI.equals(URI_DEFAULT_SOAP_ENC))
            search = URIS_SOAP_ENC;
        else if (namespaceURI.equals(URI_DEFAULT_SOAP_ENV))
            search = URIS_SOAP_ENV;
        else if (namespaceURI.equals(URI_DEFAULT_SCHEMA_XSD))
            search = URIS_SCHEMA_XSD;
        else if (namespaceURI.equals(URI_DEFAULT_SCHEMA_XSI))
            search = URIS_SCHEMA_XSI;
        else
            search = new String[] {namespaceURI};

        for (int i=0; i < search.length; i++) {
            if (search[i].equals(second.getNamespaceURI())) {
                return true;
            }
        }
        return false;
    }

    // Misc SOAP Namespaces / URIs
    public static final String URI_SOAP11_NEXT_ACTOR =
                                     "http://schemas.xmlsoap.org/soap/actor/next" ;
    public static final String URI_SOAP12_NEXT_ACTOR =
                                     "http://www.w3.org/2002/12/soap-envelope/actor/next";

    public static final String URI_SOAP12_FAULT =
                                     "http://www.w3.org/2002/12/soap-faults";

    public static final String URI_SOAP12_UPGRADE =
                                     "http://www.w3.org/2002/12/soap-upgrade";

    public static final String URI_SOAP12_RPC =
                                     "http://www.w3.org/2002/12/soap-rpc";

    public static final String URI_SOAP12_NONE_ROLE =
                         "http://www.w3.org/2002/12/soap-envelope/role/none";
    public static final String URI_SOAP12_ULTIMATE_ROLE =
             "http://www.w3.org/2002/12/soap-envelope/role/ultimateReceiver";

    public static final String URI_SOAP11_HTTP =
                                     "http://schemas.xmlsoap.org/soap/http";
    public static final String URI_SOAP12_HTTP =
                                    "http://www.w3.org/2002/12/http";

    public static final String NS_URI_XMLNS =
                                       "http://www.w3.org/2000/xmlns/";

    public static final String NS_URI_XML =
                                       "http://www.w3.org/XML/1998/namespace";

    //
    // Schema XSD Namespaces
    //
    public static final String URI_1999_SCHEMA_XSD =
                                          "http://www.w3.org/1999/XMLSchema";
    public static final String URI_2000_SCHEMA_XSD =
                                       "http://www.w3.org/2000/10/XMLSchema";
    public static final String URI_2001_SCHEMA_XSD =
                                          "http://www.w3.org/2001/XMLSchema";

    public static final String URI_DEFAULT_SCHEMA_XSD = URI_2001_SCHEMA_XSD;

    public static final String[] URIS_SCHEMA_XSD = {
        URI_2001_SCHEMA_XSD,
        URI_2000_SCHEMA_XSD,
        URI_1999_SCHEMA_XSD
    };
    public static final QName [] QNAMES_NIL = {
        SchemaVersion2001.QNAME_NIL,
        SchemaVersion2000.QNAME_NIL,
        SchemaVersion1999.QNAME_NIL
    };

    /**
     * Returns true if SchemaXSD Namespace
     */
    public static boolean isSchemaXSD(String s) {
        for (int i=0; i<URIS_SCHEMA_XSD.length; i++) {
            if (URIS_SCHEMA_XSD[i].equals(s)) {
                return true;
            }
        }
        return false;
    }

    //
    // Schema XSI Namespaces
    //
    public static final String URI_1999_SCHEMA_XSI =
                                 "http://www.w3.org/1999/XMLSchema-instance";
    public static final String URI_2000_SCHEMA_XSI =
                              "http://www.w3.org/2000/10/XMLSchema-instance";
    public static final String URI_2001_SCHEMA_XSI =
                                 "http://www.w3.org/2001/XMLSchema-instance";
    public static final String URI_DEFAULT_SCHEMA_XSI = URI_2001_SCHEMA_XSI;

    public static final String[] URIS_SCHEMA_XSI = {
        URI_1999_SCHEMA_XSI,
        URI_2000_SCHEMA_XSI,
        URI_2001_SCHEMA_XSI,
    };

    /**
     * Returns true if SchemaXSI Namespace
     */
    public static boolean isSchemaXSI(String s) {
        for (int i=0; i<URIS_SCHEMA_XSI.length; i++) {
            if (URIS_SCHEMA_XSI[i].equals(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * WSDL Namespace
     */
    public static final String NS_URI_WSDL11 =
                                 "http://schemas.xmlsoap.org/wsdl/";

    public static final String[] NS_URIS_WSDL = {
        NS_URI_WSDL11,
    };

    /**
     * Returns true if WSDL Namespace
     */
    public static boolean isWSDL(String s) {
        for (int i=0; i<NS_URIS_WSDL.length; i++) {
            if (NS_URIS_WSDL[i].equals(s)) {
                return true;
            }
        }
        return false;
    }

    //
    // WSDL extensions for SOAP in DIME
    // (http://gotdotnet.com/team/xml_wsspecs/dime/WSDL-Extension-for-DIME.htm)
    //
    public static final String URI_DIME_WSDL =
                                 "http://schemas.xmlsoap.org/ws/2002/04/dime/wsdl/";

    public static final String URI_DIME_CONTENT =
                                 "http://schemas.xmlsoap.org/ws/2002/04/content-type/";

    public static final String URI_DIME_REFERENCE=
                                 "http://schemas.xmlsoap.org/ws/2002/04/reference/";

    public static final String URI_DIME_CLOSED_LAYOUT=
                                 "http://schemas.xmlsoap.org/ws/2002/04/dime/closed-layout";

    public static final String URI_DIME_OPEN_LAYOUT=
                                 "http://schemas.xmlsoap.org/ws/2002/04/dime/open-layout";

    //
    // WSDL SOAP Namespace
    //
    public static final String URI_WSDL11_SOAP =
                                 "http://schemas.xmlsoap.org/wsdl/soap/";
    public static final String URI_WSDL12_SOAP =
                                 "http://schemas.xmlsoap.org/wsdl/soap12/";

    public static final String[] NS_URIS_WSDL_SOAP = {
        URI_WSDL11_SOAP,
        URI_WSDL12_SOAP
    };

    /**
     * Returns true if WSDL SOAP Namespace
     */
    public static boolean isWSDLSOAP(String s) {
        for (int i=0; i<NS_URIS_WSDL_SOAP.length; i++) {
            if (NS_URIS_WSDL_SOAP[i].equals(s)) {
                return true;
            }
        }
        return false;
    }

    // Axis Mechanism Type
    public static final String AXIS_SAX = "Axis SAX Mechanism";

    public static final String ELEM_ENVELOPE = "Envelope" ;
    public static final String ELEM_HEADER   = "Header" ;
    public static final String ELEM_BODY     = "Body" ;
    public static final String ELEM_FAULT    = "Fault" ;

    public static final String ELEM_NOTUNDERSTOOD = "NotUnderstood";
    public static final String ELEM_UPGRADE           = "Upgrade";
    public static final String ELEM_SUPPORTEDENVELOPE = "SupportedEnvelope";

    public static final String ELEM_FAULT_CODE   = "faultcode" ;
    public static final String ELEM_FAULT_STRING = "faultstring" ;
    public static final String ELEM_FAULT_DETAIL = "detail" ;
    public static final String ELEM_FAULT_ACTOR  = "faultactor" ;

    public static final String ELEM_FAULT_CODE_SOAP12 = "Code" ;
    public static final String ELEM_FAULT_VALUE_SOAP12 = "Value" ;
    public static final String ELEM_FAULT_SUBCODE_SOAP12 = "Subcode" ;
    public static final String ELEM_FAULT_REASON_SOAP12 = "Reason" ;
    public static final String ELEM_FAULT_NODE_SOAP12 = "Node" ;
    public static final String ELEM_FAULT_ROLE_SOAP12 = "Role" ;
    public static final String ELEM_FAULT_DETAIL_SOAP12 = "Detail" ;
    public static final String ELEM_TEXT_SOAP12 = "Text" ;    

    public static final String ATTR_MUST_UNDERSTAND = "mustUnderstand" ;
    public static final String ATTR_ENCODING_STYLE  = "encodingStyle" ;
    public static final String ATTR_ACTOR           = "actor" ;
    public static final String ATTR_ROLE            = "role" ;
    public static final String ATTR_ROOT            = "root" ;
    public static final String ATTR_ID              = "id" ;
    public static final String ATTR_HREF            = "href" ;
    public static final String ATTR_REF             = "ref" ;
    public static final String ATTR_QNAME           = "qname";
    public static final String ATTR_ARRAY_TYPE      = "arrayType";
    public static final String ATTR_ITEM_TYPE       = "itemType";
    public static final String ATTR_ARRAY_SIZE      = "arraySize";
    public static final String ATTR_OFFSET          = "offset";
    public static final String ATTR_POSITION        = "position";
    public static final String ATTR_TYPE            = "type";
    public static final String ATTR_HANDLERINFOCHAIN = "handlerInfoChain";

    // Fault Codes
    //////////////////////////////////////////////////////////////////////////
    public static final String FAULT_SERVER_GENERAL =
                                                   "Server.generalException";

    public static final String FAULT_SERVER_USER =
                                                   "Server.userException";

    public static final QName FAULT_VERSIONMISMATCH =
                                  new QName(URI_SOAP11_ENV, "VersionMismatch");

    public static final QName FAULT_MUSTUNDERSTAND =
                                  new QName(URI_SOAP11_ENV, "MustUnderstand");


    public static final QName FAULT_SOAP12_MUSTUNDERSTAND =
                                  new QName(URI_SOAP12_ENV, "MustUnderstand");

    public static final QName FAULT_SOAP12_VERSIONMISMATCH =
                                  new QName(URI_SOAP12_ENV, "VersionMismatch");

    public static final QName FAULT_SOAP12_DATAENCODINGUNKNOWN =
                                  new QName(URI_SOAP12_ENV, "DataEncodingUnknow");

    public static final QName FAULT_SOAP12_SENDER =
                                  new QName(URI_SOAP12_ENV, "Sender");

    public static final QName FAULT_SOAP12_RECEIVER =
                                  new QName(URI_SOAP12_ENV, "Receiver");

    // QNames
    //////////////////////////////////////////////////////////////////////////
    public static final QName QNAME_FAULTCODE =
                                         new QName("", ELEM_FAULT_CODE);
    public static final QName QNAME_FAULTSTRING =
                                       new QName("", ELEM_FAULT_STRING);
    public static final QName QNAME_FAULTACTOR =
                                        new QName("", ELEM_FAULT_ACTOR);
    public static final QName QNAME_FAULTDETAILS =
                                         new QName("", ELEM_FAULT_DETAIL);

    public static final QName QNAME_FAULTCODE_SOAP12 =
                                         new QName(URI_SOAP12_ENV, ELEM_FAULT_CODE_SOAP12);
    public static final QName QNAME_FAULTVALUE_SOAP12 =
                                         new QName(URI_SOAP12_ENV, ELEM_FAULT_VALUE_SOAP12);
    public static final QName QNAME_FAULTSUBCODE_SOAP12 =
                                         new QName(URI_SOAP12_ENV, ELEM_FAULT_SUBCODE_SOAP12);
    public static final QName QNAME_FAULTREASON_SOAP12 =
                                         new QName(URI_SOAP12_ENV, ELEM_FAULT_REASON_SOAP12);
    public static final QName QNAME_TEXT_SOAP12 =
                                         new QName(URI_SOAP12_ENV, ELEM_TEXT_SOAP12);

    public static final QName QNAME_FAULTNODE_SOAP12 =
                                         new QName(URI_SOAP12_ENV, ELEM_FAULT_NODE_SOAP12);
    public static final QName QNAME_FAULTROLE_SOAP12 =
                                         new QName(URI_SOAP12_ENV, ELEM_FAULT_ROLE_SOAP12);
    public static final QName QNAME_FAULTDETAIL_SOAP12 =
                                         new QName(URI_SOAP12_ENV, ELEM_FAULT_DETAIL_SOAP12);

    // Define qnames for the all of the XSD and SOAP-ENC encodings
    public static final QName XSD_STRING = new QName(URI_DEFAULT_SCHEMA_XSD, "string");
    public static final QName XSD_BOOLEAN = new QName(URI_DEFAULT_SCHEMA_XSD, "boolean");
    public static final QName XSD_DOUBLE = new QName(URI_DEFAULT_SCHEMA_XSD, "double");
    public static final QName XSD_FLOAT = new QName(URI_DEFAULT_SCHEMA_XSD, "float");
    public static final QName XSD_INT = new QName(URI_DEFAULT_SCHEMA_XSD, "int");
    public static final QName XSD_INTEGER = new QName(URI_DEFAULT_SCHEMA_XSD, "integer");
    public static final QName XSD_LONG = new QName(URI_DEFAULT_SCHEMA_XSD, "long");
    public static final QName XSD_SHORT = new QName(URI_DEFAULT_SCHEMA_XSD, "short");
    public static final QName XSD_BYTE = new QName(URI_DEFAULT_SCHEMA_XSD, "byte");
    public static final QName XSD_DECIMAL = new QName(URI_DEFAULT_SCHEMA_XSD, "decimal");
    public static final QName XSD_BASE64 = new QName(URI_DEFAULT_SCHEMA_XSD, "base64Binary");
    public static final QName XSD_HEXBIN = new QName(URI_DEFAULT_SCHEMA_XSD, "hexBinary");
    public static final QName XSD_ANYTYPE = new QName(URI_DEFAULT_SCHEMA_XSD, "anyType");
    public static final QName XSD_ANY = new QName(URI_DEFAULT_SCHEMA_XSD, "any");
    public static final QName XSD_QNAME = new QName(URI_DEFAULT_SCHEMA_XSD, "QName");
    public static final QName XSD_DATETIME = new QName(URI_DEFAULT_SCHEMA_XSD, "dateTime");
    public static final QName XSD_DATE = new QName(URI_DEFAULT_SCHEMA_XSD, "date");
    public static final QName XSD_TIME = new QName(URI_DEFAULT_SCHEMA_XSD, "time");
    public static final QName XSD_TIMEINSTANT1999 = new QName(URI_1999_SCHEMA_XSD, "timeInstant");
    public static final QName XSD_TIMEINSTANT2000 = new QName(URI_2000_SCHEMA_XSD, "timeInstant");

    public static final QName XSD_NORMALIZEDSTRING = new QName(URI_2001_SCHEMA_XSD, "normalizedString");
    public static final QName XSD_TOKEN = new QName(URI_2001_SCHEMA_XSD, "token");

    public static final QName XSD_UNSIGNEDLONG = new QName(URI_2001_SCHEMA_XSD, "unsignedLong");
    public static final QName XSD_UNSIGNEDINT = new QName(URI_2001_SCHEMA_XSD, "unsignedInt");
    public static final QName XSD_UNSIGNEDSHORT = new QName(URI_2001_SCHEMA_XSD, "unsignedShort");
    public static final QName XSD_UNSIGNEDBYTE = new QName(URI_2001_SCHEMA_XSD, "unsignedByte");
    public static final QName XSD_POSITIVEINTEGER = new QName(URI_2001_SCHEMA_XSD, "positiveInteger");
    public static final QName XSD_NEGATIVEINTEGER = new QName(URI_2001_SCHEMA_XSD, "negativeInteger");
    public static final QName XSD_NONNEGATIVEINTEGER = new QName(URI_2001_SCHEMA_XSD, "nonNegativeInteger");
    public static final QName XSD_NONPOSITIVEINTEGER = new QName(URI_2001_SCHEMA_XSD, "nonPositiveInteger");

    public static final QName XSD_YEARMONTH = new QName(URI_2001_SCHEMA_XSD, "gYearMonth");
    public static final QName XSD_MONTHDAY = new QName(URI_2001_SCHEMA_XSD, "gMonthDay");
    public static final QName XSD_YEAR = new QName(URI_2001_SCHEMA_XSD, "gYear");
    public static final QName XSD_MONTH = new QName(URI_2001_SCHEMA_XSD, "gMonth");
    public static final QName XSD_DAY = new QName(URI_2001_SCHEMA_XSD, "gDay");
    public static final QName XSD_DURATION = new QName(URI_2001_SCHEMA_XSD, "duration");

    public static final QName XSD_NAME = new QName(URI_2001_SCHEMA_XSD, "Name");
    public static final QName XSD_NCNAME = new QName(URI_2001_SCHEMA_XSD, "NCName");
    public static final QName XSD_NMTOKEN = new QName(URI_2001_SCHEMA_XSD, "NMTOKEN");
    public static final QName XSD_NMTOKENS = new QName(URI_2001_SCHEMA_XSD, "NMTOKENS");
    public static final QName XSD_NOTATION = new QName(URI_2001_SCHEMA_XSD, "NOTATION");
    public static final QName XSD_ENTITY = new QName(URI_2001_SCHEMA_XSD, "ENTITY");
    public static final QName XSD_ENTITIES = new QName(URI_2001_SCHEMA_XSD, "ENTITIES");
    public static final QName XSD_IDREF = new QName(URI_2001_SCHEMA_XSD, "IDREF");
    public static final QName XSD_IDREFS = new QName(URI_2001_SCHEMA_XSD, "IDREFS");
    public static final QName XSD_ANYURI = new QName(URI_2001_SCHEMA_XSD, "anyURI");
    public static final QName XSD_LANGUAGE = new QName(URI_2001_SCHEMA_XSD, "language");
    public static final QName XSD_ID = new QName(URI_2001_SCHEMA_XSD, "ID");
    public static final QName XSD_SCHEMA = new QName(URI_2001_SCHEMA_XSD, "schema");

    public static final QName XML_LANG = new QName(NS_URI_XML, "lang");

    public static final QName SOAP_BASE64 = new QName(URI_DEFAULT_SOAP_ENC, "base64");
    public static final QName SOAP_BASE64BINARY = new QName(URI_DEFAULT_SOAP_ENC, "base64Binary");
    public static final QName SOAP_STRING = new QName(URI_DEFAULT_SOAP_ENC, "string");
    public static final QName SOAP_BOOLEAN = new QName(URI_DEFAULT_SOAP_ENC, "boolean");
    public static final QName SOAP_DOUBLE = new QName(URI_DEFAULT_SOAP_ENC, "double");
    public static final QName SOAP_FLOAT = new QName(URI_DEFAULT_SOAP_ENC, "float");
    public static final QName SOAP_INT = new QName(URI_DEFAULT_SOAP_ENC, "int");
    public static final QName SOAP_LONG = new QName(URI_DEFAULT_SOAP_ENC, "long");
    public static final QName SOAP_SHORT = new QName(URI_DEFAULT_SOAP_ENC, "short");
    public static final QName SOAP_BYTE = new QName(URI_DEFAULT_SOAP_ENC, "byte");
    public static final QName SOAP_INTEGER = new QName(URI_DEFAULT_SOAP_ENC, "integer");
    public static final QName SOAP_DECIMAL = new QName(URI_DEFAULT_SOAP_ENC, "decimal");
    public static final QName SOAP_ARRAY = new QName(URI_DEFAULT_SOAP_ENC, "Array");
    public static final QName SOAP_ARRAY12 = new QName(URI_SOAP12_ENC, "Array");

    public static final QName SOAP_MAP = new QName(NS_URI_XMLSOAP, "Map");
    public static final QName SOAP_ELEMENT = new QName(NS_URI_XMLSOAP, "Element");
    public static final QName SOAP_VECTOR = new QName(NS_URI_XMLSOAP, "Vector");
    public static final QName MIME_IMAGE = new QName(NS_URI_XMLSOAP, "Image");
    public static final QName MIME_PLAINTEXT = new QName(NS_URI_XMLSOAP, "PlainText");
    public static final QName MIME_MULTIPART = new QName(NS_URI_XMLSOAP, "Multipart");
    public static final QName MIME_SOURCE = new QName(NS_URI_XMLSOAP, "Source");
    public static final QName MIME_OCTETSTREAM = new QName(NS_URI_XMLSOAP, "octetstream");
    public static final QName MIME_DATA_HANDLER = new QName(NS_URI_XMLSOAP, "DataHandler");


    public static final QName QNAME_LITERAL_ITEM = new QName(URI_LITERAL_ENC,"item");
    public static final QName QNAME_RPC_RESULT = new QName(URI_SOAP12_RPC,"result");

    /**
     * QName of stack trace element in an axis fault detail.
     */
    public static final QName QNAME_FAULTDETAIL_STACKTRACE = new QName(NS_URI_AXIS,"stackTrace");

    /**
     * QName of exception Name element in an axis fault detail.
     * Do not use - this is for pre-1.0 server->client exceptions.
     */
    public static final QName QNAME_FAULTDETAIL_EXCEPTIONNAME = new QName(NS_URI_AXIS, "exceptionName");

    /**
     * QName of stack trace element in an axis fault detail.
     */
    public static final QName QNAME_FAULTDETAIL_RUNTIMEEXCEPTION = new QName(NS_URI_AXIS, "isRuntimeException");

    //QNames of well known faults
    /**
     * the no-service fault value
     */
    public static final QName QNAME_NO_SERVICE_FAULT_CODE
            = new QName(NS_URI_AXIS, "Server.NoService");

    // Misc Strings
    //////////////////////////////////////////////////////////////////////////

    // Where to put those pesky JWS classes
    public static final String MC_JWS_CLASSDIR = "jws.classDir" ;
    // Where we're rooted
    public static final String MC_HOME_DIR = "home.dir";

    // Relative path of the request URL (ie. http://.../axis/a.jws = /a.jws
    public static final String MC_RELATIVE_PATH = "path";

    // MessageContext param for the engine's path
    public static final String MC_REALPATH = "realpath";
    // MessageContext param for the location of config files
    public static final String MC_CONFIGPATH = "configPath";
    // MessageContext param for the IP of the calling client
    public static final String MC_REMOTE_ADDR = "remoteaddr";
    // When invoked from a servlet, per JAX-RPC, we need  a
    // ServletEndpointContext object.  This is where it lives.
    public static final String MC_SERVLET_ENDPOINT_CONTEXT = "servletEndpointContext";

    /**
     * what the extension of JWS files is. If changing this, note that
     * AxisServlet has an xdoclet declaration in the class javadocs that
     * also needs updating.
     */
    public static final String JWS_DEFAULT_FILE_EXTENSION = ".jws";
}
