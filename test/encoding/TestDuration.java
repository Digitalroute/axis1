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
package test.encoding;

import junit.framework.TestCase;
import org.apache.axis.types.Duration;

public class TestDuration extends TestCase
  {

  public TestDuration( String name )
    {
    super( name );
    }

    
  public void testDurations()
    throws Exception
    {
    // invoke the web service as if it was a local java object
    String[] durationStrings = new String[ 11 ];
    durationStrings[ 0 ] = "P2Y3M8DT8H1M3.3S";
    durationStrings[ 1 ] = "P2Y3M8DT8H1M3S";
    durationStrings[ 2 ] = "PT8H1M3.3S";
    durationStrings[ 3 ] = "P2Y3M8D";
    durationStrings[ 4 ] = "P2YT8H";
    durationStrings[ 5 ] = "P8DT3.3S";
    durationStrings[ 6 ] = "P3MT1M";
    durationStrings[ 7 ] = "PT0.3S";
    durationStrings[ 8 ] = "P1M";
    durationStrings[ 9 ] = "-P1M";
    durationStrings[ 10 ] = "-P2Y3M8DT8H1M3.3S";

    for( int i = 0; i < durationStrings.length; i++ )
      {
      String durationString = durationStrings[ i ];
      org.apache.axis.types.Duration duration = 
              new org.apache.axis.types.Duration( durationString );

      assertTrue( "Duration string \"" + durationString + 
                  "\" not equal to returned: " + duration.toString(), 
                  durationString.equals( duration.toString() ) );
      }
    }
  }
