package demo;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

// import io.github.bonigarcia.wdm.WebDriverManager;
import demo.wrappers.Wrappers;

public class TestCases {
    ChromeDriver driver;
    Wrappers wrappers;

    /*
     * TODO: Write your tests here with testng @Test annotation.
     * Follow `testCase01` `testCase02`... format or what is provided in
     * instructions
     */

    /*
     * Do not change the provided methods unless necessary, they will help in
     * automation and assessment
     */
    @BeforeTest(alwaysRun = true)
    public void startBrowser() {
        System.setProperty("java.util.logging.config.file", "logging.properties");

        // NOT NEEDED FOR SELENIUM MANAGER
        // WebDriverManager.chromedriver().timeout(30).setup();

        ChromeOptions options = new ChromeOptions();
        LoggingPreferences logs = new LoggingPreferences();

        logs.enable(LogType.BROWSER, Level.ALL);
        logs.enable(LogType.DRIVER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logs);
        options.addArguments("--remote-allow-origins=*");

        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "build/chromedriver.log");

        driver = new ChromeDriver(options);
        wrappers = new Wrappers(driver);

        driver.manage().window().maximize();
    }

    @Test(priority = 0, description = "Go to this website and click on 'Hockey Teams: Forms, Searching and Pagination'")
    public void testCase01() throws InterruptedException, IOException {
        System.out.println("Start Testcase 01");
        wrappers.openURL("https://www.scrapethissite.com/pages/");
        driver.findElement(By.xpath("//a[text()='Hockey Teams: Forms, Searching and Pagination']")).click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table/tbody/tr")));


        List<Map<String, Object>> teamDatas = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            List<WebElement> rows = driver.findElements(By.xpath("//table/tbody/tr[2]"));
            for (WebElement row : rows) {
                System.out.println("Inside the loop");

                String teamName = row.findElement(By.xpath("//td[@class='name']")).getText();
                System.out.println(teamName);
                String year = row.findElement(By.xpath("//td[@class='year']")).getText();
                System.out.println(year);
                double winPercentage = Double.parseDouble(row.findElement(By.xpath("//td[contains(@class, 'pct text')]")).getText());
                System.out.println(winPercentage);
                if (winPercentage < 40.0) {
                    Map<String, Object> teamData = new HashMap<>();
                    teamData.put("Epoch Time", Instant.now().getEpochSecond());
                    teamData.put("teamName", teamName);
                    teamData.put("year", year);
                    teamData.put("WinPercentage", winPercentage);
                    teamDatas.add(teamData);
                }

                WebElement nextPageButton = driver.findElement(By.xpath("//a[@aria-label ='Next']"));
                if (nextPageButton.isEnabled()) {
                    nextPageButton.click();
                } else {
                    break;
                }

            }

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(new File("hockey-team-data.json"), teamDatas);
            System.out.println("Data Saved to hockey-team-data.json");
        }
    }

    @Test(priority = 1, description = "Go to this website and click on 'Oscar Winning Films'")
    public void testCase02() throws InterruptedException, IOException {
        System.out.println("Start Testcase 02");
        wrappers.openURL("https://www.scrapethissite.com/pages/");
        driver.findElement(By.xpath("//a[contains(text(),'Oscar Winning Films')]")).click();

        List<Map<String, Object>> MoviesDataList = new ArrayList<>();

        List<WebElement> chooseYears = driver.findElements(By.className("year-link"));

        for (WebElement yearElement : chooseYears) {
            String year = yearElement.getText();
            yearElement.click();

            List<WebElement> rows = driver.findElements(By.xpath("//table/tbody/tr"));
            for (int i = 0; i < Math.min(5, rows.size()); i++) {
                WebElement row = rows.get(i);
                String title = row.findElement(By.xpath("./td[@class='film-title']")).getText();
                String nominations = row.findElement(By.xpath("./td[@class='film-nominations']")).getText();
                String awards = row.findElement(By.xpath("./td[@class='film-awards']")).getText();
                boolean isWinner = row.findElement(By.xpath("//td/i[contains(@class, 'glyphicon glyphicon-flag')]")) != null;

                Map<String, Object> movieData = new HashMap<>();
                movieData.put("Epoch Time", Instant.now().getEpochSecond());
                movieData.put("Year", year);
                movieData.put("Title", title);
                movieData.put("Nomination", nominations);
                movieData.put("Awards", awards);
                movieData.put("isWinner", isWinner);
                MoviesDataList.add(movieData);
            }
        }
        File outputDir = new File("output");
        if (!outputDir.exists()) {
        outputDir.mkdirs();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(new File("output/oscar-winner-data.json"), MoviesDataList);
        System.out.println("Data Saved to hockey-team-data.json");

            // File jsonFile = new File("output/oscar-winner-data.json");
            // Assert.assertTrue(jsonFile.exists(), "JSON file does not exist");
            // Assert.assertTrue(jsonFile.length() > 0, "JSON file is empty");

        System.out.println("Success");
        
    }

    @AfterTest
    public void endTest() {
        driver.close();
        driver.quit();

    }
}