/*
 * Copyright 2004,2005 The Apache Software Foundation.
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

package javax.xml.rpc;

import java.lang.annotation.*;

/**
 * Annotation Type WebFault
 * This annotation can only be applied against classes, interfaces (annotations
 * included), enum declarations and VM will retain this type annotations to 
 * enable use of reflective APIs on them at runtime.
 * @author sunja07
 */
@Target(value=ElementType.TYPE)
@Retention(value=RetentionPolicy.RUNTIME)
public @interface WebFault {
	/**
	 * Fault bean name.
	 */
	String faultBean() default "" ;
	
	/**
	 * Elements local name.
	 */
	String name() default "" ;
	
	/**
	 * Elements namespace name.
	 */
	String targetNamespace() default "" ;
}