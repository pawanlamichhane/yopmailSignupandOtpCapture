import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;

import com.github.javafaker.Faker;

public class enterpriseSignUp {

    static String serverDomain = "yopmail.com";
    static String password = "Pawandude@1470";

    public static String getRandomEmail() {
        return "user" + System.currentTimeMillis() + "@" + serverDomain;
    }

    static String emailId = getRandomEmail(); // Generate random email ID

    public static void main(String[] args) throws InterruptedException {
        // Set up ChromeDriver with options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        try {
            // Step 1: Register on the website
            registerOnWebsite(driver);

            // Step 2: Retrieve OTP from YopMail
            String otp = getOtpFromYopMail(driver);

            // Step 3: Enter the OTP in the registration form
            if (otp != null) {
                enterOtp(driver, otp, otp);
            } else {
                System.out.println("OTP not found in the email body.");
            }
        } finally {
            // Clean up and close the driver
           // driver.quit();
        }
    }

    private static void registerOnWebsite(WebDriver driver) {
        driver.get("https://www.dev.veelapp.com/");
        driver.manage().window().maximize();
        Faker faker = new Faker();
        String fullname = faker.name().fullName();
        String orgname = faker.company().name();

        driver.findElement(By.xpath(
                "//li[contains(@class,'text-white false text-[28px] cursor-pointer max-md:text-[23px] max-sm:list-none')]"))
                .click();
        driver.findElement(By.xpath("//input[contains(@placeholder,'Enter your full name')]")).sendKeys(fullname);
        driver.findElement(By.xpath("//input[@placeholder='Enter your enterprise’s full name']")).sendKeys(orgname);
        driver.findElement(By.xpath("//input[@placeholder='Enter your work email address']")).sendKeys(emailId);
        driver.findElement(By.xpath("//input[@placeholder='Enter password']")).sendKeys(password);
        driver.findElement(By.xpath("//input[@placeholder='Re-enter your password']")).sendKeys(password);
        driver.findElement(By.xpath("//button[@type='submit']")).click();
    }

    private static String getOtpFromYopMail(WebDriver driver) throws InterruptedException {
        driver.switchTo().newWindow(WindowType.TAB);
        driver.get("https://yopmail.com/en/");
        driver.findElement(By.id("login")).sendKeys(emailId);
        driver.findElement(By.cssSelector("i.material-icons-outlined.f36")).click();

        // Loop until the email is opened
        boolean emailOpened = false;
        while (!emailOpened) {
            try {
                driver.switchTo().frame("ifinbox");
                driver.findElement(By.cssSelector("div.m")).click();
                emailOpened = true;
            } catch (Exception e) {
                driver.switchTo().defaultContent();
                driver.navigate().refresh();
                Thread.sleep(5000); // Wait for the page to refresh
            }
        }

        driver.switchTo().defaultContent();
        driver.switchTo().frame("ifmail");

        String emailBody = driver.findElement(By.id("mail")).getText();
        System.out.println("Email Body: " + emailBody);

        Pattern pattern = Pattern
                .compile("This is your confirmation code:\\s*(\\d)\\s*(\\d)\\s*(\\d)\\s*(\\d)\\s*(\\d)\\s*(\\d)");
        Matcher matcher = pattern.matcher(emailBody);

        if (matcher.find()) {
            return matcher.group(1) + matcher.group(2) + matcher.group(3) + matcher.group(4) + matcher.group(5)
                    + matcher.group(6);
        }

        return null;
    }

    private static void enterOtp(WebDriver driver, String otp, CharSequence fullname) throws InterruptedException {
        driver.switchTo().window(driver.getWindowHandles().toArray()[0].toString());
        WebElement otpInputField = driver.findElement(By.xpath(
                "/html/body/div[2]/div/div[1]/div/div[1]/section/div/div[2]/div/div/div/div/div/div[1]/input[1]"));
        otpInputField.sendKeys(otp);
        driver.findElement(
                By.xpath("/html/body/div[2]/div/div[1]/div/div[1]/section/div/div[2]/div/div/div/div/div/button"))
                .click();
        Thread.sleep(5000);

        driver.findElement(By.xpath("/html/body/div[2]/div/div[1]/div/div[1]/section/div/div[2]/div/div/button")).click();
        driver.findElement(By.xpath("/html/body/div[2]/div/div[1]/div/div[4]/div[3]/div[1]/div[1]/div/div[1]/button/div")).click();
        driver.findElement(By.xpath("/html/body/div[2]/div/div[1]/div/div[2]/div[3]/div/div/form/div[2]/div[1]/div[2]/div/label/div")).click();
        driver.findElement(By.xpath("/html/body/div[2]/div/div[1]/div/div[2]/div[3]/div/div/form/div[2]/div[3]/button")).click();
        driver.findElement(By.xpath("/html/body/div[1]/div/div/div[2]/main/div/form/div[1]/div/div/div[2]/div/div[2]/div/div/div/div/div[1]/div/fieldset/div[1]/div[1]/div/div[1]/span/input")).sendKeys("4242424242424242");
        driver.findElement(By.xpath("/html/body/div[1]/div/div/div[2]/main/div/form/div[1]/div/div/div[2]/div/div[2]/div/div/div/div/div[1]/div/fieldset/div[1]/div[2]/div/div/span/input"))
                .sendKeys("0228");
        driver.findElement(By.xpath("/html/body/div[1]/div/div/div[2]/main/div/form/div[1]/div/div/div[2]/div/div[2]/div/div/div/div/div[1]/div/fieldset/div[1]/div[3]/div/div[1]/span/input"))
                .sendKeys("234");
        driver.findElement(By.xpath("/html/body/div[1]/div/div/div[2]/main/div/form/div[1]/div/div/div[2]/div/div[2]/div/div/div/div/div[2]/div/div/div/div/div/div[1]/div/div[2]/div[1]/div[1]/div/div/span/input"))
                .sendKeys(fullname);
        
        Select select = new Select(driver.findElement(By.xpath("/html/body/div[1]/div/div/div[2]/main/div/form/div[1]/div/div/div[2]/div/div[2]/div/div/div/div/div[2]/div/div/div/div/div/div[2]/div/div[2]/div[1]/div[1]/div/div/div/select"))
                );
        select.selectByVisibleText("United States");
        driver.findElement(By.xpath("/html/body/div[1]/div/div/div[2]/main/div/form/div[1]/div/div/div[2]/div/div[2]/div/div/div/div/div[2]/div/div/div/div/div/div[2]/div/fieldset/div[1]/div[2]/div/div/span/input"))
                .sendKeys("94102");
        Thread.sleep(2000);
        driver.findElement(By.xpath("/html/body/div[1]/div/div/div[2]/main/div/form/div[1]/div/div/div[3]/div/div[2]/button/div[3]")).click();
        driver.switchTo().alert().dismiss();
        driver.findElement(By.xpath("/html/body/div[1]/div/div/div[2]/main/div/form/div[1]/div/div/div[3]/div/div[2]/button/div[3]")).click();
        System.out.println("email:"+emailId);
        System.out.println("password:"+password);
        // Additional steps for uploading intro video if required

        // driver.findElement(By.xpath("/html/body/div[3]/div/div/div[2]/div[2]/div/button")).click();
        // driver.findElement(By.xpath("/html/body/div[3]/div/div/div[2]/div[2]/div/button[1]")).click(); // for skipping intro video upload
    }
}
