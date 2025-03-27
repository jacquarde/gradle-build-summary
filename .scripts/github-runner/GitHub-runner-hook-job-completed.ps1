
$folder = $Env:GITHUB_WORKSPACE


# Add build summary
try {
    if (Test-Path -Path $folder\build\build-summary.md ) {
        Get-Content -Path $folder\build\build-summary.md -ErrorAction Stop >> $Env:GITHUB_STEP_SUMMARY
        Write-Output "Added build summary"
    }
}
catch {
    Write-Output "::warning::Failed to add build summary"
    Write-Warning -Message $_
}


# Clean workspace
try {
    if (Test-Path -Path $folder ) {
        Remove-Item -Path $folder\* -Force -Recurse -ErrorAction Stop
        Write-Output "Workspace ${folder} cleaned."
    }
}
catch {
     Write-Output "::warning::Failed to clean workspace after job"
}
