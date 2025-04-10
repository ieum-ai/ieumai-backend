#!/bin/bash

ARTIFACT_DIR="./build/libs"
PORT=8080

echo "=== Starting Deployment Script ==="

set -e

# 0. 기존 서버 종료
EXIST_PID=$(lsof -t -i :$PORT)

if [ -n "$EXIST_PID" ]; then
  echo ">>> Killing existing process on port $PORT (PID: $EXIST_PID)"
  kill -9 $EXIST_PID
  sleep 2
else
  echo ">>> No existing process on port $PORT"
fi

# 1. Git Pull
echo "[1/3] Pulling latest code from Git repository (${BRANCH_NAME} branch)..."
git pull origin ${BRANCH_NAME}

# 2. Gradle Build
echo "[2/3] Building the project using Gradle..."

if [ ! -x ./gradlew ]; then
  echo "Warning: ./gradlew does not have execute permissions. Granting permissions."
  chmod +x ./gradlew
fi

./gradlew --stop
./gradlew build

# 3. 실행할 JAR 찾기
echo "[3/3] Finding and executing the built artifact (JAR file)..."
JAR_FILE=$(ls -t ${ARTIFACT_DIR}/*.jar | grep -v 'plain' | head -n 1)

if [ -z "${JAR_FILE}" ]; then
  echo "Error: Could not find a JAR file in the ${ARTIFACT_DIR} directory."
  exit 1
fi

echo ">>> Executing JAR file: ${JAR_FILE}"

# 4. nohup 으로 백그라운드 실행
rm -f nohup.out
nohup java -jar "${JAR_FILE}" &
sleep 3
echo ">>> Deployment complete!"
