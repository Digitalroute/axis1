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

import java.io.IOException;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.wsdl.Binding;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Fault;
import javax.wsdl.Input;
import javax.wsdl.Operation;
import javax.wsdl.OperationType;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.PortType;
import javax.wsdl.QName;

import org.w3c.dom.Node;

import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPOperation;

import org.apache.axis.utils.JavaUtils;

/**
* This is Wsdl2java's stub writer.  It writes the <BindingName>Stub.java
* file which contains the <bindingName>Stub class.
*/
public class JavaStubWriter extends JavaWriter {
    private BindingEntry bEntry;
    private Binding binding;
    private SymbolTable symbolTable;

    /**
     * Constructor.
     */
    protected JavaStubWriter(
            Emitter emitter,
            BindingEntry bEntry,
            SymbolTable symbolTable) {
        super(emitter, bEntry, "Stub", "java",
                JavaUtils.getMessage("genStub00"), "stub");
        this.bEntry = bEntry;
        this.binding = bEntry.getBinding();
        this.symbolTable = symbolTable;
    } // ctor

    /**
     * Write the body of the binding's stub file.
     */
    protected void writeFileBody() throws IOException {
        PortType portType = binding.getPortType();
        PortTypeEntry ptEntry =
                symbolTable.getPortTypeEntry(portType.getQName());
        String name = Utils.xmlNameToJavaClass(qname.getLocalPart());
        String portTypeName = ptEntry.getName();
        boolean isRPC = true;
        if (bEntry.getBindingStyle() == BindingEntry.STYLE_DOCUMENT) {
            isRPC = false;
        }

        pw.println("public class " + className + " extends javax.xml.rpc.Stub implements " + portTypeName + " {");

        pw.println("    private javax.xml.rpc.Service service = null;");
        pw.println();
        pw.println("    // If maintainSessionSet is true, then setMaintainSession");
        pw.println("    // was called and it set the value of maintainSession.");
        pw.println("    // Use that value when getting the new Call object.");
        pw.println("    // If maintainSession HAS NOT been set, then the");
        pw.println("    // Call object uses the default maintainSession");
        pw.println("    // from the Service.");
        pw.println("    private boolean maintainSessionSet = false;");
        pw.println("    private boolean maintainSession = false;");
        pw.println();
        pw.println("    private java.net.URL cachedEndpoint = null;");
        pw.println("    private java.util.Properties cachedProperties = new java.util.Properties();");

        HashSet types = getTypesInPortType(portType);
        if (types.size() > 0) {
            pw.println("    private java.util.Vector cachedSerClasses = new java.util.Vector();");
            pw.println("    private java.util.Vector cachedSerQNames = new java.util.Vector();");
            pw.println("    private java.util.Vector cachedSerializers = new java.util.Vector();");
            pw.println("    private java.util.Vector cachedDeserFactories = new java.util.Vector();");
        }
        pw.println();

        pw.println("    public " + className + "() throws org.apache.axis.AxisFault {");
        pw.println("         this(null);");
        pw.println("    }");
        pw.println();

        pw.println("    public " + className + "(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {");
        pw.println("         this(service);");
        pw.println("         cachedEndpoint = endpointURL;");
        pw.println("    }");
        pw.println();

        pw.println("    public " + className + "(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {");
        pw.println("        try {" );
        pw.println("            if (service == null) {");
        pw.println("                this.service = new org.apache.axis.client.Service();");
        pw.println("            } else {");
        pw.println("                this.service = service;");
        pw.println("            }");

        Iterator it = types.iterator();
        while (it.hasNext()) {
            writeSerializationInit((TypeEntry) it.next());
        }

        pw.println("        }");
        pw.println("        catch(Exception t) {");
        pw.println("            throw org.apache.axis.AxisFault.makeFault(t);");
        pw.println("        }");

        pw.println("    }");
        pw.println();
        pw.println("    /**");
        pw.println("     * Sets the value for a named property. JAX-RPC 1.0 specification"); 
        pw.println("     * specifies a standard set of properties that may be passed ");
        pw.println("     * <UL>");
        pw.println("     * <LI>http.auth.username: Username for the HTTP Basic Authentication");
        pw.println("     * <LI>http.auth.password: Password for the HTTP Basic Authentication");
        pw.println("     * <LI>security.auth.subject: JAAS Subject that carries client principal and its credentials");
        pw.println("     * <LI>encodingstyle.namespace.uri: Encoding style specified as a namespace URI");
        //pw.println("     * <LI>[TBD: Additional properties]");
        pw.println("     * </UL>");
        pw.println("     *");
        pw.println("     * @param name - Name of the property");
        pw.println("     * @param value - Value of the property");
        pw.println("     */");
        pw.println("    public void _setProperty(String name, Object value) {");
        pw.println("        cachedProperties.put(name, value);");
        pw.println("    }");
        pw.println();
        pw.println("    // " +
                JavaUtils.getMessage("from00", "javax.xml.rpc.Stub"));
        pw.println("    public Object _getProperty(String name) {");
        pw.println("        return cachedProperties.get(name);");
        pw.println("    }");
        pw.println();
        pw.println("    // " +
                JavaUtils.getMessage("from00", "javax.xml.rpc.Stub"));
        pw.println("    public void _setTargetEndpoint(java.net.URL address) {");
        pw.println("        cachedEndpoint = address;");
        pw.println("    }");
        pw.println();
        pw.println("    // " +
                JavaUtils.getMessage("from00", "javax.xml.rpc.Stub"));
        pw.println("    public java.net.URL _getTargetEndpoint() {");
        pw.println("        return cachedEndpoint;");
        pw.println("    }");
        pw.println();
        pw.println("    public void setMaintainSession(boolean session) {");
        pw.println("        maintainSessionSet = true;");
        pw.println("        maintainSession = session;");
        pw.println("    }");
        pw.println();
        pw.println("    // From javax.naming.Referenceable");
        pw.println("    public javax.naming.Reference getReference() {");
        pw.println("        return null; // ???");
        pw.println("    }");
        pw.println();
        pw.println("    private javax.xml.rpc.Call getCall() throws java.rmi.RemoteException {");
        pw.println("        try {");
        pw.println("            org.apache.axis.client.Call call =");
        pw.println("                    (org.apache.axis.client.Call) this.service.createCall();");
        pw.println("            if (maintainSessionSet) {");
        pw.println("                call.setMaintainSession(maintainSession);");
        pw.println("            }");
        pw.println("            if (cachedEndpoint != null) {");
        pw.println("                call.setTargetEndpointAddress(cachedEndpoint);");
        pw.println("                call.setProperty(org.apache.axis.transport.http.HTTPTransport.URL, cachedEndpoint.toString());");
        pw.println("            }");
        pw.println("            java.util.Enumeration keys = cachedProperties.keys();");
        pw.println("            while (keys.hasMoreElements()) {");
        pw.println("                String key = (String) keys.nextElement();");
        pw.println("                call.setProperty(key, cachedProperties.get(key));");
        pw.println("            }");
        if (types.size() > 0) {
            pw.println("            for (int i = 0; i < cachedSerializers.size(); ++i) {");
            pw.println("                Class cls = (Class) cachedSerClasses.get(i);");
            pw.println("                javax.xml.rpc.namespace.QName qname =");
            pw.println("                        (javax.xml.rpc.namespace.QName) cachedSerQNames.get(i);");
            pw.println("                org.apache.axis.encoding.Serializer ser =");
            pw.println("                        (org.apache.axis.encoding.Serializer) cachedSerializers.get(i);");
            pw.println("                org.apache.axis.encoding.DeserializerFactory deserFac =");
            pw.println("                        (org.apache.axis.encoding.DeserializerFactory)");
            pw.println("                         cachedDeserFactories.get(i);");
            pw.println("                call.addSerializer(cls, qname, ser);");
            pw.println("                call.addDeserializerFactory(qname, cls, deserFac);");
            pw.println("            }");
        }
        pw.println("            return call;");
        pw.println("        }");
        pw.println("        catch (Throwable t) {");
        pw.println("            throw new org.apache.axis.AxisFault(\""
                + JavaUtils.getMessage("badCall01") + "\", t);");
        pw.println("        }");
        pw.println("    }");
        pw.println();

        List operations = binding.getBindingOperations();
        for (int i = 0; i < operations.size(); ++i) {
            BindingOperation operation = (BindingOperation) operations.get(i);
            Parameters parameters =
                    ptEntry.getParameters(operation.getOperation().getName());

            // Get the soapAction from the <soap:operation>
            String soapAction = "";
            Iterator operationExtensibilityIterator = operation.getExtensibilityElements().iterator();
            for (; operationExtensibilityIterator.hasNext();) {
                Object obj = operationExtensibilityIterator.next();
                if (obj instanceof SOAPOperation) {
                    soapAction = ((SOAPOperation) obj).getSoapActionURI();
                    break;
                }
            }
            // Get the namespace for the operation from the <soap:body>
            // RJB: is this the right thing to do?
            String namespace = "";
            Iterator bindingMsgIterator = null;
            BindingInput input = operation.getBindingInput();
            BindingOutput output;
            if (input != null) {
                bindingMsgIterator =
                        input.getExtensibilityElements().iterator();
            }
            else {
                output = operation.getBindingOutput();
                if (output != null) {
                    bindingMsgIterator =
                            output.getExtensibilityElements().iterator();
                }
            }
            if (bindingMsgIterator != null) {
                for (; bindingMsgIterator.hasNext();) {
                    Object obj = bindingMsgIterator.next();
                    if (obj instanceof SOAPBody) {
                        namespace = ((SOAPBody) obj).getNamespaceURI();
                        if (namespace == null) {
                            namespace = emitter.def.getTargetNamespace();
                        }
                        if (namespace == null)
                            namespace = "";
                        break;
                    }
                }
            }
            Operation ptOperation = operation.getOperation();
            OperationType type = ptOperation.getStyle();

            // These operation types are not supported.  The signature
            // will be a string stating that fact.
            if (type == OperationType.NOTIFICATION
                    || type == OperationType.SOLICIT_RESPONSE) {
                pw.println(parameters.signature);
                pw.println();
            }
            else {
                writeOperation(
                        operation, parameters, soapAction, namespace, isRPC);
            }
        }
        pw.println("}");
        pw.close();
    } // writeFileBody

