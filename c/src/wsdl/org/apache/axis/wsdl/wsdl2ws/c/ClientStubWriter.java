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
 * @author Kanchana Welagedara(kanchana@openource.lk)
 * @author Susantha Kumara(susantha@opensource.lk, skumara@virtusa.com)
 */

package org.apache.axis.wsdl.wsdl2ws.c;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axis.wsdl.wsdl2ws.CUtils;
import org.apache.axis.wsdl.wsdl2ws.WrapperFault;
import org.apache.axis.wsdl.wsdl2ws.WrapperUtils;
import org.apache.axis.wsdl.wsdl2ws.info.FaultInfo;
import org.apache.axis.wsdl.wsdl2ws.info.MethodInfo;
import org.apache.axis.wsdl.wsdl2ws.info.ParameterInfo;
import org.apache.axis.wsdl.wsdl2ws.info.Type;
import org.apache.axis.wsdl.wsdl2ws.info.WebServiceContext;

public class ClientStubWriter extends CFileWriter
{
    protected ArrayList methods;

    /**
     * @param wscontext
     * @throws WrapperFault
     */
    public ClientStubWriter(WebServiceContext wscontext) throws WrapperFault
    {
        super(wscontext.getServiceInfo().getServicename());
        this.wscontext = wscontext;
        this.methods = wscontext.getServiceInfo().getMethods();
    }

    /* (non-Javadoc)
     * @see org.apache.axis.wsdl.wsdl2ws.BasicFileWriter#writeClassComment()
     */
    protected void writeClassComment() throws WrapperFault
    {
        try
        {
            writer.write("/*\n");
            writer.write(" * This file was auto-generated by the Axis C++ Web Service Generator (WSDL2Ws)\n");
            writer.write(" * This file contains Client Stub implementation for remote web service.\n");
            writer.write(" */\n\n");
        }
        catch (IOException e)
        {
            throw new WrapperFault(e);
        }
    }
    /* 
     * @see org.apache.axis.wsdl.wsdl2ws.BasicFileWriter#writeMethods()
     * Used by literal code too!
     */
    protected void writeMethods() throws WrapperFault
    {
        try
          {            
            // get_xxx_stub() routine
            CUtils.printMethodComment(writer, "Function to get new object representing a Web service stub.");
            
            writer.write("AXISCHANDLE get_" + classname + "_stub(const char* pchEndPointUri)\n{\n");
            writer.write("\tif(pchEndPointUri)\n");
            writer.write("\t\treturn axiscStubCreate(pchEndPointUri, AXISC_APTHTTP1_1);\n");
            writer.write("\telse\n");
            writer.write("\t\treturn axiscStubCreate(\"" 
                    + wscontext.getServiceInfo().getTargetEndpointURI() 
                    + "\", AXISC_APTHTTP1_1);\n");
            writer.write("}\n\n");
            
            // destroy_xxxx_stub()
            CUtils.printMethodComment(writer, "Function to destroy an object representing a Web service stub.");
            
            writer.write("void destroy_" + classname + "_stub(AXISCHANDLE p)\n{\n");
            writer.write("\taxiscStubDestroy(p);\n}\n\n");

            // get_xxxx_Status() routine
            CUtils.printMethodComment(writer, "Function to get the status of last Web service method invocation.");
            
            writer.write("int get_" + classname + "_Status(AXISCHANDLE stub)\n{\n");
            writer.write("\tAXISCHANDLE call = axiscStubGetCall(stub);\n");
            writer.write("\treturn axiscCallGetStatus(call);\n");
            writer.write("}\n\n");

            CUtils.printMethodComment(writer, "Function to set a Web service stub's exception handler.");
            writer.write("void set_" + classname 
                    + "_ExceptionHandler(AXISCHANDLE stub, AXIS_EXCEPTION_HANDLER_FUNCT fp)\n{\n");          
            writer.write("\taxiscStubSetCExceptionHandler(stub, (void *)fp);\n");          
            writer.write("}\n");
            
            MethodInfo minfo;
            for (int i = 0; i < methods.size(); i++)
            {
                  minfo = (MethodInfo) methods.get(i);
                  this.writeMethodInWrapper(minfo);
            }
          }
        catch (IOException e)
          {
            throw new WrapperFault(e);
          }
    }

