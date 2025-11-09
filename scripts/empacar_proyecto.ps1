param(
  [string]$Destino = "build/entrega_proyecto.zip"
)

$ErrorActionPreference = 'Stop'

$root = Split-Path -Path $PSScriptRoot -Parent
Set-Location $root

$destPath = Join-Path $root $Destino
$destDir = Split-Path -Path $destPath -Parent
if (-not (Test-Path $destDir)) {
  New-Item -ItemType Directory -Force -Path $destDir | Out-Null
}

# Recolectar archivos excluyendo directorios de trabajo
$items = Get-ChildItem -Path $root -Recurse -File |
  Where-Object {
    $_.FullName -notmatch "\\\\.git\\" -and \
    $_.FullName -notmatch "\\\\build\\" -and \
    $_.FullName -notmatch "\\\\out\\" -and \
    $_.FullName -notmatch "\\\\.idea\\" -and \
    $_.FullName -notmatch "\\\\salida\\resumen.zip$"
  }

if (Test-Path $destPath) { Remove-Item $destPath -Force }

Compress-Archive -Path ($items | ForEach-Object { $_.FullName }) -DestinationPath $destPath -Force

Write-Host "ZIP generado en: $destPath"
