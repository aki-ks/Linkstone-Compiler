package me.aki.linkstone.compiler;

import me.aki.linkstone.compiler.meta.*;
import me.aki.linkstone.annotations.Version;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.*;

/**
 * Removes all classes, methods, fields, getters and setters that do not exist for a certain version.
 * It does also rename getters and setters.
 */
public class TemplateTransformer {
    private final static String GETTER_PREFIX = "$linkstone$getter$";
    private final static String SETTER_PREFIX = "$linkstone$setter$";

    private final Version version;

    public TemplateTransformer(Version version) {
        this.version = version;
    }

    public void processClasses(List<ClassNode> cns) {
        Iterator<ClassNode> iter = cns.iterator();
        while (iter.hasNext()) {
            ClassNode cn = iter.next();
            ClassfileMeta meta = ClassfileMeta.from(cn);

            if(meta.hasAnnotation() && !meta.getVersions().contains(version)) {
                iter.remove();
                continue;
            }

            processFieldAccessors(cn);
            processFields(cn.fields);
            processMethods(cn.methods);
        }
    }

    private void processFieldAccessors(ClassNode cn) {
        AccessorCollector accessors = new AccessorCollector(cn);
        for(FieldNode fn : accessors.getFields()) {
            FieldMeta fieldMeta = FieldMeta.from(fn);
            if(!fieldMeta.hasAnnotation())continue;

            for(MethodNode getter : accessors.getGetters(fn)) {
                MethodMeta meta = MethodMeta.from(getter);
                if(!meta.getVersions().contains(this.version)) {
                    cn.methods.remove(getter);
                }
                getter.name = GETTER_PREFIX + fn.name;
            }

            for(MethodNode setter : accessors.getSetters(fn)) {
                MethodMeta meta = MethodMeta.from(setter);
                if(!meta.getVersions().contains(this.version)) {
                    cn.methods.remove(meta);
                }
                setter.name = SETTER_PREFIX + fn.name;
            }
        }
    }

    private void processFields(List<FieldNode> fields) {
        Iterator<FieldNode> iter = fields.iterator();
        while (iter.hasNext()) {
            FieldNode fn = iter.next();
            FieldMeta meta = FieldMeta.from(fn);

            if(meta.hasAnnotation() && !meta.getVersions().contains(version)) {
                iter.remove();
            }
        }
    }

    private void processMethods(List<MethodNode> methods) {
        Iterator<MethodNode> iter = methods.iterator();
        while (iter.hasNext()) {
            MethodNode mn = iter.next();
            MethodMeta meta = MethodMeta.from(mn);

            if(meta.hasAnnotation() && !meta.getVersions().contains(version)) {
                iter.remove();
            }
        }
    }

}
