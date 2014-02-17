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
import java.util.HashMap;
import java.util.Map;

import org.ow2.mind.doc.DocumentationIndexGenerator;
import org.ow2.mind.inject.GuiceModuleExtensionHelper;
import org.ow2.mind.plugin.PluginLoaderModule;
import org.ow2.mind.plugin.PluginManager;
import org.ow2.mind.st.StringTemplateComponentLoader;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class DocumentationIndexGeneratorTest {

  private final File target = new File("target/doc");
  private final File source[] = new File []{new File ("src/test/resources")};
  private final File resource = new File ("src/main/resources");
  private final File overview = new File ("src/test/resources/overview.html");
  private final String title = "Test Documentation<br/><font size=\"-1\">small text</font>";

  final Injector bootStrapPluginManagerInjector = getBootstrapInjector();
  final PluginManager pluginManager = bootStrapPluginManagerInjector.getInstance(PluginManager.class);

  final private Map<Object, Object> context = new HashMap<Object, Object>();

  final private Injector injector = Guice.createInjector(GuiceModuleExtensionHelper
      .getModules(pluginManager, context));

  private Injector getBootstrapInjector() {
    return Guice.createInjector(new PluginLoaderModule());
  }

  @Test
  public void testAllDefinitionFrame() throws Exception {
    final DocumentationIndexGenerator generator =
      new DocumentationIndexGenerator(source, resource, title, overview, this.injector.getInstance(StringTemplateComponentLoader.class));
    generator.generateAllDefinitionFrame(target);
  }

  @Test
  public void testOverviewFrame() throws Exception {
    final DocumentationIndexGenerator generator =
      new DocumentationIndexGenerator(source, resource, title, overview, this.injector.getInstance(StringTemplateComponentLoader.class));
    generator.generateOverviewFrame(target);
  }

  @Test
  public void testPackageFrame() throws Exception {
    final DocumentationIndexGenerator generator =
      new DocumentationIndexGenerator(source, resource, title, overview, this.injector.getInstance(StringTemplateComponentLoader.class));
    generator.generatePackageFrame(target);
  }

  @Test
  public void testOverviewSummary() throws Exception {
    final DocumentationIndexGenerator generator =
      new DocumentationIndexGenerator(source, resource, title, overview, this.injector.getInstance(StringTemplateComponentLoader.class));
    generator.generateOverviewSummary(target);
  }

  @Test
  public void testPackageSummary() throws Exception {
    final DocumentationIndexGenerator generator =
      new DocumentationIndexGenerator(source, resource, title, overview, this.injector.getInstance(StringTemplateComponentLoader.class));
    generator.generatePackageSummary(target);
  }
}
