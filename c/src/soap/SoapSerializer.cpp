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

/*
 * @author Roshan Weerasuriya (roshan@opensource.lk, roshanw@jkcsworld.com)
 * @author Susantha Kumara (susantha@opensource.lk, skumara@virtusa.com)
 * @author Damitha Kumarage (damitha@opensource.lk, damitha@jkcsworld.com)
 *
 */

/*
 * Revision 1.1  2005/01/09 Roshan
 * Added cleanup of SoapAttachments to the Destructor.
 */

#ifdef WIN32
#pragma warning (disable : 4786)
#endif

#include "SoapEnvelope.h"
#include "SoapSerializer.h"
#include "HeaderBlock.h"
#include <axis/GDefine.hpp>
#include "../common/ArrayBean.h"
#include "../common/BasicTypeSerializer.h"
#include "../soap/SoapKeywordMapping.h"
#include "AxisSoapException.h"
#include <stdio.h>
#include <stdarg.h>

#include "../common/AxisTrace.h"
#include "../common/AxisConfig.h"

extern AXIS_CPP_NAMESPACE_PREFIX AxisConfig* g_pConfig;

AXIS_CPP_NAMESPACE_START

SoapSerializer::SoapSerializer()
{
    m_pSoapEnvelope = NULL;
    m_iSoapVersion = SOAP_VER_1_1;
    m_pOutputStream = NULL;
	m_pNamespace = NULL;
}

SoapSerializer::~SoapSerializer()
{
    if (m_pSoapEnvelope) delete m_pSoapEnvelope;

	/* Cleaning the memory allocated to the SoapAttachments */       
	map<AxisXMLString, ISoapAttachment*>::iterator itCurrAttach= m_SoapAttachments.begin();
	while(itCurrAttach != m_SoapAttachments.end())
    {        
        delete ((SoapAttachment*)((*itCurrAttach).second));		

        itCurrAttach++;
    }
}

int SoapSerializer::setSoapEnvelope(SoapEnvelope *pSoapEnvelope)
{
    m_pSoapEnvelope = pSoapEnvelope;

    return AXIS_SUCCESS;
}

int SoapSerializer::setSoapHeader(SoapHeader *pSoapHeader)
{
    int intStatus = AXIS_FAIL;

    if(m_pSoapEnvelope)
    {

        if((m_pSoapEnvelope)&& (m_pSoapEnvelope->m_pSoapHeader))
        {
            /* no need to create a SOAP Header, it already exists */
            intStatus= AXIS_OBJECT_ALREADY_EXISTS;
        }
        else
        {
            m_pSoapEnvelope->setSoapHeader(pSoapHeader);
            intStatus= AXIS_SUCCESS;
        }
    }

    return intStatus;
}

int SoapSerializer::setSoapBody(SoapBody *pSoapBody)
{
    int intStatus= AXIS_FAIL;

    if(m_pSoapEnvelope)
    {
        m_pSoapEnvelope->setSoapBody(pSoapBody);
        intStatus= AXIS_SUCCESS;
    }

    return intStatus;
}

int SoapSerializer::setSoapMethod(SoapMethod *pSoapMethod)
{
    int intStatus= AXIS_FAIL;

    if(m_pSoapEnvelope && (m_pSoapEnvelope->m_pSoapBody))
    {
		if (!(m_pSoapEnvelope->m_pSoapBody->m_pSoapMethod)) {
			m_pSoapEnvelope->m_pSoapBody->m_pSoapMethod= pSoapMethod;
		}
			
		intStatus= AXIS_SUCCESS;
    }

    return intStatus;
}

int SoapSerializer::addOutputBasicArrayParam(const Axis_Array* pArray, 
                                             XSDTYPE nType,
                                             const AxisChar* pName)
{
    IArrayBean* pAb = makeArrayBean(nType, (void*)(pArray->m_Array));
    pAb->SetDimension(pArray->m_Size);
    Param* pParam = new Param();
    if (RPC_ENCODED == m_nStyle)
    {
        pAb->SetItemName("item");
        pParam->setName(pName);
    }
    else
    {
        pAb->SetItemName(pName);
        pParam->setName("array");        
    }
    pParam->m_Value.pIArray = pAb;
    pParam->m_Type = XSD_ARRAY;
    if(m_pSoapEnvelope && (m_pSoapEnvelope->m_pSoapBody) && 
        (m_pSoapEnvelope->m_pSoapBody->m_pSoapMethod)) 
    {
        m_pSoapEnvelope->m_pSoapBody->m_pSoapMethod->addOutputParam(pParam);
    }
    return AXIS_SUCCESS;
}

