/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.geronimo.ews.ws4j2ee.toWs.wrapperWs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.geronimo.ews.ws4j2ee.context.J2EEWebServiceContext;
import org.apache.geronimo.ews.ws4j2ee.context.SEIOperation;
import org.apache.geronimo.ews.ws4j2ee.context.j2eeDD.WebContext;
import org.apache.geronimo.ews.ws4j2ee.toWs.GenerationFault;
import org.apache.geronimo.ews.ws4j2ee.toWs.JavaClassWriter;
import org.apache.geronimo.ews.ws4j2ee.toWs.UnrecoverableGenerationFault;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * <h4>WebEndpoint Based Serivce Implementation Bean</h4>
 * <p>The Service Implementation Bean must follow the Service Developer requirements outlined in the JAX-RPC specification and are listed below except as noted.</p>
 * <ol>
 * <li>?The Service Implementation Bean must have a default public constructor.</li>
 * <li>?The Service Implementation Bean may implement the Service Endpoint
 * Interface as defined by the JAX-RPC Servlet model. The bean must implement
 * all the method signatures of the SEI. In addition, a Service Implementation
 * Bean may be implemented that does not implement the SEI. This additional
 * requirement provides the same SEI implementation flexibility as provided by
 * EJB service endpoints. The business methods of the bean must be public and
 * must not be static.</li>
 * <li>If the Service Implementation Bean does not implement the SEI, the
 * business methods must not be final. The Service Implementation Bean
 * may implement other methods in addition to those defined by the SEI,
 * but only the SEI methods are exposed to the client.  </li>
 * <li>?A Service Implementation must be a stateless object. A Service
 * Implementation Bean must not save client specific state across method
 * calls either within the bean instance�s data members or external to
 * the instance. A container may use any bean instance to service a request.</li>
 * <li>?The class must be public, must not be final and must not be abstract.</li>
 * <li>?The class must not define the finalize() method.</li>
 * </ol>
 *
 * @author Srinath Perera(hemapani@opensource.lk)
 */
public class WebEndpointWrapperClassWriter extends JavaClassWriter {
    protected static Log log =
            LogFactory.getLog(WrapperWsGenerator.class.getName());
    protected String seiName = null;
    private String implBean = null;

    /**
     * @param j2eewscontext
     * @param qulifiedName
     * @throws GenerationFault
     */
    public WebEndpointWrapperClassWriter(J2EEWebServiceContext j2eewscontext)
            throws GenerationFault {
        super(j2eewscontext, getName(j2eewscontext) + "Impl");
        seiName = j2eewscontext.getMiscInfo().getJaxrpcSEI();
        WebContext webcontext = j2eewscontext.getWebDDContext();
        if (webcontext == null) {
            throw new UnrecoverableGenerationFault("for webbased Impl" +
                    " the WebDDContext must not be null");
        }
        implBean = webcontext.getServletClass();
    }

    private static String getName(J2EEWebServiceContext j2eewscontext) {
        String name = j2eewscontext.getWSDLContext().gettargetBinding().getName();
        if (name == null) {
            name = j2eewscontext.getMiscInfo().getJaxrpcSEI();
        }
        return name;
    }

    protected String getimplementsPart() {
        return " implements "
                + j2eewscontext.getMiscInfo().getJaxrpcSEI() + ",org.apache.geronimo.ews.ws4j2ee.wsutils.ContextAccssible";
    }

    protected void writeAttributes() throws GenerationFault {
        out.write("private " + implBean + " bean = null;\n");
        out.write("private org.apache.axis.MessageContext msgcontext;\n");
    }

    protected void writeConstructors() throws GenerationFault {
        out.write("\tpublic " + classname + "()throws org.apache.geronimo.ews.ws4j2ee.wsutils.J2EEFault{\n");
        out.write("\t\tbean = (" + implBean + ")org.apache.geronimo.ews.ws4j2ee.wsutils.ImplBeanPool.getImplBean(\""
                + implBean + "\");\n");
        out.write("\t}\n");
    }

    /* (non-Javadoc)
     * @see org.apache.geronimo.ews.ws4j2ee.toWs.JavaClassWriter#writeMethods()
     */
    protected void writeMethods() throws GenerationFault {
        out.write("\tpublic void setMessageContext(org.apache.axis.MessageContext msgcontext){;\n");
        out.write("\t\tthis.msgcontext = msgcontext;\n");
        out.write("\t}\n");
        String parmlistStr = null;
        ArrayList operations = j2eewscontext.getMiscInfo().getSEIOperations();
        for (int i = 0; i < operations.size(); i++) {
            parmlistStr = "";
            SEIOperation op = (SEIOperation) operations.get(i);
            String returnType = op.getReturnType();
            if (returnType == null)
                returnType = "void";
            out.write("\tpublic " + returnType + " " + op.getMethodName() + "(");
            Iterator pas = op.getParameterNames().iterator();
            boolean first = true;
            while (pas.hasNext()) {
                String name = (String) pas.next();
                String type = op.getParameterType(name);
                if (first) {
                    first = false;
                    out.write(type + " " + name);
                    parmlistStr = parmlistStr + name;
                } else {
                    out.write("," + type + " " + name);
                    parmlistStr = parmlistStr + "," + name;
                }
            }
            out.write(") throws java.rmi.RemoteException");
            ArrayList faults = op.getFaults();
            for (int j = 0; j < faults.size(); j++) {
                out.write("," + (String) faults.get(j));
            }
            out.write("{\n");
            if (!"void".equals(returnType))
                out.write("\t\treturn bean." + op.getMethodName() + "(" + parmlistStr + ");\n");
            else
                out.write("\t\tbean." + op.getMethodName() + "(" + parmlistStr + ");\n");
            out.write("\t}\n");
        }
    }

}
