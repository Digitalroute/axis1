/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2004 The Apache Software Foundation.  All rights
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

package org.apache.geronimo.ews.ws4j2ee.parsers;
/**
 * <p>This denotes the Exception occured at the code genaration.
 * There is a isssue of wrapping the Exception such that JDK1.3 compatibility.
 * This code write same way as it was done at the RemoteException.</p>
 */
public class ParserFault extends Exception {
	/**
	 * Nested Exception to hold wrapped exception.
	 *
	 * <p>This field predates the general-purpose exception chaining facility.
	 * The {@link Throwable#getCause()} method is now the preferred means of
	 * obtaining this information.</p>
	 *
	 * @serial
	 */
	public Throwable detail;

	public ParserFault(Exception e){
		initCause(null);  // Disallow subsequent initCause
		detail = e;
	}
	
	public ParserFault(String message){
		super(message);
	}

	/**
	 * Constructs a <code>Exception</code> with the specified
	 * detail message and nested exception.
	 *
	 * @param s the detail message
	 * @param ex the nested exception
	 */
	public ParserFault(String s, Throwable ex) {
	super(s);
		initCause(null);  // Disallow subsequent initCause
	detail = ex;
	}

	/**
	 * Returns the detail message, including the message from the nested
	 * exception if there is one.
	 * 
	 * @return	the detail message, including nested exception message if any
	 */
	public String getMessage() {
	if (detail == null) {
		return super.getMessage();
	} else {
		return super.getMessage() + "; nested exception is: \n\t" +
		detail.toString();
	}
	}

	/**
	 * Returns the wrapped exception (the <i>cause</i>).
	 *
	 * @return  the wrapped exception, which may be <tt>null</tt>.
	 */
	public Throwable getCause() {
		return detail;
	}
}
