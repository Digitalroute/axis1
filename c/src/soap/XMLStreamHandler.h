/* -*- C++ -*- */

/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "SOAP" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 *
 */
#ifdef WIN32
#pragma warning (disable : 4786)
#endif

#if !defined(__XMLSTREAMHANDLER_H_INCLUDED__)
#define __XMLSTREAMHANDLER_H_INCLUDED__

#include <xercesc/sax2/DefaultHandler.hpp>
#include <xercesc/sax2/SAX2XMLReader.hpp>
#include <xercesc/sax2/XMLReaderFactory.hpp>
#include <xercesc/util/PlatformUtils.hpp>
#include <xercesc/framework/MemBufInputSource.hpp>

#include <axis/server/Param.h>
#include "../common/ArrayBean.h"
#include "SoapEnvelope.h"
#include "SoapHeader.h"
#include "SoapMethod.h"
#include "SoapBody.h"
#include "SoapFault.h"
#include <axis/server/GDefine.h>
#include "SoapKeywordMapping.h"

#define TRANSCODE_BUFFER_SIZE 256

#include <map>
#include <list>
#include <string>

using namespace std;


XERCES_CPP_NAMESPACE_USE

enum SOAP_PARSE_LEVEL { SOAP_UNKNOWN=1, SOAP_ENVELOP, SOAP_HEADER, SOAP_FAULT, SOAP_BODY, SOAP_METHOD, SOAP_PARAM, SOAP_HEADER_BLOCK};
/**
    @class XMLStreamHandler
    @brief Header file containing definitions for the Axis Engineclasses


    @author Susantha Kumara (skumara@virtusa.com)
*/
class XMLStreamHandler:public DefaultHandler
{
	friend class SoapDeSerializer;
private:
	SoapEnvelope* m_pEnv;
	SoapHeader* m_pHead;
	SoapBody* m_pBody;
	SoapFault* m_pFault;
	SoapMethod* m_pMethod;
	//After parsing all params will be in this flat list(even nested params and arrays)
	list<Param*> m_Params;
	list<Param*>::iterator m_it;
	map<AxisXMLString, AxisXMLString> m_NsStack;
	SOAP_VERSION m_nSoapVersion;
	AxisChar m_Buffer[TRANSCODE_BUFFER_SIZE]; //used to transcode XMLCh to AxisChar
private:
	Param* GetParam();
	int Success();
	Param m_Param; //Param to which each parameter data is extracted
	ArrayBean m_ArrayBean;
	ComplexObjectHandler m_CplxObj;
	int m_Success;
	SOAP_PARSE_LEVEL m_PL0;
	SOAP_PARSE_LEVEL m_PL1;
	SOAP_PARSE_LEVEL m_PL2;
	AxisXMLString m_sLastElement;
	HeaderBlock* m_pHeaderBlock;
private:
	const AxisChar* __XTRC(const XMLCh* pChar);
	void createHeaderBlock(const XMLCh *const uri,const XMLCh *const localname,const XMLCh *const qname,const Attributes &attrs);
	int m_nParamNestingLevel;
	int SetArrayDimensions(AxisXMLString& sDimensions);
	void FillMethod(const XMLCh *const uri,const XMLCh *const localname,const XMLCh *const qname,const Attributes &attrs);
	void FillFault(const XMLCh *const uri,const XMLCh *const localname,const XMLCh *const qname,const Attributes &attrs);
	void FillHeader(const XMLCh *const uri,const XMLCh *const localname,const XMLCh *const qname,const Attributes &attrs);
	void FillBody(const XMLCh *const uri,const XMLCh *const localname,const XMLCh *const qname,const Attributes &attrs);
	void FillEnvelope(const XMLCh *const uri,const XMLCh *const localname,const XMLCh *const qname,const Attributes &attrs);
	void Init();
	XMLStreamHandler();
	~XMLStreamHandler();
//	void SetMessageObject(MessageData* pMsg);
//	void ResetMessageObject();
    // -----------------------------------------------------------------------
    //  Implementations of the SAX DocumentHandler interface
    // -----------------------------------------------------------------------
	void startElement(const XMLCh *const uri,const XMLCh *const localname,const XMLCh *const qname,const Attributes &attrs);
	void characters (const XMLCh *const chars,const unsigned int length);
	void endElement (const XMLCh *const uri,const XMLCh *const localname,const XMLCh *const qname);
	void startPrefixMapping(const XMLCh* const prefix, const XMLCh* const uri);
	void endPrefixMapping(const XMLCh* const prefix);

	// -----------------------------------------------------------------------
    //  Implementations of the SAX ErrorHandler interface
    // -----------------------------------------------------------------------
    void warning(const SAXParseException& exception);
    void error(const SAXParseException& exception);
    void fatalError(const SAXParseException& exception);
	void SetParamType(const Attributes &attrs);
};

#endif
