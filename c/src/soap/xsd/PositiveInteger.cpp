// Copyright 2003-2004 The Apache Software Foundation.
// (c) Copyright IBM Corp. 2004, 2005 All Rights Reserved
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//        http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

#include "PositiveInteger.hpp"

AXIS_CPP_NAMESPACE_START

PositiveInteger::PositiveInteger()
{}

PositiveInteger::PositiveInteger(const xsd__positiveInteger* value) throw (AxisSoapException)
{
    if (value)
    {
        if((*value) < 1) // the value must be one or greater, hence validate and fix
            throw AxisSoapException(CLIENT_SOAP_SOAP_CONTENT_ERROR, "PositiveInteger value must be one or greater");
        setNil(false);
        serialize(value);
    }
}

XSDTYPE PositiveInteger::getType()
{
    return XSD_POSITIVEINTEGER;
}

xsd__positiveInteger* PositiveInteger::getPositiveInteger()
{
    if (isNil())
    {
        return NULL;
    }
    else
    {
        return deserializePositiveInteger(m_Buf);
    }
}

void * PositiveInteger::getValue()
{
    return (void*) getPositiveInteger();
}

xsd__positiveInteger* PositiveInteger::deserializePositiveInteger(const AxisChar* valueAsChar) throw (AxisSoapException)
{
    return (xsd__positiveInteger*) deserializeNonNegativeInteger(valueAsChar);
}

MinInclusive* PositiveInteger::getMinInclusive()
{   
    return new MinInclusive((unsigned LONGLONG) 1);
}

AXIS_CPP_NAMESPACE_END
