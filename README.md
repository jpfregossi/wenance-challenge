# Wenance Challenge

# Instrucciones para correr la aplicación



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
Como se solicitaba el uso de la librería java.util.stream, opte por una estructura de datos con persistencia in-memory del tipo lista enlazada (ya que se trata de una serie temporal y las busquedas serán sequenciales), de atributos primitivos del tipo long (para el timestamp) y double para la cotización.
Para hacer los requests a intervalos regulares de opté por el uso de Flux de una manera que no fuera bloqueante (a diferencia de la annotation @Scheduled).
Este Flux es utilizado por TimeseriesRepo al suscribirse para alimentar los datos en memoria.
Las queries son resueltas con la api solicitada.

## Consideraciones
- Datos: Podría haberse optado por una base de datos in-memory al estilo de Redis (que posee estructuras especialmente diseñadas para series temporales) pero entonces carecería de sentido usar java.util.stream ya que se puede acceder de manera reactiva con sus propios operadores.
También es cuestionable el uso de lista enlazada, ya que si se deseara hacer uso de varios núcleos para resolver las queries, un arreglo sería una mejor opción.



