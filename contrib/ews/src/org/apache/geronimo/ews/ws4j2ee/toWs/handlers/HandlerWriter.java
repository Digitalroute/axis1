/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2004 The Apache Software Foundation.  All rights
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

package org.apache.geronimo.ews.ws4j2ee.toWs.handlers;

import org.apache.geronimo.ews.ws4j2ee.context.J2EEWebServiceContext;
import org.apache.geronimo.ews.ws4j2ee.context.webservices.server.interfaces.WSCFHandler;
import org.apache.geronimo.ews.ws4j2ee.context.webservices.server.interfaces.WSCFSOAPHeader;
import org.apache.geronimo.ews.ws4j2ee.toWs.AbstractWriter;
import org.apache.geronimo.ews.ws4j2ee.toWs.GenerationFault;
import org.apache.geronimo.ews.ws4j2ee.utils.Utils;

/**
 * <p>Simpley print the Handler without much mess.</p>
 * 
 * @author Srinath perera(hemapani@opensource.lk)
 */
public class HandlerWriter extends AbstractWriter {
    private WSCFHandler handler;
    private String className = null;
    private String packageName = null;
    
    /**
     * @param j2eewscontext 
     * @throws GenerationFault 
     */
    public HandlerWriter(J2EEWebServiceContext j2eewscontext, WSCFHandler handler)
            throws GenerationFault {
        super(j2eewscontext,j2eewscontext.getMiscInfo().getOutPutPath()
			+  "/" + handler.getHandlerClass().replace('.', '/') +  ".java");
        this.handler = handler;

        className = Utils.getClassNameFromQuallifiedName(handler.getHandlerClass());
        packageName = Utils.getPackageNameFromQuallifiedName(handler.getHandlerClass());
    }

    public String getFileName() {
        throw new UnsupportedOperationException();
    }

    /**
     * just print it out
     */
    public void writeCode() throws GenerationFault {
        super.writeCode();
        if(out == null)
        	return;
        out.write("package " + packageName + ";\n");
		out.write("import org.apache.axis.AxisFault;\n");
		out.write("import org.apache.axis.MessageContext;\n");
        out.write("public class " + className + " extends org.apache.axis.handlers.BasicHandler{\n");

        out.write("\tpublic " + className + "(){\n");
        out.write("\t\tsetName(\"" + handler.getHandlerName() + "\");\n");
        out.write("\t}\n");

        out.write("\tpublic void init(){}\n");
        out.write("\tpublic void cleanup(){}\n");
        out.write("\tpublic void onFault(MessageContext msgContext){}\n");
        out.write("\tpublic void invoke(MessageContext msgContext) throws AxisFault{\n");
        out.write("\t\t//write your implementation here\n");
        out.write("\t}\n");

        out.write("\tpublic java.util.List getUnderstoodHeaders() {\n");
        out.write("\t\t	java.util.List list = new java.util.ArrayList();\n");
		WSCFSOAPHeader[] headers = handler.getSoapHeader();
        for (int i = 0; i < headers.length; i++) {
            out.write("\t\tjavax.xml.namespace.QName name" + i + " = new javax.xml.namespace.QName(\"" + headers[i].getNamespaceURI()+"\",\""+ headers[i].getNamespaceURI()+ "\");\n");
            out.write("\t\tlist.add(name" + i + ");\n");
        }
        out.write("\t\treturn list;\n");
        out.write("\t}\n");

        out.write("}");
        out.flush();
        out.close();
    }
    
    

}
