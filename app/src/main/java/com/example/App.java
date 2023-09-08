package com.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class App implements RequestHandler<Object, String>
{
	/**
	 * AWS Lambda entry point. Send an empty request and it will return the page
	 * title of `https://aws.amazon.com/`.
	 */
	public String handleRequest(Object message, Context context)
	{
		var driver = AwsChromium.launch();
		driver.get("https://aws.amazon.com");
		var title = driver.getTitle();
		driver.quit();
		return title;
	}


	/**
	 * Command line entry point, for manually launching within a local Docker container.
	 */
	@SuppressWarnings("java:S106") /* console output */
	public static void main(String[] args)
	{
		var app = new App();
		var pageTitle = app.handleRequest(null, null);
		System.out.println("Requested page title is " + pageTitle);
	}
}
