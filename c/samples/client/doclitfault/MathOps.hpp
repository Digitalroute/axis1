/*
 * This file was auto-generated by the Axis C++ Web Service Generator (WSDL2Ws)
 * This file contains Client Stub Class for remote web service 
 */

#if !defined(__MATHOPS_CLIENTSTUB_H__INCLUDED_)
#define __MATHOPS_CLIENTSTUB_H__INCLUDED_

#include <axis/client/Stub.hpp>
#include <axis/SoapFaultException.hpp>
#include <axis/ISoapFault.hpp>
AXIS_CPP_NAMESPACE_USE
#include "DivByZeroStruct.hpp"

class MathOps :public Stub
{
public:
	STORAGE_CLASS_INFO MathOps(const char* pchEndpointUri, AXIS_PROTOCOL_TYPE eProtocol=APTHTTP1_1);
	STORAGE_CLASS_INFO MathOps();
public:
	STORAGE_CLASS_INFO virtual ~MathOps();
public: 
	STORAGE_CLASS_INFO void SetSecure( char *, ...);
	STORAGE_CLASS_INFO xsd__int div(xsd__int Value0,xsd__int Value1);

private:
	void includeSecure();

protected:
	std::string sArguments[8];
};

#endif /* !defined(__MATHOPS_CLIENTSTUB_H__INCLUDED_)*/
