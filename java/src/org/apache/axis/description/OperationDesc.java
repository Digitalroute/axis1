/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
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
package org.apache.axis.description;

import javax.xml.rpc.namespace.QName;
import java.util.ArrayList;

/**
 * An OperationDesc is an abstract description of an operation on a service.
 *
 * !!! WORK IN PROGRESS
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class OperationDesc {
    /** The service we're a part of */
    private ServiceDesc parent;

    /** Parameter list */
    private ArrayList parameters = new ArrayList();

    /** The operation name (String, or QName?) */
    private String name;

    /** An XML QName which should dispatch to this method */
    private QName elementQName;

    /** The return QName (if it should be different from <method>Result) */
    private QName returnQName;

    /** The return type */
    private QName returnType;

    /** The Java method name this maps to (is this just name?) */
    private String methodName;

    /** This operation's style.  If null, we default to our parent's */
    private Integer style;

    /**
     * Default constructor.
     */
    public OperationDesc() {
    }

    /**
     * Return the operation's name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the operation's name
     */
    public void setName(String name) {
        this.name = name;
    }

    public QName getReturnQName() {
        return returnQName;
    }

    public void setReturnQName(QName returnQName) {
        this.returnQName = returnQName;
    }

    public QName getReturnType() {
        return returnType;
    }

    public void setReturnType(QName returnType) {
        this.returnType = returnType;
    }

    public QName getElementQName() {
        return elementQName;
    }

    public void setElementQName(QName elementQName) {
        this.elementQName = elementQName;
    }

    public ServiceDesc getParent() {
        return parent;
    }

    public void setParent(ServiceDesc parent) {
        this.parent = parent;
    }

    public void setStyle(int style)
    {
        this.style = new Integer(style);
    }

    /**
     * Return the style of the operation, defaulting to the parent
     * ServiceDesc's style if we don't have one explicitly set.
     */
    public int getStyle()
    {
        if (style == null) {
            if (parent != null) {
                return parent.getStyle();
            }
            return ServiceDesc.STYLE_RPC; // Default
        }

        return style.intValue();
    }

    public void addParameter(Parameter param)
    {
        parameters.add(param);
    }

    public Parameter getParameter(int i)
    {
        return (Parameter)parameters.get(i);
    }
}

