package com.barrouh.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.zip.ZipArchiver;
import com.google.inject.internal.util.Lists;

/**
 * 
 * @author Mohamed Barrouh
 *
 */
@Mojo(name = "jds-package")
public class PackageGenerator extends AbstractMojo {

	private static final String EXTENSION = ".zip";

	private static final String OUT_DIR = "target/";

	private static final String TMP_DIR = "target/tmp/";

	private static final String READ_DIR = "src/main/resources";

	private ZipArchiver zipArchiver = new ZipArchiver();

	@Parameter(defaultValue = "${project}", required = true, readonly = true)
	private MavenProject project;

	@SuppressWarnings("unchecked")
	public void execute() throws MojoExecutionException {
		try {
            String finalName = project.getArtifactId().concat("-").concat(project.getVersion()).concat(EXTENSION);
            File zipFile = getZipFile(new File(OUT_DIR), finalName);

			zipArchiver.setDestFile(zipFile);
			zipArchiver.addDirectory(new File(READ_DIR));

			ArrayList<Artifact> dependencies = Lists.newArrayList((Set<Artifact>)project.getDependencyArtifacts());
			File file = dependencies.get(0).getFile();
			FileUtils.copyFile(file, new File(TMP_DIR.concat("app/").concat(file.getName())));

			zipArchiver.addDirectory(new File(TMP_DIR));
			zipArchiver.createArchive();
			project.getArtifact().setFile(zipFile);
		} catch (IOException e) {
			getLog().error(e);
		}
	}

	protected File getZipFile(File basedir, String finalName) {
		return new File(basedir, finalName);
	}

}