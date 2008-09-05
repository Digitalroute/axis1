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

/*
 * Created on Jun 3, 2004
 */
package org.apache.axis.wsdl.wsdl2ws.cpp;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.axis.wsdl.wsdl2ws.BasicFileWriter;
import org.apache.axis.wsdl.wsdl2ws.CUtils;
import org.apache.axis.wsdl.wsdl2ws.WSDL2Ws;
import org.apache.axis.wsdl.wsdl2ws.WrapperFault;
import org.apache.axis.wsdl.wsdl2ws.info.WebServiceContext;

/**
 * @author nithya
 * @author Samisa Abeysinghe (sabeysinghe@virtusa.com)
 */

public class ExceptionWriter extends BasicFileWriter
{
    private String faultInfoName;

    public ExceptionWriter(WebServiceContext wscontext, String faultInfoName)
        throws WrapperFault
    {
        super(wscontext.getServiceInfo().getServicename(), CUtils.getImplFileExtension());
        this.wscontext = wscontext;
        this.faultInfoName = "Axis" + faultInfoName + "Exception";
    }

    /* (non-Javadoc)
     * @see org.apache.axis.wsdl.wsdl2ws.BasicFileWriter#getFilePath(boolean)
     */
    protected File getFilePath(boolean useServiceName) throws WrapperFault
    {
        String targetOutputLocation = wscontext.getWrapperInfo().getTargetOutputLocation();
        new File(targetOutputLocation).mkdirs();
        
        String serviceName = "";
        if (useServiceName)
            serviceName = wscontext.getServiceInfo().getServicename() + "_";
        
        String fileName = targetOutputLocation + "/" + serviceName + faultInfoName + c_fileExtension;
        
        wscontext.addGeneratedFile(fileName);
        return new File(fileName);
    }

    private String getServiceName() throws WrapperFault
    {
        return wscontext.getServiceInfo().getServicename();
    }

