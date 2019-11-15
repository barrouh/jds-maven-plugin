package com.barrouh.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.jar.ManifestException;

import com.google.inject.internal.util.Lists;

/**
 * 
 * @author mbarrouh
 *
 */
@Mojo(name = "generate-jds-package")
public class PackageGenerator extends AbstractMojo {

	private static final String EXTENSION = ".zip";

	private static final String OUT_DIR = "target/";

	private static final String TMP_DIR = "target/tmp/";

	private static final String READ_DIR = "src/main/resources";

	private MavenArchiver archiver = new MavenArchiver();

	private MavenArchiveConfiguration archive = new MavenArchiveConfiguration();

	private JarArchiver jarArchiver = new JarArchiver();

	@Parameter(defaultValue = "${project}", required = true, readonly = true)
	MavenProject project;

	@Parameter(defaultValue = "${session}")
	private MavenSession session;

	@SuppressWarnings("unchecked")
	public void execute() throws MojoExecutionException {
		try {
			File jarFile = this.getJarFile(new File(OUT_DIR), project.getArtifactId() + "-" + project.getVersion());

			archiver.setArchiver(this.jarArchiver);
			archiver.setOutputFile(jarFile);
			archiver.setArchiver(jarArchiver);
			archiver.getArchiver().addDirectory(new File(READ_DIR));
			ArrayList<Artifact> dependencies = Lists
					.newArrayList((Set<Artifact>) this.project.getDependencyArtifacts());
			File file = dependencies.get(0).getFile();
			FileUtils.copyFile(file, new File(TMP_DIR + "app/" + file.getName()));
			getLog().info("" + file.getAbsolutePath());
			archiver.getArchiver().addDirectory(new File(TMP_DIR));
			archiver.createArchive(session, project, archive);
			getLog().info("size: " + project.getDependencyArtifacts().size());
		} catch (IOException | DependencyResolutionRequiredException | ManifestException e) {
			getLog().error(e);
		}
	}

	protected File getJarFile(final File basedir, final String finalName) {
		return new File(basedir, finalName + EXTENSION);
	}

}
