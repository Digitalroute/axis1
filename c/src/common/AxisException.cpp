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
 * @author  Damitha Kumarage (damitha@opensource.lk, damitha@jkcsworld.com)
 *
 */

#include "AxisException.h"

AxisException::AxisException(int exceptionCode)
{
  processException(exceptionCode);
}

AxisException::AxisException(exception* e)
{
  processException(e);
}

AxisException::AxisException(exception* e, int exceptionCode)
{
  processException(e);
}

void AxisException::processException(exception* e, int exceptionCode)
{
  m_sMessage = getMessage(e) + getMessage(exceptionCode);
}

void AxisException::processException(exception* e)
{
  m_sMessage = getMessage(e);
}

void AxisException::processException(int exceptionCode)
{
  m_sMessage = getMessage(exceptionCode);
}

string AxisException::getMessage(exception* e)
{
  string sMessage;
  exception *objType = static_cast<bad_alloc*> (e);
  if(objType != NULL)
  {
    //cout << "bad_alloc" << endl;
    sMessage = "thrown by new";
  }

  objType = static_cast<bad_cast*> (e);
  if(objType != NULL)
  {
    //cout << "bad_cast" << endl;
    sMessage = "thrown by dynamic_cast when fails with a referenced type";
  }

  objType = static_cast<bad_exception*> (e);
  if(objType != NULL)
  {
    //cout << "bad_exception" << endl;
    sMessage = "thrown when an exception doesn't match any catch";
  }

  objType = static_cast<bad_typeid*> (e);
  if(objType != NULL)
  {
    //cout << "bad_typeid" << endl;
    sMessage = "thrown by typeid";
  }

   return sMessage;
}

string AxisException::getMessage(int e)
{
  string sMessage;
  if(e == 1)
  sMessage = "AXIS_TEST_ERROR";
  //cout <<  "AXIS_TEST_ERROR" << endl;

  return sMessage;

}

AxisException::~AxisException() throw()
{

}

const char* AxisException::what() const throw()
{
  return m_sMessage.c_str();
}

