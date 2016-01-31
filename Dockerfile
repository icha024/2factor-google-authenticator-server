FROM java:8
ADD auth-otp.jar auth-otp.jar
EXPOSE 8080
ENTRYPOINT ["java","-DPORT=8080","-jar","auth-otp.jar"]
