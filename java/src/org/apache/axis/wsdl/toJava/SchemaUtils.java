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
package org.apache.axis.wsdl.toJava;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

import javax.wsdl.QName;
import javax.wsdl.Fault;
import javax.wsdl.Message;
import javax.xml.rpc.holders.BooleanHolder;
import javax.xml.rpc.holders.IntHolder;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.Collator;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.axis.Constants;

/**
 * This class contains static utility methods specifically for schema type queries.
 *
 * @author Rich Scheuerle  (scheu@us.ibm.com)
 */
public class SchemaUtils {

    /**
     * If the specified node represents a supported JAX-RPC complexType/element,
     * a Vector is returned which contains the child element types and
     * child element names.  The even indices are the element types (TypeEntry) and
     * the odd indices are the corresponding names (Strings).
     * If the specified node is not a supported JAX-RPC complexType/element
     * null is returned.
     */
    public static Vector getComplexElementTypesAndNames(Node node, SymbolTable symbolTable) {
        if (node == null) {
            return null;
        }

        // If the node kind is an element, dive into it.
        QName nodeKind = Utils.getNodeQName(node);
        if (nodeKind != null &&
            nodeKind.getLocalPart().equals("element") &&
            Constants.isSchemaXSD(nodeKind.getNamespaceURI())) {
            NodeList children = node.getChildNodes();
            Node complexNode = null;
            for (int j = 0; j < children.getLength() && complexNode == null; j++) {
                QName complexKind = Utils.getNodeQName(children.item(j));
                if (complexKind != null &&
                    complexKind.getLocalPart().equals("complexType") &&
                    Constants.isSchemaXSD(complexKind.getNamespaceURI())) {
                    complexNode = children.item(j);
                    node = complexNode;
                }
            }
        }

        // Expecting a schema complexType
        nodeKind = Utils.getNodeQName(node);
        if (nodeKind != null &&
            nodeKind.getLocalPart().equals("complexType") &&
            Constants.isSchemaXSD(nodeKind.getNamespaceURI())) {

            // Under the complexType there could be complexContent &
            // extension elements if this is a derived type.  Skip over these.
            NodeList children = node.getChildNodes();
            Node complexContent = null;
            Node extension = null;
            for (int j = 0; j < children.getLength() && complexContent == null; j++) {
                QName complexContentKind = Utils.getNodeQName(children.item(j));
                if (complexContentKind != null &&
                    complexContentKind.getLocalPart().equals("complexContent") &&
                    Constants.isSchemaXSD(complexContentKind.getNamespaceURI()))
                    complexContent = children.item(j);
            }
            if (complexContent != null) {
                children = complexContent.getChildNodes();
                for (int j = 0; j < children.getLength() && extension == null; j++) {
                    QName extensionKind = Utils.getNodeQName(children.item(j));
                    if (extensionKind != null &&
                        extensionKind.getLocalPart().equals("extension") &&
                        Constants.isSchemaXSD(extensionKind.getNamespaceURI()))
                        extension = children.item(j);
                }
            }
            if (extension != null) {
                node = extension;  // Skip over complexContent and extension
            }


            // Under the complexType (or extension) there should be a sequence or all group node.
            // (There may be other #text nodes, which we will ignore).
            children = node.getChildNodes();
            Node groupNode = null;
            for (int j = 0; j < children.getLength() && groupNode == null; j++) {
                QName groupKind = Utils.getNodeQName(children.item(j));
                if (groupKind != null &&
                    (groupKind.getLocalPart().equals("sequence") ||
                     groupKind.getLocalPart().equals("all")) &&
                    Constants.isSchemaXSD(groupKind.getNamespaceURI()))
                    groupNode = children.item(j);
            }

            if (groupNode == null) {
                return new Vector();
            }
            if (groupNode != null) {

                // Process each of the choice or element nodes under the sequence/all node
                Vector v = new Vector();
                NodeList elements = groupNode.getChildNodes();
                for (int i=0; i < elements.getLength(); i++) {
                    QName elementKind = Utils.getNodeQName(elements.item(i));
                    if (elementKind != null &&
                        Constants.isSchemaXSD(elementKind.getNamespaceURI())) {
                        if ( elementKind.getLocalPart().equals("element")) {
                            v.addAll(processChildElementNode(elements.item(i), symbolTable));
                        } else if (elementKind.getLocalPart().equals("choice")) {
                            Vector choiceElems = processChoiceNode(elements.item(i), symbolTable);
                            v.addAll(choiceElems);
                        }
                    }
                }
                return v;
            }
        }
        return null;
    }

