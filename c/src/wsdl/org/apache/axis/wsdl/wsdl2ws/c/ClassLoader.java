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
 * @author Srinath Perera(hemapani@openource.lk)
 * @author Susantha Kumara(susantha@opensource.lk, skumara@virtusa.com)
 */

package org.apache.axis.wsdl.wsdl2ws.c;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.axis.wsdl.wsdl2ws.SourceWriter;
import org.apache.axis.wsdl.wsdl2ws.WrapperFault;
import org.apache.axis.wsdl.wsdl2ws.WrapperUtils;
import org.apache.axis.wsdl.wsdl2ws.CUtils;
import org.apache.axis.wsdl.wsdl2ws.info.WebServiceContext;

public class ClassLoader implements SourceWriter{
	private Writer writer;
	private WebServiceContext wscontext;
	private String classname;
	public ClassLoader(WebServiceContext wscontext) throws WrapperFault {
		this.wscontext = wscontext;
		this.classname = WrapperUtils.getClassNameFromFullyQualifiedName(wscontext.getSerInfo().getQualifiedServiceName());
	}

	public void writeSource() throws WrapperFault {
		String wrappername = classname + CUtils.WRAPPER_NAME_APPENDER;
	try{	
	  this.writer = new BufferedWriter(new FileWriter(getFilePath(), false));
	  writer.write("/*This file is automatically generated by the Axis C++ Wrapper Class Generator\n");
	  writer.write(" *Service file containing the two export functions of the Web service Library*/\n");
	  writer.write("#ifdef WIN32\n");
	  writer.write("#define STORAGE_CLASS_INFO __declspec(dllexport)\n");
	  writer.write("#else\n");
	  writer.write("#define STORAGE_CLASS_INFO \n");
	  writer.write("#endif\n\n");
	  writer.write("#include <malloc.h>\n");
	  writer.write("#include \""+wrappername+".h\"\n\n");
	  writer.write("STORAGE_CLASS_INFO\n");
	  writer.write("int GetClassInstance(BasicHandler **inst)\n");
	  writer.write("{\n");
	  writer.write("\tBasicHandler* pBH = malloc(sizeof(BasicHandler));\n");
	  writer.write("\tBasicHandlerX* pBHX = malloc(sizeof(BasicHandlerX));\n");
	  writer.write("\tif (pBHX)\n");
	  writer.write("\t{\n");
	  writer.write("\t\tpBHX->Invoke = Invoke;\n");
	  writer.write("\t\tpBHX->OnFault = OnFault;\n");
	  writer.write("\t\tpBHX->Init = Init;\n");
	  writer.write("\t\tpBHX->Fini = Fini;\n");
	  writer.write("\t\tpBH->__vfptr = pBHX;\n");
	  writer.write("\t\t*inst = pBH;\n");
	  writer.write("\t\treturn AXIS_SUCCESS;\n");
	  writer.write("\t}\n");
	  writer.write("\treturn AXIS_FAIL;\n");
	  writer.write("}\n\n");
	  writer.write("STORAGE_CLASS_INFO \n");
	  writer.write("int DestroyInstance(BasicHandler *inst)\n");
	  writer.write("{\n");
	  writer.write("\tBasicHandler* pBH;\n");
	  writer.write("\tif (inst)\n"); 
	  writer.write("\t{\n");
	  writer.write("\t\tpBH = inst;\n");
	  writer.write("\t\tfree(pBH->__vfptr);\n");
	  writer.write("\t\tfree(pBH);\n");
	  writer.write("\t\treturn AXIS_SUCCESS;\n");
	  writer.write("\t}\n");
	  writer.write("\treturn AXIS_FAIL;\n");
	  writer.write("}\n");
	  writer.flush();
	  writer.close();
	  System.out.println(getFilePath().getAbsolutePath() + " created.....");
	 }catch(IOException e){
		throw new WrapperFault(e);
	 }
	}

	protected File getFilePath() throws WrapperFault {
		String targetOutputLocation = this.wscontext.getWrapInfo().getTargetOutputLocation();
		if(targetOutputLocation.endsWith("/"))
			targetOutputLocation = targetOutputLocation.substring(0, targetOutputLocation.length() - 1);
		new File(targetOutputLocation).mkdirs();
		String fileName = targetOutputLocation + "/" + classname + CUtils.CLASS_LODER_APPENDER + ".c";
		return new File(fileName);
	}

}
