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

/**
 * @author peter
 * 
 */
public class CParameter {
    private String name;
    private CParameterizedType type;

    public CParameter() {
    }

    public CParameter(String name, CParameterizedType t) {
        this.name = name;
        this.type = t;

        System.out.println("Parameter created with name " + this.name);

        if (this.name.startsWith("arg"))
            try {
                throw new Exception();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    }

    /**
     * @return the name
     */
    public String getName() {
        System.out.println("Parameter name (" + this.name + ") was read.");
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        System.out.println("Parameter name was changed (old:" + this.name + "; new:" + name + ")");
        this.name = name;
    }

    /**
     * @return the type
     */
    public CParameterizedType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(CParameterizedType type) {
        this.type = type;
    }
}
