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
package org.apache.axis.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Stack;
import java.util.Iterator;

/**
 * @author: James Snell
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public class NSStack {
    protected static Log log =
        LogFactory.getLog(NSStack.class.getName());
    
    private static final ArrayList EMPTY = new ArrayList();

    private Stack stack = new Stack();
    
    private NSStack parent = null;

    public NSStack() {}
    
    public NSStack(ArrayList table) {
        push(table);
    }
    
    public NSStack(NSStack parent) {
        this.parent = parent;
    }
    
    public void push() {
        if (stack == null) stack = new Stack();

        if (log.isTraceEnabled())
            log.trace("NSPush (" + stack.size() + ")");

        stack.push(EMPTY);
    }
    
    public void push(ArrayList table) {
        if (stack == null) stack = new Stack();

        if (log.isTraceEnabled())
            log.trace("NSPush (" + stack.size() + ")");

        if (table.size() == 0) 
           stack.push(EMPTY);
        else
           stack.push(table);
    }
    
    public ArrayList peek() {
        if (stack.isEmpty())
            if (parent != null)
                return parent.peek();
            else
                return EMPTY;
                
        
        return (ArrayList)stack.peek();
    }
    
    public ArrayList pop() {
        if (stack.isEmpty()) {
            if (log.isTraceEnabled())
                log.trace("NSPop (" + JavaUtils.getMessage("empty00") + ")");

            if (parent != null)
                return parent.pop();

            return null;
        }
        
        if (log.isTraceEnabled()){
            ArrayList t = (ArrayList)stack.pop();
            log.trace("NSPop (" + stack.size() + ")");
            return t;
        } else {
            return (ArrayList)stack.pop();
        }
    }
    
    public void add(String namespaceURI, String prefix) {
        if (stack.isEmpty()) push();
        ArrayList table = peek();
        if (table == EMPTY) {
            table = new ArrayList();
            stack.pop();
            stack.push(table);
        }
        // Replace duplicate prefixes (last wins - this could also fault)
        for (Iterator i = table.iterator(); i.hasNext();) {
            Mapping mapping = (Mapping)i.next();
            if (mapping.getPrefix().equals(prefix)) {
                mapping.setNamespaceURI(namespaceURI);
                return;
            }
        }
        table.add(new Mapping(namespaceURI, prefix));
    }
    
    /**
     * remove a namespace from the topmost table on the stack
     */
    /*
    public void remove(String namespaceURI) {
        if (stack.isEmpty()) return;
        ArrayList current = peek();
        for (int i = 0; i < current.size(); i++) {
            Mapping map = (Mapping)current.get(i);
            if (map.getNamespaceURI().equals(namespaceURI)) {
                current.removeElementAt(i);
                return; // ???
            }
        }
    }
    */
    
    /**
     * Return an active prefix for the given namespaceURI.  NOTE : This
     * may return null even if the namespaceURI was actually mapped further
     * up the stack IF the prefix which was used has been repeated further
     * down the stack.  I.e.:
     * 
     * <pre:outer xmlns:pre="namespace">
     *   <pre:inner xmlns:pre="otherNamespace">
     *      *here's where we're looking*
     *   </pre:inner>
     * </pre:outer>
     * 
     * If we look for a prefix for "namespace" at the indicated spot, we won't
     * find one because "pre" is actually mapped to "otherNamespace"
     */ 
    public String getPrefix(String namespaceURI) {
        if ((namespaceURI == null) || (namespaceURI.equals("")))
            return null;
        
        if (!stack.isEmpty()) {
            for (int n = stack.size() - 1; n >= 0; n--) {
                ArrayList t = (ArrayList)stack.get(n);
                
                for (int i = 0; i < t.size(); i++) {
                    Mapping map = (Mapping)t.get(i);
                    if (map.getNamespaceURI().equals(namespaceURI)) {
                        String possiblePrefix = map.getPrefix();
                        if (getNamespaceURI(possiblePrefix).equals(namespaceURI))
                            return possiblePrefix;
                    }
                }
            }
        }
        
        if (parent != null)
            return parent.getPrefix(namespaceURI);
        return null;
    }
    
    public String getNamespaceURI(String prefix) {
        if (prefix == null)
            prefix = "";
        
        if (!stack.isEmpty()) {
            for (int n = stack.size() - 1; n >= 0; n--) {
                ArrayList t = (ArrayList)stack.get(n);
                
                for (int i = 0; i < t.size(); i++) {
                    Mapping map = (Mapping)t.get(i);
                    if (map.getPrefix().equals(prefix))
                        return map.getNamespaceURI();
                }
            }
        }
        
        if (parent != null)
            return parent.getNamespaceURI(prefix);

        if (log.isTraceEnabled()){
            log.trace("--" + JavaUtils.getMessage("noPrefix00", "" + this, prefix));
            dump("--");
            log.trace("--" + JavaUtils.getMessage("end00"));
        }

        return null;
    }
    
    public boolean isDeclared(String namespaceURI) {
        if (!stack.isEmpty()) {
            for (int n = stack.size() - 1; n >= 0; n--) {
                ArrayList t = (ArrayList)stack.get(n);
                if ((t != null) && (t != EMPTY)) {
                    for (int i = 0; i < t.size(); i++) {
                        if (((Mapping)t.get(i)).getNamespaceURI().
                                   equals(namespaceURI))
                            return true;
                    }
                }
            }
        }

        if (parent != null)
            return parent.isDeclared(namespaceURI);

        return false;
    }
    
    public void dump(String dumpPrefix)
    {
        Enumeration e = stack.elements();
        while (e.hasMoreElements()) {
            ArrayList list = (ArrayList)e.nextElement();

            if (list == null) {
                log.trace(dumpPrefix + JavaUtils.getMessage("nullTable00"));
                continue;
            }

            for (int i = 0; i < list.size(); i++) {
                Mapping map = (Mapping)list.get(i);
                log.trace(dumpPrefix + map.getNamespaceURI() + " -> " + map.getPrefix());
            }
        }

        if (parent != null) {
            log.trace(dumpPrefix + "--" + JavaUtils.getMessage("parent00"));
            parent.dump(dumpPrefix + "--");
        }
    }
}
