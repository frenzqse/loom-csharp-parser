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

import java.util.ArrayList;
import java.util.List;

/**
 * @author peter
 * 
 */
public class CMethod extends CTypeEntry {
    private List<CParameter> parameters = new ArrayList<CParameter>();

    public CMethod(String name) {
        super(name);
    }

    public CProperty convertToProperty() {
        return new CProperty(this);
    }

    /**
     * @return the parameters
     */
    public List<CParameter> getParameters() {
        return parameters;
    }

    /**
     * @param parameters the parameters to set
     */
    public void setParameters(List<CParameter> parameters) {
        this.parameters = parameters;
    }
}
