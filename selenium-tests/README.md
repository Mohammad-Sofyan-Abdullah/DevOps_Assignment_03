# Selenium Java Tests

Automated Selenium WebDriver tests for Student & Book Management System.

## Quick Start

### Run Tests Locally
```bash
mvn clean test
```

### Run in Docker
```bash
docker build -t selenium-java-tests .
docker run --rm selenium-java-tests
```

## Test Cases (12 Total)

All tests use headless Chrome and TestNG framework:

1. Page Load Verification
2. Tab Navigation
3. Create Student
4. Read All Students
5. Update Student
6. Delete Student
7. Create Book
8. Read All Books
9. Update Book
10. Book Availability Toggle
11. Delete Book
12. Form Validation

## Technologies

- Java 11
- Maven
- Selenium WebDriver 4.15.0
- TestNG 7.8.0
- WebDriverManager 5.6.2

## View Reports

After running tests, open: `target/surefire-reports/index.html`

See [SETUP_GUIDE.md](SETUP_GUIDE.md) for complete instructions.
