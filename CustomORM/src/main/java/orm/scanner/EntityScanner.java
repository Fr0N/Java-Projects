package orm.scanner;

import annotations.Entity;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class EntityScanner {
    public static List<Class> getAllEntities(String startPath) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        List<Class> result = new ArrayList<>();
        File dir = new File(startPath);
        File[] files = dir.listFiles();

        for (File file : files) {
            if (file.isFile() && file.getAbsolutePath().endsWith(".class")) {
                String currPath = file.getAbsolutePath().replace('\\', '.').replace(".class", "");
                Class currentClass = null;


                do {
                    try {
                        currentClass = Class.forName(currPath);
                        Constructor<?> constructor = currentClass.getDeclaredConstructor();
                        Object instance = constructor.newInstance();
                        if (instance.getClass().isAnnotationPresent(Entity.class)) {
                            result.add(instance.getClass());
                        }
                    } catch (Exception e) {
                        currPath = cutPath(currPath);
                    }
                } while (currentClass == null && currPath != null);
            } else if (file.isDirectory()) {
                List<Class> allEntities = getAllEntities(file.getAbsolutePath());
                result.addAll(allEntities);
            }
        }

        return result;
    }

    private static String cutPath(String path) {
        int substringLength = path.indexOf('.');
        if (substringLength < 0) {
            return null;
        }
        path = path.substring(substringLength + 1, path.length());
        return path;
    }
}
