FROM java:8
ADD googleauth-totp-web.jar googleauth-totp-web.jar
EXPOSE 8080
ENTRYPOINT ["java","-DPORT=8080","-jar","googleauth-totp-web.jar"]
