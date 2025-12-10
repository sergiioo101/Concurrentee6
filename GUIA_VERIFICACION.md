# Guía de Verificación - Sistema de Procesamiento por Lotes

## 📋 Requisitos del Enunciado a Verificar

Según el enunciado, debes verificar:

1. **Métricas de rendimiento**: Tiempo de respuesta medio, tasa de procesamiento de datos por segundo
2. **Criterios de éxito**: 
   - Sistema funcionando en tiempo real sin caídas
   - Datos procesados correctamente
   - Reanudación de trabajos fallidos efectiva
3. **Elementos visuales**: 
   - Tabla de trabajos por lotes procesados y su estado actual
   - Gráfico de rendimiento del sistema en tiempo real
   - Diagrama de arquitectura del sistema

---

## 🚀 PASO 1: Iniciar la Aplicación

### Comando:
```bash
mvn spring-boot:run
```

### Salida Esperada:
```
Started DepartamentoMisteriosApplication in X.XXX seconds
Tomcat started on port(s): 8080 (http)
H2 console available at '/h2-console'
```

✅ **Verificación**: Si ves "Started DepartamentoMisteriosApplication", la aplicación está funcionando.

---

## 📊 PASO 2: Verificar Métricas de Rendimiento

### 2.1. Inicializar Datos de Ejemplo

**Comando (PowerShell):**
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/datos-magicos/inicializar" -Method POST
```

**O usando curl:**
```bash
curl -X POST http://localhost:8080/api/datos-magicos/inicializar
```

**Salida Esperada:**
```json
{
  "mensaje": "Datos de ejemplo inicializados correctamente"
}
```

### 2.2. Ejecutar un Job y Medir el Tiempo

**Comando (PowerShell):**
```powershell
$startTime = Get-Date
Invoke-RestMethod -Uri "http://localhost:8080/api/jobs/procesar-hechizos" -Method POST
$endTime = Get-Date
$duration = ($endTime - $startTime).TotalSeconds
Write-Host "Tiempo de ejecución: $duration segundos"
```

**Salida Esperada:**
```json
{
  "mensaje": "Job de procesamiento de hechizos iniciado",
  "jobId": 1,
  "jobName": "procesarHechizosJob",
  "status": "STARTED"
}
```

### 2.3. Consultar Métricas de Actuator

**URL en el navegador:**
```
http://localhost:8080/actuator/metrics
```

**O con PowerShell:**
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/actuator/metrics" | ConvertTo-Json
```

**Salida Esperada:** Lista de métricas disponibles incluyendo:
- `spring.batch.job.execution.duration`
- `spring.batch.job.execution.count`
- `spring.batch.step.execution.duration`

### 2.4. Ver Métricas Específicas de Batch

**URL:**
```
http://localhost:8080/actuator/metrics/spring.batch.job.execution.duration
```

**O consultar ejecuciones de jobs:**
```
http://localhost:8080/actuator/batch-job-executions
```

**Salida Esperada:**
```json
{
  "executions": [
    {
      "jobExecutionId": 1,
      "jobName": "procesarHechizosJob",
      "status": "COMPLETED",
      "startTime": "2025-12-10T...",
      "endTime": "2025-12-10T...",
      "duration": 2.5
    }
  ]
}
```

✅ **Verificación**: 
- Tiempo de respuesta: Debe aparecer en los logs y en Actuator
- Tasa de procesamiento: Calcula `items procesados / tiempo en segundos`

---

## ✅ PASO 3: Verificar Criterios de Éxito

### 3.1. Sistema Funcionando Sin Caídas

**Verificar Health:**
```
http://localhost:8080/actuator/health
```

**Salida Esperada:**
```json
{
  "status": "UP",
  "components": {
    "db": {"status": "UP"},
    "diskSpace": {"status": "UP"}
  }
}
```

✅ **Verificación**: Status debe ser "UP"

### 3.2. Datos Procesados Correctamente

**Paso 1: Ver datos pendientes ANTES del procesamiento:**
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/datos-magicos/hechizos/pendientes" | ConvertTo-Json
```

**Salida Esperada:** Lista de hechizos con `procesado: false`

**Paso 2: Ejecutar el job:**
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/jobs/procesar-hechizos" -Method POST
```

