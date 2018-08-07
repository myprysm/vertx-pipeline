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

import fr.myprysm.pipeline.processor.Accumulator;
import fr.myprysm.pipeline.processor.Processor;
import fr.myprysm.pipeline.pump.CronEmitter;
import fr.myprysm.pipeline.pump.Pump;
import fr.myprysm.pipeline.sink.Sink;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Classpath Helpers for pipelines.
 * <p>
 * Keeps a single reference to the scan to avoid multiple scans.
 */
public final class ClasspathHelpers {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ClasspathHelpers.class);

    /**
     * Globlal class scan result.
     */
    private static ScanResult scan;

    /**
     * List of processor classes.
     */
    private static List<String> processorClassNames;

    /**
     * List of sink classes.
     */
    private static List<String> sinkClassNames;

    /**
     * List of pump classes.
     */
    private static List<String> pumpClassNames;

    /**
     * List of accumulator classes.
     */
    private static List<String> accumulatorClassName;

    /**
     * List of cron emitter classes.
     */
    private static List<String> cronEmitterClassName;

    /**
     * Map alias to component class.
     */
    private static Map<String, String> aliasToComponents;

    /**
     * utility class...
     */
    private ClasspathHelpers() {
        //
    }

    /**
     * Get the scan result of all the classes contained in the application.
     *
     * @return the scan result
     */
    public static synchronized ScanResult getScan() {
        if (scan == null) {
            LOG.info("Scanning classpath...");
            scan = new FastClasspathScanner().scan();
            LOG.info("Classpath scanned.");
        }

        return scan;
    }

    /**
     * Get the {@link Sink} matching the provided alias or <code>null</code>.
     *
     * @param alias the alias
     * @return the class name
     */
    public static synchronized String getSinkForAlias(String alias) {
        String clazz = getComponentFromAlias(alias);
        if (clazz != null && !Sink.class.isAssignableFrom(getScan().classNameToClassRef(clazz))) {
            clazz = null;
        }

        return clazz;
    }

    /**
     * Get the {@link Processor} matching the provided alias or <code>null</code>.
     *
     * @param alias the alias
     * @return the class name
     */
    public static synchronized String getProcessorForAlias(String alias) {
        String clazz = getComponentFromAlias(alias);
        if (clazz != null && !Processor.class.isAssignableFrom(getScan().classNameToClassRef(clazz))) {
            clazz = null;
        }

        return clazz;
    }

    /**
     * Get the {@link Pump} matching the provided alias or <code>null</code>.
     *
     * @param alias the alias
     * @return the class name
     */
    public static synchronized String getPumpForAlias(String alias) {
        String clazz = getComponentFromAlias(alias);
        if (clazz != null && !Pump.class.isAssignableFrom(getScan().classNameToClassRef(clazz))) {
            clazz = null;
        }

        return clazz;
    }

    /**
     * Get the {@link Pump} matching the provided alias or <code>null</code>.
     *
     * @param alias the alias
     * @return the class name
     */
    public static synchronized String getComponentFromAlias(String alias) {
        return getAliasToComponents().get(alias);
    }

    /**
     * Get the aliased components.
     * <p>
     * The components are annotated with {@link Alias}
     * and must implement one of {@link Pump}, {@link Processor}, {@link Sink}.
     *
     * @return a map with the alias as key and the class name as value
     */
    public static synchronized Map<String, String> getAliasToComponents() {
        if (aliasToComponents == null) {
            aliasToComponents = new ConcurrentHashMap<>();
            ScanResult scanResult = getScan();
            scanResult.classNamesToClassRefs(scanResult.getNamesOfClassesWithAnnotation(Alias.class)).forEach(clazz -> {
                if (Processor.class.isAssignableFrom(clazz) || Pump.class.isAssignableFrom(clazz) || Sink.class.isAssignableFrom(clazz)) {
                    Alias annot = clazz.getAnnotation(Alias.class);
                    String alias = (annot.prefix() + '.' + annot.name()).toLowerCase();
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

    /**
     * Get the list of classes annotated with {@link Accumulator}
     * to ensure during startup that those components will be only instanciated once.
     *
     * @return the list of classes annotated {@link Accumulator}
     */
    public static synchronized List<String> getAccumulatorClassNames() {
        if (accumulatorClassName == null) {
            accumulatorClassName = getScan().getNamesOfClassesWithAnnotation(Accumulator.class);
        }
        return accumulatorClassName;
    }

    /**
     * Get the list of classes implementing {@link Processor}
     * to validate during startup each element in a pipeline chain are valids.
     *
     * @return the list of classes implementing {@link Processor}
     */
    public static synchronized List<String> getProcessorClassNames() {
        if (processorClassNames == null) {
            processorClassNames = getScan().getNamesOfClassesImplementing(Processor.class);
            LOG.info("Processors scanned.");
        }

        return processorClassNames;
    }

    /**
     * Get the list of classes implementing {@link Sink}
     * to validate during startup each element in a pipeline chain are valids.
     *
     * @return the list of classes implementing {@link Sink}
     */
    public static synchronized List<String> getSinkClassNames() {
        if (sinkClassNames == null) {
            sinkClassNames = getScan().getNamesOfClassesImplementing(Sink.class);
            LOG.info("Sinks scanned.");
        }

        return sinkClassNames;
    }

    /**
     * Get the list of classes implementing {@link Pump}
     * to validate during startup each element in a pipeline chain are valids.
     *
     * @return the list of classes implementing {@link Pump}
     */
    public static synchronized List<String> getPumpClassNames() {
        if (pumpClassNames == null) {
            pumpClassNames = getScan().getNamesOfClassesImplementing(Pump.class);
            LOG.info("Pumps scanned.");
        }

        return pumpClassNames;
    }

    /**
     * Get the list of classes implementing {@link CronEmitter}
     * to validate during startup each element in a pipeline chain are valids.
     *
     * @return the list of classes implementing {@link CronEmitter}
     */
    public static synchronized List<String> getCronEmitterClassNames() {
        if (cronEmitterClassName == null) {
            cronEmitterClassName = getScan().getNamesOfSubclassesOf(CronEmitter.class);
        }

        return cronEmitterClassName;
    }
}
