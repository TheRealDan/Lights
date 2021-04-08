call gradlew desktop:dist

FOR /F "tokens=* USEBACKQ" %%F IN (`git describe --tags`) DO (SET describe=%%F)

FOR /f "tokens=1 delims=-" %%a in ("%describe%") do (SET tag=%%a)

FOR /f "tokens=2 delims=-" %%a in ("%describe%") do (SET build=%%a)

FOR /f "tokens=3 delims=-" %%a in ("%describe%") do (SET hash=%%a)

CD "desktop/build/libs"
COPY "desktop-latest.jar" "Lights %tag%.%build%-%hash%.jar"

PAUSE