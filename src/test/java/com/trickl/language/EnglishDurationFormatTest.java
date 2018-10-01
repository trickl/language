package com.trickl.language;

import java.text.ParseException;
import java.time.Duration;
import org.junit.Assert;
import org.junit.Test;

/** @author tgee */
public class EnglishDurationFormatTest {

  @Test
  public void testParse() throws ParseException {
    Assert.assertEquals(Duration.ofHours(1), new EnglishDurationFormat().parse("hour"));
    Assert.assertEquals(Duration.ofHours(1), new EnglishDurationFormat().parse("1 hour"));
    Assert.assertEquals(Duration.ofHours(2), new EnglishDurationFormat().parse("2 hours"));
    Assert.assertEquals(
        Duration.ofHours(1).plus(Duration.ofMinutes(17)),
        new EnglishDurationFormat().parse("1 hour 17 minutes"));
    Assert.assertEquals(
        Duration.ofHours(1).plus(Duration.ofMinutes(1)),
        new EnglishDurationFormat().parse("1 hour 1 minute"));
    Assert.assertEquals(
        Duration.ofDays(1)
            .plus(Duration.ofHours(3))
            .plus(Duration.ofMinutes(17))
            .plus(Duration.ofSeconds(10)),
        new EnglishDurationFormat().parse("1 day 3 hours 17 minutes 10 seconds"));
    Assert.assertEquals(
        Duration.ofSeconds(7).plus(Duration.ofMillis(320)),
        new EnglishDurationFormat().parse("7 seconds 320 millis"));

    // Aliases
    Assert.assertEquals(Duration.ofHours(1), new EnglishDurationFormat().parse("1 hr"));
    Assert.assertEquals(Duration.ofHours(2), new EnglishDurationFormat().parse("2 hrs"));
    Assert.assertEquals(
        Duration.ofHours(1).plus(Duration.ofMinutes(17)),
        new EnglishDurationFormat().parse("1 hr 17 mins"));
    Assert.assertEquals(
        Duration.ofHours(1).plus(Duration.ofMinutes(1)),
        new EnglishDurationFormat().parse("1 hr 1 min"));
    Assert.assertEquals(
        Duration.ofDays(1)
            .plus(Duration.ofHours(3))
            .plus(Duration.ofMinutes(17))
            .plus(Duration.ofSeconds(10)),
        new EnglishDurationFormat().parse("1 day 3 hrs 17 mins 10s"));
    Assert.assertEquals(
        Duration.ofSeconds(7).plus(Duration.ofMillis(320)),
        new EnglishDurationFormat().parse("7 s 320 ms"));

    // Spaces
    Assert.assertEquals(
        Duration.ofDays(1)
            .plus(Duration.ofHours(3))
            .plus(Duration.ofMinutes(17))
            .plus(Duration.ofSeconds(10)),
        new EnglishDurationFormat().parse("1 day 3 hrs 17 mins 10s"));
    Assert.assertEquals(
        Duration.ofSeconds(7).plus(Duration.ofMillis(320)),
        new EnglishDurationFormat().parse("7s 320ms"));
    
    // Trailing text
    Assert.assertEquals(
        Duration.ofMinutes(85),
        new EnglishDurationFormat().parse("85 mins."));
    
    // Larger durations
    Assert.assertEquals(Duration.ofDays(1), new EnglishDurationFormat().parse("day"));
    Assert.assertEquals(Duration.ofDays(1), new EnglishDurationFormat().parse("1 day"));
  }

  @Test
  public void testFormat() {
    Assert.assertEquals("1 hour", new EnglishDurationFormat().format(Duration.ofHours(1)));
    Assert.assertEquals("2 hours", new EnglishDurationFormat().format(Duration.ofHours(2)));
    Assert.assertEquals(
        "1 hour 17 minutes",
        new EnglishDurationFormat().format(Duration.ofHours(1).plus(Duration.ofMinutes(17))));
    Assert.assertEquals(
        "1 hour 1 minute",
        new EnglishDurationFormat().format(Duration.ofHours(1).plus(Duration.ofMinutes(1))));
    Assert.assertEquals(
        "1 day 3 hours 17 minutes 10 seconds",
        new EnglishDurationFormat()
            .format(
                Duration.ofDays(1)
                    .plus(Duration.ofHours(3))
                    .plus(Duration.ofMinutes(17))
                    .plus(Duration.ofSeconds(10))));
    Assert.assertEquals(
        "7 seconds 320 millis",
        new EnglishDurationFormat().format(Duration.ofSeconds(7).plus(Duration.ofMillis(320))));
  }
}
