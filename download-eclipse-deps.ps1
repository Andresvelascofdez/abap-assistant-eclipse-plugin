Write-Host "===================================================" -ForegroundColor Green
Write-Host "ABAP Assistant - Eclipse Dependencies Downloader" -ForegroundColor Green  
Write-Host "===================================================" -ForegroundColor Green
Write-Host ""

# Crear directorio lib si no existe
if (!(Test-Path "lib")) { New-Item -ItemType Directory -Name "lib" }

# URLs de dependencias de Eclipse
$dependencies = @{
    "eclipse-core-runtime-3.29.0.jar" = "https://repo1.maven.org/maven2/org/eclipse/platform/org.eclipse.core.runtime/3.29.0/org.eclipse.core.runtime-3.29.0.jar"
    "eclipse-core-resources-3.19.0.jar" = "https://repo1.maven.org/maven2/org/eclipse/platform/org.eclipse.core.resources/3.19.0/org.eclipse.core.resources-3.19.0.jar"  
    "eclipse-core-jobs-3.15.0.jar" = "https://repo1.maven.org/maven2/org/eclipse/platform/org.eclipse.core.jobs/3.15.0/org.eclipse.core.jobs-3.15.0.jar"
    "eclipse-text-3.12.300.jar" = "https://repo1.maven.org/maven2/org/eclipse/platform/org.eclipse.text/3.12.300/org.eclipse.text-3.12.300.jar"
    "eclipse-jface-text-3.24.0.jar" = "https://repo1.maven.org/maven2/org/eclipse/platform/org.eclipse.jface.text/3.24.0/org.eclipse.jface.text-3.24.0.jar"
    "eclipse-ui-3.201.0.jar" = "https://repo1.maven.org/maven2/org/eclipse/platform/org.eclipse.ui/3.201.0/org.eclipse.ui-3.201.0.jar"
    "eclipse-ui-workbench-3.130.0.jar" = "https://repo1.maven.org/maven2/org/eclipse/platform/org.eclipse.ui.workbench/3.130.0/org.eclipse.ui.workbench-3.130.0.jar"
    "eclipse-ui-workbench-texteditor-3.17.0.jar" = "https://repo1.maven.org/maven2/org/eclipse/platform/org.eclipse.ui.workbench.texteditor/3.17.0/org.eclipse.ui.workbench.texteditor-3.17.0.jar"
    "eclipse-jface-3.32.0.jar" = "https://repo1.maven.org/maven2/org/eclipse/platform/org.eclipse.jface/3.32.0/org.eclipse.jface-3.32.0.jar"
    "eclipse-swt-3.124.0.jar" = "https://repo1.maven.org/maven2/org/eclipse/platform/org.eclipse.swt/3.124.0/org.eclipse.swt-3.124.0.jar"
    "eclipse-swt-win32-x64-3.124.0.jar" = "https://repo1.maven.org/maven2/org/eclipse/platform/org.eclipse.swt.win32.win32.x86_64/3.124.0/org.eclipse.swt.win32.win32.x86_64-3.124.0.jar"
    "eclipse-core-commands-3.11.0.jar" = "https://repo1.maven.org/maven2/org/eclipse/platform/org.eclipse.core.commands/3.11.0/org.eclipse.core.commands-3.11.0.jar"
    "eclipse-equinox-common-3.18.0.jar" = "https://repo1.maven.org/maven2/org/eclipse/platform/org.eclipse.equinox.common/3.18.0/org.eclipse.equinox.common-3.18.0.jar"
    "eclipse-equinox-registry-3.11.200.jar" = "https://repo1.maven.org/maven2/org/eclipse/platform/org.eclipse.equinox.registry/3.11.200/org.eclipse.equinox.registry-3.11.200.jar"
    "osgi-framework-3.18.500.jar" = "https://repo1.maven.org/maven2/org/eclipse/platform/org.eclipse.osgi/3.18.500/org.eclipse.osgi-3.18.500.jar"
}

$count = 0
$success = 0

Write-Host "Downloading Eclipse Platform Dependencies..." -ForegroundColor Cyan
Write-Host ""

foreach ($file in $dependencies.Keys) {
    $count++
    Write-Host "[$count/$($dependencies.Count)] Downloading $file..." -ForegroundColor Yellow
    
    try {
        $url = $dependencies[$file]
        $output = "lib\$file"
        
        # Skip if already exists
        if (Test-Path $output) {
            Write-Host "  SKIPPED: $file (already exists)" -ForegroundColor Gray
            $success++
            continue
        }
        
        Invoke-WebRequest -Uri $url -OutFile $output -UseBasicParsing
        Write-Host "  SUCCESS: $file" -ForegroundColor Green
        $success++
    }
    catch {
        Write-Host "  ERROR: $file - $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "====================================" -ForegroundColor Yellow
Write-Host "Download Summary" -ForegroundColor Yellow  
Write-Host "====================================" -ForegroundColor Yellow
Write-Host "Total files: $count" -ForegroundColor White
Write-Host "Successfully downloaded/existing: $success" -ForegroundColor Green
Write-Host "Failed: $($count - $success)" -ForegroundColor Red

if ($success -eq $count) {
    Write-Host ""
    Write-Host "🎉 ALL DEPENDENCIES READY! 🎉" -ForegroundColor Green
    Write-Host ""
    Write-Host "Your ABAP Assistant now supports:" -ForegroundColor Cyan
    Write-Host "  ✓ Deep Code Analysis" -ForegroundColor Green
    Write-Host "  ✓ Variable Tracking" -ForegroundColor Green  
    Write-Host "  ✓ Dependency Mapping" -ForegroundColor Green
    Write-Host "  ✓ Advanced UI Dialogs" -ForegroundColor Green
    Write-Host "  ✓ Full Eclipse Integration" -ForegroundColor Green
    Write-Host ""
} else {
    Write-Host ""
    Write-Host "⚠️  Some downloads failed. Please check your internet connection." -ForegroundColor Yellow
}

Write-Host "Press any key to continue..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
