package test.outparams;

import junit.framework.TestCase;

import org.apache.axis.*;
import org.apache.axis.transport.local.LocalTransport;
import org.apache.axis.client.ServiceClient;
import org.apache.axis.encoding.*;
import org.apache.axis.handlers.soap.*;
import org.apache.axis.message.*;
import org.apache.axis.server.*;
import org.apache.axis.registries.*;

import java.util.Vector;

import test.RPCDispatch.Data;

/**
 * Test org.apache.axis.handlers.RPCDispatcher
 *
 * @author Sam Ruby <rubys@us.ibm.com>
 */
public class TestOutParams extends TestCase {
    private final String serviceURN = "urn:X-test-outparams";


    /** A fixed message, since the return is hardcoded */
    private final String message =
        "<?xml version=\"1.0\"?>\n" +
        "<soap:Envelope " +
             "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
             "xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\" " +
             "xmlns:xsi=\"" + Constants.URI_CURRENT_SCHEMA_XSI + "\" " +
             "xmlns:xsd=\"" + Constants.URI_CURRENT_SCHEMA_XSD + "\">\n" +
             "<soap:Body>\n" +
             "<ns:someMethod xmlns:ns=\"" + serviceURN + "\"/>\n" +
             "</soap:Body>\n" +
        "</soap:Envelope>\n";

    private ServiceClient client = new ServiceClient();
    private AxisServer server = new AxisServer();

    private HandlerRegistry hr;
    private HandlerRegistry sr;

    public TestOutParams(String name) {
        super(name);
        server.init();
        hr = (HandlerRegistry) server.getHandlerRegistry();
        sr = (HandlerRegistry) server.getServiceRegistry();
    }

    /**
     * Test returning output params
     */
    public void testOutputParams() throws Exception {
        // Register the service
        Handler h = new ServiceHandler();

        // ??? Do we need to register the handler?

        SOAPService service = new SOAPService(h);
        sr.add(serviceURN, service);

        // Make sure the local transport uses the server we just configured
        client.setTransport(new LocalTransport(server));

        // Create the message context
        MessageContext msgContext = new MessageContext(server);

        // Construct the soap request
        SOAPEnvelope envelope = new SOAPEnvelope();
        msgContext.setRequestMessage(new Message(envelope));

        // Invoke the Axis server
        Object ret = client.invoke(serviceURN, "method",
                                new Object [] { "test" });

        Vector outParams = client.getOutputParams();
        assertNotNull("No output Params returned!", outParams);

        RPCParam param = (RPCParam)outParams.get(0);
        assertEquals("Param 0 does not equal expected value", param.getValue(), ServiceHandler.OUTPARAM1);

        param = (RPCParam)outParams.get(1);
        assertEquals("Param 1 does not equal expected value", param.getValue(), ServiceHandler.OUTPARAM2);
    }

    public static void main(String args[])
    {
      try {
        TestOutParams tester = new TestOutParams("RPC test");
        tester.testOutputParams();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
}
