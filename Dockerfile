
FROM seleniarm/standalone-chromium:latest

ENV CHROMEDRIVER_PORT 4444
ENV CHROMEDRIVER_WHITELISTED_IPS "127.0.0.1"
ENV CHROMEDRIVER_URL_BASE ''
EXPOSE 4444

EXPOSE 8080
EXPOSE 5005
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar


# define base docker image
FROM openjdk:19
LABEL maintainer="CornFarmerNZ"
ADD target/ClothesScraper-1.0-SNAPSHOT.jar ClothesScraper.jar
ENTRYPOINT ["java","-jar","ClothesScraper.jar"]

RUN microdnf install unzip
# Install Chrome WebDriver
RUN CHROMEDRIVER_VERSION=`curl -sS chromedriver.storage.googleapis.com/LATEST_RELEASE` && \
    mkdir -p /opt/chromedriver-$CHROMEDRIVER_VERSION && \
    curl -sS -o /tmp/chromedriver_linux64.zip http://chromedriver.storage.googleapis.com/$CHROMEDRIVER_VERSION/chromedriver_linux64.zip && \
    unzip /tmp/chromedriver_linux64.zip && \
    chmod +x chromedriver
#    curl -sS -o /tmp/chrome.deb https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb && \
#    chmod +x /tmp/chrome.deb && \
#    install /tmp/chrome.deb /usr/bin/
