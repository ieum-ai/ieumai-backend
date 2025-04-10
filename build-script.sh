
#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/java-21-amazon-corretto.x86_64
export PATH=$JAVA_HOME/bin:$PATH
export GRADLE_OPTS="-Dorg.gradle.java.home=$JAVA_HOME"

rm -rf /home/ec2-user/ieumai-backend/build/libs/ieumai-backend-0.0.1-SNAPSHOT.jar
rm -rf /home/ec2-user/ieumai-backend/build/libs/ieumai-backend-0.0.1-SNAPSHOT-plain.jar
REPO_URL="https://github.com/ieum-ai/ieumai-backend.git"

BRANCH_NAME="main"

ARTIFACT_DIR="./build/libs"

echo "=== Starting Deployment Script ==="

set -e

echo "[1/3] Pulling latest code from Git repository (${BRANCH_NAME} branch)..."
git pull origin ${BRANCH_NAME}

echo "[2/3] Building the project using Gradle..."

if [ ! -x ./gradlew ]; then
	  echo "Warning: ./gradlew does not have execute permissions. Granting permissions."
	  chmod +x ./gradlew
fi
./gradlew --stop
./gradlew


echo "[3/3] Finding and executing the built artifact (JAR file)..."
JAR_FILE=$(ls -t ${ARTIFACT_DIR}/*.jar | head -n 1)
if [ -z "${JAR_FILE}" ]; then
	echo "Error: Could not find a JAR file in the ${ARTIFACT_DIR} directory."
	  exit 1
fi

echo "Executing file: ${JAR_FILE}"
echo "Starting the Java application..."
# JVM options can be added if necessary (e.g., memory setting -Xmx2g)
# java -jar "${JAR_FILE}"
#
# # This line might not be reached if the application runs continuously in the foreground.
# echo "=== Application Startup Complete ==="
#
# exit 0

java -jar /home/ec2-user/ieumai-backend/build/libs/ieumai-backend-0.0.1-SNAPSHOT.jar