    /**
     * Invoked by getComplexElementTypesAndNames to get the child element types
     * and child element names underneath a Choice Node
     */
    private static Vector processChoiceNode(Node choiceNode, SymbolTable symbolTable) {
        Vector v = new Vector();
        NodeList children = choiceNode.getChildNodes();
        for (int j = 0; j < children.getLength(); j++) {
            QName subNodeKind = Utils.getNodeQName(children.item(j));
            if (subNodeKind != null &&
                Constants.isSchemaXSD(subNodeKind.getNamespaceURI())) {
                if (subNodeKind.getLocalPart().equals("choice")) {
                    v.addAll(processChoiceNode(children.item(j), symbolTable));
                } else if (subNodeKind.getLocalPart().equals("sequence")) {
                    v.addAll(processSequenceNode(children.item(j), symbolTable));
                } else if (subNodeKind.getLocalPart().equals("group")) {
                    v.addAll(processGroupNode(children.item(j), symbolTable));
                } else if (subNodeKind.getLocalPart().equals("element")) {
                    v.addAll(processChildElementNode(children.item(j), symbolTable));
                }
            }
        }
        return v;
    }

    /**
     * Invoked by getComplexElementTypesAndNames to get the child element types
     * and child element names underneath a Sequence Node
     */
    private static Vector processSequenceNode(Node sequenceNode, SymbolTable symbolTable) {
        Vector v = new Vector();
        NodeList children = sequenceNode.getChildNodes();
        for (int j = 0; j < children.getLength(); j++) {
            QName subNodeKind = Utils.getNodeQName(children.item(j));
            if (subNodeKind != null &&
                Constants.isSchemaXSD(subNodeKind.getNamespaceURI())) {
                if (subNodeKind.getLocalPart().equals("choice")) {
                    v.addAll(processChoiceNode(children.item(j), symbolTable));
                } else if (subNodeKind.getLocalPart().equals("sequence")) {
                    v.addAll(processSequenceNode(children.item(j), symbolTable));
                } else if (subNodeKind.getLocalPart().equals("group")) {
                    v.addAll(processGroupNode(children.item(j), symbolTable));
                } else if (subNodeKind.getLocalPart().equals("element")) {
                    v.addAll(processChildElementNode(children.item(j), symbolTable));
                }
            }
        }
        return v;
    }

    /**
     * Invoked by getComplexElementTypesAndNames to get the child element types
     * and child element names underneath a group node.
     * (Currently the code only supports a defined group it does not
     * support a group that references a previously defined group)
     */
    private static Vector processGroupNode(Node groupNode, SymbolTable symbolTable) {
        Vector v = new Vector();
        NodeList children = groupNode.getChildNodes();
        for (int j = 0; j < children.getLength(); j++) {
            QName subNodeKind = Utils.getNodeQName(children.item(j));
            if (subNodeKind != null &&
                Constants.isSchemaXSD(subNodeKind.getNamespaceURI())) {
                if (subNodeKind.getLocalPart().equals("choice")) {
                    v.addAll(processChoiceNode(children.item(j), symbolTable));
                } else if (subNodeKind.getLocalPart().equals("sequence")) {
                    v.addAll(processSequenceNode(children.item(j), symbolTable));
                } else if (subNodeKind.getLocalPart().equals("all")) {
                    v.addAll(processAllNode(children.item(j), symbolTable));
                }
            }
        }
        return v;
    }

