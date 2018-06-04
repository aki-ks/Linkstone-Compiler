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
        Iterator<FieldNode> fieldIter = cn.fields.iterator();
        Iterator<MethodNode> methodIter = cn.methods.iterator();
        while (fieldIter.hasNext()) {
            FieldNode fn = fieldIter.next();
            FieldMeta fieldMeta = FieldMeta.from(fn);
            if(!fieldMeta.hasAnnotation())continue;

            boolean isFinal = (fn.access & Opcodes.ACC_FINAL) != 0;
            Set<Version> notYetSeenGetterVersions = new HashSet<>(fieldMeta.getVersions());
            Set<Version> notYetSeenSetterVersions = new HashSet<>(fieldMeta.getVersions());

            while (methodIter.hasNext()) {
                MethodNode mn = methodIter.next();
                GetterMeta getterMeta = GetterMeta.from(mn);

                if(getterMeta.hasAnnotation()) {
                    if(!getterMeta.getVersions().contains(this.version)) {
                        methodIter.remove();
                    }

                    mn.name = GETTER_PREFIX + fn.name;

                    notYetSeenGetterVersions.removeAll(getterMeta.getVersions());
                } else if(!isFinal) {
                    SetterMeta setterMeta = SetterMeta.from(mn);
                    if(setterMeta.hasAnnotation()) {
                        if(!setterMeta.getVersions().contains(this.version)) {
                            methodIter.remove();
                        }

                        mn.name = SETTER_PREFIX + fn.name;

                        notYetSeenSetterVersions.removeAll(setterMeta.getVersions());
                    }
                }

                if(notYetSeenGetterVersions.isEmpty() && (isFinal || notYetSeenSetterVersions.isEmpty()))break;
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
