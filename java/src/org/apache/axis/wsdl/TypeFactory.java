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
package org.apache.axis.wsdl;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.wsdl.QName;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
/**
 * This factory creates Type objects for the types supported by the WSDL2Java emitter.
 * The factory creates the Types by analyzing the XML Document.  The Type encapsulates
 * the QName, Java Name and defining Node for each Type.
 *
 * @author Rich Scheuerle  (scheu@us.ibm.com)
 */
public class TypeFactory {

    private HashMap types;                    // All Types
    private Namespaces mapNamespaceToPackage; // Mapping from Namespace to Java Package
    private static int nameSpaceSuffix = 1;
    private String nameSpacePrefix = null;

    /**
     * Create an Emit Type Factory
     */
    public TypeFactory(Namespaces namespaces) {
        types = new HashMap();
        mapNamespaceToPackage = namespaces;
    }

    /**
     * Invoke this method to associate a namespace URI with a autogenerated Java Package
     * name, if an entry is not already present
     *
     */
    public void map (String namespace) {
        if (mapNamespaceToPackage.get(namespace) == null) {
          mapNamespaceToPackage.put(namespace, Utils.makePackageName(namespace));
        }
    }

    /**
     * Invoke this method to associate a namespace URI with a particular Java Package
     */
    public void map (String namespace, String pkg) {
        mapNamespaceToPackage.put(namespace, pkg);
    }

    public void setNamespaceMap(HashMap map) {
        mapNamespaceToPackage.putAll(map);
    }

    /**
     * Invoke this method from the Emitter prior to emitting any code.
     * All of the supported Types are identified and placed in the types HashMap.
     */
    public void buildTypes(Document doc) {
        addTypes(doc);
    }

    /**
     * Access all of the emit types.
     */
    public HashMap getTypes() {
        return types;
    }

    /**
     * Get the Type for the given QName
     */
    public Type getType(QName qName) {
        return (Type) types.get(qName);
    }

    /**
     * Get the defined Type for the given Java Name
     * Return null if not found.
     */
    public Type getDefinedType(String javaName) {
        Iterator i = types.values().iterator();
        while (i.hasNext()) {
            Type et = (Type) i.next();
            if(et.getJavaName().equals(javaName)) {
                // There could be multiple types/elements with this
                // name.  Get one that defines the name.
                if (et instanceof BaseJavaType ||
                    et instanceof DefinedType ||
                    (et instanceof ElementType &&
                     ((ElementType) et).getDefinedDirectly())) {
                    return et;
                }
            }
        }
        return null;
    }

    /**
     * Dump Types for debugging
     */
    public void dump() {
        Iterator i = types.values().iterator();
        while (i.hasNext()) {
            Type et = (Type) i.next();
            System.out.println("");
            System.out.println(et);
        }
    }

    /**
     * If the specified node represents a supported JAX-RPC complexType/element,
     * a Vector is returned which contains the child element types and
     * child element names.  The even indices are the element types (Types) and
     * the odd indices are the corresponding names (Strings).
     * If the specified node is not a supported JAX-RPC complexType/element
     * null is returned.
     */
    public Vector getComplexElementTypesAndNames(Node node) {
        if (node == null) {
            return null;
        }

        // If the node kind is an element, dive into it.
        QName nodeKind = Utils.getNodeQName(node);
        if (nodeKind != null &&
            nodeKind.getLocalPart().equals("element") &&
            Utils.isSchemaNS(nodeKind.getNamespaceURI())) {
            NodeList children = node.getChildNodes();
            Node complexNode = null;
            for (int j = 0; j < children.getLength() && complexNode == null; j++) {
                QName complexKind = Utils.getNodeQName(children.item(j));
                if (complexKind != null &&
                    complexKind.getLocalPart().equals("complexType") &&
                    Utils.isSchemaNS(complexKind.getNamespaceURI())) {
                    complexNode = children.item(j);
                    node = complexNode;
                }
            }
        }

        // Expecting a schema complexType
        nodeKind = Utils.getNodeQName(node);
        if (nodeKind != null &&
            nodeKind.getLocalPart().equals("complexType") &&
            Utils.isSchemaNS(nodeKind.getNamespaceURI())) {

            // Under the complexType there should be a sequence or all group node.
            // (There may be other #text nodes, which we will ignore).
            NodeList children = node.getChildNodes();
            Node groupNode = null;
            for (int j = 0; j < children.getLength() && groupNode == null; j++) {
                QName groupKind = Utils.getNodeQName(children.item(j));
                if (groupKind != null &&
                    (groupKind.getLocalPart().equals("sequence") ||
                     groupKind.getLocalPart().equals("all")) &&
                    Utils.isSchemaNS(groupKind.getNamespaceURI()))
                    groupNode = children.item(j);
            }

            if (groupNode == null) {
                return new Vector();
            }
            if (groupNode != null) {

                // Process each of the element nodes under the group node
                Vector v = new Vector();
                NodeList elements = children.item(1).getChildNodes();
                for (int i=0; i < elements.getLength(); i++) {
                    QName elementKind = Utils.getNodeQName(elements.item(i));
                    if (elementKind != null &&
                        elementKind.getLocalPart().equals("element") &&
                        Utils.isSchemaNS(elementKind.getNamespaceURI())) {

                        // Get the name and type qnames.
                        // The name of the element is the local part of the name's qname.
                        // The type qname is used to locate the Type, which is then
                        // used to retrieve the proper java name of the type.
                        Node elementNode = elements.item(i);
                        QName nodeName = Utils.getNodeNameQName(elementNode);
                        QName nodeType = Utils.getNodeTypeRefQName(elementNode);
                        if (nodeType == null) { // The element may use an anonymous type
                            nodeType = nodeName;
                        }

                        Type Type = (Type) types.get(nodeType);
                        if (Type != null) {
                            v.add(Type);
                            v.add(nodeName.getLocalPart());
                        }
                    }
                }
                return v;
            }
        }
        return null;
    }