    /* (non-Javadoc)
     * @see org.apache.axis.wsdl.wsdl2ws.BasicFileWriter#writePreprocessorStatements()
     */
    protected void writePreprocessorStatements() throws WrapperFault
    {
        try
        {
            //writer.write("#include <stdlib.h>\n");
            writer.write("#include <stdio.h>\n");
            writer.write("#include <string.h>\n");
            writer.write("\n");
            
            writer.write("#include \"" + classname + CUtils.C_HEADER_SUFFIX + "\"\n");
            writer.write("\n");
            writer.write("#include <axis/client/Stub.h>\n");
            writer.write("#include <axis/client/Call.h>\n");
            writer.write("#include <axis/IWrapperSoapSerializer.h>\n");
            writer.write("#include <axis/IWrapperSoapDeSerializer.h>\n");
            
            writer.write("\n");
        }
        catch (IOException e)
        {
            throw new WrapperFault(e);
        }
    }

    /**
     * This method genarate methods that wraps the each method of the service
     * @param methodName
     * @param params
     * @param outparam
     * @throws IOException
     */

    public void writeMethodInWrapper(MethodInfo minfo)  throws WrapperFault, IOException
    {

        boolean isAllTreatedAsOutParams = false;
        ParameterInfo returntype = null;
        int noOfOutParams = minfo.getOutputParameterTypes().size();
        if (0 == noOfOutParams)
            returntype = null;
        else if (1 == noOfOutParams)
            returntype = (ParameterInfo) minfo.getOutputParameterTypes().iterator().next();
        else
            isAllTreatedAsOutParams = true;
        
        Collection params = minfo.getInputParameterTypes();

        String methodName = minfo.getMethodname();
        Type retType = null;
        boolean returntypeissimple = false;
        boolean returntypeisarray = false;
        String outparamTypeName = null;
        if (returntype != null)
        {
            outparamTypeName = 
                WrapperUtils.getClassNameFromParamInfoConsideringArrays(returntype, wscontext);
            retType = wscontext.getTypemap().getType(returntype.getSchemaName());
            if (retType != null)
            {
                returntypeisarray = retType.isArray();

                if (CUtils.isSimpleType(retType.getLanguageSpecificName()))
                    returntypeissimple = true;
            }
        }

        CUtils.printMethodComment(writer, "This function wraps the service method " + methodName + ".");
        
        //method signature
        String paramTypeName;
        boolean typeisarray = false;
        boolean typeissimple = false;
        Type type;
        if (returntype == null)
            writer.write("void");
        else
        {
            if (returntypeissimple
                    && (!(returntype.isNillable() || returntype.isOptional()) 
                            || CUtils.isPointerType(outparamTypeName)))
                writer.write (outparamTypeName);
            else if (outparamTypeName.lastIndexOf ("*") > 0)
                writer.write (outparamTypeName);
            else
                writer.write (outparamTypeName + "*");

        }
        
        writer.write(" " + methodName + "(AXISCHANDLE stub");
        ArrayList paramsB = (ArrayList) params;
        for (int i = 0; i < paramsB.size(); i++)
        {
            writer.write(", ");
            
            paramTypeName = 
                WrapperUtils.getClassNameFromParamInfoConsideringArrays((ParameterInfo) paramsB.get(i), wscontext);
            
            typeissimple = CUtils.isSimpleType(paramTypeName);
            if (typeissimple
                    && (!(((ParameterInfo) paramsB.get (0)).isNillable () 
                            || ((ParameterInfo) paramsB.get (0)).isOptional())
                    || CUtils.isPointerType(paramTypeName)))
                writer.write (paramTypeName + " Value" + i);
            else if (paramTypeName.lastIndexOf ("*") > 0)
                writer.write (paramTypeName + " Value" + i);
            else
                writer.write (paramTypeName + "* Value" + i);
        }
        
        // Multiples parameters so fill the methods prototype
        ArrayList paramsC = (ArrayList) minfo.getOutputParameterTypes();
        if (isAllTreatedAsOutParams)
        {
            for (int i = 0; i < paramsC.size(); i++)
            {
                type = wscontext.getTypemap().getType(
                        ((ParameterInfo) paramsC.get(i)).getSchemaName());
                writer.write(", AXISC_OUT_PARAM  "
                    + WrapperUtils.getClassNameFromParamInfoConsideringArrays(
                                (ParameterInfo) paramsC.get(i),wscontext) + " *OutValue" + i);
            }
        }
        
        writer.write(")\n{\n");
        
        writer.write("\tAXISCHANDLE call = axiscStubGetCall(stub);\n");
        
        if (returntype != null)
        {
            writer.write("\t");
            if (returntypeisarray)
            {
                QName qname = null;
                if (WrapperUtils.getArrayType (retType) != null)
                    qname = WrapperUtils.getArrayType (retType).getName ();
                else
                    qname = retType.getName ();
                if (CUtils.isSimpleType (qname))               
                    writer.write(outparamTypeName + " *RetArray = (" + outparamTypeName + " *)axiscAxisNew(XSDC_ARRAY, 0);\n");
                else
                    writer.write(outparamTypeName + " *RetArray = (" + outparamTypeName + " *)Axis_Create_" + outparamTypeName + "(0);\n");
            }
            else if (!returntypeissimple)
                writer.write(outparamTypeName + "\tpReturn = NULL;\n");
            else if (returntype.isNillable() || returntype.isOptional()
                        || CUtils.isPointerType(outparamTypeName))
            {
                writer.write(outparamTypeName);
                if (!CUtils.isPointerType(outparamTypeName))
                    writer.write(" *");

                writer.write("\tRet = NULL;\n");
            }
            else
            {
                String initValue = CUtils.getInitValue(outparamTypeName);
                if (initValue != null)
                    writer.write(outparamTypeName + " Ret = " + initValue + ";\n");
                else if (outparamTypeName.equals("xsdc__dateTime")
                        || outparamTypeName.equals("xsdc__date")
                        || outparamTypeName.equals("xsdc__time"))
                {
                    writer.write(outparamTypeName + " Ret;\n");
                    writer.write("\tmemset(&Ret,0,sizeof(" + outparamTypeName + "));\n");
                }
                else
                    writer.write(outparamTypeName + " Ret;\n");
            }
        }

        //writer.write("\tchar* cFaultcode;\n");
        //writer.write("\tchar* cFaultstring;\n");
        //writer.write("\tchar* cFaultactor;\n");
        writer.write("\tconst char *\tpcCmplxFaultName = NULL;\n\n");

        writer.write("\n\tif( AXISC_SUCCESS != axiscCallInitialize(call, CPP_RPC_PROVIDER"
                        + "))\n\t\treturn ");

        if (returntype != null)
        {
            if (returntypeisarray)
                writer.write("RetArray;\n");
            else if (CUtils.isPointerType(outparamTypeName) || 
                       (returntypeissimple && !(returntype.isNillable() || returntype.isOptional())))
                writer.write("Ret;\n");
            else if (returntypeissimple)
                writer.write("*Ret;\n");
            else
                writer.write("pReturn;\n");
        }
        else
            writer.write(";\n");

        writer.write("\n");

        writer.write("\tif( NULL == axiscCallGetTransportProperty(call, \"SOAPAction\", 0))\n");
        writer.write("\t\taxiscCallSetTransportProperty(call, AXISC_SOAPACTION_HEADER,\""
                + minfo.getSoapAction() + "\");\n");

        writer.write("\taxiscCallSetSOAPVersion(call, SOAP_VER_1_1);\n");
        //TODO check which version is it really.
        writer.write("\taxiscCallSetOperation(call, \"" + minfo.getMethodname()
                + "\", \"" + wscontext.getWrapperInfo().getTargetNameSpaceOfWSDL()
                + "\");\n");
        writer.write ("\taxiscStubApplyUserPreferences(stub);\n");        
        
        for (int i = 0; i < paramsB.size(); i++)
        {
            type = wscontext.getTypemap().getType( ((ParameterInfo) paramsB.get(i)).getSchemaName());
            
            if (type != null)
            {
                paramTypeName = type.getLanguageSpecificName();
                typeisarray = type.isArray();
            }
            else
            {
                paramTypeName = ((ParameterInfo) paramsB.get(i)).getLangName();
                typeisarray = false;
            }
            
            typeissimple = CUtils.isSimpleType(paramTypeName);
            
            if (typeisarray)
            {
                //arrays
                QName qname = WrapperUtils.getArrayType(type).getName();
                String containedType = null;
                if (CUtils.isSimpleType(qname))
                {
                    containedType = CUtils.getclass4qname(qname);
                    writer.write("\taxiscCallAddBasicArrayParameter(call, ");
                    writer.write("(Axisc_Array *)Value" + i + ", "
                            + CUtils.getXSDTypeForBasicType(containedType)
                            + ", \""
                            + ((ParameterInfo) paramsB.get(i)).getParamNameAsSOAPString()
                            + "\"");
                }
                else
                {
                    containedType = qname.getLocalPart();
                    writer.write("\taxiscCallAddCmplxArrayParameter(call, ");
                    writer.write("(Axisc_Array *)Value" + i
                            + ",(void *) Axis_Serialize_" + containedType
                            + ",(void *) Axis_Delete_" + containedType
                            + ",\"" + ((ParameterInfo) paramsB.get(i)).getParamNameAsSOAPString() + "\""
                            + ",Axis_URI_" + containedType);
                }
            }
            else if (typeissimple)
            {

                //for simple types
                if (((ParameterInfo) paramsB.get(i)).isNillable()
                        || CUtils.isPointerType(paramTypeName))
                {
                    writer.write("\taxiscCallAddParameter(call, ");
                    writer.write("(void *) Value"
                            + i
                            + ", \""
                            + ((ParameterInfo) paramsB.get(i))
                                    .getParamNameAsSOAPString() + "\", "
                            + CUtils.getXSDTypeForBasicType(paramTypeName));
                }
                else
                {
                    writer.write("\taxiscCallAddParameter(call, ");
                    writer.write("(void *) &Value"
                            + i
                            + ", \""
                            + ((ParameterInfo) paramsB.get(i))
                                    .getParamNameAsSOAPString() + "\", "
                            + CUtils.getXSDTypeForBasicType(paramTypeName));
                }
            }
            else
            {
                //for complex types
                writer.write("\taxiscCallAddCmplxParameter(call, ");
                writer.write("Value" + i + ",(void *) Axis_Serialize_"
                        + paramTypeName + ",(void *) Axis_Delete_"
                        + paramTypeName + ",\""
                        + ((ParameterInfo) paramsB.get(i)).getParamNameAsSOAPString()
                        + "\",Axis_URI_" + paramTypeName);
            }
            writer.write(");\n");
        }
        writer.write("\n\tif( AXISC_SUCCESS == axiscCallSendAndReceive(call))\n\t{\n");
        writer.write("\t\tif( AXISC_SUCCESS == axiscCallCheckMessage(call, \""
                + minfo.getOutputMessage().getLocalPart() + "\",\""
                + wscontext.getWrapperInfo().getTargetNameSpaceOfWSDL()
                + "\"))\n\t\t{\n");
      
        String paramTagName = "";
        
        if( returntype != null)
            paramTagName = returntype.getParamNameAsSOAPString();
        
        if (isAllTreatedAsOutParams)
        {
            String currentParamName;
            String currentParaType;
            for (int i = 0; i < paramsC.size(); i++)
            {
                ParameterInfo currentType = (ParameterInfo) paramsC.get(i);
                type = wscontext.getTypemap().getType(
                        currentType.getSchemaName());
                if (type != null)
                {
                    currentParaType = type.getLanguageSpecificName();
                    typeisarray = type.isArray();
                }
                else
                {
                    currentParaType = ((ParameterInfo) paramsC.get(i))
                            .getLangName();
                    typeisarray = false;
                }
                typeissimple = CUtils.isSimpleType(currentParaType);

                currentParamName = "*OutValue" + i;
                    
                // Some code need to be merged as we have some duplicated in
                // coding here.
                if (typeisarray)
                {
                    QName qname = WrapperUtils.getArrayType(type).getName();
                    String containedType = null;
                    if (CUtils.isSimpleType(qname))
                    {
                        containedType = CUtils.getclass4qname(qname);
                        writer.write("\n\t\tAxisc_Array * pReturn" + i 
                                + " = axiscCallGetBasicArray(call, " 
                                + CUtils.getXSDTypeForBasicType (containedType) 
                                + ",\"" + currentType.getParamNameAsSOAPString() + "\",0);\n\n");
                        writer.write("\t\tif( OutValue" + i + " != NULL)\n");
                        writer.write("\t\t{\n");
                        writer.write("\t\t\tif( *OutValue" + i + " != NULL)\n");
                        writer.write("\t\t\t\taxiscAxisDelete(*OutValue" + i + ", XSDC_ARRAY);\n");                        
                        writer.write("\t\t\t\t*OutValue" + i + " = pReturn;\n");
                        writer.write("\t\t}\n\n");
                        writer.write("\t\telse\n");                        
                        writer.write("\t\t\taxiscAxisDelete(pReturn" + i + ", XSDC_ARRAY);\n");
                    }
                    else
                    {
                        containedType = qname.getLocalPart();
                        writer.write("\n\t\tif( OutValue" + i + " != NULL)\n" );
                        writer.write("\t\t{\n");
                        writer.write("\t\t\tif( " + currentParamName + " != NULL)\n");
                        writer.write("\t\t\t\tAxis_Delete_" + containedType + "_Array(" + currentParamName + ", 0);\n");
                        writer.write("\t\t\t" + currentParamName + " = (" + containedType + " *)Axis_Create_" + containedType + "_Array(0);\n");

                        writer.write("\t\t\taxiscCallGetCmplxArray(call, " + currentParamName 
                                + ",(void *) Axis_DeSerialize_" + containedType
                                + ",(void *) Axis_Create_" + containedType
                                + ",(void *) Axis_Delete_" + containedType
                                + ",\"" +currentType.getElementNameAsSOAPString() 
                                + "\",Axis_URI_" + containedType + ");\n");
                        writer.write("\t\t}\n");
                        writer.write("\t\telse\n");
                        writer.write("\t\t{\n");
                        writer.write("\t\t\t// Unable to return value, but will deserialize to ensure subsequent elements can be correctly processed.\n");
                        writer.write("\t\t\t" + containedType + "_Array * pTemp" + i + " = (" + containedType + " *)Axis_Create_" + containedType + "_Array(0);\n");
                        writer.write("\t\t\taxiscCallGetCmplxArray(call, pTemp" + i 
                                + ",(void *) Axis_DeSerialize_" + containedType
                                + ",(void *) Axis_Create_" + containedType
                                + ",(void *) Axis_Delete_" + containedType
                                + ",\"" + currentType.getElementNameAsSOAPString() 
                                + "\",Axis_URI_" + containedType + ");\n");
                        writer.write("\t\t\t\tAxis_Delete_" + containedType + "_Array(pTemp" + i + ", 0);\n");
                        writer.write("\t\t}\n");
                    }
                }
                else
                {
                    if (typeissimple)
                    {
                        if( i > 0)
                            writer.write( "\n");
                        
                        if (CUtils.isPointerType(currentParaType))
                        {
                            String xsdType =  WrapperUtils.getClassNameFromParamInfoConsideringArrays ((ParameterInfo) paramsC.get (i), wscontext);
                            
                            if( !CUtils.isPointerType(xsdType))
                                xsdType += " *";
                            
                            writer.write( "\n");
                            writer.write( "\t\t\tif( pReturn" + i + " != NULL && OutValue" + i + " != NULL)\n");
                            writer.write( "\t\t\t{\n");
                            writer.write( "\t\t\t\tif( *OutValue" + i + " != NULL)\n");
                            writer.write( "\t\t\t\t{\n");
                            writer.write( "\t\t\t\t\tint\tiStringSize" + i + " = strlen( (char *) *OutValue" + i + ");\n");
                            writer.write( "\t\t\t\t\tint\tiStrLenValue" + i + " = strlen( pReturn" + i + ");\n");
                            writer.write( "\n");
                            writer.write( "\t\t\t\t\tif( iStrLenValue" + i + " > iStringSize" + i + ")\n");
                            writer.write( "\t\t\t\t\t{\n");
                            writer.write( "\t\t\t\t\t\t*OutValue" + i + " =(" + xsdType + ") axiscAxisNew(XSDC_STRING, iStrLenValue" + i + " + 1);\n");
                            writer.write( "\t\t\t\t\t\tstrcpy( (char *) *OutValue" + i + ", pReturn" + i + ");\n");
                            writer.write( "\t\t\t\t\t}\n");
                            writer.write( "\t\t\t\t\telse\n");
                            writer.write( "\t\t\t\t\t\tstrcpy( (char *) *OutValue" + i + ", pReturn" + i + ");\n");
                            writer.write( "\t\t\t\t}\n");
                            writer.write( "\t\t\t\telse\n");
                            writer.write( "\t\t\t\t{\n");
                            writer.write( "\t\t\t\t\t*OutValue" + i + " = (" + xsdType + ") axiscAxisNew(XSDC_STRING,strlen( pReturn" + i + ") + 1);\n");
                            writer.write( "\t\t\t\t\tstrcpy( (char *) *OutValue" + i + ", pReturn" + i + ");\n");
                            writer.write( "\t\t\t\t}\n");
                            writer.write( "\t\t\t}\n");
                            writer.write( "\n");
                            writer.write( "\t\t\taxiscAxisDelete( (void *) pReturn" + i + ", " + CUtils.getXSDTypeForBasicType( currentParaType) + ");\n");
                        }
                        else 
                        {
                            writer.write( "\t\t" + currentParaType + " * pReturn" + i + " = axiscCall" + CUtils.getParameterGetValueMethodName( currentParaType, false) + "(call, \"" + currentType.getParamNameAsSOAPString() + "\", 0);\n");
                            writer.write( "\n");
                            writer.write( "\t\tif( pReturn" + i + " != NULL && OutValue" + i + " != NULL)\n");
                            writer.write( "\t\t{\n");
                            writer.write( "\t\t\t// OutValue" + i + " is not nillable.\n");
                            writer.write( "\t\t\t*OutValue" + i + " = *pReturn" + i + ";\n");
                            writer.write( "\t\t}\n");
                            writer.write( "\n");
                            writer.write( "\t\taxiscAxisDelete(pReturn" + i + ", " + CUtils.getXSDTypeForBasicType( currentParaType) + ");\n");
                        }
                    }
                    else
                    {
                        writer.write("\t\t\t"
                                        + currentParamName
                                        + " = ("
                                        + currentParaType
                                        + " *) axiscCallGetCmplxObject(call, (void *) Axis_DeSerialize_" + currentParaType
                                        + ",(void *) Axis_Create_" + currentParaType
                                        + ",(void *) Axis_Delete_" + currentParaType
                                        + ",\"" + currentType.getParamNameAsSOAPString()
                                        + "\",0);\n");
                    }
                }
            }
            writer.write("\t\t}\n");
            writer.write("\t}\n\n");
            writer.write("\taxiscCallUnInitialize(call);\n");
        }
        else if (returntype == null)
        {
            writer.write("\t\t\t/*not successful*/\n\t\t\t}\n");
            writer.write("\t}\n\n");
            writer.write("\taxiscCallUnInitialize(call);\n");
        }
        else if (returntypeisarray)
        {
            QName qname = WrapperUtils.getArrayType(retType).getName();
            String containedType = null;
            if (CUtils.isSimpleType(qname))
            {
                containedType = CUtils.getclass4qname(qname);
                writer.write("\t\t\tRetArray = (" + containedType + "_Array *)axiscCallGetBasicArray(call, "
                        + CUtils.getXSDTypeForBasicType(containedType) + ",\""
                        + paramTagName + "\",0);\n");
            }
            else
            {
                containedType = qname.getLocalPart();
                writer.write("\t\t\tRetArray = (" + containedType 
                        + "_Array *) axiscCallGetCmplxArray(call, (Axisc_Array *)RetArray,(void *) Axis_DeSerialize_"
                        + containedType);
                //damitha
                writer.write(",(void *) Axis_Create_" + containedType
                        + ",(void *) Axis_Delete_" + containedType
                        + ",\"" + paramTagName
                        + "\",Axis_URI_" + containedType + ");\n");            
            }
            writer.write("\t\t}\n\t}\n\n");
            writer.write("\taxiscCallUnInitialize(call);\n\n");

            writer.write("\treturn RetArray;\n");
        }
        else if (returntypeissimple)
        {
            writer.write("\t\t\t" + outparamTypeName);
            if (!CUtils.isPointerType(outparamTypeName))
                writer.write(" *");
            
            writer.write(" pReturn = axiscCall"
                    + CUtils.getParameterGetValueMethodName(outparamTypeName,
                            false) + "(call, \"" + paramTagName + "\", 0);\n\n");
            writer.write("\t\t\tif( pReturn)\n");
            writer.write("\t\t\t{\n");
            if (CUtils.isPointerType(outparamTypeName))
            {
                writer.write("\t\t\t\tRet = pReturn;\n");
            }
            else if (returntype.isOptional() || returntype.isNillable())
                writer.write("\t\t\t\tRet = pReturn;\n");
            else
            {
                writer.write("\t\t\t\tRet = *pReturn;\n");
                writer.write("\t\t\t\taxiscAxisDelete(pReturn, " + CUtils.getXSDTypeForBasicType(outparamTypeName) + ");\n");                
            }

            writer.write("\t\t\t}\n");
            writer.write("\t\t}\n");
            writer.write("\t}\n\n");
            writer.write("\taxiscCallUnInitialize(call);\n");
            writer.write("\treturn Ret;\n");
        }
        else
        {
            outparamTypeName = returntype.getLangName();
            //need to have complex type name without *
            writer.write("\t\t\tpReturn = (" + outparamTypeName
                    + " *) axiscCallGetCmplxObject(call, (void *) Axis_DeSerialize_"
                    + outparamTypeName + ",(void *) Axis_Create_"
                    + outparamTypeName + ",(void *) Axis_Delete_"
                    + outparamTypeName + ",\"" + paramTagName
                    + "\",0);\n\t\t\t}\n");
            writer.write("\t}\n\n");
            writer.write("\taxiscCallUnInitialize(call);\n\n");
            writer.write("\treturn pReturn;\n");

        }

        writer.write("}\n");        
    }


