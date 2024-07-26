import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import com.github.javafaker.Faker;
import com.mailosaur.MailosaurClient;
import com.mailosaur.MailosaurException;
import com.mailosaur.models.Message;
import com.mailosaur.models.MessageSearchParams;
import com.mailosaur.models.SearchCriteria;

public class SignUp {
    String apiKey = "Y5fb1shbbMMT52QhQ3KJ0hsBXcLjxJeK";
    String serverId = "yn2qif6u";
    String serverDomain = "yn2qif6u.mailosaur.net";
    String from = "no-reply@veelapp.com";

    public String getRandomEmail() {
        return "user" + System.currentTimeMillis() + "@" + serverDomain;
    }

    public Message waitForEmail(String emailId, MailosaurClient mailosaur) throws MailosaurException {
        Wait<MailosaurClient> wait = new FluentWait<>(mailosaur)
                .withTimeout(Duration.ofSeconds(30)) // Maximum wait 30 seconds
                .pollingEvery(Duration.ofMillis(500)) // Check every 500 milliseconds
                .ignoring(Exception.class); // Ignore Mailosaur exceptions

        return wait.until(mailosaurClient -> {
            try {
                // Search for email
                MessageSearchParams params = new MessageSearchParams();
                params.withServer(serverId);
                SearchCriteria criteria = new SearchCriteria();
                criteria.withSentTo(emailId);
                Message message = mailosaurClient.messages().get(params, criteria);
                return message;
            } catch (MailosaurException | IOException e) {
                // Return null if email not found
                return null;
            }
        });
    }

    @Test
    public void testMailExample() throws IOException, MailosaurException, InterruptedException {
        String emailId = getRandomEmail(); // Set the correct path to chromedriver
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.dev.veelapp.com/");
		driver.manage().window().maximize();
		Faker fake = new Faker();
		String fullname = fake.name().fullName();
		@SuppressWarnings({ })
		String orgName = fake.name().name();
		driver.findElement(By.xpath("//li[contains(@class,'text-white false text-[28px] cursor-pointer max-md:text-[23px] max-sm:list-none')]")).click();
		driver.findElement(By.xpath("//input[@placeholder='Enter your full name']")).sendKeys(fullname);
		driver.findElement(By.xpath("//input[@placeholder='Enter your enterprise’s full name']")).sendKeys(orgName);
		driver.findElement(By.xpath("//input[@placeholder='Enter your work email address']")).sendKeys(emailId);
		Thread.sleep(2000);
		driver.findElement(By.xpath("//input[@placeholder='Enter password']")).sendKeys("Pawandude@1470");
		driver.findElement(By.xpath("//input[contains(@placeholder,'Re-enter your password')]")).sendKeys("Pawandude@1470");
		driver.findElement(By.xpath("//button[@type='submit']")).click();
        //driver.get("https://groww.in/login");
        //driver.findElement(By.id("login_email1")).sendKeys(emailId);
        //driver.findElement(By.xpath("//span[text()='Continue']")).click();

        MailosaurClient mailosaur = new MailosaurClient(apiKey);
        Message message = waitForEmail(emailId, mailosaur);

        assertNotNull("Email not received", message);
        String subject = message.subject();
        System.out.println(subject);
        String body = message.text().body();
        System.out.println(body); // "Your access code is 243546."
        boolean containsText = body.contains("This is your confirmation code:");
        System.out.println(containsText); // true

       // System.out.println("get otp--");

        Pattern pattern = Pattern.compile("This is your confirmation code: *^\\d{6}$\r\n"
        		+ "*");
        Matcher matcher = pattern.matcher(subject);
        assertNotNull("OTP not found in email", matcher.find());
        String otp = matcher.group(1);
        System.out.println(otp);

        driver.findElement(By.xpath("/html/body/div[2]/div/div[1]/div/div[1]/section/div/div[2]/div/div/div/div/div/div[1]/input[1]")).sendKeys(otp);
		driver.findElement(By.xpath("/html/body/div[2]/div/div[1]/div/div[1]/section/div/div[2]/div/div/div/div/div/button")).click();
		driver.quit();
    }
}