    /**
     * If the specified node represents a supported JAX-RPC enumeration,
     * a Vector is returned which contains the base type and the enumeration values.
     * The first element in the vector is the base type (an Type).
     * Subsequent elements are values (Strings).
     * If this is not an enumeration, null is returned.
     */
    public Vector getEnumerationBaseAndValues(Node node) {
        if (node == null) {
            return null;
        }

        // If the node kind is an element, dive into it.
        QName nodeKind = Utils.getNodeQName(node);
        if (nodeKind != null &&
            nodeKind.getLocalPart().equals("element") &&
            Utils.isSchemaNS(nodeKind.getNamespaceURI())) {
            NodeList children = node.getChildNodes();
            Node simpleNode = null;
            for (int j = 0; j < children.getLength() && simpleNode == null; j++) {
                QName simpleKind = Utils.getNodeQName(children.item(j));
                if (simpleKind != null &&
                    simpleKind.getLocalPart().equals("simpleType") &&
                    Utils.isSchemaNS(simpleKind.getNamespaceURI())) {
                    simpleNode = children.item(j);
                    node = simpleNode;
                }
            }
        }
        // Get the node kind, expecting a schema simpleType
        nodeKind = Utils.getNodeQName(node);
        if (nodeKind != null &&
            nodeKind.getLocalPart().equals("simpleType") &&
            Utils.isSchemaNS(nodeKind.getNamespaceURI())) {

            // Under the simpleType there should be a restriction.
            // (There may be other #text nodes, which we will ignore).
            NodeList children = node.getChildNodes();
            Node restrictionNode = null;
            for (int j = 0; j < children.getLength() && restrictionNode == null; j++) {
                QName restrictionKind = Utils.getNodeQName(children.item(j));
                if (restrictionKind != null &&
                    restrictionKind.getLocalPart().equals("restriction") &&
                    Utils.isSchemaNS(restrictionKind.getNamespaceURI()))
                    restrictionNode = children.item(j);
            }

            // The restriction node indicates the type being restricted
            // (the base attribute contains this type).
            // The base type must be a built-in type...and we only think
            // this makes sense for string.
            Type baseEType = null;
            if (restrictionNode != null) {
                QName baseType = Utils.getNodeTypeRefQName(restrictionNode, "base");
                baseEType = getType(baseType);
                if (baseEType != null && 
                    !baseEType.getJavaName().equals("java.lang.String")) {
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
                        Utils.isSchemaNS(enumKind.getNamespaceURI())) {

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
     * If the specified node represents an array encoding of one of the following
     * forms, then return the qname repesenting the element type of the array.
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
    public QName getArrayElementQName(Node node) {
        if (node == null) {
            return null;
        }

        // If the node kind is an element, dive into it.
        QName nodeKind = Utils.getNodeQName(node);
        if (nodeKind != null &&
            nodeKind.getLocalPart().equals("element") &&
            Utils.isSchemaNS(nodeKind.getNamespaceURI())) {
            NodeList children = node.getChildNodes();
            Node complexNode = null;
            for (int j = 0; j < children.getLength() && complexNode == null; j++) {
                QName complexKind = Utils.getNodeQName(children.item(j));
                if (complexKind != null &&
                    complexKind.getLocalPart().equals("complexType") &&
                    Utils.isSchemaNS(complexKind.getNamespaceURI())) {
                    complexNode = children.item(j);
                    node = complexNode;
                }
            }
        }
        // Get the node kind, expecting a schema complexType
        nodeKind = Utils.getNodeQName(node);
        if (nodeKind != null &&
            nodeKind.getLocalPart().equals("complexType") &&
            Utils.isSchemaNS(nodeKind.getNamespaceURI())) {

            // Under the complexType there should be a complexContent.
            // (There may be other #text nodes, which we will ignore).
            NodeList children = node.getChildNodes();
            Node complexContentNode = null;
            for (int j = 0; j < children.getLength() && complexContentNode == null; j++) {
                QName complexContentKind = Utils.getNodeQName(children.item(j));
                if (complexContentKind != null &&
                    complexContentKind.getLocalPart().equals("complexContent") &&
                    Utils.isSchemaNS(complexContentKind.getNamespaceURI()))
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
                        Utils.isSchemaNS(restrictionKind.getNamespaceURI()))
                        restrictionNode = children.item(j);
                }
            }

            // The restriction node must have a base of soapenc:Array.  
            QName baseType = null;
            if (restrictionNode != null) {
                baseType = Utils.getNodeTypeRefQName(restrictionNode, "base");
                if (baseType != null &&
                    baseType.getLocalPart().equals("Array") &&
                    Utils.isSoapEncodingNS(baseType.getNamespaceURI()))
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
                        Utils.isSchemaNS(kind.getNamespaceURI())) {
                        groupNode = children.item(j);
                    }
                    if (kind != null &&
                        kind.getLocalPart().equals("attribute") &&
                        Utils.isSchemaNS(kind.getNamespaceURI())) {
                        attributeNode = children.item(j);
                    }
                }
            }

            // If there is an attribute node, it must have a ref of soapenc:array and
            // a wsdl:arrayType attribute.
            if (attributeNode != null) {
                QName refQName = Utils.getNodeTypeRefQName(attributeNode, "ref");
                if (refQName != null &&
                    refQName.getLocalPart().equals("arrayType") &&
                    Utils.isSoapEncodingNS(refQName.getNamespaceURI()))
                    ; // Okay
                else
                    refQName = null;  // Did not find ref="soapenc:arrayType"

                String wsdlArrayTypeValue = null;
                if (refQName != null) {
                    Vector attrs = Utils.getAttributesWithLocalName(attributeNode, "arrayType");
                    for (int i=0; i < attrs.size() && wsdlArrayTypeValue == null; i++) {
                        Node attrNode = (Node) attrs.elementAt(i);
                        String attrName = attrNode.getNodeName();
                        QName attrQName = Utils.getQNameFromPrefixedName(attributeNode, attrName);
                        if (Utils.isWsdlNS(attrQName.getNamespaceURI())) {
                            wsdlArrayTypeValue = attrNode.getNodeValue();
                        }
                    }
                }
                
                // The value should have [] on the end, strip these off.
                // The convert the prefixed name into a qname, and return
                if (wsdlArrayTypeValue != null) {
                    int i = wsdlArrayTypeValue.indexOf("[");
                    if (i > 0) {
                        String prefixedName = wsdlArrayTypeValue.substring(0,i);
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
                        Utils.isSchemaNS(elementKind.getNamespaceURI())) {
                        elementNode = elements.item(i);
                    }
                }
                 
                // The element node should have maxOccurs="unbounded" and
                // a type
                if (elementNode != null) {
                    String maxOccursValue = Utils.getAttribute(elementNode, "maxOccurs");
                    if (maxOccursValue != null &&
                        maxOccursValue.equalsIgnoreCase("unbounded")) {
                        return Utils.getNodeTypeRefQName(elementNode);
                    }
                }
            }
            
        }
        return null;
    }


