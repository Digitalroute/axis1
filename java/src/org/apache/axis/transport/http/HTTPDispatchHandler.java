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

package org.apache.axis.transport.http;

import java.io.* ;
import java.net.* ;
import java.util.* ;
import java.lang.reflect.*;

import org.apache.axis.* ;
import org.apache.axis.utils.* ;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeader;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.transport.http.NonBlockingBufferedInputStream;
import org.apache.axis.encoding.Base64 ;

import org.w3c.dom.* ;

/**
 * This is meant to be used on a SOAP Client to call a SOAP server.
 *
 * @author Doug Davis (dug@us.ibm.com)
 */
public class HTTPDispatchHandler extends BasicHandler {
  public void invoke(MessageContext msgContext) throws AxisFault {
    Debug.Print( 1, "Enter: HTTPDispatchHandler::invoke" );
    /* Find the service we're invoking so we can grab it's options */
    /***************************************************************/
    String   targetURL = null ;
    Message  outMsg    = null ;
    String   reqEnv    = null ;

    targetURL = msgContext.getStrProp( MessageContext.TRANS_URL);
    try {
      String   host ;
      int      port   = 80 ;
      String   action = msgContext.getStrProp(HTTPConstants.MC_HTTP_SOAPACTION);
      URL      tmpURL = new URL( targetURL );
      byte[]   buf    = new byte[4097];
      int      rc     = 0 ;

      host = tmpURL.getHost();
      if ( (port = tmpURL.getPort()) == -1 ) port = 80;

      Socket             sock = null ;

      if (tmpURL.getProtocol().equalsIgnoreCase("https")) {
        if ( (port = tmpURL.getPort()) == -1 ) port = 443;
        String tunnelHost = System.getProperty("https.proxyHost");
        String tunnelPortString = System.getProperty("https.proxyPort");
        String tunnelUsername = System.getProperty("https.proxyUsername");
        String tunnelPassword = System.getProperty("https.proxyPassword");
        try {
          Class SSLSocketFactoryClass =
            Class.forName("javax.net.ssl.SSLSocketFactory");
          Class SSLSocketClass = Class.forName("javax.net.ssl.SSLSocket");
          Method createSocketMethod =
            SSLSocketFactoryClass.getMethod("createSocket",
                                            new Class[] {String.class, Integer.TYPE});
          Method getDefaultMethod =
            SSLSocketFactoryClass.getMethod("getDefault", new Class[] {});
          Method startHandshakeMethod =
            SSLSocketClass.getMethod("startHandshake", new Class[] {});
          Object factory = getDefaultMethod.invoke(null, new Object[] {});
          Object sslSocket = null;
          if (tunnelHost == null || tunnelHost.equals("")) {
            // direct SSL connection
            sslSocket = createSocketMethod .invoke(factory,
                                 new Object[] {host, new Integer(port)});
          } else {
            // SSL tunnelling through proxy server
            Method createSocketMethod2 =
              SSLSocketFactoryClass.getMethod("createSocket",
                                              new Class[] {Socket.class, String.class, Integer.TYPE, Boolean.TYPE});
            int tunnelPort = (tunnelPortString != null? (Integer.parseInt(tunnelPortString) < 0? 443: Integer.parseInt(tunnelPortString)): 443);
            Object tunnel = createSocketMethod .invoke(factory,
                                 new Object[] {tunnelHost, new Integer(tunnelPort)});
            // The tunnel handshake method (condensed and made reflexive)
            OutputStream tunnelOutputStream = (OutputStream)SSLSocketClass.getMethod("getOutputStream", new Class[] {}).invoke(tunnel, new Object[] {});
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(tunnelOutputStream)));
            out.print("CONNECT " + host + ":" + port + " HTTP/1.0\n\r\n\r");
            out.flush();
            InputStream tunnelInputStream = (InputStream)SSLSocketClass.getMethod("getInputStream", new Class[] {}).invoke(tunnel, new Object[] {});
            //BufferedReader in = new BufferedReader(new InputStreamReader(tunnelInputStream));
            //DataInputStream in = new DataInputStream(tunnelInputStream);
            Debug.Print(1, "Is tunnelInputStream null? " + String.valueOf(tunnelInputStream == null));
            String replyStr = ""; int i;
            while ((i = tunnelInputStream.read()) != '\n' && i != '\r' && i != -1) { replyStr += String.valueOf((char)i); Debug.Print(1, "got a character in reply, so far: " + replyStr); }
            if (!replyStr.startsWith("HTTP/1.0 200") && !replyStr.startsWith("HTTP/1.1 200")) {
              throw new IOException("Unable to tunnel through " + tunnelHost + ":" + tunnelPort + ".  Proxy returns \"" + replyStr + "\"");
            }
            // End of condensed reflective tunnel handshake method
            sslSocket = createSocketMethod2.invoke(factory,
                                 new Object[] {tunnel, host, new Integer(port), new Boolean(true)});
            Debug.Print( 1, "Set up SSL tunnelling through " + tunnelHost + ":" +tunnelPort);
          }
          // must shake out hidden errors!
          startHandshakeMethod.invoke(sslSocket, new Object[] {});
          sock = (Socket)sslSocket;
        } catch (ClassNotFoundException cnfe) {
          Debug.Print( 1, "SSL feature disallowed: JSSE files not installed or present in classpath");
          throw new AxisFault(cnfe);
        } catch (NumberFormatException nfe) {
          Debug.Print( 1, "Proxy port number, \"" + tunnelPortString + "\", incorrectly formatted");
          throw new AxisFault(nfe);
        }
        Debug.Print( 1, "Created an SSL connection");
      } else {
        if ((port = tmpURL.getPort()) == -1 ) port = 80;
        sock    = new Socket( host, port );
        Debug.Print( 1, "Created an insecure HTTP connection");
      }

