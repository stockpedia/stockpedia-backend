#!/usr/bin/env bash

set -Eeuo pipefail

EC2_USER="ubuntu"
EC2_HOST="52.78.129.16"
PEM_KEY="$HOME/Documents/ktb-key.pem"

REMOTE_NEW_JAR="/home/ubuntu/KTB/init-ktb.jar"
REMOTE_RESTART_SCRIPT="/home/ubuntu/KTB/restart.sh"

echo "1. Spring Boot JAR을 빌드합니다. (테스트 제외)"
./gradlew clean bootJar -x test

JAR_FILE=$(find build/libs -maxdepth 1 -type f -name "*.jar" ! -name "*-plain.jar" | head -n 1)

if [ -z "$JAR_FILE" ]; then
    echo "빌드된 실행 JAR을 찾지 못했습니다."
    exit 1
fi

if [ ! -f "$PEM_KEY" ]; then
    echo "PEM 키를 찾지 못했습니다: $PEM_KEY"
    exit 1
fi

echo "빌드된 JAR: $JAR_FILE"

echo "2. 새 JAR을 EC2에 업로드합니다."
scp -i "$PEM_KEY" "$JAR_FILE" "$EC2_USER@$EC2_HOST:$REMOTE_NEW_JAR"

echo "3. EC2에서 새 JAR로 교체하고 서비스를 재시작합니다."
ssh -i "$PEM_KEY" "$EC2_USER@$EC2_HOST" "sudo $REMOTE_RESTART_SCRIPT"

echo "백엔드 배포가 완료되었습니다."