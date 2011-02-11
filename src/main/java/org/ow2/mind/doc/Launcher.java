/**
 * Copyright (C) 2009 STMicroelectronics
 *
 * This file is part of "Mind Compiler" is free software: you can redistribute
 * it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact: mind@ow2.org
 *
 * Authors: michel.metzger@st.com
 * Contributors:
 */

package org.ow2.mind.doc;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.ow2.mind.inject.GuiceModuleExtensionHelper;
import org.ow2.mind.plugin.PluginLoaderModule;
import org.ow2.mind.plugin.PluginManager;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Launcher {
  private static final String  MINDOC_HOME             = "MINDOC_HOME";
  private static final String  RESOURCE_DIR_NAME       = "resources";
  public static final String   DOC_FILES_DIRECTORY     = "doc-files";

  public static final Logger   logger                  = Logger
                                                           .getAnonymousLogger();

  private static final String  COMMAND_NAME            = "mindoc";

  private static final String  DEFAULT_DESTINATION     = "./target/doc";
  static final String          HTML_RESOURCES_DIR      = "resources/html";

  private final static Options options                 = new Options();
  private static final String  DESTINATION_PATH_OPTION = "d";
  private static final String  VERBOSE_OPTION          = "v";
  private static final String  HELP_OPTION             = "help";
  private static final String  HELP_OPTION_SHORT       = "h";
  private static final String  OVERVIEW_OPTION         = "overview";
  private static final String  DOCTITLE_OPTION         = "doctitle";

  public static void main(final String[] args) {
    initLogger();

    if (System.getenv(MINDOC_HOME) == null) {
      logger
          .severe("MINDOC_HOME variable is not defined. MINDOC_HOME must point to the location where mindoc is installed.");
      System.exit(1);
    }

    final Option destinationPathOption = new Option(DESTINATION_PATH_OPTION,
        true, "The path where the documentation is generated.");
    // destinationPathOption.setRequired(true);

    final Option verboseOption = new Option(VERBOSE_OPTION, false,
        "Verbose output.");

    final Option helpDirectoryOption = new Option(HELP_OPTION_SHORT,
        HELP_OPTION, false, "Print this message and exit.");

    final Option overviewOption = new Option(OVERVIEW_OPTION, true,
        "Specifies the file that contains the overview documentation.");

    final Option docTitleOption = new Option(DOCTITLE_OPTION, true,
        "Specifies the title that will be used in the the overview page.");

    options.addOption(destinationPathOption);
    options.addOption(verboseOption);
    options.addOption(helpDirectoryOption);
    options.addOption(overviewOption);
    options.addOption(docTitleOption);

    File sourceDirectories[] = null;
    File targetDirectory = new File(DEFAULT_DESTINATION);
    File overviewFile = null;
    String docTitle = null;

    final CommandLineParser parser = new PosixParser();

    try {
      final CommandLine cmd = parser.parse(options, args);

      if (cmd.hasOption(HELP_OPTION)) {
        printHelp();
        System.exit(0);
      }

      if (cmd.hasOption(VERBOSE_OPTION)) logger.setLevel(Level.ALL);

      if (cmd.getArgs().length >= 1) {
        final String sourceList[] = cmd.getArgs();
        sourceDirectories = new File[sourceList.length];
        for (int i = 0; i < sourceList.length; i++) {
          final File sourceDirectory = new File(sourceList[i]);
          if (!sourceDirectory.isDirectory() || !sourceDirectory.canRead()) {
            logger.severe(String.format("Cannot read source path '%s'.",
                sourceDirectory.getPath()));
            System.exit(2);
          }
          sourceDirectories[i] = sourceDirectory;
        }
      } else {
        logger.severe("You must specify a source path.");
        printHelp();
        System.exit(1);
      }

      if (cmd.hasOption(DESTINATION_PATH_OPTION)) {
        targetDirectory = new File(cmd.getOptionValue(DESTINATION_PATH_OPTION));
      } else {
        logger
            .info("Destination directory not specified. Documentation will be generated in default location ("
                + DEFAULT_DESTINATION + ").");
      }

      if (cmd.hasOption(OVERVIEW_OPTION)) {
        overviewFile = new File(cmd.getOptionValue(OVERVIEW_OPTION));
      }

      if (cmd.hasOption(DOCTITLE_OPTION)) {
        docTitle = cmd.getOptionValue(DOCTITLE_OPTION);
      }
    } catch (final ParseException e) {
      logger.severe("Command line parse error. Reason: " + e.getMessage());
      System.exit(1);
    }

    // check destination directory
    if (targetDirectory.exists()) {
      if (!targetDirectory.isDirectory()) {
        logger.severe("Destination path must be a directory.");
        System.exit(1);
      }
      if (!targetDirectory.canWrite()) {
        logger.severe("Cannot write in destination directory.");
        System.exit(1);
      }
    } else {
      if (!targetDirectory.mkdirs()) {
        logger.severe("Cannot create destination directory.");
        System.exit(1);
      }
    }

    final Injector pluginManagerInjector = Guice
        .createInjector(new PluginLoaderModule());
    final PluginManager pluginManager = pluginManagerInjector
        .getInstance(PluginManager.class);
    runGenarators(pluginManager, sourceDirectories, targetDirectory, new File(
        getMindocHome(), RESOURCE_DIR_NAME), docTitle, overviewFile);
    ResourceCopier.copyResources(sourceDirectories, targetDirectory);
    logger.info("Documentation generated in " + targetDirectory.getPath());
  }

  private static void runGenarators(final PluginManager pluginManager,
      final File sourceDirectories[], final File targetDirectory,
      final File resourceDirectory, final String docTitle,
      final File overviewFile) {
    try {
      // init context
      final Map<Object, Object> context = new HashMap<Object, Object>();

      // Put this in context to enable mindoc Guice modules.
      context.put("org.ow2.mind.doc.GenrateDoc", Boolean.TRUE);

      // create injector from guice-module extensions
      final Injector injector = Guice.createInjector(GuiceModuleExtensionHelper
          .getModules(pluginManager, context));

      logger.fine("Generating indexes...");
      final DocumentationIndexGenerator indexGenerator = new DocumentationIndexGenerator(
          sourceDirectories, resourceDirectory, docTitle, overviewFile);
      indexGenerator.generateIndexPages(targetDirectory);

      logger.fine("Generating documentation...");
      final DefinitionTreeDocumentationGenerator definitionGenerator = injector
          .getInstance(DefinitionTreeDocumentationGenerator.class);
      definitionGenerator.generateDocumentation(sourceDirectories,targetDirectory,
          context);
    } catch (final Exception e) {
      logger.severe("Error while generating documentation: "
          + e.getLocalizedMessage());
      System.exit(1);
    }

  }

  private static void printHelp() {
    final HelpFormatter formatter = new HelpFormatter();
    final String header = " generates documentation for ADL, IDL and implementation files located in <sourcepath>.";
    formatter.printHelp(COMMAND_NAME + " [OPTION] (<sourcepath>)+", header,
        options, null);
  }

  static String getMindocHome() {
    return System.getenv(MINDOC_HOME);
  }

  private static void initLogger() {
    final Handler consoleHandler = new ConsoleHandler();
    consoleHandler.setLevel(Level.ALL);
    logger.setLevel(Level.INFO);
    logger.setUseParentHandlers(false);
    logger.addHandler(consoleHandler);
  }

  private static class ConsoleHandler extends StreamHandler {
    public ConsoleHandler() {
      super(System.out, new Formatter() {
        @Override
        public String format(final LogRecord record) {
          final StringBuilder sb = new StringBuilder();
          if (record.getLevel() == Level.SEVERE) {
            sb.append("Error: ");
          } else if (record.getLevel() == Level.WARNING) {
            sb.append("Warning: ");
          }
          sb.append(record.getMessage());
          sb.append('\n');
          return sb.toString();
        }
      });
    }

    @Override
    public synchronized void publish(final LogRecord arg0) {
      super.publish(arg0);
      flush();
    }
  }
}
