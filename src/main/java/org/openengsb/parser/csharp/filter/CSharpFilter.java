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
package org.openengsb.parser.csharp.filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openengsb.parser.csharp.structure.CClass;
import org.openengsb.parser.csharp.structure.CEnum;
import org.openengsb.parser.csharp.structure.CInterface;
import org.openengsb.parser.csharp.structure.CMethod;
import org.openengsb.parser.csharp.structure.CType;
import org.openengsb.parser.csharp.structure.CTypeEntry;

/**
 * @author peter
 * 
 */
public class CSharpFilter implements Filter {
    private Map<String, String> typeTranslation = new HashMap<String, String>();
    private List<String> errors = new ArrayList<String>();

    /*
     * (non-Javadoc)
     * 
     * @see org.openengsb.dotnet.parser.filter.Filter#initialize(java.lang.String)
     */
    @Override
    public void initialize(String configFile) throws IOException {
        String line;
        String[] values;
        BufferedReader br = new BufferedReader(new FileReader(new File(configFile)));

        while ((line = br.readLine()) != null) {
            values = line.split("=");
            typeTranslation.put(values[0].trim(), values[1].trim());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openengsb.dotnet.parser.filter.Filter#process()
     */
    @SuppressWarnings("unchecked")
    @Override
    public Collection<CType<?>> process(Collection<CType<?>> types) {
        for (CType<?> t : types) {
            if (typeTranslation.containsKey(t.getFullName())) {
                t.setFullName(typeTranslation.get(t.getFullName()));
            } else if (!t.isSimpleType()) {
                StringBuilder sb = new StringBuilder(t.getFullName().replace('$', '.'));

                for (int i = 0; i > -1; i = sb.indexOf(".", i)) {
                    if (i > 0)
                        i++;

                    sb.setCharAt(i, Character.toUpperCase(sb.charAt(i)));
                }

                t.setFullName(sb.toString());

                if (!(t instanceof CEnum)) {
                    if (t instanceof CInterface) {
                        t.setFullName(toCSharpInterfaceName(t.getFullName()));
                    }

                    for (CTypeEntry te : ((CType<CTypeEntry>) t).getEntries()) {
                        te.setName(toCSharpName(te.getName()));
                    }

                    if (t instanceof CClass)
                        convertToProperties((CClass) t);
                }
            }
        }

        return types;
    }

    private String toCSharpName(String name) {
        String head = name.substring(0, 1);
        String tail = name.substring(1);

        head = head.toUpperCase();

        return head + tail;
    }

    private String toCSharpInterfaceName(String name) {
        int initIndex = name.lastIndexOf('.') + 1;
        String head = name.substring(0, initIndex);
        String tail = name.substring(initIndex);

        return head + 'I' + tail;
    }

    private void convertToProperties(CClass t) {
        Map<String, CMethod> properties = new HashMap<String, CMethod>();
        CMethod method, otherMethod, setMethod, getMethod;
        String name;
        int length;

        List<CTypeEntry> toDelete = new ArrayList<CTypeEntry>();
        List<CTypeEntry> toAdd = new ArrayList<CTypeEntry>();

        for (CTypeEntry te : t.getEntries()) {
            name = te.getName();

            if (te instanceof CMethod && (name.startsWith("Get") || name.startsWith("Set") || name.startsWith("Is"))) {
                method = (CMethod) te;
                length = 3;

                if (name.startsWith("Is"))
                    length = 2;

                if (name.startsWith("Set")) {
                    if (method.getParameters().size() != 1 || method.getReturnType().getType().getFullName() != "void")
                        continue;
                } else {
                    if (method.getParameters().size() != 0)
                        continue;
                }

                name = name.substring(length);
                if (properties.containsKey(name)) {
                    otherMethod = properties.get(name);

                    if (method.getName().startsWith("Set")) {
                        setMethod = method;
                        getMethod = otherMethod;
                    } else if (otherMethod.getName().startsWith("Set")) {
                        setMethod = otherMethod;
                        getMethod = method;
                    } else {
                        properties.remove(name);
                        continue;
                    }

                    System.out.println("Getter: " + getMethod + "; Setter: " + setMethod);
                    if (getMethod.getReturnType().equals(setMethod.getParameters().get(0).getType())) {
                        toAdd.add(getMethod.convertToProperty());
                        toDelete.add(method);
                        toDelete.add(otherMethod);
                    }

                    properties.remove(name);
                } else {
                    properties.put(name, method);
                }
            }
        }

        t.getEntries().removeAll(toDelete);
        t.getEntries().addAll(toAdd);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openengsb.dotnet.parser.filter.Filter#getErrors()
     */
    @Override
    public List<String> getErrors() {
        // TODO Auto-generated method stub
        return errors;
    }

}
