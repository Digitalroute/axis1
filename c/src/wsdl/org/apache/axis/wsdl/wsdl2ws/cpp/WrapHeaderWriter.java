/*
 *   Copyright 2003-2004 The Apache Software Foundation.
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
import org.apache.axis.wsdl.wsdl2ws.info.FaultInfo;
import org.apache.axis.wsdl.wsdl2ws.info.MethodInfo;
import org.apache.axis.wsdl.wsdl2ws.info.WebServiceContext;

public class WrapHeaderWriter extends HeaderFileWriter
{
    private ArrayList methods;
    public WrapHeaderWriter(WebServiceContext wscontext) throws WrapperFault
    {
        super(
            WrapperUtils.getClassNameFromFullyQualifiedName(
                wscontext.getSerInfo().getQualifiedServiceName()
                    + CUtils.WRAPPER_NAME_APPENDER));
        this.wscontext = wscontext;
        this.methods = wscontext.getSerInfo().getMethods();
    }

    protected void writeClassComment() throws WrapperFault
    {
        try
        {
            writer.write("/*\n");
            writer.write(
                " * This file was auto-generated by the Axis C++ Web Service Generator (WSDL2Ws)\n");
            writer.write(
                " * This file contains Web Service Wrapper declarations\n");
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
            writer.write("public:\n\t" + classname + "();\n");
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
            writer.write("public:\n\tvirtual ~" + classname + "();\n");
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
        try
        {
            writer.write(
                "public:/*implementation of WrapperClassHandler interface*/\n");
            writer.write("\tint AXISCALL invoke(void* pMsg);\n");
            writer.write("\tvoid AXISCALL onFault(void* pMsg);\n");
            writer.write("\tint AXISCALL init();\n");
            writer.write("\tint AXISCALL fini();\n");
            writer.write(
                "\tAXIS_BINDING_STYLE AXISCALL getBindingStyle(){return RPC_ENCODED;};\n");
            writer.write(
                "private:/*Methods corresponding to the web service methods*/\n");
            MethodInfo minfo;
            for (int i = 0; i < methods.size(); i++)
            {
                minfo = (MethodInfo) methods.get(i);
                writer.write(
                    "\tint " + minfo.getMethodname() + "(void* pMsg);");
                writer.write("\n");
            }

        }
        catch (IOException e)
        {
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
                    + CUtils.getWebServiceNameFromWrapperName(classname)
                    + ".h\"\n");
            writer.write("#include <axis/server/WrapperClassHandler.hpp>\n");
            writer.write("#include <axis/server/IMessageData.hpp>\n");
            writer.write("#include <axis/server/GDefine.hpp>\n");
            writer.write("#include <axis/server/AxisWrapperAPI.hpp>\n");
            writer.write("#include \"AxisServiceException.h\" \n");
            writer.write("AXIS_CPP_NAMESPACE_USE \n\n");
            //writeFaultHeaders();

        }
        catch (IOException e)
        {
            throw new WrapperFault(e);
        }
    }
    /* (non-Javadoc)
     * @see org.apache.axis.wsdl.wsdl2ws.cpp.HeaderFileWriter#writeMethods()
     */
    protected void writeFaultHeaders() throws WrapperFault
    {
        try
        {

            MethodInfo minfo;
            for (int i = 0; i < methods.size(); i++)
            {
                minfo = (MethodInfo) methods.get(i);
                Iterator fault = minfo.getFaultType().iterator();
                String faultInfoName = null;
                while (fault.hasNext())
                {
                    FaultInfo info = (FaultInfo) fault.next();
                    faultInfoName = info.getFaultInfo();
                    writer.write(
                        "#include \"Axis"
                            + faultInfoName.toString()
                            + "Exception.h\"\n");
                }
            }
            writer.write("\n");
        }
        catch (IOException e)
        {
            throw new WrapperFault(e);
        }
    }
    protected String getExtendsPart()
    {
        return " : public WrapperClassHandler";
    }
    protected void writeAttributes() throws WrapperFault
    {
        try
        {
            writer.write(
                "private:/* Actual web service object*/\n\t"
                    + CUtils.getWebServiceNameFromWrapperName(classname)
                    + " *pWs;\n");
        }
        catch (IOException e)
        {
            throw new WrapperFault(e);
        }
    }
    protected String getFileType()
    {
        return "ServerWrapper";
    }
}