    /*
     * Used by literal code too!
     */
    protected void writeFaultHandlingCode(MethodInfo minfo) throws WrapperFault, IOException
    {  
        writer.write ("\taxiscCallSetSoapFaultNamespace(call, \"" 
                + wscontext.getServiceInfo().getTargetEndpointURI () + "\");\n");
        
        //to get fault info             
        Iterator paramsFault = minfo.getFaultType ().iterator ();
        String faultInfoName = null;
        String langName = null;
        
        while (paramsFault.hasNext ())
        {
            FaultInfo info = (FaultInfo) paramsFault.next ();
            faultInfoName = info.getFaultInfo ();

            // FJP - D0004 > Looking through the list of attributes for the 'error' part of
            //               the fault message.  If found, update the faultInfoName with the
            //               'localname' of the qname of the attribute.                         
            Iterator infoArrayListIterator = info.getParams ().iterator ();
            boolean found = false;

            while (infoArrayListIterator.hasNext () && !found)
            {
                ParameterInfo paramInfo = (ParameterInfo) infoArrayListIterator.next ();
        
                if (paramInfo != null)
                    if ("error".equals (paramInfo.getParamName ()))
                    {
                        faultInfoName = paramInfo.getElementName ().getLocalPart ();
                        found = true;
                    }
            }

            ArrayList paramInfo = info.getParams ();
            for (int i = 0; i < paramInfo.size (); i++)
            {
                ParameterInfo par = (ParameterInfo) paramInfo.get (i);
                langName = par.getLangName ();

                writer.write ("\taxiscCallAddSoapFaultToList(call, \"" 
                        + faultInfoName + "\", "
                        + "(void*) Axis_Create_" + langName + ", "
                        + "(void*) Axis_Delete_" + langName + ", "                    
                        + "(void*) Axis_DeSerialize_" + langName + ");\n");
            }
        }
    }    
    
    /* (non-Javadoc)
     * @see org.apache.axis.wsdl.wsdl2ws.cpp.CPPClassWriter#writeGlobalCodes()
     * Used by literal code too!
     */
    protected void writeGlobalCodes() throws WrapperFault
    {
    }
}
