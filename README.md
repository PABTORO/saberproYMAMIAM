# Banco de Preguntas Saber PRO — Segundo Parcial Práctico (Corte II)

Ingeniería de Software II · Universidad del Cauca · 2026.2

Tres microservicios independientes (Spring Boot 3.3.5, Java 17, Maven multimódulo) que implementan los flujos de
**creación + validación (REST síncrono)**, **persistencia JPA con arquitectura limpia** y **envío a revisión asíncrono (RabbitMQ)**.

> ## Estado de verificación (leer primero)
> Este proyecto se generó en un entorno **sin Maven, Docker, RabbitMQ ni acceso a red**, por lo que
> **los tres servicios NO se arrancaron ni se ejecutó la colección Postman contra ellos**. Lo que sí se verificó:
>
> | Verificación | Resultado |
> |---|---|
> | Compilación con `javac` (Java 21, `-Xlint:all`, sin advertencias) de validador, dominio, entidades JPA, mapeador y máquina de estados (con *stubs* de las anotaciones de Spring/JPA/JUnit) | OK |
> | 11 pruebas unitarias (validador 5, mapeador + aislamiento del dominio 3, transiciones de estado 3) ejecutadas con un runner mínimo | 11/11 pasan |
> | Imports propios resuelven a archivos existentes; paquete = ruta en los 51 `.java` | OK |
> | Sintaxis de los 10 scripts de Postman (`node --check`) y regex del test 400 contra los mensajes reales del validador | OK |
> | `mvn package`, arranque de servicios, H2, RabbitMQ, ejecución de Postman | **Pendiente: ejecutarlo en su máquina** (ver sección 9) |
>
> Si `mvn clean package` o el arranque muestran errores, son los primeros que hay que corregir; no se han visto.

## 1. Arquitectura

```
                       POST /questions                         POST /validate (RestTemplate, síncrono)
   Cliente / Postman ───────────────────►  Question Service ───────────────────────────────► Validation Service
                                            :8083  (H2, JPA)  ◄─── 200 OK | 400 + errores ──   :8082
                                               │
              POST /questions/{id}/submit      │ RabbitTemplate.convertAndSend(...)
              ◄── 202 {operationId,PROCESSING} │
                                               ▼
                          exchange  saberpro.direct.exchange  (direct)
                          routing key  question.submit
                          cola  question.submit.review.queue
                                               │ @RabbitListener
                                               ▼
                                        Lifecycle Service :8084
                                        DRAFT → PENDING_REVIEW (autoridad del estado)
```

Question Service sigue arquitectura limpia (las dependencias apuntan hacia el dominio):

```
api ─► application ─► domain  ◄─ infrastructure (jpa, client, messaging)
```

## 2. Tecnologías

Java 17 (compila y corre también en 21) · Spring Boot 3.3.5 · Maven · Spring Web · Spring Data JPA (Hibernate) · H2 ·
Spring AMQP / RabbitMQ 3.13 · Jackson · JUnit 5 · Postman / Newman.

## 3. Estructura

```
saberpro/
├── pom.xml                      (padre, multimódulo)
├── docker-compose.yml           (RabbitMQ + consola de administración)
├── README.md
├── postman/
│   ├── SaberPRO.postman_collection.json
│   └── SaberPRO.postman_environment.json
├── validation-service/          (:8082)  api · domain (QuestionValidator, Java puro)
├── question-service/            (:8083)
│   └── src/main/java/.../question/
│       ├── domain/model/            Question, AnswerOption, QuestionStatus   ← Java puro, sin JPA
│       ├── domain/repository/       QuestionRepository (interfaz)
│       ├── application/             QuestionService + puertos (ValidationGateway, SubmitCommandPublisher)
│       ├── infrastructure/jpa/record/       QuestionRecord, AnswerOptionRecord (@Entity)
│       ├── infrastructure/jpa/mapper/       QuestionDomainMapper
│       ├── infrastructure/jpa/repository/   SpringQuestionDataRepository, QuestionRepositoryJpaImpl
│       ├── infrastructure/client/   ValidationRestClient (RestTemplate)
│       ├── infrastructure/messaging/        RabbitConfig, RabbitSubmitCommandPublisher
│       └── api/                     QuestionController, DTOs, GlobalExceptionHandler
└── lifecycle-service/           (:8084)  messaging (@RabbitListener) · domain (QuestionStateStore) · api
```

