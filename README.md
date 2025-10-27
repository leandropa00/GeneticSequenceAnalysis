# Sistema de Análisis de Secuencias Genéticas

Sistema para la gestión y análisis de secuencias genéticas implementado en Java, diseñado con patrones de diseño y arquitectura robusta.

## Características

- **Gestión de Secuencias**: Carga y almacenamiento de secuencias desde archivos FASTA
- **Análisis Avanzados**:
  - Alineamiento de secuencias con cálculo de similitud
  - Detección de motivos genéticos
  - Predicción de estructuras secundarias
- **Sistema de Caché**: Almacenamiento inteligente de resultados para optimizar rendimiento
- **Generación de Reportes**: Exportación de análisis en formato texto o CSV
- **Interfaz Interactiva**: Menú de consola intuitivo y completo
- **Persistencia**: Base de datos MySQL para almacenamiento permanente

## Arquitectura y Patrones de Diseño

El sistema implementa los siguientes patrones de diseño:

- **Singleton**: Gestión de configuración, conexión a base de datos y almacenamiento de resultados
- **Factory**: Creación dinámica de estrategias de análisis
- **Strategy**: Diferentes algoritmos de análisis intercambiables
- **Facade**: Simplificación de la interfaz compleja del sistema
- **Builder**: Construcción flexible de reportes
- **Proxy**: Control de acceso y caché para análisis

## Requisitos

- Docker y Docker Compose
- Make (opcional, pero recomendado)

> **Nota**: No se requiere tener Java o Maven instalados localmente, ya que todo se ejecuta dentro de contenedores Docker.

## Instalación y Ejecución

### Opción 1: Usando Make (Recomendado)

```bash
# Ejecutar la aplicación
make run
```

### Opción 2: Sin Make

Si no tiene Make instalado, puede usar Docker Compose directamente:

```bash
docker compose run --rm app
```

## Uso

Al ejecutar la aplicación, se presentará un menú interactivo con las siguientes opciones:

### 1. Cargar Secuencias desde FASTA
Importa secuencias genéticas desde archivos en formato FASTA. Por defecto, carga desde `data/sequences.fasta`.

```
¿Usar ruta por defecto? (S/N): S
```

### 2. Ver Secuencias Almacenadas
Consulta las secuencias en la base de datos:
- Ver todas las secuencias
- Filtrar por tipo (DNA/RNA/Protein)
- Buscar por nombre

### 3. Realizar Análisis

#### Alineamiento de Secuencias
Compara dos secuencias y calcula su similitud:
```
Nombre de la primera secuencia: Seq001
Nombre de la segunda secuencia: Seq002
```

#### Detección de Motivos
Busca patrones específicos en una secuencia:
```
Nombre de la secuencia: Seq001
Motivo a buscar: ATG
```

#### Predicción de Estructura
Predice la estructura secundaria de una secuencia:
```
Nombre de la secuencia: Seq001
```

### 4. Generar Reportes
- Reporte de análisis de la sesión actual
- Reporte de todos los análisis en caché
- Exportación a archivo de texto o CSV

### 5. Configuración
- Cambiar ruta FASTA por defecto
- Ajustar longitud mínima de secuencias
- Ver estadísticas del sistema

### 6. Limpiar Caché
Elimina los resultados de análisis almacenados en memoria.

## Estructura del Proyecto

