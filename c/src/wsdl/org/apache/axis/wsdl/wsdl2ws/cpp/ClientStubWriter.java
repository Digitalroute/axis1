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
 * @author Susantha Kumara(susantha@opensource.lk, skumara@virtusa.com)
 */

package org.apache.axis.wsdl.wsdl2ws.cpp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axis.wsdl.wsdl2ws.WrapperConstants;
import org.apache.axis.wsdl.wsdl2ws.WrapperFault;
import org.apache.axis.wsdl.wsdl2ws.WrapperUtils;
import org.apache.axis.wsdl.wsdl2ws.CUtils;
import org.apache.axis.wsdl.wsdl2ws.info.MethodInfo;
import org.apache.axis.wsdl.wsdl2ws.info.ParameterInfo;
import org.apache.axis.wsdl.wsdl2ws.info.Type;
import org.apache.axis.wsdl.wsdl2ws.info.FaultInfo; 
import org.apache.axis.wsdl.wsdl2ws.info.WebServiceContext;
	
public class ClientStubWriter extends CPPClassWriter{
	private WebServiceContext wscontext;
	private ArrayList methods;	
	public ClientStubWriter(WebServiceContext wscontext)throws WrapperFault{
		super(WrapperUtils.getClassNameFromFullyQualifiedName(wscontext.getSerInfo().getQualifiedServiceName()));
		this.wscontext = wscontext;
		this.methods = wscontext.getSerInfo().getMethods();
	}
	protected File getFilePath() throws WrapperFault {
		String targetOutputLocation = this.wscontext.getWrapInfo().getTargetOutputLocation();
		if(targetOutputLocation.endsWith("/"))
			targetOutputLocation = targetOutputLocation.substring(0, targetOutputLocation.length() - 1);
		new File(targetOutputLocation).mkdirs();
		String fileName = targetOutputLocation + "/" + classname + ".cpp";
		return new File(fileName);
	}
	protected void writeClassComment() throws WrapperFault {
			try{
				writer.write("/*\n");	
				writer.write(" * This is the Client Stub implementation file genarated by WSDL2Ws tool.\n");
				writer.write(" * "+classname+".cpp: implemtation for the "+classname+".\n");
				writer.write("* Default when no parameter passed. When thrown with no parameter \n");
				writer.write("more general "+classname+"  is assumed.\n");
				writer.write(" *\n");										
				writer.write(" */\n\n");
			}catch(IOException e){
				throw new WrapperFault(e);
			}
	}

