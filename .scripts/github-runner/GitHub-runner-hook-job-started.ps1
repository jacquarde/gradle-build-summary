
$folder = $Env:GITHUB_WORKSPACE


# Clean workspace
try {
    if (Test-Path -Path $folder ) {
        Remove-Item -Path $folder\* -Force -Recurse -ErrorAction Stop
        Write-Output "Workspace ${folder} cleaned."
    }
}
catch {
    Write-Output "::error::Failed to clean workspace before job."
    Write-Error -Message $_ -ErrorAction Stop
}