## 4. Requisitos

- JDK 17 o superior (`java -version`)
- Maven 3.9+ (`mvn -v`)
- Docker Desktop (para RabbitMQ) — o un RabbitMQ local con usuario `guest/guest`
- Postman (o Node.js + `npm i -g newman`)

## 5. Iniciar RabbitMQ (PowerShell)

```powershell
cd saberpro
docker compose up -d
docker compose ps                 # esperar estado "healthy"
```

Consola de administración: <http://localhost:15672> — **usuario `guest` / contraseña `guest`** (solo desarrollo). AMQP en `localhost:5672`.

## 6. Iniciar los microservicios (PowerShell)

```powershell
cd saberpro
mvn clean package                 # compila los 3 módulos y corre las pruebas unitarias
```

Una terminal por servicio, **en este orden**:

```powershell
mvn -pl validation-service spring-boot:run     # http://localhost:8082
mvn -pl question-service   spring-boot:run     # http://localhost:8083
mvn -pl lifecycle-service  spring-boot:run     # http://localhost:8084
```

Alternativa con los JAR: `java -jar validation-service\target\validation-service-1.0.0.jar` (igual para los otros).

Prueba manual rápida:

```powershell
$body = @'
{"statement":"Cual estructura sigue LIFO?","options":[{"key":"A","text":"Cola"},{"key":"B","text":"Pila"},{"key":"C","text":"Arbol"},{"key":"D","text":"Tabla hash"}],"correctKey":"B","justification":"La pila es LIFO: el ultimo en entrar es el primero en salir, a diferencia de la cola."}
'@
$q = Invoke-RestMethod -Method Post -Uri http://localhost:8083/questions -ContentType 'application/json' -Body $body
$q                                                                   # 201, status DRAFT
Invoke-RestMethod -Method Post -Uri "http://localhost:8083/questions/$($q.id)/submit"   # 202, PROCESSING
Invoke-RestMethod -Uri "http://localhost:8084/lifecycle/questions/$($q.id)"             # PENDING_REVIEW
```

## 7. Ejecutar las pruebas Postman

**Postman:** *Import* → `postman\SaberPRO.postman_collection.json` y `postman\SaberPRO.postman_environment.json`;
seleccionar el entorno *Saber PRO - local* y usar *Run collection* (las solicitudes dependen del orden).

**Newman:**
```powershell
newman run postman\SaberPRO.postman_collection.json -e postman\SaberPRO.postman_environment.json
```

| # | Solicitud | Verifica |
|---|---|---|
| 1 | `POST /questions` válida | 201, estructura, `status=DRAFT`, `Location`; **captura `id` en `{{questionId}}`** |
| 2 | `POST /questions` inválida | 400, `errors[]` con los 3 mensajes (4 opciones, clave inexistente, justificación) |
| 3 | `POST /questions/{{questionId}}/submit` | 202, `operationId` (UUID), `status=PROCESSING`, respuesta < 2 s |
| 4 | `GET :15672/api/queues/%2F/question.submit.review.queue` | cola durable, ≥1 consumidor, `publish ≥ 1`, `ack ≥ 1`, `messages_ready = 0` |
| E1–E4 | extras | Validation 200/400 directo, estado en Lifecycle = `PENDING_REVIEW`, pregunta persistida |

Los puertos están en variables (`host`, `validationPort`, `questionPort`, `lifecyclePort`, `rabbitMgmtPort`, `rabbitUser`, `rabbitPass`).
La solicitud 4 espera 6 s antes de consultar porque RabbitMQ refresca sus estadísticas cada ~5 s.

## 8. Comprobar H2 y RabbitMQ

**H2** (en memoria, mientras Question Service esté corriendo): <http://localhost:8083/h2-console>
JDBC URL `jdbc:h2:mem:questiondb` · usuario `sa` · contraseña vacía.
```sql
SELECT * FROM questions;
SELECT * FROM answer_options ORDER BY question_id, id;
```

