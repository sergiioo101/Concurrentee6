# Sistema de Procesamiento por Lotes - Departamento de Misterios

## Integrantes del Grupo
#### Mario LLansó
### Miguel de Dios
### Sergio Martín

## Descripción del Proyecto

Este proyecto implementa un sistema avanzado de procesamiento por lotes utilizando Spring Batch para el Departamento de Misterios del Ministerio de Magia. El sistema es capaz de procesar grandes volúmenes de datos mágicos (hechizos, artefactos y registros mágicos) de manera eficiente, con soporte para reanudación de trabajos fallidos, manejo de errores y monitorización en tiempo real.

## Arquitectura de la Solución

El sistema está construido siguiendo los principios de Inversión de Control (IoC) y separación de responsabilidades, utilizando Spring Batch para el procesamiento por lotes.

### Componentes Principales

#### 1. **Modelos de Datos** (`model/`)
- `Hechizo.java`: Representa un hechizo mágico con nombre, tipo, nivel de poder y estado.
- `Artefacto.java`: Representa un artefacto mágico con categoría, nivel de magia y ubicación.
- `RegistroMagico.java`: Representa un registro genérico de datos mágicos con tipo, identificador y datos.

#### 2. **Repositorios** (`repository/`)
- `HechizoRepository.java`: Interfaz JPA para acceso a datos de hechizos.
- `ArtefactoRepository.java`: Interfaz JPA para acceso a datos de artefactos.
- `RegistroMagicoRepository.java`: Interfaz JPA para acceso a datos de registros mágicos.

#### 3. **Componentes de Spring Batch** (`batch/`)

**Readers** (`batch/reader/`):
- `HechizoItemReader.java`: Lee hechizos pendientes de procesamiento desde la base de datos.
- `ArtefactoItemReader.java`: Lee artefactos pendientes de procesamiento desde la base de datos.
- `RegistroMagicoItemReader.java`: Lee registros mágicos pendientes de procesamiento desde la base de datos.

**Processors** (`batch/processor/`):
- `HechizoItemProcessor.java`: Procesa y valida hechizos, actualizando su estado.
- `ArtefactoItemProcessor.java`: Procesa y valida artefactos, actualizando su estado.
- `RegistroMagicoItemProcessor.java`: Procesa y valida registros mágicos, actualizando su fecha de procesamiento.

**Writers** (`batch/writer/`):
- `HechizoItemWriter.java`: Guarda los hechizos procesados en la base de datos.
- `ArtefactoItemWriter.java`: Guarda los artefactos procesados en la base de datos.
- `RegistroMagicoItemWriter.java`: Guarda los registros mágicos procesados en la base de datos.

**Listeners** (`batch/listener/`):
- `JobCompletionListener.java`: Escucha eventos de inicio y finalización de jobs, registrando métricas y duración.
- `StepExecutionListener.java`: Escucha eventos de steps, registrando items procesados, errores y duración.
- `ChunkListener.java`: Escucha eventos de chunks para monitoreo detallado del procesamiento.

#### 4. **Configuración** (`config/`)
- `BatchConfig.java`: Configura los Jobs y Steps de Spring Batch con procesamiento por chunks, manejo de errores y reintentos.
- `DatabaseConfig.java`: Configura el TaskExecutor para procesamiento concurrente.
- `DataInitializer.java`: Inicializa datos de ejemplo al arrancar la aplicación.

#### 5. **Servicios** (`service/`)
- `JobService.java`: Servicio para ejecutar los diferentes jobs de procesamiento por lotes.
- `DatosMagicosService.java`: Servicio para gestionar la creación y consulta de datos mágicos.

#### 6. **Controladores REST** (`controller/`)
- `JobController.java`: Endpoints REST para ejecutar jobs de procesamiento.
- `DatosMagicosController.java`: Endpoints REST para gestionar datos mágicos.

#### 7. **Configuración de Aplicación**
- `application.yml`: Configuración de Spring Boot, base de datos H2, Spring Batch, Actuator y logging.
- `pom.xml`: Dependencias Maven del proyecto (Spring Boot, Spring Batch, JPA, Actuator, H2, Lombok).

## Características Implementadas

### Procesamiento por Lotes
- Jobs configurables para procesar hechizos, artefactos y registros mágicos.
- Procesamiento por chunks (10 items por chunk) para optimizar el rendimiento.
- Jobs individuales y un job completo que procesa todos los tipos de datos.

### Reanudación de Trabajos
- Utiliza `JobRepository` de Spring Batch para almacenar el estado de los jobs.
- Los jobs pueden ser reanudados desde el punto de fallo.
- Configuración de reintentos (3 intentos) y límite de omisiones (100 items).

