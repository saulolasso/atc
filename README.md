# ATC Subscription System
## 1. Overview
This application is a Newsletter Subscription System using a microservice architecture.

It consists of 5 components:
#### Public Service (Gateway)
This microservice implements the API Gateway Pattern. It merely exposes the private microservices to the internet. It has been built using the Spring Cloud Gateway Framework. It has been built on Spring Cloud Gateway. It runs on port 9000.

### Subscription Service
This microservice implements the API Gateway Pattern. It merely exposes the private microservices to the internet. It has been built using the Spring Cloud Gateway Framework. It has been built on Spring Cloud Gateway. It runs on port 9000.
This microservice implements a Rest API with the following operations:
- Create new subscription
- Cancel existing subscription
- Get details of a subscription
- Get all subscriptions

It has been built on Spring Boot with Java 11. It runs on port 9002.
The Rest API documentation has been automatically generated using OpenAPI v3, and can be found after deploying the component at http://localhost:9002/v3/api-docs.

### Database Service
This is a MySQL Community Edition database that serves the Subscription Service. It runs on port 3306.

### Email Service
This microservice implements an event listener with the following operations:
- Send subscription created notification.
- Send subscription cancelled notification.

It has been built on Spring Boot with Java 11. This component runs on port 9003.
The Rest API documentation has been automatically generated using OpenAPI v3, and can be found after deploying the component at http://localhost:9003/v3/api-docs.

### Kafka Event Broker
This container runs a Kafka instance with two topics:
-	sendSubscriptionCreatedNotification
-	sendSubscriptionCancelledNotification

## 2. How to install and run on Docker Containers in Linux
Prerequisites:
- Linux
- Docker
- Docker compose
- Git
- OpenJDK 11
- Apache Maven
- Maven Wrapper

#### Container 1: MySQL
Pull MySQL image adn run container:
```sh
$ docker pull mysql/mysql-server:latest
$ docker run -p 3306:3306 -d --name=mysql mysql/mysql-server:latest
```
Get the root password from the log. Look for the line starting with "[Entrypoint] GENERATED ROOT PASSWORD:"
```sh
$ docker logs mysql
```
Run mySQL bash:
```sh
$ mysql -uroot -p
```
Reset root password:
```sh
ALTER USER 'root'@'localhost' IDENTIFIED BY 'newpassword';
```
Create the database, create a user and grant permissions:
```sh
$ CREATE DATABASE atc_subscription;
$ CREATE USER 'atc_subscription'@'localhost' IDENTIFIED BY 'atc_subscription';
$ GRANT ALL PRIVILEGES ON *.* TO 'atc_subscription'@'localhost' WITH GRANT OPTION;
$ CREATE USER 'atc_subscription'@'%' IDENTIFIED BY 'atc_subscription';
$ GRANT ALL PRIVILEGES ON *.* TO 'atc_subscription'@'%' WITH GRANT OPTION;
$ FLUSH PRIVILEGES;
```
Note: In a production environment only the minimum privileges should be granted.
#### Containers 2 and 3: Apache Zookeeper and Apache Kafka
Download file https://github.com/bitnami/bitnami-docker-kafka/blob/master/docker-compose.yml
Download docker-compose.yml and run docker composer to create containers:
```sh
$ curl -sSL https://raw.githubusercontent.com/bitnami/bitnami-docker-kafka/master/docker-compose.yml > docker-compose.yml
$ docker-compose up -d
```
Create the topics
```sh
$ docker-compose exec broker kafka-topics --create –zookeeper zookeeper:2181 --replication-factor 1 --partitions 1 --topic sendSubscriptionCreatedNotification
$ docker-compose exec broker kafka-topics --create –zookeeper zookeeper:2181 --replication-factor 1 --partitions 1 --topic sendSubscriptionCancelledNotification
```
#### Container 4: Mail Service
Get the code from GitHub:
```sh
$ git clone https://github.com/saulolasso/atc.git
```
Change to the Mail project folder with the POM file:
```sh
$ cd atc/com.slb.atc.mail
```
Create Maven wrapper, generate image and run
```sh
$ mvn -N io.takari:maven:wrapper
$ ./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=atc-mail/atc-mail-docker
$ docker run -p 9003:9003 -t atc-mail/atc-mail-docker
```
To verify, open in browser:
http://localhost:9003/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

