#
# This ProGuard configuration file illustrates how to process applications.
# Usage:
#     java -jar proguard.jar @applications.pro
#

-verbose

# Specify the input jars, output jars, and library jars.

-injars  ../../.export/dedd.jar
-outjars ../../.export/dedd_pg.jar

# Before Java 9, the runtime classes were packaged in a single jar file.
#-libraryjars <java.home>/lib/rt.jar

# As of Java 9, the runtime classes are packaged in modular jmod files.
#-libraryjars <java.home>/jmods/java.base.jmod(!**.jar;!module-info.class)
-libraryjars D:\Program Files\Java\jdk-18/jmods/
-libraryjars ../../lib/

#-libraryjars junit.jar
#-libraryjars servlet.jar
#-libraryjars jai_core.jar
#...

# Save the obfuscation mapping to a file, so you can de-obfuscate any stack
# traces later on. Keep a fixed source file attribute and all line number
# tables to get line numbers in the stack traces.
# You can comment this out if you're not interested in stack traces.

#-printmapping out.map
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

# Preserve all annotations.

-keepattributes *Annotation*

# You can print out the seeds that are matching the keep options below.

#-printseeds out.seeds

# Preserve all public applications.

-keepclasseswithmembers public class * {
    public static void main(java.lang.String[]);
}

# Preserve all native method names and the names of their classes.

-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}

# Preserve the special static methods that are required in all enumeration
# classes.

-keepclassmembers,allowoptimization enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Explicitly preserve all serialization members. The Serializable interface
# is only a marker interface, so it wouldn't save them.
# You can comment this out if your application doesn't use serialization.
# If your code contains serializable classes that have to be backward 
# compatible, please refer to the manual.

 -keepclassmembers class * implements java.io.Serializable {
     static final long serialVersionUID;
 }

# Your application may contain more items that need to be preserved; 
# typically classes that are dynamically created using Class.forName:

-keep public class tk.kqstone.dedd.Teeth { *; }
-keep public class tk.kqstone.dedd.Tooth { *; }
-keep public class tk.kqstone.dedd.Rect2D { *; }
-keep public class tk.kqstone.dedd.Rect2D$* { *; }

# -keep public interface com.example.MyInterface
# -keep public class * implements com.example.MyInterface
