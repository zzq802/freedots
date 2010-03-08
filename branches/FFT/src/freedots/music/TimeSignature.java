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
package freedots.music;

/**
 * The time signature is a notational convention used in Western musical
 * notation to specify how many beats are in each measure and what note value
 * constitutes one beat.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Time_signature">Wikipedia:Time_signature</a>
 */
public class TimeSignature extends Fraction {
  public TimeSignature(final int numerator, final int denominator) {
    super(numerator, denominator);
  }

  /** 3/4 is not equal to 6/8 when it comes to time signatures.
   * @param object is the time signature to compare to
   * @return true if the two time signatures are truly equal
   */
  @Override public boolean equals(Object object) {
    if (!(object instanceof TimeSignature)) return false;
    TimeSignature other = (TimeSignature)object;
    return this.getNumerator() == other.getNumerator() &&
           this.getDenominator() == other.getDenominator();
  }
}