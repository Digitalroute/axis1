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

package org.apache.wsdl;

import org.apache.axis2.wsdl.builder.WOMBuilderFactory;

import javax.xml.namespace.QName;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author chathura@opensource.lk
 *  
 */
public class BindingOperationTest extends AbstractTestCase {

	public BindingOperationTest(String arg) {
		super(arg);
	}

	/**
	 * The WSDL should be passed properly and the WOM should be built if 
	 * everything is allright
	 * @throws Exception
	 */
	public void testBindingOperation() throws Exception {
		WSDLDescription womDescription;

		InputStream in = new FileInputStream(
				getTestResourceFile("BookQuote.wsdl"));
		womDescription = WOMBuilderFactory.getBuilder(WOMBuilderFactory.WSDL11)
				.build(in).getDescription();
		
		assertNotNull(womDescription);
		if(null !=womDescription){
			String ns = "http://www.Monson-Haefel.com/jwsbook/BookQuote";
			WSDLBinding binding = womDescription.getBinding(new QName(ns,"BookQuoteBinding"));
			WSDLBindingOperation bindingOperation = binding.getBindingOperation(new QName(ns, "getBookPrice"));
			assertNotNull(bindingOperation.getInput());
			assertNull(bindingOperation.getOutput());
		}

	}
}