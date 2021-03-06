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
 *    Apache Software Foundation (http://www.apache.org/)."
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

/*
 * XmlSchemaSimpleContentRestriction.java
 *
 * Created on September 27, 2001, 3:18 AM
 */

package org.apache.axis.xsd.xml.schema;

import org.apache.axis.xsd.xml.QualifiedName;

/**
 * Class for simple types that are derived by restriction. Restricts the
 * range of values for the element to a subset of the inherited simple types. 
 * Represents the World Wide Web Consortium (W3C) restriction element for simple content.  
 *
 * @author  mukund
 */

// Vidyanand - 16th Oct - initial implementation

public class XmlSchemaSimpleContentRestriction extends XmlSchemaContent {

    /** Creates new XmlSchemaSimpleContentRestriction */
    public XmlSchemaSimpleContentRestriction() {
        facets = new XmlSchemaObjectCollection();
        attributes = new XmlSchemaObjectCollection();
    }

    /* Allows an XmlSchemaAnyAttribute to be used for the attribute value.*/
    XmlSchemaAnyAttribute anyAttribute;

    public void setAnyAttribute(XmlSchemaAnyAttribute anyAttribute) {
        this.anyAttribute = anyAttribute;
    }

    public XmlSchemaAnyAttribute getAnyAttribute() {
        return this.anyAttribute;
    }

    /* Contains XmlSchemaAttribute and XmlSchemaAttributeGroupRef. Collection of attributes for the simple type.*/
    XmlSchemaObjectCollection attributes;

    public XmlSchemaObjectCollection getAttributes() {
        return this.attributes;
    }

    /*Derived from the type specified by the base value.*/
    XmlSchemaSimpleType baseType;

    public void setBaseType(XmlSchemaSimpleType baseType) {
        this.baseType = baseType;
    }

    public XmlSchemaSimpleType getBaseType() {
        return this.baseType;
    }

    /* Name of the built-in data type, simple type, or complex type.*/
    QualifiedName baseTypeName;

    public void setBaseTypeName(QualifiedName baseTypeName) {
        this.baseTypeName = baseTypeName;
    }

    public QualifiedName getBaseTypeName() {
        return this.baseTypeName;
    }

    /* One or more of the facet classes: */
    XmlSchemaObjectCollection facets;

    public XmlSchemaObjectCollection getFacets() {
        return this.facets;
    }

}
			
