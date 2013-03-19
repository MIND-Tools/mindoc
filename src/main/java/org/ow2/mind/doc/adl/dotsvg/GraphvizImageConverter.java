package org.ow2.mind.doc.adl.dotsvg;

import java.io.File;
import java.io.IOException;

public class GraphvizImageConverter {


	private final String imageFormat;

	public GraphvizImageConverter(final String imageFormat) {
		this.imageFormat = imageFormat;
	}

	/**
	 * This method does nothing in the default configuration.
	 * If the user specified "@DumpDot(generateImage=<format>)" where format is svg or png,
	 * create a picture from the previously generated dot file.
	 */
	public void convertDotToImage(final String dir, final String name) {

		if (imageFormat.equals("none"))
			return;

		final String graphVizCommand[] = {"dot", "-T" + imageFormat, dir + name + ".dot"};

		// Better than Runtime getRuntime exec !
		final ProcessBuilder builder = new ProcessBuilder(graphVizCommand);
		builder.redirectOutput(new File(dir + name + "." + imageFormat));
		try {
			//Use the following to track the process: Process graphVizProcess = builder.start();
			builder.start();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * This method does nothing in the default configuration.
	 * If the user specified "@DumpDot(generateImage=<format>, useMindocFolderConvention=true)" where format is svg or png,
	 * create a picture from the previously generated dot file, and output to a specific directory
	 */
	public void convertDotToImage(final String dir, final String name, final String outputDir, final String shortDefName) {

		if (imageFormat.equals("none"))
			return;

		final String graphVizCommand[] = {"dot", "-T" + imageFormat, dir + name + ".dot"};

		// Better than Runtime getRuntime exec !
		final ProcessBuilder builder = new ProcessBuilder(graphVizCommand);
		builder.redirectOutput(new File(outputDir + shortDefName + "." + imageFormat));
		try {
			//Use the following to track the process: Process graphVizProcess = builder.start();
			builder.start();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
