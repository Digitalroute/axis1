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

/* SoapBinInputStream.h: interface for the SoapBinInputStream class. */

#if !defined(AXIS_SOAPBININPUTSTREAM_H__OF_AXIS_INCLUDED_)
#define AXIS_SOAPBININPUTSTREAM_H__OF_AXIS_INCLUDED_

#include <xercesc/util/BinInputStream.hpp>
#include <axis/server/Packet.h>

XERCES_CPP_NAMESPACE_USE

class SoapBinInputStream : public BinInputStream
{
private:
    AXIS_MODULE_CALLBACK_GET_MESSAGE_BYTES m_pReadFunct;
    unsigned int m_nByteCount;
    const void* m_pContext;
public:
    SoapBinInputStream(AXIS_MODULE_CALLBACK_GET_MESSAGE_BYTES pReadFunct, const void* pContext);
    virtual ~SoapBinInputStream();
    unsigned int curPos() const;
    unsigned int readBytes(XMLByte* const toFill, const unsigned int maxToRead);
};

#endif