    /**
     * This method returns a set of all the TypeEntry in a given PortType.
     * The elements of the returned HashSet are Types.
     */
    private HashSet getTypesInPortType(PortType portType) {
        HashSet types = new HashSet();
        HashSet firstPassTypes = new HashSet();

        PortTypeEntry pe = symbolTable.getPortTypeEntry(portType.getQName());

        // Get all the types from all the operations
        List operations = portType.getOperations();

        for (int i = 0; i < operations.size(); ++i) {
            Operation op = (Operation) operations.get(i);
            firstPassTypes.addAll(getTypesInOperation(op, pe));
        }

        // Extract those types which are complex types.
        Iterator i = firstPassTypes.iterator();
        while (i.hasNext()) {
            TypeEntry type = (TypeEntry) i.next();
            if (!types.contains(type)) {
                types.add(type);
                if ((type.getNode() != null) && type.getBaseType() == null) {
                    types.addAll(
                            Utils.getNestedTypes(type.getNode(), symbolTable));
                }
            }
        }
        return types;
    } // getTypesInPortType

    /**
     * This method returns a set of all the TypeEntry in a given Operation.
     * The elements of the returned HashSet are TypeEntry.
     */
    private HashSet getTypesInOperation(Operation operation, PortTypeEntry portEntry) {
        HashSet types = new HashSet();
        Vector v = new Vector();

        Parameters params = portEntry.getParameters(operation.getName());
        
        // Loop over parameter types for this operation
        for (int i=0; i < params.list.size(); i++) {
            Parameter p = (Parameter) params.list.get(i);
            v.add(p.type);
        }
        
        // Add the return type
        if (params.returnType != null)
            v.add(params.returnType);
        
        // Collect all the types in faults
        Map faults = operation.getFaults();

        if (faults != null) {
            Iterator i = faults.values().iterator();

            while (i.hasNext()) {
                Fault f = (Fault) i.next();
                partTypes(v,
                        f.getMessage().getOrderedParts(null),
                        (bEntry.getFaultBodyType(operation, f.getName()) == BindingEntry.USE_LITERAL));
            }
        }
        // Put all these types into a set.  This operation eliminates all duplicates.
        for (int i = 0; i < v.size(); i++)
            types.add(v.get(i));

        return types;
    } // getTypesInOperation

