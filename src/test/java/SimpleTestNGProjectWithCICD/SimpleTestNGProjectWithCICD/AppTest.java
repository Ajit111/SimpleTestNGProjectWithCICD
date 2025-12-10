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

        // 🔥 THE REAL FIX (kills Chrome password modal completely)
        options.addArguments("--disable-features=PasswordManagerEnabled,AutofillServerCommunication,CredentialManager");

        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-web-security");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-site-isolation-trials");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        driver.manage().window().maximize();
        driver.get("https://www.saucedemo.com/");
        Thread.sleep(2000);
    }

    private WebElement find(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    @Test
    public void testSauceDemoFlow() throws Exception {

        Thread.sleep(2000);

        // Login
        find(By.id("user-name")).sendKeys("standard_user");
        Thread.sleep(1000);

        find(By.id("password")).sendKeys("secret_sauce");
        Thread.sleep(1000);

        find(By.id("login-button")).click();
        Thread.sleep(2000);

        // 🌟 NOW popup will NEVER appear — safe to continue

        // Add product to cart
        WebElement add = wait.until(ExpectedConditions.elementToBeClickable(
                By.id("add-to-cart-sauce-labs-backpack")));
        add.click();
        Thread.sleep(2000);

        // Go to cart
        WebElement cart = wait.until(ExpectedConditions.elementToBeClickable(
                By.className("shopping_cart_link")));
        cart.click();
        Thread.sleep(2000);

        // Checkout
        WebElement checkoutBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.id("checkout")));
        checkoutBtn.click();
        Thread.sleep(2000);

        // Fill form
        find(By.id("first-name")).sendKeys("John");
        Thread.sleep(1000);

        find(By.id("last-name")).sendKeys("Doe");
        Thread.sleep(1000);

        find(By.id("postal-code")).sendKeys("12345");
        Thread.sleep(1000);

        find(By.id("continue")).click();
        Thread.sleep(1000);

        find(By.id("finish")).click();
        Thread.sleep(1500);

        // Assertion
        Assert.assertEquals(
                find(By.className("complete-header")).getText(),
                "Thank you for your order!"
        );

        Thread.sleep(2000);
    }

    @AfterClass
    public void teardown() throws Exception {
        Thread.sleep(2000);
        if (driver != null) driver.quit();
    }
}
