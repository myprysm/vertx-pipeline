/*
 * Copyright 2018 the original author or the original authors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package fr.myprysm.pipeline.util;

import fr.myprysm.pipeline.processor.Processor;
import fr.myprysm.pipeline.pump.Pump;
import fr.myprysm.pipeline.sink.Sink;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ClasspathHelpers {
    private static final Logger LOG = LoggerFactory.getLogger(ClasspathHelpers.class);
    private static ScanResult scan;
    private static List<String> processorClassNames;
    private static List<String> sinkClassNames;
    private static List<String> pumpClassNames;

    private ClasspathHelpers() {
    }

    /**
     * Get the scan result of all the classes contained in the application.
     *
     * @return the scan result
     */
    public synchronized static ScanResult getScan() {
        if (scan == null) {
            LOG.info("Scanning classpath...");
            scan = new FastClasspathScanner().scan();
            LOG.info("Classpath scanned.");
        }

        return scan;
    }



//    public static List<Class<Processor>> getProcessors() {
//        return getProcessorClassNames().stream()
//                .map(ClasspathHelpers::<Processor>toClass)
//                .collect(toList());
//    }

    public synchronized static List<String> getProcessorClassNames() {
        if (processorClassNames == null) {
            processorClassNames = getScan().getNamesOfClassesImplementing(Processor.class);
            LOG.info("Processors scanned.");
        }

        return processorClassNames;
    }

    public synchronized static List<String> getSinkClassNames() {
        if (sinkClassNames == null) {
            sinkClassNames = getScan().getNamesOfClassesImplementing(Sink.class);
            LOG.info("Sinks scanned.");
        }

        return sinkClassNames;
    }

    public synchronized static List<String> getPumpClassNames() {
        if (pumpClassNames == null) {
            pumpClassNames = getScan().getNamesOfClassesImplementing(Pump.class);
            LOG.info("Pumps scanned.");
        }

        return pumpClassNames;
    }

}
