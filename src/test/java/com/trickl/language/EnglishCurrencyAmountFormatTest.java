package com.trickl.language;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.AbstractMap;
import java.util.Currency;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

public class EnglishCurrencyAmountFormatTest {

  @Test
  public void testParseCurrencyAmounts() throws ParseException {
    assertParsesCurrencyAmount("£0", "GBP", 0L);
    assertParsesCurrencyAmount("$0", "USD", 0L);
    assertParsesCurrencyAmount("£23", "GBP", 23L);
    assertParsesCurrencyAmount("£13 million", "GBP", 13000000L);
    assertParsesCurrencyAmount("$110\u00a0million", "USD", 110000000L);
    
    assertParsesCurrencyAmount("$1.4 million", "USD", 1400000L);
    assertParsesCurrencyAmount("$1.4 million and five hundred", "USD", 1400500L);
    assertParsesCurrencyAmount("$1.4 million and 500", "USD", 1400500L);
    assertParsesCurrencyAmount("$1،350،000", "USD", 1350000L);
    
    assertParsesCurrencyAmount("13 million euro", "EUR", 13000000L);
    assertParsesCurrencyAmount("13 million us dollar", "USD", 13000000L);
    assertParsesCurrencyAmount("200 US dollar", "USD", 200L);
    assertParsesCurrencyAmount("one hundred british pound sterling", "GBP", 100L);
    assertParsesCurrencyAmount("₹420 million", "INR", 420000000L);
    
    // assertParsesCurrencyAmount("₹20 crore", "INR", 200000000);
    // assertParsesCurrencyAmount("₹13 lakt crore", "INR", 13000000000);
    
  }
  
  private void assertParsesCurrencyAmount(
      String value,
      String expectedCurrencyCode,
      long expectedAmount) {
    Map.Entry<Currency, BigDecimal> parsed = new EnglishCurrencyAmountFormat().parse(value);
    Map.Entry<Currency, Long> rounded = 
        new AbstractMap.SimpleEntry<>(parsed.getKey(), parsed.getValue().longValue());
    Map.Entry<Currency, Long> expected =
        new AbstractMap.SimpleEntry<>(Currency.getInstance(expectedCurrencyCode), expectedAmount);
    
    Assert.assertEquals(expected, rounded); 
  }  
}
