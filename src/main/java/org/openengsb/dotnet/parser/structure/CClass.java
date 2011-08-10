/**
 * Licensed to the Austrian Association for Software Tool Integration (AASTI)
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. The AASTI licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openengsb.dotnet.parser.structure;

import java.util.Collection;

/**
 * @author peter
 * 
 */
public class CClass extends CType<CTypeEntry> {
    private Collection<CParameterizedType> interfaces;
    private CParameterizedType superClass;

    public CClass(String name) {
        super(name);
    }

    /**
     * @param interfaces the interfaces to set
     */
    public void setInterfaces(Collection<CParameterizedType> interfaces) {
        this.interfaces = interfaces;
    }

    /**
     * @return the interfaces
     */
    public Collection<CParameterizedType> getInterfaces() {
        return interfaces;
    }

    /**
     * @param superClass the superClass to set
     */
    public void setSuperClass(CParameterizedType superClass) {
        this.superClass = superClass;
    }

    /**
     * @return the superClass
     */
    public CParameterizedType getSuperClass() {
        return superClass;
    }
}
