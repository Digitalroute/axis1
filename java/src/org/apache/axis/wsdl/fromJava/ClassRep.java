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
package org.apache.axis.wsdl.fromJava;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Field;
import java.util.Vector;

/**
 * ClassRep is the representation of a class used inside the Java2WSDL
 * emitter.  The information in the ClassRep can be changed by 
 * user provided code to affect the emitted wsdl file.
 *            
 *             name  
 * ClassRep +-+---------> String
 *  | | | | | |
 *  | | | | | | isIntf
 *  | | | | | +---------> boolean
 *  | | | | | 
 *  | | | | | modifiers
 *  | | | | +-----------> int (use java.lang.reflect.Modifier to decode)
 *  | | | |  
 *  | | | | super 
 *  | | | +-------------> ClassRep
 *  | | |  
 *  | | | interfaces 
 *  | | +---------------> ClassRep(s)
 *  | |
 *  | | methods
 *  | +-----------------> MethodRep(s)
 *  |
 *  | fields
 *  +-------------------> FieldRep(s)
 *
 *            name
 *  MethodRep ----------> String
 *        | |
 *        | | return
 *        | +-----------> ParamRep
 *        |
 *        | params
 *        +-------------> ParamRep
 *
 *           name
 *  ParamRep -----------> String
 *      | |  
 *      | |  type
 *      | +-------------> Class
 *      |
 *      |   mode
 *      +---------------> int (in/out/inout)
 *
 *           name
 *  FieldRep -----------> String
 *       |
 *       | type
 *       +--------------> Class
 *
 * @author Rich Scheuerle  (scheu@us.ibm.com)
 */
public class ClassRep {
    
    private String   _name       = "";
    private boolean  _isInterface= false;
    private int      _modifiers  = 0;  // Use java.lang.reflect.Modifer to decode
    private ClassRep _super      = null;
    private Vector   _interfaces = new Vector();
    private Vector   _methods    = new Vector();
    private Vector   _fields     = new Vector();
    

    /**
     * Constructor
     * Create an empty ClassRep
     */ 
    public ClassRep() {
    }

    /**
     * Constructor
     * Create a default representation of ClassRep
     * @param cls Class to use to create default ClassRep
     * @param inhMethods if true, then the methods array will contain
     *                   methods declared and/or inherited else only
     *                   the declared methods are put in the list
     */ 
    public ClassRep(Class cls, boolean inhMethods) {
        _name = cls.getName();
        _isInterface = cls.isInterface();
        _modifiers = cls.getModifiers();
        if (cls.getSuperclass() != null)
            _super = new ClassRep(cls.getSuperclass(), inhMethods);
        for (int i=0; i < cls.getInterfaces().length; i++) {
            _interfaces.add(new ClassRep(cls.getInterfaces()[i], inhMethods));
        }
        // Constructs a vector of all the public methods
        Method[] m;
        if (inhMethods)
            m = cls.getMethods();
        else
            m = cls.getDeclaredMethods();
        for (int i=0; i < m.length; i++) {
            int mod = m[i].getModifiers();
            if (Modifier.isPublic(mod))
                _methods.add(new MethodRep(m[i]));
        }

        // Constructs a FieldRep for every public field and
        // for every field that has JavaBean accessor methods
        for (int i=0; i < cls.getDeclaredFields().length; i++) {
            Field f = cls.getDeclaredFields()[i];
            int mod = f.getModifiers();
            if (Modifier.isPublic(mod) ||
                isJavaBeanNormal(cls, f.getName(), f.getType()) ||
                isJavaBeanIndexed(cls, f.getName(), f.getType())) {
                if (!isJavaBeanIndexed(cls, f.getName(), f.getType())) {
                    FieldRep fr = new FieldRep(f);
                    if (!_fields.contains(fr))
                        _fields.add(fr);
                } else {
                    FieldRep fr = new FieldRep();
                    fr.setName(f.getName());
                    fr.setType(f.getType().getComponentType());
                    fr.setIndexed(true);
                    if (!_fields.contains(fr))
                        _fields.add(fr);
                }
            }
        }

        // Now add FieldReps for any remaining bean accessors.
        for (int i=0; i < cls.getDeclaredMethods().length; i++) {
            Method method = cls.getDeclaredMethods()[i];
            int mod = method.getModifiers();
            if (Modifier.isPublic(mod) &&
                (method.getName().startsWith("is") ||
                 method.getName().startsWith("get"))) {
                String name = method.getName();
                if (name.startsWith("is")) {
                    name = name.substring(2);
                } else {
                    name = name.substring(3);
                }
                Class type = method.getReturnType();
                if (isJavaBeanNormal(cls, name, type) ||
                    isJavaBeanIndexed(cls, name, type)) {
                    if (!isJavaBeanIndexed(cls, name, type)) {
                        FieldRep fr = new FieldRep();
                        fr.setName(name);
                        fr.setType(type);
                        if (!_fields.contains(fr))
                            _fields.add(fr);
                    } else {
                        FieldRep fr = new FieldRep();
                        fr.setName(name);
                        fr.setType(type.getComponentType());
                        fr.setIndexed(true);
                        if (!_fields.contains(fr))
                            _fields.add(fr);
                    }
                }
                
            }
        }

    }
       
