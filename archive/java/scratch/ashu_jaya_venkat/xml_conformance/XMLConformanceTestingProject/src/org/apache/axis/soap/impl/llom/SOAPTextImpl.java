package org.apache.axis.soap.impl.llom;

import org.apache.axis.om.OMElement;
import org.apache.axis.om.impl.llom.OMElementImpl;
import org.apache.axis.soap.SOAPText;

/**
 * Copyright 2001-2004 The Apache Software Foundation.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * <p/>
 */
public class SOAPTextImpl extends OMElementImpl implements SOAPText{
    /**
     * Eran Chinthaka (chinthaka@apache.org)
     */
    /**
     * @param parent
     * @param parent
     */
    public SOAPTextImpl(OMElement parent) {
        super(parent);
    }

    public void setLang(String lang) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getLang() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