**Paso 3: Ver datos DESPUÉS del procesamiento:**
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/datos-magicos/hechizos/pendientes" | ConvertTo-Json
```

**Salida Esperada:** Lista vacía `[]` (todos procesados)

**Paso 4: Verificar en H2 Console:**
1. Abrir: `http://localhost:8080/h2-console`
2. JDBC URL: `jdbc:h2:mem:magia_db`
3. Usuario: `sa`
4. Contraseña: (vacía)
5. Ejecutar: `SELECT * FROM hechizos WHERE procesado = true;`

✅ **Verificación**: Todos los hechizos deben tener `procesado = true` y `estado = 'ACTIVO'`

### 3.3. Reanudación de Trabajos Fallidos

**Nota:** Para probar la reanudación, necesitarías simular un fallo. Por ahora, verifica que el JobRepository esté configurado:

**Verificar tablas de Spring Batch:**
En H2 Console, ejecutar:
```sql
SELECT * FROM BATCH_JOB_INSTANCE;
SELECT * FROM BATCH_JOB_EXECUTION;
SELECT * FROM BATCH_STEP_EXECUTION;
```

✅ **Verificación**: Las tablas deben existir y contener registros de ejecuciones.

---

## 📈 PASO 4: Verificar Logs y Métricas en Consola

### 4.1. Observar Logs Durante la Ejecución

Cuando ejecutas un job, deberías ver en la consola:

```
=========================================
Iniciando Job: procesarHechizosJob
Job ID: 1
Fecha de inicio: 2025-12-10T...
=========================================
Iniciando Step: procesarHechizosStep
Items leídos: 5
Items escritos: 5
Duración: X segundos
=========================================
Finalizando Job: procesarHechizosJob
Estado: COMPLETED
Duración: X segundos
=========================================
```

✅ **Verificación**: Los logs muestran inicio, procesamiento y finalización correcta.

---

## 🎯 PASO 5: Script de Verificación Completa

Crea un archivo `verificar.ps1` con este contenido:

```powershell
Write-Host "=== VERIFICACIÓN DEL SISTEMA ===" -ForegroundColor Green

# 1. Verificar que la aplicación está corriendo
Write-Host "`n1. Verificando Health..." -ForegroundColor Yellow
$health = Invoke-RestMethod -Uri "http://localhost:8080/actuator/health"
if ($health.status -eq "UP") {
    Write-Host "✅ Sistema funcionando correctamente" -ForegroundColor Green
} else {
    Write-Host "❌ Sistema no está funcionando" -ForegroundColor Red
    exit
}

# 2. Inicializar datos
Write-Host "`n2. Inicializando datos..." -ForegroundColor Yellow
Invoke-RestMethod -Uri "http://localhost:8080/api/datos-magicos/inicializar" -Method POST | Out-Null
Write-Host "✅ Datos inicializados" -ForegroundColor Green

# 3. Ver datos pendientes antes
Write-Host "`n3. Datos pendientes ANTES del procesamiento:" -ForegroundColor Yellow
$pendientesAntes = Invoke-RestMethod -Uri "http://localhost:8080/api/datos-magicos/hechizos/pendientes"
Write-Host "Hechizos pendientes: $($pendientesAntes.Count)" -ForegroundColor Cyan

# 4. Ejecutar job y medir tiempo
Write-Host "`n4. Ejecutando job..." -ForegroundColor Yellow
$startTime = Get-Date
$jobResult = Invoke-RestMethod -Uri "http://localhost:8080/api/jobs/procesar-hechizos" -Method POST
$endTime = Get-Date
$duration = ($endTime - $startTime).TotalSeconds
Write-Host "✅ Job ejecutado en $duration segundos" -ForegroundColor Green
Write-Host "Job ID: $($jobResult.jobId)" -ForegroundColor Cyan
Write-Host "Status: $($jobResult.status)" -ForegroundColor Cyan

# Esperar a que termine el job
Start-Sleep -Seconds 3

