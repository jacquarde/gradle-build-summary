param ($Property)

$command=".\gradlew properties -q --console=plain"
$pattern="^${Property}: (.+)$"
$result=Invoke-Expression ".\gradlew properties -q --console=plain" | Select-String -Pattern $pattern
$value=$result.Matches[0].Groups[1]
Write-Host "$Property=$value"
