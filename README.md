# NowSilence 🤫

**Filtro Inteligente de Notificaciones con IA Local para Android**

## Propósito

NowSilence es una aplicación móvil Android que procesa las notificaciones entrantes de forma **100% privada dentro del dispositivo**, utilizando un modelo LLM pequeño y local (On-Device) compatible con el **Google AI Edge SDK**.

Nada sale de tu teléfono. Toda la inferencia de IA ocurre localmente.

## Enfoques de negocio

| Plan | Enfoque |
|------|---------|
| **Particular** | Bienestar y reducción de estrés: filtra notificaciones no deseadas para proteger tu tranquilidad. |
| **Empresarial** | Productividad e integración con herramientas de trabajo: prioriza y clasifica notificaciones relevantes del entorno laboral. |

## Arquitectura

El proyecto sigue principios de **Clean Architecture** dividida en capas:

```
app/src/main/java/com/nowsilence/app/
├── data/          # Implementaciones de repositorios, fuentes locales, mappers
│   ├── local/
│   ├── mapper/
│   └── repository/
├── domain/        # Reglas de negocio puras (independientes de Android)
│   ├── model/
│   ├── repository/
│   └── usecase/
└── presentation/  # UI Jetpack Compose, ViewModels, temas
    ├── component/
    ├── screen/
    ├── theme/
    └── viewmodel/
```

## Stack tecnológico

- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **IA On-Device:** Google AI Edge SDK (MediaPipe GenAI) — LLM local
- **DI:** Koin
- **Persistencia:** Room + DataStore
- **Min SDK:** 26 (Android 8.0) — soporte para servicios de notificaciones modernos
- **Target SDK:** 35

## Estado del proyecto

🚧 Fase 1: Inicialización y andamiaje completada. Próximas fases: vistas, motor de filtrado con IA local, listener de notificaciones, planes Particular/Empresarial.

## Licencia

TBD
