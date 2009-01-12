/* -*- c-basic-offset: 2; -*- */
package org.delysid.freedots.musicxml;

import java.util.Map;
import java.util.HashMap;

import org.delysid.freedots.AugmentedFraction;
import org.delysid.freedots.Fraction;

import org.delysid.freedots.model.StaffElement;
import org.delysid.freedots.model.VoiceElement;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class Note extends Musicdata implements StaffElement, VoiceElement {
  Fraction offset;

  Element grace = null;
  Pitch pitch = null;
  Text staff;
  Text voice;
  Type type = Type.NONE;

  private static Map<String, Type> typeMap = new HashMap<String, Type>() {
    {
      put("long", Type.LONG);
      put("breve", Type.BREVE);
      put("whole", Type.WHOLE);
      put("half", Type.HALF);
      put("quarter", Type.QUARTER);
      put("eighth", Type.EIGHTH);
      put("16th", Type.SIXTEENTH);
      put("32nd", Type.THIRTYSECOND);
      put("64th", Type.SIXTYFOURTH);
      put("128th", Type.ONEHUNDREDTWENTYEIGHTH);
      put("256th", Type.TWOHUNDREDFIFTYSIXTH);
    }
  };

  public Note(
    Fraction offset, Element element,
    int divisions, int durationMultiplier
  ) throws MusicXMLParseException {
    super(element, divisions, durationMultiplier);
    this.offset = offset;
    NodeList nodeList = element.getElementsByTagName("grace");
    if (nodeList.getLength() >= 1) {
      grace = (Element)nodeList.item(nodeList.getLength()-1);
    }
    nodeList = element.getElementsByTagName("pitch");
    if (nodeList.getLength() >= 1) {
      pitch = new Pitch((Element)nodeList.item(nodeList.getLength()-1));
    }
    staff = getTextContent(element, "staff");
    voice = getTextContent(element, "voice");

    Text textNode = getTextContent(element, "type");
    if (textNode != null) {
      String typeName = textNode.getWholeText();
      String santizedTypeName = typeName.trim().toLowerCase();
      if (typeMap.containsKey(santizedTypeName))
        type = typeMap.get(santizedTypeName);
      else
        throw new MusicXMLParseException("Illegal <type> content '"+typeName+"'");
    }
  }

  public boolean isGrace() {
    if (grace != null) return true;
    return false;
  }
  public boolean isRest() {
    if ("forward".equals(element.getTagName()) ||
        element.getElementsByTagName("rest").getLength() > 0)
      return true;
    return false;
  }
  public Pitch getPitch() {
    return pitch;
  }
  public String getStaffName() {
    if (staff != null) {
      return staff.getWholeText();
    }
    return null;
  }
  public String getVoiceName() {
    if (voice != null) {
      return voice.getWholeText();
    }
    return null;
  }
  public void setVoiceName(String name) {
    if (voice != null) {
      voice.replaceWholeText(name);
    }
  }

  public AugmentedFraction getAugmentedFraction() {
    if (type != Type.NONE) {
      return new AugmentedFraction(type.getNumerator(), type.getDenominator(),
                                   element.getElementsByTagName("dot").getLength());
    } else {
      return new AugmentedFraction(getDuration());
    }
  }

  public Fraction getOffset() { return offset; }

  enum Type {
    LONG(4, 1), BREVE(2, 1), WHOLE(1, 1), HALF(1, 2), QUARTER(1, 4),
    EIGHTH(1, 8), SIXTEENTH(1, 16), THIRTYSECOND(1, 32),
    SIXTYFOURTH(1, 64), ONEHUNDREDTWENTYEIGHTH(1, 128),
    TWOHUNDREDFIFTYSIXTH(1, 256), NONE(0, 1);

    int numerator;
    int denominator;
    private Type(int numerator, int denominator) {
      this.numerator = numerator;
      this.denominator = denominator;
    }
    int getNumerator() { return numerator; }
    int getDenominator() { return denominator; }      
  }
  static Text getTextContent(Element element, String childTagName) {
    NodeList nodeList = element.getElementsByTagName(childTagName);
    if (nodeList.getLength() >= 1) {
      nodeList = nodeList.item(nodeList.getLength()-1).getChildNodes();
      for (int index = 0; index < nodeList.getLength(); index++) {
        Node node = nodeList.item(index);
        if (node.getNodeType() == Node.TEXT_NODE) return (Text)node;
      }
    }
    return null;
  }
}