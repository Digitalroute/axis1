/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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

/**
 * This Class represents a method (operation is wsdl) in a web service 
 * class.
 * @author Srinath Perera(hemapani@opensource.lk)
 */
package org.apache.axismora.wsdl2ws.info;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MethodInfo {
    private String methodname;
    private ArrayList parameters;
    private ParameterInfo returnType;
    private String soapAction;
    private List inputEncoding;
    private List outputEncoding;
    private String inputUse;
    private String outputUse;

    public MethodInfo() {
        this.returnType = null;
        this.methodname = null;
        parameters = new ArrayList(101);
    }

    public MethodInfo(ParameterInfo returnType, String methodname) {
        this.returnType = returnType;
        this.methodname = methodname;
        parameters = new ArrayList(101);
    }

    public String getMethodname() {
        return methodname;
    }

    public void setMethodname(String methodname) {
        this.methodname = methodname;
    }

    public ParameterInfo getReturnType() {
        return returnType;
    }

    public void setReturnType(ParameterInfo returnType) {
        this.returnType = returnType;
    }
/*
    public Enumeration getParameters() {
        return this.parameters.keys();
    }
*/
    public Collection getParameterTypes() {
        return this.parameters;
    }

    public void addParameter(ParameterInfo type) {
        this.parameters.add(type);
    }
/*
    public ParameterInfo getParameter(String paramName) {
        Object obj = this.parameters.get(paramName);
        if (obj != null)
            return (ParameterInfo) obj;
        return null;
    }
*/
    /**
     * @return
     */
    public List getInputEncoding() {
        return inputEncoding;
    }

    /**
     * @return
     */
    public List getOutputEncoding() {
        return outputEncoding;
    }

    /**
     * @return
     */
    public String getSoapAction() {
        return soapAction;
    }

    /**
     * @param string
     */
    public void setInputEncoding(List string) {
        inputEncoding = string;
    }

    /**
     * @param string
     */
    public void setOutputEncoding(List string) {
        outputEncoding = string;
    }

    /**
     * @param string
     */
    public void setSoapAction(String string) {
        soapAction = string;
    }

    /**
     * @return
     */
    public String getInputUse() {
        return inputUse;
    }

    /**
     * @return
     */
    public String getOutputUse() {
        return outputUse;
    }

    /**
     * @param string
     */
    public void setInputUse(String string) {
        inputUse = string;
    }

    /**
     * @param string
     */
    public void setOutputUse(String string) {
        outputUse = string;
    }

}