    /* (non-Javadoc)
     * @see org.apache.axis.wsdl.wsdl2ws.BasicFileWriter#writePreprocessorStatements()
     */
    protected void writePreprocessorStatements() throws WrapperFault
    {
        try
        {
            if ("AxisClientException".equals(faultInfoName))
            {
                c_writer.write(
                    "#include \""
                        + getServiceName()
                        + "_"
                        + faultInfoName
                        + CUtils.getHeaderFileExtension()
                        + "\"\n\n");
            }
            else
            {
                c_writer.write(
                    "#include \""
                        + faultInfoName
                        + CUtils.getHeaderFileExtension()
                        + "\"\n\n");
            }
            c_writer.write("#include <axis/AxisWrapperAPI.hpp>\n\n");
        }
        catch (IOException e)
        {
            throw new WrapperFault(e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.axis.wsdl.wsdl2ws.BasicFileWriter#writeClassComment()
     */
    protected void writeClassComment() throws WrapperFault
    {
        try
        {
            c_writer.write("/*\n");
            c_writer.write(
                " * This file was auto-generated by the Axis C++ Web Service "
                    + "Generator (WSDL2Ws)\n");
            c_writer.write(
                " * This file contains implementations of the "
                    + getServiceName()
                    + " Exception "
                    + "class of the web service.\n");
            c_writer.write(" */\n\n");

        }
        catch (IOException e)
        {
            throw new WrapperFault(e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.axis.wsdl.wsdl2ws.BasicFileWriter#writeConstructors()
     */
    protected void writeConstructors() throws WrapperFault
    {
        try
        {
            String faultName = faultInfoName;

            if ("AxisClientException".equals(faultInfoName))
            {
                faultName = getServiceName() + "_" + faultInfoName;
            }

            c_writer.write(
                faultName + "::\n" + faultName + "(ISoapFault* pFault)\n");
            c_writer.write("{\n");
            c_writer.write(
                "\tm_iExceptionCode = AXISC_SERVICE_THROWN_EXCEPTION;\n");
            c_writer.write("\tm_pISoapFault = pFault;\n"); // Fred Preston
            c_writer.write("}\n\n");

            c_writer.write(
                faultName + "::\n" + faultName + "(const int iExceptionCode, const char* pcMessage):AxisException(iExceptionCode)\n");
            c_writer.write("{\n\n");
            c_writer.write("\tstring sMessage = \"\";\n");
            c_writer.write("\tif (pcMessage)\n\t{\n");
            c_writer.write("\t\tsMessage = string(pcMessage);\n");
            c_writer.write("\t\tgetMessageForExceptionCode(iExceptionCode);\n");
            c_writer.write("\t\tm_sMessageForExceptionCode = getMessageForExceptionCode(iExceptionCode) + \" \" + sMessage;\n");
            c_writer.write("\t\tsetMessage(m_sMessageForExceptionCode.c_str());\n");
			c_writer.write("\t}\n\n");
            c_writer.write("\telse\n");
            c_writer.write("\t\tsetMessage(getMessageForExceptionCode(iExceptionCode).c_str());\n");
            c_writer.write("}\n\n");
            c_writer.write(faultName + "::\n" + faultName + "(const " + faultName + "& e):AxisException(e)\n");
            c_writer.write("{}\n\n");
            
            
        }
        catch (IOException e)
        {
            throw new WrapperFault(e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.axis.wsdl.wsdl2ws.BasicFileWriter#writeDestructors()
     */
    protected void writeDestructors() throws WrapperFault
    {
        try
        {
            String faultName = faultInfoName;

            if ("AxisClientException".equals(faultInfoName))
            {
                faultName = getServiceName() + "_" + faultInfoName;
            }
            c_writer.write(
                faultName
                    + "::\n~"
                    + faultName
                    + "() throw () \n{}\n\n");
        }
        catch (IOException e)
        {
            throw new WrapperFault(e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.axis.wsdl.wsdl2ws.BasicFileWriter#writeMethods()
     */
    protected void writeMethods() throws WrapperFault
    {
        try
        {
            String faultName = faultInfoName;

            if ("AxisClientException".equals(faultInfoName))
            {
                faultName = getServiceName() + "_" + faultInfoName;
            }
            
            c_writer.write(
                "string "
                    + faultName
                    + "::\ngetMessageForExceptionCode (int iExceptionCode)\n");
            c_writer.write("{\n");
            c_writer.write("\tswitch(iExceptionCode)\n");
            c_writer.write("\t{\n");
            c_writer.write("\t\tcase AXISC_SERVICE_THROWN_EXCEPTION:\n");
            c_writer.write(
                "\t\t\tm_sMessageForExceptionCode = \"The "
                    + getServiceName()
                    + " service has thrown an exception. see details\";\n");
            c_writer.write("\t\t\tbreak;\n");
            c_writer.write("\t\tdefault:\n");
            c_writer.write(
                "\t\t\tm_sMessageForExceptionCode = \"Unknown Exception has occurred in the "
                    + getServiceName()
                    + " service\";\n");
            c_writer.write("\t}\n");
            c_writer.write("return m_sMessageForExceptionCode;\n");
            c_writer.write("}\n\n");

            c_writer.write("const ISoapFault* " + faultName + "::\ngetFault()");
            //Fred Preston
            c_writer.write("{\n");
            c_writer.write("\treturn m_pISoapFault;\n");
            c_writer.write("}\n\n");

        }
        catch (IOException e)
        {
            throw new WrapperFault(e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.axis.wsdl.wsdl2ws.SourceWriter#writeSource()
     */
    public void writeSource() throws WrapperFault
    {
        try
        {
            String filename = getFilePath(false).getName();

            c_writer =
                new BufferedWriter(
                    new FileWriter(
                        getFilePath(filename.startsWith("AxisClientException")),
                        false));
            writeClassComment();
            writePreprocessorStatements();
            writeGlobalCodes();
            writeAttributes();
            writeConstructors();
            writeDestructors();
            writeMethods();
            c_writer.flush();
            c_writer.close();
            if (WSDL2Ws.c_veryVerbose)
                System.out.println(
                    getFilePath(false).getAbsolutePath() + " created.....");
        }
        catch (IOException e)
        {
            throw new WrapperFault(e);
        }
    }

    /**
     * @throws WrapperFault
     */
    protected void writeGlobalCodes() throws WrapperFault
    {}
}