    /**
     * Invoked by getComplexElementTypesAndNames to get the child element types
     * and child element names underneath an all node.
     */
    private static Vector processAllNode(Node allNode, SymbolTable symbolTable) {
        Vector v = new Vector();
        NodeList children = allNode.getChildNodes();
        for (int j = 0; j < children.getLength(); j++) {
            QName subNodeKind = Utils.getNodeQName(children.item(j));
            if (subNodeKind != null &&
                Constants.isSchemaXSD(subNodeKind.getNamespaceURI())) {
                if (subNodeKind.getLocalPart().equals("element")) {
                    v.addAll(processChildElementNode(children.item(j), symbolTable));
                }
            }
        }
        return v;
    }


    /**
     * Invoked by getComplexElementTypesAndNames to get the child element type
     * and child element name for a child element node.
     *
     * If the specified node represents a supported JAX-RPC child element,
     * a Vector is returned which contains the child element type and
     * child element name.  The 0th index is the element types (TypeEntry) and
     * the 1st index is the corresponding name (Strings).
     */
    private static Vector processChildElementNode(Node elementNode, SymbolTable symbolTable) {
        Vector v = new Vector();
        // Get the name and type qnames.
        // The name of the element is the local part of the name's qname.
        // The type qname is used to locate the TypeEntry, which is then
        // used to retrieve the proper java name of the type.
        QName nodeName = Utils.getNodeNameQName(elementNode);
        BooleanHolder forElement = new BooleanHolder();
        QName nodeType = Utils.getNodeTypeRefQName(elementNode, forElement);
        if (nodeType == null) { // The element may use an anonymous type
             nodeType = nodeName;
             forElement.value = false;
        }

        TypeEntry type = (TypeEntry) symbolTable.getTypeEntry(nodeType, forElement.value);
        if (type != null) {
            v.add(type);
            v.add(nodeName.getLocalPart());
        }
        return v;
    }

    /**
     * If the specified node represents a supported JAX-RPC complexType/element
     * which extends another complexType.  The Type of the base is returned.
     */
    public static TypeEntry getComplexElementExtensionBase(Node node, SymbolTable symbolTable) {
        if (node == null) {
            return null;
        }

        // If the node kind is an element, dive into it.
        QName nodeKind = Utils.getNodeQName(node);
        if (nodeKind != null &&
            nodeKind.getLocalPart().equals("element") &&
            Constants.isSchemaXSD(nodeKind.getNamespaceURI())) {
            NodeList children = node.getChildNodes();
            Node complexNode = null;
            for (int j = 0; j < children.getLength() && complexNode == null; j++) {
                QName complexKind = Utils.getNodeQName(children.item(j));
                if (complexKind != null &&
                    complexKind.getLocalPart().equals("complexType") &&
                    Constants.isSchemaXSD(complexKind.getNamespaceURI())) {
                    complexNode = children.item(j);
                    node = complexNode;
                }
            }
        }

        // Expecting a schema complexType
        nodeKind = Utils.getNodeQName(node);
        if (nodeKind != null &&
            nodeKind.getLocalPart().equals("complexType") &&
            Constants.isSchemaXSD(nodeKind.getNamespaceURI())) {

            // Under the complexType there could be should be a complexContent &
            // extension elements if this is a derived type. 
            NodeList children = node.getChildNodes();
            Node complexContent = null;
            Node extension = null;
            for (int j = 0; j < children.getLength() && complexContent == null; j++) {
                QName complexContentKind = Utils.getNodeQName(children.item(j));
                if (complexContentKind != null &&
                    complexContentKind.getLocalPart().equals("complexContent") &&
                    Constants.isSchemaXSD(complexContentKind.getNamespaceURI()))
                    complexContent = children.item(j);
            }
            if (complexContent != null) {
                children = complexContent.getChildNodes();
                for (int j = 0; j < children.getLength() && extension == null; j++) {
                    QName extensionKind = Utils.getNodeQName(children.item(j));
                    if (extensionKind != null &&
                        extensionKind.getLocalPart().equals("extension") &&
                        Constants.isSchemaXSD(extensionKind.getNamespaceURI()))
                        extension = children.item(j);
                }
            }
            if (extension == null) {
                return null;  // No extension                               
            }

            // Get the QName of the extension base
            QName extendsType = Utils.getNodeTypeRefQName(extension, "base");
            if (extendsType == null) {
                return null; // No extension base
            }
            // Return associated Type
            return (TypeEntry) symbolTable.getType(extendsType);
        }
        return null;
    }

