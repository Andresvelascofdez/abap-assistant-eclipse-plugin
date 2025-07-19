@echo off
echo Building ABAP Assistant Eclipse Plugin...
echo.

REM Check if Maven is available
mvn --version >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo Maven not found. Please install Maven and add it to PATH.
    pause
    exit /b 1
)

echo Step 1: Cleaning previous build...
mvn clean

echo.
echo Step 2: Compiling and copying dependencies...
mvn compile
mvn dependency:copy-dependencies

echo.
echo Step 3: Creating plugin JAR...
if exist "dist" rmdir /s /q "dist"
mkdir "dist"

REM Create the plugin JAR with all necessary files
jar cfm dist\ABAPAssistant.jar META-INF\MANIFEST.MF ^
    -C src . ^
    -C . plugin.xml ^
    -C . build.properties ^
    -C lib . ^
    -C icons . ^
    -C resources .

if %ERRORLEVEL% equ 0 (
    echo.
    echo ✅ Plugin built successfully!
    echo 📦 Plugin JAR created: dist\ABAPAssistant.jar
    echo.
    echo 🚀 Installation Instructions:
    echo 1. Copy dist\ABAPAssistant.jar to your Eclipse\plugins folder
    echo 2. Restart Eclipse
    echo 3. Go to Window ^> Preferences ^> ABAP Assistant
    echo 4. Configure your OpenAI API Key
    echo 5. Open Window ^> Show View ^> ABAP Assistant
    echo.
) else (
    echo ❌ Build failed!
    pause
    exit /b 1
)

pause
