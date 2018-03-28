/*
 * Copyright 2018 the original author or the original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.myprysm.pipeline.util;

import fr.myprysm.pipeline.datasource.DatasourceConfig;
import fr.myprysm.pipeline.datasource.DatasourceConfiguration;
import fr.myprysm.pipeline.processor.Accumulator;
import fr.myprysm.pipeline.processor.Processor;
import fr.myprysm.pipeline.pump.Pump;
import fr.myprysm.pipeline.sink.Sink;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClasspathHelpers {
    private static final Logger LOG = LoggerFactory.getLogger(ClasspathHelpers.class);
    private static ScanResult scan;
    private static List<String> processorClassNames;
    private static List<String> sinkClassNames;
    private static List<String> pumpClassNames;
    private static List<String> accumulatorClassNames;
    private static Map<String, String> aliasToComponents;
    private static List<String> datasourceComponentClassNames;

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

    public static List<Class<?>> toClasses(List<String> classes) {
        return getScan().classNamesToClassRefs(classes);
    }

    public synchronized static String getDatasourceConfigurationForAlias(String alias) {
        String clazz = getComponentFromAlias(alias);
        if (clazz != null && !DatasourceConfiguration.class.isAssignableFrom(getScan().classNameToClassRef(clazz))) {
            clazz = null;
        }

        return clazz;
    }


    public synchronized static String getSinkForAlias(String alias) {
        String clazz = getComponentFromAlias(alias);
        if (clazz != null && !Sink.class.isAssignableFrom(getScan().classNameToClassRef(clazz))) {
            clazz = null;
        }

        return clazz;
    }

    public synchronized static String getProcessorForAlias(String alias) {
        String clazz = getComponentFromAlias(alias);
        if (clazz != null && !Processor.class.isAssignableFrom(getScan().classNameToClassRef(clazz))) {
            clazz = null;
        }

        return clazz;
    }

    public synchronized static String getPumpForAlias(String alias) {
        String clazz = getComponentFromAlias(alias);
        if (clazz != null && !Pump.class.isAssignableFrom(getScan().classNameToClassRef(clazz))) {
            clazz = null;
        }

        return clazz;
    }

    public synchronized static String getComponentFromAlias(String alias) {
        return getAliasToComponents().get(alias);
    }

    public synchronized static Map<String, String> getAliasToComponents() {
        if (aliasToComponents == null) {
            aliasToComponents = new ConcurrentHashMap<>();
            ScanResult scan = getScan();
            scan.classNamesToClassRefs(scan.getNamesOfClassesWithAnnotation(Alias.class)).forEach(clazz -> {
                if (Processor.class.isAssignableFrom(clazz) || Pump.class.isAssignableFrom(clazz) || Sink.class.isAssignableFrom(clazz)) {
                    Alias anAlias = clazz.getAnnotation(Alias.class);
                    String alias = (anAlias.prefix() + '.' + anAlias.name()).toLowerCase();
                    if (aliasToComponents.putIfAbsent(alias, clazz.getName()) == null) {
                        LOG.info("Mapped {} to {}", alias, clazz.getName());
                    } else {
                        LOG.error("Alias {} cannot be bound to {}. Already mapped to {}", alias, clazz.getName(), aliasToComponents.get(alias));
                    }
                }
            });
        }

        return aliasToComponents;
    }

    public synchronized static List<String> getDatasourceComponentClassNames() {
        if (datasourceComponentClassNames == null) {
            datasourceComponentClassNames = getScan().getNamesOfClassesWithAnnotation(DatasourceConfig.class);
        }
        return datasourceComponentClassNames;
    }

    /**
     * Get the list of classes annotated with {@link Accumulator}
     * to ensure during startup that those components will be only instanciated once.
     *
     * @return the list of classes annotated {@link Accumulator}
     */
    public synchronized static List<String> getAccumulatorClassNames() {
        if (accumulatorClassNames == null) {
            accumulatorClassNames = getScan().getNamesOfClassesWithAnnotation(Accumulator.class);
        }
        return accumulatorClassNames;
    }

    /**
     * Get the list of classes implementing {@link Processor}
     * to validate during startup each element in a pipeline chain are valids.
     *
     * @return the list of classes implementing {@link Processor}
     */
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