int SoapSerializer::addOutputCmplxArrayParam(const Axis_Array* pArray, 
                                             void* pSZFunct, void* pDelFunct,
                                             void* pSizeFunct, 
                                             const AxisChar* pName, 
                                             const AxisChar* pNamespace)
{
    IArrayBean* pAb = makeArrayBean((void*)(pArray->m_Array), pSZFunct, 
        pDelFunct, pSizeFunct);
    pAb->SetDimension(pArray->m_Size);
    Param* pParam = new Param();
    if (RPC_ENCODED == m_nStyle)
    {
        pAb->SetItemName("item");
        pAb->SetTypeName(pName);
        pAb->SetUri(pNamespace);
        pParam->setName(pName);
    }
    else
    {
        pAb->SetItemName(pName);
	    if (NULL != pNamespace) {
		    pParam->setURI(pNamespace);
		}
        pParam->setName("array");        
    }
    pParam->m_Value.pIArray = pAb;
    pParam->m_Type = XSD_ARRAY;
    if(m_pSoapEnvelope && (m_pSoapEnvelope->m_pSoapBody) && (m_pSoapEnvelope->
        m_pSoapBody->m_pSoapMethod)) 
    {
        m_pSoapEnvelope->m_pSoapBody->m_pSoapMethod->addOutputParam(pParam);
    }
    pParam->setName(pName);
    return AXIS_SUCCESS;
}

int SoapSerializer::addOutputCmplxParam(void* pObject, void* pSZFunct, 
                                        void* pDelFunct, const AxisChar* pName,
                                        const AxisChar* pNamespace )
{ 
    Param* pParam = new Param();
    pParam->m_Value.pCplxObj = new ComplexObjectHandler;
    pParam->m_Value.pCplxObj->pObject = pObject;
    pParam->m_Value.pCplxObj->pSZFunct = (AXIS_SERIALIZE_FUNCT)pSZFunct;
    pParam->m_Value.pCplxObj->pDelFunct = (AXIS_OBJECT_DELETE_FUNCT)pDelFunct;
	
    if(m_pSoapEnvelope && (m_pSoapEnvelope->m_pSoapBody) && (m_pSoapEnvelope->
        m_pSoapBody->m_pSoapMethod)) 
    {
        m_pSoapEnvelope->m_pSoapBody->m_pSoapMethod->addOutputParam(pParam);
    }
    pParam->setName(pName);
    return AXIS_SUCCESS;
}

int SoapSerializer::addFaultDetail(void* pObject, void* pSZFunct, 
                                        void* pDelFunct, const AxisChar* pName,
                                        const AxisChar* pNamespace )
{ 
    Param* pParam = new Param();
    pParam->m_Value.pCplxObj = new ComplexObjectHandler;
    pParam->m_Value.pCplxObj->pObject = pObject;
    pParam->m_Value.pCplxObj->pSZFunct = (AXIS_SERIALIZE_FUNCT)pSZFunct;
    pParam->m_Value.pCplxObj->pDelFunct = (AXIS_OBJECT_DELETE_FUNCT)pDelFunct;
    if(m_pSoapEnvelope && (m_pSoapEnvelope->m_pSoapBody) && (m_pSoapEnvelope->
        m_pSoapBody->m_pSoapFault)) 
    {
        m_pSoapEnvelope->m_pSoapBody->m_pSoapFault->setFaultDetail(pParam);
    }
    pParam->setName(pName);
    return AXIS_SUCCESS;
}

int SoapSerializer::setSoapFault(SoapFault *pSoapFault)
{
    int intStatus= AXIS_FAIL;

    if(m_pSoapEnvelope && (m_pSoapEnvelope->m_pSoapBody))
    {
        m_pSoapEnvelope->m_pSoapBody->m_pSoapFault= pSoapFault;
        intStatus= AXIS_SUCCESS;
    }

    return intStatus;
}

int SoapSerializer::setOutputStream(SOAPTransport* pStream)
{
    m_pOutputStream = pStream;
    int iStatus= AXIS_SUCCESS;
    try
    {
    if(m_pSoapEnvelope)
    {
		if (checkAttachmentAvailability()) {

            string asStartID;
            string asSOAPMimeHeaders;
            asSOAPMimeHeaders   = pStream->getIncomingSOAPMimeHeaders();
            int start= asSOAPMimeHeaders.find("Content-Type");
            int startPosIdValue = asSOAPMimeHeaders.find ("<",start+strlen("Content-Id:"))+1;
            int endPosIdValue   = asSOAPMimeHeaders.find(">", start+strlen("Content-Type"));
            int length          = endPosIdValue - startPosIdValue ;
            asStartID           = asSOAPMimeHeaders.substr (startPosIdValue,length); 
        
            string * asContentType = new string("multipart/related; type=\"text/xml\"; start=\"<");
            *asContentType=*asContentType + asStartID + ">\"";
            *asContentType=*asContentType +";  boundary=\"------=MIME BOUNDARY\"";
            pStream->setTransportProperty(CONTENT_TYPE, (*asContentType).c_str()); 

			serialize("\n------=MIME BOUNDARY\n", NULL);
			serialize(pStream->getIncomingSOAPMimeHeaders(), "\n\n", NULL);
		}

        serialize("<?xml version='1.0' encoding='utf-8' ?>", NULL);
        iStatus= m_pSoapEnvelope->serialize(*this, 
            (SOAP_VERSION)m_iSoapVersion);		
		
		if (checkAttachmentAvailability()) {
			serialize("\n------=MIME BOUNDARY\n", NULL);
			serializeAttachments(*this);
		}

    }
    }
    catch(AxisSoapException& e)
    {
        e = e;

        throw;
    }
    catch(AxisException& e)
    {
        e = e;

        throw;
    }
    catch(...)
    {
        throw;
    }
    return iStatus;
}

