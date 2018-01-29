FROM openjdk:alpine
 
EXPOSE 80

RUN apk add --no-cache maven
ADD src /json-validator/src
ADD pom.xml /json-validator/pom.xml
WORKDIR /json-validator
RUN mvn clean install -e
CMD ["java","-jar","/usr/src/app/target/validator-0.1-jar-with-dependencies.jar"]
#  sudo docker build -t validator .
#  sudo docker run -d --rm -p 80:80  validator
