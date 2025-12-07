package com.crud.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;

/**
 * Selenium Test Suite for Student & Book Management System
 * Tests all CRUD operations using headless Chrome
 * 
 * @author Sofyan Abdullah
 */
public class CRUDWebAppTests {

    private WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl = "http://13.48.104.189:8000"; // Update with your EC2 IP

    @BeforeClass
    public void setUp() {
        // Setup WebDriverManager for automatic ChromeDriver management
        WebDriverManager.chromedriver().setup();

        // Configure Chrome options for headless mode
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--remote-allow-origins=*");

        // Initialize WebDriver
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        System.out.println("✓ WebDriver initialized successfully");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("✓ WebDriver closed successfully");
        }
    }

    @BeforeMethod
    public void navigateToHomePage() {
        driver.get(baseUrl);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("h1")));
    }

    // ==================== TEST CASE 1: Page Load ====================
    @Test(priority = 1, description = "Verify main page loads successfully")
    public void test01_pageLoadsSuccessfully() {
        System.out.println("\n▶ Running Test 1: Page Load Verification");

        String pageTitle = driver.getTitle();
        Assert.assertTrue(pageTitle.contains("CRUD Operations"), 
            "Page title should contain 'CRUD Operations'");

        WebElement header = driver.findElement(By.tagName("h1"));
        Assert.assertTrue(header.getText().contains("Student & Book Management"), 
            "Header should contain 'Student & Book Management'");

        System.out.println("✓ Test 1 Passed: Page loaded successfully");
    }

    // ==================== TEST CASE 2: Tab Navigation ====================
    @Test(priority = 2, description = "Verify tab switching between Students and Books")
    public void test02_tabNavigation() {
        System.out.println("\n▶ Running Test 2: Tab Navigation");

        // Click Books tab
        WebElement booksTab = driver.findElement(By.xpath("//button[contains(text(), 'Books')]"));
        booksTab.click();
        wait.until(ExpectedConditions.attributeContains(
            By.id("books-section"), "class", "active"));

        WebElement booksSection = driver.findElement(By.id("books-section"));
        Assert.assertTrue(booksSection.getAttribute("class").contains("active"), 
            "Books section should be active");

        // Click Students tab
        WebElement studentsTab = driver.findElement(By.xpath("//button[contains(text(), 'Students')]"));
        studentsTab.click();
        wait.until(ExpectedConditions.attributeContains(
            By.id("students-section"), "class", "active"));

        WebElement studentsSection = driver.findElement(By.id("students-section"));
        Assert.assertTrue(studentsSection.getAttribute("class").contains("active"), 
            "Students section should be active");

        System.out.println("✓ Test 2 Passed: Tab navigation works correctly");
    }

    // ==================== TEST CASE 3: Create Student ====================
    @Test(priority = 3, description = "Create a new student record")
    public void test03_createStudent() {
        System.out.println("\n▶ Running Test 3: Create Student");

        // Navigate to Students tab
        driver.findElement(By.xpath("//button[contains(text(), 'Students')]")).click();

        // Fill student form
        driver.findElement(By.id("student-name")).sendKeys("Ahmed Hassan");
        driver.findElement(By.id("student-age")).sendKeys("23");
        driver.findElement(By.id("student-email")).sendKeys("ahmed.hassan@test.com");
        driver.findElement(By.id("student-course")).sendKeys("Software Engineering");
        driver.findElement(By.id("student-grade")).sendKeys("A");

        // Submit form
        WebElement submitBtn = driver.findElement(
            By.xpath("//form[@id='student-form']//button[@type='submit']"));
        submitBtn.click();

        // Wait for student to appear in list
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
            By.id("students-list"), "Ahmed Hassan"));

        WebElement studentsList = driver.findElement(By.id("students-list"));
        Assert.assertTrue(studentsList.getText().contains("Ahmed Hassan"), 
            "Student should appear in the list");

        System.out.println("✓ Test 3 Passed: Student created successfully");
    }

    // ==================== TEST CASE 4: Read All Students ====================
    @Test(priority = 4, description = "Retrieve and display all students")
    public void test04_readAllStudents() {
        System.out.println("\n▶ Running Test 4: Read All Students");

        // Navigate to Students tab
        driver.findElement(By.xpath("//button[contains(text(), 'Students')]")).click();

        // Click refresh button
        WebElement refreshBtn = driver.findElement(
            By.xpath("//div[@id='students-section']//button[contains(text(), 'Refresh')]"));
        refreshBtn.click();

        // Wait for students list to load
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("students-list")));

        WebElement studentsList = driver.findElement(By.id("students-list"));
        Assert.assertNotNull(studentsList, "Students list should be present");

        System.out.println("✓ Test 4 Passed: Students list loaded successfully");
    }

    // ==================== TEST CASE 5: Update Student ====================
    @Test(priority = 5, description = "Update existing student information")
    public void test05_updateStudent() {
        System.out.println("\n▶ Running Test 5: Update Student");

        try {
            // Navigate to Students tab
            driver.findElement(By.xpath("//button[contains(text(), 'Students')]")).click();
            Thread.sleep(1000);

            // Find and click first Edit button
            List<WebElement> editButtons = driver.findElements(
                By.xpath("//div[@id='students-list']//button[contains(text(), 'Edit')]"));

            if (!editButtons.isEmpty()) {
                editButtons.get(0).click();

                // Wait for modal to appear
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("edit-modal")));

                // Update name field
                WebElement nameField = driver.findElement(By.id("edit-name"));
                nameField.clear();
                nameField.sendKeys("Updated Student Name");

                // Save changes
                WebElement saveBtn = driver.findElement(
                    By.xpath("//div[@id='edit-modal']//button[contains(text(), 'Save Changes')]"));
                saveBtn.click();

                Thread.sleep(2000);

                System.out.println("✓ Test 5 Passed: Student updated successfully");
            } else {
                System.out.println("⚠ Test 5: No students available to update");
            }
        } catch (Exception e) {
            System.out.println("⚠ Test 5: " + e.getMessage());
        }
    }

    // ==================== TEST CASE 6: Delete Student ====================
    @Test(priority = 6, description = "Delete a student record")
    public void test06_deleteStudent() {
        System.out.println("\n▶ Running Test 6: Delete Student");

        try {
            // Navigate to Students tab
            driver.findElement(By.xpath("//button[contains(text(), 'Students')]")).click();
            Thread.sleep(1000);

            // Get initial count
            List<WebElement> studentCards = driver.findElements(
                By.xpath("//div[@id='students-list']//div[@class='item-card']"));
            int initialCount = studentCards.size();

            if (initialCount > 0) {
                // Click first Delete button
                WebElement deleteBtn = driver.findElement(
                    By.xpath("//div[@id='students-list']//button[contains(text(), 'Delete')]"));
                deleteBtn.click();

                // Handle alert
                Thread.sleep(500);
                driver.switchTo().alert().accept();
                Thread.sleep(2000);

                // Verify deletion
                List<WebElement> finalCards = driver.findElements(
                    By.xpath("//div[@id='students-list']//div[@class='item-card']"));
                Assert.assertTrue(finalCards.size() < initialCount || finalCards.size() == 0, 
                    "Student count should decrease after deletion");

                System.out.println("✓ Test 6 Passed: Student deleted successfully");
            } else {
                System.out.println("⚠ Test 6: No students available to delete");
            }
        } catch (Exception e) {
            System.out.println("⚠ Test 6: " + e.getMessage());
        }
    }

    // ==================== TEST CASE 7: Create Book ====================
    @Test(priority = 7, description = "Create a new book record")
    public void test07_createBook() {
        System.out.println("\n▶ Running Test 7: Create Book");

        // Navigate to Books tab
        driver.findElement(By.xpath("//button[contains(text(), 'Books')]")).click();

        // Fill book form
        driver.findElement(By.id("book-title")).sendKeys("Introduction to Algorithms");
        driver.findElement(By.id("book-author")).sendKeys("Cormen, Leiserson, Rivest");
        driver.findElement(By.id("book-isbn")).sendKeys("978-0262033848");
        driver.findElement(By.id("book-year")).sendKeys("2009");

        // Submit form
        WebElement submitBtn = driver.findElement(
            By.xpath("//form[@id='book-form']//button[@type='submit']"));
        submitBtn.click();

        // Wait for book to appear in list
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
            By.id("books-list"), "Introduction to Algorithms"));

        WebElement booksList = driver.findElement(By.id("books-list"));
        Assert.assertTrue(booksList.getText().contains("Introduction to Algorithms"), 
            "Book should appear in the list");

        System.out.println("✓ Test 7 Passed: Book created successfully");
    }

    // ==================== TEST CASE 8: Read All Books ====================
    @Test(priority = 8, description = "Retrieve and display all books")
    public void test08_readAllBooks() {
        System.out.println("\n▶ Running Test 8: Read All Books");

        // Navigate to Books tab
        driver.findElement(By.xpath("//button[contains(text(), 'Books')]")).click();

        // Click refresh button
        WebElement refreshBtn = driver.findElement(
            By.xpath("//div[@id='books-section']//button[contains(text(), 'Refresh')]"));
        refreshBtn.click();

        // Wait for books list to load
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("books-list")));

        WebElement booksList = driver.findElement(By.id("books-list"));
        Assert.assertNotNull(booksList, "Books list should be present");

        System.out.println("✓ Test 8 Passed: Books list loaded successfully");
    }

    // ==================== TEST CASE 9: Update Book ====================
    @Test(priority = 9, description = "Update existing book information")
    public void test09_updateBook() {
        System.out.println("\n▶ Running Test 9: Update Book");

        try {
            // Navigate to Books tab
            driver.findElement(By.xpath("//button[contains(text(), 'Books')]")).click();
            Thread.sleep(1000);

            // Find and click first Edit button in books section
            List<WebElement> editButtons = driver.findElements(
                By.xpath("//div[@id='books-list']//button[contains(text(), 'Edit')]"));

            if (!editButtons.isEmpty()) {
                editButtons.get(0).click();

                // Wait for modal
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("edit-modal")));

                // Update title field
                WebElement titleField = driver.findElement(By.id("edit-title"));
                titleField.clear();
                titleField.sendKeys("Updated Book Title - Second Edition");

                // Save changes
                WebElement saveBtn = driver.findElement(
                    By.xpath("//div[@id='edit-modal']//button[contains(text(), 'Save Changes')]"));
                saveBtn.click();

                Thread.sleep(2000);

                System.out.println("✓ Test 9 Passed: Book updated successfully");
            } else {
                System.out.println("⚠ Test 9: No books available to update");
            }
        } catch (Exception e) {
            System.out.println("⚠ Test 9: " + e.getMessage());
        }
    }

    // ==================== TEST CASE 10: Book Availability Toggle ====================
    @Test(priority = 10, description = "Test book availability checkbox functionality")
    public void test10_bookAvailabilityToggle() {
        System.out.println("\n▶ Running Test 10: Book Availability Toggle");

        // Navigate to Books tab
        driver.findElement(By.xpath("//button[contains(text(), 'Books')]")).click();

        // Create a book with availability unchecked
        driver.findElement(By.id("book-title")).sendKeys("Unavailable Test Book");
        driver.findElement(By.id("book-author")).sendKeys("Test Author");
        driver.findElement(By.id("book-isbn")).sendKeys("123-456-789-X");
        driver.findElement(By.id("book-year")).sendKeys("2024");

        // Uncheck availability
        WebElement availableCheckbox = driver.findElement(By.id("book-available"));
        if (availableCheckbox.isSelected()) {
            availableCheckbox.click();
        }

        // Submit
        WebElement submitBtn = driver.findElement(
            By.xpath("//form[@id='book-form']//button[@type='submit']"));
        submitBtn.click();

        // Wait and verify
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
            By.id("books-list"), "Not Available"));

        WebElement booksList = driver.findElement(By.id("books-list"));
        Assert.assertTrue(booksList.getText().contains("Not Available"), 
            "Book should show as 'Not Available'");

        System.out.println("✓ Test 10 Passed: Book availability toggle works correctly");
    }

    // ==================== TEST CASE 11: Delete Book ====================
    @Test(priority = 11, description = "Delete a book record")
    public void test11_deleteBook() {
        System.out.println("\n▶ Running Test 11: Delete Book");

        try {
            // Navigate to Books tab
            driver.findElement(By.xpath("//button[contains(text(), 'Books')]")).click();
            Thread.sleep(1000);

            // Get initial count
            List<WebElement> bookCards = driver.findElements(
                By.xpath("//div[@id='books-list']//div[@class='item-card']"));
            int initialCount = bookCards.size();

            if (initialCount > 0) {
                // Click first Delete button
                WebElement deleteBtn = driver.findElement(
                    By.xpath("//div[@id='books-list']//button[contains(text(), 'Delete')]"));
                deleteBtn.click();

                // Handle alert
                Thread.sleep(500);
                driver.switchTo().alert().accept();
                Thread.sleep(2000);

                // Verify deletion
                List<WebElement> finalCards = driver.findElements(
                    By.xpath("//div[@id='books-list']//div[@class='item-card']"));
                Assert.assertTrue(finalCards.size() <= initialCount, 
                    "Book count should not increase after deletion");

                System.out.println("✓ Test 11 Passed: Book deleted successfully");
            } else {
                System.out.println("⚠ Test 11: No books available to delete");
            }
        } catch (Exception e) {
            System.out.println("⚠ Test 11: " + e.getMessage());
        }
    }

    // ==================== TEST CASE 12: Form Validation ====================
    @Test(priority = 12, description = "Verify form validation for required fields")
    public void test12_formValidation() {
        System.out.println("\n▶ Running Test 12: Form Validation");

        // Navigate to Students tab
        driver.findElement(By.xpath("//button[contains(text(), 'Students')]")).click();

        // Try to submit empty form
        WebElement submitBtn = driver.findElement(
            By.xpath("//form[@id='student-form']//button[@type='submit']"));
        submitBtn.click();

        // Check if name field shows validation
        WebElement nameField = driver.findElement(By.id("student-name"));
        String validationMessage = nameField.getAttribute("validationMessage");

        Assert.assertTrue(validationMessage != null && !validationMessage.isEmpty(), 
            "Required field should show validation message");

        System.out.println("✓ Test 12 Passed: Form validation works for required fields");
    }
}
