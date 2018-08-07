package fr.myprysm.pipeline.kotlin.processor

import fr.myprysm.pipeline.processor.LogProcessorOptions
import org.slf4j.event.Level

/**
 * A function providing a DSL for building [fr.myprysm.pipeline.processor.LogProcessorOptions] objects.
 *
 * Log Processor options.
 * <p>
 * They extend directly base [fr.myprysm.pipeline.processor.ProcessorOptions] and provide
 * the ability to set the acceptable level to log incoming messages.
 *
 * @param instances  The number of instances to deploy. <p> Must be a positive [java.lang.Integer].
 * @param level  The log  to write the incoming items. <p> Defaults to <code>DEBUG</code>. <p> One of: <ul> <li><code>TRACE</code></li> <li><code>DEBUG</code></li> <li><code>INFO</code></li> <li><code>WARN</code></li> <li><code>ERROR</code></li> </ul>
 * @param name  The name of the processor. <p> This is automatically populated when the pipeline configuration is a list. <p> You still can name your processor for any purpose by using a map instead of a list when you describe your pipeline.
 * @param type  The type of the processor. <p> This is the fully qualified name of the <code>class</code> that acts as processor.
 *
 * <p/>
 * NOTE: This function has been automatically generated from the [fr.myprysm.pipeline.processor.LogProcessorOptions original] using Vert.x codegen.
 */
fun LogProcessorOptions(
        instances: Int? = null,
        level: Level? = null,
        name: String? = null,
        type: String? = null): LogProcessorOptions = fr.myprysm.pipeline.processor.LogProcessorOptions().apply {

    if (instances != null) {
        this.setInstances(instances)
    }
    if (level != null) {
        this.setLevel(level)
    }
    if (name != null) {
        this.setName(name)
    }
    if (type != null) {
        this.setType(type)
    }
}

