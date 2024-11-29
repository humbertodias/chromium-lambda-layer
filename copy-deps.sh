#!/bin/bash

# Name of the binary file to analyze
BINARY="$1"

# Check if the binary file exists
if [ ! -f "$BINARY" ]; then
  echo "Error: The file $BINARY was not found."
  exit 1
fi

# Directory where the binary resides
BINARY_DIR=$(dirname "$BINARY")

# Retrieve dependencies using ldd and copy them to the binary's directory
echo "Fetching dependencies for $BINARY..."
ldd "$BINARY" | awk '/=>/ {print $3}' | while read -r lib; do
  if [ -f "$lib" ] || [ -L "$lib" ]; then
    # Copy files and symbolic links while preserving the directory structure
    echo "Copying: $lib"
    cp -L -r "$lib" "$BINARY_DIR"
  fi
done

echo "Dependencies copied to $BINARY_DIR."
patchelf --force-rpath --set-rpath '$ORIGIN' $BINARY