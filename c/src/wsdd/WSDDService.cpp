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
 *
 *
 * @author Sanjaya Singharage
 * @author Susantha Kumara (skumara@virtusa.com, susantha@opensource.lk)
 *
 */

// WSDDService.cpp: implementation of the WSDDService class.
//
//////////////////////////////////////////////////////////////////////

#include <axis/server/WSDDService.h>
#include <axis/server/AxisTrace.h>

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

WSDDService::WSDDService()
{
	m_RequestHandlers = NULL;
	m_ResponseHandlers = NULL;
	m_Provider = UNKNOWN_PROVIDER;
}

WSDDService::~WSDDService()
{
	WSDDHandlerList::iterator iter;
	if(m_RequestHandlers)
	{
		for(iter=m_RequestHandlers->begin();iter!=m_RequestHandlers->end();iter++)
		{
			delete (*iter);
		}
	}

	if(m_ResponseHandlers)
	{
		for(iter=m_ResponseHandlers->begin();iter!=m_ResponseHandlers->end();iter++)
		{
			delete (*iter);
		}
	}
}

void WSDDService::SetProvider(const AxisChar* sProvider)
{
	if (0 == strcmp(sProvider, "CPP:RPC"))
	{
		m_Provider = CPP_RPC_PROVIDER;
	}
	else if (0 == strcmp(sProvider, "C:RPC"))
	{
		m_Provider = C_RPC_PROVIDER;
	}
	else if (0 == strcmp(sProvider, "CPP:DOCUMENT"))
	{
		m_Provider = CPP_DOC_PROVIDER;
	}
	else if (0 == strcmp(sProvider, "C:DOCUMENT"))
	{
		m_Provider = C_DOC_PROVIDER;
	}
	else if (0 == strcmp(sProvider, "MS:COM"))
	{
		m_Provider = COM_PROVIDER;
	}
	else
	{
		m_Provider = UNKNOWN_PROVIDER;
	}
}

PROVIDERTYPE WSDDService::GetProvider() const
{
	return m_Provider;
}

const AxisChar* WSDDService::GetServiceName() const
{
	return m_sName.c_str();
}

void WSDDService::AddAllowedMethod(const AxisChar* sMethodName)
{
	m_AllowedMethods.push_back(sMethodName);
}

bool WSDDService::IsAllowedMethod(const AxisChar* sServiceName) const
{
	list<AxisString>::const_iterator iter;
	for(iter = m_AllowedMethods.begin();iter != m_AllowedMethods.end();iter++)
	{
		if((*iter) == sServiceName)
		{
			return true;
		}
	}
	return false;
}

const WSDDHandlerList* WSDDService::GetRequestFlowHandlers() const
{
	return m_RequestHandlers;
}

const WSDDHandlerList* WSDDService::GetResponseFlowHandlers() const
{
	return m_ResponseHandlers;
}

void WSDDService::AddAllowedRole(const AxisChar* sRole)
{
	m_AllowedRoles.push_back(sRole);
}

const list<AxisString>& WSDDService::GetAllowedRoles()
{
	return m_AllowedRoles;
}

void  WSDDService::AddHandler(bool bRequestFlow, WSDDHandler* pHandler)
{
	if (bRequestFlow)
	{
        //AXISTRACE1("WSDDService::AddHandler");
		if (!m_RequestHandlers) m_RequestHandlers = new WSDDHandlerList;
		m_RequestHandlers->push_back(pHandler);
	}
	else
	{
		if (!m_ResponseHandlers) m_ResponseHandlers = new WSDDHandlerList;
		m_ResponseHandlers->push_back(pHandler);
	}
}

/**
 * This method removes the service specific handler of this service. But the entry
 * in WSDDDeployment's <LibId, LibName> is not removed because the same handler may
 * be in use by other services. 
 */
int WSDDService::RemoveHandler(bool bRequestFlow, WSDDHandler* pHandler)
{
	WSDDHandlerList* pTempList = bRequestFlow ? m_RequestHandlers : m_ResponseHandlers;
	if (!pTempList) return AXIS_NO_SUCH_HANDLER;
	for (WSDDHandlerList::iterator itr = pTempList->begin();
		 itr != pTempList->end(); itr++)
	{
		if (strcmp((*itr)->GetLibName(), pHandler->GetLibName()) == 0)
		{
			pTempList->remove(*itr);
			delete (*itr);
			delete pHandler;
			return AXIS_SUCCESS;
		}
	}
	return AXIS_NO_SUCH_HANDLER;
}

const list<AxisString> WSDDService::getAllowedMethods() const
{
	return m_AllowedMethods;
}

int WSDDService::UpdateWSDD(FILE* wsddfile, int tabcount)
{
	m_file = wsddfile;
	PrintTabs(tabcount); *this << "<service name=\"" << m_sName.c_str() << "\" provider=\"" << GetProviderString() << "\" description=\"" << m_sDescription.c_str() << "\">\n";
	if (m_Params)
	{
		map<AxisString, AxisString>::iterator itr;
		for (itr = m_Params->begin(); itr != m_Params->end(); itr++)
		{
			PrintTabs(tabcount+1); *this << "<parameter name=\"" << (*itr).first.c_str() << "\" value=\"" << (*itr).second.c_str() << "\" />";
		}
	}
	WSDDHandlerList::iterator iter;
	if(m_RequestHandlers)
	{
		*this << "\t\t<requestFlow>\n";
		for(iter=m_RequestHandlers->begin(); iter!=m_RequestHandlers->end(); iter++)
		{
			(*iter)->UpdateWSDD(wsddfile, 3);
		}
		*this << "\t\t</requestFlow>\n";
	}

	if(m_ResponseHandlers)
	{
		*this << "\t\t<responseFlow>\n";
		for(iter=m_ResponseHandlers->begin(); iter!=m_ResponseHandlers->end(); iter++)
		{
			(*iter)->UpdateWSDD(wsddfile, 3);
		}
		*this << "\t\t</responseFlow>\n";
	}
	PrintTabs(tabcount+1); *this << "<parameter name=\"allowedMethods\" value=\"";
	//for looop
	for (list<AxisString>::iterator itrs = m_AllowedMethods.begin(); itrs != m_AllowedMethods.end(); itrs++)
	{
		*this << (*itrs).c_str() << " ";
	}
	*this << "\"/>\n"; 
	PrintTabs(tabcount+1); *this << "<parameter name=\"className\" value=\"" << m_sLibName.c_str();
	*this << "\" />\n"; 
	PrintTabs(tabcount); *this << "</service>\n";
	m_file = 0;
	return AXIS_SUCCESS;
}

const char* WSDDService::GetProviderString()
{
	switch(m_Provider)
	{
	case C_RPC_PROVIDER: return "C:RPC";
	case C_DOC_PROVIDER: return "C:DOCUMENT";
	case CPP_RPC_PROVIDER: return "CPP:RPC";
	case CPP_DOC_PROVIDER: return "CPP:DOCUMENT";
	case COM_PROVIDER: return "COM";
	default: return "unknown";
	}
	return 0;
}
