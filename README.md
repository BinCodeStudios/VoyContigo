
# Voy Contigo — Plataforma Web de Transporte Terrestre

**Voy Contigo** es una plataforma web desarrollada para modernizar el sistema de compra y gestión de pasajes de transporte terrestre en Colombia. Está orientada a terminales como la de Bucaramanga, e integra funcionalidades avanzadas, inteligencia artificial y diseño responsivo para mejorar la experiencia de usuarios, conductores y administradores.

---

## 🚀 Tecnologías Utilizadas

- **Backend:** Spring Boot (Java)
- **Frontend:** HTML5, CSS3, JavaScript, Bootstrap, TailwindCSS, Thymeleaf
- **Base de Datos:** MongoDB
- **Despliegue:** Docker (contenedorización de servicios)
- **Machine Learning:** KNIME (para predicción de demanda, recomendación de rutas, análisis de PQRS)
- **Control de versiones:** Git & GitHub

---

## 🧩 Arquitectura del Sistema

- Arquitectura en capas (Controlador, Servicio, Repositorio).
- Integración de modelos ML mediante servicios conectados a KNIME.
- Contenedores Docker para despliegue y escalabilidad.
- Motor de plantillas Thymeleaf para renderizado dinámico.
- Acceso segmentado por roles: Cliente, Conductor, Taquillero, Administrador General y Administrador de Flota.

---

## 🔐 Roles del Sistema

- **Administrador General:** Gestión de usuarios, empresas, monitoreo global.
- **Administrador de Flota:** Asignación de buses y personal, seguimiento de viajes.
- **Taquillero:** Registro y validación de ventas presenciales.
- **Conductor:** Visualización de rutas asignadas y gestión de su ejecución.
- **Cliente:** Compra de tiquetes, consulta de horarios, historial y PQRS.

---

## 🧠 Funcionalidades Principales

- Registro, login y control de acceso por rol.
- Compra de tiquetes con selección de ruta y asiento.
- Dashboard administrativo con analítica operativa.
- Visualización de rutas y disponibilidad en tiempo real.
- Clasificación de PQRS usando análisis de sentimiento (NLP).
- Predicción de demanda de viajes con modelos de ML integrados desde KNIME.
- Recomendación inteligente de rutas según historial.

---

## ⚙️ Despliegue con Docker

La aplicación se ejecuta en contenedores usando Docker:

```bash
docker-compose up --build
```

Servicios:
- `backend`: Spring Boot + MongoDB
- `frontend`: HTML/CSS/JS renderizado con Thymeleaf
- `ml-engine`: integración de KNIME ejecutado en segundo plano

---

## 📊 Análisis de Datos con KNIME

Se implementaron cuatro flujos de análisis:
1. **Predicción de Demanda** — Regresión sobre ventas históricas.
2. **Recomendación de Rutas** — Filtrado basado en comportamiento de clientes.
3. **Análisis de PQRS** — Clasificación automática por sentimiento.
4. **Optimización de Horarios** — Agrupamiento por patrones de uso (clustering).

---

## 📅 Metodología de Desarrollo

- Enfoque ágil con sprints (Scrum).
- Historias de usuario organizadas por rol.
- Validación mediante pruebas funcionales, QA manual y encuestas.
- Pruebas automatizadas con Selenium para flujos críticos.

---

## 📝 Autores

Proyecto desarrollado por estudiantes de Ingeniería de Sistemas — UTS Bucaramanga:
- Yeison Eliecer Prada Reatiga  
- Elkin Arley Rangel Durán  
- Daniel Sebastián Delgado Carrillo

**Director:** Julián Barney Jaimes Rincón

---

## 📩 Contacto

Para consultas técnicas o demostraciones:
**devarleyran@gmail.com**
