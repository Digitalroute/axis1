package org.apache.axis.om;

import org.apache.axis.om.impl.llom.factory.OMXMLBuilderFactory;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLInputFactory;
import java.io.ByteArrayInputStream;

/*
* Copyright 2004,2005 The Apache Software Foundation.
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
*
*  Aim of this test is to check the compatibilty of the stax parser implementation
*/
public class StaxParserTest extends AbstractTestCase {

    XMLStreamReader parser1;
    XMLStreamReader parser2;
    XMLStreamReader parser3;
    String xmlDocument = "<purchase-order xmlns=\"http://openuri.org/easypo\">\n" +
            "  <customer>\n" +
            "    <name>Gladys Kravitz</name>\n" +
            "    <address>Anytown, PA</address>\n" +
            "  </customer>\n" +
            "  <date>2005-03-06T14:06:12.697+06:00</date>\n" +
            "</purchase-order>";

    public StaxParserTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        //make the parsers
        parser1 = XMLInputFactory.newInstance().createXMLStreamReader(new ByteArrayInputStream(xmlDocument.getBytes()));

        OMXMLParserWrapper builder = OMXMLBuilderFactory.createStAXOMBuilder(OMFactory.newInstance(),
                XMLInputFactory.newInstance().createXMLStreamReader(new ByteArrayInputStream(xmlDocument.getBytes())));
        parser2 = builder.getDocumentElement().getPullParser(false);

        OMXMLParserWrapper builder2 = OMXMLBuilderFactory.createStAXOMBuilder(OMFactory.newInstance(),
                XMLInputFactory.newInstance().createXMLStreamReader(new ByteArrayInputStream(xmlDocument.getBytes())));
        parser3 = builder.getDocumentElement().getPullParser(true);
        
    }

    public void xtestParserEvents() throws Exception{

        assertEquals(parser1.getEventType(),parser2.getEventType());

        while(parser1.hasNext()){
            int parser1Event = parser1.next();
            int parser2Event = parser2.next();
            assertEquals(parser1Event,parser2Event);
        }


    }

    public void testParserEvents2() throws Exception{
        while(parser1.hasNext()){
            int parser1Event = parser1.getEventType();
            int parser2Event = parser2.getEventType();
            parser1.next();
            parser2.next();
            assertEquals(parser1Event,parser2Event);
        }


    }
}
//     public void testParserEvents2() throws Exception{
//
//        System.out.println("parser2 initial = " + parser2.getEventType());
//        while(parser2.hasNext()){
//            System.out.println(" event "+ parser2.next() + parser2.getLocalName());
//        }


//}



