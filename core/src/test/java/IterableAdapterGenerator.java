import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

import java.util.HashSet;
import java.util.Set;

public class IterableAdapterGenerator {
    public static void main(String[] args) {

        try (ScanResult scanResult = new ClassGraph()
                .enableSystemJarsAndModules()
                .enableAllInfo()
                .acceptPackages("java.util")
                .scan()) {
            ClassInfoList classInfoList = scanResult.getClassesImplementing("java.util.Collection").filter(classInfo -> classInfo.isOuterClass() && classInfo.isPublic() && !classInfo.isAbstract());
            Set<String> set = new HashSet<>();
            for (ClassInfo classInfo : classInfoList) {
                for (ClassInfo infoInterface : classInfo.getInterfaces().filter(ci -> ci.getName().startsWith("java.util"))) {
                    set.add(infoInterface.getName());
                }
                if (classInfo.getSuperclass() != null)
                    System.out.println(classInfo.getSuperclass().getName()+" --> "+classInfo.getName());
            }
            for (String s : set) {
                System.out.println(s);
            }
        }
    }

    public static void dfs(Class<?> c) {
    }
}
