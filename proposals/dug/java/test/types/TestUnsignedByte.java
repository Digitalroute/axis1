/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package test.types;

import junit.framework.TestCase;
import org.apache.axis.types.UnsignedByte;

/**
 * Test validation of types.UnsignedByte
 */
public class TestUnsignedByte extends TestCase {

    public TestUnsignedByte(String name) {
        super(name);
    }

    /**
     * Run a failure test.  value should be invalid.
     */
    private void runFailTest(long value) throws Exception {
        UnsignedByte oUnsignedByte = null;
        try {
            oUnsignedByte = new UnsignedByte(value);
        }
        catch (Exception e) { // catch the validation exception
        }
        // object is not iNstantiated on bad data value
        assertNull("validation restriction failed [" +
                String.valueOf(value) + "]. did not restrict bad value.", oUnsignedByte);
    }

    /**
     * Run a successful test.  value should be valid.
     */
    private void runPassTest(long value) throws Exception {
        UnsignedByte oUnsignedByte = null;
        try {
            oUnsignedByte = new UnsignedByte(value);
        }
        catch (Exception e) { // catch the validation exception
        }
        assertEquals("unsigned byte not equal" +
                String.valueOf(value), oUnsignedByte.toString(), String.valueOf(value));
    }

    /**
     * Test that a positive value succeeeds
     */
    public void testPositiveValue() throws Exception {
        runPassTest(100);
    }

    /**
     * Test that a negative number fails
     */
    public void testNegativeValue() throws Exception {
        runFailTest(-100);
    }


    /**
    * Test that a number at MaxInclusive succeeds
    */
    public void testMaxInclusive() throws Exception {
      runPassTest(255);
    }

    /**
    * Test that a number over MaxInclusive fails
    */
    public void testMaxOver() throws Exception {
      runFailTest(256);
    }

    /**
    * Test that a number at MinInclusive succeeds
    */
    public void testMinExclusive() throws Exception {
       runPassTest(0L);
    }

    public void testCompareTo() throws Exception {
        UnsignedByte v1, v2;

        v1 = new UnsignedByte(100);
        v2 = new UnsignedByte(200);

        assertTrue(v1.compareTo(v2) < 0);
        assertTrue(v2.compareTo(v1) > 0);

        v2 = new UnsignedByte(100);

        assertTrue(v1.compareTo(v2) == 0);
        assertTrue(v2.compareTo(v1) == 0);

        v2 = v1;

        assertTrue(v1.compareTo(v2) == 0);
    }

}