    /**
     * If the specified node represents a supported JAX-RPC enumeration,
     * a Vector is returned which contains the base type and the enumeration values.
     * The first element in the vector is the base type (an TypeEntry).
     * Subsequent elements are values (Strings).
     * If this is not an enumeration, null is returned.
     */
    public static Vector getEnumerationBaseAndValues(Node node, SymbolTable symbolTable) {
        if (node == null) {
            return null;
        }

        // If the node kind is an element, dive into it.
        QName nodeKind = Utils.getNodeQName(node);
        if (nodeKind != null &&
            nodeKind.getLocalPart().equals("element") &&
            Constants.isSchemaXSD(nodeKind.getNamespaceURI())) {
            NodeList children = node.getChildNodes();
            Node simpleNode = null;
            for (int j = 0; j < children.getLength() && simpleNode == null; j++) {
                QName simpleKind = Utils.getNodeQName(children.item(j));
                if (simpleKind != null &&
                    simpleKind.getLocalPart().equals("simpleType") &&
                    Constants.isSchemaXSD(simpleKind.getNamespaceURI())) {
                    simpleNode = children.item(j);
                    node = simpleNode;
                }
            }
        }
        // Get the node kind, expecting a schema simpleType
        nodeKind = Utils.getNodeQName(node);
        if (nodeKind != null &&
            nodeKind.getLocalPart().equals("simpleType") &&
            Constants.isSchemaXSD(nodeKind.getNamespaceURI())) {

            // Under the simpleType there should be a restriction.
            // (There may be other #text nodes, which we will ignore).
            NodeList children = node.getChildNodes();
            Node restrictionNode = null;
            for (int j = 0; j < children.getLength() && restrictionNode == null; j++) {
                QName restrictionKind = Utils.getNodeQName(children.item(j));
                if (restrictionKind != null &&
                    restrictionKind.getLocalPart().equals("restriction") &&
                    Constants.isSchemaXSD(restrictionKind.getNamespaceURI()))
                    restrictionNode = children.item(j);
            }

            // The restriction node indicates the type being restricted
            // (the base attribute contains this type).
            // The base type must be a built-in type, and not boolean
            TypeEntry baseEType = null;
            if (restrictionNode != null) {
                QName baseType = Utils.getNodeTypeRefQName(restrictionNode, "base");
                baseEType = symbolTable.getType(baseType);
                if (baseEType != null) {
                    String javaName = baseEType.getName();
                    if (javaName.equals("java.lang.String") ||
                        javaName.equals("int") ||
                        javaName.equals("long") ||
                        javaName.equals("short") ||
                        javaName.equals("float") ||
                        javaName.equals("double") ||
                        javaName.equals("byte"))
                        ; // Okay Type
                    else
                        baseEType = null;
                }
            }

            // Process the enumeration elements underneath the restriction node
            if (baseEType != null && restrictionNode != null) {

                Vector v = new Vector();
                v.add(baseEType);
                NodeList enums = restrictionNode.getChildNodes();
                for (int i=0; i < enums.getLength(); i++) {
                    QName enumKind = Utils.getNodeQName(enums.item(i));
                    if (enumKind != null &&
                        enumKind.getLocalPart().equals("enumeration") &&
                        Constants.isSchemaXSD(enumKind.getNamespaceURI())) {

                        // Put the enum value in the vector.
                        Node enumNode = enums.item(i);
                        String value = Utils.getAttribute(enumNode, "value");
                        if (value != null) {
                            v.add(value);
                        }
                    }
                }
                return v;
            }
        }
        return null;
    }

