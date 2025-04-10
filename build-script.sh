#!/bin/bash

ARTIFACT_DIR="./build/libs"
PORT=8080

# 기본 브랜치 설정
if [ -z "$BRANCH_NAME" ]; then
  BRANCH_NAME="main"
fi

echo "=== Starting Deployment Script ==="

set -e

# lsof 명령어가 있는지 확인
if command -v lsof &> /dev/null; then
  echo "lsof command found. Using lsof to check process."
  EXIST_PID=$(lsof -t -i :$PORT || true)
else
  echo "lsof command not found. Using ps to check process."
  EXIST_PID=$(ps aux | grep ":$PORT" | grep -v grep | awk '{print $2}' || true)
fi

# PID가 존재하는 경우 종료
if [ -n "$EXIST_PID" ]; then
  echo ">>> Killing existing process on port $PORT (PID: $EXIST_PID)"
  kill -9 $EXIST_PID
  sleep 2
else
  echo ">>> No existing process on port $PORT"
fi

# 1. Git Pull
echo "[1/3] Pulling latest code from Git repository (${BRANCH_NAME} branch)..."
git pull origin ${BRANCH_NAME} || exit 1

# 2. Gradle Build
echo "[2/3] Building the project using Gradle..."
if [ ! -x ./gradlew ]; then
  echo "Warning: ./gradlew does not have execute permissions. Granting permissions."
  chmod +x ./gradlew
fi
./gradlew --stop
./gradlew build

# 3. JAR 파일 실행
echo "[3/3] Finding and executing the built artifact (JAR file)..."
JAR_FILE=$(ls -t ${ARTIFACT_DIR}/*.jar | grep -v 'plain' | head -n 1)

if [ -z "${JAR_FILE}" ]; then
  echo "Error: No valid JAR file found in ${ARTIFACT_DIR}. Exiting."
  exit 1
fi

echo ">>> Executing JAR file: ${JAR_FILE}"
rm -f nohup.out
nohup java -jar "${JAR_FILE}" > nohup.out 2>&1 &

echo ">>> Deployment complete!"
exit 0
