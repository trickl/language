package com.trickl.language;

import java.text.ParseException;
import java.util.AbstractMap;
import java.util.Currency;
import org.junit.Assert;
import org.junit.Test;

public class EnglishCurrencyAmountFormatTest {

  @Test
  public void testParseCurrencyAmounts() throws ParseException {
    Assert.assertEquals(
        new AbstractMap.SimpleEntry<>(Currency.getInstance("GBP"), 0L),
        new EnglishCurrencyAmountFormat().parse("£0"));
    Assert.assertEquals(
        new AbstractMap.SimpleEntry<>(Currency.getInstance("USD"), 0L),
        new EnglishCurrencyAmountFormat().parse("$0"));
    Assert.assertEquals(
        new AbstractMap.SimpleEntry<>(Currency.getInstance("GBP"), 23L),
        new EnglishCurrencyAmountFormat().parse("£23"));
    Assert.assertEquals(
        new AbstractMap.SimpleEntry<>(Currency.getInstance("GBP"), 13000000L),
        new EnglishCurrencyAmountFormat().parse("£13 million"));   
    Assert.assertEquals(
        new AbstractMap.SimpleEntry<>(Currency.getInstance("USD"), 110000000L),
        new EnglishCurrencyAmountFormat().parse("$110\u00a0million"));       
    Assert.assertEquals(
        new AbstractMap.SimpleEntry<>(Currency.getInstance("EUR"), 13000000L),
        new EnglishCurrencyAmountFormat().parse("13 million euro"));
    Assert.assertEquals(
        new AbstractMap.SimpleEntry<>(Currency.getInstance("USD"), 13000000L),
        new EnglishCurrencyAmountFormat().parse("13 million us dollar"));
    Assert.assertEquals(
        new AbstractMap.SimpleEntry<>(Currency.getInstance("USD"), 200L),
        new EnglishCurrencyAmountFormat().parse("200 US dollar"));
    Assert.assertEquals(
        new AbstractMap.SimpleEntry<>(Currency.getInstance("GBP"), 100L),
        new EnglishCurrencyAmountFormat().parse("one hundred british pound sterling"));
    Assert.assertEquals(
        new AbstractMap.SimpleEntry<>(Currency.getInstance("INR"), 420000000L),
        new EnglishCurrencyAmountFormat().parse("₹420 million"));    
  }
}
