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

    private WebDriver driver;
    private WebDriverWait wait;

    // ------------------------------------
    // SETUP: Works for Manual + CI/CD
    // ------------------------------------
    @BeforeClass
    public void setup() {

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--incognito");

        // CI/CD mode auto-detection
        if (System.getenv("GITHUB_ACTIONS") != null) {
            System.out.println("Running in GitHub Actions (Headless Mode Enabled)");

            options.addArguments("--headless=new");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--window-size=1920,1080");
        }

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.manage().window().maximize();

        driver.get("https://www.saucedemo.com/");
    }

    private WebElement find(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    // ------------------------------------
    // TEST 1: VALID CHECKOUT FLOW
    // ------------------------------------
    @Test(priority = 1)
    public void testValidCheckoutFlow() {

        login("standard_user", "secret_sauce");

        wait.until(ExpectedConditions.elementToBeClickable(
                By.id("add-to-cart-sauce-labs-backpack")))
                .click();

        find(By.className("shopping_cart_link")).click();
        find(By.id("checkout")).click();

        // Checkout details
        find(By.id("first-name")).sendKeys("John");
        find(By.id("last-name")).sendKeys("Doe");
        find(By.id("postal-code")).sendKeys("12345");
        find(By.id("continue")).click();

        find(By.id("finish")).click();

        Assert.assertEquals(
                find(By.className("complete-header")).getText(),
                "Thank you for your order!"
        );
    }

    // ------------------------------------
    // TEST 2: INVALID PASSWORD
    // ------------------------------------
    @Test(priority = 2)
    public void testInvalidPassword() {

        driver.get("https://www.saucedemo.com/");

        login("standard_user", "wrong_password");

        WebElement errorMsg = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("[data-test='error']")
                )
        );

        Assert.assertTrue(
                errorMsg.getText().contains("Epic sadface"),
                "Error message not shown for invalid password!"
        );
    }

    // ------------------------------------
    // TEST 3: INVALID USERNAME + PASSWORD
    // ------------------------------------
    @Test(priority = 3)
    public void testInvalidUsername() {

        driver.get("https://www.saucedemo.com/");

        login("wrong_user", "wrong_password");

        WebElement errorMsg = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("[data-test='error']")
                )
        );

        Assert.assertTrue(
                errorMsg.getText().contains("Epic sadface"),
                "Error message not shown for invalid username!"
        );
    }

    // ------------------------------------
    // REUSABLE LOGIN METHOD
    // ------------------------------------
    private void login(String username, String password) {
        find(By.id("user-name")).clear();
        find(By.id("user-name")).sendKeys(username);

        find(By.id("password")).clear();
        find(By.id("password")).sendKeys(password);

        find(By.id("login-button")).click();
    }

    // ------------------------------------
    // CLEANUP
    // ------------------------------------
    @AfterClass(alwaysRun = true)
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