    /**
     * If the specified node represents a 'normal' non-enumeration simpleType,
     * the QName of the simpleType base is returned.
     */
    public static QName getSimpleTypeBase(Node node, SymbolTable symbolTable) {
        QName baseQName = null;

        if (node == null) {
            return null;
        }

        // If the node kind is an element, dive into it.
        QName nodeKind = Utils.getNodeQName(node);
        if (nodeKind != null &&
            nodeKind.getLocalPart().equals("element") &&
            Constants.isSchemaXSD(nodeKind.getNamespaceURI())) {
            NodeList children = node.getChildNodes();
            Node simpleNode = null;
            for (int j = 0; j < children.getLength() && simpleNode == null; j++) {
                QName simpleKind = Utils.getNodeQName(children.item(j));
                if (simpleKind != null &&
                    simpleKind.getLocalPart().equals("simpleType") &&
                    Constants.isSchemaXSD(simpleKind.getNamespaceURI())) {
                    simpleNode = children.item(j);
                    node = simpleNode;
                }
            }
        }
        // Get the node kind, expecting a schema simpleType
        nodeKind = Utils.getNodeQName(node);
        if (nodeKind != null &&
            nodeKind.getLocalPart().equals("simpleType") &&
            Constants.isSchemaXSD(nodeKind.getNamespaceURI())) {

            // Under the simpleType there should be a restriction.
            // (There may be other #text nodes, which we will ignore).
            NodeList children = node.getChildNodes();
            Node restrictionNode = null;
            for (int j = 0; j < children.getLength() && restrictionNode == null; j++) {
                QName restrictionKind = Utils.getNodeQName(children.item(j));
                if (restrictionKind != null &&
                    restrictionKind.getLocalPart().equals("restriction") &&
                    Constants.isSchemaXSD(restrictionKind.getNamespaceURI()))
                    restrictionNode = children.item(j);
            }

            // The restriction node indicates the type being restricted
            // (the base attribute contains this type).
            
            if (restrictionNode != null) {
                baseQName = Utils.getNodeTypeRefQName(restrictionNode, "base");
            }
            
            // Look for enumeration elements underneath the restriction node
            if (baseQName != null && restrictionNode != null) {
                NodeList enums = restrictionNode.getChildNodes();
                for (int i=0; i < enums.getLength(); i++) {
                    QName enumKind = Utils.getNodeQName(enums.item(i));
                    if (enumKind != null &&
                        enumKind.getLocalPart().equals("enumeration") &&
                        Constants.isSchemaXSD(enumKind.getNamespaceURI())) {
                        
                        // Found an enumeration, this isn't a 
                        // 'normal' simple type.
                        return null;
                    }
                }
            }
        }
        return baseQName;
    }

    /**
     * Returns the contained restriction or extension node underneath
     * the specified node.  Returns null if not found
     */
    public static Node getRestrictionOrExtensionNode(Node node) {
        Node re = null;
        if (node == null) {
            return re;
        }

        // If the node kind is an element, dive into it.
        QName nodeKind = Utils.getNodeQName(node);
        if (nodeKind != null &&
            nodeKind.getLocalPart().equals("element") &&
            Constants.isSchemaXSD(nodeKind.getNamespaceURI())) {
            NodeList children = node.getChildNodes();
            Node node2 = null;
            for (int j = 0; j < children.getLength() && node2 == null; j++) {
                QName kind2 = Utils.getNodeQName(children.item(j));
                if (kind2 != null &&
                    (kind2.getLocalPart().equals("simpleType") ||
                     kind2.getLocalPart().equals("complexType")) &&
                    Constants.isSchemaXSD(kind2.getNamespaceURI())) {
                    node2 = children.item(j);
                    node = node2;
                }
            }
        }
        // Get the node kind, expecting a schema simpleType
        nodeKind = Utils.getNodeQName(node);
        if (nodeKind != null &&
            (nodeKind.getLocalPart().equals("simpleType") ||
             nodeKind.getLocalPart().equals("complexType")) &&
            Constants.isSchemaXSD(nodeKind.getNamespaceURI())) {

            // Under the complexType there could be a complexContent.
            NodeList children = node.getChildNodes();
            Node complexContent = null;
            Node extension = null;
            if (nodeKind.getLocalPart().equals("complexType")) {
                for (int j = 0; j < children.getLength() && complexContent == null; j++) {
                    QName complexContentKind = Utils.getNodeQName(children.item(j));
                    if (complexContentKind != null &&
                        complexContentKind.getLocalPart().equals("complexContent") &&
                        Constants.isSchemaXSD(complexContentKind.getNamespaceURI()))
                        complexContent = children.item(j);
                }
                node = complexContent;
            }
            // Now get the extension or restriction node
            if (node != null) {
                children = node.getChildNodes();
                for (int j = 0; j < children.getLength() && re == null; j++) {
                    QName reKind = Utils.getNodeQName(children.item(j));
                    if (reKind != null &&
                        (reKind.getLocalPart().equals("extension") ||
                         reKind.getLocalPart().equals("restriction")) &&
                        Constants.isSchemaXSD(reKind.getNamespaceURI()))
                        re = children.item(j);
                }
            }
        }
            
        return re;
    }

