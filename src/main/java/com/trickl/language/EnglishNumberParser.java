package com.trickl.language;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jparsec.Parser;
import org.jparsec.Parsers;
import org.jparsec.Scanners;
import org.jparsec.Terminals;
import org.jparsec.Token;

/**
 * A simple number parser with a similar set of rules to
 * https://blog.cordiner.net/2010/01/02/parsing-english-numbers-with-perl/
 */
public class EnglishNumberParser {

  @AllArgsConstructor
  @Getter
  private enum NumberLiteral {
    ZERO(0),
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10),
    ELEVEN(11),
    TWELVE(12),
    THIRTEEN(13),
    FOURTEEN(14),
    FIFTEEN(15),
    SIXTEEN(16),
    SEVENTEEN(17),
    EIGHTEEN(18),
    NINETEEN(19),
    TWENTY(20),
    THIRTY(30),
    FORTY(40),
    FIFTY(50),
    SIXTY(60),
    SEVENTY(70),
    EIGHTY(80),
    NINETY(90),
    HUNDRED(100),
    THOUSAND(1000),
    MILLION(1000000),
    BILLION(1000000000),
    TRILLION(1000000000000L);

    private final long value;
  }

  private static final Terminals OPERATORS = Terminals.operators(Collections.EMPTY_LIST);

  static final Set<String> NUMBER_LITERALS =
      EnumSet.allOf(NumberLiteral.class)
          .stream()
          .map(e -> e.toString())
          .collect(Collectors.toSet());

  private static final Terminals KEYWORDS =
      OPERATORS.words(Scanners.IDENTIFIER).caseInsensitiveKeywords(NUMBER_LITERALS).build();

  private static final Parser<?> TOKENIZER =
      Parsers.or(OPERATORS.tokenizer(), KEYWORDS.tokenizer(), Terminals.IntegerLiteral.TOKENIZER);

  static final Parser<Void> IGNORED =
      Parsers.or(Scanners.WHITESPACES, Scanners.stringCaseInsensitive("and"), Scanners.isChar(','))
          .skipMany();

  private static final Function<Terminals, Parser<Long>> ZERO =
      keywords -> numberEquals(keywords, NumberLiteral.ZERO);
  private static final Function<Terminals, Parser<Long>> ONE_TO_9 =
      keywords -> numberBetween(keywords, NumberLiteral.ONE, NumberLiteral.NINE);
  private static final Function<Terminals, Parser<Long>> TEN_TO_19 =
      keywords -> numberBetween(keywords, NumberLiteral.TEN, NumberLiteral.NINETEEN);
  private static final Function<Terminals, Parser<Long>> TENS =
      keywords -> numberBetween(keywords, NumberLiteral.TWENTY, NumberLiteral.NINETY);
  private static final Function<Terminals, Parser<Long>> HUNDRED =
      keywords -> numberEquals(keywords, NumberLiteral.HUNDRED);
  private static final Function<Terminals, Parser<Long>> THOUSAND =
      keywords -> numberEquals(keywords, NumberLiteral.THOUSAND);
  private static final Function<Terminals, Parser<Long>> MILLION =
      keywords -> numberEquals(keywords, NumberLiteral.MILLION);
  private static final Function<Terminals, Parser<Long>> BILLION =
      keywords -> numberEquals(keywords, NumberLiteral.BILLION);
  private static final Function<Terminals, Parser<Long>> TRILLION =
      keywords -> numberEquals(keywords, NumberLiteral.TRILLION);

  private static final Function<Terminals, Parser<Long>> HUNDREDS =
      keywords ->
          Parsers.sequence(
              ONE_TO_9.apply(keywords).optional(1L), HUNDRED.apply(keywords), (a, b) -> a * b);

  private static final Function<Terminals, Parser<Long>> TWENTY_TO_99 =
      keywords ->
          Parsers.sequence(
              TENS.apply(keywords), ONE_TO_9.apply(keywords).optional(0L), (a, b) -> a + b);

  private static final Function<Terminals, Parser<Long>> ONE_TO_99 =
      keywords ->
          Parsers.or(
              ONE_TO_9.apply(keywords), TEN_TO_19.apply(keywords), TWENTY_TO_99.apply(keywords));

  private static final Function<Terminals, Parser<Long>> ONE_TO_999 =
      keywords ->
          Parsers.sequence(
              HUNDREDS.apply(keywords).optional(0L),
              ONE_TO_99.apply(keywords).optional(0L),
              (a, b) -> a + b);

  private static final Function<Terminals, Parser<Long>> THOUSANDS =
      keywords ->
          Parsers.sequence(
              ONE_TO_999.apply(keywords).optional(1L), THOUSAND.apply(keywords), (a, b) -> a * b);

  private static final Function<Terminals, Parser<Long>> MILLIONS =
      keywords ->
          Parsers.sequence(
              ONE_TO_999.apply(keywords).optional(1L), MILLION.apply(keywords), (a, b) -> a * b);

  private static final Function<Terminals, Parser<Long>> BILLIONS =
      keywords ->
          Parsers.sequence(
              ONE_TO_999.apply(keywords).optional(1L), BILLION.apply(keywords), (a, b) -> a * b);

  private static final Function<Terminals, Parser<Long>> TRILLIONS =
      keywords ->
          Parsers.sequence(
              ONE_TO_999.apply(keywords).optional(1L), TRILLION.apply(keywords), (a, b) -> a * b);

  private static final Function<Terminals, Parser<Long>> ALL_POS_NUMBERS =
      keywords ->
          Parsers.sequence(
              TRILLIONS.apply(keywords).optional(0L),
              BILLIONS.apply(keywords).optional(0L),
              MILLIONS.apply(keywords).optional(0L),
              THOUSANDS.apply(keywords).optional(0L),
              ONE_TO_999.apply(keywords).optional(0L),
              (a, b, c, d, e) -> a + b + c + d + e);

  static final Function<Terminals, Parser<Long>> ALL_NUMBERS =
      keywords -> Parsers.longest(ALL_POS_NUMBERS.apply(keywords), ZERO.apply(keywords));

  private static Long parseNumberLiteral(Token token) {
    String value = token.toString().toUpperCase();
    return Enum.valueOf(NumberLiteral.class, value).getValue();
  }

  private static Parser<Long> numberEquals(Terminals keywords, NumberLiteral value) {
    return numberBetween(keywords, value, value);
  }

  private static Parser<Long> numberBetween(
      Terminals keywords, NumberLiteral minValue, NumberLiteral maxValue) {
    String[] restrictedKeywords =
        EnumSet.allOf(NumberLiteral.class)
            .stream()
            .filter(e -> e.getValue() >= minValue.getValue() && e.getValue() <= maxValue.getValue())
            .map(e -> e.toString())
            .toArray(String[]::new);

    return Parsers.or(
        keywords.token(restrictedKeywords).map(EnglishNumberParser::parseNumberLiteral),
        Terminals.IntegerLiteral.PARSER.map(Long::parseLong));
  }

  public long parse(String number) {
    return ALL_NUMBERS.apply(KEYWORDS).from(TOKENIZER, IGNORED).parse(number);
  }
}
