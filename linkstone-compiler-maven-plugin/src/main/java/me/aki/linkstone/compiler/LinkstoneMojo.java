package me.aki.linkstone.compiler;

import me.aki.linkstone.annotations.Version;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.util.List;


@Mojo(
        name = "compile",
        defaultPhase = LifecyclePhase.PROCESS_CLASSES
)
public class LinkstoneMojo extends AbstractMojo {
    @Parameter( readonly = true, defaultValue = "${project}" )
    private MavenProject project;

    @Parameter()
    private String version;

    public void execute() throws MojoExecutionException {
        getLog().info("-----------------------------------------------------");
        getLog().info("         L I N K S T O N E   C O M P I L E R         ");
        getLog().info("-----------------------------------------------------");

        Version version = Version.forName(this.version);
        getLog().info("Generating classes for version " + version.getName());

        File outputDir = new File(project.getBuild().getOutputDirectory());

        LinkstoneCompiler compiler = new LinkstoneCompiler();
        List<ClassNode> templates = compiler.loadClasses(outputDir);

        FileUtils.delete(outputDir);

        List<ClassNode> generatedClasses = compiler.generateClasses(templates, version);
        compiler.writeClasses(outputDir, generatedClasses);
    }
}
