/*

 * Copyright 2001-2004 The Apache Software Foundation.

 * 

 * Licensed under the Apache License, Version 2.0 (the "License");

 * you may not use this file except in compliance with the License.

 * You may obtain a copy of the License at

 * 

 *      http://www.apache.org/licenses/LICENSE-2.0

 * 

 * Unless required by applicable law or agreed to in writing, software

 * distributed under the License is distributed on an "AS IS" BASIS,

 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

 * See the License for the specific language governing permissions and

 * limitations under the License.

 */


#include "ServiceNameType.hpp"  
#include "Constants.hpp"

ServiceNameType::ServiceNameType()
{
	m_pachPortName = NULL;
}
ServiceNameType::~ServiceNameType()
{
    if(m_pachPortName != NULL)
	    delete(m_pachPortName);
}

ServiceNameType::ServiceNameType(const AxisChar* pachLocalName, const AxisChar* pachUri, const AxisChar* pachPortName)
:AttributedQName(pachLocalName,pachUri)
{
    if(m_pachPortName != NULL)
	    delete(m_pachPortName);
	m_pachPortName = new AxisChar(strlen(pachPortName)+1);
	strcpy(m_pachPortName,pachPortName);
}

ServiceNameType::ServiceNameType(const AxisChar* pachQname, const AxisChar * pachPortName)
:AttributedQName(pachQname)
{   
    if(m_pachPortName != NULL)
        delete(m_pachPortName);
	m_pachPortName = new AxisChar(strlen(pachPortName)+1);
	strcpy(m_pachPortName,pachPortName);
}

AxisChar * ServiceNameType::getPortName()
{
	return m_pachPortName;
}

void ServiceNameType::setPortName(AxisChar * pachPortName)
{
	delete(m_pachPortName);
	m_pachPortName = new AxisChar(strlen(pachPortName)+1);
	strcpy(m_pachPortName,pachPortName);
}

IHeaderBlock * ServiceNameType::toSoapHeaderBlock(IMessageData *pIMsg)
{
   	IHandlerSoapSerializer* pISZ;
	pIMsg->getSoapSerializer(&pISZ);

	IHeaderBlock* pIHeaderBlock= pISZ->createHeaderBlock();

	pIHeaderBlock->setLocalName(Constants.PORT_TYPE);
	pIHeaderBlock->setUri(Constants.NS_URI_ADDRESSING);
   		          
	if(m_pachPortName!=NULL && strlen(m_pachPortName)!=0)   
		pIHeaderBlock->createAttribute(Constants.PORT_NAME,"",m_pachPortName);
	
	BasicNode * pBasicNode = pIHeaderBlock->createChild(CHARACTER_NODE);
	pBasicNode->setValue(getQname());
	pIHeaderBlock->addChild(pBasicNode);

    return pIHeaderBlock;
}


























































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































 
   
