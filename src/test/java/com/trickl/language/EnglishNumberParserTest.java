package com.trickl.language;

import java.text.ParseException;
import org.junit.Assert;
import org.junit.Test;

/** @author tgee */
public class EnglishNumberParserTest {

  @Test
  public void testParseNumbersOnly() throws ParseException {
    // Numbers all lowercase
    assertParsesLong(0, "zero");
    assertParsesLong(3, "three");
    assertParsesLong(14, "fourteen");
    assertParsesLong(35, "thirty five");
    assertParsesLong(70, "seventy");
    assertParsesLong(100, "hundred");
    assertParsesLong(100, "one hundred");
    assertParsesLong(264, "two hundred sixty four");
    assertParsesLong(13000, "thirteen thousand");
    assertParsesLong(45055, "forty five thousand fifty five");
    assertParsesLong(99387, "ninety nine thousand three hundred eighty seven");
  }

  @Test
  public void testParseNumbersWithCommas() throws ParseException {
    assertParsesLong(45055, "forty five thousand, fifty five");
    assertParsesLong(99387, "ninety nine thousand three hundred, eighty seven");
  }

  @Test
  public void testParseNumbersWithAnd() throws ParseException {
    assertParsesLong(45055, "forty five thousand and fifty five");
    assertParsesLong(99387, "ninety nine thousand three hundred and eighty seven");
  }

  @Test
  public void testParseCaseInsensitvity() throws ParseException {
    assertParsesLong(100, "Hundred");
    assertParsesLong(100, "ONE HUNDRED");
    assertParsesLong(264, "Two hundred sixty four");
  }

  @Test
  public void testParseMixNumbersText() throws ParseException {
    assertParsesLong(200, "2 hundred");
    assertParsesLong(13000000, "13 million");
    assertParsesLong(13000012, "13 million and 12");
    assertParsesLong(23000397, "23 million, three hundred and 97");
  }

  @Test
  public void testParseCommaDelimitedText() throws ParseException {
    assertParsesLong(1200, "1,200");
    assertParsesLong(13000001, "13,000,001");
    assertParsesLong(123456789, "123,456,789");
    assertParsesLong(123456789, "123،456،789");
  }
  
  @Test
  public void testParseDecimals() throws ParseException {
    assertParsesDouble(0.7, "0.7", 1e-5);
    assertParsesDouble(0.34, ".34", 1e-5);
  }
  
  private void assertParsesLong(long expected, String text) {
    Assert.assertEquals(expected, new EnglishNumberParser()
        .parse(text).longValueExact());
  }
  
  private void assertParsesDouble(double expected, String text, double tolerance) {
    Assert.assertEquals(expected, new EnglishNumberParser()
        .parse(text).floatValue(), tolerance);
  }
}
