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
package org.apache.axis.wsdl.wsdl2ws.c.literal;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import org.apache.axis.wsdl.wsdl2ws.CUtils;
import org.apache.axis.wsdl.wsdl2ws.WrapperFault;
import org.apache.axis.wsdl.wsdl2ws.info.Type;
import org.apache.axis.wsdl.wsdl2ws.info.WebServiceContext;

public class ClientStubHeaderWriter
    extends org.apache.axis.wsdl.wsdl2ws.c.ClientStubHeaderWriter
{
    /**
     * @param wscontext
     * @throws WrapperFault
     */
    public ClientStubHeaderWriter(WebServiceContext wscontext)
        throws WrapperFault
    {
        super(wscontext);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.axis.wsdl.wsdl2ws.cpp.HeaderFileWriter#writePreprocssorStatements()
     */
    protected void writePreprocessorStatements() throws WrapperFault
    {
        try
        {
			writer.write("#include <axis/GDefine.h>\n");
			writer.write("#include <axis/AxisUserAPI.h>\n");
			writer.write("#include <axis/SoapEnvVersions.h>\n");
			writer.write("#include <axis/WSDDDefines.h>\n");
			writer.write("#include <axis/TypeMapping.h>\n");
			writer.write("#include <axis/client/Stub.h>\n");
			writer.write("#include <axis/client/Call.h>\n");

            Type atype;
            Iterator types = this.wscontext.getTypemap().getTypes().iterator();
            Vector typeSet = new Vector();
            while (types.hasNext())
            {
                atype = (Type) types.next();
                if (atype.getLanguageSpecificName().startsWith(">"))
                    continue;
                if (!atype.isArray())
                    typeSet.insertElementAt(atype.getLanguageSpecificName(), 0);
                else
                    typeSet.add(atype.getLanguageSpecificName());
            }
            Iterator itr = typeSet.iterator();
            while (itr.hasNext())
            {
                writer.write(
                    "#include \""
                        + itr.next().toString()
                        + CUtils.C_HEADER_SUFFIX
                        + "\"\n");
            }
            writer.write("\n");
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new WrapperFault(e);
        }
    }
}