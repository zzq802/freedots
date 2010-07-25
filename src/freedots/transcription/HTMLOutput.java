/* -*- c-basic-offset: 2; indent-tabs-mode: nil; -*- */
/*
 * FreeDots -- MusicXML to braille music transcription
 *
 * Copyright 2008-2010 Mario Lang  All Rights Reserved.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details (a copy is included in the LICENSE.txt file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License
 * along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This file is maintained by Mario Lang <mlang@delysid.org>.
 */
package freedots.transcription;

import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import freedots.Options;
import freedots.braille.BrailleList;
import freedots.braille.BrailleSequence;
import freedots.braille.NewLine;
import freedots.braille.Sign;
import freedots.musicxml.Score;
import freedots.transcription.Transcriber;

public final class HTMLOutput {
  public static String convert(BrailleList braille) throws javax.xml.parsers.ParserConfigurationException {
    DOMImplementation dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().getDOMImplementation();
    DocumentType docType =
      dom.createDocumentType("html", "-//W3C//DTD XHTML 1.0 Strict//EN",
                             "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd");
    Document document = dom.createDocument("http://www.w3.org/1999/xhtml",
                                           "html", docType);
    Element html = document.getDocumentElement();
    Element head = document.createElement("head");
    Element meta = document.createElement("meta");
    meta.setAttribute("http-equiv", "Content-Type");
    meta.setAttribute("content", "text/html; charset=utf-8");
    head.appendChild(meta);
    Element style = document.createElement("style");
    style.setAttribute("type", "text/css");
    style.setAttribute("media", "all");
    style.setTextContent(".freedots.braille.PitchAndValueSign {color: blue; }");
    head.appendChild(style);
    html.appendChild(head);

    Element body = document.createElement("body");

    appendHTML(braille, body);

    html.appendChild(body);

    DOMSource domSource = new DOMSource(document);
    StringWriter stringWriter = new StringWriter();
    StreamResult resultStream = new StreamResult(stringWriter);
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    try {
      Transformer transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC,
                                    docType.getPublicId());
      transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,
                                    docType.getSystemId());
      transformer.setOutputProperty(OutputKeys.INDENT, "no");
      try {
        transformer.transform(domSource, resultStream);

        return stringWriter.toString();
      } catch (javax.xml.transform.TransformerException e) {
        e.printStackTrace();
      }
    } catch (javax.xml.transform.TransformerConfigurationException e) {
      e.printStackTrace();
    }
    return null;
  }

  static void appendHTML(BrailleList braille, Element element) {
    Element span = element.getOwnerDocument().createElement("span");
    span.setAttribute("class", braille.getClass().getName());

    for (BrailleSequence sequence: braille) {
      if (sequence instanceof NewLine) {
        Element br = element.getOwnerDocument().createElement("br");
        span.appendChild(br);
      } else if (sequence instanceof Sign) {
        Element container = element.getOwnerDocument().createElement("span");
        container.setAttribute("title", sequence.getDescription());
        container.setAttribute("class", sequence.getClass().getName());
        String string = sequence.toString().replaceAll(" ", String.valueOf((char)160));
        container.setTextContent(string);
        span.appendChild(container);
      } else appendHTML((BrailleList)sequence, span);
    }
    element.appendChild(span);
  }
  public static void main(String[] args) {
    try {
      Score score = new Score("freedots/musicxml/bwv1013-1.xml");
      Options options = new Options(args);
      Transcriber transcriber = new Transcriber(options);
      transcriber.setScore(score);
      System.out.println(transcriber.toString(freedots.braille.BrailleEncoding.HTML));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
