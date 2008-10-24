/* -*- c-basic-offset: 2; -*- */

package musicxml;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import java.math.BigInteger;

import java.net.URL;

import java.util.List;
import java.util.ArrayList;

import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MusicXML {
  private Document document;

  public MusicXML(
    String filename
  ) throws ParserConfigurationException,
	   IOException, SAXException, XPathExpressionException {
    File file = new File(filename);
    InputStream inputStream = null;
    String extension = null;

    int dot = filename.lastIndexOf('.');
    if (dot != -1) {
      extension = filename.substring(dot + 1);
    }

    if (file.exists()) {
      inputStream = new FileInputStream(file);
    } else {
      URL url = new URL(filename);
      inputStream = url.openConnection().getInputStream();
    }
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    documentBuilder.setEntityResolver(new MusicXMLEntityResolver());

    if ("mxl".equals(extension)) {
      String zipEntryName = null;
      ZipInputStream zipInputStream = new ZipInputStream(inputStream);
      ZipEntry zipEntry = null;
      while ((zipEntry = zipInputStream.getNextEntry()) != null) {
	if ("META-INF/container.xml".equals(zipEntry.getName())) {
	  Document container = documentBuilder.parse(getInputSourceFromZipInputStream(zipInputStream));
	  XPath xpath = XPathFactory.newInstance().newXPath();
	  zipEntryName = (String) xpath.evaluate("container/rootfiles/rootfile/@full-path",
						 container,
						 XPathConstants.STRING);
	} else if (zipEntry.getName().equals(zipEntryName)) {
	  document = documentBuilder.parse(getInputSourceFromZipInputStream(zipInputStream));
	}
	zipInputStream.closeEntry();
      }
    } else { /* Plain XML file */
      document = documentBuilder.parse(inputStream);
    }
    document.getDocumentElement().normalize();
  }

  private InputSource getInputSourceFromZipInputStream(
    ZipInputStream zipInputStream
  ) throws IOException {
    BufferedReader reader =
      new BufferedReader(new InputStreamReader(zipInputStream));
    StringBuilder stringBuilder = new StringBuilder();
    String string = null;
    while ((string = reader.readLine()) != null) {
      stringBuilder.append(string + "\n");
    }
    return new InputSource(new StringReader(stringBuilder.toString()));
  }

  public String getScoreType () {
    return document.getDocumentElement().getNodeName();
  }

  public int getDivisions() {
    XPath xpath = XPathFactory.newInstance().newXPath();
    try {
      NodeList nodelist = (NodeList) xpath.evaluate("//attributes/divisions/text()",
						   document,
						   XPathConstants.NODESET);
      BigInteger result = BigInteger.ONE;
      for (int i = 0; i < nodelist.getLength(); i++) {
	Node node = nodelist.item(i);
	BigInteger divisions = new BigInteger(node.getNodeValue());
	result = result.multiply(divisions.divide(result.gcd(divisions)));
      }
      return result.intValue();
    } catch (XPathExpressionException e) {
      return 0;
    }
  }

  public List<Part> parts() {
    List<Part> result = new ArrayList<Part>();
    Element root = document.getDocumentElement();
    NodeList nodes = root.getElementsByTagName("part");
    Element partList = (Element) root.getElementsByTagName("part-list").item(0);
    NodeList partListKids = partList.getChildNodes();
    for (int i=0; i<nodes.getLength(); i++) {
      Element part = (Element) nodes.item(i);
      String idValue = part.getAttribute("id");
      Element scorePart = null;
      for (int j=0; j<partListKids.getLength(); j++) {
	Node kid = partListKids.item(j);
	if (kid.getNodeType() == Node.ELEMENT_NODE) {
	  Element elem = (Element) kid;
	  if (idValue.equals(elem.getAttribute("id"))) {
	    scorePart = elem;
	  }
	}
      }
      result.add(new Part(part, scorePart));
    }
    return result;
  }
}