/*
 * Some client side transport layer may wait until the end of buffers is 
 * acknowladged. Use this function to do that.
 */
void SoapSerializer::markEndOfStream()
{
    m_pOutputStream->flushOutput();
}

/*
 * Initializing the members of the class. This is needed since
 * the same object instance of this class, may be used to server
 * several SOAP requests.
 */
int SoapSerializer::init()
{
    m_nStatus = AXIS_SUCCESS;

    if(m_pSoapEnvelope)
    {
        delete m_pSoapEnvelope;
        m_pSoapEnvelope= NULL;
    }

    /* Adding SoapEnvelop and SoapBody to Serializer */
    m_pSoapEnvelope = new SoapEnvelope();
    m_pSoapEnvelope->setSoapBody(new SoapBody());
    
    m_nCounter=0;
    m_NsStack.clear();
    return AXIS_SUCCESS;
}

int SoapSerializer::setSoapVersion(SOAP_VERSION nSoapVersion)
{
    m_iSoapVersion = nSoapVersion;

    m_pSoapEnvelope->clearStandardNamespaceDecl(); // Samisa : AXISCPP-368

    /* here the default namespaces of the SoapEnvelop should be added and 
     * intialized as well. 
     */
    m_pSoapEnvelope->addStandardNamespaceDecl
        (SoapKeywordMapping::map(nSoapVersion).pEnv);
    m_pSoapEnvelope->addStandardNamespaceDecl
        (SoapKeywordMapping::map(nSoapVersion).pXsd);
    m_pSoapEnvelope->addStandardNamespaceDecl
        (SoapKeywordMapping::map(nSoapVersion).pXsi);

    /* Adding the standard SOAP namespace to the namespace stack */
    m_NsStack[SoapKeywordMapping::map(nSoapVersion).pchNamespaceUri] = 
        SoapKeywordMapping::map(nSoapVersion).pchPrefix;

    return AXIS_SUCCESS;
}
void SoapSerializer::removeNamespacePrefix(const AxisChar* pNamespace)
{
    if (m_NsStack.find(pNamespace) != m_NsStack.end())
    {
        m_NsStack.erase(pNamespace);
    }
}

const AxisChar* SoapSerializer::getNamespacePrefix(const AxisChar* pNamespace)
{
    if (m_NsStack.find(pNamespace) == m_NsStack.end())
    {
        m_nCounter++;
        AxisSprintf(m_Buf, 8, "ns%d", m_nCounter);
        m_NsStack[pNamespace] = m_Buf;
    }
    return m_NsStack[pNamespace].c_str();
}

const AxisChar* SoapSerializer::getNamespacePrefix(const AxisChar* pNamespace, 
                                                   bool& blnIsNewPrefix)
{
    if (m_NsStack.find(pNamespace) == m_NsStack.end())
    {
        m_nCounter++;
        AxisSprintf(m_Buf, 8, "ns%d", m_nCounter);
        m_NsStack[pNamespace] = m_Buf;
        blnIsNewPrefix = true;
    }
    return m_NsStack[pNamespace].c_str();
}

IWrapperSoapSerializer& SoapSerializer::operator <<(const AxisChar* 
                                                    cSerialized)
{
    if (AXIS_SUCCESS != m_nStatus) 
    {
        /* some thing has gone wrong. So do nothing */
        return *this;
    }

    try
    {
      /* send everything to transport layer, it should handle bufferization itself */
      int nStatus = m_pOutputStream->sendBytes(cSerialized, 0);
      // FIXME: should we process nStatus somehow?
    }
    catch(AxisSoapException& e)
    {
        e = e;

        throw;
    }
    catch(AxisException& e)
    {
        e = e;

        throw;
    }
    catch(...)
    {
        throw;
    }

    return *this;
}

