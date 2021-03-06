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
package org.apache.geronimo.ews.ws4j2ee.toWs.dd;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.geronimo.ews.ws4j2ee.context.J2EEWebServiceContext;
import org.apache.geronimo.ews.ws4j2ee.toWs.GenerationConstants;
import org.apache.geronimo.ews.ws4j2ee.toWs.GenerationFault;
import org.apache.geronimo.ews.ws4j2ee.toWs.Generator;
import org.apache.geronimo.ews.ws4j2ee.toWs.Writer;
import org.apache.geronimo.ews.ws4j2ee.toWs.dd.geronimo.GeronimoDDWriter;
import org.apache.geronimo.ews.ws4j2ee.toWs.dd.jboss.JBossDDWriter;
import org.apache.geronimo.ews.ws4j2ee.toWs.dd.jonas.JOnASDDWriter;

/**
 * @author Srinath perera(hemapani@opesnource.lk)
 */
public class J2EEContainerSpecificDDGenerator implements Generator {
    private J2EEWebServiceContext j2eewscontext;
    private Writer writer;
    protected static Log log =
            LogFactory.getLog(JaxrpcMapperGenerator.class.getName());

    public J2EEContainerSpecificDDGenerator(J2EEWebServiceContext j2eewscontext) throws GenerationFault {
        this.j2eewscontext = j2eewscontext;
        if (GenerationConstants.JBOSS_CONTAINER.equals(j2eewscontext.getMiscInfo().getTargetJ2EEContainer()))
            writer = new JBossDDWriter(j2eewscontext);
        else if (GenerationConstants.JONAS_CONTAINER.equals(j2eewscontext.getMiscInfo().getTargetJ2EEContainer()))
            writer = new JOnASDDWriter(j2eewscontext);
        else if (GenerationConstants.GERONIMO_CONTAINER.equals(j2eewscontext.getMiscInfo().getTargetJ2EEContainer()))
            writer = new GeronimoDDWriter(j2eewscontext);
        else
            new GenerationFault("unsupported j2ee container " + j2eewscontext.getMiscInfo().getTargetJ2EEContainer());
    }

    public void generate() throws GenerationFault {
        if (writer != null)
            writer.write();
    }
}
