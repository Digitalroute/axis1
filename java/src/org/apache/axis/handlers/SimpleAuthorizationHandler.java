/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.axis.handlers ;

import org.apache.axis.AxisFault;
import org.apache.axis.Handler;
import org.apache.axis.MessageContext;
import org.apache.axis.security.AuthenticatedUser;
import org.apache.axis.security.SecurityProvider;
import org.apache.axis.utils.JavaUtils;
import org.apache.log4j.Category;

import java.util.StringTokenizer;

/**
 * Just a simple Authorization Handler to see if the user
 * specified in the Bag in the MessageContext is allowed to preform this
 * action.
 *
 * Just look for 'user' and 'action' in a file called 'perms.lst'
 *
 * Replace this with your 'real' Authorization code.
 *
 * @author Doug Davis (dug@us.ibm.com)
 * @author Sam Ruby (rubys@us.ibm.com)
 */
public class SimpleAuthorizationHandler extends BasicHandler {
    static Category category =
            Category.getInstance(SimpleAuthorizationHandler.class.getName());

    /**
     * Authorize the user and targetService from the msgContext
     */
    public void invoke(MessageContext msgContext) throws AxisFault {
        if (category.isDebugEnabled()) {
            category.debug(JavaUtils.getMessage("enter00", 
                "SimpleAuthorizationHandler::invoke") );
        }

        boolean allowByDefault = false;
        String optVal = (String)getOption("allowByDefault");
        if ((optVal != null) && (optVal.equalsIgnoreCase("true")))
            allowByDefault = true;

        AuthenticatedUser user = (AuthenticatedUser)msgContext.
                                         getProperty(MessageContext.AUTHUSER);

        if (user == null)
            throw new AxisFault("Server.NoUser",
                    JavaUtils.getMessage("needUser00"), null, null);

        String userID = user.getName();
        Handler serviceHandler = msgContext.getServiceHandler();

        if (serviceHandler == null)
            throw new AxisFault(JavaUtils.getMessage("needService00"));

        String serviceName = serviceHandler.getName();

        String allowedRoles = (String)serviceHandler.getOption("allowedRoles");
        if (allowedRoles == null) {
            if (allowByDefault) {
                if (category.isInfoEnabled()) {
                    category.info(JavaUtils.getMessage( "noRoles00"));
                }
            }
            else {
                if (category.isInfoEnabled()) {
                    category.info(JavaUtils.getMessage( "noRoles01"));
                }

                throw new AxisFault( "Server.Unauthorized",
                    JavaUtils.getMessage("notAuth00", userID, serviceName),
                    null, null );
            }

            if (category.isDebugEnabled()) {
                category.debug(JavaUtils.getMessage("exit00", 
                    "SimpleAuthorizationHandler::invoke") );
            }
            return;
        }

        SecurityProvider provider = (SecurityProvider)msgContext.getProperty("securityProvider");
        if (provider == null)
            throw new AxisFault(JavaUtils.getMessage("noSecurity00"));

        StringTokenizer st = new StringTokenizer(allowedRoles, ",");
        while (st.hasMoreTokens()) {
            String thisRole = st.nextToken();
            if (provider.userMatches(user, thisRole)) {

                if (category.isInfoEnabled()) {
                    category.info(JavaUtils.getMessage("auth01", 
                        userID, serviceName));
                }

                if (category.isDebugEnabled()) {
                    category.debug(JavaUtils.getMessage("exit00", 
                        "SimpleAuthorizationHandler::invoke") );
                }
                return;
            }
        }

        throw new AxisFault( "Server.Unauthorized",
            JavaUtils.getMessage("cantAuth02", userID, serviceName),
            null, null );
    }

    /**
     * Nothing to undo
     */
    public void undo(MessageContext msgContext) {
        if (category.isDebugEnabled()) {
            category.debug(JavaUtils.getMessage("enter00", 
                "SimpleAuthorizationHandler::undo") );
            category.debug(JavaUtils.getMessage("exit00", 
                "SimpleAuthorizationHandler::undo") );
        }
    }
};
