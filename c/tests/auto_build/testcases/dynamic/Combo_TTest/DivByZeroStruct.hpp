/*
 * Copyright 2003-2004 The Apache Software Foundation.

 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This file was auto-generated by the Axis C++ Web Service Generator (WSDL2Ws)
 * This file contains functions to manipulate complex type DivByZeroStruct
 */

#if !defined(__DIVBYZEROSTRUCT_PARAM_H__INCLUDED_)
#define __DIVBYZEROSTRUCT_PARAM_H__INCLUDED_

#include <axis/AxisUserAPI.hpp>
#include <axis/AxisUserAPIArrays.hpp>
#include <axis/SoapFaultException.hpp>
using namespace std;
AXIS_CPP_NAMESPACE_USE 

/*Local name and the URI for the type*/
static const char* Axis_URI_DivByZeroStruct = "http://soapinterop.org";
static const char* Axis_TypeName_DivByZeroStruct = "DivByZeroStruct";

class STORAGE_CLASS_INFO DivByZeroStruct : public SoapFaultException
{
public:
	xsd__string varString;
	xsd__int varInt;
	xsd__float varFloat;

	xsd__string getvarString();
	void setvarString(xsd__string InValue);

	xsd__int getvarInt();
	void setvarInt(xsd__int InValue);

	xsd__float getvarFloat();
	void setvarFloat(xsd__float InValue);

	DivByZeroStruct();

	void reset();
	virtual ~DivByZeroStruct() throw();
};

#endif /* !defined(__DIVBYZEROSTRUCT_PARAM_H__INCLUDED_)*/
