/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
 *    Apache Software Foundation (http://www.apache.org/)."
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

package org.apache.axismora.wsdl2ws.java;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.axismora.wsdl2ws.SourceWriter;
import org.apache.axismora.wsdl2ws.WrapperFault;
import org.apache.axismora.wsdl2ws.WrapperUtils;
/**
 * abstract class writer
 * @author Srianth Perera(hemapani@opensource.lk)
 */
public abstract class JavaClassWriter implements SourceWriter {
    protected String classname;
    protected String packageName;  
    private String pacakgesatement;
    protected BufferedWriter writer;
    private String targetDirectory;
    protected boolean overwrite = true;
    
    public JavaClassWriter(String packagename, String classname,String targetDirectory) throws WrapperFault {
		this.classname = classname;
		int arrayIndex = classname.indexOf('[');  
		if( arrayIndex >0) 
			this.classname = 
				classname.substring(0,arrayIndex)+"Array";
		this.packageName = packagename;
        if (packagename != null && !packagename.trim().equals(""))
            this.pacakgesatement = "package " + packagename + ";\n";
        else
            this.pacakgesatement = "";
        this.targetDirectory = targetDirectory;
        
     
    }
    
	public JavaClassWriter(String qualifiedName,String targetDirectory) throws WrapperFault {
		this.classname = WrapperUtils.getClassNameFromFullyQualifiedName(qualifiedName);		
		int arrayIndex = classname.indexOf('[');  
		if( arrayIndex >0) 
			this.classname = 
				classname.substring(0,arrayIndex)+"Array";
		this.packageName = WrapperUtils.getPackegeName4QualifiedName(qualifiedName);
		if (packageName != null && !packageName.trim().equals(""))
			this.pacakgesatement = "package " + packageName + ";\n";
		else
			this.pacakgesatement = "";
		this.targetDirectory = targetDirectory;
        
     
	}
    
    public void writeSource() throws WrapperFault {
        try {
        	File file = getJavaFilePath();
        	if(!overwrite && file.exists()){
        		System.out.println("the WSDL2WS will not overwrite the service file");
        		return;
        	}	
            this.writer = new BufferedWriter(new FileWriter(file, false));
            //this.writer = new BufferedWriter(new FileWriter(getJavaFilePath()));
            this.writer.write(this.pacakgesatement);
            writeImportStatements();
            writeClassComment();
            this.writer.write(
                "public class "
                    + classname
                    + getExtendsPart()
                    + getimplementsPart()
                    + "{\n");

            writeAttributes();
            writeConstructors();
            writeMethods();
            this.writer.write("}\n");
            //cleanup
            writer.flush();
            writer.close();
            System.out.println(getJavaFilePath().getAbsolutePath() + " created.....");

        } catch (IOException e) {
            e.printStackTrace();
            throw new WrapperFault(e);
        }

    }
    protected String getExtendsPart() {
        return " ";
    }
    protected String getimplementsPart() {
        return " ";
    }
    protected void writeClassComment() throws WrapperFault {
    }
    protected void writeImportStatements() throws WrapperFault {
    }
    protected abstract void writeAttributes() throws WrapperFault;
    protected abstract void writeConstructors() throws WrapperFault;
    protected abstract void writeMethods() throws WrapperFault;
    protected final File getJavaFilePath() throws WrapperFault{   	
		if(!targetDirectory.endsWith("/"))
			targetDirectory = targetDirectory + "/";
 
    	String path = targetDirectory + packageName.replace('.','/');
    	File packagefolder  = new File(path);
    	if(!packagefolder.exists())
			packagefolder.mkdirs();
 	  	return new File(path+"/"+classname+".java");
    }
}
