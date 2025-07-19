@echo off
echo ====================================
echo ABAP Assistant Plugin - Diagnostics
echo ====================================
echo.

echo 1. Checking project structure...
if exist ".project" (
    echo ✓ .project file exists
) else (
    echo ✗ .project file missing
)

if exist "META-INF\MANIFEST.MF" (
    echo ✓ MANIFEST.MF exists
) else (
    echo ✗ MANIFEST.MF missing
)

if exist "plugin.xml" (
    echo ✓ plugin.xml exists
) else (
    echo ✗ plugin.xml missing
)

echo.
echo 2. Checking libraries in lib folder...
cd lib
for %%f in (*.jar) do (
    echo ✓ %%f
)
cd ..

echo.
echo 3. Checking icons...
if exist "icons\abap_assistant.png" (
    echo ✓ Icon file exists
) else (
    echo ✗ Icon file missing
)

echo.
echo 4. Solutions for common Eclipse issues:
echo.
echo Problem: "The declared package does not match the expected package"
echo Solution: Ensure Eclipse recognizes this as a PDE project
echo   1. Right-click project in Eclipse → Properties
echo   2. Go to Project Natures
echo   3. Ensure "Plug-in Development" nature is enabled
echo.
echo Problem: "The import org.eclipse cannot be resolved"
echo Solutions:
echo   1. Check Eclipse is PDE-enabled (Plugin Development Environment)
echo   2. Right-click project → Properties → Project Natures
echo   3. Add "Plug-in Development" if missing
echo   4. In Properties → Build Path → Libraries
echo   5. Add "Plug-in Dependencies" if not present
echo.
echo Problem: Archive for required library cannot be read
echo Solution: Library paths may be incorrect, refresh project (F5)
echo.
echo 5. Steps to fix in Eclipse:
echo   1. Close the project
echo   2. Refresh workspace (F5)
echo   3. Reopen project
echo   4. Right-click project → Properties
echo   5. Go to "Project Natures" 
echo   6. Ensure these are checked:
echo      ☑ Plug-in Development
echo      ☑ Java
echo   7. Apply and Close
echo   8. Clean and rebuild project
echo.
pause
