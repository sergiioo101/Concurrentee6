# Arquitectura — Sistema de Procesamiento por Lotes (Departamento de Misterios)

## Objetivo

Procesar grandes volúmenes de datos mágicos de forma modular y robusta mediante **Spring Batch**, aplicando:

- **Procesamiento por lotes** con chunks.
- **Manejo de errores** con reintentos y omisiones controladas.
- **Trazabilidad** (logs por job/step/chunk).
- **Persistencia de ejecuciones** en tablas `BATCH_*` (JobRepository).

## Vista general (componentes)

- **API REST**
  - `DatosMagicosController`: alta y consulta de entidades “pendientes”.
  - `JobController`: lanzamiento manual de jobs.
- **Servicios**
  - `DatosMagicosService`: crea datos y define el seeding.
  - `JobService`: ejecuta jobs con `JobLauncher`.
- **Spring Batch**
  - `BatchConfig`: define jobs y steps, chunk size y tolerancia a fallos.
  - `ItemReader` / `ItemProcessor` / `ItemWriter` por tipo de entidad.
  - Listeners: `JobCompletionListener`, `CustomStepExecutionListener`, `CustomChunkListener`.
- **Persistencia**
  - H2 en memoria (`jdbc:h2:mem:magia_db`).
  - Tablas de dominio: `hechizos`, `artefactos`, `registros_magicos`.
  - Tablas batch: `BATCH_*`.

## Diagrama de arquitectura

```mermaid
flowchart TB
  U[Usuario] -->|HTTP| API[Spring MVC Controllers]
  API --> SVC[Services]
  SVC -->|JPA| DB[(H2)]

  API -->|POST /api/jobs/*| JL[JobLauncher]
  JL --> JOBS[Spring Batch Jobs]
  JOBS --> STEPS[Steps (chunk=10)]
  STEPS --> RW[Reader/Processor/Writer]
  RW -->|JPA| DB

  JOBS --> JR[JobRepository]
  JR --> BT[(BATCH_* tables)]
```

## Jobs/Steps (comportamiento real)

Definidos en `src/main/java/com/ministerio/magia/config/BatchConfig.java`:

- `procesarDatosMagicosJob` (job “orquestador”): ejecuta secuencialmente:
  - `procesarHechizosStep`
  - `procesarArtefactosStep`
  - `procesarRegistrosMagicosStep`
- `procesarHechizosJob`: ejecuta solo `procesarHechizosStep`
- `procesarArtefactosJob`: ejecuta solo `procesarArtefactosStep`
- `procesarRegistrosMagicosJob`: ejecuta solo `procesarRegistrosMagicosStep`

### Chunks y transacciones

Cada step procesa en **chunks de 10** ítems:

1. Lee hasta 10 ítems pendientes.
2. Procesa/valida cada ítem.
3. Escribe/persiste el chunk como unidad (misma transacción del step para ese chunk).

### Tolerancia a fallos (fault tolerant)

En cada step:

- `retryLimit(3)` + `retry(Exception.class)` → reintenta hasta 3 veces ante excepción.
- `skip(Exception.class)` + `skipLimit(100)` → si sigue fallando, omite el ítem (máximo 100 omisiones).

## Modelo de estados (dominio)

### Hechizo

- Pendiente si: `estado = ACTIVO` y `procesado = false`.
- Processor: valida `nivelPoder` (0..100); si OK, pasa a `EN_PROCESO`.
- Writer: marca `procesado = true` y `estado = ACTIVO`.

### Artefacto

- Pendiente si: `estado = REGISTRADO` y `procesado = false`.
- Processor: valida `nivelMagia` (0..100); si OK, pasa a `EN_ANALISIS`.
- Writer: marca `procesado = true` y `estado = PROCESADO`.

### RegistroMagico

- Pendiente si: `estado = PENDIENTE`.
- Processor: valida `datos` no vacío y `tipoRegistro` ∈ {`HECHIZO`,`ARTEFACTO`,`EVENTO`}; actualiza `fechaProcesamiento`.
- Writer: marca `estado = PROCESADO`.

## Observabilidad (logs)

- `JobCompletionListener`: inicio/fin de job, duración y status.
- `CustomStepExecutionListener`: métricas de step (read/write/skip/failures) y duración.
- `CustomChunkListener`: logs por chunk (before/after/error).

## Limitaciones conocidas (documentadas)

- `@EnableScheduling` está habilitado pero no hay `@Scheduled` en el proyecto.
- Existe un `TaskExecutor` (`DatabaseConfig.java`) pero no se usa en los steps (no hay ejecución multi-hilo).
- La API lanza jobs con parámetro `time` siempre distinto → cada ejecución crea un `JobInstance` nuevo (no hay endpoint de “restart con mismos parámetros”).

