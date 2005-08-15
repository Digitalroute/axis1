package org.apache.axis2.saaj;

import junit.framework.TestCase;

import javax.xml.soap.*;
import javax.xml.soap.MimeHeaders;
import java.io.ByteArrayInputStream;
import java.util.Iterator;

public class SOAPFaultDetailTest extends TestCase {
	
	public SOAPFaultDetailTest(String name){
		super(name);
	}
	
    String xmlString =
    	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
    	"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
    	" <soapenv:Body>" +
    	"  <soapenv:Fault>" +
    	"   <faultcode>soapenv:Server.generalException</faultcode>" +
    	"   <faultstring></faultstring>" +
    	"   <detail>" +
    	"    <tickerSymbol xsi:type=\"xsd:string\">MACR</tickerSymbol>" +
    	"   <ns1:exceptionName xmlns:ns1=\"http://xml.apache.org/axis/\">test.wsdl.faults.InvalidTickerFaultMessage</ns1:exceptionName>" +
    	"   </detail>" +
    	"  </soapenv:Fault>" +
    	" </soapenv:Body>" +
    	"</soapenv:Envelope>";
    
    public void testDetails() throws Exception{
    	MessageFactory mf = MessageFactory.newInstance();
    	SOAPMessage smsg = 
    		mf.createMessage(new MimeHeaders(), new ByteArrayInputStream(xmlString.getBytes()));
    	SOAPBody body = smsg.getSOAPBody();
    	//smsg.writeTo(System.out);
    	SOAPFault flt = body.getFault();
    	flt.addDetail();
    	javax.xml.soap.Detail d = flt.getDetail();
    	Iterator i = d.getDetailEntries();
    	while (i.hasNext()){
    		DetailEntry entry = (DetailEntry) i.next();
    		String name = entry.getElementName().getLocalName();
    		if ("tickerSymbol".equals(name)) {
    			assertEquals("the value of the tickerSymbol element didn't match",
    					"MACR", entry.getValue());
    		} else if ("exceptionName".equals(name)) {
    			assertEquals("the value of the exceptionName element didn't match",
    					"test.wsdl.faults.InvalidTickerFaultMessage", entry.getValue());
    		} else {
    			assertTrue("Expecting details element name of 'tickerSymbol' or 'expceptionName' - I found :" + name, false);
    		}
    	}
    	assertTrue(d != null);
    }
    
    /**
     * Main
     */
    public static void main(String[] args)
            throws Exception
    {
        SOAPFaultDetailTest detailTest = new SOAPFaultDetailTest("faultdetails");
        detailTest.testDetails();
    }

}