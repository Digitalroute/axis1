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
package org.apache.axis2.wsdl.builder.wsdl4j;

import org.apache.axis2.wsdl.WSDLVersionWrapper;
import org.apache.axis2.wsdl.builder.WOMBuilder;
import org.apache.axis2.wsdl.builder.WSDLComponentFactory;
import org.apache.wsdl.WSDLDescription;
import org.apache.wsdl.impl.WSDLDescriptionImpl;
import org.apache.wsdl.util.Utils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author chathura@opensource.lk
 */
public class WSDL1ToWOMBuilder implements WOMBuilder {

    public WSDLVersionWrapper build(InputStream in) throws WSDLException {

        WSDLDescription wsdlDescription = new WSDLDescriptionImpl();

        Definition wsdl1Definition = this.readInTheWSDLFile(in);
        WSDLPump pump = new WSDLPump(wsdlDescription, wsdl1Definition);
        pump.pump();

        return new WSDLVersionWrapper(wsdlDescription, wsdl1Definition);
    }

    public WSDLVersionWrapper build(InputStream in,
                                    WSDLComponentFactory wsdlComponentFactory) throws WSDLException {
        WSDLDescription wsdlDescription = wsdlComponentFactory.createDescription();

        Definition wsdl1Definition = this.readInTheWSDLFile(in);
        WSDLPump pump = new WSDLPump(wsdlDescription,
                wsdl1Definition,
                wsdlComponentFactory);
        pump.pump();

        return new WSDLVersionWrapper(wsdlDescription, wsdl1Definition);

    }

    private Definition readInTheWSDLFile(InputStream in) throws WSDLException {

        WSDLReader reader =
                WSDLFactory.newInstance().newWSDLReader();
        Document doc;
        try {
            doc = Utils.newDocument(in);
        } catch (ParserConfigurationException e) {
            throw new WSDLException(WSDLException.PARSER_ERROR,
                    "Parser Configuration Error",
                    e);
        } catch (SAXException e) {
            throw new WSDLException(WSDLException.PARSER_ERROR,
                    "Parser SAX Error",
                    e);

        } catch (IOException e) {
            throw new WSDLException(WSDLException.INVALID_WSDL, "IO Error", e);
        }

        return reader.readWSDL(null, doc);
    }


}
