# Clothes-Scraper

This Application is a web scraper built with the Spring framework that uses an HTTP Client & a Web Driver (Selenium remote web driver) to get around restrictions and scrape item prices and images from stores.


Currently supported stores: KMART, GLASSONS, THE_WAREHOUSE, CHEMIST_WAREHOUSE, AMAZON, POSTIE, IPPONDO, DAIKOKU

Store URLSs and prices are stored in AWS DynamoDB databases.

# To run (Docker):

1. Build Docker image: "docker build -t clothes-scraper:latest ."
2. Run Selenium Remote Web Driver: 
    MacOS: CLI - "docker run -it -p 4444:4444 -p 5900:5900 -p 7900:7900 --shm-size 2g seleniarm/standalone-chromium:latest "
    Other: https://github.com/SeleniumHQ/docker-selenium 
3. Run Docker image: docker run -d --network="host" --env AWS_ACCESS_KEY_ID={} --env AWS_SECRET_ACCESS_KEY={} clothes-scraper:latest env
    Replace env variable swith own AWS credentials and change the dynamodb.table value in the application.yml file.
