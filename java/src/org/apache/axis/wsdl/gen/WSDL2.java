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
package org.apache.axis.wsdl.gen;

import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;

import java.util.List;

import org.apache.axis.utils.CLArgsParser;
import org.apache.axis.utils.CLOption;
import org.apache.axis.utils.CLOptionDescriptor;
import org.apache.axis.utils.CLUtil;
import org.apache.axis.utils.JavaUtils;

public class WSDL2 {

    protected static final int DEBUG_OPT = 'D';
    protected static final int HELP_OPT = 'h';
    protected static final int NOIMPORTS_OPT = 'n';
    protected static final int VERBOSE_OPT = 'v';

    protected CLOptionDescriptor[] options = new CLOptionDescriptor[]{
        new CLOptionDescriptor("help",
                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                HELP_OPT,
                JavaUtils.getMessage("optionHelp00")),
        new CLOptionDescriptor("verbose",
                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                VERBOSE_OPT,
                JavaUtils.getMessage("optionVerbose00")),
        new CLOptionDescriptor("noImports",
                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                NOIMPORTS_OPT,
                JavaUtils.getMessage("optionImport00")),
        new CLOptionDescriptor("Debug",
                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                DEBUG_OPT,
                JavaUtils.getMessage("optionDebug00"))
    };

    protected String wsdlURI = null;
    protected Parser parser;

    protected WSDL2 () {
        parser = createParser();
    } // ctor

    protected Parser createParser() {
        return new Parser();
    } // createParser

    protected Parser getParser() {
        return parser;
    } // getParser

    protected void addOptions(CLOptionDescriptor[] newOptions) {
        if (newOptions != null && newOptions.length > 0) {
            CLOptionDescriptor[] allOptions = new CLOptionDescriptor[
                   options.length + newOptions.length];
            System.arraycopy(options, 0, allOptions, 0, options.length);
            System.arraycopy(newOptions, 0, allOptions, options.length, newOptions.length);
            options = allOptions;
        }
    } // addOptions

    protected void parseOption(CLOption option) {
        switch (option.getId()) {
            case CLOption.TEXT_ARGUMENT:
                if (wsdlURI != null) {
                    printUsage();
                }
                wsdlURI = option.getArgument();
                break;

            case HELP_OPT:
                printUsage();
                break;

            case NOIMPORTS_OPT:
                parser.setImports(false);
                break;

            case VERBOSE_OPT:
                parser.setVerbose(true);
                break;

            case DEBUG_OPT:
                parser.setDebug(true);
                break;
        }
    } // parseOption

    protected void validateOptions() {
        if (wsdlURI == null) {
            printUsage();
        }

        // Set username and password if provided in URL
        checkForAuthInfo(wsdlURI);
        Authenticator.setDefault(new DefaultAuthenticator(
                parser.getUsername(), parser.getPassword()));
    } // validateOptions

    private void checkForAuthInfo(String uri) {
        URL url = null;
        try {
            url = new URL(uri);
        } catch (MalformedURLException e) {
            // not going to have userInfo
            return;
        }
        String userInfo = url.getUserInfo();
        if (userInfo != null) {
            int i = userInfo.indexOf(':');
            if (i >= 0) {
                parser.setUsername(userInfo.substring(0,i));
                parser.setPassword(userInfo.substring(i+1));
            } else {
                parser.setUsername(userInfo);
            }
        } 
    }

    protected void printUsage() {
        String lSep = System.getProperty("line.separator");
        StringBuffer msg = new StringBuffer();
        msg.append(
                "java WSDL2 [options] WSDL-URI")
                .append(lSep);
        msg.append(lSep);
        msg.append(CLUtil.describeOptions(options).toString());
        System.out.println(msg.toString());
        System.exit(1);
    } // printUsage

    protected void run(String[] args) {
        // Parse the arguments
        CLArgsParser argsParser = new CLArgsParser(args, options);

        // Print parser errors, if any
        if (null != argsParser.getErrorString()) {
            System.err.println(
                    JavaUtils.getMessage("error01", argsParser.getErrorString()));
            printUsage();
        }

        // Get a list of parsed options
        List clOptions = argsParser.getArguments();
        int size = clOptions.size();

        try {
            // Parse the options and configure the emitter as appropriate.
            for (int i = 0; i < size; i++) {
                parseOption((CLOption)clOptions.get(i));
            }

            // validate argument combinations
            //
            validateOptions();

            parser.run(wsdlURI);
            
            // everything is good
            System.exit(0);
        }
        catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    } // run

    public static void main(String[] args) {
        WSDL2 wsdl2 = new WSDL2();
        wsdl2.run(args);
    } // main

    /**
     * This class is used by WSDL2 main() only
     * Supports the http.proxyUser and http.proxyPassword properties.
     */
    public static class DefaultAuthenticator extends Authenticator {
        private String user;
        private String password;
        
        DefaultAuthenticator(String user, String pass) {
            this.user = user;
            this.password = pass;
        }
        protected PasswordAuthentication getPasswordAuthentication() {
            // if user and password weren't provided, check the system properties
            if (user == null) {
                user = System.getProperty("http.proxyUser","");
            }
            if (password == null) {
                password = System.getProperty("http.proxyPassword","");
            }
            
            return new PasswordAuthentication (user, password.toCharArray());
        }
    }
} // class WSDL2
