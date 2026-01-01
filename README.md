# Sistema de Procesamiento por Lotes (Spring Batch) — Departamento de Misterios

## Integrantes del grupo (obligatorio)

> Sustituir por los nombres reales (misma entrega para todos los miembros).

- Integrante 1: **NOMBRE APELLIDOS**
- Integrante 2: **NOMBRE APELLIDOS**
- (Opcional) Integrante 3: **NOMBRE APELLIDOS**

## Resumen (qué resuelve el proyecto)

Aplicación **Spring Boot 3.2 + Spring Batch** que procesa datos mágicos en lote (hechizos, artefactos y registros mágicos) desde una base **H2 en memoria**, aplicando validaciones, cambios de estado y persistencia; expone endpoints REST para **crear/consultar datos** y **lanzar jobs** manualmente, y endpoints Actuator para **monitorización**.

## Lógica de la solución (en una idea)

1. Se almacenan entidades mágicas en BBDD con un criterio de “pendiente”.
2. Un Job de Spring Batch ejecuta Steps por **chunks de 10**: `ItemReader` (lee pendientes) → `ItemProcessor` (valida/transforma) → `ItemWriter` (marca y guarda).
3. Listeners registran logs de **Job / Step / Chunk** (duración, contadores y errores).

## Archivos relevantes (1 línea por archivo)

> Solo se listan los necesarios para entender la solución.

- `pom.xml`: dependencias (Web, JPA, Batch, Actuator, H2, Lombok) y Java 17.
- `src/main/resources/application.yml`: configuración de H2, JPA, Batch (tablas `BATCH_*`), Actuator y logging.
- `src/main/java/com/ministerio/magia/DepartamentoMisteriosApplication.java`: arranque de Spring Boot (tiene `@EnableScheduling`, sin tareas programadas actualmente).
- `src/main/java/com/ministerio/magia/config/BatchConfig.java`: definición de **Jobs** y **Steps**, chunk(10), tolerancia a fallos (retry/skip) y listeners.
- `src/main/java/com/ministerio/magia/config/DataInitializer.java`: carga datos de ejemplo **automáticamente al arrancar**.
- `src/main/java/com/ministerio/magia/controller/JobController.java`: endpoints REST para lanzar jobs.
- `src/main/java/com/ministerio/magia/controller/DatosMagicosController.java`: endpoints REST para inicializar, crear y consultar “pendientes”.
- `src/main/java/com/ministerio/magia/service/JobService.java`: `JobLauncher.run(...)` con parámetro `time` (cada llamada crea una instancia nueva).
- `src/main/java/com/ministerio/magia/service/DatosMagicosService.java`: creación/consulta de entidades y seeding.
- `src/main/java/com/ministerio/magia/batch/reader/*`: readers que consultan pendientes y los iteran.
- `src/main/java/com/ministerio/magia/batch/processor/*`: validación + cambio de estado intermedio.
- `src/main/java/com/ministerio/magia/batch/writer/*`: marcado final + persistencia.
- `src/main/java/com/ministerio/magia/batch/listener/*`: logs por job/step/chunk.
- `src/main/java/com/ministerio/magia/model/*`: entidades JPA (`Hechizo`, `Artefacto`, `RegistroMagico`).
- `src/main/java/com/ministerio/magia/repository/*`: consultas JPA para “pendientes”.
- `GUIA_VERIFICACION.md`: checklist práctica para validar métricas, logs y elementos visuales.

## Arquitectura (diagrama)

```mermaid
flowchart LR
  subgraph API[REST API]
    DC[DatosMagicosController]
    JC[JobController]
  end

  subgraph APP[Servicios]
    DS[DatosMagicosService]
    JS[JobService]
  end

  subgraph BATCH[Spring Batch]
    J[Job: procesarDatosMagicosJob]
    S1[Step: procesarHechizosStep]
    S2[Step: procesarArtefactosStep]
    S3[Step: procesarRegistrosMagicosStep]
    R[ItemReader]
    P[ItemProcessor]
    W[ItemWriter]
    L[Listeners\n(Job/Step/Chunk)]
  end

  subgraph DB[(H2 + JPA)]
    T1[hechizos]
    T2[artefactos]
    T3[registros_magicos]
    TB[BATCH_* (JobRepository)]
  end

  DC --> DS --> DB
  JC --> JS --> BATCH
  J --> S1 --> R --> P --> W --> DB
  S1 --> L
  J --> S2 --> R
  J --> S3 --> R
  BATCH --> TB
```

## Qué se considera “pendiente” (reglas reales del código)

- **Hechizos**: `estado = 'ACTIVO'` y `procesado = false` (`HechizoRepository.findHechizosPendientes()`).
- **Artefactos**: `estado = 'REGISTRADO'` y `procesado = false` (`ArtefactoRepository.findArtefactosPendientes()`).
- **Registros mágicos**: `estado = 'PENDIENTE'` (`RegistroMagicoRepository.findRegistrosPendientes()`).

## Jobs y Steps implementados

Definidos en `BatchConfig.java`:

- **Job completo**: `procesarDatosMagicosJob`
  - ejecuta `procesarHechizosStep` → `procesarArtefactosStep` → `procesarRegistrosMagicosStep`
- **Jobs individuales**:
  - `procesarHechizosJob`
  - `procesarArtefactosJob`
  - `procesarRegistrosMagicosJob`

Todos los steps usan:

- **Chunk**: 10 ítems por transacción (`chunk(10, transactionManager)`).
- **Tolerancia a fallos**: `faultTolerant()`, `retryLimit(3)`, `retry(Exception.class)`, `skip(Exception.class)`, `skipLimit(100)`.
- **Listeners**: `CustomStepExecutionListener` + `CustomChunkListener` (por step) y `JobCompletionListener` (por job).

