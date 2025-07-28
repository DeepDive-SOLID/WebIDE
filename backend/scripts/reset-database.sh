#!/bin/bash
# 데이터베이스 리셋 스크립트

echo "⚠️  경고: 이 스크립트는 webide 데이터베이스의 모든 데이터를 삭제합니다!"
read -p "계속하시겠습니까? (y/N): " confirm

if [ "$confirm" != "y" ]; then
    echo "취소되었습니다."
    exit 1
fi

# MySQL 접속 정보
read -p "MySQL 사용자명 (기본값: root): " MYSQL_USER
MYSQL_USER=${MYSQL_USER:-root}

read -sp "MySQL 비밀번호: " MYSQL_PASS
echo

# 스크립트 경로
SCHEMA_DIR="../src/main/resources/schema"

echo "데이터베이스 삭제 중..."
mysql -u $MYSQL_USER -p$MYSQL_PASS -e "DROP DATABASE IF EXISTS webide;"

echo "데이터베이스 생성 중..."
mysql -u $MYSQL_USER -p$MYSQL_PASS -e "CREATE DATABASE webide CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

echo "스키마 적용 중..."
mysql -u $MYSQL_USER -p$MYSQL_PASS webide < $SCHEMA_DIR/WebIDE.sql

echo "테스트 데이터 추가 중..."
mysql -u $MYSQL_USER -p$MYSQL_PASS webide < $SCHEMA_DIR/test-data.sql

echo "✅ 데이터베이스가 성공적으로 재생성되었습니다!"
echo "테이블 목록:"
mysql -u $MYSQL_USER -p$MYSQL_PASS webide -e "SHOW TABLES;"