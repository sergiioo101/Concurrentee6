Write-Host "=== VERIFICACIÓN DEL SISTEMA ===" -ForegroundColor Green

# 1. Verificar que la aplicación está corriendo
Write-Host "`n1. Verificando Health..." -ForegroundColor Yellow
try {
    $health = Invoke-RestMethod -Uri "http://localhost:8080/actuator/health"
    if ($health.status -eq "UP") {
        Write-Host "✅ Sistema funcionando correctamente" -ForegroundColor Green
    } else {
        Write-Host "❌ Sistema no está funcionando" -ForegroundColor Red
        exit
    }
} catch {
    Write-Host "❌ No se puede conectar a la aplicación. ¿Está corriendo?" -ForegroundColor Red
    exit
}

# 2. Inicializar datos
Write-Host "`n2. Inicializando datos..." -ForegroundColor Yellow
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/datos-magicos/inicializar" -Method POST | Out-Null
    Write-Host "✅ Datos inicializados" -ForegroundColor Green
} catch {
    Write-Host "⚠️  Error al inicializar datos" -ForegroundColor Yellow
}

# 3. Ver datos pendientes antes
Write-Host "`n3. Datos pendientes ANTES del procesamiento:" -ForegroundColor Yellow
try {
    $pendientesAntes = Invoke-RestMethod -Uri "http://localhost:8080/api/datos-magicos/hechizos/pendientes"
    Write-Host "Hechizos pendientes: $($pendientesAntes.Count)" -ForegroundColor Cyan
} catch {
    Write-Host "⚠️  Error al consultar datos pendientes" -ForegroundColor Yellow
}

# 4. Ejecutar job y medir tiempo
Write-Host "`n4. Ejecutando job..." -ForegroundColor Yellow
try {
    $startTime = Get-Date
    $jobResult = Invoke-RestMethod -Uri "http://localhost:8080/api/jobs/procesar-hechizos" -Method POST
    $endTime = Get-Date
    $duration = ($endTime - $startTime).TotalSeconds
    Write-Host "✅ Job ejecutado en $duration segundos" -ForegroundColor Green
    Write-Host "Job ID: $($jobResult.jobId)" -ForegroundColor Cyan
    Write-Host "Status: $($jobResult.status)" -ForegroundColor Cyan
    
    # Esperar a que termine el job
    Write-Host "Esperando a que termine el procesamiento..." -ForegroundColor Yellow
    Start-Sleep -Seconds 5
} catch {
    Write-Host "⚠️  Error al ejecutar job" -ForegroundColor Yellow
}

# 5. Ver datos pendientes después
Write-Host "`n5. Datos pendientes DESPUÉS del procesamiento:" -ForegroundColor Yellow
try {
    $pendientesDespues = Invoke-RestMethod -Uri "http://localhost:8080/api/datos-magicos/hechizos/pendientes"
    Write-Host "Hechizos pendientes: $($pendientesDespues.Count)" -ForegroundColor Cyan
    
    if ($pendientesDespues.Count -eq 0) {
        Write-Host "✅ Todos los datos fueron procesados correctamente" -ForegroundColor Green
    } else {
        Write-Host "⚠️  Aún hay $($pendientesDespues.Count) datos pendientes" -ForegroundColor Yellow
    }
} catch {
    Write-Host "⚠️  Error al consultar datos después" -ForegroundColor Yellow
}

# 6. Ver métricas
Write-Host "`n6. Consultando métricas..." -ForegroundColor Yellow
try {
    $metrics = Invoke-RestMethod -Uri "http://localhost:8080/actuator/metrics"
    Write-Host "✅ Métricas disponibles: $($metrics.names.Count) métricas" -ForegroundColor Green
    Write-Host "Algunas métricas:" -ForegroundColor Cyan
    $metrics.names | Select-Object -First 10 | ForEach-Object { Write-Host "  - $_" }
} catch {
    Write-Host "⚠️  Error al consultar métricas" -ForegroundColor Yellow
}

# 7. Ver ejecuciones de jobs
Write-Host "`n7. Ejecuciones de jobs:" -ForegroundColor Yellow
try {
    $executions = Invoke-RestMethod -Uri "http://localhost:8080/actuator/batch-job-executions"
    if ($executions.executions) {
        Write-Host "✅ Total de ejecuciones: $($executions.executions.Count)" -ForegroundColor Green
        foreach ($exec in $executions.executions) {
            Write-Host "  - Job: $($exec.jobName), Status: $($exec.status)" -ForegroundColor Cyan
        }
    } else {
        Write-Host "⚠️  No hay ejecuciones registradas aún" -ForegroundColor Yellow
    }
} catch {
    Write-Host "⚠️  Endpoint de ejecuciones no disponible o aún no hay datos" -ForegroundColor Yellow
}

Write-Host "`n=== VERIFICACIÓN COMPLETA ===" -ForegroundColor Green
Write-Host "`nPara más detalles, consulta:" -ForegroundColor Cyan
Write-Host "  - Health: http://localhost:8080/actuator/health" -ForegroundColor White
Write-Host "  - Métricas: http://localhost:8080/actuator/metrics" -ForegroundColor White
Write-Host "  - H2 Console: http://localhost:8080/h2-console" -ForegroundColor White

