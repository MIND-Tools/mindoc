/**
 * Copyright (C) 2014 Schneider Electric
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
 * Authors: Stephane Seyvoz
 * Contributors:
 */

package org.ow2.mind.doc.adl.graph;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

/**
 * This class is used to fix SVG figures that miss the "preserveAspectRatio" attribute
 * which is not written by GraphViz during the Dot to SVG conversion. This missing attribute
 * leads to a wrong display of the SVG element in Google Chrome (and Internet Explorer).
 *
 * We navigate in the XML document to find the svg nodes and add the good attribute to the SVG node:
 * preserveAspectRatio = "xMinYMin meet"
 *
 * We used to do this with javascript, but it didn't work in Google Chrome with local files,
 * the browser only allowing remote URLs for document loading and modification.
 *
 */
public class SVGFixer {

  /** The parser factory used to create new parsers. */
  DocumentBuilderFactory dbf;

  /**
   * Creates a new {@link SVGFixer}.
   * This class is used to fix GraphViz-generated SVG files (from Dot files).
   * GraphViz omits to write the "preserveAspectRatio" attribute in the svgFigure node, leading
   * to erroneous rendering in Google Chrome and Internet Explorer.
   *
   * @param validate
   */
  public SVGFixer() {
    dbf = DocumentBuilderFactory.newInstance();

    dbf.setValidating(false);
    try {
      // Do not try to using the document's http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd
      dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
    } catch (final ParserConfigurationException e) {
      e.printStackTrace();
    }
  }

  public void fix(final File svgFile) {

    try {
      final DocumentBuilder db = dbf.newDocumentBuilder();
      final Document doc = db.parse(svgFile);
      final NodeList svgNodesList =  doc.getElementsByTagName("svg");

      for (int i = 0 ; i < svgNodesList.getLength() ; i++) {
        final Element currentElement = (Element) svgNodesList.item(i);

        // force attribute value, even if it exists
        currentElement.setAttribute("preserveAspectRatio", "xMinYMin meet");
      }

      // We do not use a Transformer but an LSSerializer since the first discarded the DTD DocType header in the output.

      final DOMImplementationLS ls = (DOMImplementationLS)
          DOMImplementationRegistry.newInstance().getDOMImplementation("LS");
      final Writer writer = new OutputStreamWriter(new FileOutputStream(svgFile));
      final LSOutput lsout = ls.createLSOutput();
      lsout.setCharacterStream(writer);

      /*
       * If "doc" has been constructed by parsing an XML document, we
       * should keep its encoding when serializing it; if it has been
       * constructed in memory, its encoding has to be decided by the
       * client code.
       */
      lsout.setEncoding(doc.getXmlEncoding());
      final LSSerializer serializer = ls.createLSSerializer();
      serializer.write(doc, lsout);

    } catch (final IOException e) {
      e.printStackTrace();
    } catch (final ParserConfigurationException e) {
      e.printStackTrace();
    } catch (final SAXException e) {
      e.printStackTrace();
    } catch (final ClassNotFoundException e) {
      e.printStackTrace();
    } catch (final InstantiationException e) {
      e.printStackTrace();
    } catch (final IllegalAccessException e) {
      e.printStackTrace();
    } catch (final ClassCastException e) {
      e.printStackTrace();
    }
  }

}
