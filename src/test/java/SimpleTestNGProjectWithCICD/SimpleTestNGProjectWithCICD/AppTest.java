package SimpleTestNGProjectWithCICD.SimpleTestNGProjectWithCICD;

import java.time.Duration;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

import io.github.bonigarcia.wdm.WebDriverManager;

public class AppTest {

    WebDriver driver;
    WebDriverWait wait;

    @BeforeClass
    public void setup() throws Exception {

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();

        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--incognito");

        // 🔥 PERFECT FIX to block Chrome password popups
        options.addArguments("--disable-features=PasswordManagerEnabled,AutofillServerCommunication,CredentialManager");

        // Important for GitHub Actions Linux
        options.addArguments("--headless=new");
       options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-web-security");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-site-isolation-trials");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        driver.manage().window().maximize();
        driver.get("https://www.saucedemo.com/");

        Thread.sleep(1500);
    }

    private WebElement find(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    // =====================================================
    // 🟢 TEST 1 — VALID LOGIN + COMPLETE CHECKOUT FLOW
    // =====================================================
    @Test(priority = 1)
    public void testSauceDemoFlow() throws Exception {

        // Login
        find(By.id("user-name")).sendKeys("standard_user");
        find(By.id("password")).sendKeys("secret_sauce");
        find(By.id("login-button")).click();
        Thread.sleep(1500);

        // Add product
        WebElement add = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("add-to-cart-sauce-labs-backpack"))
        );
        add.click();
        Thread.sleep(1000);

        // Go to cart
        find(By.className("shopping_cart_link")).click();
        Thread.sleep(1000);

        // Checkout
        find(By.id("checkout")).click();
        Thread.sleep(1000);

        // Fill form
        find(By.id("first-name")).sendKeys("John");
        find(By.id("last-name")).sendKeys("Doe");
        find(By.id("postal-code")).sendKeys("12345");
        find(By.id("continue")).click();
        Thread.sleep(1000);

        // Finish order
        find(By.id("finish")).click();
        Thread.sleep(1000);

        // Assertion
        Assert.assertEquals(
                find(By.className("complete-header")).getText(),
                "Thank you for your order!",
                "Order success message not found"
        );
    }

    // =====================================================
    // 🔴 TEST 2 — INVALID LOGIN VALIDATION
    // =====================================================
    @Test(priority = 2)
    public void InvalidLogin() throws Exception {

        // Logout (if needed)
        driver.get("https://www.saucedemo.com/");
        Thread.sleep(1000);

        // WRONG LOGIN
        find(By.id("user-name")).sendKeys("wrong_user");
        find(By.id("password")).sendKeys("wrong_password");
        find(By.id("login-button")).click();

        // Wait for error message
        WebElement errorMsg = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test='error']"))
        );

        Assert.assertTrue(
                errorMsg.isDisplayed(),
                "Error message did not appear!"
        );

        Assert.assertTrue(
                errorMsg.getText().contains("Username and password do not match"),
                "Incorrect error message!"
        );
    }

    @AfterClass
    public void teardown() throws Exception {
        Thread.sleep(1000);
        if (driver != null) driver.quit();
    }
}
