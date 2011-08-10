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
package org.openengsb.parser.csharp.writer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openengsb.parser.csharp.structure.CClass;
import org.openengsb.parser.csharp.structure.CEnum;
import org.openengsb.parser.csharp.structure.CInterface;
import org.openengsb.parser.csharp.structure.CMethod;
import org.openengsb.parser.csharp.structure.CParameter;
import org.openengsb.parser.csharp.structure.CParameterizedType;
import org.openengsb.parser.csharp.structure.CProperty;
import org.openengsb.parser.csharp.structure.CType;
import org.openengsb.parser.csharp.structure.CTypeEntry;

public class CSharpWriter implements Writer {
    private Set<String> serializedTypes = new HashSet<String>();
    private List<String> errors = new ArrayList<String>();
    private Collection<CType<?>> processedTypes;
    private String outputDir;

    @Override
    public void initialize(String outputDir) throws IOException {
        this.outputDir = outputDir;
    }

    @Override
    public void process(Collection<CType<?>> types) {
        PrintWriter writer;
        processedTypes = types;

        for (CType<?> t : types) {
            if (!t.isSimpleType() && !serializedTypes.contains(t.getFullName())) {
                try {
                    writer = new PrintWriter(getFile(t));
                    serializeType(t, writer);
                    writer.close();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private File getFile(CType<?> t) {
        if (!outputDir.endsWith(File.separator))
            outputDir = outputDir + File.separator;

        if (!t.getFullName().contains("."))
            return new File(outputDir + t.getFullName() + ".cs");

        // else
        String path, fileName, fullDir;

        fullDir = t.getFullName();
        path = fullDir.substring(0, fullDir.lastIndexOf('.'));
        fileName = fullDir.substring(path.length() + 1);
        fullDir = outputDir + path.replace('.', File.separatorChar);

        new File(fullDir).mkdirs();
        return new File(fullDir + File.separator + fileName + ".cs");
    }

    private void serializeType(CType<?> t, PrintWriter pw) {
        // using System;
        // using System.Collections.Generic;
        // using System.Text;
        //
        // namespace ClassLibrary1
        // {
        // public class Class1
        // {
        // public class SubClass1
        // {
        // }
        // }
        // }

        pw.println("using System;");
        pw.println("using System.Collections.Generic;");
        pw.println("using System.Text;");
        pw.println();
        pw.println("namespace " + getNamespace(t));
        pw.println("{");
        serializeSubType(t, pw);
        pw.println("}");

    }

    private void serializeSubType(CType<?> t, PrintWriter pw) {
        pw.println("	//[OriginalName(\"" + t.getOriginalName() + "\")]");

        if (t instanceof CClass)
            serializeClass((CClass) t, pw);
        else if (t instanceof CInterface)
            serializeInterface((CInterface) t, pw);
        else if (t instanceof CEnum)
            serializeEnum((CEnum) t, pw);
        else
            errors.add("Type " + t.getClass() + " is not supported.");

        serializedTypes.add(t.getFullName());
    }

    private void findSubTypesToSerialize(CType<?> t, PrintWriter pw) {
        String searchedPrefix = t.getFullName() + ".";

        for (CType<?> subT : processedTypes) {
            if (subT.getFullName().startsWith(searchedPrefix) &&
                    subT.getFullName().lastIndexOf('.') == (searchedPrefix.length() - 1)) {
                serializeSubType(subT, pw);
            }
        }
    }

    private void serializeEnum(CEnum t, PrintWriter pw) {
        pw.println("	public enum " + getClassName(t));
        pw.println("	{");

        List<String> lstVals = t.getEntries();

        for (int i = 0; i < lstVals.size(); i++) {
            pw.print("		" + lstVals.get(i));

            if (i < (lstVals.size() - 1)) {
                pw.println(',');
            } else {
                pw.println();
            }
        }

        pw.println("	}");
    }

    private void serializeClass(CClass t, PrintWriter pw) {
        pw.println("	public class " + getClassName(t)
                + getExteandsAndImplementsString(t.getSuperClass(), t.getInterfaces()));
        pw.println("	{");

        for (CTypeEntry te : t.getEntries()) {
            serializeEntry(te, pw);
            pw.println();
        }

        findSubTypesToSerialize(t, pw);

        pw.println("	}");
    }

    private void serializeInterface(CInterface t, PrintWriter pw) {
        pw.println("	public interface " + getClassName(t) + getExteandsAndImplementsString(null, t.getInterfaces()));
        pw.println("	{");

        for (CTypeEntry te : t.getEntries()) {
            serializeEntrySignature(te, pw);
            pw.println();
        }

        findSubTypesToSerialize(t, pw);

        pw.println("	}");
    }

    private String getExteandsAndImplementsString(CParameterizedType superClass,
            Collection<CParameterizedType> interfaces) {
        StringBuilder sb = new StringBuilder();

        if (superClass != null) {
            sb.append(" : ");
            sb.append(parametrizedTypeToString(superClass));
        }

        if (interfaces != null && !interfaces.isEmpty()) {
            for (CParameterizedType pt : interfaces) {
                if (sb.length() == 0) {
                    sb.append(" : ");
                } else {
                    sb.append(", ");
                }

                sb.append(parametrizedTypeToString(pt));
            }
        }

        return sb.toString();
    }

    private String getNamespace(CType<?> t) {
        String fullName = t.getFullName();
        int lastDot = fullName.lastIndexOf('.');

        if (lastDot == -1)
            return "TestNamespace";
        else
            return fullName.substring(0, lastDot);
    }

    private String getClassName(CType<?> t) {
        String fullName = t.getFullName();
        int lastDot = fullName.lastIndexOf('.');

        return fullName.substring(lastDot + 1);
    }

    @Override
    public List<String> getErrors() {
        return errors;
    }

    private void serializeEntrySignature(CTypeEntry te, PrintWriter pw) {
        pw.println("		//[OriginalName(\"" + te.getOriginalName() + "\")]");

        if (te instanceof CMethod) {
            pw.println("		" + createMethodSignature((CMethod) te));
        } else {
            pw.println("		" + te.getName() + "{ get; set; }");
        }
    }

    private void serializeEntry(CTypeEntry te, PrintWriter pw) {
        pw.println("		//[OriginalName(\"" + te.getOriginalName() + "\")]");

        if (te instanceof CMethod) {
            errors.add("Method-Entries can only be serialized to a signature. (Method: " + te.toString() + ")");
        } else {
            serializeProperty((CProperty) te, pw);
        }
    }

    private void serializeProperty(CProperty prop, PrintWriter pw) {
        String fieldName = prop.getOriginalName();
        String strType = parametrizedTypeToString(prop.getReturnType());

        fieldName = '_' + fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);

        pw.println("		public " + strType + " " + prop.getName());
        pw.println(" 		{");
        pw.println("			get { return " + fieldName + "; }");
        pw.println("			set { " + fieldName + " = value; }");
        pw.println("		}");
        pw.println();
        pw.println("		private " + strType + " " + fieldName + ";");
    }

    private String createMethodSignature(CMethod m) {
        StringBuilder sb = new StringBuilder("");

        sb.append(parametrizedTypeToString(m.getReturnType()));
        sb.append(' ');
        sb.append(m.getName());
        sb.append('(');

        List<CParameter> params = m.getParameters();

        if (params != null && params.size() > 0) {
            boolean isFirst = true;
            for (CParameter p : params) {
                if (!isFirst) {
                    sb.append(", ");
                } else {
                    isFirst = false;
                }

                sb.append(parametrizedTypeToString(p.getType()));
                sb.append(' ');
                sb.append(p.getName());
            }
        }

        sb.append(");");

        return sb.toString();
    }

    private String parametrizedTypeToString(CParameterizedType t) {
        StringBuilder sb = new StringBuilder();

        if (t.getType() == null)
            sb.append('T');
        else
            sb.append(t.getType().getFullName());

        List<CParameterizedType> genericTypes = t.getGenericTypes();

        if (genericTypes != null && genericTypes.size() > 0) {
            sb.append('<');

            boolean isFirst = true;
            for (CParameterizedType gt : genericTypes) {
                if (!isFirst) {
                    sb.append(", ");
                } else {
                    isFirst = false;
                }

                sb.append(parametrizedTypeToString(gt));
            }

            sb.append('>');
        }

        return sb.toString();
    }
}