int SoapSerializer::createSoapMethod(const AxisChar* sLocalName, 
                                     const AxisChar* sURI)
{
    SoapMethod* pMethod = new SoapMethod();
    setSoapMethod(pMethod);
    pMethod->setLocalName(sLocalName);
    pMethod->setPrefix(getNamespacePrefix(sURI));
    pMethod->setURI(sURI);
    return AXIS_SUCCESS;
}

int SoapSerializer::createSoapFault(const AxisChar* sLocalName, 
                                     const AxisChar* sURI,
				     const AxisChar* sFaultCode,
				     const AxisChar* sFaultString)
{
    SoapFault* pSoapFault = new SoapFault();
    setSoapFault(pSoapFault);
    pSoapFault->setLocalName(sLocalName);
    pSoapFault->setPrefix(getNamespacePrefix(sURI));
    pSoapFault->setURI(sURI);
    pSoapFault->setFaultcode(sFaultCode);
    pSoapFault->setFaultstring(sFaultString);
    char* pcNodeName = g_pConfig->getAxisConfProperty(AXCONF_NODENAME);
    char* pcPort = g_pConfig->getAxisConfProperty(AXCONF_LISTENPORT);
    string strUrl = pcNodeName;
    strUrl += ":";
    strUrl += string(pcPort);
    pSoapFault->setFaultactor(strUrl);
    
    return AXIS_SUCCESS;
}

/*
 * Used to create an ArrayBean when the return type of a method is an array
 * of basic types
 */
IArrayBean* SoapSerializer::makeArrayBean(XSDTYPE nType, void* pArray)
{
    ArrayBean* pAb = new ArrayBean();
    pAb->m_type = nType;
    pAb->m_value.sta = pArray;
    return pAb;
}

/*
 * Used to create an ArrayBean when the return type of a method is an array
 * of complex types
 */
IArrayBean* SoapSerializer::makeArrayBean(void* pObject, void* pSZFunct, 
                                          void* pDelFunct, void* pSizeFunct)
{
    ArrayBean* pAb = new ArrayBean();
    pAb->m_type = USER_TYPE;
    pAb->m_value.cta = new ComplexObjectHandler;
    pAb->m_value.cta->pSZFunct = (AXIS_SERIALIZE_FUNCT)pSZFunct;
    pAb->m_value.cta->pDelFunct = (AXIS_OBJECT_DELETE_FUNCT)pDelFunct;
    pAb->m_value.cta->pSizeFunct = (AXIS_OBJECT_SIZE_FUNCT)pSizeFunct;
    pAb->m_value.cta->pObject = pObject;
    return pAb;
}

IHeaderBlock* SoapSerializer::createHeaderBlock()
{
    HeaderBlock* pHeaderBlock= new HeaderBlock();
    setHeaderBlock(pHeaderBlock);
    return pHeaderBlock;
}

int SoapSerializer::setHeaderBlock(HeaderBlock *pHeaderBlock)
{
    int intStatus= AXIS_FAIL;

    if((m_pSoapEnvelope)&& (m_pSoapEnvelope->m_pSoapHeader))
    {
        /* no need to create a SOAP Header, it already exists */
    }
    else
    {
        SoapHeader* pSoapHeader= new SoapHeader();
        setSoapHeader(pSoapHeader);
    }

    m_pSoapEnvelope->m_pSoapHeader->addHeaderBlock(pHeaderBlock);
    intStatus= AXIS_SUCCESS;

    return intStatus;
}

/*
 * This method not only removes the existing SoapHeader, but also removes
 * all the existing HeaderBlocks which are associated with this SoapHeader.
 * Therefore the caller of this method has to be exactly sure whether he wants
 * to do this.
 */
int SoapSerializer::removeSoapHeader()
{
    delete m_pSoapEnvelope->m_pSoapHeader;
    m_pSoapEnvelope->m_pSoapHeader= NULL;

    return AXIS_SUCCESS;
}

/*
 * Used to Serialize an array of complex types inside a complex type. Called 
 * from within the Serialize wrapper method of the complex type.
 */
int SoapSerializer::serializeCmplxArray(const Axis_Array* pArray, 
                                        void* pSZFunct, void* pDelFunct,
                                        void* pSizeFunct, 
                                        const AxisChar* pName, 
                                        const AxisChar* pNamespace)
{
    ArrayBean* pAb = (ArrayBean*)makeArrayBean((void*)(pArray->m_Array), 
        pSZFunct, pDelFunct, pSizeFunct);
    pAb->SetDimension(pArray->m_Size);
    Param* pParam = new Param();
    if (RPC_ENCODED == m_nStyle)
    {
        pAb->SetItemName("item");
        pAb->SetTypeName(pName);
        pAb->SetUri(pNamespace);
        pParam->setName(pName);
    }
    else
    {
        pAb->SetItemName(pName);
        pParam->setName("array");        
    }
    pParam->m_Value.pIArray = pAb;
    pParam->m_Type = XSD_ARRAY;
	if (pNamespace != NULL)
    {
        const AxisChar* np = getNamespacePrefix(pNamespace);
        const AxisChar* originalNamespace = getNamespace(); // Store original namespace
        pParam->setPrefix(np);
        setNamespace(pNamespace);
        pParam->serialize(*this);
        setNamespace(originalNamespace); // Revert back original namespace
    }
    else
    {
        pParam->serialize(*this);
    }
    /* Remove pointer to the array from the ArrayBean to avoid deleting the 
     * array when ArrayBean is deleted. Array will be deleted when the complex
     * type that contains this array is deleted */
    pAb->RemoveArrayPointer();
    delete pParam;
    return AXIS_SUCCESS;
}

