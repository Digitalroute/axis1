/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
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

package org.apache.axis.providers.java ;

import java.util.* ;
import java.lang.reflect.* ;
import org.apache.axis.* ;
import org.apache.axis.utils.* ;
import org.apache.axis.utils.cache.* ;
import org.apache.axis.message.* ;

/**
 * Implement message processing by walking over RPCElements of the
 * envelope body, invoking the appropriate methods on the service object.
 *
 * @author Doug Davis (dug@us.ibm.com)
 */
public class RPCProvider extends JavaProvider {
    private static final boolean DEBUG_LOG = false;
    
    public void processMessage (MessageContext msgContext,
                                String clsName,
                                String methodName,
                                SOAPEnvelope reqEnv,
                                SOAPEnvelope resEnv,
                                JavaClass jc,
                                Object obj)
        throws Exception
    {
        Vector          bodies = reqEnv.getBodyElements();
        
        /* Loop over each entry in the SOAPBody - each one is a different */
        /* RPC call.                                                      */
        /******************************************************************/
        for ( int bNum = 0 ; bNum < bodies.size() ; bNum++ ) {
            if (!(bodies.get(bNum) instanceof RPCElement))
                continue;
            
            RPCElement   body  = (RPCElement) bodies.get( bNum );

            /* This breaks JWS.  With JWS, the service name is JWSProcessor,
               but the URN is the actual service name.  hmmmmm.
            
            // validate that the incoming targetService is the same as the
            // namespace URI of the body... if not, someone spoofed the
            // SOAPAction header (or equivalent)
            if (body.getNamespaceURI() != null
                && !body.getNamespaceURI().equals(msgContext.getTargetService()))
            {
                throw new AxisFault( "AxisServer.error",
                                    "Incoming target service name doesn't match body namespace URI\n" +
                                        "Target service name=" + msgContext.getTargetService() + "\n" +
                                        "Body URI=" + body.getNamespaceURI(),
                                    null, null );  // should they??
            }
             */
            
            
            String       mName      = body.getMethodName();
            Vector       args       = body.getParams();
            Object[]     argValues  =  null ;
            
            
            if ( args != null && args.size() > 0 ) {
                argValues = new Object[ args.size()];
                for ( int i = 0 ; i < args.size() ; i++ ) {
                    argValues[i]  = ((RPCParam)args.get(i)).getValue() ;
                    
                    if (DEBUG_LOG) {
                        System.out.println("  value: " + argValues[i] );
                    }
                }
            }
            
            if ( methodName != null && !methodName.equals(mName) )
                throw new AxisFault( "AxisServer.error",
                                    "Method names don't match\n" +
                                        "Body name=" + mName + "\n" +
                                        "Service name=" + methodName,
                                    null, null );  // should they??
            
            Debug.Print( 2, "mName: " + mName );
            Debug.Print( 2, "MethodName: " + methodName );
            Method       method = jc.getMethod(mName, args.size());

            if ( method == null ) {
              // If method wasn't found using the args that were passed 
              // in then try again, this time with MessageContext frist
              args.add( 0, msgContext );
              method = jc.getMethod(mName, args.size());
              if ( method != null ) {
                Object[] tmpArgs = new Object[args.size()];
                for ( int i = 1 ; i < args.size() ; i++ )
                  tmpArgs[i] = argValues[i-1];
                tmpArgs[0] = msgContext ;
                argValues = tmpArgs ;
              }
            }

            if ( method == null )
                throw new AxisFault( "AxisServer.error",
                                    "Method not found\n" +
                                        "Method name=" + methodName + "\n" +
                                        "Service name=" + msgContext.getTargetService(),
                                    null, null );
            
            Object objRes = method.invoke( obj, argValues );
            
            /* Now put the result in the result SOAPEnvelope */
            /*************************************************/
            RPCElement resBody = new RPCElement(mName + "Response");
            resBody.setPrefix( body.getPrefix() );
            resBody.setNamespaceURI( body.getNamespaceURI() );
            if ( objRes != null ) {
                RPCParam param = new RPCParam("return", objRes);
                resBody.addParam(param);
            }
            resEnv.addBodyElement( resBody );
        }
    }
}
