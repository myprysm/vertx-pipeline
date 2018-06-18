package fr.myprysm.pipeline.groovy;
import groovy.lang.MetaMethod;
import org.codehaus.groovy.runtime.m12n.ExtensionModule;
import org.codehaus.groovy.runtime.m12n.MetaInfExtensionModule;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
public class VertxExtensionModule extends ExtensionModule {
  private static final String extensionClasses = "fr.myprysm.pipeline.groovy.pipeline.PipelineService_GroovyExtension";
  private static final String staticExtensionClasses = "";
  private final ExtensionModule delegate;  public VertxExtensionModule() {
    super("fr.myprysm.pipeline", "3.5.0");
    Properties props = new Properties();
    props.put("moduleName", "fr.myprysm.pipeline");
    props.put("moduleVersion", "3.5.0");
    props.put("extensionClasses", extensionClasses);
    props.put("staticExtensionClasses", staticExtensionClasses);
    delegate = MetaInfExtensionModule.newModule(props, getClass().getClassLoader());
  }
  public List<MetaMethod> getMetaMethods() {
    return delegate.getMetaMethods();
  }
}