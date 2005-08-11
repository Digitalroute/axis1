/*
 * Copyright 2004,2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package sample.mtom.imagetransfer.client;

import org.apache.axis2.Constants;
import org.apache.axis2.soap.SOAP12Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.attachments.utils.ImageDataSource;
import org.apache.axis2.attachments.utils.JDK13IO;
import org.apache.axis2.clientapi.Call;
import org.apache.axis2.om.*;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;


public class MTOMClientModel {
    private File inputFile = null;

    private EndpointReference targetEPR = new EndpointReference("http://127.0.0.1:8080/axis2/services/MyService");

    private QName operationName = new QName("mtomSample");


    public MTOMClientModel() {

    }

    private OMElement createEnvelope(String fileName) throws Exception {

        DataHandler expectedDH;
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://localhost/my", "my");

        OMElement data = fac.createOMElement("mtomSample", omNs);
        OMElement image = fac.createOMElement("image", omNs);
        Image expectedImage;
        expectedImage = new JDK13IO()
                .loadImage(new FileInputStream(inputFile));

        ImageDataSource dataSource = new ImageDataSource("test.jpg",
                expectedImage);
        expectedDH = new DataHandler(dataSource);
        OMText textData = fac.createText(expectedDH, true);
        image.addChild(textData);

        OMElement imageName = fac.createOMElement("fileName", omNs);
        if (fileName != null) {
            imageName.setText(fileName);
        }
        OMElement wrap = fac.createOMElement("wrap",omNs);
        wrap.addChild(image);
        wrap.addChild(imageName);
        data.addChild(wrap);
        return data;

    }

    public OMElement testEchoXMLSync(String fileName) throws Exception {

        OMElement payload = createEnvelope(fileName);

        Call call = new Call();
        call.setSoapVersionURI(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);
        call.setTo(targetEPR);
        // enabling MTOM in the client side
        call.set(Constants.Configuration.ENABLE_MTOM, Constants.VALUE_TRUE);
        call.setTransportInfo(Constants.TRANSPORT_HTTP,
                Constants.TRANSPORT_HTTP, false);
        return call.invokeBlocking(operationName
                .getLocalPart(),
                payload);

    }


    public void setTargetEPR(String targetEPR) {
        this.targetEPR = new EndpointReference(targetEPR);

    }


    public void setInputFile(File inputFile) {
        this.inputFile = inputFile;
    }
}
