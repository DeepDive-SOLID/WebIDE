#!/bin/bash

# Docker 실행 기능 테스트 스크립트 (jq 없는 버전)

BASE_URL="http://172.20.208.1:8081"
MEMBER_ID="testuser1"

echo "1. JWT 토큰 생성 중..."
TOKEN_RESPONSE=$(curl -s "$BASE_URL/test/token/$MEMBER_ID")
echo "응답: $TOKEN_RESPONSE"

# 토큰을 수동으로 추출 (jq 대신)
ACCESS_TOKEN=$(echo $TOKEN_RESPONSE | grep -o '"accessToken":"[^"]*' | sed 's/"accessToken":"//')

if [ -z "$ACCESS_TOKEN" ]; then
    echo "토큰 생성 실패!"
    exit 1
fi

echo "토큰 생성 성공!"
echo "토큰: $ACCESS_TOKEN"
echo ""

echo "2. Python 코드 실행 테스트..."
curl -X POST "$BASE_URL/api/docker/execute" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "containerId": 1,
    "language": "python",
    "code": "print(\"Hello, World!\")\nfor i in range(3):\n    print(f\"Count: {i}\")",
    "input": ""
  }'

echo ""
echo ""
echo "3. Java 코드 실행 테스트..."
curl -X POST "$BASE_URL/api/docker/execute" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "containerId": 1,
    "language": "java",
    "code": "public class Main {\n    public static void main(String[] args) {\n        System.out.println(\"Hello from Java!\");\n        for(int i = 0; i < 3; i++) {\n            System.out.println(\"Count: \" + i);\n        }\n    }\n}",
    "input": ""
  }'

echo ""
echo ""
echo "4. 실행 이력 조회..."
curl -s "$BASE_URL/api/docker/containers/1/executions" \
  -H "Authorization: Bearer $ACCESS_TOKEN"

echo ""
echo ""
echo "5. 통계 조회..."
curl -s "$BASE_URL/api/docker/containers/1/statistics" \
  -H "Authorization: Bearer $ACCESS_TOKEN"