## Manejo de errores y reintentos (cómo funciona aquí)

- Si un `ItemProcessor` lanza excepción (p.ej. nivel fuera de 0–100, registro sin datos o tipo inválido), el step aplica:
  - **reintentos** hasta 3
  - si sigue fallando, **omite** el ítem (hasta 100 omisiones por step)
  - el job continúa con el siguiente ítem/chunk/step (según el caso)
- Los listeners dejan trazas de errores en logs (y contadores de skip/failures).

## Reanudación de trabajos (matiz importante)

El proyecto **sí** inicializa el esquema de Spring Batch y guarda ejecuciones en `BATCH_*` (JobRepository).  
Sin embargo, la API lanza siempre el job con un parámetro `time` único (`JobService`), por lo que **cada ejecución crea una instancia nueva**. Para “reanudar” exactamente una ejecución fallida (restart del mismo `JobInstance`), haría falta ejecutar con **los mismos parámetros** (no hay endpoint específico en esta entrega).

## Concurrencia / rendimiento

- Se procesa en **chunks de 10** para mejorar rendimiento y reducir overhead de transacciones.
- Existe un `TaskExecutor` en `config/DatabaseConfig.java` (concurrencyLimit=5), pero **no está cableado a los steps**, así que el batch actual se ejecuta **en un solo hilo**.

## Datos de ejemplo (seeding)

- Al arrancar la app, `DataInitializer` ejecuta `DatosMagicosService.inicializarDatosEjemplo()`.
- Además, existe un endpoint manual para inicializar (`POST /api/datos-magicos/inicializar`).  
  Nota: ambas vías insertan datos “a pelo” (no idempotente), por lo que repetirlo generará **duplicados**.

## Requisitos

- Java 17+
- Maven 3.6+

## Ejecutar

```bash
mvn clean test
mvn spring-boot:run
```

### URLs útiles

- H2 Console: `http://localhost:8080/h2-console` (JDBC: `jdbc:h2:mem:magia_db`, usuario `sa`, contraseña vacía)
- Actuator base: `http://localhost:8080/actuator`

## API REST (ejemplos)

### Crear datos

- Crear hechizo:

```bash
curl -X POST http://localhost:8080/api/datos-magicos/hechizos \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Stupefy","tipo":"Ofensivo","nivelPoder":55,"descripcion":"Aturde al objetivo"}'
```

- Crear artefacto:

```bash
curl -X POST http://localhost:8080/api/datos-magicos/artefactos \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Mapa del Merodeador","categoria":"Mapa","nivelMagia":60,"ubicacion":"Hogwarts"}'
```

- Crear registro mágico:

```bash
curl -X POST http://localhost:8080/api/datos-magicos/registros \
  -H "Content-Type: application/json" \
  -d '{"tipoRegistro":"EVENTO","identificador":"EVT-999","datos":"Evento de prueba"}'
```

### Consultar pendientes

```bash
curl http://localhost:8080/api/datos-magicos/hechizos/pendientes
curl http://localhost:8080/api/datos-magicos/artefactos/pendientes
curl http://localhost:8080/api/datos-magicos/registros/pendientes
```

### Lanzar jobs

```bash
curl -X POST http://localhost:8080/api/jobs/procesar-todos
curl -X POST http://localhost:8080/api/jobs/procesar-hechizos
curl -X POST http://localhost:8080/api/jobs/procesar-artefactos
curl -X POST http://localhost:8080/api/jobs/procesar-registros
```

## Monitorización (Actuator)

Configurado en `application.yml` (expuestos):

- Health: `http://localhost:8080/actuator/health`
- Metrics: `http://localhost:8080/actuator/metrics`
- Batch Jobs: `http://localhost:8080/actuator/batch-jobs`
- Batch Job Executions: `http://localhost:8080/actuator/batch-job-executions`

Nota: en `application.yml` se habilita export Prometheus, pero **no** hay dependencia de registry en `pom.xml`. Si se quiere `/actuator/prometheus`, habría que añadir `micrometer-registry-prometheus`.

## Elementos visuales (tabla, gráfico, diagrama)

- **Tabla (trabajos por lotes y estado)**: consultar `BATCH_*` en H2.
- **Gráfico de rendimiento “en tiempo real”**: a partir de `/actuator/metrics` (y opcionalmente Prometheus/Grafana si se añade registry).
- **Diagrama de arquitectura**: incluido arriba (Mermaid).

Ejemplo SQL (tabla de ejecuciones):

```sql
SELECT
  JI.JOB_NAME AS JOB_NAME,
  JE.JOB_EXECUTION_ID AS EXECUTION_ID,
  JE.STATUS AS STATUS,
  JE.START_TIME AS START_TIME,
  JE.END_TIME AS END_TIME
FROM BATCH_JOB_INSTANCE JI
JOIN BATCH_JOB_EXECUTION JE ON JI.JOB_INSTANCE_ID = JE.JOB_INSTANCE_ID
ORDER BY JE.START_TIME DESC;
```

## Entrega (cómo preparar el ZIP)

1. Completar “Integrantes del grupo” en este `README.md`.
2. Comprimir el proyecto (misma carpeta/archivos para todos) en un único `.zip`/`.rar`.
3. Incluir `README.md` + `GUIA_VERIFICACION.md` + el código fuente.

## Referencias

- [Documentación Spring Batch](https://docs.spring.io/spring-batch/docs/current/reference/html/)
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)