    /**
     * Utility method which walks the Document and creates Type objects for
     * each complexType, simpleType, or element referenced or defined.
     */
    private void addTypes(Node node) {
        if (node == null) {
            return;
        }
        // Get the kind of node (complexType, wsdl:part, etc.)
        QName nodeKind = Utils.getNodeQName(node);

        if (nodeKind != null) {
            if (nodeKind.getLocalPart().equals("complexType") &&
                Utils.isSchemaNS(nodeKind.getNamespaceURI())) {

                // This is a definition of a complex type.
                // Create a Type.
                createTypeFromDef(node, false);
            }
            if (nodeKind.getLocalPart().equals("simpleType") &&
                Utils.isSchemaNS(nodeKind.getNamespaceURI())) {

                // This is a definition of a simple type, which could be an enum
                // Create a Type.
                createTypeFromDef(node, false);
            }
            else if (nodeKind.getLocalPart().equals("element") &&
                   Utils.isSchemaNS(nodeKind.getNamespaceURI())) {
                // If the element has a type/ref attribute, create
                // a Type representing the referenced type.
                if (Utils.getAttribute(node, "type") != null ||
                    Utils.getAttribute(node, "ref") != null) {
                    createTypeFromRef(node);
                }

                // Create a type representing an element.  (This may
                // seem like overkill, but is necessary to support ref=
                // and element=.
                createTypeFromDef(node, true);
            }
            else if (nodeKind.getLocalPart().equals("part") &&
                     Utils.isWsdlNS(nodeKind.getNamespaceURI())) {

                // This is a wsdl part.  Create an Type representing the reference
                createTypeFromRef(node);
            }
        }
        // Recurse through children nodes
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            addTypes(children.item(i));
        }
    }

    /**
     * Create a Type from the indicated node, which defines a type
     * that represents a complexType, simpleType or element (for ref=).
     */
    private void createTypeFromDef(Node node, boolean isElement) {
        // If this is not an element, make sure it is not an anonymous type.
        // If it is, the the existing ElementType will be used.  If
        // not, create a new type.
        if (!isElement &&
            Utils.getAttribute(node, "name") == null) {
            return;
        }

        // Get the QName of the node's name attribute value
        QName qName = Utils.getNodeNameQName(node);
        if (qName != null) {
            map(qName.getNamespaceURI());

            // If the node has a type or ref attribute, get the 
            // ultimate ref'd type
            QName refQName = Utils.getNodeTypeRefQName(node);
            if (refQName != null) {
                Type refType = null;
                while (refQName != null) {
                    refType = getType(refQName);
                    refQName = null;
                    if (refType != null &&
                        refType.getNode() != null) {
                        refQName = Utils.getNodeTypeRefQName(refType.getNode());
                    }                         
                }
                // Create a type from the referenced type
                types.put(qName, new ElementType(qName, refType, node));

            }   
            else {

                // See if this is an array definition.
                QName arrayQName = getArrayElementQName(node);
                if (arrayQName != null) {
                    String javaName = getJavaName(arrayQName)+"[]";
                    Type arrayType = null;
                    if (isElement) {
                        arrayType = new ElementType(qName, javaName, node);
                    } else {
                        arrayType = new DefinedType(qName, javaName, node);
                    }
                    types.put(qName,arrayType);
                    arrayType.setShouldEmit(false);
                }
                else {
                    // Create a Type representing a base type or non-base type
                    String baseJavaName = Utils.getBaseJavaName(qName);
                    if (baseJavaName != null)
                        types.put(qName, new BaseJavaType(qName));
                    else if (isElement)
                        types.put(qName, new ElementType(qName, getJavaName(qName), node));
                    else
                        types.put(qName, new DefinedType(qName, getJavaName(qName), node));
                }
            }
        }
    } // createTypeFromDef
    
    /**
     * Node may contain a reference (via type=, ref=, or element= attributes) to 
     * another type.  Create a Type object representing this referenced type.
     */
    private void createTypeFromRef(Node node) {
        // Get the QName of the node's type attribute value
        QName qName = Utils.getNodeTypeRefQName(node);
        if (qName != null) {
            String javaName = getJavaName(qName);

            Type type = (Type) types.get(qName);
            if (type == null) {
                // Type not defined, add a base java type or a refdType
                String baseJavaName = Utils.getBaseJavaName(qName);
                if (baseJavaName != null)
                    types.put(qName, new BaseJavaType(qName));
                else
                    types.put(qName, new RefdType(qName, javaName));
            } else {
                // Type exists, update shouldEmit flag if necessary
                if (type instanceof ElementType &&
                    type.isDefined() &&
                    type.getJavaName().indexOf("[") < 0 &&
                    ((ElementType) type).getDefinedDirectly()) {
                    type.setShouldEmit(true);
                }
            }
                
        }
    }


    /**
     * Get the Package name for the specified namespace
     */
    private String getPackage(String namespace) {
        return (String) mapNamespaceToPackage.get(namespace);
    }

    /**
     * Get the Package name for the specified QName
     */
    private String getPackage(QName qName) {
        return getPackage(qName.getNamespaceURI());
    }

    /**
     * Convert the specified QName into a full Java Name.
     */
    public String getJavaName(QName qName) {
        // The QName may represent a base java name, so check this first
        String fullJavaName = Utils.getBaseJavaName(qName);
        if (fullJavaName != null) 
            return fullJavaName;
        
        // Use the namespace uri to get the appropriate package
        String pkg = getPackage(qName.getNamespaceURI());
        if (pkg != null) {
            fullJavaName = pkg + "." + Utils.capitalizeFirstChar(qName.getLocalPart());
        } else {
            fullJavaName = Utils.capitalizeFirstChar(qName.getLocalPart());
        }
        return fullJavaName;
    }
}








