package me.aki.linkstone.runtime.reflectionredirect;

/**
 * Classloader that loads byte arrays of bytecode
 */
public class DynamicClassLoader extends ClassLoader {
    public Class<?> loadBytecode(String classname, byte[] bytecode) {
        return defineClass(classname, bytecode, 0, bytecode.length);
    }
}
