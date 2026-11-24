# RinconSur - Microservicios

Backend de la aplicación móvil RinconSur, desarrollado para la asignatura Desarrollo de Aplicaciones Móviles (DSY1105) de Duoc UC. Son microservicios en Spring Boot que la app Android consume a través de un API Gateway. La app está en otro repositorio: [https://github.com/lanedd-1/DesarrolloAppMoviles.git].

## Integrantes

- [Cristopher Bustamante]
- [Jose Recabarren]
- [Brandon Banda]

Docente: [Victor Rosendo]
Sección: [012D, 013D, 014D]
Equipo: [1,8,9]

## Descripción

RinconSur sirve para registrar y analizar el flujo de visitantes de una tienda. El backend está dividido en cuatro microservicios, un servidor Eureka para el registro de servicios y un API Gateway como punto de entrada único. La app solo se comunica con el gateway.

## Servicios y puertos

- eureka: 8761
- gateway: 8080
- ms_usuario: 8081 (base de datos rinconsur_usuarios)
- ms_visitante: 8082 (base de datos rinconsur_visitante)
- ms_administracion: 8083 (base de datos rinconsur_administracion)
- ms_dashboard: 8084 (sin base de datos, consulta a ms_visitante con WebClient)

## Funcionalidades

- Usuarios: login, creación de usuarios con rol, listado, cambio de estado y de rol, cambio de contraseña, recuperación de contraseña con código de 6 dígitos y foto de perfil.
- Visitantes: sesiones de observación, registros de visitantes, estadísticas, y edición y eliminación de registros.
- Administración: listado de accesos y cambio de su disponibilidad.
- Dashboard: resumen diario, distribución por categorías y detalle de registros.
- Eureka y API Gateway: descubrimiento de servicios y enrutamiento de las peticiones de la app.

## Endpoints

Todas las peticiones de la app pasan por el gateway (http://localhost:8080). El gateway las envía al servicio correspondiente según la ruta:

- /api/usuarios/** y /api/roles/** van a ms_usuario
- /api/sesiones/** y /api/registros/** van a ms_visitante
- /api/accesos/** va a ms_administracion
- /api/dashboard/** va a ms_dashboard

### ms_usuario

- POST /api/usuarios/login
- POST /api/usuarios
- GET /api/usuarios
- GET /api/usuarios/{id}
- PUT /api/usuarios/{id}
- PUT /api/usuarios/{id}/estado
- PUT /api/usuarios/{id}/rol
- PUT /api/usuarios/{id}/contrasena
- POST /api/usuarios/recuperacion/solicitar
- POST /api/usuarios/recuperacion/verificar
- POST /api/usuarios/recuperacion/restablecer
- GET /api/roles
- POST /api/usuarios/{id}/foto
- GET /api/usuarios/{id}/foto

### ms_visitante

- GET /api/sesiones
- GET /api/sesiones/estadisticas
- GET /api/sesiones/{id}
- GET /api/sesiones/activas
- POST /api/sesiones
- PUT /api/sesiones/{id}/finalizar
- GET /api/sesiones/{id}/registros
- POST /api/sesiones/{id}/registros
- DELETE /api/sesiones/{id}/registros/ultimo
- GET /api/registros?fecha=&acceso=
- GET /api/registros/fechas
- PUT /api/registros/{id}
- DELETE /api/registros/{id}

### ms_administracion

- GET /api/accesos
- PUT /api/accesos/{nombre}/disponibilidad

### ms_dashboard

- GET /api/dashboard/fechas
- GET /api/dashboard/resumen?fecha=
- GET /api/dashboard/distribucion?fecha=&acceso=
- GET /api/dashboard/registros?fecha=&acceso=

## Pasos para ejecutar

Requisitos: Java 21, Maven y Laragon con MySQL en el puerto 3306.

1. En MySQL crear las bases rinconsur_usuarios, rinconsur_visitante y rinconsur_administracion.
2. Abrir una terminal por servicio y levantarlos en este orden con `mvn spring-boot:run`: eureka, ms_usuario, ms_visitante, ms_administracion, ms_dashboard y gateway.
3. Entrar a http://localhost:8761 y esperar a que aparezcan los cuatro servicios registrados. Puede tardar hasta 30 segundos.
4. Probar una ruta a través del gateway, por ejemplo http://localhost:8080/api/roles.

## Pruebas

Las pruebas unitarias se ejecutan en cada servicio con `mvn test`.