    /**
     * This method returns a vector of TypeEntry for the parts.
     */
    private void partTypes(Vector v, Collection parts, boolean literal) {
        Iterator i = parts.iterator();

        while (i.hasNext()) {
            Part part = (Part) i.next();
            
            QName qType = part.getTypeName(); 
            if (qType != null) {
                v.add(symbolTable.getType(qType));
            } else {
                qType = part.getElementName();
                if (qType != null) {
                    v.add(symbolTable.getElement(qType));
                }
            }
        } // while
        
    } // partTypes

    /**
     * In the stub constructor, write the serializer code for the complex types.
     */
    private boolean firstSer = true ;

    private void writeSerializationInit(TypeEntry type) throws IOException {
        if (type.getBaseType() != null || type.getName().endsWith("[]")) {
            return;
        }
        if ( firstSer ) {
            pw.println("            Class cls;" );
            pw.println("            org.apache.axis.encoding.Serializer ser;");
            pw.println("            org.apache.axis.encoding.DeserializerFactory deserFac;");
        }
        firstSer = false ;

        QName qname = type.getQName();
        pw.println("            cachedSerQNames.add(new javax.xml.rpc.namespace.QName(\""
                + qname.getNamespaceURI() + "\", \"" + qname.getLocalPart()
                + "\"));");
        pw.println("            cls = " + type.getName() + ".class;");
        pw.println("            cachedSerClasses.add(cls);");
        pw.println("            cachedSerializers.add(new org.apache.axis.encoding.BeanSerializer(cls));");
        pw.println("            cachedDeserFactories.add(org.apache.axis.encoding.BeanSerializer.getFactory());");
        pw.println();
    } // writeSerializationInit