**RabbitMQ:** consola → *Queues and Streams* → `question.submit.review.queue` (gráficas *Message rates*, columnas *Ready/Total* y *Consumers*).
Por línea de comandos:
```powershell
docker exec saberpro-rabbitmq rabbitmqctl list_queues name messages consumers
docker exec saberpro-rabbitmq rabbitmqctl list_bindings | Select-String "question.submit"
```
Para ver el mensaje en la cola, detenga Lifecycle, envíe una pregunta (solicitud 3) y use *Get messages* en la consola; al reiniciar Lifecycle lo consume.

## 9. Registro de resultados (completar al ejecutar)

| Verificación | Fecha | Resultado real |
|---|---|---|
| `mvn clean package` | | |
| Arranque 8082 / 8083 / 8084 | | |
| Postman 1 — 201 | | |
| Postman 2 — 400 | | |
| Postman 3 — 202 | | |
| Postman 4 — cola RabbitMQ | | |
| H2: filas en `questions` / `answer_options` | | |

## 10. Decisiones técnicas (lo que el PDF no especifica)

| Tema | Decisión | Motivo |
|---|---|---|
| Versiones | Spring Boot 3.3.5, Java 17 | Combinación estable; `jakarta.persistence`, Spring AMQP 3.1 incluidos en el BOM. |
| Cuerpo de `POST /questions` | `{statement, options[{key,text}], correctKey, justification}` | El PDF no define el JSON. |
| Reglas de validación | 4 opciones exactas; cada opción con clave y texto, sin claves repetidas; `correctKey` ∈ claves (comparación exacta, sensible a mayúsculas); justificación obligatoria de **20 a 500 caracteres**; enunciado obligatorio | El PDF dice "justificación válida" sin criterios; se fijaron reglas pedagógicas mínimas. Constantes en `QuestionValidator`. |
| Respuesta de Validation ante error | **HTTP 400** con `{valid:false, errors:[…]}` (200 `{valid:true}` si es válida) | El PDF exige 200 en éxito; 400 en fallo mantiene coherencia REST. Question Service interpreta el *status* (el `RestTemplate` no lanza excepción en 4xx). |
| Servicios caídos | Validation caído → **503**; RabbitMQ caído → **503** (timeout de conexión 5 s) | Evita devolver 500 genéricos o colgarse 60 s. |
| Estado en Lifecycle | Almacén **en memoria** (`ConcurrentHashMap`); una pregunta desconocida se considera `DRAFT`; reenviar el comando es **idempotente** | El PDF asigna el estado a Lifecycle pero no pide base de datos allí. Consecuencia: el estado de Lifecycle se pierde al reiniciarlo. |
| Dos fuentes de `status` | Question Service guarda `DRAFT` y **no** se actualiza al pasar a `PENDING_REVIEW`; el estado vigente se consulta en `GET :8084/lifecycle/questions/{id}` | Lifecycle es la autoridad centralizada; actualizar de vuelta exigiría un evento de retorno que el PDF no pide. Es una limitación consciente. |
| Contrato del comando | Cada servicio tiene su propia copia de `SubmitQuestionForReview`; el contrato es el JSON. El tipo se infiere del parámetro del `@RabbitListener` | Los microservicios no comparten código; evita fallo por el *header* `__TypeId__`. |
| Topología AMQP | Exchange, cola y binding declarados en **ambos** servicios (idempotente) | El mensaje no se pierde si Question publica antes de que Lifecycle arranque. |
| Mensajes fallidos | `default-requeue-rejected=false` en Lifecycle | Evita bucle infinito con un mensaje defectuoso (no hay DLQ). |
| H2 | En memoria, `ddl-auto=update`, consola habilitada | Basta para el parcial; no hay persistencia entre reinicios. Para archivo: `jdbc:h2:file:./data/questiondb`. |
| Nombres de columna | `option_key`, `option_text` | `KEY` es palabra reservada en H2. |
| Extras | `GET /questions/{id}`, `GET /lifecycle/questions[/{id}]`, solicitudes E1–E4 | Permiten verificar persistencia y la transición sin acceder a la base. |
