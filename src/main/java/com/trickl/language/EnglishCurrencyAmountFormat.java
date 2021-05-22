package com.trickl.language;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Value;
import org.jparsec.OperatorTable;
import org.jparsec.Parser;
import org.jparsec.Parsers;
import org.jparsec.Scanners;
import org.jparsec.Terminals;
import org.jparsec.Token;
import org.jparsec.error.ParserException;

@Value
public class EnglishCurrencyAmountFormat {

  private Currency defaultCurrency = Currency.getInstance("USD");

  private static final Set<String> ISO_3166 =
      new HashSet<>(Arrays.asList(Locale.getISOCountries()));

  private static final List<Locale.LanguageRange> LOCALE_PRIORITY_LIST = 
       Stream.concat(Locale.LanguageRange.parse("en-US;q=1.0,en-GB").stream(),
       Arrays.asList(Locale.getAvailableLocales()).stream()
       .map(locale -> new Locale.LanguageRange(locale.toLanguageTag())))
       .collect(Collectors.toList());

  private static final List<Locale> LOCALES_ISO_3166 =
      Locale.filter(LOCALE_PRIORITY_LIST, Arrays.asList(Locale.getAvailableLocales()))
          .stream()
          .filter(locale -> ISO_3166.contains(locale.getCountry()))
          .collect(Collectors.toList());

  private static final Set<String> CURRENCY_NAME_KEYWORDS =
      LOCALES_ISO_3166
          .stream()
          .map(locale -> Currency.getInstance(locale).getDisplayName(Locale.ENGLISH).toUpperCase())
          .map(displayName -> displayName.split(" "))
          .flatMap(Arrays::stream)
          .collect(Collectors.toSet());

  private static final Set<String> CURRENCY_SYMBOLS =
      Stream.concat(
              LOCALES_ISO_3166
                  .stream()
                  .map(locale -> Currency.getInstance(locale).getSymbol(locale)),
              Arrays.stream(AltCurrencySymbol.values()).map(acs -> acs.getSymbol()))
          .collect(Collectors.toSet());

  private static final Terminals OPERATORS = Terminals.operators(CURRENCY_SYMBOLS);

  private static final Terminals KEYWORDS =
      OPERATORS
          .words(Scanners.IDENTIFIER)
          .caseInsensitiveKeywords(
              Stream.concat(
                      CURRENCY_NAME_KEYWORDS.stream(), EnglishNumberParser.NUMBER_LITERALS.stream())
                  .collect(Collectors.toSet()))
          .build();

  private static final Parser<?> TOKENIZER =
      Parsers.or(OPERATORS.tokenizer(), KEYWORDS.tokenizer(), Terminals.DecimalLiteral.TOKENIZER);

  private static final Parser<Currency> CURRENCY_NAMES =
      Parsers.or(
          LOCALES_ISO_3166
              .stream()
              .map(
                  locale ->
                      Currency.getInstance(locale).getDisplayName(Locale.ENGLISH).toUpperCase())
              .map(
                  displayName ->
                      KEYWORDS
                          .phrase(displayName.split(" "))
                          .map(EnglishCurrencyAmountFormat::parseCurrencyDisplayName))
              .collect(Collectors.toList()));

  private static final Parser<
          Function<Map.Entry<Currency, BigDecimal>, Map.Entry<Currency, BigDecimal>>>
      CURRENCY_SYMBOL =
          OPERATORS
              .token(CURRENCY_SYMBOLS.toArray(new String[0]))
              .map(
                  symbol ->
                      cn ->
                          new AbstractMap.SimpleEntry<>(
                              parseCurrencySymbol(symbol), cn.getValue()));

  private static final Function<Currency, Parser<Map.Entry<Currency, BigDecimal>>>
      CURRENCY_SYMBOL_NUMBER =
          defaultCurrency ->
              new OperatorTable<Map.Entry<Currency, BigDecimal>>()
                  .prefix(CURRENCY_SYMBOL, 10) 
                  .build(
                      EnglishNumberParser.ALL_NUMBERS
                          .apply(KEYWORDS)
                          .map(amount -> new AbstractMap.SimpleEntry<>(defaultCurrency, amount)));

  private static final Parser<Map.Entry<Currency, BigDecimal>> NUMBER_CURRENCY_NAME =
      Parsers.sequence(
          EnglishNumberParser.ALL_NUMBERS
              .apply(KEYWORDS),              
          CURRENCY_NAMES,
          (n, c) -> new AbstractMap.SimpleEntry<>(c, n));

  private static final Function<Currency, Parser<Map.Entry<Currency, BigDecimal>>> CURRENCY_AMOUNT =
      defaultCurrency ->
          Parsers.longest(CURRENCY_SYMBOL_NUMBER.apply(defaultCurrency), NUMBER_CURRENCY_NAME);

  private static Currency parseCurrencySymbol(Token token) {
    String symbol = token.toString();
    return Stream.concat(
            LOCALES_ISO_3166
                .stream()
                .filter(locale -> Currency.getInstance(locale).getSymbol(locale).equals(symbol))
                .map(locale -> Currency.getInstance(locale)),
            Arrays.stream(AltCurrencySymbol.values())
                .filter(acs -> acs.getSymbol().equals(symbol))
                .map(acs -> Currency.getInstance(acs.getCode())))
        .findFirst()
        .get();
  }

  private static Currency parseCurrencyDisplayName(Object token) {
    String displayName = token.toString().toUpperCase();
    return LOCALES_ISO_3166
        .stream()
        .filter(
            locale ->
                Currency.getInstance(locale)
                    .getDisplayName(locale)
                    .toUpperCase()
                    .equals(displayName))
        .map(locale -> Currency.getInstance(locale))
        .findFirst()
        .get();
  }

  /**
   * Convert a string into a currency and amount.
   *
   * @param text The string to parse
   * @return The currency and amount
   * @throws ParseException if unable to parse the string
   */
  public Map.Entry<Currency, BigDecimal> parse(String text) 
      throws ParseException {
    try {
      return CURRENCY_AMOUNT
          .apply(defaultCurrency)
          .from(TOKENIZER, EnglishNumberParser.IGNORED)
          .parse(text);
    } catch (ParserException ex) {
      throw new ParseException(ex.getMessage(), ex.getLocation().column);
    }
  }
}