/*
 * Used to Serialize an array of basic types inside a complex type. Called from
 * within the Serialize wrapper method of the complex type.
 */
int SoapSerializer::serializeBasicArray(const Axis_Array* pArray, 
                                        XSDTYPE nType, const AxisChar* pName)
{
	return serializeBasicArray(pArray, NULL, nType, pName);
}

/*
 * Used to Serialize an array of basic types inside a complex type. Contains 
 * the namespace so that it can be serialized. Called from within the Serialize
 * wrapper method of the complex type.
 */
int SoapSerializer::serializeBasicArray(const Axis_Array* pArray, 
										const AxisChar* pNamespace, 
                                        XSDTYPE nType, const AxisChar* pName)
{
	ArrayBean* pAb = (ArrayBean*)makeArrayBean(nType, 
    (void*)(pArray->m_Array));
    pAb->SetDimension(pArray->m_Size);
    Param* pParam = new Param();
    if (RPC_ENCODED == m_nStyle)
    {
        pAb->SetItemName("item");
        pParam->setName(pName);
    }
    else
    {
        pAb->SetItemName(pName);
        pParam->setName("array");        
    }
    pParam->m_Value.pIArray = pAb;
    pParam->m_Type = XSD_ARRAY;
	if (pNamespace != NULL) {
		const AxisChar* np = getNamespacePrefix(pNamespace);
		pParam->setPrefix(np);
		setNamespace(pNamespace);
    }
    pParam->serialize(*this);
    /* Remove pointer to the array from the ArrayBean to avoid deleting the
     * array when ArrayBean is deleted. Array will be deleted when the complex
     * type that contains this array is deleted
     */
    pAb->RemoveArrayPointer();
    delete pParam;
    return AXIS_SUCCESS;
}

#ifdef UNIT_TESTING_ON
int SoapSerializer::setOutputStreamForTesting(SOAPTransport* pStream)
{
    m_pOutputStream = pStream;

    return AXIS_SUCCESS;
}
#endif

/*
 * Basic output parameter going to be serialized as an Element later
 */
int SoapSerializer::addOutputParam(const AxisChar* pchName, void* pValue, 
                                   XSDTYPE type)
{
    Param* pParam = new Param();
    if (!pParam) return AXIS_FAIL;
    pParam->m_Type = type;
    pParam->m_sName = pchName;
    switch(type)
    {
    case XSD_INT:
    case XSD_BOOLEAN:
        pParam->m_Value.nValue = *((int*)(pValue));
        break; 
    case XSD_UNSIGNEDINT:
        pParam->m_Value.unValue = *((unsigned int*)(pValue));
        break;           
    case XSD_SHORT:
        pParam->m_Value.sValue = *((short*)(pValue));
        break; 
    case XSD_UNSIGNEDSHORT:
        pParam->m_Value.usValue = *((unsigned short*)(pValue));
        break;         
    case XSD_BYTE:
        pParam->m_Value.cValue = *((char*)(pValue));
        break; 
    case XSD_UNSIGNEDBYTE:
        pParam->m_Value.ucValue = *((unsigned char*)(pValue));
        break;
    case XSD_LONG:
        pParam->m_Value.llValue = *((LONGLONG*)(pValue));
        break;
    case XSD_INTEGER:
        pParam->m_Value.lValue = *((long*)(pValue));
        break;        
    case XSD_DURATION:
        pParam->m_Value.lDuration = *((long*)(pValue));
        break;        
    case XSD_UNSIGNEDLONG:
        pParam->m_Value.ulValue = *((unsigned long*)(pValue));
        break;
    case XSD_FLOAT:
        pParam->m_Value.fValue = *((float*)(pValue));
        break;
    case XSD_DOUBLE:
    case XSD_DECIMAL:
        pParam->m_Value.dValue = *((double*)(pValue));
        break;              
    case XSD_ANYURI:
    case XSD_QNAME:
    case XSD_NOTATION:
    case XSD_STRING:
        pParam->m_Value.pStrValue = *((char**)(pValue));
        break;
    case XSD_HEXBINARY:
        pParam->m_Value.hbValue = *((xsd__hexBinary*)(pValue));
        break;
    case XSD_BASE64BINARY:
        pParam->m_Value.b64bValue = *((xsd__base64Binary*)(pValue));
        break;
    case XSD_DATETIME:
    case XSD_DATE:
    case XSD_TIME:
        pParam->m_Value.tValue = *((struct tm*)(pValue));
        break; 
    default:
        delete pParam;
        return AXIS_FAIL;
    }
    if(m_pSoapEnvelope && (m_pSoapEnvelope->m_pSoapBody) && (m_pSoapEnvelope->
        m_pSoapBody->m_pSoapMethod)) 
    {
        m_pSoapEnvelope->m_pSoapBody->m_pSoapMethod->addOutputParam(pParam);
    }
    else
    {
        delete pParam;
        return AXIS_FAIL;
    }    
    return AXIS_SUCCESS;
}