# 5. Ver datos pendientes después
Write-Host "`n5. Datos pendientes DESPUÉS del procesamiento:" -ForegroundColor Yellow
$pendientesDespues = Invoke-RestMethod -Uri "http://localhost:8080/api/datos-magicos/hechizos/pendientes"
Write-Host "Hechizos pendientes: $($pendientesDespues.Count)" -ForegroundColor Cyan

if ($pendientesDespues.Count -eq 0) {
    Write-Host "✅ Todos los datos fueron procesados correctamente" -ForegroundColor Green
} else {
    Write-Host "❌ Aún hay datos pendientes" -ForegroundColor Red
}

# 6. Ver métricas
Write-Host "`n6. Consultando métricas..." -ForegroundColor Yellow
$metrics = Invoke-RestMethod -Uri "http://localhost:8080/actuator/metrics"
Write-Host "✅ Métricas disponibles: $($metrics.names.Count) métricas" -ForegroundColor Green

# 7. Ver ejecuciones de jobs
Write-Host "`n7. Ejecuciones de jobs:" -ForegroundColor Yellow
try {
    $executions = Invoke-RestMethod -Uri "http://localhost:8080/actuator/batch-job-executions"
    Write-Host "✅ Total de ejecuciones: $($executions.executions.Count)" -ForegroundColor Green
    foreach ($exec in $executions.executions) {
        Write-Host "  - Job: $($exec.jobName), Status: $($exec.status)" -ForegroundColor Cyan
    }
} catch {
    Write-Host "⚠️  Endpoint de ejecuciones no disponible" -ForegroundColor Yellow
}

Write-Host "`n=== VERIFICACIÓN COMPLETA ===" -ForegroundColor Green
```

**Ejecutar:**
```powershell
.\verificar.ps1
```

---

## 📊 PASO 6: Verificar Tabla de Trabajos (Elemento Visual)

### Opción 1: Usar H2 Console

1. Abrir: `http://localhost:8080/h2-console`
2. Conectarse a la base de datos
3. Ejecutar:
```sql
SELECT 
    JI.JOB_NAME as "Nombre Job",
    JE.JOB_EXECUTION_ID as "ID Ejecución",
    JE.STATUS as "Estado",
    JE.START_TIME as "Inicio",
    JE.END_TIME as "Fin",
    TIMESTAMPDIFF('SECOND', JE.START_TIME, JE.END_TIME) as "Duración (seg)"
FROM BATCH_JOB_INSTANCE JI
JOIN BATCH_JOB_EXECUTION JE ON JI.JOB_INSTANCE_ID = JE.JOB_INSTANCE_ID
ORDER BY JE.START_TIME DESC;
```

### Opción 2: Usar Actuator

```
http://localhost:8080/actuator/batch-jobs
```

---

## 📝 RESUMEN: Qué Verificar

### ✅ Métricas de Rendimiento
- [ ] Tiempo de respuesta medio visible en logs y Actuator
- [ ] Tasa de procesamiento calculable (items/segundo)
- [ ] Métricas disponibles en `/actuator/metrics`

### ✅ Criterios de Éxito
- [ ] Sistema funcionando: `/actuator/health` muestra "UP"
- [ ] Datos procesados: Antes hay pendientes, después no hay
- [ ] Reanudación: Tablas BATCH_* existen y tienen datos

### ✅ Elementos Visuales
- [ ] Tabla de trabajos: Consultar en H2 o Actuator
- [ ] Logs detallados: Visible en consola durante ejecución
- [ ] Diagrama de arquitectura: Documentado en README.md

---

## 🎯 Salida Final Esperada

Al completar todas las verificaciones, deberías tener:

1. **Logs en consola** mostrando:
   - Inicio y fin de jobs
   - Items procesados
   - Tiempo de ejecución

2. **Datos en base de datos**:
   - Hechizos/Artefactos/Registros con `procesado = true`
   - Registros en tablas BATCH_*

3. **Métricas en Actuator**:
   - Health: UP
   - Métricas de batch disponibles
   - Ejecuciones de jobs registradas

4. **Respuestas de API**:
   - Jobs ejecutados exitosamente
   - Datos pendientes se reducen a 0 después del procesamiento

