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
 * @author Samisa Abeysinghe (sabeysinghe@virtusa.com)
 */

package org.apache.axis.wsdl.wsdl2ws.cpp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.axis.wsdl.wsdl2ws.CUtils;
import org.apache.axis.wsdl.wsdl2ws.WrapperFault;
import org.apache.axis.wsdl.wsdl2ws.BasicFileWriter;
import org.apache.axis.wsdl.wsdl2ws.WSDL2Ws;
import org.apache.axis.wsdl.wsdl2ws.info.WebServiceContext;

public abstract class HeaderFileWriter extends BasicFileWriter
{
    protected WebServiceContext wscontext;
    public HeaderFileWriter(String classname) throws WrapperFault
    {
        super(classname);
    }
    public void writeSource() throws WrapperFault
    {
        try
        {
            String filename = getFilePath().getName();

            this.writer =
                new BufferedWriter(
                    new FileWriter(
                        getFilePath(filename.startsWith("AxisClientException")),
                        false));

            writeClassComment();
            // if this headerfile not defined define it 
            this.writer.write(
                "#if !defined(__"
                    + classname.toUpperCase()
                    + "_"
                    + getFileType().toUpperCase()
                    + "_H__INCLUDED_)\n");
            this.writer.write(
                "#define __"
                    + classname.toUpperCase()
                    + "_"
                    + getFileType().toUpperCase()
                    + "_H__INCLUDED_\n\n");
            //includes
            writePreprocessorStatements();
            //class

            if ("AxisClientException".equals(classname))
            {
                this.writer.write(
                    "class "
                        + getServiceName()
                        + "_"
                        + classname
                        + getExtendsPart()
                        + "\n{\n");
            }
            else
            {
                this.writer.write(
                    "class " + classname + getExtendsPart() + "\n{\n");
            }
            writeAttributes();
            writeConstructors();
            writeDestructors();
            writeMethods();
            this.writer.write("};\n\n");
            this.writer.write(
                "#endif /* !defined(__"
                    + classname.toUpperCase()
                    + "_"
                    + getFileType().toUpperCase()
                    + "_H__INCLUDED_)*/\n");
            //cleanup
            writer.flush();
            writer.close();
            if (WSDL2Ws.verbose)
                System.out.println(
                    getFilePath().getAbsolutePath() + " created.....");

        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new WrapperFault(e);
        }

    }
    protected abstract String getExtendsPart(); //{return " ";}
    protected abstract String getFileType();

    protected File getFilePath() throws WrapperFault
    {
        return this.getFilePath(false);
    }
    protected File getFilePath(boolean useServiceName) throws WrapperFault
    {
        String targetOutputLocation =
            this.wscontext.getWrapInfo().getTargetOutputLocation();
        if (targetOutputLocation.endsWith("/"))
        {
            targetOutputLocation =
                targetOutputLocation.substring(
                    0,
                    targetOutputLocation.length() - 1);
        }
        new File(targetOutputLocation).mkdirs();

        String fileName =
            targetOutputLocation + "/" + classname + CUtils.CPP_HEADER_SUFFIX;

        if (useServiceName)
        {
            fileName =
                targetOutputLocation
                    + "/"
                    + this.getServiceName()
                    + "_"
                    + classname
                    + CUtils.CPP_HEADER_SUFFIX;
        }

        return new File(fileName);
    }
    protected String getServiceName() throws WrapperFault
    {
        return wscontext.getSerInfo().getServicename();
    }
}
