package com.example;

import java.io.*;
import java.nio.file.Path;


import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;

public class AwsChromium
{
	private AwsChromium()
	{}

	public static ChromeDriver launch() throws IOException {

		// LD_LIBRARY_PATH
		LibraryPath.updateLdLibraryPath("/tmp", "/tmp/lib64");
		LibraryPath.listFiles("/usr/lib64");
		// LD_LIBRARY_PATH

		var tarGzFilePath = "/tmp/java11-chrome-layer.tar.gz";
		if(!new File("/tmp/lib64").exists()){
			TarGzipHelper.extractTarGz(tarGzFilePath, "/");
		}

		var chromiumFile = Path.of("/tmp/chromium").toFile();
		if (!chromiumFile.setExecutable(true))
			throw new IllegalStateException("Unable to mark `chromium` as executable");

		var driverFile = Path.of("/tmp/chromedriver").toFile();
		if (!driverFile.setExecutable(true))
			throw new IllegalStateException("Unable to mark `chromedriver` as executable");

		var options = new ChromeOptions()
			.setBinary(chromiumFile)
			.addArguments("--headless")
			.addArguments("--no-sandbox")
			.addArguments("--disable-dev-shm-usage")
			.addArguments("--disable-gpu")
			.addArguments("--disable-dev-tools")
			.addArguments("--no-zygote")
			.addArguments("--single-process")
			.addArguments("--window-size=1920,1080")
			.addArguments("--user-data-dir=/tmp/chrome-user-data");

		System.setProperty("webdriver.chrome.driver", driverFile.toString());

		ChromeDriverService service = new ChromeDriverService.Builder()
				.withLogOutput(System.out)
				.withVerbose(true)
				.usingAnyFreePort()
				.usingDriverExecutable(driverFile)
				.build();

		return new ChromeDriver(service, options);
	}



}
