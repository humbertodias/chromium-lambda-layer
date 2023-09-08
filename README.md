# Chromium AWS Lambda Layer for Java

## How I made this image

1. Grab the latest `*-layer.zip` release from [@Sparticuz/chromium](https://github.com/Sparticuz/chromium). Unpack it, then find the compressed binaries at `nodejs/node_modules/@sparticuz/chromium/bin/`.

2. Copy `chromium.br` to the layer's root folder. Decompress `aws.tar.br` and `swiftshader.tar.br` using [Brotli](https://github.com/google/brotli) and move the library files and `vk_swiftshader_icd` to the layer's `lib/` folder. _(I'm not shipping the compressed libraries because I couldn't figure out how to get Lambda to pick up `/tmp/lib` as a library search path; setting `LD_LIBRARY_PATH` in the Lambda environment didn't work for me. If you can figure out a way all of the binaries could be compressed to reduce the layer size quite a lot.)_

3. Deploy a Linux64 [AWS Lambda container image](https://docs.aws.amazon.com/lambda/latest/dg/images-create.html) to a Docker container. Copy out these files from `/usr/lib64` and place them in the layer's `lib/` folder: `libglib-2.0.so.0`, `libXau.so.6`, `libxcb.so.1`

4. Grab the [Linux64 Chrome Web Driver](https://googlechromelabs.github.io/chrome-for-testing/) corresponding the version of Chromium downloaded above. Use Brotli to compress the `chromedriver` executable; copy `chromedriver.br` to the layer's root folder next to `chromium.br`.

## Using the image

See [AwsChromium.java](./app/src/main/java/com/example/AwsChromium.java) for an example of how to decompress the executables and create a Selenium `ChromeDriver`.

## Testing

I used a Linux64 [AWS Lambda container image](https://docs.aws.amazon.com/lambda/latest/dg/images-create.html) running locally in Docker to puzzle this stuff out; see the included [Dockerfile](./Dockerfile). To use it, place the contents of the layer in a folder called `layer/` at the same level as `Dockerfile`, then create a Docker image.

```
$ gradle build
$ docker build --platform linux/amd64 -t chromium-lambda:test .
```

Start the container.

```
$ docker run -p 9000:8080 chromium-lambda:test
```

Invoke the method via Powershell or Curl.

```
$ Invoke-WebRequest -Uri "http://localhost:9000/2015-03-31/functions/function/invocations" -UseBasicParsing -Body "{}" -Method POST

$ curl "http://localhost:9000/2015-03-31/functions/function/invocations" -d '{}'
```

Or open a terminal on the container and run `App.jar` directly.

```
$ java -jar App.jar
```

I found it helpful to decompress `chromium` and `chromedriver` and try executing them from the terminal to see which library dependencies were missing. Use `yum` to install the required libraries and then copy them out of `/usr/lib64` and add them to the layer.
