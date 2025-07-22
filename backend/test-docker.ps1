# Docker 실행 기능 테스트 스크립트 (PowerShell 버전)

$BASE_URL = "http://localhost:8081"
$MEMBER_ID = "testuser1"

Write-Host "1. JWT 토큰 생성 중..." -ForegroundColor Yellow
$tokenResponse = Invoke-RestMethod -Uri "$BASE_URL/test/token/$MEMBER_ID" -Method Get
$accessToken = $tokenResponse.data.accessToken

if (-not $accessToken) {
    Write-Host "토큰 생성 실패!" -ForegroundColor Red
    exit 1
}

Write-Host "토큰 생성 성공!" -ForegroundColor Green
Write-Host ""

Write-Host "2. Python 코드 실행 테스트..." -ForegroundColor Yellow
$pythonBody = @{
    containerId = 1
    language = "python"
    code = "print('Hello, World!')`nfor i in range(3):`n    print(f'Count: {i}')"
    input = ""
} | ConvertTo-Json

$headers = @{
    "Authorization" = "Bearer $accessToken"
    "Content-Type" = "application/json"
}

$pythonResult = Invoke-RestMethod -Uri "$BASE_URL/api/docker/execute" -Method Post -Headers $headers -Body $pythonBody
$pythonResult | ConvertTo-Json -Depth 10

Write-Host ""
Write-Host "3. Java 코드 실행 테스트..." -ForegroundColor Yellow
$javaCode = @"
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello from Java!");
        for(int i = 0; i < 3; i++) {
            System.out.println("Count: " + i);
        }
    }
}
"@

$javaBody = @{
    containerId = 1
    language = "java"
    code = $javaCode
    input = ""
} | ConvertTo-Json

$javaResult = Invoke-RestMethod -Uri "$BASE_URL/api/docker/execute" -Method Post -Headers $headers -Body $javaBody
$javaResult | ConvertTo-Json -Depth 10

Write-Host ""
Write-Host "4. 실행 이력 조회..." -ForegroundColor Yellow
$executions = Invoke-RestMethod -Uri "$BASE_URL/api/docker/containers/1/executions" -Method Get -Headers $headers
$executions | ConvertTo-Json -Depth 10

Write-Host ""
Write-Host "5. 통계 조회..." -ForegroundColor Yellow
$statistics = Invoke-RestMethod -Uri "$BASE_URL/api/docker/containers/1/statistics" -Method Get -Headers $headers
$statistics | ConvertTo-Json -Depth 10