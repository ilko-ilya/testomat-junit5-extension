# Testomat.io JUnit 5 Extension

This is a custom JUnit 5 extension developed for seamless integration with the Testomat.io Test Management System. The library automatically reports test execution results, including status, execution time, and stack traces for failed tests.

## Features
- **Zero Configuration**: Automatically registered via Java Service Loader.
- **Modern Java**: Built using Java 21 features like `Records` and the new `HttpClient`.
- **Automatic Reporting**: Creates a new Test Run on startup and closes it upon completion.
- **Rich Metadata**: Supports custom `@TestId` and `@Title` annotations for detailed reports.

## Prerequisites
- **Java 21** or higher
- **Maven 3.9+**
- A Testomat.io account and API Key

## Configuration
The extension requires an API key to communicate with Testomat.io. Set the following environment variable:

```bash
TESTOMATIO=your_api_key_here