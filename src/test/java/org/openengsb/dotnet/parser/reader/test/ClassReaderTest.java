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

package org.openengsb.dotnet.parser.reader.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openengsb.dotnet.parser.reader.ClassReader;
import org.openengsb.dotnet.parser.structure.CType;

public class ClassReaderTest {

    private ClassReader reader;

    @Before
    public void setUp() throws Exception {
        reader = new ClassReader();
    }

    /**
     * Test method for {@link org.openengsb.dotnet.parser.reader.ClassReader#initialize(java.lang.String)}.
     */
    @Test
    @Ignore
    public final void testInitialize() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link org.openengsb.dotnet.parser.reader.ClassReader#process()}.
     */
    @Test
    @Ignore
    public final void testProcess() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link org.openengsb.dotnet.parser.reader.ClassReader#getErrors()}.
     */
    @Test
    public final void testGetErrors() {
        assertEquals(0, reader.getErrors().size());
    }

    /**
     * Test method for
     * {@link org.openengsb.dotnet.parser.reader.ClassReader#createAndSaveType(java.lang.Class, boolean)}.
     */
    @Test
    public final void testCreateAndSaveSimpleType() {
        CType<?> type = reader.createAndSaveType(List.class, true);
        CType<?> type2 = reader.createAndSaveType(List.class, true);

        assertEquals(type, type2);

        assertEquals(0, type.getEntries().size());
    }

    /**
     * Test method for
     * {@link org.openengsb.dotnet.parser.reader.ClassReader#createAndSaveType(java.lang.Class, boolean)}.
     */
    @Test
    public final void testCreateAndSaveInterfaceType() {
        int testInterfaceEntriesCount = 1;

        CType<?> type = reader.createAndSaveType(TestInterface1.class, false);
        CType<?> type2 = reader.createAndSaveType(TestInterface1.class, false);

        assertEquals(type, type2);
        assertEquals(testInterfaceEntriesCount, type.getEntries().size());
    }

    /**
     * Test method for
     * {@link org.openengsb.dotnet.parser.reader.ClassReader#createAndSaveType(java.lang.Class, boolean)}.
     */
    @Test
    public final void testCreateAndSaveClassType() {
        int testClassEntriesCount = 1;

        CType<?> type = reader.createAndSaveType(TestClass1.class, false);
        CType<?> type2 = reader.createAndSaveType(TestClass1.class, false);

        assertEquals(type, type2);
        assertEquals(testClassEntriesCount, type.getEntries().size());
    }

    /**
     * Test method for
     * {@link org.openengsb.dotnet.parser.reader.ClassReader#createParametrizedType(java.lang.reflect.Type)}.
     */
    @Test
    @Ignore
    public final void testCreateParametrizedType() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for
     * {@link org.openengsb.dotnet.parser.reader.ClassReader#analyzeType(org.openengsb.dotnet.parser.structure.CType, java.lang.Class)}
     * .
     */
    @Test
    @Ignore
    public final void testAnalyzeType() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link org.openengsb.dotnet.parser.reader.ClassReader#createMethod(java.lang.reflect.Method)}.
     */
    @Test
    @Ignore
    public final void testCreateMethod() {
        fail("Not yet implemented"); // TODO
    }

    public interface TestInterface1 {
        public void publicMethodTest();
    }

    public class TestClass1 {
        private void privateMethodTest() {
        }

        public void publicMethodTest() {
        }
    }
}