### Manejo de Errores
- Listeners para capturar y registrar errores.
- Configuración de `faultTolerant()` en los steps para manejar excepciones.
- Reintentos automáticos para errores transitorios.
- Omisión de items con error para continuar el procesamiento.

### Monitorización
- Spring Actuator configurado con endpoints de health, metrics y batch-jobs.
- Logging detallado en todos los niveles (Job, Step, Chunk).
- Métricas de rendimiento (tiempo de ejecución, items procesados, errores).

### Inversión de Control (IoC)
- Todos los componentes son beans de Spring gestionados por el contenedor.
- Inyección de dependencias mediante `@Autowired`.
- Configuración modular y desacoplada.

## Requisitos

- Java 17 o superior
- Maven 3.6 o superior
- Spring Boot 3.2.0
- H2 Database (incluida, en memoria)

## Instalación y Ejecución

1. **Clonar o descargar el proyecto**

2. **Compilar el proyecto:**
   ```bash
   mvn clean install
   ```

3. **Ejecutar la aplicación:**
   ```bash
   mvn spring-boot:run
   ```

4. **La aplicación estará disponible en:**
   - Puerto: 8080 (por defecto)
   - H2 Console: http://localhost:8080/h2-console
   - Actuator: http://localhost:8080/actuator

## Uso de la API

### Inicializar Datos de Ejemplo
```bash
POST http://localhost:8080/api/datos-magicos/inicializar
```

### Ejecutar Jobs de Procesamiento

**Procesar todos los datos:**
```bash
POST http://localhost:8080/api/jobs/procesar-todos
```

**Procesar solo hechizos:**
```bash
POST http://localhost:8080/api/jobs/procesar-hechizos
```

**Procesar solo artefactos:**
```bash
POST http://localhost:8080/api/jobs/procesar-artefactos
```

**Procesar solo registros mágicos:**
```bash
POST http://localhost:8080/api/jobs/procesar-registros
```

### Consultar Datos Pendientes

```bash
GET http://localhost:8080/api/datos-magicos/hechizos/pendientes
GET http://localhost:8080/api/datos-magicos/artefactos/pendientes
GET http://localhost:8080/api/datos-magicos/registros/pendientes
```

## Monitorización

### Endpoints de Actuator

- **Health:** http://localhost:8080/actuator/health
- **Metrics:** http://localhost:8080/actuator/metrics
- **Batch Jobs:** http://localhost:8080/actuator/batch-jobs
- **Batch Job Executions:** http://localhost:8080/actuator/batch-job-executions

### Logs

Los logs se muestran en consola con el siguiente formato:
- Nivel INFO: Información general de jobs y steps
- Nivel DEBUG: Información detallada de chunks y procesamiento
- Nivel ERROR: Errores y excepciones

## Estructura del Proyecto

```
Concurrente6/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/ministerio/magia/
│   │   │       ├── DepartamentoMisteriosApplication.java
│   │   │       ├── model/
│   │   │       ├── repository/
│   │   │       ├── batch/
│   │   │       │   ├── reader/
│   │   │       │   ├── processor/
│   │   │       │   ├── writer/
│   │   │       │   └── listener/
│   │   │       ├── config/
│   │   │       ├── service/
│   │   │       └── controller/
│   │   └── resources/
│   │       └── application.yml
│   └── test/
├── pom.xml
├── .gitignore
└── README.md
```

## Criterios de Éxito

1. Sistema funcionando en tiempo real sin caídas  
2. Datos procesados correctamente  
3. Reanudación de trabajos fallidos efectiva  
4. Manejo de errores robusto  
5. Monitorización completa mediante Actuator  
6. Logging eficiente para rastreo de eventos  

## Tecnologías Utilizadas

- **Spring Boot 3.2.0**: Framework principal
- **Spring Batch**: Procesamiento por lotes
- **Spring Data JPA**: Acceso a datos
- **Spring Actuator**: Monitorización
- **H2 Database**: Base de datos en memoria
- **Lombok**: Reducción de boilerplate
- **Maven**: Gestión de dependencias

graph TD
    User((Usuario)) -->|POST /procesar-todos| API[JobController]
    API -->|Ejecutar| Service[JobService]
    Service -->|Run| Launcher[JobLauncher]
    
    subgraph Spring Batch Context
        Launcher -->|Inicia| Job[ProcesarDatosMagicosJob]
        
        Job --> Step1[Step: Hechizos]
        Job --> Step2[Step: Artefactos]
        Job --> Step3[Step: Registros]
        
        subgraph Step1 Detail
            R1[Reader: DB Pendientes] --> P1[Processor: Validar Poder]
            P1 --> W1[Writer: Guardar Activos]
        end
        
        Step1 -.-> Step1Detail
    end
    
    W1 -->|Persiste| DB[(H2 Database)]
