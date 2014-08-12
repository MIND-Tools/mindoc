/**
 * Copyright (C) 2009 STMicroelectronics
 *
 * This file is part of "Mind Compiler".
 * "Mind Compiler" is a free software tool.
 * This file is licensed under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact: mind@ow2.org
 *
 * Authors: Ali-Erdem Ozcan, Michel Metzger, Matthieu Leclercq
 * Contributors:
 */

package org.ow2.mind.doc.idl;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import org.ow2.mind.idl.jtb.syntaxtree.NodeToken;
import org.ow2.mind.idl.jtb.syntaxtree.TypeDefName;
import org.ow2.mind.idl.jtb.visitor.DepthFirstVisitor;

/**
 * Dumps the syntax tree to a Writer using the location information in
 * each NodeToken.
 */
public class IDLTreeDumper extends DepthFirstVisitor {
   protected PrintWriter out;
   private int curLine = 1;
   private int curColumn = 1;
   private boolean startAtNextToken = false;

   /**
    * The default constructor uses System.out as its output location.
    * You may specify your own Writer or OutputStream using one of the
    * other constructors.
    */
   public IDLTreeDumper()       { out = new PrintWriter(System.out, true); }
   public IDLTreeDumper(final Writer o)        { out = new PrintWriter(o, true); }
   public IDLTreeDumper(final OutputStream o)  { out = new PrintWriter(o, true); }


   List<String> tagQueue = new LinkedList<String>();

   /**
    * Flushes the OutputStream or Writer that this TreeDumper is using.
    */
   public void flushWriter()        { out.flush(); }

   /**
    * Starts the tree dumper on the line containing the next token
    * visited.  For example, if the next token begins on line 50 and the
    * dumper is currently on line 1 of the file, it will set its current
    * line to 50 and continue printing from there, as opposed to
    * printing 49 blank lines and then printing the token.
    */
   public void startAtNextToken()   { startAtNextToken = true; }

   /**
    * Resets the position of the output "cursor" to the first line and
    * column.  When using a dumper on a syntax tree more than once, you
    * either need to call this method or startAtNextToken() between each
    * dump.
    */
   public void resetPosition()      { curLine = curColumn = 1; }

   /**
    * Dumps the current NodeToken to the output stream being used.
    *
    * @throws  IllegalStateException   if the token position is invalid
    *   relative to the current position, i.e. its location places it
    *   before the previous token.
    */
   @Override
  public void visit(final NodeToken n) {
      if ( n.beginLine == -1 || n.beginColumn == -1 ) {
         printToken(n.tokenImage);
         return;
      }

      //
      // Handle startAtNextToken option
      //
      if ( startAtNextToken ) {
         curLine = n.beginLine;
         curColumn = 1;
         startAtNextToken = false;

         if ( n.beginColumn < curColumn )
            out.println();
      }

      //
      // Check for invalid token position relative to current position.
      //
      if ( n.beginLine < curLine )
         throw new IllegalStateException("at token \"" + n.tokenImage +
            "\", n.beginLine = " + Integer.toString(n.beginLine) +
            ", curLine = " + Integer.toString(curLine));
      else if ( n.beginLine == curLine && n.beginColumn < curColumn )
         throw new IllegalStateException("at token \"" + n.tokenImage +
            "\", n.beginColumn = " +
            Integer.toString(n.beginColumn) + ", curColumn = " +
            Integer.toString(curColumn));

      //
      // Move output "cursor" to proper location, then print the token
      //
      if ( curLine < n.beginLine ) {
         curColumn = 1;
         for ( ; curLine < n.beginLine; ++curLine )
            out.print("<br />");
      }

      for ( ; curColumn < n.beginColumn; ++curColumn )
         out.print("&nbsp;");

      /*for (final String tag : tagQueue) {
        out.print(tag);
      }
      tagQueue.clear();*/
      printToken(n.tokenImage);
   }

   private void printToken(final String s) {
      for ( int i = 0; i < s.length(); ++i ) {
         if ( s.charAt(i) == '\n' ) {
            ++curLine;
            curColumn = 1;
         }
         else
            curColumn++;

         out.print(s.charAt(i));
      }

      out.flush();
   }

   protected void addTag(final String tag) {
     tagQueue.add(tag);
   }

   @Override
  public void visit(final TypeDefName n) {
     addTag(String.format("<a href=\"#%1$s\" title=\"typedef %2$s\">", n.f0.toString(), n.f0.toString()));
     super.visit(n);
     addTag("</a>");
  }
}