    /**
     * If the specified node represents an array encoding of one of the following
     * forms, then return the qname repesenting the element type of the array.
     * @param node is the node
     * @param dims is the output value that contains the number of dimensions if return is not null
     * @return QName or null
     */
    public static QName getArrayElementQName(Node node, IntHolder dims) {
        dims.value = 1;  // assume 1 dimension
        QName qName = getCollectionElementQName(node);
        if (qName == null) {
            qName = getArrayElementQName_JAXRPC(node, dims);
        }
        return qName;
    }

    /**
     * If the specified node represents an element that refernces a collection
     * then return the qname repesenting the element type of the collection.
     *
     *  <xsd:element name="alias" type="xsd:string" maxOccurs="unbounded"/>
     *
     */
    private static QName getCollectionElementQName(Node node) {
        if (node == null) {
            return null;
        }

        // If the node kind is an element, dive get its type.
        QName nodeKind = Utils.getNodeQName(node);
        if (nodeKind != null &&
            nodeKind.getLocalPart().equals("element") &&
            Constants.isSchemaXSD(nodeKind.getNamespaceURI())) {

            // Get the qName of just the type.
            // The compare it against the full type of the node, which
            // takes into account maxOccurs and could return a collection type.
            // If different, return just the type (which is the collection element type).
            QName justTypeQName = Utils.getNodeTypeRefQName(node, "type");
            if (justTypeQName != null) {
                QName fullTypeQName = Utils.getNodeTypeRefQName(node, new BooleanHolder());
                if (justTypeQName != fullTypeQName)
                    return justTypeQName;
            }
        }
        return null;
    }

