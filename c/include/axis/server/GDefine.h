/* -*- C++ -*- */

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
 * @author Susantha Kumara (skumara@virtusa.com)
 *
 */
#ifdef WIN32
#pragma warning (disable : 4786)
#pragma warning (disable : 4530)
#endif

#if !defined(__GDEFINE_INCLUDED__)
#define __GDEFINE_INCLUDED__
/* This file contains all global definitions that are valid across whole Axis C++ project.*/

typedef enum { SUCCESS=0, FAIL = -1, OBJECT_ALREADY_EXISTS=1} AXIS_GLOBAL_ERROR;
typedef enum { APTHTTP=1, APTFTP, APTSMTP, APTOTHER } AXIS_PROTOCOL_TYPE;
typedef enum { CRITICAL=1, WARN, INFO, TRIVIAL} AXIS_SEVERITY_LEVEL;

#define SOAPACTIONHEADER "SOAPAction"

#define AxisChar char //Charactor used in Axis
#define AxisString basic_string<char> //String used in Axis

#define AxisXMLCh char //Xerces uses 16 bit char always.
#define AxisXMLString basic_string<AxisXMLCh>

#ifdef WIN32
    #define AxisSprintf(X, Y, Z, W) sprintf(X, Z, W)
#else /*linux*/
    #define AxisSprintf(X, Y, Z, W) sprintf(X, Z, W)
#endif

extern void Ax_Sleep(int);
extern void ModuleInitialize();
extern void ModuleUnInitialize();

#endif /*__GDEFINE_INCLUDED__*/
