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

        // --- CI/CD Required Flags ---
        options.addArguments("--headless=new");           // IMPORTANT
        options.addArguments("--no-sandbox");             // IMPORTANT
        options.addArguments("--disable-dev-shm-usage");  // IMPORTANT

        // Optional
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--incognito");

        // Disable Chrome password modal
        options.addArguments("--disable-features=PasswordManagerEnabled,AutofillServerCommunication,CredentialManager");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-save-password-bubble");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        driver.get("https://www.saucedemo.com/");
    }

    private WebElement find(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    @Test
    public void testSauceDemoFlow() throws Exception {

        find(By.id("user-name")).sendKeys("standard_user");
        find(By.id("password")).sendKeys("secret_sauce");
        find(By.id("login-button")).click();

        // Add product to cart
        find(By.id("add-to-cart-sauce-labs-backpack")).click();

        // Go to cart
        find(By.className("shopping_cart_link")).click();

        // Checkout
        find(By.id("checkout")).click();

        // Fill form
        find(By.id("first-name")).sendKeys("John");
        find(By.id("last-name")).sendKeys("Doe");
        find(By.id("postal-code")).sendKeys("12345");
        find(By.id("continue")).click();

        find(By.id("finish")).click();

        // Assertion
        Assert.assertEquals(
                find(By.className("complete-header")).getText(),
                "Thank you for your order!"
        );
    }

    @AfterClass
    public void teardown() {
        if (driver != null) driver.quit();
    }
}