      reqEnv  = (String) msgContext.getRequestMessage().getAs("String");
      
      //System.out.println("Msg: " + reqEnv);

      BufferedInputStream inp = new BufferedInputStream(sock.getInputStream());
      OutputStream  out  = sock.getOutputStream();
      StringBuffer  otherHeaders = new StringBuffer();
      String        userID = null ;
      String        passwd = null ;

      userID = msgContext.getStrProp( MessageContext.USERID );
      passwd = msgContext.getStrProp( MessageContext.PASSWORD );

      if ( userID != null ) {
        StringBuffer tmpBuf = new StringBuffer();
        tmpBuf.append( userID )
              .append( ":" )
              .append( (passwd == null) ? "" : passwd) ;
        otherHeaders.append( HTTPConstants.HEADER_AUTHORIZATION )
                    .append( ": Basic " )
                    .append( Base64.encode( tmpBuf.toString().getBytes() ) )
                    .append("\n" );
      }
     
      StringBuffer header = new StringBuffer();

      header.append( HTTPConstants.HEADER_POST )
            .append(" " )
            .append( ((tmpURL.getFile() == null || 
                       tmpURL.getFile().equals(""))? "/": tmpURL.getFile()) )
            .append( " HTTP/1.0\r\n" )
            .append( HTTPConstants.HEADER_CONTENT_LENGTH )
            .append( ": " )
            .append(reqEnv.length() )
            .append( "\r\n" )
            .append( HTTPConstants.HEADER_CONTENT_TYPE )
            .append( ": text/xml\r\n" )
            .append( (otherHeaders == null ? "" : otherHeaders.toString()) )
            .append( HTTPConstants.HEADER_SOAP_ACTION )
            .append( ": \"" )
            .append( action )
            .append( "\"\r\n\r\n" );

      out.write( header.toString().getBytes() );
      out.write( reqEnv.getBytes() );

      Debug.Print( 1, "XML sent:" );
      Debug.Print( 1, "---------------------------------------------------");
      Debug.Print( 1, header + reqEnv );

      byte       lastB=0, b ;
      int        len = 0 ;
      int        colonIndex = -1 ;
      Hashtable  headers = new Hashtable();
      String     name, value ;

      // Need to add logic for getting the version # and the return code
      // but that's for tomorrow!

      for ( ;; ) {
        if ( (b = (byte) inp.read()) == -1 ) break ;
        if ( b != '\r' && b != '\n' ) {
          if ( b == ':' ) colonIndex = len ;
          lastB = (buf[len++] = b);
        }
        else if ( b == '\r' )
          continue ;
        else {
          if ( len == 0 ) break ;
          if ( colonIndex != -1 ) {
            name = new String( buf, 0, colonIndex );
            value = new String( buf, colonIndex+1, len-1-colonIndex );
          }
          else {
            name = new String( buf, 0, len );
            value = "" ;
          }
          Debug.Print( 2, name + value );
          if ( msgContext.getProperty(HTTPConstants.MC_HTTP_STATUS_CODE) == null ) {
            // Reader status code
            int start = name.indexOf( ' ' ) + 1 ;
            int end   = name.indexOf( ' ', start ) ;
            rc = Integer.parseInt( name.substring(start, end) );
            msgContext.setProperty( HTTPConstants.MC_HTTP_STATUS_CODE,
                                    new Integer(rc) );
            msgContext.setProperty( HTTPConstants.MC_HTTP_STATUS_MESSAGE,
                                    name.substring(end+1));
          }
          else
            headers.put( name.toLowerCase(), value );
          len = 0 ;
        }
      }

      if ( b != -1 && Debug.getDebugLevel() > 8 ) {
        // Special case - if the debug level is this high then something
        // really bad must be going on - so just dump the input stream
        // to stdout.
        while ( (b = (byte) inp.read()) != -1 )
          System.err.print((char)b);
        System.err.println("");
      }

      if ( b != -1 ) {

        if (Debug.getDebugLevel() > 0) {
          String contentLength = (String) headers.get("content-length");
          byte[] data = new byte[Integer.parseInt(contentLength)];
          for (len=0; len<data.length; )
            len+= inp.read(data,len,data.length-len);
          String xml = new String(data);

          outMsg = new Message( data, "String" );

          Debug.Print( 1, "\nXML received:" );
          Debug.Print( 1, "-------------------------------------------------");
          Debug.Print( 1, xml );
        } else {
          outMsg = new Message( inp, "InputStream" );
        }

        msgContext.setResponseMessage( outMsg );
      }
      inp.close();
      out.close();
      sock.close();
    }
    catch( Exception e ) {
      Debug.Print( 1, e );
      e.printStackTrace();
      if ( !(e instanceof AxisFault) ) e = new AxisFault(e);
      throw (AxisFault) e ;
    }
    Debug.Print( 1, "Exit: HTTPDispatchHandler::invoke" );
  }

  public void undo(MessageContext msgContext) {
    Debug.Print( 1, "Enter: HTTPDispatchHandler::undo" );
    Debug.Print( 1, "Exit: HTTPDispatchHandler::undo" );
  }
};
