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
 
package org.apache.axis.description;

public interface Parameter {
    public static int TEXT_PARAMETER = 0;
    public static int DOM_PARAMETER = 1;

    public String getName();

    public Object getValue();

    public void setName(String name);

    public void setValue(String value);

    public boolean isLocked();

    public void setLocked(boolean value);

    public int getParameterType();
}