    /**
     * If the specified node represents an array encoding of one of the following
     * forms, then return the qname repesenting the element type of the array.
     *
     * @param node is the node
     * @param dims is the output value that contains the number of dimensions if return is not null
     * @return QName or null
     *
     * JAX-RPC Style 2:
     *<xsd:complexType name="hobbyArray">
     *  <xsd:complexContent>
     *    <xsd:restriction base="soapenc:Array">
     *      <xsd:attribute ref="soapenc:arrayType" wsdl:arrayType="xsd:string[]"/>
     *    </xsd:restriction>
     *  </xsd:complexContent>
     *</xsd:complexType>
     *
     * JAX-RPC Style 3:
     *<xsd:complexType name="petArray">
     *  <xsd:complexContent>
     *    <xsd:restriction base="soapenc:Array">
     *      <xsd:sequence>
     *        <xsd:element name="alias" type="xsd:string" maxOccurs="unbounded"/>
     *      </xsd:sequence>
     *    </xsd:restriction>
     *  </xsd:complexContent>
     *</xsd:complexType>
     *
     */
    private static QName getArrayElementQName_JAXRPC(Node node, IntHolder dims) {
        dims.value = 0;  // Assume 0
        if (node == null) {
            return null;
        }

        // If the node kind is an element, dive into it.
        QName nodeKind = Utils.getNodeQName(node);
        if (nodeKind != null &&
            nodeKind.getLocalPart().equals("element") &&
            Constants.isSchemaXSD(nodeKind.getNamespaceURI())) {
            NodeList children = node.getChildNodes();
            Node complexNode = null;
            for (int j = 0; j < children.getLength() && complexNode == null; j++) {
                QName complexKind = Utils.getNodeQName(children.item(j));
                if (complexKind != null &&
                    complexKind.getLocalPart().equals("complexType") &&
                    Constants.isSchemaXSD(complexKind.getNamespaceURI())) {
                    complexNode = children.item(j);
                    node = complexNode;
                }
            }
        }
        // Get the node kind, expecting a schema complexType
        nodeKind = Utils.getNodeQName(node);
        if (nodeKind != null &&
            nodeKind.getLocalPart().equals("complexType") &&
            Constants.isSchemaXSD(nodeKind.getNamespaceURI())) {

            // Under the complexType there should be a complexContent.
            // (There may be other #text nodes, which we will ignore).
            NodeList children = node.getChildNodes();
            Node complexContentNode = null;
            for (int j = 0; j < children.getLength() && complexContentNode == null; j++) {
                QName complexContentKind = Utils.getNodeQName(children.item(j));
                if (complexContentKind != null &&
                    complexContentKind.getLocalPart().equals("complexContent") &&
                    Constants.isSchemaXSD(complexContentKind.getNamespaceURI()))
                    complexContentNode = children.item(j);
            }

            // Under the complexContent there should be a restriction.
            // (There may be other #text nodes, which we will ignore).
            Node restrictionNode = null;
            if (complexContentNode != null) {
                children = complexContentNode.getChildNodes();
                for (int j = 0; j < children.getLength() && restrictionNode == null; j++) {
                    QName restrictionKind = Utils.getNodeQName(children.item(j));
                    if (restrictionKind != null &&
                        restrictionKind.getLocalPart().equals("restriction") &&
                        Constants.isSchemaXSD(restrictionKind.getNamespaceURI()))
                        restrictionNode = children.item(j);
                }
            }

            // The restriction node must have a base of soapenc:Array.  
            QName baseType = null;
            if (restrictionNode != null) {
                baseType = Utils.getNodeTypeRefQName(restrictionNode, "base");
                if (baseType != null &&
                    baseType.getLocalPart().equals("Array") &&
                    Constants.isSOAP_ENC(baseType.getNamespaceURI()))
                    ; // Okay
                else
                    baseType = null;  // Did not find base=soapenc:Array
            }

            
            // Under the restriction there should be an attribute OR a sequence/all group node.
            // (There may be other #text nodes, which we will ignore).
            Node groupNode = null;
            Node attributeNode = null;
            if (baseType != null) {
                children = restrictionNode.getChildNodes();
                for (int j = 0;
                     j < children.getLength() && groupNode == null && attributeNode == null;
                     j++) {
                    QName kind = Utils.getNodeQName(children.item(j));
                    if (kind != null &&
                        (kind.getLocalPart().equals("sequence") ||
                         kind.getLocalPart().equals("all")) &&
                        Constants.isSchemaXSD(kind.getNamespaceURI())) {
                        groupNode = children.item(j);
                    }
                    if (kind != null &&
                        kind.getLocalPart().equals("attribute") &&
                        Constants.isSchemaXSD(kind.getNamespaceURI())) {
                        // If the attribute node does not have ref="soapenc:arrayType"
                        // then keep looking.
                        QName refQName = Utils.getNodeTypeRefQName(children.item(j), "ref");
                        if (refQName != null &&
                            refQName.getLocalPart().equals("arrayType") &&
                            Constants.isSOAP_ENC(refQName.getNamespaceURI())) {
                            attributeNode = children.item(j);
                        }
                    }
                }
            }

            // If there is an attribute node, look at wsdl:arrayType to get the element type
            if (attributeNode != null) {
                String wsdlArrayTypeValue = null;
                Vector attrs = Utils.getAttributesWithLocalName(attributeNode, "arrayType");
                for (int i=0; i < attrs.size() && wsdlArrayTypeValue == null; i++) {
                    Node attrNode = (Node) attrs.elementAt(i);
                    String attrName = attrNode.getNodeName();
                    QName attrQName = Utils.getQNameFromPrefixedName(attributeNode, attrName);
                    if (Constants.isWSDL(attrQName.getNamespaceURI())) {
                        wsdlArrayTypeValue = attrNode.getNodeValue();
                    }
                }

                // The value could have any number of [] or [,] on the end
                // Strip these off to get the prefixed name.
                // The convert the prefixed name into a qname.
                // Count the number of [ and , to get the dim information.
                if (wsdlArrayTypeValue != null) {
                    int i = wsdlArrayTypeValue.indexOf('[');
                    if (i > 0) {
                        String prefixedName = wsdlArrayTypeValue.substring(0,i);
                        String mangledString = wsdlArrayTypeValue.replace(',', '[');
                        dims.value = 0;
                        int index = mangledString.indexOf('[');
                        while (index > 0) {
                            dims.value++;
                            index = mangledString.indexOf('[',index+1);
                        }
                        
                        return Utils.getQNameFromPrefixedName(restrictionNode, prefixedName);
                    }
                }
            } else if (groupNode != null) {

                // Get the first element node under the group node.       
                NodeList elements = groupNode.getChildNodes();
                Node elementNode = null;
                for (int i=0; i < elements.getLength() && elementNode == null; i++) {
                    QName elementKind = Utils.getNodeQName(elements.item(i));
                    if (elementKind != null &&
                        elementKind.getLocalPart().equals("element") &&
                        Constants.isSchemaXSD(elementKind.getNamespaceURI())) {
                        elementNode = elements.item(i);
                    }
                }
                 
                // The element node should have maxOccurs="unbounded" and
                // a type
                if (elementNode != null) {
                    String maxOccursValue = Utils.getAttribute(elementNode, "maxOccurs");
                    if (maxOccursValue != null &&
                        maxOccursValue.equalsIgnoreCase("unbounded")) {
                        // Get the QName of just the type
                        dims.value = 1;
                        return Utils.getNodeTypeRefQName(elementNode, "type");
                    }
                }
            }
            
        }
        return null;
    }

