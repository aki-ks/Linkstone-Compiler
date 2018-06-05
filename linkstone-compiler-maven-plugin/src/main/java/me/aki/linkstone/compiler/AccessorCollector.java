package me.aki.linkstone.compiler;

import me.aki.linkstone.annotations.Field;
import me.aki.linkstone.annotations.Version;
import me.aki.linkstone.compiler.meta.FieldMeta;
import me.aki.linkstone.compiler.meta.GetterMeta;
import me.aki.linkstone.compiler.meta.SetterMeta;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.*;

/**
 * Find all fields and their getters and setters in a template.
 */
public class AccessorCollector {
    private Set<FieldNode> fields = new HashSet<>();
    private Map<FieldNode, Set<MethodNode>> getters = new HashMap<>();
    private Map<FieldNode, Set<MethodNode>> setters = new HashMap<>();

    public AccessorCollector(ClassNode cn) {
        Iterator<FieldNode> fieldIter = cn.fields.iterator();
        Iterator<MethodNode> methodIter = cn.methods.iterator();
        while (fieldIter.hasNext()) {
            FieldNode fn = fieldIter.next();
            FieldMeta fieldMeta = FieldMeta.from(fn);
            if(!fieldMeta.hasAnnotation())continue;

            fields.add(fn);

            boolean isFinal = (fn.access & Opcodes.ACC_FINAL) != 0;
            Set<Version> notYetSeenGetterVersions = new HashSet<>(fieldMeta.getVersions());
            Set<Version> notYetSeenSetterVersions = new HashSet<>(fieldMeta.getVersions());

            while (methodIter.hasNext()) {
                MethodNode mn = methodIter.next();
                GetterMeta getterMeta = GetterMeta.from(mn);

                if(getterMeta.hasAnnotation()) {
                    getters.computeIfAbsent(fn, x -> new HashSet<>()).add(mn);
                    notYetSeenGetterVersions.removeAll(getterMeta.getVersions());
                } else if(!isFinal) {
                    SetterMeta setterMeta = SetterMeta.from(mn);
                    if(setterMeta.hasAnnotation()) {
                        setters.computeIfAbsent(fn, x -> new HashSet<>()).add(mn);
                        notYetSeenSetterVersions.removeAll(setterMeta.getVersions());
                    }
                }

                if(notYetSeenGetterVersions.isEmpty() && (isFinal || notYetSeenSetterVersions.isEmpty()))break;
            }
        }
    }


    /**
     * @return all fields with a {@link Field} annotation
     */
    public Set<FieldNode> getFields() {
        return fields;
    }

    public Set<MethodNode> getGetters(FieldNode fn) {
        return getters.computeIfAbsent(fn, x -> new HashSet<>());
    }

    public Set<MethodNode> getSetters(FieldNode fn) {
        return setters.computeIfAbsent(fn, x -> new HashSet<>());
    }

}
