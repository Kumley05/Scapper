package demo.wrappers;

import org.openqa.selenium.chrome.ChromeDriver;

public class Wrappers {
    /*
     * Write your selenium wrappers here
     */
    ChromeDriver driver;
    public Wrappers(ChromeDriver driver) {
        this.driver = driver;
    }

    
    public void openURL(String url)throws InterruptedException{
        System.out.println("Open URL:" +url);
        driver.get(url);
        Thread.sleep(2000);
        System.out.println("Success!");
    }
}
