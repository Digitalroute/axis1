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

import java.io.IOException;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Fault;
import javax.wsdl.Input;
import javax.wsdl.Operation;
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
                JavaUtils.getMessage("genStub00"));
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
        String name = Utils.xmlNameToJava(qname.getLocalPart());
        String portTypeName = ptEntry.getName();
        boolean isRPC = true;
        if (bEntry.getBindingStyle() == BindingEntry.STYLE_DOCUMENT) {
            isRPC = false;
        }

        pw.println("public class " + className + " extends javax.xml.rpc.Stub implements " + portTypeName + " {");
        pw.println("    private org.apache.axis.client.Service service = null ;");
        pw.println("    private org.apache.axis.client.Call call = null ;");
        pw.println("    private java.util.Hashtable properties = new java.util.Hashtable();");
        pw.println();
        pw.println("    public " + className + "(java.net.URL endpointURL) throws org.apache.axis.AxisFault {");
        pw.println("         this();");
        pw.println("         call.setTargetEndpointAddress( endpointURL );");
        pw.println("         call.setProperty(org.apache.axis.transport.http.HTTPTransport.URL, endpointURL.toString());");
        pw.println("    }");

        pw.println("    public " + className + "() throws org.apache.axis.AxisFault {");

        HashSet types = getTypesInPortType(portType);
        Iterator it = types.iterator();

        pw.println("        try {" );
        pw.println("            service = new org.apache.axis.client.Service();");
        pw.println("            call = (org.apache.axis.client.Call) service.createCall();");

        while (it.hasNext()) {
            writeSerializationInit((Type) it.next());
        }

        pw.println("        }");
        pw.println("        catch(Exception t) {");
        pw.println("            throw org.apache.axis.AxisFault.makeFault(t);");
        pw.println("        }");

        pw.println("    }");
        pw.println();
        pw.println("    public void _setProperty(String name, Object value) {");
        pw.println("        properties.put(name, value);");
        pw.println("    }");
        pw.println();
        pw.println("    // " +
                JavaUtils.getMessage("from00", "javax.xml.rpc.Stub"));
        pw.println("    public Object _getProperty(String name) {");
        pw.println("        return properties.get(name);");
        pw.println("    }");
        pw.println();
        pw.println("    // " +
                JavaUtils.getMessage("from00", "javax.xml.rpc.Stub"));
        pw.println("    public void _setTargetEndpoint(java.net.URL address) {");
        pw.println("        call.setProperty(org.apache.axis.transport.http.HTTPTransport.URL, address.toString());");
        pw.println("    }");
        pw.println();
        pw.println("    // " +
                JavaUtils.getMessage("from00", "javax.xml.rpc.Stub"));
        pw.println("    public java.net.URL _getTargetEndpoint() {");
        pw.println("        try {");
        pw.println("            return new java.net.URL((String) call.getProperty(org.apache.axis.transport.http.HTTPTransport.URL));");
        pw.println("        }");
        pw.println("        catch (java.net.MalformedURLException mue) {");
        pw.println("            return null; // ???");
        pw.println("        }");
        pw.println("    }");
        pw.println();
        pw.println("    // " +
                JavaUtils.getMessage("from00", "javax.xml.rpc.Stub"));
        pw.println("    public synchronized void setMaintainSession(boolean session) {");
        pw.println("        call.setMaintainSession(session);");
        pw.println("    }");
        pw.println();
        pw.println("    // From javax.naming.Referenceable");
        pw.println("    public javax.naming.Reference getReference() {");
        pw.println("        return null; // ???");
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
            String namespace = "";
            Iterator bindingInputIterator
                    = operation.getBindingInput().getExtensibilityElements().iterator();
            for (; bindingInputIterator.hasNext();) {
                Object obj = bindingInputIterator.next();
                if (obj instanceof SOAPBody) {
                    namespace = ((SOAPBody) obj).getNamespaceURI();
                    if (namespace == null)
                        namespace = "";
                    break;
                }
            }
            writeOperation(operation, parameters, soapAction, namespace, isRPC);
        }
        pw.println("}");
        pw.close();
    } // writeFileBody

    /**
     * This method returns a set of all the Types in a given PortType.
     * The elements of the returned HashSet are Types.
     */
    private HashSet getTypesInPortType(PortType portType) {
        HashSet types = new HashSet();
        HashSet firstPassTypes = new HashSet();

        // Get all the types from all the operations
        List operations = portType.getOperations();

        for (int i = 0; i < operations.size(); ++i) {
            firstPassTypes.addAll(getTypesInOperation((Operation) operations.get(i)));
        }

        // Extract those types which are complex types.
        Iterator i = firstPassTypes.iterator();
        while (i.hasNext()) {
            Type type = (Type) i.next();
            if (!types.contains(type)) {
                types.add(type);
                if (type.isDefined() && type.getBaseType() == null) {
                    types.addAll(
                            Utils.getNestedTypes(type.getNode(), symbolTable));
                }
            }
        }
        return types;
    } // getTypesInPortType

    /**
     * This method returns a set of all the Types in a given Operation.
     * The elements of the returned HashSet are Types.
     */
    private HashSet getTypesInOperation(Operation operation) {
        HashSet types = new HashSet();
        Vector v = new Vector();

        // Collect all the input types
        Input input = operation.getInput();

        if (input != null) {
            partTypes(v,
                    input.getMessage().getOrderedParts(null),
                    (bEntry.getInputBodyType(operation) == BindingEntry.USE_LITERAL));
        }

        // Collect all the output types
        Output output = operation.getOutput();

        if (output != null) {
            partTypes(v,
                    output.getMessage().getOrderedParts(null),
                    (bEntry.getOutputBodyType(operation) == BindingEntry.USE_LITERAL));
        }

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
     * This method returns a vector of Types for the parts.
     */
    private void partTypes(Vector v, Collection parts, boolean literal) {
        Iterator i = parts.iterator();

        while (i.hasNext()) {
            Part part = (Part) i.next();

            QName qType;
            if (literal) {
                qType = part.getElementName();
                if (qType != null) {
                    v.add(symbolTable.getElementTypeEntry(qType));
                }
            } else {
                qType = part.getTypeName(); 
                if (qType == null) {
                    qType = part.getElementName();
                    if (qType != null) {
                        v.add(symbolTable.getElementTypeEntry(qType));
                    }
                }
                else {
                    if (qType != null) {
                        v.add(symbolTable.getTypeEntry(qType));
                    }
                }
            }
        }
    } // partTypes

    /**
     * In the stub constructor, write the serializer code for the complex types.
     */
    private boolean firstSer = true ;

    private void writeSerializationInit(Type type) throws IOException {
        if (type.getBaseType() != null || !type.getShouldEmit()) {
            return;
        }
        if ( firstSer ) {
            pw.println("            javax.xml.rpc.namespace.QName qn;" );
            pw.println("            Class cls;" );
        }
        firstSer = false ;

        QName qname = type.getQName();
        pw.println("            qn = new javax.xml.rpc.namespace.QName(\"" + qname.getNamespaceURI() + "\", \"" + qname.getLocalPart() + "\");");
        pw.println("            cls = " + type.getJavaName() + ".class;");
        pw.println("            call.addSerializer(cls, qn, new org.apache.axis.encoding.BeanSerializer(cls));");
        pw.println("            call.addDeserializerFactory(qn, cls, org.apache.axis.encoding.BeanSerializer.getFactory());");
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
        pw.println("        if (call.getProperty(org.apache.axis.transport.http.HTTPTransport.URL) == null) {");
        pw.println("            throw new org.apache.axis.NoEndPointException();");
        pw.println("        }");
        pw.println("        call.removeAllParameters();");

        // DUG: need to set the isRPC flag in the Call object

        // loop over paramters and set up in/out params
        for (int i = 0; i < parms.list.size(); ++i) {
            Parameter p = (Parameter) parms.list.get(i);


            QName qn = p.type.getQName();
            String typeString = "new org.apache.axis.encoding.XMLType( new javax.xml.rpc.namespace.QName(\"" + qn.getNamespaceURI() + "\", \"" +
                    qn.getLocalPart() + "\"))";
            if (p.mode == Parameter.IN) {
                pw.println("        call.addParameter(\"" + p.name + "\", " + typeString + ", org.apache.axis.client.Call.PARAM_MODE_IN);");
            }
            else if (p.mode == Parameter.INOUT) {
                pw.println("        call.addParameter(\"" + p.name + "\", " + typeString + ", call.PARAM_MODE_INOUT);");
            }
            else { // p.mode == Parameter.OUT
                pw.println("        call.addParameter(\"" + p.name + "\", " + typeString + ", call.PARAM_MODE_OUT);");
            }
        }
        // set output type
        if (parms.returnType != null) {
            QName qn = parms.returnType.getQName();
            String outputType = "new org.apache.axis.encoding.XMLType(new javax.xml.rpc.namespace.QName(\"" + qn.getNamespaceURI() + "\", \"" +
                qn.getLocalPart() + "\"))";
            pw.println("        call.setReturnType(" + outputType + ");");

            pw.println();
        }

        pw.println("        call.setProperty(org.apache.axis.transport.http.HTTPTransport.ACTION, \"" + soapAction + "\");");
        pw.println("        call.setProperty(call.NAMESPACE, \"" + namespace
                                                 + "\");" );
        pw.println("        call.setOperationName( \"" + operation.getName() + "\");" );
        pw.print("        Object resp = call.invoke(");
        pw.print("new Object[] {");

        // Write the input and inout parameter list
        boolean needComma = false;
        for (int i = 0; i < parms.list.size(); ++i) {
            Parameter p = (Parameter) parms.list.get(i);

            if (needComma) {
                if (p.mode != Parameter.OUT)
                    pw.print(", ");
            }
            else
                needComma = true;
            if (p.mode == Parameter.IN)
                pw.print(wrapPrimitiveType(p.type, p.name));
            else if (p.mode == Parameter.INOUT)
                pw.print(wrapPrimitiveType(p.type, p.name + "._value"));
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

                    while (p.mode != Parameter.INOUT)
                        p = (Parameter) parms.list.get(++i);
                    pw.println ("            " + p.name + "._value = " + getResponseString(p.type, "resp"));
                }
                else {
                    // (parms.outputs == 1)
                    // There is only one output and it is the return value.
                    pw.println("             return " + getResponseString(parms.returnType, "resp"));
                }
            }
            else {
                // There is more than 1 output.  resp is the first one.  The rest are from
                // call.getOutputParams ().  Pull the Objects from the appropriate place -
                // resp or call.getOutputParms - and put them in the appropriate place,
                // either in a holder or as the return value.
                pw.println("            java.util.Vector output = call.getOutputParams();");
                int outdex = 0;
                boolean firstInoutIsResp = (parms.outputs == 0);
                for (int i = 0; i < parms.list.size (); ++i) {
                    Parameter p = (Parameter) parms.list.get (i);
                    if (p.mode != Parameter.IN) {
                        if (firstInoutIsResp) {
                            firstInoutIsResp = false;
                            pw.println ("            " + p.name + "._value = " + getResponseString(p.type,  "resp"));
                        }
                        else {
                            pw.println ("            " + p.name + "._value = " + getResponseString(p.type, "((org.apache.axis.message.RPCParam) output.get(" + outdex++ + ")).getValue()"));
                        }
                    }

                }
                if (parms.outputs > 0)
                    pw.println ("            return " + getResponseString(parms.returnType, "resp"));

            }
            pw.println("        }");
        }
        pw.println("    }");
        pw.println();
    } // writeOperation

} // class JavaStubWriter
