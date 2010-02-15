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
package freedots.math;

/** Represents an arbitrary fractional value.
 */
public class Fraction extends AbstractFraction {
  private int numerator, denominator;

  public Fraction(final int numerator, final int denominator) {
    if (denominator == 0)
      throw new ArithmeticException("denominator is zero");
    this.numerator = numerator;
    this.denominator = denominator;
  }

  public int numerator() { return numerator; }
  public int denominator() { return denominator; }

  public Fraction simplify() {
    final int n = numerator();
    final int d = denominator();
    final int gcd = gcd(n, d);
    numerator = n / gcd;
    denominator = d / gcd;
    return this;
  }

  public final static Fraction ZERO = new Fraction(0, 1);
}