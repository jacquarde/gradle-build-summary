param ($Property)

$command=".\gradlew properties -q --console=plain"
$pattern="^${Property}: (.+)$"
$property=Invoke-Expression ".\gradlew properties -q --console=plain" | Select-String -Pattern "$pattern"
Write-Host $property.Matches[0].Groups[1]
