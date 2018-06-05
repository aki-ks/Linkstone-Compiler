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
    private Map<FieldNode, Set<MethodNode>> getterMap = new HashMap<>();
    private Map<FieldNode, Set<MethodNode>> setterMap = new HashMap<>();

    public AccessorCollector(ClassNode cn) {
        Iterator<FieldNode> fieldIter = cn.fields.iterator();
        Iterator<MethodNode> methodIter = cn.methods.iterator();
        fieldLoop: while (fieldIter.hasNext()) {
            FieldNode fn = fieldIter.next();
            FieldMeta fieldMeta = FieldMeta.from(fn);
            if(!fieldMeta.hasAnnotation())continue;

            fields.add(fn);

            boolean isFinal = (fn.access & Opcodes.ACC_FINAL) != 0;
            Set<Version> notYetSeenGetterVersions = new HashSet<>(fieldMeta.getVersions());
            Set<Version> notYetSeenSetterVersions = new HashSet<>(fieldMeta.getVersions());
            Set<MethodNode> getters = getterMap.computeIfAbsent(fn, x -> new HashSet<>());
            Set<MethodNode> setters = setterMap.computeIfAbsent(fn, x -> new HashSet<>());

            while (methodIter.hasNext()) {
                MethodNode mn = methodIter.next();
                GetterMeta getterMeta = GetterMeta.from(mn);

                if(getterMeta.hasAnnotation()) {
                    getters.add(mn);

                    for(Version version : getterMeta.getVersions()) {
                        if(!notYetSeenGetterVersions.remove(version)) {
                            onDuplicatedGetterError(fn, version);
                            break fieldLoop;
                        }
                    }
                } else if(!isFinal) {
                    SetterMeta setterMeta = SetterMeta.from(mn);
                    if(setterMeta.hasAnnotation()) {
                        setters.add(mn);

                        for(Version version : setterMeta.getVersions()) {
                            if(!notYetSeenSetterVersions.remove(version)) {
                                onDuplicatedSetterError(fn, version);
                                break fieldLoop;
                            }
                        }
                    }
                }

                if(notYetSeenGetterVersions.isEmpty() && (isFinal || notYetSeenSetterVersions.isEmpty()))break fieldLoop;
            }

            onMissingAccessorError(fn, notYetSeenGetterVersions, notYetSeenSetterVersions);
            break fieldLoop;
        }
    }


    protected void onMissingAccessorError(FieldNode fn, Set<Version> missingGetterVersions, Set<Version> missingSetterVersions) {
        throw new IllegalStateException("Missing accessor");
    }

    protected void onDuplicatedGetterError(FieldNode fn, Version version) {
        throw new IllegalStateException("Duplicated getter");
    }

    protected void onDuplicatedSetterError(FieldNode fn, Version version) {
        throw new IllegalStateException("Duplicated setter");
    }

    /**
     * @return all fields with a {@link Field} annotation
     */
    public Set<FieldNode> getFields() {
        return fields;
    }

    /**
     * Get all getters for a field or null
     * if it has no {@link Field} annotation.
     *
     * @param fn the field
     * @return getters for the field
     */
    public Set<MethodNode> getGetters(FieldNode fn) {
        return getterMap.get(fn);
    }

    /**
     * Get all setters for a field or null
     * if it has no {@link Field} annotation.
     *
     * @param fn the field
     * @return setters for the field
     */
    public Set<MethodNode> getSetters(FieldNode fn) {
        return setterMap.get(fn);
    }
}
