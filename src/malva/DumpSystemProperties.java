package malva;

import java.util.Properties;

/** Prints all system properties, one key=value pair per line. */
public class DumpSystemProperties {
  public static void main(String[] args) {
    Properties properties = System.getProperties();

    for (String name : properties.stringPropertyNames())
      System.out.println(name + "=" + properties.getProperty(name));
  }
}
