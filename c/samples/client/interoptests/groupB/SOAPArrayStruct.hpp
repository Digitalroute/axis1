/*
 * This file was auto-generated by the Axis C++ Web Service Generator (WSDL2Ws)
 * This file contains functions to manipulate complex type SOAPArrayStruct
 */

#if !defined(__SOAPARRAYSTRUCT_PARAM_H__INCLUDED_)
#define __SOAPARRAYSTRUCT_PARAM_H__INCLUDED_

#include <axis/AxisUserAPI.hpp>
AXIS_CPP_NAMESPACE_USE 

/*Local name and the URI for the type*/
static const char* Axis_URI_SOAPArrayStruct = "http://soapinterop.org/xsd";
static const char* Axis_TypeName_SOAPArrayStruct = "SOAPArrayStruct";

class SOAPArrayStruct
{
public:
	xsd__string varString;
	xsd__int varInt;
	xsd__float varFloat;
	xsd__string_Array varArray;
	SOAPArrayStruct();
	virtual ~SOAPArrayStruct();
};

#endif /* !defined(__SOAPARRAYSTRUCT_PARAM_H__INCLUDED_)*/
