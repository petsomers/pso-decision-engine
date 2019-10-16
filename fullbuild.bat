cd frontend/engine-interface
call npm install
call npm run build
cd ../..
call robocopy frontend/engine-interface/build src/main/resources/static/engine-interface /S /E
call mvn clean package -Dspring.config.location=file:appconfig/pso-decision-engine/ -P release-war