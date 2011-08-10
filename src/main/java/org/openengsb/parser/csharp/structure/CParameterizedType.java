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
package org.openengsb.parser.csharp.structure;

import java.util.List;

/**
 * @author peter
 * 
 */
public class CParameterizedType {
    private List<CParameterizedType> genericTypes;
    private CType<?> type;

    /**
     * @return the genericTypes
     */
    public List<CParameterizedType> getGenericTypes() {
        return genericTypes;
    }

    /**
     * @param genericTypes the genericTypes to set
     */
    public void setGenericTypes(List<CParameterizedType> genericTypes) {
        this.genericTypes = genericTypes;
    }

    /**
     * @return the type
     */
    public CType<?> getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(CType<?> type) {
        this.type = type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = ((genericTypes == null) ? 0 : genericTypes.hashCode());
        result += ((type == null) ? 0 : type.hashCode());
        return result;
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
        if (!(obj instanceof CParameterizedType)) {
            return false;
        }
        CParameterizedType other = (CParameterizedType) obj;
        if (genericTypes == null) {
            if (other.genericTypes != null) {
                return false;
            }
        } else if (!genericTypes.equals(other.genericTypes)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
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
        return "CParameterizedType [type=" + type + ", genericTypes="
                + genericTypes + "]";
    }
}
