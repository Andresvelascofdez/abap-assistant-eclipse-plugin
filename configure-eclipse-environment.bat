@echo off
echo Configuring Eclipse Development Environment for ABAP Assistant Plugin...
echo.

REM Check if Eclipse dependencies are available
if not exist "lib\eclipse-core-runtime-3.29.0.jar" (
    echo ERROR: Eclipse dependencies not found in lib directory
    echo Please ensure all Eclipse JAR files are in the lib folder
    pause
    exit /b 1
)

echo Setting up Eclipse classpath...
set ECLIPSE_LIBS=
for %%f in (lib\eclipse-*.jar) do (
    if defined ECLIPSE_LIBS (
        set ECLIPSE_LIBS=!ECLIPSE_LIBS!;%%f
    ) else (
        set ECLIPSE_LIBS=%%f
    )
)

echo Setting up external dependencies classpath...
set EXTERNAL_LIBS=
for %%f in (lib\*.jar) do (
    if not "%%f"=="lib\eclipse-*" (
        if defined EXTERNAL_LIBS (
            set EXTERNAL_LIBS=!EXTERNAL_LIBS!;%%f
        ) else (
            set EXTERNAL_LIBS=%%f
        )
    )
)

set FULL_CLASSPATH=%ECLIPSE_LIBS%;%EXTERNAL_LIBS%;bin;.

echo.
echo Eclipse environment configured successfully!
echo Classpath: %FULL_CLASSPATH%
echo.
echo Ready for compilation. Use this classpath for javac commands.
pause
