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

package org.apache.axis.wsdl.wsdl2ws.cpp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.axis.wsdl.wsdl2ws.WrapperFault;
import org.apache.axis.wsdl.wsdl2ws.WrapperUtils;
import org.apache.axis.wsdl.wsdl2ws.info.MethodInfo;
import org.apache.axis.wsdl.wsdl2ws.info.ParameterInfo;
import org.apache.axis.wsdl.wsdl2ws.info.Type;
import org.apache.axis.wsdl.wsdl2ws.info.WebServiceContext;

public class ServiceHeaderWriter extends HeaderFileWriter{
	private WebServiceContext wscontext;
	private ArrayList methods;	
	public ServiceHeaderWriter(WebServiceContext wscontext)throws WrapperFault{
		super(WrapperUtils.getClassNameFromFullyQualifiedName(wscontext.getSerInfo().getQualifiedServiceName()));
		this.wscontext = wscontext;
		this.methods = wscontext.getSerInfo().getMethods();
	}

	protected File getFilePath() throws WrapperFault {
		String targetOutputLocation = this.wscontext.getWrapInfo().getTargetOutputLocation();
		if(targetOutputLocation.endsWith("/"))
			targetOutputLocation = targetOutputLocation.substring(0, targetOutputLocation.length() - 1);
		new File(targetOutputLocation).mkdirs();
		String fileName = targetOutputLocation + "/" + classname + ".h";
		return new File(fileName);
	}

	/* (non-Javadoc)
	 * @see org.apache.axis.wsdl.wsdl2ws.cpp.HeaderFileWriter#writeAttributes()
	 */
	protected void writeAttributes() throws WrapperFault {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.axis.wsdl.wsdl2ws.cpp.HeaderFileWriter#writeClassComment()
	 */
	protected void writeClassComment() throws WrapperFault {
		try{
			writer.write("/*\n");
			writer.write(" * This is the Service Class genarated by the tool WSDL2Ws\n");
			writer.write(" * "+classname+".h: interface for the "+classname+"class.\n");
			writer.write(" *\n");
			writer.write(" */\n");
		}catch(IOException e){
			throw new WrapperFault(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.axis.wsdl.wsdl2ws.cpp.HeaderFileWriter#writeConstructors()
	 */
	protected void writeConstructors() throws WrapperFault {
		try{
		writer.write("\tpublic:\n\t\t"+classname+"();\n");
		}catch(IOException e){
			throw new WrapperFault(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.axis.wsdl.wsdl2ws.cpp.HeaderFileWriter#writeDistructors()
	 */
	protected void writeDistructors() throws WrapperFault {
		try{
		writer.write("\tpublic:\n\t\tvirtual ~"+classname+"();\n");
		}catch(IOException e){
			throw new WrapperFault(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.axis.wsdl.wsdl2ws.cpp.HeaderFileWriter#writeMethods()
	 */
	protected void writeMethods() throws WrapperFault {
		MethodInfo minfo;
		boolean isSimpleType;
		try{
		  	writer.write("\tpublic: \n");	
		  	for(int i = 0; i < methods.size(); i++){
			  	minfo = (MethodInfo)this.methods.get(i);
				boolean isAllTreatedAsOutParams = false;
				ParameterInfo returntype = null;
				int noOfOutParams = minfo.getOutputParameterTypes().size();
				if (0==noOfOutParams){
					returntype = null;
					writer.write("\t\tvoid ");
				}
				else if (1==noOfOutParams){
					returntype = (ParameterInfo)minfo.getOutputParameterTypes().iterator().next();
					String outparam = returntype.getLangName();
					writer.write("\t\t"+WrapperUtils.getClassNameFromParamInfoConsideringArrays(returntype,wscontext)+" ");
				}
				else{
					isAllTreatedAsOutParams = true;
					writer.write("\t\tvoid ");
				}
			  	//write return type
			  	writer.write(minfo.getMethodname()+"(");
			  	//write parameter names 
			  	Iterator params = minfo.getInputParameterTypes().iterator();
			  	if(params.hasNext()){
			  	  	ParameterInfo fparam = (ParameterInfo)params.next();
				  	writer.write(WrapperUtils.getClassNameFromParamInfoConsideringArrays(fparam,wscontext)+" Value"+0);
			  	}
			  	for(int j =1; params.hasNext();j++){
				  	ParameterInfo nparam = (ParameterInfo)params.next();
				  	writer.write(","+WrapperUtils.getClassNameFromParamInfoConsideringArrays(nparam,wscontext)+" Value"+j);
			  	}
				if (isAllTreatedAsOutParams){
					params = minfo.getOutputParameterTypes().iterator();
					for(int j =0; params.hasNext();j++){
						ParameterInfo nparam = (ParameterInfo)params.next();
						writer.write(", AXIS_OUT_PARAM "+WrapperUtils.getClassNameFromParamInfoConsideringArrays(nparam,wscontext)+" *OutValue"+j);
					}
				}			  	
			  	writer.write(");\n");
		  	}
		}catch (Exception e){
			e.printStackTrace();
			throw new WrapperFault(e);
		}	
	}

	/* (non-Javadoc)
	 * @see org.apache.axis.wsdl.wsdl2ws.cpp.HeaderFileWriter#writePreprocssorStatements()
	 */
	protected void writePreprocssorStatements() throws WrapperFault {
		try{
			Type atype;
			Iterator types = this.wscontext.getTypemap().getTypes().iterator();
			HashSet typeSet = new HashSet();
			writer.write("#include <axis/server/AxisUserAPI.h>\n\n");
			while(types.hasNext()){
				atype = (Type)types.next();
				if (atype.getLanguageSpecificName().startsWith(">")) continue;				
				typeSet.add(atype.getLanguageSpecificName());
			}		
			Iterator itr = typeSet.iterator();
			while(itr.hasNext())
			{
				writer.write("#include \""+itr.next().toString()+".h\"\n");
			}		
			writer.write("\n");
		}catch (IOException e) {
			e.printStackTrace();
			throw new WrapperFault(e);
		}
	}
	protected String getFileType()
	{
		return "ServerSkeleton";	
	}
}

