/* -*- C++ -*- */
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

/*
 * This is the Service Class genarated by the tool WSDL2Ws
 * AxisAdminServiceWrapper.h: interface for the AxisAdminServiceWrapperclass.
 */

#if !defined(__AXISADMINSERVICEWRAPPER_SERVERWRAPPER_H__INCLUDED_)
#define __AXISADMINSERVICEWRAPPER_SERVERWRAPPER_H__INCLUDED_

#include <axis/server/WrapperClassHandler.h>
#include <axis/server/IMessageData.h>
#include <axis/server/GDefine.h>
#include <axis/server/AxisWrapperAPI.h>

class AxisAdminServiceWrapper:public WrapperClassHandler
{
    public:
        AxisAdminServiceWrapper ();
    public:
        virtual ~ AxisAdminServiceWrapper ();
    public: /*implementation of WrapperClassHandler interface */
        int AXISCALL Invoke (void* pMsg);
        void AXISCALL OnFault (void* pMsg);
        int AXISCALL Init ();
        int AXISCALL Fini ();
        AXIS_BINDING_STYLE AXISCALL GetBindingStyle ()
        {
            return DOC_LITERAL;
        };
    private: /*Methods corresponding to the web service methods */
        int updateWSDD (void* pMsg);
};

#endif 
