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
 * This class has the basic logic of the genarating Param classes (Type wrappers).
 * The responsibility of writing serializing and desirializing code is given to the
 * concreate subclasses.
 * @author JayaKumaran
 * @author Susantha Kumara(susantha@opensource.lk, skumara@virtusa.com)
 */


package org.apache.axis.wsdl.wsdl2ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.axis.wsdl.wsdl2ws.info.ElementInfo;
import org.apache.axis.wsdl.wsdl2ws.info.Type;
import org.apache.axis.wsdl.wsdl2ws.info.WebServiceContext;
import org.apache.axis.wsdl.wsdl2ws.info.AttributeInfo;

public abstract class ParamWriter extends BasicFileWriter{
    protected static final int INPUT_PARM = 0;
    protected static final int RETURN_PARM = 1;
    protected static final int COMMAN_PARM = 2;
	
	protected AttributeInfo extensionBaseAttrib=null;
	/* no of parameters treated as attributes: ie first attributeParamCount of
	 * attribs will be treated as attributes
	 */
	protected int attributeParamCount = 0;
	/* array of parameter types and parameter names of the this param */
    protected AttributeInfo[] attribs;
    
    protected WebServiceContext wscontext;
    
    /* Type of this param */
    protected Type type;

    public ParamWriter(WebServiceContext wscontext,Type type) throws WrapperFault {
            super(WrapperUtils.getLanguageTypeName4Type(type));
            this.wscontext = wscontext;
            this.type = type;
            populateAttribList(wscontext.getSerInfo().getQualifiedServiceName());
    }
 
    protected void writeClassComment() throws WrapperFault {
        try{
			writer.write("/*\n");
			writer.write(" * This file was auto-generated by the Axis C++ Web Service Generator (WSDL2Ws)\n");
			writer.write(" * This file contains functions to manipulate complex type "+ classname+"\n");
			writer.write(" */\n\n");
    	   } catch (IOException e) {
            e.printStackTrace();
            throw new WrapperFault(e);
        }
    }
	public AttributeInfo[] getAttribList(String Qualifiedname) throws WrapperFault {
		return this.attribs;
	}   
 	/* genarate the arrtibs array */
 	private void populateAttribList(String Qualifiedname) throws WrapperFault {
 		ElementInfo elemi = type.getExtensionBaseType();
 		if ( elemi != null){
			extensionBaseAttrib = new AttributeInfo();
			extensionBaseAttrib.setParamName(elemi.getName().getLocalPart());
			extensionBaseAttrib.setTypeName(CUtils.getclass4qname(elemi.getType().getName()));
			extensionBaseAttrib.setType(elemi.getType());
			extensionBaseAttrib.setElementName(elemi.getName());
 		} 
		ArrayList attribfeilds = new ArrayList();
		ArrayList elementfeilds = new ArrayList();

		Iterator names = type.getAttributeNames();
		while (names.hasNext()){
			attribfeilds.add(names.next());
		}        
		names = type.getElementnames();
		while (names.hasNext()){
			elementfeilds.add(names.next());
		}
		int intAttrFieldSz = attribfeilds.size();
		attributeParamCount = intAttrFieldSz;
		int intEleFieldSz = elementfeilds.size();
		this.attribs = new AttributeInfo[intAttrFieldSz+intEleFieldSz];
		for (int i = 0 ; i < intAttrFieldSz; i++) {
			this.attribs[i] = new AttributeInfo();
			this.attribs[i].setParamName((String)attribfeilds.get(i));
			Type attribType = type.getTypForAttribName(this.attribs[i].getParamName());            
			if(CUtils.isSimpleType(attribType.getName()))
				this.attribs[i].setTypeName(CUtils.getclass4qname(attribType.getName()));
			else{
				this.attribs[i].setTypeName(attribType.getLanguageSpecificName());
				this.attribs[i].setSimpleType(false);
			}
			this.attribs[i].setType(attribType);
			this.attribs[i].setAttribute(true);
			this.attribs[i].setElementName(attribType.getName()); //TODO this is wrong. correct immediately. this will cause attributes serialized incorrectly
			//TODO : how to find whether this attribute is optional or not ?
		}

		for (int i = intAttrFieldSz ; i < intAttrFieldSz+intEleFieldSz; i++) {
			this.attribs[i] = new AttributeInfo();
			this.attribs[i].setParamName((String) elementfeilds.get(i-attributeParamCount));   
			ElementInfo elem = type.getElementForElementName(this.attribs[i].getParamName());
			Type elementType = elem.getType();
			if(CUtils.isAnyType(elementType.getName())){
				this.attribs[i].setAnyType(true);
			}
			if(CUtils.isSimpleType(elementType.getName()))
				this.attribs[i].setTypeName(CUtils.getclass4qname(elementType.getName()));
			else{
				this.attribs[i].setTypeName(elementType.getLanguageSpecificName());
				this.attribs[i].setSimpleType(false);
			}
			this.attribs[i].setType(elementType);
			this.attribs[i].setElementName(elem.getName());
		   	if(elementType.isArray()){ //soap encoding arrays.
				Type arrayType = WrapperUtils.getArrayType(elementType); //get contained type
				this.attribs[i].setArray(true);
				if(CUtils.isSimpleType(arrayType.getName())){
					this.attribs[i].setTypeName(CUtils.getclass4qname(arrayType.getName()));
					this.attribs[i].setSimpleType(true);
				}
				else{
					this.attribs[i].setTypeName(arrayType.getLanguageSpecificName());
					this.attribs[i].setSimpleType(false);
				}
				this.attribs[i].setType(arrayType); //set contained type as type
		   }else if (elem.getMaxOccurs() > 1){
		   		//arrays but the same type as was set above
				this.attribs[i].setArray(true);
		   }
		   if (elem.getMinOccurs() == 0) this.attribs[i].setOptional(true);
		}	
 	}

 	protected String getCorrectParmNameConsideringArraysAndComplexTypes(AttributeInfo attrib)throws WrapperFault{
		if (attrib.isArray()){
			if (attrib.isSimpleType())
				return CUtils.getBasicArrayNameforType(attrib.getTypeName());
			else
				return CUtils.getCmplxArrayNameforType(attrib.getSchemaName());
		}
		else if (!attrib.isSimpleType()){
			return attrib.getTypeName()+"*";	
		}else{
			if (attrib.isAttribute() && attrib.isOptional()){ //variables corresponding to optional attributes are pointer types
				return attrib.getTypeName()+"*";	
			}
			else
				return attrib.getTypeName();
		}
	}
 	
 	/* This is a must for complex wsdl file (cycle in includes)*/
 	protected String getCHeaderFileCorrectParmNameConsideringArraysAndComplexTypes(AttributeInfo attrib)throws WrapperFault{
		if (attrib.isArray()){
			if (attrib.isSimpleType())
				return CUtils.getBasicArrayNameforType(attrib.getTypeName());
			else
				return CUtils.getCmplxArrayNameforType(attrib.getSchemaName());
		}
		else if (!attrib.isSimpleType()){
			return attrib.getTypeName()+"*";	
		}else{
			if (attrib.isAttribute() && attrib.isOptional()){ //variables corresponding to optional attributes are pointer types
				return attrib.getTypeName()+"*";	
			}
			else
				return attrib.getTypeName();
		}
	} 	

}
