package com.trickl.language;

import java.util.Collections;
import java.util.EnumSet;
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

  private static final Terminals KEYWORDS =
      OPERATORS
          .words(Scanners.IDENTIFIER)
          .caseInsensitiveKeywords(
              EnumSet.allOf(NumberLiteral.class)
                  .stream()
                  .map(e -> e.toString())
                  .toArray(String[]::new))
          .build();

  private static final Parser<?> TOKENIZER =
      Parsers.or(OPERATORS.tokenizer(), KEYWORDS.tokenizer(), Terminals.IntegerLiteral.TOKENIZER);

  private static final Parser<Void> IGNORED =
      Parsers.or(Scanners.WHITESPACES, Scanners.stringCaseInsensitive("and"), Scanners.isChar(','))
          .skipMany();

  private static final Parser<Long> ZERO = keywordsEquals(NumberLiteral.ZERO);
  private static final Parser<Long> ONE_TO_9 =
      keywordBetween(NumberLiteral.ONE, NumberLiteral.NINE);
  private static final Parser<Long> TEN_TO_19 =
      keywordBetween(NumberLiteral.TEN, NumberLiteral.NINETEEN);
  private static final Parser<Long> TENS =
      keywordBetween(NumberLiteral.TWENTY, NumberLiteral.NINETY);
  private static final Parser<Long> HUNDRED = keywordsEquals(NumberLiteral.HUNDRED);
  private static final Parser<Long> THOUSAND = keywordsEquals(NumberLiteral.THOUSAND);
  private static final Parser<Long> MILLION = keywordsEquals(NumberLiteral.MILLION);
  private static final Parser<Long> BILLION = keywordsEquals(NumberLiteral.BILLION);
  private static final Parser<Long> TRILLION = keywordsEquals(NumberLiteral.TRILLION);

  private static final Parser<Long> HUNDREDS =
      Parsers.sequence(ONE_TO_9.optional(1L), HUNDRED, (a, b) -> a * b);

  private static final Parser<Long> TWENTY_TO_99 =
      Parsers.sequence(TENS, ONE_TO_9.optional(0L), (a, b) -> a + b);

  private static final Parser<Long> ONE_TO_99 = Parsers.or(ONE_TO_9, TEN_TO_19, TWENTY_TO_99);

  private static final Parser<Long> ONE_TO_999 =
      Parsers.sequence(HUNDREDS.optional(0L), ONE_TO_99.optional(0L), (a, b) -> a + b);

  private static final Parser<Long> THOUSANDS =
      Parsers.sequence(ONE_TO_999.optional(1L), THOUSAND, (a, b) -> a * b);

  private static final Parser<Long> MILLIONS =
      Parsers.sequence(ONE_TO_999.optional(1L), MILLION, (a, b) -> a * b);

  private static final Parser<Long> BILLIONS =
      Parsers.sequence(ONE_TO_999.optional(1L), BILLION, (a, b) -> a * b);

  private static final Parser<Long> TRILLIONS =
      Parsers.sequence(ONE_TO_999.optional(1L), TRILLION, (a, b) -> a * b);

  private static final Parser<Long> ALL_POS_NUMBERS =
      Parsers.sequence(
          TRILLIONS.optional(0L),
          BILLIONS.optional(0L),
          MILLIONS.optional(0L),
          THOUSANDS.optional(0L),
          ONE_TO_999.optional(0L),
          (a, b, c, d, e) -> a + b + c + d + e);

  private static final Parser<Long> ALL_NUMBERS = Parsers.longest(ALL_POS_NUMBERS, ZERO);

  private static Long parseNumberLiteral(Token token) {
    String value = token.toString().toUpperCase();
    return Enum.valueOf(NumberLiteral.class, value).getValue();
  }

  private static Parser<Long> keywordsEquals(NumberLiteral value) {
    return keywordBetween(value, value);
  }

  private static Parser<Long> keywordBetween(NumberLiteral minValue, NumberLiteral maxValue) {
    String[] keywords =
        EnumSet.allOf(NumberLiteral.class)
            .stream()
            .filter(e -> e.getValue() >= minValue.getValue() && e.getValue() <= maxValue.getValue())
            .map(e -> e.toString())
            .toArray(String[]::new);

    return KEYWORDS.token(keywords).map(EnglishNumberParser::parseNumberLiteral);
  }

  public long parse(String number) {
    return ALL_NUMBERS.from(TOKENIZER, IGNORED).parse(number);
  }
}