    /**
     * Write the stub code for the given operation.
     */
    private void writeOperation(
            BindingOperation operation,
            Parameters parms,
            String soapAction,
            String namespace,
            boolean isRPC) throws IOException {

        writeComment(pw, operation.getDocumentationElement());

        pw.println(parms.signature + "{");
        pw.println("        if (cachedEndpoint == null) {");
        pw.println("            throw new org.apache.axis.NoEndPointException();");
        pw.println("        }");
        pw.println("        javax.xml.rpc.Call call = getCall();");
        pw.println("        try {");

        // DUG: need to set the isRPC flag in the Call object

        // loop over paramters and set up in/out params
        for (int i = 0; i < parms.list.size(); ++i) {
            Parameter p = (Parameter) parms.list.get(i);

            QName qn = p.type.getQName();
            String typeString = "new javax.xml.rpc.namespace.QName(\"" +
                    qn.getNamespaceURI() + "\", \"" +
                    qn.getLocalPart() + "\")";
            if (p.mode == Parameter.IN) {
                pw.println("            call.addParameter(\"" + p.name + "\", " + typeString + ", javax.xml.rpc.ParameterMode.PARAM_MODE_IN);");
            }
            else if (p.mode == Parameter.INOUT) {
                pw.println("            call.addParameter(\"" + p.name + "\", " + typeString + ", javax.xml.rpc.ParameterMode.PARAM_MODE_INOUT);");
            }
            else { // p.mode == Parameter.OUT
                pw.println("            call.addParameter(\"" + p.name + "\", " + typeString + ", javax.xml.rpc.ParameterMode.PARAM_MODE_OUT);");
            }
        }
        // set output type
        if (parms.returnType != null) {
            QName qn = parms.returnType.getQName();
            String outputType = "new javax.xml.rpc.namespace.QName(\"" +
                qn.getNamespaceURI() + "\", \"" +
                qn.getLocalPart() + "\")";
            pw.println("            call.setReturnType(" + outputType + ");");
        }
        else {
            pw.println("            call.setReturnType(null);");
        }
        pw.println("        }");
        pw.println("        catch (javax.xml.rpc.JAXRPCException jre) {");
        pw.println("            throw new java.rmi.RemoteException(\"" +
                JavaUtils.getMessage("badCall00") + "\");");
        pw.println("        }");

        // SoapAction
        if (soapAction != null) {
            pw.println("        call.setProperty(\"soap.http.soapaction.use\", Boolean.TRUE);");
            pw.println("        call.setProperty(\"soap.http.soapaction.uri\", \"" + soapAction + "\");");
        }

        // Encoding literal or encoded use.
        int use = bEntry.getInputBodyType(operation.getOperation());
        if (use == BindingEntry.USE_LITERAL) {
            // Turn off encoding
            pw.println("        ((org.apache.axis.client.Call)call).setEncodingStyle(null);");
            // turn off multirefs
            pw.println("        call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);");
            // turn off XSI types
            pw.println("        call.setProperty(org.apache.axis.AxisEngine.PROP_SEND_XSI, Boolean.FALSE);");
        }
        
        // Operation name
        pw.println("        call.setOperationName(new javax.xml.rpc.namespace.QName(\"" + namespace + "\", \"" + operation.getName() + "\"));" );
        
        // Invoke the operation
        pw.println();
        pw.print("        Object resp = call.invoke(");
        pw.print("new Object[] {");

        // Write the input and inout parameter list
        boolean needComma = false;
        for (int i = 0; i < parms.list.size(); ++i) {
            Parameter p = (Parameter) parms.list.get(i);

            String javifiedName = Utils.xmlNameToJava(p.name);
            if (p.mode != Parameter.OUT) {
                if (needComma) {
                    pw.print(", ");
                }
                else {
                    needComma = true;
                }
                if (p.mode == Parameter.IN) {
                    pw.print(wrapPrimitiveType(p.type, javifiedName));
                }
                else { // p.mode == Parameter.INOUT
                    pw.print(wrapPrimitiveType(p.type, javifiedName + ".value"));
                }
            }
        }
        pw.println("});");
        pw.println();
        pw.println("        if (resp instanceof java.rmi.RemoteException) {");
        pw.println("            throw (java.rmi.RemoteException)resp;");
        pw.println("        }");

        int allOuts = parms.outputs + parms.inouts;
        if (allOuts > 0) {
            pw.println("        else {");
            if (allOuts == 1) {
                if (parms.inouts == 1) {
                    // There is only one output and it is an inout, so the resp object
                    // must go into the inout holder.
                    int i = 0;
                    Parameter p = (Parameter) parms.list.get(i);

                    while (p.mode != Parameter.INOUT) {
                        p = (Parameter) parms.list.get(++i);
                    }
                    String javifiedName = Utils.xmlNameToJava(p.name);
                    pw.println("            java.util.Map output;");
                    pw.println("            try {");
                    pw.println("                output = call.getOutputParams();");
                    pw.println("            }");
                    pw.println("            catch (javax.xml.rpc.JAXRPCException jre) {");
                    pw.println("            throw new java.rmi.RemoteException(\"" +
                            JavaUtils.getMessage("badCall02") + "\");");
                    pw.println("            }");
                    // If expecting an array, need to call convert(..) because
                    // the runtime stores arrays in a different form (ArrayList). 
                    // NOTE A:
                    // It seems that it should be the responsibility of the 
                    // Call to convert the ArrayList into the proper array.
                    if (p.type.getName().endsWith("[]")) {
                        pw.println("            // REVISIT THIS!");
                        pw.println("            " + javifiedName
                                    + ".value = (" + p.type.getName()
                                    + ") org.apache.axis.utils.JavaUtils.convert(output.get(\""
                                    + p.name + "\"), " + p.type.getName()
                                    + ".class);");
                    }
                    else {
                        pw.println("            " + javifiedName + ".value = "
                                + getResponseString(p.type,
                                "output.get(\"" + p.name + "\")"));
                    }
                }
                else {
                    // (parms.outputs == 1)
                    // There is only one output and it is the return value.
                    
                    // If expecting an array, need to call convert(..) because
                    // the runtime stores arrays in a different form (ArrayList). 
                    // (See NOTE A)
                    if (parms.returnType != null &&
                        parms.returnType.getName() != null &&
                        parms.returnType.getName().indexOf("[]") > 0) {
                        pw.println("             // REVISIT THIS!");
                        pw.println("             return ("+parms.returnType.getName() + ")" 
                                   +"org.apache.axis.utils.JavaUtils.convert(resp,"
                                   + parms.returnType.getName()+".class);");
                    } else {
                        pw.println("             return " + getResponseString(parms.returnType, "resp"));
                    }
                }
            }
            else {
                // There is more than 1 output.  resp is the first one.  The rest are from
                // call.getOutputParams ().  Pull the Objects from the appropriate place -
                // resp or call.getOutputParms - and put them in the appropriate place,
                // either in a holder or as the return value.
                pw.println("            java.util.Map output;");
                pw.println("            try {");
                pw.println("                output = call.getOutputParams();");
                pw.println("            }");
                pw.println("            catch (javax.xml.rpc.JAXRPCException jre) {");
                pw.println("            throw new java.rmi.RemoteException(\"" +
                           JavaUtils.getMessage("badCall02") + "\");");
                pw.println("            }");
                boolean firstInoutIsResp = (parms.outputs == 0);
                for (int i = 0; i < parms.list.size (); ++i) {
                    Parameter p = (Parameter) parms.list.get (i);
                    String javifiedName = Utils.xmlNameToJava(p.name);
                    if (p.mode != Parameter.IN) {
                        if (firstInoutIsResp) {
                            firstInoutIsResp = false;
                            // If expecting an array, need to call convert(..) because
                            // the runtime stores arrays in a different form (ArrayList). 
                            // (See NOTE A)
                            if (p.type.getName().endsWith("[]")) {
                                pw.println("             // REVISIT THIS!");
                                pw.println ("            " + javifiedName
                                        + ".value = (" + p.type.getName()
                                        + ") org.apache.axis.utils.JavaUtils.convert(output.get(\"" + p.name + "\"), "
                                        + p.type.getName() + ".class);");
                            }
                            else {
                                pw.println ("            " + javifiedName +
                                            ".value = " +
                                            getResponseString(p.type,  "output.get(\"" + p.name + "\")"));
                            }
                        }
                        else {
                            // If expecting an array, need to call convert(..) because
                            // the runtime stores arrays in a different form (ArrayList). 
                            // (See NOTE A)
                            if (p.type.getName().endsWith("[]")) {
                                pw.println("             // REVISIT THIS!");
                                pw.println ("            " + javifiedName
                                            + ".value = (" + p.type.getName()
                                            + ") org.apache.axis.utils.JavaUtils.convert("
                                            + "output.get(\"" + p.name + "\"), "
                                            + p.type.getName() + ".class);");
                            }
                            else {
                                pw.println ("            " + javifiedName
                                            + ".value = " + getResponseString(p.type,
                                    "output.get(\"" + p.name + "\")"));
                            }
                        }
                    }

                }
                if (parms.outputs > 0) {

                    // If expecting an array, need to call convert(..) because
                    // the runtime stores arrays in a different form (ArrayList). 
                    // (See NOTE A)
                    if (parms.returnType != null &&
                        parms.returnType.getName() != null &&
                        parms.returnType.getName().indexOf("[]") > 0) {
                        pw.println("             // REVISIT THIS!");
                        pw.println("             return ("
                                + parms.returnType.getName() + ")"
                                + "org.apache.axis.utils.JavaUtils.convert(output.get("
                                + parms.returnName + "),"
                                + parms.returnType.getName()+".class);");
                    } else if (parms.returnType != null) {
                        pw.println("             return " + getResponseString(parms.returnType, "resp"));
                    }

                }

            }
            pw.println("        }");
        }
        pw.println("    }");
        pw.println();
    } // writeOperation

} // class JavaStubWriter
