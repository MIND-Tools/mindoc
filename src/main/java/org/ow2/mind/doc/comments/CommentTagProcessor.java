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

package org.ow2.mind.doc.comments;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.objectweb.fractal.adl.Node;
import org.ow2.mind.doc.HTMLDocumentationHelper.SourceKind;
import org.ow2.mind.doc.comments.LinkTag.ComponentLinkElementType;
import org.ow2.mind.doc.comments.LinkTag.InterfaceLinkElementType;


public class CommentTagProcessor {
  public static final Logger  logger                  = Logger.getAnonymousLogger();

  private static final String COMPONENT_LINK = "@component";
  private static final String INTERFACE_LINK = "@interface";

  private static final String PARAM = "@param";
  private static final String RETURN = "@return";

  private static final String LINK_PATTERN = "\\s+(this|([a-zA-Z0-9]\\w*(\\.[a-zA-Z0-9]\\w*)*))(#(\\w+)(#([a-zA-Z0-9]\\w*))?)?";

  private static final Pattern componentLinkPattern = Pattern.compile(COMPONENT_LINK + LINK_PATTERN);

  private static final Pattern interfaceLinkPattern = Pattern.compile(INTERFACE_LINK + LINK_PATTERN);

  private static final Pattern figurePattern =
      Pattern.compile("@figure\\s+(((\\./)?(\\.\\./)*|/)[^\\s]+(/[^\\s]+)*(\\.[^\\s]+)?)(\\s(width|height)=(\\d+)px)?");

  // Note: default Pattern class behavior is single-line, not multiline, so we don't need $ at the end.
  private static final Pattern paramPattern = Pattern.compile(PARAM + "\\s+([a-zA-Z_][a-zA-Z_0-9]*)\\s+(.*)"); // @param + spaces + param_name + spaces + anything (description)
  private static final Pattern returnPattern = Pattern.compile(RETURN + "\\s+(.+)"); // @return + spaces + anything (description, at least one character)

  private static String definitionName;
  private int lastIndex = 0;
  private final String comment;
  private static Node node;

  private final SourceKind sourceKind;
  private final List<CommentTag> tags;

  /**
   * Create a new tag processor for a Definition or a Package.
   * @param name the name of the definition or the name of the package.
   * @param isForPackage true if the tag processor is build to process tags from a package documentation.
   */
  public CommentTagProcessor(final String name, final String comment, final SourceKind sourceKind) {
    CommentTagProcessor.definitionName = name;
    this.comment = comment;
    this.sourceKind = sourceKind;
    tags = extractTags(comment);
  }

  /**
   * Create a new tag processor for a Definition or a Package.
   * @param node is the current node targetted by the comment. Used for Method parameters.
   * @param name the name of the definition or the name of the package.
   * @param isForPackage true if the tag processor is build to process tags from a package documentation.
   */
  public CommentTagProcessor(final Node node, final String name, final String comment, final SourceKind sourceKind) {
    CommentTagProcessor.definitionName = name;
    this.comment = comment;
    this.sourceKind = sourceKind;
    CommentTagProcessor.node = node;
    tags = extractTags(comment);
  }


  public int getEndOfFirstSentence() {
    int index = comment.indexOf('.');

    for (final CommentTag tag : tags) {
      if(index < tag.beginIndex) {
        return index;
      }
      index = comment.indexOf('.', index + 1);
    }
    return index;
  }

  public String replaceTagsInComment() {
    return replaceTags(comment);
  }

  public String replaceTags(final String string) {
    lastIndex = 0;

    final StringBuilder sb = new StringBuilder();
    for (final CommentTag tag : tags) {
      if(tag.endIndex > string.length())
        break;
      appendReplacement(sb, string, tag, tag.getReplacement(definitionName, sourceKind));
    }
    appendTail(sb, string);
    return sb.toString();
  }

  public String replaceTagsInShortComment() {
    final int endOfShortComment = getEndOfFirstSentence();
    if(endOfShortComment == -1)
      return replaceTags(comment);
    else
      return replaceTags(comment.substring(0, endOfShortComment+1));

  }

