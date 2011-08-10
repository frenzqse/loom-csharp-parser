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
package org.openengsb.parser.csharp;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.openengsb.parser.csharp.filter.Filter;
import org.openengsb.parser.csharp.reader.Reader;
import org.openengsb.parser.csharp.structure.CType;
import org.openengsb.parser.csharp.writer.Writer;

/**
 * @author peter
 * 
 */
public class Main {
    private static int statusCode = 0;

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("Current Classpath: " + System.getProperty("java.class.path"));

        Collection<CType<?>> types;
        try {
            Reader r;
            r = (Reader) Class.forName(args[0]).newInstance();
            r.initialize(args[1]);

            types = r.process();
            printErrors(r.getErrors(), r.getClass());

            Filter f;
            for (int i = 2; i < args.length - 2; i += 2) {
                f = (Filter) Class.forName(args[i]).newInstance();
                f.initialize(args[i + 1]);

                types = f.process(types);
                printErrors(f.getErrors(), f.getClass());
            }

            Writer w = (Writer) Class.forName(args[args.length - 2]).newInstance();
            w.initialize(args[args.length - 1]);

            w.process(types);
            printErrors(w.getErrors(), w.getClass());
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            statusCode = 1;
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            statusCode = 1;
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            statusCode = 1;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            statusCode = 1;
        }

        if (statusCode != 0) {
            System.exit(statusCode);
        }
    }

    private static void printErrors(List<String> errors, Class<?> source) {
        boolean errorPrinted = false;

        for (String err : errors) {
            errorPrinted = true;
            System.err.println("Error in " + source.getName() + ": " + err);
        }

        if (!errorPrinted) {
            System.out.println(source.getName() + " finished without any errors.");
        } else {
            statusCode = 1;
        }
    }
}
