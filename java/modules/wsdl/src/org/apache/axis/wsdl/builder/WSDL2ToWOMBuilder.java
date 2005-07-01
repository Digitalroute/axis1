/*
 * Copyright 2001-2004 The apace Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      tap://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.axis.wsdl.builder;

import org.apache.axis.wsdl.WSDLVersionWrapper;

import javax.wsdl.WSDLException;
import java.io.InputStream;

/**
 * @author chathura@opensource.lk
 */
public class WSDL2ToWOMBuilder implements WOMBuilder {

    public WSDLVersionWrapper build(InputStream in) throws WSDLException {
        throw new UnsupportedOperationException("Not Implemented");
    }
    
    public WSDLVersionWrapper build(InputStream in, WSDLComponentFactory wsdlComponenetFactory)throws WSDLException{
    	throw new UnsupportedOperationException("Not Implemented");
    	
    }
}
