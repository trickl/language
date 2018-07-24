# Trickl Language

[![build_status](https://travis-ci.com/trickl/language.svg?branch=master)](https://travis-ci.com/trickl/language)
[![Maintainability](https://api.codeclimate.com/v1/badges/be4af1f4cc620e465849/maintainability)](https://codeclimate.com/github/trickl/language/maintainability)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A set of short utility functions for parsing common English language constructs. 

Specifically - reading numbers, currency amounts and durations as English text and converting into a strongly typed class.

## Getting Started

These instructions will get you a copy of the library 

### Prerequisites

Requires Git, Maven 3 and a Java 8 compiler installed on your system.

### Downloding and Building

To download the library into a folder called "language" run

```
git clone https://github.com/trickl/language.git
```

To build the library run

```
mvn clean build
```

## Running the tests

The build will automatically run the tests

## Usage

See the Junit tests for usage

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

## Authors

* **Tim Gee** - *Initial work* - [Trickl](https://github.com/trickl)

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.

## License

This project is licensed under the GNU GPL v3 License - see the [LICENSE](LICENSE) file for details

## Acknowledgments

* The number parsed was built with an inspiration from a blog post https://blog.cordiner.net/2010/01/02/parsing-english-numbers-with-perl/