```
GeneticSequenceAnalysis/
├── data/                           # Archivos de entrada
│   └── sequences.fasta            # Secuencias de ejemplo
├── db/
│   └── migrations/                # Scripts SQL de migración
│       └── 001_create_genetic_sequences_table.sql
├── reports/                       # Reportes generados (exportados)
├── src/
│   └── main/
│       └── java/master/ucaldas/
│           ├── builder/           # Patrón Builder (Reportes)
│           │   ├── Report.java
│           │   └── ReportBuilder.java
│           ├── dao/               # Acceso a datos
│           │   └── SequenceDAO.java
│           ├── facade/            # Patrón Facade
│           │   └── GeneticAnalysisFacade.java
│           ├── factory/           # Patrón Factory
│           │   ├── AnalysisFactory.java
│           │   └── AnalysisType.java
│           ├── model/             # Modelos de dominio
│           │   ├── AnalysisResult.java
│           │   └── GeneticSequence.java
│           ├── proxy/             # Patrón Proxy
│           │   └── AnalysisProxy.java
│           ├── singleton/         # Patrón Singleton
│           │   ├── AnalysisResultStorage.java
│           │   ├── Configuration.java
│           │   └── DatabaseConnection.java
│           ├── strategy/          # Patrón Strategy
│           │   ├── IAnalysisStrategy.java
│           │   ├── AlignmentAnalysis.java
│           │   ├── MotifDetectionAnalysis.java
│           │   └── StructurePredictionAnalysis.java
│           ├── util/              # Utilidades
│           │   └── FASTAReader.java
│           └── Main.java          # Punto de entrada
├── docker-compose.yml             # Configuración Docker Compose
├── Dockerfile                     # Imagen de la aplicación
├── Makefile                       # Comandos simplificados
└── pom.xml                        # Configuración Maven
```

## Tecnologías Utilizadas

- **Java 21**: Lenguaje de programación
- **Maven**: Gestión de dependencias y construcción
- **MySQL 8.0**: Base de datos relacional
- **Docker**: Contenedorización
- **MySQL Connector/J**: Driver JDBC para MySQL

## Base de Datos

La aplicación utiliza MySQL con la siguiente estructura:

**Tabla: genetic_sequences**
- `id`: Identificador único (AUTO_INCREMENT)
- `name`: Nombre de la secuencia
- `sequence`: Cadena de nucleótidos/aminoácidos
- `type`: Tipo de secuencia (DNA/RNA/Protein)
- `description`: Descripción opcional
- `created_at`: Fecha de creación

Las migraciones se ejecutan automáticamente al iniciar el contenedor MySQL.

## Formato FASTA

El sistema acepta archivos en formato FASTA estándar:

```
>Seq001 description=Example DNA sequence type=DNA
ATGCCGTAGCCTAGGCTTAAAGTCCGTA
>Seq002 description=Example DNA sequence type=DNA
ATGGGCTTAACCGTACGTAGTCGATCG
```

## Configuración

La configuración del sistema se maneja a través de:

- **Variables de entorno** (definidas en `docker-compose.yml`):
  - `MYSQL_ROOT_PASSWORD`: Contraseña del usuario root
  - `MYSQL_DATABASE`: Nombre de la base de datos
  - `MYSQL_USER`: Usuario de la aplicación
  - `MYSQL_PASSWORD`: Contraseña del usuario

- **Configuración interna** (Singleton Configuration):
  - Ruta FASTA por defecto: `data/sequences.fasta`
  - Longitud mínima de secuencias: configurable desde el menú

## Ejemplo de Uso

```bash
# 1. Iniciar la aplicación
make run

# 2. En el menú, seleccionar opción 1 (Cargar secuencias)
# 3. Usar ruta por defecto (S)
# 4. Seleccionar opción 3 (Realizar análisis)
# 5. Elegir tipo de análisis (ej. 1 - Alineamiento)
# 6. Ingresar nombres de secuencias: Seq001 y Seq002
# 7. Ver reporte completo (S)
# 8. Opción 4 para generar reporte de sesión
# 9. Exportar a archivo si se desea
```

## Notas Adicionales

- Los resultados de análisis se almacenan en caché durante la sesión para optimizar consultas repetidas
- Los reportes generados se pueden exportar al directorio `reports/`
- La base de datos persiste entre ejecuciones gracias a los volúmenes de Docker
- El sistema valida automáticamente las conexiones a la base de datos antes de permitir operaciones

**Universidad de Caldas** | Maestría en Ingeniería Computacional | 2025