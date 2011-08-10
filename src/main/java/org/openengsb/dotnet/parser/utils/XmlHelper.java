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
package org.openengsb.dotnet.parser.utils;

import java.io.File;
import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author peter
 * 
 */
public class XmlHelper {
    public static Document getDocument(String filePath) throws SAXException, IOException, ParserConfigurationException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(filePath));
    }

    public static NodeList executeXPath(Document dom, String xpath) throws XPathExpressionException {
        return (NodeList) executeXPath(dom, xpath, XPathConstants.NODESET);
    }

    public static Object executeXPath(Document dom, String xpath, QName returnType) throws XPathExpressionException {
        XPathExpression expr = XPathFactory.newInstance().newXPath().compile(xpath);
        return expr.evaluate(dom, returnType);
    }
}
