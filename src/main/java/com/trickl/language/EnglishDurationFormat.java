package com.trickl.language;

import java.text.ParseException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

@Log
@RequiredArgsConstructor
public final class EnglishDurationFormat {

  private final ChronoUnit accuracy;

  private final boolean showZeroes;

  private static final Map<String, ChronoUnit> UNIT_ALIASES;
  
  private static final List<ChronoUnit> SORTED_UNITS;
  
  private static final String FORMAT_PATTERN = 
      "^((\\p{Space}*(\\p{Digit}+))?\\p{Space}*(\\p{Alpha}+)\\p{Space}*).*";
  
  public EnglishDurationFormat() {
    this(ChronoUnit.MILLIS);
  }
  
  public EnglishDurationFormat(ChronoUnit accuracy) {
    this(ChronoUnit.MILLIS, false);  
  }

  static {
    Map<String, ChronoUnit> m = new HashMap<>();
    m.put("hrs", ChronoUnit.HOURS);
    m.put("mins", ChronoUnit.MINUTES);
    m.put("s", ChronoUnit.SECONDS);
    m.put("ms", ChronoUnit.MILLIS);
    m.put("milliseconds", ChronoUnit.MILLIS);
    m.put("microseconds", ChronoUnit.MICROS);
    m.put("ns", ChronoUnit.NANOS);
    m.put("nanoseconds", ChronoUnit.NANOS);
    UNIT_ALIASES = Collections.unmodifiableMap(m);
    
    SORTED_UNITS = new ArrayList<>(EnumSet.allOf(ChronoUnit.class));
    SORTED_UNITS.remove(ChronoUnit.FOREVER);
    SORTED_UNITS.remove(ChronoUnit.ERAS);    
    Collections.sort(SORTED_UNITS, Comparator.reverseOrder());
  }

  /**
   * Format a duration as an English string in the form [unit_quantity unit]+ e.g. 1 day 2 hours 15
   * minutes.
   *
   * @param duration The duration to convert
   * @return English representation
   */
  public String format(Duration duration) {    
    List<String> formattedParts = new LinkedList<>();
    for (ChronoUnit unit : SORTED_UNITS) {
      if (unit.compareTo(accuracy) == -1) {
        break;
      }

      ChronoUnit resolveUnit = ChronoUnit.SECONDS;
      if (unit.compareTo(ChronoUnit.SECONDS) < 0) {
        resolveUnit = ChronoUnit.NANOS;
      }

      long resolvedUnit = unit.getDuration().get(resolveUnit);
      long resolvedAmount = duration.get(resolveUnit);
      long unitSize = resolvedAmount / resolvedUnit;
      if (unitSize > 0 || showZeroes) {
        String unitName = unit.toString().toLowerCase();
        if (unitSize == 1) {
          // Singularize unit
          unitName = singularize(unitName);
        }
        String formattedPart = unitSize + " " + unitName;
        formattedParts.add(formattedPart);
        duration = duration.minus(unitSize, unit);
      }
    }

    return String.join(" ", formattedParts);
  }

  /**
   * Convert an English text string into a duration.
   *
   * @param text The text to parse, same format as .format
   * @return A duration object
   * @throws ParseException If text cannot be parsed
   */
  public Duration parse(String text) throws ParseException {
    Duration duration = null;
    String value = text;

    while (!value.isEmpty()) {
      Pattern amountAndUnitPattern =
          Pattern.compile(FORMAT_PATTERN);
      Matcher matcher = amountAndUnitPattern.matcher(value);
      if (matcher.matches()) {
        value = value.substring(matcher.group(1).length());
        String unitName = matcher.group(4);
        unitName = unitName.toLowerCase();

        // Pluralize if necessary
        unitName = pluralize(unitName);

        ChronoUnit unit = parseChronoUnit(unitName);
        
        if (duration == null) {
          duration = Duration.ZERO;
        }
        
        long amount = matcher.group(3) == null ? 1 : Long.parseLong(matcher.group(3));
        duration = duration.plus(amount, unit);
      } else {
        value = "";
      }
    }

    if (duration == null) {
      throw new ParseException("Unable to parse Duration '" + text + "'", 0);
    }

    return duration;
  }
  
  private String pluralize(String noun) {
    if (noun.endsWith("ium")) {
      return noun.replaceAll("ium$", "ia");
    } else if (noun.endsWith("ry")) {
      return noun.replaceAll("ry$", "ries");
    } else if (!noun.endsWith("s")) {
      return noun + "s";
    }
    return noun;
  }
  
  private String singularize(String noun) {
    if (noun.endsWith("ia")) {
      return noun.replaceAll("ia$", "ium");
    } else if (noun.endsWith("ries")) {
      return noun.replaceAll("ries$", "ry");
    } else if (noun.endsWith("s")) {
      return noun.replaceAll("s$", "");
    }
    return noun;
  }
  
  private ChronoUnit parseChronoUnit(String text) throws ParseException {
    ChronoUnit unit;
    if (UNIT_ALIASES.containsKey(text)) {
      unit = UNIT_ALIASES.get(text);
    } else {
      try {
        unit = Enum.valueOf(ChronoUnit.class, text.toUpperCase());
      } catch (IllegalArgumentException ex) {
        throw new ParseException(ex.getMessage(), 0);
      }
    }
    
    return unit;
  }
}
