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

package org.apache.axis.providers.java;

import java.util.* ;
import org.apache.axis.* ;
import org.apache.axis.utils.* ;
import org.apache.axis.utils.cache.* ;
import org.apache.axis.message.* ;
import org.apache.axis.providers.BasicProvider;

import java.rmi.*;
import java.rmi.server.*;
import java.util.*;
import javax.naming.*;
import java.lang.reflect.Method;

/**
 * A basic EJB Provider
 *
 * @author Carl Woolf (cwoolf@macromedia.com)
 */
public class EJBProvider extends RPCProvider 
{
    private static final String beanNameOption = "beanJndiName";
    private static final String allowedMethodsOption = "allowedMethods";
    public static final String jndiContextClass = "jndiContextClass";
    public static final String jndiURL = "jndiURL";
    public static final String jndiUsername = "jndiUser";
    public static final String jndiPassword = "jndiPassword";

    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////
    /////// Default methods from JavaProvider ancestor, overridden
    ///////   for ejbeans
    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     *
     */
    protected Object getNewServiceObject(MessageContext msgContext,
                                             String clsName)
        throws Exception
    {
        Handler serviceHandler = msgContext.getServiceHandler();
        Object home;

        try
        {
            String username = (String)getStrOption(jndiUsername,
                                                   serviceHandler);
            String password = (String)getStrOption(jndiPassword,
                                                   serviceHandler);
            String factoryClass = (String)getStrOption(jndiContextClass,
                                                       serviceHandler);
            String contextUrl = (String)getStrOption(jndiURL,
                                                     serviceHandler);

            Properties properties = new Properties();

            properties.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                                   factoryClass);
            properties.setProperty(Context.PROVIDER_URL,
                                   contextUrl);

            properties.setProperty(Context.SECURITY_PRINCIPAL,
                                   username);
            properties.setProperty(Context.SECURITY_CREDENTIALS,
                                   password);

            Context context = new InitialContext(properties);
            if (null == context)
            {
                throw new AxisFault("EJBProvider can't get Context");
            }

            home =  context.lookup(clsName);
            if (null == home)
            {
                throw new AxisFault("EJBProvider can't get Bean Home");
            }
        }
        catch (Exception exception)
        {
            throw new AxisFault(exception);
        }

        Class homeClass = home.getClass();
        Class params[] = new Class[0];
        Method createMethod = homeClass.getMethod("create", params);
        
        Object args[] = new Object[0];
        Object result = createMethod.invoke(home, args);
        
        return result;
    }

    /**
     *
     */
    protected String getServiceClassName(Handler service)
    {
        return (String) service.getOption( beanNameOption );
    }
    /**
     *
     */
    protected String getServiceAllowedMethods(Handler service)
    {
        return (String) service.getOption( allowedMethodsOption );
    }
    /**
     *
     */
    protected String getServiceClassNameOptionName()
    {
        return beanNameOption;
    }
    /**
     *
     */
    protected String getServiceAllowedMethodsOptionName()
    {
        return allowedMethodsOption;
    }
    
    /**
     * Get a String option by looking first in the service options,
     * and then at the Handler's options.  This allows defaults to be
     * specified at the provider level, and then overriden for particular
     * services.
     *
     * @param optionName the option to retrieve
     * @return String the value of the option or null if not found in
     *                either scope
     */
    private String getStrOption(String optionName, Handler service)
    {
        String value = null;
        if (service != null)
            value = (String)service.getOption(optionName);
        if (value == null)
            value = (String)getOption(optionName);
        return value;
    }
}
