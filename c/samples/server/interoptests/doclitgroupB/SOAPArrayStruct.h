///////////////////////////////////////////////////////////////////////////////////
//This file is automatically generated by the Axis C++ Wrapper Class Generator
//Web service wrapper class's implementation generated by Axis WCG
//Parameters and wrapper methos to manipulate SOAPArrayStruct
////////////////////////////////////////////////////////////////////////////////////

#if !defined(__SOAPARRAYSTRUCT_PARAM_H__OF_AXIS_INCLUDED_)
#define __SOAPARRAYSTRUCT_PARAM_H__OF_AXIS_INCLUDED_

#include <axis/server/AxisUserAPI.hpp>

#include "ArrayOfstring.h"

AXIS_CPP_NAMESPACE_USE

//Local name and the URI for the type
static const char* Axis_URI_SOAPArrayStruct = "http://soapinterop.org/xsd";
static const char* Axis_TypeName_SOAPArrayStruct = "SOAPArrayStruct";

class SOAPArrayStruct
{
public:
	AxisChar* varString;
	int varInt;
	float varFloat;
	ArrayOfstring varArray;
	SOAPArrayStruct();
	~SOAPArrayStruct();
};

#endif // !defined(__SOAPARRAYSTRUCT_PARAM_H__OF_AXIS_INCLUDED_)
