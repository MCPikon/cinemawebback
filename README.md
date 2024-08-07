# CinemaWebBack

[![Java](https://img.shields.io/badge/Java-21%2B-saddlebrown?style=for-the-badge&logo=openjdk&logoColor=white&labelColor=101010)](https://docs.oracle.com/en/java/index.html) &nbsp;
[![Spring Boot](https://img.shields.io/badge/spring%20boot-3.3.1%2B-%236DB33F?style=for-the-badge&logo=springboot&logoColor=white&labelColor=101010)](https://spring.io/projects/spring-boot) &nbsp;
[![MongoDB](https://img.shields.io/badge/MongoDB-6.0+-00684A?style=for-the-badge&logo=mongodb&logoColor=white&labelColor=101010)](https://www.mongodb.com) &nbsp;
[![Swagger](https://img.shields.io/badge/Swagger-OAS3-%2385EA2D?style=for-the-badge&logo=swagger&logoColor=%23FFFFFF&labelColor=%23000000)](https://swagger.io/)

## 🙋‍♂️ Autor

* [Javier Picón](https://github.com/MCPikon)

## ✨ Descripción

API REST de películas, series y reseñas de las mismas.

> [!TIP]
> Este proyecto utiliza Swagger UI para la documentación.
> 
> Para consultar la web de documentación en local visita esta url: **http://localhost:8080/api/v1/docs** 

## 💡 Cómo funciona

Este proyecto realiza un CRUD (Crear, Obtener, Modificar y Eliminar) de Películas, Series y sus Reseñas. Disponen de entidades, DTOs, repositorios, servicios e implementaciones de los mismos.

## ✅ Testing

El testing del proyecto utiliza las dependencias JUnit 5, Mockito, WebMVCTest y Testcontainers. Hay archivos de test para los paquetes de controladores, repositorios, servicios y utilidades.

La cobertura total de lineas del proyecto es de un **95%**.

## 🛠 Tecnologías

* Java 21
* Spring Boot 3.3.1
* _**Dependencias Maven:**_
    * Spring Boot Starter Data MongoDB
    * Spring Boot Starter Web
    * Spring Boot Starter Test
    * Spring Boot Starter Actuator
    * Spring Boot Starter Validation
    * Spring Boot DevTools
    * Lombok
    * spring-dotenv (4.0.0)
    * SpringDoc OpenAPI Starter WebMVC UI (2.5.0)
    * java-json-tools _(json-patch)_ (1.13)
    * Spring Boot Testcontainers
    * Testcontainers junit-jupiter
    * Testcontainers mongodb

## 📄 Licencia

Este proyecto es de código abierto y está disponible bajo la [Licencia 2.0 de Apache](LICENSE).