int SoapSerializer::serializeAsElement(const AxisChar* pName, void* pValue, 
                                       XSDTYPE type)
{
	return serializeAsElement(pName, NULL, pValue, type);
}

int SoapSerializer::serializeAsElement(const AxisChar* pName, 
									   const AxisChar* pNamespace,
									   void* pValue, 
                                       XSDTYPE type) 
{
    const AxisChar* pPrefix = NULL;
    if (pNamespace)
	{
        pPrefix = getNamespacePrefix(pNamespace);
    }
    const AxisChar* pSerialized = m_BTSZ.serializeAsElement(pName, pPrefix, pValue, 
        type);
    if (pSerialized)
    {
        *this << pSerialized;
        return AXIS_SUCCESS;
    }
    else
    {
        return AXIS_FAIL;
    }
} 

int SoapSerializer::serializeAsAttribute(const AxisChar* pName, 
                                         const AxisChar* pNamespace, 
                                         void* pValue, XSDTYPE type)
{
    const AxisChar* pPrefix = NULL;
    if (pNamespace)
    {
        pPrefix = getNamespacePrefix(pNamespace);
    }
    const AxisChar* pSerialized = m_BTSZ.serializeAsAttribute(pName, pPrefix,
        pValue, type);
    if (pSerialized)
    {
        *this << pSerialized;
        return AXIS_SUCCESS;
    }
    else
    {
        return AXIS_FAIL;
    }
}

void SoapSerializer::serializeVargs(int count, const char **args)
{
    for (int i=0; i<count; i++)
    {
        if (NULL != args[i])
            *this << args[i];
    }
}

void SoapSerializer::serialize(const char* pFirst, ...)
{
    va_list vList;
    va_start( vList, pFirst );     /* Initialize variable arguments. */
    const char* pArg;
    if (pFirst) *this << pFirst;
    do
    {
        pArg = va_arg( vList, const char*);
        if (pArg)
            *this << pArg;
    }
    while (pArg != NULL);
    va_end( vList);              /* Reset variable arguments.      */
}

/*
 * Used probably by a handler to add a header block to the Serializer. 
 */
int SoapSerializer::addHeaderBlock(IHeaderBlock* pBlk)
{
    if (m_pSoapEnvelope)
    {
        if(!m_pSoapEnvelope->m_pSoapHeader) m_pSoapEnvelope->m_pSoapHeader = 
            new SoapHeader();
    }
    else /* error : by this time there should be an envelope */
    {
        return AXIS_FAIL;
    }
    m_pSoapEnvelope->m_pSoapHeader->addHeaderBlock((HeaderBlock*)pBlk);
    return AXIS_SUCCESS;
}

int SoapSerializer::setBodyAsHexBinary(xsd__hexBinary body)
{
    /* TODO */
    return AXIS_SUCCESS;
}

int SoapSerializer::setBodyAsBase64Binary(xsd__base64Binary body)
{
    /* TODO */    
    return AXIS_SUCCESS;
}

const AxisChar* SoapSerializer::getBodyAsString()
{
    /* TODO */    
    return NULL;
}

/* following two functions are needed by serializer functions of complex types
 * for RPC style web services
 */
void SoapSerializer::serializeStartElementOfType(const AxisChar* pName, 
                                                 const AxisChar* pNamespace, 
                                                 const AxisChar* pPrefix)
{
    if (pPrefix)
    {
        serialize("<", pName, " xsi:type=\"", pPrefix, ":", pName, "\" xmlns:",
            pPrefix, "=\"", pNamespace, "\">", NULL);
    }
    else
    {
        serialize("<", pName, ">", NULL);
    }
}

void SoapSerializer::serializeEndElementOfType(const AxisChar* pName)
{
    serialize("</", pName, ">", NULL);
}


