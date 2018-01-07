package com.trickl.language;

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

@Value
public class EnglishCurrencyAmountFormat {

  private Currency defaultCurrency = Currency.getInstance("USD");

  private static final Set<String> ISO_3166 =
      new HashSet<>(Arrays.asList(Locale.getISOCountries()));

  private static final List<Locale> LOCALES_ISO_3166 =
      Arrays.asList(Locale.getAvailableLocales())
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
      LOCALES_ISO_3166
          .stream()
          .map(locale -> Currency.getInstance(locale).getSymbol(locale))
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
      Parsers.or(OPERATORS.tokenizer(), KEYWORDS.tokenizer(), Terminals.IntegerLiteral.TOKENIZER);

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

  private static final Parser<Function<Map.Entry<Currency, Long>, Map.Entry<Currency, Long>>>
      CURRENCY_SYMBOL =
          OPERATORS
              .token(CURRENCY_SYMBOLS.toArray(new String[0]))
              .map(
                  symbol ->
                      cn ->
                          new AbstractMap.SimpleEntry<>(
                              parseCurrencySymbol(symbol), cn.getValue()));

  private static final Function<Currency, Parser<Map.Entry<Currency, Long>>>
      CURRENCY_SYMBOL_NUMBER =
          defaultCurrency ->
              new OperatorTable<Map.Entry<Currency, Long>>()
                  .prefix(CURRENCY_SYMBOL, 10)
                  .build(
                      EnglishNumberParser.ALL_NUMBERS
                          .apply(KEYWORDS)
                          .map(amount -> new AbstractMap.SimpleEntry<>(defaultCurrency, amount)));

  private static final Parser<Map.Entry<Currency, Long>> NUMBER_CURRENCY_NAME =
      Parsers.sequence(
          EnglishNumberParser.ALL_NUMBERS.apply(KEYWORDS),
          CURRENCY_NAMES,
          (n, c) -> new AbstractMap.SimpleEntry<>(c, n));

  private static final Function<Currency, Parser<Map.Entry<Currency, Long>>> CURRENCY_AMOUNT =
      defaultCurrency ->
          Parsers.longest(CURRENCY_SYMBOL_NUMBER.apply(defaultCurrency), NUMBER_CURRENCY_NAME);

  private static Currency parseCurrencySymbol(Token token) {
    String symbol = token.toString();
    return LOCALES_ISO_3166
        .stream()
        .filter(locale -> Currency.getInstance(locale).getSymbol(locale).equals(symbol))
        .map(locale -> Currency.getInstance(locale))
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
   */
  public Map.Entry<Currency, Long> parse(String text) {
    return CURRENCY_AMOUNT
        .apply(defaultCurrency)
        .from(TOKENIZER, EnglishNumberParser.IGNORED)
        .parse(text);
  }
}
