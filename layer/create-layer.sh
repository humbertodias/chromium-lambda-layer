#!/usr/bin/env bash

docker run -it --entrypoint bash --platform linux/amd64 public.ecr.aws/lambda/java:21

dnf -y update && dnf install -y unzip
export CHROME_VERSION=130.0.6723.116

cd /tmp && \
curl -sSL https://storage.googleapis.com/chrome-for-testing-public/${CHROME_VERSION}/linux64/chromedriver-linux64.zip -o chromedriver-linux64.zip && \
unzip chromedriver-linux64.zip && \
curl -sSL https://storage.googleapis.com/chrome-for-testing-public/${CHROME_VERSION}/linux64/chrome-headless-shell-linux64.zip -o chrome-headless-shell-linux64.zip && \
unzip chrome-headless-shell-linux64.zip && \
rm -rf *.zip


dnf -y update && \
    dnf install -y libxcb libXcomposite libXdamage libXrandr libgbm alsa-lib nss atk at-spi2-atk fontconfig && \
    dnf clean all && \

ENV CHROME_DRIVER_PATH=/tmp/chromedriver
ENV CHROME_HEADLESS_PATH=/tmp/chrome-headless-shell-linux64/chrome-headless-shell