IHeaderBlock* SoapSerializer::createHeaderBlock(const AxisChar *pachLocalName, 
                                                const AxisChar *pachUri)
{
    HeaderBlock* pHeaderBlock= new HeaderBlock(pachLocalName, pachUri);
    setHeaderBlock(pHeaderBlock);
    return pHeaderBlock;    
}

SoapMethod* SoapSerializer::getSOAPMethod()
{
	/*
	 *TODO
	 * return the SOAPMethod object if one is available. If not available create a SOAPMethod and set it and return it.
	 */

	return NULL;
}

int SoapSerializer::setSOAPMethodAttribute(Attribute *pAttribute)
{
	if(m_pSoapEnvelope && (m_pSoapEnvelope->m_pSoapBody) && (m_pSoapEnvelope->
        m_pSoapBody->m_pSoapMethod))
    {
		m_pSoapEnvelope->m_pSoapBody->m_pSoapMethod->addAttribute(pAttribute);
		return AXIS_SUCCESS;
	}

	return AXIS_FAIL;
}

int SoapSerializer::serializeAnyObject(AnyType* pAnyObject)
{
    int i;
    for (i=0; i<pAnyObject->_size; i++)
    {
        serialize(pAnyObject->_array[i], 0);
    }
	return AXIS_SUCCESS;
}

int SoapSerializer::addOutputAnyObject(AnyType* pAnyObject)
{
    Param* pParam = new Param();
    pParam->m_Type = XSD_ANY;
    pParam->m_Value.pAnyObject = pAnyObject;
    if(m_pSoapEnvelope && (m_pSoapEnvelope->m_pSoapBody) && (m_pSoapEnvelope->
        m_pSoapBody->m_pSoapMethod))
    {
        m_pSoapEnvelope->m_pSoapBody->m_pSoapMethod->addOutputParam(pParam);
    }
    return AXIS_SUCCESS;
}

/*
 * TODO: Have to remove this method. Date logged 13Jan2005
 
IHeaderBlock* SoapSerializer::getHeaderBlock()
{
    if ((!m_pSoapEnvelope) || (!m_pSoapEnvelope->m_pSoapHeader)) return NULL;

    return (HeaderBlock*)m_pSoapEnvelope->m_pSoapHeader->getHeaderBlock(false);	
}
*/

IHeaderBlock* SoapSerializer::getHeaderBlock(const AxisChar *pcName, 
											 const AxisChar *pcNamespace)
{
    if ((!m_pSoapEnvelope) || (!m_pSoapEnvelope->m_pSoapHeader)) return NULL; 
	/* there has been no <Header> element so there
                                  can be no Header blocks */

    return m_pSoapEnvelope->m_pSoapHeader->getHeaderBlock(pcName, pcNamespace, false);
}

IHeaderBlock* SoapSerializer::getFirstHeaderBlock()
{
	return m_pSoapEnvelope->m_pSoapHeader->getFirstHeaderBlock();
}

IHeaderBlock* SoapSerializer::getNextHeaderBlock()
{
	return m_pSoapEnvelope->m_pSoapHeader->getNextHeaderBlock();
}

