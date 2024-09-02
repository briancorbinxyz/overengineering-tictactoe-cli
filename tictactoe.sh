#!/bin/bash

#!/bin/bash

VERSION=1.2.1-SNAPSHOT
RELATIVE_FILE_PATH="tictactoe-cli/build/tictactoe-cli-$VERSION-runner"

# Convert the relative path to an absolute path
FILE="$(cd "$(dirname "$RELATIVE_FILE_PATH")" && pwd)/$(basename "$RELATIVE_FILE_PATH")"

# Check if the file exists
if [ -f "$FILE" ]; then
  bash -c "$FILE $@"
else
  echo "$FILE does not exist. Running native build: gradlew build -Dquarkus.native.enabled=true -Dquarkus.package.jar.enabled=false..."
  $(cd ./tictactoe-cli && ./gradlew build -Dquarkus.native.enabled=true -Dquarkus.package.jar.enabled=false)

  # Run the file after building
  if [ -f "$FILE" ]; then
    echo "$FILE exists now. Running it with arguments..."
    bash -c "$FILE $@"
  else
    echo "$FILE still does not exist. Exiting."
    exit 1
  fi
fi