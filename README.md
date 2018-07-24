# Trickl Language

[![build_status](https://travis-ci.com/trickl/language.svg?branch=master)](https://travis-ci.com/trickl/language)
[![Maintainability](https://api.codeclimate.com/v1/badges/be4af1f4cc620e465849/maintainability)](https://codeclimate.com/github/trickl/language/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/be4af1f4cc620e465849/test_coverage)](https://codeclimate.com/github/trickl/language/test_coverage)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A set of short utility functions for parsing common English language constructs. 

Specifically - reading numbers, currency amounts and durations as English text and converting into a strongly typed class.

### Prerequisites

Requires Maven and a Java 8 compiler installed on your system.

## Usage

See the Junit tests for usage

### Installing

To download the library into a folder called "language" run

```
git clone https://github.com/trickl/language.git
```

To build the library run

```
mvn clean build
```

## Examples

### Duration Parsing

* "1 day 3 hours 17 minutes 10 seconds" -> java.time.Duration
* "7 seconds 320 millis" -> java.time.Duration

### Number Parsing

* "two hundred sixty four" -> 264
* "23 million, three hundred and 97" -> 23000397

### Currency Amount Parsing

* "$13 million" -> (USD, 13000000)
* "one hundred british pound sterling" -> (GBP, 100)

## Acknowledgments

* The number parsed was built with an inspiration from a blog post https://blog.cordiner.net/2010/01/02/parsing-english-numbers-with-perl/

