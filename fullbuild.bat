cd frontend/engine-interface
call npm install || exit /b
call npm run build || exit /b
cd ../..
call robocopy frontend/engine-interface/build src/main/resources/static/engine-interface /S /E
call mvn clean package -Dspring.config.location=file:appconfig/pso-decision-engine/ -P release-war || exit /b