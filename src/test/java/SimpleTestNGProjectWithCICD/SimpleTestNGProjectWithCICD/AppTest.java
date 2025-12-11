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

    //This is testing project only.
    @BeforeClass
    public void setup() throws Exception {

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--incognito");

        if (System.getenv("GITHUB_ACTIONS") != null) {
            System.out.println("Headless mode enabled (GitHub Actions)");
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
        }

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        driver.manage().window().maximize();
        driver.get("https://www.saucedemo.com/");
    }

    private WebElement find(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    // -------------------------
    // TEST 1: VALID CHECKOUT
    // -------------------------
    @Test(priority = 1)
    public void testValidCheckoutFlow() throws Exception {

        find(By.id("user-name")).sendKeys("standard_user");
        find(By.id("password")).sendKeys("secret_sauce");
        find(By.id("login-button")).click();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.id("add-to-cart-sauce-labs-backpack"))).click();

        find(By.className("shopping_cart_link")).click();
        find(By.id("checkout")).click();

        find(By.id("first-name")).sendKeys("John");
        find(By.id("last-name")).sendKeys("Doe");
        find(By.id("postal-code")).sendKeys("12345");
        find(By.id("continue")).click();

        find(By.id("finish")).click();

        Assert.assertEquals(find(By.className("complete-header")).getText(),
                "Thank you for your order!");
    }

    // -------------------------
    // TEST 2: INVALID PASSWORD
    // -------------------------
    @Test(priority = 2)
    public void testInvalidPassword() throws Exception {

        driver.get("https://www.saucedemo.com/");

        find(By.id("user-name")).sendKeys("standard_user");
        find(By.id("password")).sendKeys("wrong_password");
        find(By.id("login-button")).click();

        WebElement errorMsg = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("[data-test='error']")
                )
        );

        Assert.assertTrue(errorMsg.getText().contains("Epic sadface"),
                "Error message not shown for invalid password!");
    }

    // -------------------------
    // TEST 3: INVALID USERNAME + PASSWORD
    // -------------------------
    @Test(priority = 3)
    public void testInvalidUsername() throws Exception {

        driver.get("https://www.saucedemo.com/");

        find(By.id("user-name")).sendKeys("wrong_user");
        find(By.id("password")).sendKeys("wrong_password");
        Thread.sleep(5000);
        find(By.id("login-button")).click();

        WebElement errorMsg = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("[data-test='error']")
                )
        );

        Assert.assertTrue(errorMsg.getText().contains("Epic sadface"),
                "Error message not shown for invalid username!");
    }

    @AfterClass
    public void teardown() throws Exception {
        if (driver != null) driver.quit();
    }
}
