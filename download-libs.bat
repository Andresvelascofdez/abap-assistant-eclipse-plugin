@echo off
echo Descargando librerías para ABAP Assistant Plugin...
echo.

REM Crear directorio lib si no existe
if not exist "lib" mkdir "lib"

echo Descargando librerías...

REM Apache HTTP Client
curl -L -o "lib\httpclient-4.5.14.jar" "https://repo1.maven.org/maven2/org/apache/httpcomponents/httpclient/4.5.14/httpclient-4.5.14.jar"
curl -L -o "lib\httpcore-4.4.16.jar" "https://repo1.maven.org/maven2/org/apache/httpcomponents/httpcore/4.4.16/httpcore-4.4.16.jar"

REM JSON Libraries
curl -L -o "lib\json-20230618.jar" "https://repo1.maven.org/maven2/org/json/json/20230618/json-20230618.jar"
curl -L -o "lib\gson-2.10.1.jar" "https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar"

REM Apache POI for Document Processing
curl -L -o "lib\poi-5.2.4.jar" "https://repo1.maven.org/maven2/org/apache/poi/poi/5.2.4/poi-5.2.4.jar"
curl -L -o "lib\poi-ooxml-5.2.4.jar" "https://repo1.maven.org/maven2/org/apache/poi/poi-ooxml/5.2.4/poi-ooxml-5.2.4.jar"
curl -L -o "lib\poi-scratchpad-5.2.4.jar" "https://repo1.maven.org/maven2/org/apache/poi/poi-scratchpad/5.2.4/poi-scratchpad-5.2.4.jar"

REM PDF Processing
curl -L -o "lib\pdfbox-2.0.29.jar" "https://repo1.maven.org/maven2/org/apache/pdfbox/pdfbox/2.0.29/pdfbox-2.0.29.jar"
curl -L -o "lib\fontbox-2.0.29.jar" "https://repo1.maven.org/maven2/org/apache/pdfbox/fontbox/2.0.29/fontbox-2.0.29.jar"

REM Commons Utilities
curl -L -o "lib\commons-lang3-3.12.0.jar" "https://repo1.maven.org/maven2/org/apache/commons/commons-lang3/3.12.0/commons-lang3-3.12.0.jar"
curl -L -o "lib\commons-io-2.11.0.jar" "https://repo1.maven.org/maven2/commons-io/commons-io/2.11.0/commons-io-2.11.0.jar"
curl -L -o "lib\commons-collections4-4.4.jar" "https://repo1.maven.org/maven2/org/apache/commons/commons-collections4/4.4/commons-collections4-4.4.jar"
curl -L -o "lib\commons-compress-1.21.jar" "https://repo1.maven.org/maven2/org/apache/commons/commons-compress/1.21/commons-compress-1.21.jar"

REM Jackson JSON Processing
curl -L -o "lib\jackson-core-2.15.2.jar" "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-core/2.15.2/jackson-core-2.15.2.jar"
curl -L -o "lib\jackson-databind-2.15.2.jar" "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-databind/2.15.2/jackson-databind-2.15.2.jar"

REM Logging
curl -L -o "lib\slf4j-api-1.7.36.jar" "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar"
curl -L -o "lib\slf4j-simple-1.7.36.jar" "https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/1.7.36/slf4j-simple-1.7.36.jar"

REM XMLBeans for POI
curl -L -o "lib\xmlbeans-5.1.1.jar" "https://repo1.maven.org/maven2/org/apache/xmlbeans/xmlbeans/5.1.1/xmlbeans-5.1.1.jar"

echo.
echo ✅ Todas las librerías descargadas correctamente!
echo 📂 Revisa la carpeta 'lib' para verificar los archivos
echo 🔄 Ahora puedes refrescar el proyecto en Eclipse (F5)
echo.
pause
