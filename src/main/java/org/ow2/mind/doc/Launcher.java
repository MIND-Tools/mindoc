/**
 * Copyright (C) 2009 STMicroelectronics
 * Copyright (C) 2014 Schneider-Electric
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
 * Contributors: yteissier@assystem.com
 */

package org.ow2.mind.doc;

import java.io.File;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import org.antlr.stringtemplate.StringTemplateGroupLoader;
import org.objectweb.fractal.adl.CompilerError;
import org.objectweb.fractal.adl.error.GenericErrors;
import org.ow2.mind.inject.GuiceModuleExtensionHelper;
import org.ow2.mind.plugin.PluginLoaderModule;
import org.ow2.mind.plugin.PluginManager;
import org.ow2.mind.st.StringTemplateComponentLoader;
import org.ow2.mind.cli.CmdArgument;
import org.ow2.mind.cli.CmdFlag;
import org.ow2.mind.cli.CmdOption;
import org.ow2.mind.cli.CmdOptionBooleanEvaluator;
import org.ow2.mind.cli.CmdPathOption;
import org.ow2.mind.cli.CommandLine;
import org.ow2.mind.cli.CommandLineOptionExtensionHelper;
import org.ow2.mind.cli.CommandOptionHandler;
import org.ow2.mind.cli.InvalidCommandLineException;
import org.ow2.mind.cli.Options;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class Launcher {
  private static final String  MIND_ROOT             = "MIND_ROOT";
  private static final String  RESOURCE_DIR_NAME       = "resources";
  public static final String   DOC_FILES_DIRECTORY     = "doc-files";

  public static final Logger   logger                  = Logger
      .getAnonymousLogger();

  private static final String  COMMAND_NAME            = "mindoc";

  private static final String  DEFAULT_DESTINATION     = "./target/doc";
  static final String          HTML_RESOURCES_DIR      = "resources/html";

  protected static final String ID_PREFIX              = "org.ow2.mind.doc.";


  private static final CmdFlag HELP_OPTION = new CmdFlag(
      ID_PREFIX + "Help",
      "h", "help",
      "Print the help and exit");

  // Disable this if you wish to use other module-contributed command-line options
  private static final CmdArgument DESTINATION_PATH_OPTION = new CmdArgument(
      ID_PREFIX + "Output",
      "o", "output",
      "The path where the documentation is generated",
      "<The output path>");

  private static final CmdFlag KEEPDOT_OPTION = new CmdFlag(
      ID_PREFIX + "Keepdot",
      null, "keepdot",
      "Specifies to keep the intermediary GraphViz Dot files used for SVG generation.");

  private static final CmdArgument OVERVIEW_OPTION = new CmdArgument(
      ID_PREFIX + "overview",
      null, "overview",
      "Specifies the file that contains the overview documentation.",
      "<The overview file>");

  private static final CmdArgument DOCTITLE_OPTION = new CmdArgument(
      ID_PREFIX + "doctitle",
      null, "doctitle",
      "Specifies the title that will be used in the overview page.",
      "<The title file>");


  private static final CmdFlag VERBOSE_OPTION = new CmdFlag(
      ID_PREFIX + "Verbose",
      null, "verbose",
      "Verbose output.");

  private static final CmdPathOption SRC_PATH_HANDLER = new CmdPathOption(
      ID_PREFIX + "src parh",
      "S",
      "src-path",
      "the search path of ADL,IDL and implementation files (list of path separated by ':' on Linux or ';' on Windows)",
      "<path list>"
      );

  private final static Options options                 = new Options();

  final static Map<Object, Object> context = new HashMap<Object, Object>();
  public static Injector injector = null;

  public static void main(final String[] args) {
    initLogger();

    if (System.getenv(MIND_ROOT) == null) {
      logger
      .severe("MIND_ROOT variable is not defined. MIND_ROOT must point to the location where mindoc is installed.");
      System.exit(1);
    }


    File sourceDirectories[] = null;
    File targetDirectory = new File(DEFAULT_DESTINATION);
    File overviewFile = null;
    String docTitle = null;
    boolean keepDot = false;

    /****** Initialization of the PluginManager Component *******/

    final Injector bootStrapPluginManagerInjector = getBootstrapInjector();
    final PluginManager pluginManager = bootStrapPluginManagerInjector
        .getInstance(PluginManager.class);

    /** **/

    options.addOptions(HELP_OPTION,
        DESTINATION_PATH_OPTION,
        KEEPDOT_OPTION,
        OVERVIEW_OPTION,
        DOCTITLE_OPTION,
        VERBOSE_OPTION,
        SRC_PATH_HANDLER);

    // Enable to allow using other modules command-line options
    //options.addOptions(CommandLineOptionExtensionHelper
    //    .getCommandOptions(pluginManager));

    try {
      // parse arguments to a CommandLine.
      final CommandLine cmdLine = CommandLine.parseArgs(options, false, args);

      // very important when using mind plugins with "enableWhen" in their xml descriptor
      context.put(CmdOptionBooleanEvaluator.CMD_LINE_CONTEXT_KEY, cmdLine);

      // Enable to allow using other modules command-line options
      //invokeOptionHandlers(pluginManager, cmdLine, context);
      //checkExclusiveGroups(pluginManager, cmdLine);


      // If help is asked, print it and exit.
      if (HELP_OPTION.isPresent(cmdLine)) {
        printHelp(System.err);
        System.exit(0);
      }

      // @TODO Use environment variable
      if (VERBOSE_OPTION.isPresent(cmdLine)) logger.setLevel(Level.ALL);

      List<String> sourceListExtra = null;
      List<String> sourceList = null;

      if (cmdLine.getArguments().size() >= 1) {
        sourceListExtra = cmdLine.getArguments();
      }
      if (SRC_PATH_HANDLER.isPresent(cmdLine)) {
        sourceList = SRC_PATH_HANDLER.getPathValue(cmdLine);
      }
      sourceDirectories = new File[((sourceList != null)?sourceList.size():0) + ((sourceListExtra != null)?sourceListExtra.size():0)];

      if (sourceList != null) {

        for (int i = 0; i < sourceList.size(); i++) {
          final File sourceDirectory = new File(sourceList.get(i));
          if (!sourceDirectory.isDirectory() || !sourceDirectory.canRead()) {
            logger.severe(String.format("Cannot read source path '%s'.", sourceDirectory.getPath()));
            System.exit(2);
          }
          sourceDirectories[i] = sourceDirectory;
        }
      }
      if (sourceListExtra != null) {
        for (int i = 0; i < sourceListExtra.size(); i++) {
            final File sourceDirectory = new File(sourceListExtra.get(i));
            if (!sourceDirectory.isDirectory() || !sourceDirectory.canRead()) {
              logger.severe(String.format("Cannot read source path '%s'.", sourceDirectory.getPath()));
              System.exit(2);
            }
            sourceDirectories[((sourceList != null)?sourceList.size():0) + i] = sourceDirectory;
          }
      }
      if (sourceListExtra == null && sourceList == null) {
        logger.severe("You must specify a source path.");
        printHelp(System.err);
        System.exit(1);
      }

      /**
       * Comment the block and use the // commented line if you enabled
       * other modules command-line options, to get pre-handled value:
       */
      // targetDirectory = OutPathOptionHandler.getOutPath(context);
      if (DESTINATION_PATH_OPTION.isPresent(cmdLine)) {
        targetDirectory = new File(DESTINATION_PATH_OPTION.getValue(cmdLine));
      } else {
        logger
        .info("Destination directory not specified. Documentation will be generated in default location ("
            + DEFAULT_DESTINATION + ").");
      }

      if (OVERVIEW_OPTION.isPresent(cmdLine)) {
        overviewFile = new File(OVERVIEW_OPTION.getValue(cmdLine));
      }

      if (DOCTITLE_OPTION.isPresent(cmdLine)) {
        docTitle = DOCTITLE_OPTION.getValue(cmdLine);
      }

      if (KEEPDOT_OPTION.isPresent(cmdLine))
        keepDot = true;

    } catch (final InvalidCommandLineException e) {
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

    runGenerators(pluginManager, sourceDirectories, targetDirectory, new File(
        getMindocHome(), RESOURCE_DIR_NAME), docTitle, overviewFile, keepDot);
    ResourceCopier.copyResources(sourceDirectories, targetDirectory);
    logger.info("Documentation generated in " + targetDirectory.getPath());
  }

  private static void runGenerators(final PluginManager pluginManager,
      final File sourceDirectories[], final File targetDirectory,
      final File resourceDirectory, final String docTitle,
      final File overviewFile, final boolean keepDot) {
    try {
      // Put this in context to enable mindoc Guice modules.
      context.put("org.ow2.mind.doc.GenrateDoc", Boolean.TRUE);
      // also put info about keeping Dot files or not
      context.put("org.ow2.mind.doc.KeepDot", new Boolean(keepDot));

      // create injector from guice-module extensions
      injector = Guice.createInjector(GuiceModuleExtensionHelper
          .getModules(pluginManager, context));

      logger.fine("Generating indexes...");

      // FIXME YTE Create instance of StringTemplaceGroupLoader here because the injection of this class in DocumentationIndexGenerator
      // do not work.
      final StringTemplateGroupLoader stComponentLoaderItf = injector.getInstance(StringTemplateComponentLoader.class);

      final DocumentationIndexGenerator indexGenerator = new DocumentationIndexGenerator(
          sourceDirectories, resourceDirectory, docTitle, overviewFile, stComponentLoaderItf);
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

  private static void printHelp(final PrintStream ps) {
    printUsage(ps);
    ps.println();
    ps.println("Available options are :");
    int maxCol = 0;

    for (final CmdOption opt : options.getOptions()) {
      final int col = 2 + opt.getPrototype().length();
      if (col > maxCol) maxCol = col;
    }
    for (final CmdOption opt : options.getOptions()) {
      final StringBuffer sb = new StringBuffer("  ");
      sb.append(opt.getPrototype());
      while (sb.length() < maxCol)
        sb.append(' ');
      sb.append("  ").append(opt.getDescription());
      ps.println(sb);
    }
  }

  private static void printUsage(final PrintStream ps) {
    ps.println("Usage: " + COMMAND_NAME
        + " generates documentation for ADL, IDL and implementation files located in <sourcepath>.");
    ps.println(" [OPTION] (<sourcepath>)+");
  }

  static String getMindocHome() {
    return System.getenv(MIND_ROOT);
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

  //----------------------------------------------------------------------------
  // Guice & command-line interface (CLI) methods - (copied from MindC Launcher)
  // ---------------------------------------------------------------------------

  protected static Injector getBootstrapInjector() {
    return Guice.createInjector(new PluginLoaderModule());
  }

  protected static void checkExclusiveGroups(final PluginManager pluginManagerItf,
      final CommandLine cmdLine) throws InvalidCommandLineException {
    final Collection<Set<String>> exclusiveGroups = CommandLineOptionExtensionHelper
        .getExclusiveGroups(pluginManagerItf);
    for (final Set<String> exclusiveGroup : exclusiveGroups) {
      CmdOption opt = null;
      for (final String id : exclusiveGroup) {
        final CmdOption opt1 = cmdLine.getOptions().getById(id);
        if (opt1.isPresent(cmdLine)) {
          if (opt != null) {
            throw new InvalidCommandLineException("Options '"
                + opt.getPrototype() + "' and '" + opt1.getPrototype()
                + "' cannot be specified simultaneously on the command line.",
                1);
          }
          opt = opt1;
        }
      }
    }
  }

  protected static void invokeOptionHandlers(final PluginManager pluginManagerItf,
      final CommandLine cmdLine, final Map<Object, Object> context)
          throws InvalidCommandLineException {
    final List<CmdOption> toBeExecuted = new LinkedList<CmdOption>(cmdLine
        .getOptions().getOptions());
    final Set<String> executedId = new HashSet<String>(toBeExecuted.size());
    while (!toBeExecuted.isEmpty()) {
      final int toBeExecutedSize = toBeExecuted.size();
      final Iterator<CmdOption> iter = toBeExecuted.iterator();
      while (iter.hasNext()) {
        final CmdOption option = iter.next();
        final List<String> precedenceIds = CommandLineOptionExtensionHelper
            .getPrecedenceIds(option, pluginManagerItf);
        if (executedId.containsAll(precedenceIds)) {
          // task ready to be executed
          for (final CommandOptionHandler handler : CommandLineOptionExtensionHelper
              .getHandler(option, pluginManagerItf)) {
            handler.processCommandOption(option, cmdLine, context);
          }
          executedId.add(option.getId());
          iter.remove();
        }
      }
      if (toBeExecutedSize == toBeExecuted.size()) {
        // nothing has been executed. there is a circular dependency
        throw new CompilerError(GenericErrors.GENERIC_ERROR,
            "Circular dependency in command line option handlers: "
                + toBeExecuted);
      }
    }
  }
}
