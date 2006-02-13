/*
 *   Copyright 2003-2004 The Apache Software Foundation.
// (c) Copyright IBM Corp. 2004, 2005 All Rights Reserved
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

/**
 * @author Srinath Perera(hemapani@openource.lk)
 * @author Susantha Kumara(susantha@opensource.lk, skumara@virtusa.com)
 */

package org.apache.axis.wsdl.wsdl2ws.cpp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.axis.wsdl.wsdl2ws.CUtils;
import org.apache.axis.wsdl.wsdl2ws.WrapperFault;
import org.apache.axis.wsdl.wsdl2ws.WrapperUtils;
import org.apache.axis.wsdl.wsdl2ws.info.MethodInfo;
import org.apache.axis.wsdl.wsdl2ws.info.ParameterInfo;
import org.apache.axis.wsdl.wsdl2ws.info.WebServiceContext;

public class ServiceWriter extends CPPClassWriter
{
    private ArrayList methods;
    public ServiceWriter(WebServiceContext wscontext) throws WrapperFault
    {
        super(
            WrapperUtils.getClassNameFromFullyQualifiedName(
                wscontext.getSerInfo().getQualifiedServiceName()));
        this.wscontext = wscontext;
        this.methods = wscontext.getSerInfo().getMethods();
    }

    public void writeSource() throws WrapperFault
    {
        // We should not overwrite the service file if it already exsists
        if (! getFilePath().exists())
            super.writeSource();
    }

