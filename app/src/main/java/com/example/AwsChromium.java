package com.example;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.compress.compressors.brotli.BrotliCompressorInputStream;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class AwsChromium
{
	private AwsChromium()
	{}


	/**
	 * Decompress a Brotli archive containing a single file.
	 * @param archive The location of the archive.
	 * @param destination Where to place the decompressed file.
	 */
	private static void decompressFile(Path archive, File destination)
	{
		try (var inputStream = new BrotliCompressorInputStream(
			new BufferedInputStream(
				Files.newInputStream(archive))))
		{
			try (var outputStream = new FileOutputStream(destination))
			{
				inputStream.transferTo(outputStream);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}


	public static ChromeDriver launch()
	{
		var chromiumFile = Path.of("/tmp/chromium").toFile();
		if (!chromiumFile.exists())
		{
			decompressFile(Path.of("/opt/chromium.br"), chromiumFile);
			if (!chromiumFile.setExecutable(true))
				throw new IllegalStateException("Unable to mark `chromium` as executable");
		}

		var driverFile = Path.of("/tmp/chromedriver").toFile();
		if (!driverFile.exists())
		{
			decompressFile(Path.of("/opt/chromedriver.br"), driverFile);
			if (!driverFile.setExecutable(true))
				throw new IllegalStateException("Unable to mark `chromedriver` as executable");
		}

		var options = new ChromeOptions()
			.setBinary(chromiumFile)
			.addArguments("--headless=new")
			.addArguments("--no-sandbox")
			.addArguments("--disable-dev-shm-usage")
			.addArguments("--disable-gpu")
			.addArguments("--disable-dev-tools")
			.addArguments("--no-zygote")
			.addArguments("--single-process")
			.addArguments("--window-size=1920,1080")
			.addArguments("--user-data-dir=/tmp/chrome-user-data");

		System.setProperty("webdriver.chrome.driver", driverFile.toString());

		return new ChromeDriver(options);
	}
}