int SoapSerializer::serializeAsChardata(void* pValue, XSDTYPE type)
{
    char* pStr = m_Buf;
    switch (type)
    {
    case XSD_INT:
        {
            Int intSerializer;
            pStr = intSerializer.serialize(pValue);
        }
        break;
    case XSD_BOOLEAN:
        {
            Boolean booleanSerializer;
            pStr = booleanSerializer.serialize(pValue);
        }
        break;
    case XSD_UNSIGNEDINT:
        {
            UnsignedInt unsignedIntSerializer;
            pStr = unsignedIntSerializer.serialize(pValue);
        }
        break;
    case XSD_SHORT:
        {
            Short shortSerializer;
            pStr = shortSerializer.serialize(pValue);
        }
        break;
    case XSD_UNSIGNEDSHORT:
        {
            UnsignedShort unsignedShortSerializer;
            pStr = unsignedShortSerializer.serialize(pValue);
        }
        break;
    case XSD_BYTE:
        {
            Byte byteSerializer;
            pStr = byteSerializer.serialize(pValue);
        }
        break;
    case XSD_UNSIGNEDBYTE:
        {
            UnsignedByte unsignedByteSerializer;
            pStr = unsignedByteSerializer.serialize(pValue);
        }
        break;
    case XSD_LONG:
        {
            Long longSerializer;
            pStr = longSerializer.serialize(pValue);
        }
        break;
    case XSD_INTEGER:
        {
            Integer integerSerializer;
            pStr = integerSerializer.serialize(pValue);
        }
        break;
    case XSD_DURATION:
        {
            Duration durationSerializer;
            pStr = durationSerializer.serialize(pValue);
        }
        break;
    case XSD_UNSIGNEDLONG:
        {
            UnsignedLong unsignedLongSerializer;
            pStr = unsignedLongSerializer.serialize(pValue);
        }
        break;
    case XSD_FLOAT:
        {
            Float floatSerializer;
            pStr = floatSerializer.serialize(pValue);
        }
        break;
    case XSD_DOUBLE:
        {
            Double doubleSerializer;
            pStr = doubleSerializer.serialize(pValue);
        }
    case XSD_DECIMAL:
        {
            Decimal decimalSerializer;
            pStr = decimalSerializer.serialize(pValue);
        }
        break;
    case XSD_STRING:
        {
            String stringSerializer;
            pStr = stringSerializer.serialize(pValue);
        }
        break;
    case XSD_ANYURI:
        {
            AnyURI anyURISerializer;
            pStr = anyURISerializer.serialize(pValue);
        }
        break;
    case XSD_QNAME:
        {
            XSD_QName qNameSerializer;
            pStr = qNameSerializer.serialize(pValue);
        }
        break;
    case XSD_NOTATION:
        {
            NOTATION notationSerializer;
            pStr = notationSerializer.serialize(pValue);
        }
        break;
    case XSD_HEXBINARY:
        {
            HexBinary hexBinarySerializer;
            pStr = hexBinarySerializer.serialize(pValue);
        }
        break;
    case XSD_BASE64BINARY:
        {
            Base64Binary base64BinarySerializer;
            //pStr = base64BinarySerializer.serialize(pValue);
            strcpy(pStr, base64BinarySerializer.serialize(pValue));            
        }
        break;
    case XSD_DATETIME:
        {
            DateTime dateTimeSerializer;
            pStr = dateTimeSerializer.serialize(pValue);
        }
        break;
    case XSD_DATE:
        {
            Date dateSerializer;
            pStr = dateSerializer.serialize(pValue);
        }
        break;
    case XSD_TIME:
        {
            Time timeSerializer;
            pStr = timeSerializer.serialize(pValue);
        }
        break;
    default:;
    }
    *this << pStr;
    return AXIS_SUCCESS;
}

void SoapSerializer::serializeAttachments(SoapSerializer &pSZ)
{
	/*serializing the attachements*/

	map<AxisXMLString, ISoapAttachment*>::iterator itCurrAttach= m_SoapAttachments.begin();

	while(itCurrAttach != m_SoapAttachments.end())
    {        
        ((SoapAttachment*)((*itCurrAttach).second))->serialize(pSZ);

		pSZ.serialize("\n------=MIME BOUNDARY\n", NULL);

        itCurrAttach++;
    }
}

void SoapSerializer::addAttachment(const AxisChar* achId, ISoapAttachment* pAttach)
{
	m_SoapAttachments[achId] = pAttach;
}

void SoapSerializer::addAttachmentHeader(const AxisChar* achId, const AxisChar* achHeaderName, const AxisChar* achHeaderValue)
{

	if (m_SoapAttachments[achId] == NULL) {
		m_SoapAttachments[achId] = new SoapAttachment();		
	}

	m_SoapAttachments[achId]->addHeader(achHeaderName, achHeaderValue);
	
}

void SoapSerializer::addAttachmentBody(const AxisChar* achId, xsd__base64Binary *pAttchBody)
{
	if (m_SoapAttachments[achId] == NULL) {
		m_SoapAttachments[achId] = new SoapAttachment();		
	}

	m_SoapAttachments[achId]->addBody(pAttchBody);
}

void SoapSerializer::addNamespaceToEnvelope(AxisChar *pachNamespaceURI, AxisChar* pachPrefix)
{
	Attribute *pNameSpace = new Attribute(pachPrefix, "xmlns", pachNamespaceURI);

	m_pSoapEnvelope->addNamespaceDecl(pNameSpace);

	addNamespaceToNamespaceList(pachNamespaceURI, pachPrefix);

}

void SoapSerializer::addNamespaceToNamespaceList(const AxisChar *pachNamespaceURI, const AxisChar* pachPrefix)
{
	m_NsStack[pachNamespaceURI] = pachPrefix;
}

ISoapAttachment* SoapSerializer::createSoapAttachement()
{
	SoapAttachment* pSAttch = new SoapAttachment();

	return pSAttch;
}

bool SoapSerializer::checkAttachmentAvailability()
{
	map<AxisXMLString, ISoapAttachment*>::iterator itCurrAttach= m_SoapAttachments.begin();

	if(itCurrAttach != m_SoapAttachments.end())
		return true;
	else
		return false;

}

IHeaderBlock* SoapSerializer::getCurrentHeaderBlock()
{
	return m_pSoapEnvelope->m_pSoapHeader->getCurrentHeaderBlock();
}
AXIS_CPP_NAMESPACE_END