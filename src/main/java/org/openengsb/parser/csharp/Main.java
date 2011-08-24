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
import java.net.URL;
import java.net.URLClassLoader;
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

	private static final int readerId 		= 1;
	private static final int readerConfigId = 2;
	private static final int filterId	 	= 3;
	private static final int writerId 		= 2;
	private static final int writerConfigId = 1;

    /**
     * @param args
     */
    public static void main(String[] args) {
		System.out.println("Current Classpath: " + args[0]);
		
		Collection<CType<?>> types;
		String[] strUrls = args[0].split(":");
		URL[] urls = new URL[strUrls.length];
		
        try {
			for(int i = 0; i < strUrls.length; i++) {
				urls[i] = new URL("file", "", strUrls[i]);
			}
			
			URLClassLoader cl = new URLClassLoader(urls);

			Reader r;
			r = (Reader)Class.forName(args[readerId]).newInstance();
			r.initialize(args[readerConfigId], cl);

            types = r.process();
            printErrors(r.getErrors(), r.getClass());

            Filter f;
			for (int i = filterId; i < args.length - writerId; i += 2) {
				f = (Filter)Class.forName(args[i]).newInstance();
				f.initialize(args[i + 1]);
				
				types = f.process(types);
				printErrors(f.getErrors(), f.getClass());
			}
			
			Writer w = (Writer)Class.forName(args[args.length - writerId]).newInstance();
			w.initialize(args[args.length - writerConfigId]);

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