    /**
     * Return the attribute names and types if any in the node
     * The even indices are the element types (TypeEntry) and
     * the odd indices are the corresponding names (Strings).
     * 
     * Example:
     * <complexType name="Person">
     *   <sequence>
     *     <element minOccurs="1" maxOccurs="1" name="Age" type="double" />
     *     <element minOccurs="1" maxOccurs="1" name="ID" type="xsd:float" />
     *   </sequence>
     *   <attribute name="Name" type="string" />
     *   <attribute name="Male" type="boolean" />
     * </complexType>
     * 
     */ 
    public static Vector getComplexElementAttributes(Node node, 
                                                     SymbolTable symbolTable) 
    {
        Vector v = null;    // return value
        
        if (node == null) {
            return null;
        }
        
        // examine children of the node for <attribute> elements
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            QName nodeKind = Utils.getNodeQName(child);
            if (nodeKind == null ||
                ! nodeKind.getLocalPart().equals("attribute"))
                continue;
            
            // we have an attribute node
            if (v == null)
                v = new Vector();
            
            // type
            QName typeAttr = Utils.getNodeTypeRefQName(child, "type");
            TypeEntry type = symbolTable.getTypeEntry(typeAttr, false);
            // name
            QName name = Utils.getNodeNameQName(child);
            // add type and name to vector, skip it if we couldn't parse it
            // XXX - this may need to be revisited.
            if (type != null && name != null) {
                v.add(type);
                v.add(name.getLocalPart());
            }
        }
        
        return v;
    }

}
