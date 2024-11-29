package com.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;

public class App implements RequestHandler<Object, String>
{
	/**
	 * AWS Lambda entry point. Send an empty request and it will return the page
	 * title of `https://aws.amazon.com/`.
	 */
	public String handleRequest(Object message, Context context)
	{
        ChromeDriver driver = null;
        try {
            driver = AwsChromium.launch();
			driver.get("https://www.selenium.dev");
			var title = driver.getTitle();
			driver.quit();
			return title;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
	}


	/**
	 * Command line entry point, for manually launching within a local Docker container.
	 */
	@SuppressWarnings("java:S106") /* console output */
	public static void main(String[] args) {
		var app = new App();
		var pageTitle = app.handleRequest(null, null);
		System.out.println("Requested page title is " + pageTitle);
	}
}
