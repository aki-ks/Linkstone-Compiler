package me.aki.linkstone.compiler.meta;

import me.aki.linkstone.annotations.Package;
import me.aki.linkstone.annotations.PackageContainer;
import me.aki.linkstone.annotations.Version;
import me.aki.linkstone.annotations.Mode;
import me.aki.linkstone.compiler.collect.PackageMetaVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Represents the content of all {@link Package} annotations on a package-info template.
 */
public class PackageMeta extends NamedMeta {
    private final static String PACKAGE_ANNOTION_DESC = Type.getDescriptor(Package.class);
    private final static String PACKAGE_CONTAINER_ANNOTION_DESC = Type.getDescriptor(PackageContainer.class);

    public static PackageMeta from(ClassNode cn) {
        String pkg = cn.name.substring(0, cn.name.lastIndexOf('/'));
        PackageMeta meta = new PackageMeta(pkg, new HashMap<>());
        if(cn.invisibleAnnotations != null) {
            for (AnnotationNode an : cn.invisibleAnnotations) {
                if (an.desc.equals(PACKAGE_ANNOTION_DESC) ||
                        an.desc.equals(PACKAGE_CONTAINER_ANNOTION_DESC)) {
                    an.accept(new PackageMetaVisitor(meta));
                }
            }
        }
        return meta;
    }

    private final Map<Version, Mode> targetMap = new HashMap<>();

    private PackageMeta(String templateName, Map<Version, Optional<String>> mapping) {
        super(templateName, mapping);
    }

    /**
     * @param version where the mode is assigned
     * @return what part of the name should be replaced
     */
    public Optional<Mode> getModeOpt(Version version) {
        return Optional.ofNullable(targetMap.get(version));
    }

    /**
     * Get the assigned mode or else {@link Mode#SELF}
     *
     * @param version where the mode is assigned
     * @return what part of the name should be replaced
     */
    public Mode getModeOrDefault(Version version) {
        return targetMap.getOrDefault(version, Mode.SELF);
    }

    public void putTarget(Version version, Mode target) {
        this.targetMap.put(version, target);
    }
}