    protected void writeClassComment() throws WrapperFault
    {
        try
        {
            writer.write("/*\n");
			writer.write(" * Copyright 2003-2006 The Apache Software Foundation.\n\n");
			writer.write(" *\n");
			writer.write(" * Licensed under the Apache License, Version 2.0 (the \"License\");\n");
			writer.write(" * you may not use this file except in compliance with the License.\n");
			writer.write(" * You may obtain a copy of the License at\n");
			writer.write(" *\n");
			writer.write(" *\t\thttp://www.apache.org/licenses/LICENSE-2.0\n");
			writer.write(" *\n");
			writer.write(" * Unless required by applicable law or agreed to in writing, software\n");
			writer.write(" * distributed under the License is distributed on an \"AS IS\" BASIS,\n");
			writer.write(" * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n");
			writer.write(" * See the License for the specific language governing permissions and\n");
			writer.write(" * limitations under the License.\n");
			writer.write(" *\n");
            writer.write(" * This file was auto-generated by the Axis C++ Web Service Generator (WSDL2Ws)\n");
            writer.write(" * This file contains definitions of the web service\n");
            writer.write(" */\n\n");
        }
        catch (IOException e)
        {
            throw new WrapperFault(e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.axis.wsdl.wsdl2ws.cpp.HeaderFileWriter#writeConstructors()
     */
    protected void writeConstructors() throws WrapperFault
    {
        try
        {
            writer.write("\n" + classname + "::" + classname + "()\n{\n");
            writer.write("}\n");
        }
        catch (IOException e)
        {
            throw new WrapperFault(e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.axis.wsdl.wsdl2ws.cpp.HeaderFileWriter#writeDistructors()
     */
    protected void writeDestructors() throws WrapperFault
    {
        try
        {
            writer.write("\n" + classname + "::~" + classname + "()\n{\n");
            writer.write("}\n");
        }
        catch (IOException e)
        {
            throw new WrapperFault(e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.axis.wsdl.wsdl2ws.cpp.HeaderFileWriter#writeMethods()
     */
    protected void writeMethods() throws WrapperFault
    {
        MethodInfo minfo;
        try
        {
            writer.write(
                "\n/* This function is called by the AxisEngine when something went wrong"
                    + "\n with the current web service request processing. Appropriate actions should"
                    + "\n be taken here.*/");
            writer.write("\nvoid " + classname + "::onFault()\n{\n}");
            /*writer.write(
                "\n/* This function is called by the AxisEngine when this web service"
                    + "\n library is first loaded. So here we can initialize any global/static"
                    + "\n data structures of this web service or open database connections *//*");
            writer.write("\nvoid " + classname + "::init()\n{\n}");
            writer.write(
                "\n/* This function is called by the AxisEngine when this web service"
                    + "\n library is unloaded. So we can deallocate any global/static data structures"
                    + "\n and close database connections etc here. *//*");
            writer.write("\nvoid " + classname + "::fini()\n{\n}");*/
            writer.write("\n\n");
            for (int i = 0; i < methods.size(); i++)
            {
                minfo = (MethodInfo) this.methods.get(i);
                boolean isAllTreatedAsOutParams = false;
                ParameterInfo returntype = null;
                int noOfOutParams = minfo.getOutputParameterTypes().size();
                if (0 == noOfOutParams)
                {
                    returntype = null;
                    writer.write("void ");
                }
                else
                {
                    if (1 == noOfOutParams)
                    {
                        returntype =
                            (ParameterInfo) minfo
                                .getOutputParameterTypes()
                                .iterator()
                                .next();
                        String returnTypeName = returntype.getLangName();
                        String returnType = WrapperUtils.getClassNameFromParamInfoConsideringArrays(returntype,wscontext);
                        if ((returnType.lastIndexOf ("_Array") > 0)||(CUtils.isSimpleType(returntype.getLangName())
                        		&& (returntype.isNillable()|| returntype.isOptional())
								&& !(CUtils.isPointerType(returnTypeName))))
                        {
                        	writer.write(
                        			returnType
    	                                + " * ");
                        }
                        else
                        {
	                        writer.write(
	                        		returnType
	                                + " ");
                        }
                    }
                    else
                    {
                        isAllTreatedAsOutParams = true;
                        writer.write("void ");
                    }
                }
                writer.write(classname + "::" + minfo.getMethodname() + "(");
                //write parameter names 
                
                boolean hasInputParms = false;
                Iterator params = minfo.getInputParameterTypes().iterator();
                if (params.hasNext())
                {
                	hasInputParms = true;
                	ParameterInfo fparam = (ParameterInfo) params.next();
                    String fparamTypeName = fparam.getLangName();
                    String fparamType = WrapperUtils.getClassNameFromParamInfoConsideringArrays(fparam,wscontext);
                    
                    if (fparam.getType().isAttachment())
                    {
                    	writer.write("ISoapAttachment *Value" + 0);
                    }
                    
                    else if ((fparamType.lastIndexOf ("_Array") > 0)||(CUtils.isSimpleType(fparamTypeName)
							&& (fparam.isNillable()|| fparam.isOptional())
							&& !(CUtils.isPointerType(fparamTypeName))))
                    {
                    	writer.write(
                    			fparamType
    	                            + " * Value"
    	                            + 0);
                    }
                    else
                    {
	                    writer.write(
	                    		fparamType
	                            + " Value"
	                            + 0);
                    }
                }
                for (int j = 1; params.hasNext(); j++)
                {
                	ParameterInfo nparam = (ParameterInfo) params.next();
                    String paramTypeName = nparam.getLangName();
                    String paramType = WrapperUtils.getClassNameFromParamInfoConsideringArrays(nparam,wscontext);
                    
                    if (nparam.getType().isAttachment())
                    {
                    	writer.write(", ISoapAttachment *Value" + j);
                    }
                                        
                    else if ((paramType.lastIndexOf ("_Array") > 0)||(CUtils.isSimpleType(paramTypeName)
                    		&& (nparam.isNillable()|| nparam.isOptional())
							&& !(CUtils.isPointerType(paramTypeName))))
                    {
                    	writer.write(
    	                        ","
    	                            + paramType
    	                            + " * Value"
    	                            + j);
                    }
                    else
                    {
	                    writer.write(
	                        ","
	                            + paramType
	                            + " Value"
	                            + j);
                    }
                }
                if (isAllTreatedAsOutParams)
                {
                    params = minfo.getOutputParameterTypes().iterator();
                    for (int j = 0; params.hasNext(); j++)
                    {
                        ParameterInfo nparam = (ParameterInfo) params.next();
                        
                        if (0 != j || hasInputParms)
                        {
                            writer.write(",");
                        }
                        
                        String typeName = WrapperUtils.getClassNameFromParamInfoConsideringArrays(
                                        	nparam, wscontext);
                        
                        writer.write(" AXIS_OUT_PARAM "
                                + typeName + " ");
                        
                        
                        
                        if (( nparam.isOptional() || nparam.isNillable() )
                                && CUtils.isSimpleType(typeName)
                                && !CUtils.isPointerType(typeName)
                                && !nparam.isArray())
                        {
                            writer.write("*");
                        }
                        
                        writer.write("* OutValue" + j);
                    }
                }
                writer.write(")  \n{\n}\n\n");

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new WrapperFault(e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.axis.wsdl.wsdl2ws.cpp.HeaderFileWriter#writePreprocssorStatements()
     */
    protected void writePreprocessorStatements() throws WrapperFault
    {
        try
        {
            writer.write(
                "#include \""
                    + classname
                    + CUtils.CPP_HEADER_SUFFIX
                    + "\"\n\n");
        }
        catch (IOException e)
        {
            throw new WrapperFault(e);
        }
    }
}