	/* (non-Javadoc)
	 * @see org.apache.axis.wsdl.wsdl2ws.cpp.HeaderFileWriter#writeConstructors()
	 */
	protected void writeConstructors() throws WrapperFault {
		try{
		writer.write(classname+"::"+classname+"()\n{\n");
		writer.write("\tm_pCall = new Call();\n");
		//TODO get TransportURI from WrapInfo and check what the transport is and do the following line accordingly
		writer.write("\tm_pCall->setProtocol(APTHTTP);\n");
		writer.write("\tm_pCall->setEndpointURI(\""+wscontext.getWrapInfo().getTargetEndpointURI()+"\");\n");
		writer.write("}\n\n");
		}catch(IOException e){
			throw new WrapperFault(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.axis.wsdl.wsdl2ws.cpp.HeaderFileWriter#writeDistructors()
	 */
	protected void writeDistructors() throws WrapperFault {
		try{
		writer.write(classname+"::~"+classname+"()\n{\n\tdelete m_pCall;\n}\n\n");
		}catch(IOException e){
			throw new WrapperFault(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.axis.wsdl.wsdl2ws.cpp.HeaderFileWriter#writeMethods()
	 */
	protected void writeMethods() throws WrapperFault {
		try{
			writer.write("\n/*Methods corresponding to the web service methods*/\n");
			MethodInfo minfo;
			for (int i = 0; i < methods.size(); i++) {
				minfo = (MethodInfo)methods.get(i);
				this.writeMethodInWrapper(minfo);
				writer.write("\n");
			}
			writer.write("int "+classname+"::getStatus()\n{\n\tif ( m_pCall==NULL ) \n\t\treturn AXIS_SUCCESS; \n\telse \n\t\treturn m_pCall->getStatus();\n}\n\n");//damitha
			writer.write("int "+classname+"::getFaultDetail(char** ppcDetail)\n{\n\treturn m_pCall->getFaultDetail(ppcDetail);\n}\n\n");//damitha
		}catch(IOException e){
			throw new WrapperFault(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.axis.wsdl.wsdl2ws.cpp.HeaderFileWriter#writePreprocssorStatements()
	 */
	protected void writePreprocssorStatements() throws WrapperFault {
		try{
			writer.write("#include \""+classname+".h\"\n\n");
			writer.write("#include <axis/server/AxisWrapperAPI.h>\n\n");
            writer.write("using namespace std;\n\n ");
		
		}catch(IOException e){
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

	public void writeMethodInWrapper(MethodInfo minfo) throws WrapperFault,IOException {
		boolean isAllTreatedAsOutParams = false;
		ParameterInfo returntype = null;
		ParameterInfo returntype1 = null;
		int noOfOutParams = minfo.getOutputParameterTypes().size();
		if (0==noOfOutParams){
			returntype = null;
		}
		else if (1==noOfOutParams){
			returntype = (ParameterInfo)minfo.getOutputParameterTypes().iterator().next();
		}
		else{
			isAllTreatedAsOutParams = true;
		}
		Collection params = minfo.getInputParameterTypes();
	
		String methodName = minfo.getMethodname();
		Type retType = null;
		boolean returntypeissimple = false;
		boolean returntypeisarray = false;
		String outparamTypeName = null;
		if (returntype != null){
			outparamTypeName = WrapperUtils.getClassNameFromParamInfoConsideringArrays(returntype, wscontext);
			retType = wscontext.getTypemap().getType(returntype.getSchemaName());
				if (retType != null){
				returntypeisarray = retType.isArray();
				System.out.println(retType.getLanguageSpecificName()+"LanguageName  .................... ");
				if (CUtils.isSimpleType(retType.getLanguageSpecificName())){
					returntypeissimple = true;
				}
			}
		}
		writer.write("\n/*\n");
		writer.write(" * This method wrap the service method "+ methodName +"\n");
		writer.write(" */\n");
		//method signature
		String paramTypeName;
		boolean typeisarray = false;
		boolean typeissimple = false;
		Type type;
		if (returntype == null){
			writer.write("void");
		}else{
			writer.write(outparamTypeName);
			System.out.println("Output Parameter type Name :"+outparamTypeName);
		}
		writer.write(" "+classname+"::" + methodName + "(");
		ArrayList paramsB = (ArrayList)params;
		for (int i = 0; i < paramsB.size(); i++) {
			paramTypeName = WrapperUtils.getClassNameFromParamInfoConsideringArrays((ParameterInfo)paramsB.get(i), wscontext);
			if (i>0) writer.write(", ");
			writer.write(paramTypeName+" Value"+i);
		}
		// Multiples parameters so fill the methods prototype
		ArrayList paramsC = (ArrayList)minfo.getOutputParameterTypes();
		if ( isAllTreatedAsOutParams ) {
			String currentParaTypeName;
			for (int i = 0; i < paramsC.size(); i++) {
				type = wscontext.getTypemap().getType(((ParameterInfo)paramsC.get(i)).getSchemaName());
				writer.write(", AXIS_OUT_PARAM  "+WrapperUtils.getClassNameFromParamInfoConsideringArrays((ParameterInfo)paramsC.get(i),wscontext)+" *OutValue"+i);
			}
		}
		writer.write(")\n{\n");
		if (returntype != null){
			writer.write("\t");
			if(returntypeisarray){
				//for arrays
				writer.write(outparamTypeName+" RetArray = {NULL, 0};\n");
			}else if(!returntypeissimple){
				writer.write(outparamTypeName+" pReturn = NULL;\n");
				//for complex types
			}else{
				//for simple types
				writer.write(outparamTypeName+" Ret;\n");
				//TODO initialize return parameter appropriately.
			}
		}
		String channelSecurityType = (WrapperConstants.CHANNEL_SECURITY_SSL.equals(wscontext.getWrapInfo().getChannelSecurity()))?
										"SSL_CHANNEL" : "NORMAL_CHANNEL";
//		begin added NIthya
		//	  writer.write("int Ret;\n");
			  writer.write("\tchar* cFaultcode;\n");//damitha added \t
			  writer.write("\tchar* cFaultstring;\n");//damitha \t
			  writer.write("\tchar* cFaultactor;\n");//damitha \t
			  writer.write("\tchar* cFaultdetail;\n");//damitha \t
 //     end of Nithya								
		//	begin added by nithya
		  writer.write("\ttry\n\t{");//damitha \n
		//ended by nithya								
		writer.write("\n\t\tif (AXIS_SUCCESS != m_pCall->initialize(CPP_RPC_PROVIDER, "+channelSecurityType +")) \n\t\t\treturn ");//damitha
		if (returntype != null){
			writer.write((returntypeisarray?"RetArray":returntypeissimple?"Ret":"pReturn")+";\n");
		}
		else{
			writer.write(";\n");
		}
	
		writer.write("\t\tm_pCall->setTransportProperty(SOAPACTION_HEADER , \""+minfo.getSoapAction()+"\");\n");
		writer.write("\t\tm_pCall->setSOAPVersion(SOAP_VER_1_1);\n"); //TODO check which version is it really.
		writer.write("\t\tm_pCall->setOperation(\""+minfo.getMethodname()+"\", \""+wscontext.getWrapInfo().getTargetNameSpaceOfWSDL()+"\");\n");
		for (int i = 0; i < paramsB.size(); i++) {
			type = wscontext.getTypemap().getType(((ParameterInfo)paramsB.get(i)).getSchemaName());
			if (type != null){
				paramTypeName = type.getLanguageSpecificName();
				typeisarray = type.isArray();
			}
			else {
				paramTypeName = ((ParameterInfo)paramsB.get(i)).getLangName();
				typeisarray = false;
			}
			typeissimple = CUtils.isSimpleType(paramTypeName);
			if(typeisarray){
				//arrays
				QName qname = WrapperUtils.getArrayType(type).getName();
				String containedType = null;
				if (CUtils.isSimpleType(qname)){
					containedType = CUtils.getclass4qname(qname);
					writer.write("\tm_pCall->addBasicArrayParameter(");			
					writer.write("(Axis_Array*)(&Value"+i+"), "+CUtils.getXSDTypeForBasicType(containedType)+", \""+((ParameterInfo)paramsB.get(i)).getParamName()+"\"");					
				}
				else{
					containedType = qname.getLocalPart();
					writer.write("\tm_pCall->addCmplxArrayParameter(");			
					writer.write("(Axis_Array*)(&Value"+i+"), (void*)Axis_Serialize_"+containedType+", (void*)Axis_Delete_"+containedType+", (void*) Axis_GetSize_"+containedType+", \""+((ParameterInfo)paramsB.get(i)).getParamName()+"\", Axis_URI_"+containedType);
				}
			}else if(typeissimple){
				//for simple types	
				writer.write("\t\tm_pCall->addParameter(");			
				writer.write("(void*)&Value"+i+", \"" + ((ParameterInfo)paramsB.get(i)).getParamName()+"\", "+CUtils.getXSDTypeForBasicType(paramTypeName));
			}else{
				//for complex types 
				writer.write("\tm_pCall->addCmplxParameter(");			
				writer.write("Value"+i+", (void*)Axis_Serialize_"+paramTypeName+", (void*)Axis_Delete_"+paramTypeName+", \"" + ((ParameterInfo)paramsB.get(i)).getParamName()+"\", Axis_URI_"+paramTypeName);
			}
			writer.write(");\n");
		}
		writer.write("\t\tif (AXIS_SUCCESS == m_pCall->invoke())\n\t\t{\n");//damitha
		writer.write("\t\t\tif(AXIS_SUCCESS == m_pCall->checkMessage(\""+minfo.getOutputMessage().getLocalPart()+"\", \""+wscontext.getWrapInfo().getTargetNameSpaceOfWSDL()+"\"))\n\t\t\t{\n");//damitha
				
		if ( isAllTreatedAsOutParams) {
			String currentParamName;
			String currentParaType;
			for (int i = 0; i < paramsC.size(); i++) {
				ParameterInfo currentType = (ParameterInfo)paramsC.get(i);
				type = wscontext.getTypemap().getType(currentType.getSchemaName());
				if (type != null){
					currentParaType = type.getLanguageSpecificName();
					typeisarray = type.isArray();
				}
				else {
					currentParaType = ((ParameterInfo)paramsC.get(i)).getLangName();
					typeisarray = false;
				}
				typeissimple = CUtils.isSimpleType(currentParaType);
								
				currentParamName = "*OutValue"+i;
				// Some code need to be merged as we have some duplicated in coding here.
				if (typeisarray){
					QName qname = WrapperUtils.getArrayType(type).getName();
					String containedType = null;
					if (CUtils.isSimpleType(qname)){
						containedType = CUtils.getclass4qname(qname);
						writer.write("\t\t\t\t" + currentParamName + " = ("+currentParaType+"&)m_pCall->getBasicArray("+CUtils.getXSDTypeForBasicType(containedType)+", \""+currentType.getParamName()+"\", 0);\n");//damitha
					}
					else{
						containedType = qname.getLocalPart();
						writer.write("\t\t\t\t" + currentParamName + " = ("+currentParaType+"&)m_pCall->getCmplxArray((void*) Axis_DeSerialize_"+containedType);//damitha
						writer.write("\t\t\t\t, (void*) Axis_Create_"+containedType+", (void*) Axis_Delete_"+containedType+", (void*) Axis_GetSize_"+containedType+", \""+currentType.getParamName()+"\", Axis_URI_"+containedType+");\n");//damitha
					}
				}
				else if(typeissimple){
				   writer.write("\t\t\t\t" + currentParamName + " = m_pCall->"+ CUtils.getParameterGetValueMethodName(currentParaType, false)+"(\""+currentType.getParamName()+"\", 0);\n");//damitha
				}
				else{
				   writer.write("\t\t\t\t" + currentParamName + " = ("+currentParaType+"*)m_pCall->getCmplxObject((void*) Axis_DeSerialize_"+currentParaType+", (void*) Axis_Create_"+currentParaType+", (void*) Axis_Delete_"+currentParaType+",\""+currentType.getParamName()+"\", 0);\n"); //damitha
				}				
			}	
			writer.write("\t\t\t}\n");//damitha
			writer.write("\t\t}\n\t\tm_pCall->unInitialize();\n");//damitha
		}
		else if (returntype == null){
			writer.write("\t\t\t/*not successful*/\n\t\t}\n");//damitha
			writer.write("\t\t}\n\t\tm_pCall->unInitialize();\n");//damitha
		}
		else if (returntypeisarray){
			QName qname = WrapperUtils.getArrayType(retType).getName();
			String containedType = null;
			if (CUtils.isSimpleType(qname)){
				containedType = CUtils.getclass4qname(qname);
				writer.write("\t\t\t\tRetArray = ("+outparamTypeName+"&)m_pCall->getBasicArray("+CUtils.getXSDTypeForBasicType(containedType)+", \""+returntype.getParamName()+"\", 0);\n\t\t\t}\n");//damitha
			}
			else{
				containedType = qname.getLocalPart();
				writer.write("\t\t\t\tRetArray = ("+outparamTypeName+"&)m_pCall->getCmplxArray((void*) Axis_DeSerialize_"+containedType);//damitha
				writer.write(", (void*) Axis_Create_"+containedType+", (void*) Axis_Delete_"+containedType+", (void*) Axis_GetSize_"+containedType+", \""+returntype.getParamName()+"\", Axis_URI_"+containedType+");\n\t\t\t}\n");//damitha
			}
			writer.write("\t\t}\n\t\tm_pCall->unInitialize();\n");//damitha
			writer.write("\t\treturn RetArray;\n");//damitha
		}
		else if(returntypeissimple){
			writer.write("\t\t\t\tRet = m_pCall->"+ CUtils.getParameterGetValueMethodName(outparamTypeName, false)+"(\""+returntype.getParamName()+"\", 0);\n\t\t\t}\n");//damitha
			writer.write("\t\t}\n\t\tm_pCall->unInitialize();\n");//damitha
			writer.write("\t\treturn Ret;\n");//damitha
		}
		else{
			outparamTypeName = returntype.getLangName();//need to have complex type name without *
			writer.write("\t\t\t\tpReturn = ("+outparamTypeName+"*)m_pCall->getCmplxObject((void*) Axis_DeSerialize_"+outparamTypeName+", (void*) Axis_Create_"+outparamTypeName+", (void*) Axis_Delete_"+outparamTypeName+",\""+returntype.getParamName()+"\", 0);\n\t\t}\n");//damitha
			writer.write("\t\t}\n\t\tm_pCall->unInitialize();\n");//damitha
			writer.write("\t\treturn pReturn;\n");//damitha

		}
		//added by nithya
		System.out.println("Fault message ............................"+minfo.getFaultMessage());
		writer.write("\t}\n");//damitha
		writer.write("\tcatch(AxisException& e)\n\t{\n");//damitha
		writer.write("\t\tif (AXIS_SUCCESS == m_pCall->checkFault(\"Fault\",\""+wscontext.getWrapInfo().getTargetEndpointURI()+"\" ))");//damitha
		writer.write("//Exception handling code goes here\n");
		writer.write("\t\t{\n");//damitha
		writer.write("\t\t\tcFaultcode = m_pCall->getElementAsString(\"faultcode\", 0);\n");//damitha
                writer.write("\t\t\tcFaultstring = m_pCall->getElementAsString(\"faultstring\", 0);\n");//damitha
		writer.write("\t\t\tcFaultactor = m_pCall->getElementAsString(\"faultactor\", 0);\n");//damitha
              
		//writer.write("\t\t\tif(0 != strcmp(\"service_exception\", cFaultstring))\n");//damitha
                //writer.write("\t\t\t{\n");//damitha
		//writer.write("\t\t\t\t  cFaultdetail = m_pCall->getElementAsString(\"faultdetail\", 0);\n");//damitha
		//writer.write("\t\t\t\t  throw AxisException(cFaultdetail);\n");//damitha
		//writer.write("\t\t\t}\n");//damitha
		//writer.write("\t\t\telse\n");//damitha
		//writer.write("\t\t\t{\n");//damitha
		//writer.write("\t\t\t\tif (AXIS_SUCCESS == m_pCall->checkFault(\"faultdetail\",\""+wscontext.getWrapInfo().getTargetEndpointURI()+"\"))\n");//damitha

//		to get fault info  		
		Iterator paramsFault = minfo.getFaultType().iterator();
		String faultInfoName =null;
		String faultType =null;	 
		String langName =null;
		String paramName =null;
		while (paramsFault.hasNext()){
			FaultInfo info = (FaultInfo)paramsFault.next();
			faultInfoName =info.getFaultInfo();	     
			ArrayList paramInfo =info.getParams();
			for (int i= 0; i < paramInfo.size(); i++) {				
				ParameterInfo par =(ParameterInfo)paramInfo.get(i);                                                                                                                                                           
				paramName  = par.getParamName();
				langName =par.getLangName();
				faultType = WrapperUtils.getClassNameFromParamInfoConsideringArrays(par,wscontext);
				writeExceptions(faultType,faultInfoName,paramName,langName);
				}
			}
		writer.write("\t\t\telse\n\t\t\t{\n");//damitha

		writer.write("\t\t\t\t  cFaultdetail = m_pCall->getElementAsString(\"faultdetail\", 0);\n");//damitha
		writer.write("\t\t\t\t  throw AxisException(cFaultdetail);\n");//damitha
		writer.write("\t\t\t}\n");//damitha

		writer.write("\t\t}\n");//damitha
		writer.write("\t\telse throw;\n");//damitha
		writer.write("\t}\n");//damitha
		//writer.write("m_pCall->unInitialize();\n");//damitha
		//writer.write("return Ret;\n");//damitha
	
		//end of nithya add
		
		//write end of method
		writer.write("}\n");
	}
	
	/* written by NIthya to get the expections */
	private void writeExceptions(String faulttype,String faultInfoName,String paramName,String langName) throws WrapperFault{
		try{
			    
		        //writer.write("else\n");//damitha
		        writer.write("\t\t\tif(0 == strcmp(\""+langName+"\", cFaultstring))\n");//damitha
		        writer.write("\t\t\t{\n");//damitha
		        writer.write("\t\t\t\tif (AXIS_SUCCESS == m_pCall->checkFault(\"faultdetail\",\""+wscontext.getWrapInfo().getTargetEndpointURI()+"\"))\n");//damitha
                        writer.write("\t\t\t\t{\n");//damitha added
			writer.write("\t\t\t\t\t"+faulttype+" pFaultDetail = NULL;\n");//damitha
			writer.write("\t\t\t\t\tpFaultDetail = ("+faulttype+")m_pCall->\n");//damitha
                        writer.write("\t\t\t\t\t\tgetCmplxObject((void*) Axis_DeSerialize_"+langName+",\n");//damitha
                        writer.write("\t\t\t\t\t\t(void*) Axis_Create_"+langName+",\n");//damitha
                        writer.write("\t\t\t\t\t\t(void*) Axis_Delete_"+langName+",\""+paramName+"\", 0);\n");//damitha
			//writer.write("char* temp = pFaultDetail->varString;");//damitha
                        writer.write("\t\t\t\t\t/*User code to handle the struct can be inserted here*/\n");//damitha
			writer.write("\t\t\t\t\tm_pCall->unInitialize();\n");//damitha
			writer.write("\t\t\t\t\tthrow Axis"+faultInfoName+"Exception(pFaultDetail);\n");//damitha
			writer.write("\t\t\t\t}\n");//damitha
			writer.write("\t\t\t}\n");//damitha
		}		
		catch (IOException e) {
					throw new WrapperFault(e);
				}
	}
	
	/* (non-Javadoc)
	 * @see org.apache.axis.wsdl.wsdl2ws.cpp.CPPClassWriter#writeGlobalCodes()
	 */
	protected void writeGlobalCodes() throws WrapperFault {
		Iterator types = wscontext.getTypemap().getTypes().iterator();
		HashSet typeSet = new HashSet();
		String typeName;
		Type type;
		try {
			while(types.hasNext()){
				type = (Type)types.next();
				if (type.isArray()) continue;
				typeName = type.getLanguageSpecificName();
				if (typeName.startsWith(">")) continue;
				typeSet.add(typeName);
			}
			writer.write("bool CallBase::bInitialized;\n" +
				"CallFunctions CallBase::ms_VFtable;\n");
			Iterator itr = typeSet.iterator();
			while(itr.hasNext())
			{
				typeName = itr.next().toString();
				writer.write("extern int Axis_DeSerialize_"+typeName+"("+typeName+"* param, IWrapperSoapDeSerializer *pDZ);\n");
				writer.write("extern void* Axis_Create_"+typeName+"("+typeName+" *Obj, bool bArray = false, int nSize=0);\n");
				writer.write("extern void Axis_Delete_"+typeName+"("+typeName+"* param, bool bArray = false, int nSize=0);\n");
				writer.write("extern int Axis_Serialize_"+typeName+"("+typeName+"* param, IWrapperSoapSerializer* pSZ, bool bArray = false);\n");
				writer.write("extern int Axis_GetSize_"+typeName+"();\n\n");
			}
		} catch (IOException e) {
			throw new WrapperFault(e);
		}
	}	
}





