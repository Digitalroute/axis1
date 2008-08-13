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
 * @author Samisa Abeysinghe (sabeysinghe@virtusa.com)
 */

package org.apache.axis.wsdl.wsdl2ws.c;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.namespace.QName;

import org.apache.axis.wsdl.wsdl2ws.CUtils;
import org.apache.axis.wsdl.wsdl2ws.ParamWriter;
import org.apache.axis.wsdl.wsdl2ws.WSDL2Ws;
import org.apache.axis.wsdl.wsdl2ws.WrapperFault;
import org.apache.axis.wsdl.wsdl2ws.WrapperUtils;
import org.apache.axis.wsdl.wsdl2ws.info.Type;
import org.apache.axis.wsdl.wsdl2ws.info.WebServiceContext;

public class ArrayParamWriter extends ParamWriter
{
    /**
     * @param wscontext
     * @param type
     * @throws WrapperFault
     */
    public ArrayParamWriter(WebServiceContext wscontext, Type type)
        throws WrapperFault
    {
        super(wscontext, type);
    }

    /* (non-Javadoc)
     * @see org.apache.axis.wsdl.wsdl2ws.SourceWriter#writeSource()
     */
    public void writeSource() throws WrapperFault
    {
        try
        {
            this.writer = new BufferedWriter(new FileWriter(getFilePath(), false));
            
            // Write prolog
            writeClassComment(); 

            // include system header files
            writer.write("#include <stdlib.h>\n");
            writer.write("#include <stdio.h>\n");
            writer.write("#include <string.h>\n");
            writer.write("\n");
            
            // include header file for datatype
            writer.write("#include \"" + classname + ".h\"\n");
            
            // include header file for the contained type
            QName qname = WrapperUtils.getArrayType(type).getName();
            if (!CUtils.isSimpleType(qname))
                writer.write("#include \"" + attribs[0].getTypeName() + CUtils.C_HEADER_SUFFIX + "\"\n");
            this.writer.write("\n");
            
            writer.write("\n");
            writer.write("#include <axis/AxisWrapperAPI.h>\n");
            writer.write("#include <axis/IWrapperSoapSerializer.h>\n");
            writer.write("#include <axis/IWrapperSoapDeSerializer.h>\n");
            writer.write("#include <axis/client/Stub.h>\n");
            writer.write("#include <axis/client/Call.h>\n");
            writer.write("\n");
            
            this.writeMethods();
            
            writer.flush();
            writer.close();
            if (WSDL2Ws.c_verbose)
                System.out.println(getFilePath().getAbsolutePath() + " created.....");
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new WrapperFault(e);
        }
    }
    /* (non-Javadoc)
     * @see org.apache.axis.wsdl.wsdl2ws.BasicFileWriter#writeMethods()
     */
    protected void writeMethods() throws WrapperFault
    {
        this.writeConstructors();
        this.writeDestructors();
        this.writeSetMethod();
        this.writeGetMethod();
        this.writeCloneMethod();
        this.writeClearMethod();
    }

    /**
     * @throws WrapperFault
     * 
     */
    protected void writeClearMethod() throws WrapperFault
    {}
    
    /**
     * @throws IOException
     */
    protected void writeCloneMethod() throws WrapperFault
    { }
    
    /**
     * @throws IOException
     */
    protected void writeGetMethod() throws WrapperFault
    {}
    
    /**
     * @throws IOException
     */
    protected void writeSetMethod() throws WrapperFault
    {}
    
    protected void writeConstructors() throws WrapperFault
    {
        try
        {
            CUtils.printMethodComment(writer, "Function used to create objects of type " 
                    + classname + ".");
            
            writer.write("extern void* Axis_Create_" + classname + "(int nSize)\n");
            writer.write("{\n");
            
            // Begin function body

            writer.write("\t/* Create array data type */\n");
            writer.write("\t" + classname + " *pArray = (" + classname + "*)axiscAxisNew(XSDC_ARRAY, 0);\n");
            writer.write("\tpArray->m_Type = C_USER_TYPE;\n");
            writer.write("\n");

            writer.write("\treturn pArray;\n");
            
            // End function body

            writer.write("}\n");
        }
        catch (IOException e)
        {
            throw new WrapperFault(e);
        }
        
    }
    
    protected void writeDestructors() throws WrapperFault
    {
        try
        {
            CUtils.printMethodComment(writer, "Function used to delete objects of type " 
                    + classname + ".");
            
            this.writer.write("extern void Axis_Delete_" + classname 
                    + "(" + classname + "* param, int nSize)\n");
            writer.write("{\n");
            
            // Begin function body
            
            writer.write("\t/* If null, simply return */\n");
            writer.write("\tif (param == NULL)\n");
            writer.write("\t\treturn;\n");
            writer.write("\n");
            
            writer.write("\t/* Reclaim memory resources of array elements, if it exists */\n");
            writer.write("\tif (param->m_Array && param->m_Size > 0)\n");
            writer.write("\t{\n");            
            writer.write("\t\tAxis_Delete_" +  attribs[0].getTypeName() 
                    + "((" + attribs[0].getTypeName() + " *)param->m_Array, param->m_Size);\n");
            writer.write("\t\tparam->m_Array = NULL;\n");            
            writer.write("\t}\n");                     
            writer.write("\n");
            
            writer.write("\t/* Reclaim array data type memory resources */\n");
            writer.write("\taxiscAxisDelete(param, XSDC_ARRAY);\n");
            
            // End function body            
            
            writer.write("}\n");
        }
        catch (IOException e)
        {
            throw new WrapperFault(e);
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.axis.wsdl.wsdl2ws.BasicFileWriter#getFilePath()
     */
    protected File getFilePath() throws WrapperFault
    {
        return this.getFilePath(false);
    }

    /* (non-Javadoc)
     * @see org.apache.axis.wsdl.wsdl2ws.BasicFileWriter#getFilePath(boolean)
     */
    protected File getFilePath(boolean useServiceName) throws WrapperFault
    {
        String targetOutputLocation = this.wscontext.getWrapperInfo().getTargetOutputLocation();
        if (targetOutputLocation.endsWith("/"))
            targetOutputLocation = targetOutputLocation.substring(0,targetOutputLocation.length() - 1);

        new File(targetOutputLocation).mkdirs();

        String fileName = targetOutputLocation + "/" + classname + CUtils.C_FILE_SUFFIX;

        if (useServiceName)
        {
            fileName = targetOutputLocation + "/"
                    + this.wscontext.getServiceInfo().getServicename()
                    + "_" + classname + CUtils.C_HEADER_SUFFIX;
        }

        return new File(fileName);
    }
}