  private void appendReplacement(final StringBuilder sb, final String originalString,
      final CommentTag tag, final String replacement) {

    /* check for nested tags, for example:
     * @param myParam blablablabla0 @interface blabla1
     * since @param captures the WHOLE line and started earlier than @interface,
     * when next tag @interface is handled, its beginIndex is LOWER than
     * the endIndex of @param, which leads to a negative length substring,
     * then throwing a java.lang.StringIndexOutOfBoundsException...
     * Javadoc simply skips handling the nested annotations so we do the same
     * and skip the link.
     * Substitution could be considered for later.
     */
    if (lastIndex > tag.beginIndex) {
      logger.fine("Found a nested comment annotation in definition '" + definitionName + "': Skip ! Comment was: \n" + comment);
      return;
    }

    sb.append(originalString.substring(lastIndex, tag.beginIndex));

    sb.append(replacement);
    lastIndex = tag.endIndex;
  }

  private void appendTail(final StringBuilder sb, final String originalString) {
    sb.append(originalString.substring(lastIndex));
    lastIndex = originalString.length();
  }

  public static List<CommentTag> extractTags(final String comment) {
    final List<CommentTag> tags = new LinkedList<CommentTag>();
    extractComponentLinks(comment, tags);
    extractInterfaceLinks(comment, tags);
    extractFigures(comment, tags);

    if (node != null) {
      extractParams(node, definitionName, comment, tags);
      extractReturn(node, comment, tags);
    }

    Collections.sort(tags, new CommentTag.Comparator());
    return tags;
  }

  private static void extractComponentLinks(final String comment, final List<CommentTag> tags) {
    final Matcher m = componentLinkPattern.matcher(comment);
    while (m.find()) {
      final String target = m.group(1);
      String subElemType = null;
      String subElemName = null;
      if (m.group(4) != null) {
        subElemType = m.group(5);
        if(m.group(6) != null)
          subElemName = m.group(7);
      }
      try {
        final LinkTag<ComponentLinkElementType> tag = new LinkComponentTag(target, subElemType, subElemName, m.start(), m.end());
        tags.add(tag);
      } catch (final CommentParserException e) {
        //skip tag
      }
    }
  }

  private static void extractInterfaceLinks(final String comment, final List<CommentTag> tags) {
    final Matcher m = interfaceLinkPattern.matcher(comment);
    while (m.find()) {
      final String target = m.group(1);
      String subElemType = null;
      String subElemName = null;
      if (m.group(4) != null) {
        subElemType = m.group(5);
        if(m.group(6) != null)
          subElemName = m.group(7);
      }
      try {
        final LinkTag<InterfaceLinkElementType> tag = new LinkInterfaceTag(target, subElemType, subElemName, m.start(), m.end());
        tags.add(tag);
      } catch (final CommentParserException e) {
        //skip tag
      }
    }
  }

  private static void extractFigures(final String comment, final List<CommentTag> tags) {
    final Matcher m = figurePattern.matcher(comment);
    while (m.find()) {
      final FigureTag tag;
      tag = new FigureTag(m.group(1), m.start(), m.end());
      if("width".equals(m.group(8)))
        tag.setWidth(Integer.parseInt(m.group(9)));
      else if("height".equals(m.group(8)))
        tag.setHeight(Integer.parseInt(m.group(9)));
      tags.add(tag);
    }
  }

  private static void extractParams(final Node n, final String definitionName, final String comment, final List<CommentTag> tags) {
    final Matcher m = paramPattern.matcher(comment);
    while (m.find()) {
      final ParamTag tag;
      tag = new ParamTag(n, definitionName, m.group(1), m.group(2), m.start(), m.end());
      tags.add(tag);
    }
  }

  private static void extractReturn(final Node n, final String comment, final List<CommentTag> tags) {
    final Matcher m = returnPattern.matcher(comment);
    while (m.find()) {
      final ReturnTag tag;
      tag = new ReturnTag(n, m.group(1), m.start(), m.end());
      tags.add(tag);
    }
  }
}
