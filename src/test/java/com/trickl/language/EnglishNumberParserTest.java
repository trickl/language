package com.trickl.language;

import java.text.ParseException;
import org.junit.Assert;
import org.junit.Test;

/** @author tgee */
public class EnglishNumberParserTest {

  @Test
  public void testParseNumbersOnly() throws ParseException {
    // Numbers all lowercase
    Assert.assertEquals(0, new EnglishNumberParser().parse("zero"));
    Assert.assertEquals(3, new EnglishNumberParser().parse("three"));
    Assert.assertEquals(14, new EnglishNumberParser().parse("fourteen"));
    Assert.assertEquals(35, new EnglishNumberParser().parse("thirty five"));
    Assert.assertEquals(70, new EnglishNumberParser().parse("seventy"));
    Assert.assertEquals(100, new EnglishNumberParser().parse("hundred"));
    Assert.assertEquals(100, new EnglishNumberParser().parse("one hundred"));
    Assert.assertEquals(264, new EnglishNumberParser().parse("two hundred sixty four"));
    Assert.assertEquals(13000, new EnglishNumberParser().parse("thirteen thousand"));
    Assert.assertEquals(45055, new EnglishNumberParser().parse("forty five thousand fifty five"));
    Assert.assertEquals(
        99387, new EnglishNumberParser().parse("ninety nine thousand three hundred eighty seven"));
  }

  @Test
  public void testParseNumbersWithCommas() throws ParseException {
    Assert.assertEquals(45055, new EnglishNumberParser().parse("forty five thousand, fifty five"));
    Assert.assertEquals(
        99387, new EnglishNumberParser().parse("ninety nine thousand three hundred, eighty seven"));
  }

  @Test
  public void testParseNumbersWithAnd() throws ParseException {
    Assert.assertEquals(
        45055, new EnglishNumberParser().parse("forty five thousand and fifty five"));
    Assert.assertEquals(
        99387,
        new EnglishNumberParser().parse("ninety nine thousand three hundred and eighty seven"));
  }

  @Test
  public void testParseCaseInsensitvity() throws ParseException {
    Assert.assertEquals(100, new EnglishNumberParser().parse("Hundred"));
    Assert.assertEquals(100, new EnglishNumberParser().parse("ONE HUNDRED"));
    Assert.assertEquals(264, new EnglishNumberParser().parse("Two hundred sixty four"));
  }

  @Test
  public void testParseMixNumbersText() throws ParseException {
    Assert.assertEquals(200, new EnglishNumberParser().parse("2 hundred"));
    Assert.assertEquals(13000000, new EnglishNumberParser().parse("13 million"));
    Assert.assertEquals(13000012, new EnglishNumberParser().parse("13 million and 12"));
    Assert.assertEquals(
        23000397, new EnglishNumberParser().parse("23 million, three hundred and 97"));
  }
}
