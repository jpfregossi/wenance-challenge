# Wenance Challenge

## Instrucciones para correr la aplicación

Crear una carpeta nueva y clonar el repositorio: 
 - git clone https://github.com/jpfregossi/wenance-challenge.git nombredelacarpeta
 - cd nombredelacarpeta
 - mvn spring-boot:run
 
 La api estará corriendo en http://localhost:8080
 
 Los endpoints son:
 1) http://localhost:8080/btc/[timestamp]
 2) http://localhost:8080/btc/average/[timestamp]/[timestamp]
  
  *[timestamp]* debe llevar la forma de AAAA-MM-DDTHH:MM:SS
  
  Ejemplos:
  
  curl http://localhost:8080/btc/2021-01-14T19:03:00

  curl http://localhost:8080/btc/average/2021-01-14T19:03:00/2021-01-14T19:05:00
  
## Consigna

El objetivo de este pequeño ejercicio es demostrar la utilización del api de
Streams (java.util.stream)

A) Construir un microservicio que haciendo uso del siguiente servicio REST (https://cex.io/api/last_price/BTC/USD)
realice una llamada recurrente cada 10 segundos, almacene los datos y que exponga a través de un API rest las siguientes funcionalidades:

1. Obtener el precio del bitcoin en cierto timestamp.
2. Conocer el promedio de valor entre dos timestamps así como la diferencia porcentual entre ese valor promedio y el valor máximo almacenado para toda la serie temporal disponible.

B) incorporar un archivo READ.ME que contenga una descripción de la solución propuesta así como instrucciones de ejecución en entorno local.

Indicaciones:
* La aplicación deberá estar desarrollada usando Springboot y subida a un repositorio en github con permisos públicos de acceso y clonado.
* La aplicación deberá ser ejecutada en entorno local sin necesidad de dockerización ni de otro software más que java 1.8
* El uso de frameworks accesorios queda a la elección del candidato
* La persistencia de información se realizará en una estructura de datos en memoria lo más optimizada posible.

Puntos extras si:
- Se incorpora un conjunto de test unitarios que demuestran la corrección de la solución
- Se usa WebClient 

# Solución Propuesta

El framework elegido fue Webflux, ya que incorpora WebClient de una manera reactiva.

Entendiendo que el fin del challenge era demostrar el uso de java.util.stream, opte por una estructura de datos con persistencia in-memory del tipo lista enlazada (ya que se trata de una serie temporal y las busquedas serán sequenciales), de atributos primitivos del tipo long (para el timestamp) y double para la cotización para optimizar costos temporales y espaciales, sobre la que puede operar esta api.

Para hacer los requests a intervalos regulares de opté por el uso de Flux de una manera que no fuera bloqueante (a diferencia de la annotation @Scheduled).
Este Flux es utilizado por TimeseriesRepo, al suscribirse, para alimentar los datos en memoria.
Las queries son resueltas con la api solicitada.

## Consideraciones
- Podría haberse optado por una base de datos in-memory al estilo de Redis (que posee estructuras especialmente diseñadas para series temporales) pero entonces carecería de sentido usar java.util.stream ya que se puede acceder de manera reactiva con sus propios operadores. También el uso de java.util.stream podría ser reemplazado por la implementación propia de WebFlux de reactive streams.

También es cuestionable el uso de lista enlazada, ya que si se deseara hacer uso de varios núcleos para resolver las queries, una lista simple sería una mejor opción.



