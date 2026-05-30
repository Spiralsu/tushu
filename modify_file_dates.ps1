# Script to modify file timestamps between Dec 2025 - Apr 2026, avoiding holidays

# Set date range
$startDate = [DateTime]::Parse("2025-12-01")
$endDate = [DateTime]::Parse("2026-04-30")

# Define holidays (Chinese statutory holidays for 2025-2026)
$holidays = @(
    "2025-01-01", "2025-01-28", "2025-01-29", "2025-01-30", "2025-01-31", "2025-02-01", "2025-02-02", "2025-02-03", "2025-02-04",
    "2025-04-04", "2025-04-05", "2025-04-06", "2025-05-01", "2025-05-31", "2025-06-01", "2025-06-02", "2025-10-01", "2025-10-02",
    "2025-10-03", "2025-10-04", "2025-10-05", "2025-10-06", "2025-10-07", "2025-10-08",
    "2026-01-01", "2026-01-28", "2026-01-29", "2026-01-30", "2026-01-31", "2026-02-01", "2026-02-02", "2026-02-03", "2026-02-04",
    "2026-04-04", "2026-04-05", "2026-04-06"
)

# Generate valid date (exclude holidays and weekends)
function Get-ValidDate {
    $random = New-Object System.Random
    do {
        $days = $random.Next(0, ($endDate - $startDate).Days + 1)
        $randomDate = $startDate.AddDays($days)
        $randomDate = $randomDate.AddHours($random.Next(8, 22))
        $randomDate = $randomDate.AddMinutes($random.Next(0, 60))
        $randomDate = $randomDate.AddSeconds($random.Next(0, 60))
        $isWeekend = $randomDate.DayOfWeek -eq [DayOfWeek]::Saturday -or $randomDate.DayOfWeek -eq [DayOfWeek]::Sunday
        $isHoliday = $holidays -contains $randomDate.ToString("yyyy-MM-dd")
    } while ($isWeekend -or $isHoliday)
    return $randomDate
}

# Modify file timestamps
function Set-FileDates {
    param([string]$filePath)
    try {
        $file = Get-Item $filePath
        $creationTime = Get-ValidDate
        $writeTime = $creationTime.AddMinutes((New-Object System.Random).Next(5, 120))
        $accessTime = $writeTime.AddMinutes((New-Object System.Random).Next(1, 60))
        $file.CreationTime = $creationTime
        $file.LastWriteTime = $writeTime
        $file.LastAccessTime = $accessTime
        Write-Host "Modified: $filePath"
        return $true
    }
    catch {
        Write-Host "Failed: $filePath - $_" -ForegroundColor Red
        return $false
    }
}

# Main
Write-Host "=== Starting file timestamp modification ===" -ForegroundColor Green
Write-Host "Date range: $startDate to $endDate" -ForegroundColor Cyan
Write-Host ""

$backendPath = "e:\java\project\校园旧书漂流共享系统\代码\book-backend\src\main\java\com\shanzhu\book"
$frontendPath = "e:\java\project\校园旧书漂流共享系统\代码\book-frontend\src"
$cutoffDate = [DateTime]::Parse("2025-12-01")

# Process backend files
Write-Host "=== Processing backend files ===" -ForegroundColor Yellow
$backendFiles = Get-ChildItem -Path $backendPath -Recurse -File
$backendOldFiles = $backendFiles | Where-Object { $_.CreationTime -lt $cutoffDate -or $_.LastWriteTime -lt $cutoffDate }
Write-Host "Backend total files: $($backendFiles.Count)"
Write-Host "Files to modify: $($backendOldFiles.Count)"
Write-Host ""

$backendSuccess = 0
foreach ($file in $backendOldFiles) {
    if (Set-FileDates $file.FullName) {
        $backendSuccess++
    }
}

# Process frontend files
Write-Host ""
Write-Host "=== Processing frontend files ===" -ForegroundColor Yellow
$frontendFiles = Get-ChildItem -Path $frontendPath -Recurse -File
$frontendOldFiles = $frontendFiles | Where-Object { $_.CreationTime -lt $cutoffDate -or $_.LastWriteTime -lt $cutoffDate }
Write-Host "Frontend total files: $($frontendFiles.Count)"
Write-Host "Files to modify: $($frontendOldFiles.Count)"
Write-Host ""

$frontendSuccess = 0
foreach ($file in $frontendOldFiles) {
    if (Set-FileDates $file.FullName) {
        $frontendSuccess++
    }
}

# Summary
Write-Host ""
Write-Host "=== Modification complete ===" -ForegroundColor Green
Write-Host "Backend modified: $backendSuccess / $($backendOldFiles.Count)"
Write-Host "Frontend modified: $frontendSuccess / $($frontendOldFiles.Count)"
Write-Host "Total: $($backendSuccess + $frontendSuccess) files"
