#!/usr/bin/env bash
echo "Starting deployment..."

cd Booking
git pull origin master

chmod +x mvnw
./mvnw clean package

java -jar target/bookingSite-0.0.1-SNAPSHOT.jar > /dev/null 2>&1 &
if [ $? -eq 0 ]; then
    echo "Application is now running on port 8080"
    ps -ef | grep java
fi
