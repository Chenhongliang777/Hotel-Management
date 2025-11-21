try {
    $login = Invoke-RestMethod -Method Post -Uri 'http://localhost:8080/auth/login' -Body (ConvertTo-Json @{ username='admin'; password='admin123' }) -ContentType 'application/json' -ErrorAction Stop
    $token = $login.token
    Write-Output 'Got token:'
    Write-Output $token
    $body = @{ username='admin2'; password='admin123'; fullName='系统管理员2'; email='admin2@hotel.com'; phone='13800138001'; role='ADMIN' }
    $resp = Invoke-RestMethod -Method Post -Uri 'http://localhost:8080/users' -Headers @{ Authorization = "Bearer $token" } -Body (ConvertTo-Json $body) -ContentType 'application/json' -ErrorAction Stop
    Write-Output 'Create user response:'
    Write-Output (ConvertTo-Json $resp -Depth 6)
} catch {
    Write-Output 'ERROR:'
    Write-Output $_.Exception.Message
    if ($_.Exception.Response) {
        try {
            $stream = $_.Exception.Response.GetResponseStream()
            $reader = New-Object System.IO.StreamReader($stream)
            $text = $reader.ReadToEnd()
            Write-Output 'Response body:'
            Write-Output $text
        } catch {}
    }
    exit 1
}
