/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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
 * 4. The names "Axis" and "Apache Software Foundation" must
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
 */

package test.dynamic;

import junit.framework.TestCase;
import org.apache.axis.AxisFault;
import samples.client.DynamicInvoker;

import java.io.InterruptedIOException;
import java.net.ConnectException;

public class TestDynamicInvoker extends TestCase {
    public TestDynamicInvoker(String name) {
        super(name);
    } // ctor

    public void test1() throws Exception {
        try {
            String[] args = new String[]{"http://www.xmethods.net/sd/2001/TemperatureService.wsdl", "getTemp", "02067"};
            DynamicInvoker.main(args);
        }  catch (java.net.ConnectException ce) {
            System.err.println("getTemp connect error: " + ce);
            return;
        }  catch (java.rmi.RemoteException re) {
            if (re instanceof AxisFault) {
                AxisFault fault = (AxisFault) re;
                if (fault.detail instanceof ConnectException ||
                    fault.detail instanceof InterruptedIOException ||
                    (fault.getFaultString().indexOf("Connection timed out") != -1) ||
                    fault.getFaultCode().getLocalPart().equals("HTTP")) {
                    System.err.println("getTemp HTTP error: " + fault);
                    return;
                }
            }
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }  catch (java.io.IOException ioe) {
            System.err.println("getTemp connect error: " + ioe);
            return;
        }
    }

    public void test2() throws Exception {
        try {
            String[] args = new String[]{"http://services.xmethods.net/soap/urn:xmethods-delayed-quotes.wsdl", "getQuote", "IBM"};
            DynamicInvoker.main(args);
        }  catch (java.net.ConnectException ce) {
            System.err.println("getQuote connect error: " + ce);
            return;
        }  catch (java.rmi.RemoteException re) {
            if (re instanceof AxisFault) {
                AxisFault fault = (AxisFault) re;
                if (fault.detail instanceof ConnectException ||
                    fault.detail instanceof InterruptedIOException ||
                    fault.getFaultCode().getLocalPart().equals("HTTP")) {
                    System.err.println("getQuote HTTP error: " + fault);
                    return;
                }
            }
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }  catch (java.io.IOException ioe) {
            System.err.println("getQuote connect error: " + ioe);
            return;
        }
    }

    public void test3() throws Exception {
        try {
            String[] args = new String[]{"http://mssoapinterop.org/asmx/xsd/round4XSD.wsdl", "echoString(Round4XSDTestSoap)", "Hello World!!!"};
            DynamicInvoker.main(args);
        }  catch (java.net.ConnectException ce) {
            System.err.println("round4XSD connect error: " + ce);
            return;
        }  catch (java.rmi.RemoteException re) {
            if (re instanceof AxisFault) {
                AxisFault fault = (AxisFault) re;
                if (fault.detail instanceof ConnectException ||
                    fault.detail instanceof InterruptedIOException ||
                    fault.getFaultCode().getLocalPart().equals("HTTP")) {
                    System.err.println("round4XSD HTTP error: " + fault);
                    return;
                }
            }
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }  catch (java.io.IOException ioe) {
            System.err.println("round4XSD connect error: " + ioe);
            return;
        }
    }

    public void test4() throws Exception {
        try {
            String[] args = new String[]{"http://samples.gotdotnet.com/quickstart/aspplus/samples/services/MathService/VB/MathService.asmx?WSDL", 
                                        "Add", 
                                        "3", 
                                        "4"};
            DynamicInvoker.main(args);
        }  catch (java.net.ConnectException ce) {
            System.err.println("MathService connect error: " + ce);
            return;
        }  catch (java.rmi.RemoteException re) {
            if (re instanceof AxisFault) {
                AxisFault fault = (AxisFault) re;
                if (fault.detail instanceof ConnectException ||
                    fault.detail instanceof InterruptedIOException ||
                    fault.getFaultCode().getLocalPart().equals("HTTP")) {
                    System.err.println("MathService HTTP error: " + fault);
                    return;
                }
            }
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }  catch (java.io.IOException ioe) {
            System.err.println("MathService connect error: " + ioe);
            return;
        }
    }
}
