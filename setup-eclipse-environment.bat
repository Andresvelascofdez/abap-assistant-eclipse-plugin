@echo off
echo Setting up Eclipse Development Environment for ABAP Assistant...
echo.

REM Check if Eclipse SDK is available
if not exist "C:\eclipse" (
    echo Eclipse SDK not found at C:\eclipse
    echo Please download and install Eclipse SDK for RCP/RAP Developers
    echo Download from: https://www.eclipse.org/downloads/packages/
    goto :end
)

REM Set Eclipse workspace
set ECLIPSE_WORKSPACE=%cd%\workspace
if not exist "%ECLIPSE_WORKSPACE%" (
    mkdir "%ECLIPSE_WORKSPACE%"
    echo Created Eclipse workspace at: %ECLIPSE_WORKSPACE%
)

REM Set Java classpath with Eclipse libraries
set ECLIPSE_HOME=C:\eclipse
set ECLIPSE_CLASSPATH=%ECLIPSE_HOME%\plugins\*;lib\*;bin;.

echo.
echo Eclipse Environment Setup Complete!
echo.
echo To compile the plugin manually:
echo javac -cp "%ECLIPSE_CLASSPATH%" -encoding UTF-8 -d bin src\com\abap\assistant\views\ABAPAssistantView.java
echo.
echo To launch Eclipse with this plugin:
echo "%ECLIPSE_HOME%\eclipse.exe" -data "%ECLIPSE_WORKSPACE%" -vmargs -Xmx2g
echo.

:end
pause
