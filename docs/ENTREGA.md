# Entrega — Checklist (según pautas)

## Reglas de entrega (del enunciado)

- Trabajo en grupos de **2 o 3**.
- **Todos** los miembros entregan **los mismos archivos**.
- Incluir un **README** con:
  - nombres de los miembros del grupo
  - explicación breve de la lógica
  - “en una línea” qué contiene cada archivo relevante
- Preferible entregar en un único comprimido: `.zip`, `.rar`, etc.

## Qué debe incluir vuestra entrega (recomendación)

- `README.md` (obligatorio): ya contiene:
  - integrantes (rellenar)
  - resumen + lógica
  - lista de archivos relevantes
  - cómo ejecutar
  - endpoints
  - monitorización y elementos visuales
- `GUIA_VERIFICACION.md`: pasos para comprobar criterios de éxito y métricas.
- `docs/ARQUITECTURA.md`: detalle de arquitectura, jobs/steps y estados.
- `docs/OPERACION_Y_MONITORIZACION.md`: guía práctica de operación, métricas y “elementos visuales”.
- Código fuente completo (`src/`) + `pom.xml` + `application.yml`.

## Preparación del ZIP

Desde la carpeta raíz del proyecto:

```bash
zip -r entrega_departamento_misterios.zip .
```

Alternativa (excluyendo `.git/` si queréis reducir tamaño):

```bash
zip -r entrega_departamento_misterios.zip . -x ".git/*"
```

## Último paso antes de entregar

- Rellenar los nombres reales en `README.md` → sección “Integrantes del grupo”.