    /**
     * Getters/Setters
     **/
    public String   getName()                { return _name; }
    public void     setName(String name)     { _name = name; }
    public boolean  isInterface()            { return _isInterface; }
    public void     setIsInterface(boolean b){ _isInterface = b; }
    public int      getModifiers()           { return _modifiers; }
    public void     setModifiers(int m)      { _modifiers = m; }
    public ClassRep getSuper()               { return _super; }
    public void     setSuper(ClassRep cr)    { _super = cr; }
    public Vector   getInterfaces()          { return _interfaces; }
    public void     setInterfaces(Vector v)  { _interfaces = v; }
    public Vector   getMethods()             { return _methods; }
    public void     setMethods(Vector v)     { _methods = v; }
    public Vector   getFields()              { return _fields; }
    public void     setFields(Vector v)      { _fields = v; }


    /**
     * Determines if the Property in the class has been compliant accessors. If so returns true,
     * else returns false
     * @param cls the Class
     * @param name is the name of the property
     * @param type is the type of the property
     * @return true if the Property has JavaBean style accessors
     */
    public static boolean isJavaBeanNormal(Class cls, String name, Class type) {
        try {
            String propName = name.substring(0,1).toUpperCase()
                + name.substring(1);
            String setter = "set" + propName;
            String getter = null;
            if (type.getName() == "boolean")
                getter = "is" + propName;
            else
                getter = "get" + propName;

            Method m = cls.getDeclaredMethod(setter, new Class[] {type});
            int mod = m.getModifiers();
            if (!Modifier.isPublic(mod)) {
                return false;
            }

            m = cls.getDeclaredMethod(getter, null);
            mod = m.getModifiers();
            if (!Modifier.isPublic(mod)) {
                return false;
            }       
        }
        catch (NoSuchMethodException ex) {
            return false;
        }
        return true;
    }

    /**
     * Determines if the Property in the Class has bean compliant indexed accessors. If so returns true,
     * else returns false
     * @param cls the Class
     * @param name is the name of the property
     * @param type is the type of the property
     * @return true if the Property has JavaBean style accessors
     */
    public static boolean isJavaBeanIndexed(Class cls, String name, Class type) {
        // Must be an array
        if (!type.isArray())
            return false;

        try {
            String propName =  name.substring(0,1).toUpperCase()
                + name.substring(1);
            String setter = "set" + propName;
            String getter = null;
            if (type.getName().startsWith("boolean["))
                getter = "is" + propName;
            else
                getter = "get" + propName;

            Method m = type.getDeclaredMethod(setter, new Class[] {int.class, type.getComponentType()});
            int mod = m.getModifiers();
            if (!Modifier.isPublic(mod)) {
                return false;
            }

            m = type.getDeclaredMethod(getter, new Class[] {int.class});
            mod = m.getModifiers();
            if (!Modifier.isPublic(mod)) {
                return false;
            }       
        }
        catch (NoSuchMethodException ex) {
            return false;
        }
        return true;
    }


    /**
     * Determines if the Class is a Holder class. If so returns Class of held type
     * else returns null
     * @param type the Class
     * @return class of held type or null
     */
    public static Class holderClass(Class type) {
        if (type.getName() != null &&
            type.getName().endsWith("Holder")) {
            // Holder is supposed to have a public value field.
            java.lang.reflect.Field field;
            try {
                field = type.getField("value");
            } catch (Exception e) {
                field = null;
            }
            if (field != null) {
                return field.getType();
            }
        }
        return null;
    }
};
