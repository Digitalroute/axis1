/*
 * This file was auto-generated by the Axis C++ Web Service Generator (WSDL2Ws)
 * This file contains functions to manipulate complex type SOAPStructStruct
 */

#if !defined(__SOAPSTRUCTSTRUCT_H__INCLUDED_)
#define __SOAPSTRUCTSTRUCT_H__INCLUDED_

#include <axis/server/AxisUserAPI.h>

#include "SOAPStruct.h"
/*Local name and the URI for the type*/
static const char* Axis_URI_SOAPStructStruct = "http://soapinterop.org/xsd";
static const char* Axis_TypeName_SOAPStructStruct = "SOAPStructStruct";

typedef struct SOAPStructStructTag {
	xsd__string varString;
	int varInt;
	float varFloat;
	SOAPStruct* varStruct;
} SOAPStructStruct;

#endif /* !defined(__SOAPSTRUCTSTRUCT_H__INCLUDED_)*/