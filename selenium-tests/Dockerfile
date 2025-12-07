# Dockerfile for Java Selenium Tests with Chrome
FROM markhobson/maven-chrome:jdk-11

# Set working directory
WORKDIR /app

# Copy Maven project files
COPY pom.xml .
COPY testng.xml .

# Download dependencies
RUN mvn dependency:go-offline

# Copy test source code
COPY src ./src

# Set environment variables for headless Chrome
ENV CHROME_BIN=/usr/bin/google-chrome
ENV CHROMEDRIVER_PATH=/usr/bin/chromedriver

# Run tests
CMD ["mvn", "clean", "test"]
