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
 
#ifndef __OTHERFAULTEXCEPTION_H_OF_AXIS_INCLUDED_
#define __OTHERFAULTEXCEPTION_H_OF_AXIS_INCLUDED_

#include <axis/GDefine.hpp>
#include <axis/SoapFaultException.hpp>

AXIS_CPP_NAMESPACE_START

/**
 * @class OtherFaultException
 * @brief A server-generated soap fault exception
 * 
 * An exception thrown back to a client application that represents a soap
 * fault generated by the server.
 * 
 * @author Mark Whitlock
 */

class STORAGE_CLASS_INFO OtherFaultException : public SoapFaultException
{
public:
	OtherFaultException();
	OtherFaultException(const AxisChar *code, const AxisChar *string, 
		const AxisChar *actor, const AxisChar *detail, int exceptionCode);
	OtherFaultException(AxisException& ae);
	OtherFaultException(const OtherFaultException& copy);
	virtual OtherFaultException& operator=(OtherFaultException other);
	virtual ~OtherFaultException() throw();

	virtual const AxisChar *getFaultDetail() const;
	virtual void setFaultDetail(const AxisChar *detail);
private:
	AxisChar *m_detail;
};

AXIS_CPP_NAMESPACE_END

#endif