#### Container 5: Subscriber Service
Change to the Subscription project folder with the POM file:
```sh
$ cd atc/com.slb.atc.subscription
```
Create Maven wrapper, generate image and run
```sh
$ mvn -N io.takari:maven:wrapper
$ ./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=atc-subscription/atc-subscription-docker
$ docker run -p 9002:9002 -t atc-mail/atc-subscription-docker
```
To verify, open in browser:
http://localhost:9002/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

#### Container 6: Gateway Service
Change to the Gateway project folder with the POM file:
```sh
$ cd atc/com.slb.atc.gateway
```
Create Maven wrapper, generate image and run
```sh
$ mvn -N io.takari:maven:wrapper
$ ./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=atc-gateway/atc-gateway-docker
$ docker run -p 9002:9002 -t atc-mail/atc-gateway-docker
```
To verify, open Postman or a similar client tool and send a GET request to:
http://localhost:9000/subscription/getById/1

## 3. How to deploy and run on Windows
Prerequisites:
- Java 11 installed
- MySQL installed and listening to default port 3306
- Kafka installed
- Git installed

The following is the preferred order to deploy and run the components:

#### Component 1: MySQL
Create database atc_subscription.
Create user atc_subscription with password atc_subscription
Grant privileges to user atc_subscription

#### Component 2: Apache Kafka
Start Zookeper. Go to %KAFKA_HOME%/bin/windows and run:
```sh
$ zookeeper-server-start.bat ../../config/zookeeper.properties
```
Start Kafca from the same folder:
```sh
$ kafka-server-start.bat ../../config/server.properties
```
Create the 2 topics used by the application:
```sh
$ kafka-topics.bat --create --topic sendSubscriptionCreatedNotification --bootstrap-server localhost:9092
$ kafka-topics.bat --create --topic sendSubscriptionCancelledNotification --bootstrap-server localhost:9092
```
#### Component 3: Mail Service
Open Git Bash
```sh
$ git clone https://github.com/saulolasso/atc.git
```
Change folder to /atc/com.slb.atc.mail, build with Maven and run:
```sh
$ mvn clean install
$ java -jar target/mail-0.0.1.jar com.slb.atc.gateway.MailApplication
```
To verify, open in browser:
http://localhost:9003/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

#### Component 4: Subscription Service
Change folder to /atc/com.slb.atc.subscription, build with Maven and run:
```sh
$ mvn clean install
$ java -jar target/mail-0.0.1.jar com.slb.atc.subscription.MailApplication
```
To verify, open in browser:
http://localhost:9002/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

#### Component 5: Gateway Service
Change folder to /atc/com.slb.atc.gateway, build with Maven and run:
```sh
$ mvn clean install
$ java -jar target/mail-0.0.1.jar com.slb.atc.gateway.MailApplication
```
To verify, open Postman or a similar client tool and send a GET request to:
http://localhost:9000/subscription/getById/1

## How to test and use
This project doesn't include a UI.

It requires a tool like Postman or similar to send requests.
The requests shall be sent to the Gateway at https://localhost:9000

The documentation of the specific API operations can be found at:
http://localhost:9002/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config
Tests can also be conveniently run from this UI, although they would bypass the Gateway which is at port 9000.

#### Test example
##### _Test excution_
In Postman, create a POST request with https://localhost:9000/subscription/create and the following JSON body:
```sh
{
  "id": 0,
  "email": "paul@paul.com",
  "firstname": "paul",
  "gender": "GENDER_MALE",
  "dateOfBirth": "2001-01-01",
  "flagForConsent": true,
  "newsletterId": 3,
  "cancelled": false
}
```
##### _Test excution_
After sending the request the response should be 201 with the same JSON body but with an assigned ID.
The database can be checked to verify that the record was persisted.
The application log of the Mail Service can be checked to verify that the notification was sent.

#### Unit Tests
The Subscription Service has a comprehensive suite of unit tests that can be run using Maven:
```sh
mvn clean test
```
