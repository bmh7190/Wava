param(
    [string] $AppVersion = "1.0.0",
    [switch] $Clean
)

$ErrorActionPreference = "Stop"
$ProjectRoot = Split-Path -Parent $PSScriptRoot
$BuildDir = Join-Path $ProjectRoot "build"
$ClassesDir = Join-Path $BuildDir "classes"
$JarDir = Join-Path $BuildDir "libs"
$JarPath = Join-Path $JarDir "wava.jar"
$DistDir = Join-Path $ProjectRoot "dist"
$IconPath = Join-Path $ProjectRoot "src\main\resources\wava\assets\wava.ico"
$SourceList = Join-Path $BuildDir "main-sources.txt"
$Modules = "java.desktop,java.management,java.management.rmi,jdk.attach,jdk.jfr,jdk.management,jdk.management.jfr"

function Resolve-JavaTool($toolName) {
    if ($env:JAVA_HOME) {
        $candidate = Join-Path $env:JAVA_HOME "bin\$toolName.exe"
        if (Test-Path $candidate) {
            return $candidate
        }
    }

    $command = Get-Command $toolName -ErrorAction SilentlyContinue
    if ($command) {
        return $command.Source
    }

    throw "$toolName was not found. Run this script with a JDK that includes $toolName."
}

$Javac = Resolve-JavaTool "javac"
$Jar = Resolve-JavaTool "jar"
$Jpackage = Resolve-JavaTool "jpackage"

if ($Clean) {
    Remove-Item -Recurse -Force $BuildDir, $DistDir -ErrorAction SilentlyContinue
}

New-Item -ItemType Directory -Force -Path $ClassesDir, $JarDir, $DistDir | Out-Null
Push-Location $ProjectRoot
try {
    Get-ChildItem "src\main\java" -Recurse -Filter "*.java" |
            Sort-Object FullName |
            ForEach-Object { Resolve-Path -Relative $_.FullName } |
            Set-Content -Encoding ASCII $SourceList

    & $Javac --add-modules "jdk.attach,jdk.jfr" -encoding UTF-8 -d $ClassesDir "@$SourceList"

    if (Test-Path "src\main\resources") {
        Copy-Item -Recurse -Force "src\main\resources\*" $ClassesDir
    }

    & $Jar --create --file $JarPath --main-class Main -C $ClassesDir .

    $PackageArgs = @(
        "--type", "app-image",
        "--name", "Wava",
        "--app-version", $AppVersion,
        "--vendor", "Wava",
        "--dest", $DistDir,
        "--input", $JarDir,
        "--main-jar", "wava.jar",
        "--main-class", "Main",
        "--add-modules", $Modules
    )

    if (Test-Path $IconPath) {
        $PackageArgs += @("--icon", $IconPath)
    }

    & $Jpackage @PackageArgs
    Write-Host "Packaged app image: $(Join-Path $DistDir 'Wava')"
    Write-Host "Executable: $(Join-Path $DistDir 'Wava\Wava.exe')"
} finally {
    Pop-Location
}
