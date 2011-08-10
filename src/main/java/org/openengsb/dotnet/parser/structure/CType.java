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
public abstract class CType<T> {
    private String originalName;
    private String fullName;
    private List<T> entries = new ArrayList<T>();
    private boolean simpleType = false;

    public CType(String name) {
        originalName = fullName = name;
    }

    /**
     * @return the simpleType
     */
    public boolean isSimpleType() {
        return simpleType;
    }

    /**
     * @param simpleType the simpleType to set
     */
    public void setSimpleType(boolean simpleType) {
        this.simpleType = simpleType;
    }

    /**
     * @return the fullName
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * @param fullName the fullName to set
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * @return the entries
     */
    public List<T> getEntries() {
        return entries;
    }

    /**
     * @param entries the entries to set
     */
    public void setEntries(List<T> entries) {
        this.entries = entries;
    }

    public String getOriginalName() {
        return originalName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return ((fullName == null) ? 0 : fullName.hashCode());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CType<?>)) {
            return false;
        }
        CType<?> other = (CType<?>) obj;
        if (fullName == null) {
            if (other.fullName != null) {
                return false;
            }
        } else if (!fullName.equals(other.fullName)) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Type [fullName=" + fullName + "]";
    }
}
