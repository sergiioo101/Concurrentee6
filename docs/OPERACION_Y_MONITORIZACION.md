# Operación y monitorización — Guía práctica

## 1) Arranque

```bash
mvn clean test
mvn spring-boot:run
```

### Servicios disponibles

- Aplicación: `http://localhost:8080`
- H2 Console: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:magia_db`
  - Usuario: `sa`
  - Contraseña: (vacía)
- Actuator: `http://localhost:8080/actuator`

## 2) Flujo típico de uso (manual)

1. (Opcional) Inicializar datos:
   - Nota: el proyecto ya inicializa datos al arrancar vía `DataInitializer`. El endpoint añade más datos (no idempotente).

```bash
curl -X POST http://localhost:8080/api/datos-magicos/inicializar
```

2. Consultar “pendientes”:

```bash
curl http://localhost:8080/api/datos-magicos/hechizos/pendientes
curl http://localhost:8080/api/datos-magicos/artefactos/pendientes
curl http://localhost:8080/api/datos-magicos/registros/pendientes
```

3. Lanzar jobs:

```bash
curl -X POST http://localhost:8080/api/jobs/procesar-todos
```

o por tipo:

```bash
curl -X POST http://localhost:8080/api/jobs/procesar-hechizos
curl -X POST http://localhost:8080/api/jobs/procesar-artefactos
curl -X POST http://localhost:8080/api/jobs/procesar-registros
```

4. Repetir consulta de “pendientes” (deberían reducirse).

## 3) Métricas de rendimiento (cómo calcularlas)

### Tiempo de respuesta (latencia) de lanzamiento

El endpoint de job devuelve rápido (lanza el job con `JobLauncher`). Mide el tiempo del HTTP:

```bash
time curl -s -o /dev/null -X POST http://localhost:8080/api/jobs/procesar-hechizos
```

### Tiempo real de ejecución del batch

Se observa:

- en logs (JobCompletionListener / StepExecutionListener)
- y en tablas `BATCH_*` (campos `START_TIME`, `END_TIME`)

### Tasa de procesamiento (items/segundo)

Una aproximación sencilla por step:

\[
\text{items/seg} \approx \frac{\text{writeCount}}{\text{duración(seg)}}
\]

`writeCount` se ve en logs del step (`Items escritos`) y/o en `BATCH_STEP_EXECUTION.WRITE_COUNT`.

## 4) Monitorización (Actuator)

En `application.yml` se exponen:

- `GET /actuator/health`
- `GET /actuator/metrics`
- `GET /actuator/batch-jobs`
- `GET /actuator/batch-job-executions`

Ejemplos:

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/metrics
curl http://localhost:8080/actuator/batch-job-executions
```

### Nota sobre Prometheus

Aunque hay configuración `management.metrics.export.prometheus.enabled=true`, el proyecto no incluye el registry Prometheus en `pom.xml`. Para habilitar `/actuator/prometheus` habría que añadir `micrometer-registry-prometheus`.

## 5) “Elementos visuales” del enunciado

### 5.1 Tabla de trabajos por lotes y estado

Ejecutar en H2 Console:

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

### 5.2 Tabla por steps (más detalle)

```sql
SELECT
  SE.STEP_NAME,
  SE.STATUS,
  SE.READ_COUNT,
  SE.WRITE_COUNT,
  SE.SKIP_COUNT,
  SE.START_TIME,
  SE.END_TIME
FROM BATCH_STEP_EXECUTION SE
ORDER BY SE.START_TIME DESC;
```

### 5.3 Gráfico de rendimiento “en tiempo real”

Opciones válidas para una entrega académica:

- Captura de pantalla de `/actuator/metrics` (lista y alguna métrica concreta).
- Captura de pantalla de una tabla H2 (evolución de ejecuciones).
- (Opcional) Si se añade Prometheus/Grafana: dashboard con `items/seg` y duraciones.

## 6) Logs (qué buscar)

- Inicio/fin del job con duración y estado: `JobCompletionListener`.
- Métricas por step: `CustomStepExecutionListener`.
- Información por chunk (debug): `CustomChunkListener`.

Si hay errores de validación:

- aparecerán `WARN/ERROR` en el processor
- y contadores de skip/failures en el step.

