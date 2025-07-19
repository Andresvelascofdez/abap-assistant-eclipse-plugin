@echo off
echo ==================================================
echo ABAP Assistant - Eclipse Dependencies Downloader  
echo ==================================================
echo.

setlocal enabledelayedexpansion

:: Crear directorio lib si no existe
if not exist lib mkdir lib

echo Downloading Eclipse Platform Dependencies...
echo.

:: Usamos Maven Central para descargar las dependencias de Eclipse
powershell -Command "& {
    $urls = @{
        'eclipse-core-runtime-3.29.0.jar' = 'https://repo1.maven.org/maven2/org/eclipse/platform/org.eclipse.core.runtime/3.29.0/org.eclipse.core.runtime-3.29.0.jar';
        'eclipse-core-resources-3.19.0.jar' = 'https://repo1.maven.org/maven2/org/eclipse/platform/org.eclipse.core.resources/3.19.0/org.eclipse.core.resources-3.19.0.jar';
        'eclipse-core-jobs-3.15.0.jar' = 'https://repo1.maven.org/maven2/org/eclipse/platform/org.eclipse.core.jobs/3.15.0/org.eclipse.core.jobs-3.15.0.jar';
        'eclipse-text-3.12.300.jar' = 'https://repo1.maven.org/maven2/org/eclipse/platform/org.eclipse.text/3.12.300/org.eclipse.text-3.12.300.jar';
        'eclipse-jface-text-3.24.0.jar' = 'https://repo1.maven.org/maven2/org/eclipse/platform/org.eclipse.jface.text/3.24.0/org.eclipse.jface.text-3.24.0.jar';
        'eclipse-ui-3.201.0.jar' = 'https://repo1.maven.org/maven2/org/eclipse/platform/org.eclipse.ui/3.201.0/org.eclipse.ui-3.201.0.jar';
        'eclipse-ui-workbench-3.130.0.jar' = 'https://repo1.maven.org/maven2/org/eclipse/platform/org.eclipse.ui.workbench/3.130.0/org.eclipse.ui.workbench-3.130.0.jar';
        'eclipse-ui-workbench-texteditor-3.17.0.jar' = 'https://repo1.maven.org/maven2/org/eclipse/platform/org.eclipse.ui.workbench.texteditor/3.17.0/org.eclipse.ui.workbench.texteditor-3.17.0.jar';
        'eclipse-jface-3.32.0.jar' = 'https://repo1.maven.org/maven2/org/eclipse/platform/org.eclipse.jface/3.32.0/org.eclipse.jface-3.32.0.jar';
        'eclipse-swt-3.124.0.jar' = 'https://repo1.maven.org/maven2/org/eclipse/platform/org.eclipse.swt/3.124.0/org.eclipse.swt-3.124.0.jar';
        'eclipse-core-commands-3.11.0.jar' = 'https://repo1.maven.org/maven2/org/eclipse/platform/org.eclipse.core.commands/3.11.0/org.eclipse.core.commands-3.11.0.jar';
        'eclipse-equinox-common-3.18.0.jar' = 'https://repo1.maven.org/maven2/org/eclipse/platform/org.eclipse.equinox.common/3.18.0/org.eclipse.equinox.common-3.18.0.jar';
        'eclipse-equinox-registry-3.11.200.jar' = 'https://repo1.maven.org/maven2/org/eclipse/platform/org.eclipse.equinox.registry/3.11.200/org.eclipse.equinox.registry-3.11.200.jar';
        'osgi-framework-3.18.500.jar' = 'https://repo1.maven.org/maven2/org/eclipse/platform/org.eclipse.osgi/3.18.500/org.eclipse.osgi-3.18.500.jar'
    }

    $count = 0
    $success = 0
    foreach ($file in $urls.Keys) {
        $count++
        Write-Host \"Downloading $file...\" -ForegroundColor Cyan
        try {
            Invoke-WebRequest -Uri $urls[$file] -OutFile \"lib\\$file\" -UseBasicParsing
            Write-Host \"SUCCESS: $file\" -ForegroundColor Green
            $success++
        } catch {
            Write-Host \"ERROR downloading $file : \" $_.Exception.Message -ForegroundColor Red
        }
    }
    
    Write-Host \"\" 
    Write-Host \"====================================\" -ForegroundColor Yellow
    Write-Host \"Download Summary\" -ForegroundColor Yellow  
    Write-Host \"====================================\" -ForegroundColor Yellow
    Write-Host \"Total files: $count\" -ForegroundColor White
    Write-Host \"Successfully downloaded: $success\" -ForegroundColor Green
    Write-Host \"Failed: \" ($count - $success) -ForegroundColor Red
}"

echo.
echo Downloading additional dependencies for SWT Windows...
powershell -Command "& {
    try {
        Write-Host 'Downloading SWT for Windows x64...' -ForegroundColor Cyan
        Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/eclipse/platform/org.eclipse.swt.win32.win32.x86_64/3.124.0/org.eclipse.swt.win32.win32.x86_64-3.124.0.jar' -OutFile 'lib\\eclipse-swt-win32-x64-3.124.0.jar' -UseBasicParsing
        Write-Host 'SUCCESS: eclipse-swt-win32-x64-3.124.0.jar' -ForegroundColor Green
    } catch {
        Write-Host 'ERROR downloading SWT Windows: ' $_.Exception.Message -ForegroundColor Red
    }
}"

echo.
echo ====================================
echo Eclipse Dependencies Ready!
echo ====================================
echo.
echo Available dependencies for ABAP Assistant:
echo  * Eclipse Core Runtime and Resources  
echo  * Eclipse UI Framework (SWT, JFace)
echo  * Eclipse Text Editor APIs
echo  * Eclipse Command and Handler Framework
echo  * OSGi Framework
echo.
echo Your ABAP Assistant now supports:
echo  * Deep Code Analysis
echo  * Variable Tracking
echo  * Dependency Mapping  
echo  * Advanced UI Dialogs
echo  * Full Eclipse Integration
echo.

pause
