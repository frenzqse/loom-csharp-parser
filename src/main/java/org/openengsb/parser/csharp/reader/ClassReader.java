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
package org.openengsb.parser.csharp.reader;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.openengsb.parser.csharp.structure.CClass;
import org.openengsb.parser.csharp.structure.CEnum;
import org.openengsb.parser.csharp.structure.CInterface;
import org.openengsb.parser.csharp.structure.CMethod;
import org.openengsb.parser.csharp.structure.CParameter;
import org.openengsb.parser.csharp.structure.CParameterizedType;
import org.openengsb.parser.csharp.structure.CType;
import org.openengsb.parser.csharp.structure.CTypeEntry;
import org.openengsb.parser.csharp.utils.XmlHelper;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author peter
 * 
 */
public class ClassReader implements Reader {
	protected List<String> classNames = new ArrayList<String>();
	protected List<String> errors = new ArrayList<String>();
	protected Map<String, CType<?>> types = new HashMap<String, CType<?>>();
	protected URLClassLoader loader;
	
	/* (non-Javadoc)
	 * @see org.openengsb.dotnet.parser.reader.Reader#initialize(java.lang.String)
	 */
	@Override
	public void initialize(String configFile, URLClassLoader loader) throws IOException {
		Document dom;

//		loader = Thread.currentThread().getContextClassLoader();
		this.loader = loader;
		
		try {
			dom = XmlHelper.getDocument(configFile);
		} catch (SAXException e) {
			throw new IOException("Couldn't process file " + configFile, e);
		} catch (ParserConfigurationException e) {
			throw new IOException("Couldn't process file " + configFile, e);
		}

		NodeList lst;
		String typeName;
		
		try {
			lst = XmlHelper.executeXPath(dom, "//simpleTypes/add");

			for (int i = 0; i < lst.getLength(); i++) {
				try {
					typeName = lst.item(i).getTextContent();
					System.out.println("New simple-type added: " + typeName);
					createAndSaveType(Class.forName(typeName), true);
				} catch (DOMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (XPathExpressionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			lst = XmlHelper.executeXPath(dom, "//typesToParse/add");
			
			for (int i = 0; i < lst.getLength(); i++) {
				typeName = lst.item(i).getTextContent();
				classNames.add(typeName);
			}
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.openengsb.dotnet.parser.reader.Reader#process()
	 */
	@Override
	public Collection<CType<?>> process() {
		boolean onlyInterfaces = false;
		
		errors = new ArrayList<String>();
				
		for (String name : classNames) {
			Class<?> cls = null;
			
			try {
//				createAndSaveType(Class.forName(name), false);
				cls = loader.loadClass(name);
			} catch (ClassNotFoundException e) {
				errors.add(name + " couldn't be found in the classpath. (" + e.getMessage() + ")");
			}
			
			if (cls != null) {
				if (!onlyInterfaces || cls.isInterface()) {
					createAndSaveType(cls, false);
				}
			}
		}

		return types.values();
	}
	
	public List<String> getErrors() {
		return errors;
	}
	
	public CType<?> createAndSaveType(Class<?> cls, boolean isSimple) {
		String name = cls.getName();
		CType<?> ret = null;
		
		if (cls.isPrimitive())
			isSimple = true;
		
		if (types.containsKey(name))
			ret = types.get(name);
		else {			
			if (cls.isInterface()) {
				ret = new CInterface(name);
			} else if (cls.isEnum()) {
				ret = new CEnum(name);
			} else {
				ret = new CClass(name);
			}
			
			ret.setSimpleType(isSimple);
			
			types.put(name, ret);
			
			if (!isSimple)
				System.out.println("New complex-type added: " + name);

			if (!isSimple) {
				analyzeType(ret, cls);
			}
		}
		
		return ret;
	}
	
	public CParameterizedType createParametrizedType(Type type) {
		CParameterizedType ret = new CParameterizedType();
		
		if (type instanceof ParameterizedType) {
			ParameterizedType paramType = (ParameterizedType)type;
			List<CParameterizedType> genericTypes = new ArrayList<CParameterizedType>();
			
			ret.setType(createAndSaveType((Class<?>)paramType.getRawType(), false));
			
			for(Type t : paramType.getActualTypeArguments()) {
				genericTypes.add(createParametrizedType(t));
			}
			
			ret.setGenericTypes(genericTypes);
		} else if (type instanceof Class<?>){
			ret.setType(createAndSaveType((Class<?>)type, false));
		} else {
			System.out.println("und was jetzt??? - " + type);
		}
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public void analyzeType(CType<?> type, Class<?> cls) {
		if (cls.isEnum()) {
			List<String> entries = new ArrayList<String>();
			
			for (Object item : cls.getEnumConstants()) {
				entries.add(item.toString());
			}
			
			((CType<String>)type).setEntries(entries);
		} else { // Type is Class or Interface
			List<CTypeEntry> entries = new ArrayList<CTypeEntry>();
			
			for(Method m : cls.getMethods()) {
				if (m.getDeclaringClass().equals(cls) && !isMethodOverriden(m))
					entries.add(createMethod(m));
			}
			
			((CType<CTypeEntry>)type).setEntries(entries);
			
			Type interfaces[] = cls.getGenericInterfaces();
			
			if (interfaces != null) {
				List<CParameterizedType> lstInterfaces = new Vector<CParameterizedType>();
				
				for(Type t : interfaces) {
					lstInterfaces.add(createParametrizedType(t));
				}
				
				if (cls.isInterface()) {
					((CInterface)type).setInterfaces(lstInterfaces);
				} else {
					((CClass)type).setInterfaces(lstInterfaces);
				}
			}
			
			if (!cls.isInterface()) {
				Type superClass = cls.getGenericSuperclass();
				
				if (superClass != null) {
					((CClass)type).setSuperClass(createParametrizedType(superClass));
				}
			}
		}
	}
	
	public CMethod createMethod(Method method) {
		CMethod ret = new CMethod(method.getName());
		List<CParameter> parameters = new ArrayList<CParameter>();
		
		ret.setReturnType(createParametrizedType(method.getGenericReturnType()));
				
		int argCount = 0;
		
		for(Type t : method.getGenericParameterTypes()) {
			parameters.add(new CParameter("arg" + argCount, createParametrizedType(t)));			
			argCount++;
		}
		
		ret.setParameters(parameters);
		return ret;
	}
	
	public static boolean isMethodOverriden(Method myMethod) {
	    Class<?> declaringClass = myMethod.getDeclaringClass();
	    if (declaringClass.equals(Object.class)) {
	        return false;
	    } else {
	    	declaringClass = declaringClass.getSuperclass();
	    	
	    	if (declaringClass == null)
	    		return false;
	    	else {
			    try {
			        declaringClass.getMethod(myMethod.getName(), myMethod.getParameterTypes());
			        return true;
			    } catch (NoSuchMethodException e) {
			    	return false;
			    }
	    	}
	    }
	}
}

