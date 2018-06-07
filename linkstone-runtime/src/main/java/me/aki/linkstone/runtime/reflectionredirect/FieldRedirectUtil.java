package me.aki.linkstone.runtime.reflectionredirect;

import java.lang.reflect.Field;

/**
 * Utility class that reflects on a {@link Field} to
 * redirect its get and set methods to a getter and setter.
 */
public class FieldRedirectUtil {
    private final DynamicClassLoader classloader = new DynamicClassLoader();
    private final Field[] accessorFields;

    public FieldRedirectUtil() throws NoSuchFieldException {
        this.accessorFields = new Field[] {
                Field.class.getDeclaredField("fieldAccessor"),
                Field.class.getDeclaredField("overrideFieldAccessor"),
        };

        for(Field f : this.accessorFields) {
            f.setAccessible(true);
        }
    }

    /**
     * Modify a {@link Field} to invoke its getters.
     *
     * @param field that will be modified
     */
    public void redirectField(Field field) throws InstantiationException, IllegalAccessException {
        Object accessor = newAccessor(field);

        for(Field accessorField : accessorFields) {
            accessorField.set(field, accessor);
        }
    }

    private Object newAccessor(Field field) throws IllegalAccessException, InstantiationException {
        FieldAccessorGenerator generator = new FieldAccessorGenerator(field);
        String className = generator.getClassName();
        Class<?> accessorClass;

        try {
            accessorClass = classloader.loadClass(className);
        } catch(ClassNotFoundException e) {
            byte[] bytecode = generator.generateAccessor();
            accessorClass = classloader.loadBytecode(className.replace('/', '.'), bytecode);
        }

        return accessorClass.newInstance();
    }
}
