FROM openjdk:17

EXPOSE 8081

WORKDIR /shop
ADD ./target/shop-1.0.jar ./shop.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "shop.jar"]