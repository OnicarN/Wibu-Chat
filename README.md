# 💬 WibuChat

**WibuChat** es una aplicación de mensajería instantánea nativa para Android desarrollada en Java con Android Studio. Permite chatear en tiempo real con otros usuarios, crear grupos, enviar solicitudes de amistad con notificaciones push y hablar con un asistente de IA integrado.

---

## 📱 Capturas de pantalla

> _Añade aquí tus capturas cuando tengas la app lista_

---

## ✨ Funcionalidades

- 🔐 **Autenticación** — Registro e inicio de sesión con email y contraseña mediante Firebase Authentication
- 👥 **Amigos** — Sistema de solicitudes de amistad con notificaciones push automáticas
- 💬 **Chat individual** — Mensajes en tiempo real con confirmación de lectura (doble tick ✓✓)
- 👨‍👩‍👧 **Chat grupal** — Creación de grupos con múltiples participantes
- 🔒 **Cifrado AES** — Todos los mensajes se cifran antes de guardarse en Firebase
- 🤖 **BotFriend** — Chat con IA integrado usando la API de Gemini (Google)
- 🔔 **Notificaciones push** — Alertas de solicitudes de amistad via Firebase Cloud Messaging
- 🔍 **Buscador de usuarios** — Busca cualquier usuario registrado por nombre

---

## 🛠️ Tecnologías utilizadas

| Tecnología | Uso |
|---|---|
| Java | Lenguaje principal |
| Android Studio Narwhal | IDE de desarrollo |
| Firebase Authentication | Registro e inicio de sesión |
| Firebase Realtime Database | Mensajes y perfiles en tiempo real |
| Firebase Cloud Messaging | Notificaciones push |
| Firebase Cloud Functions | Lógica backend automática (Node.js) |
| Gemini API (Google) | IA del BotFriend |
| ViewPager2 + TabLayout | Navegación por pestañas |
| RecyclerView | Listas de mensajes y contactos |
| AES/CBC/PKCS5 | Cifrado de mensajes |

---

## 🏗️ Arquitectura

La app se organiza en paquetes por funcionalidad:

```
com.example.wibuchat100/
├── amigos/          # Fragment de lista de amigos
├── botfriend/       # Fragment de chat con IA (Gemini)
├── chats/           # Chat individual, grupal, adapters y modelos
├── cifrador/        # CifradoHelper (AES)
├── contactos/       # Búsqueda de usuarios y perfil de contacto
├── crearcuentas/    # Login, SignUp y modelo Usuario
├── perfiles/        # Fragment de perfil propio
├── secciones/       # MainPagerAdapter
├── solicitudes/     # SolicitudActivity y FCM Service
└── MainActivity     # Contenedor principal con tabs
```

### Estructura de Firebase Realtime Database

```json
{
  "users":       { "{uid}": { "uid", "username", "email", "fcmToken" } },
  "chats":       { "{chatId}": { "messages": { "{msgId}": { "texto", "emisorUid", "timestamp", "leido" } } } },
  "grupos":      { "{grupoId}": { "nombre", "creadoPor", "participantes", "messages": {} } },
  "amigos":      { "{miUid}": { "{amigoUid}": true } },
  "solicitudes": { "{solicitudId}": { "de", "para", "estado", "timestamp" } }
}
```

---

## 🚀 Instalación y configuración

### Requisitos previos

- Android Studio Narwhal (2025.1.1) o superior
- JDK 25
- Cuenta de Firebase
- Cuenta de Google AI Studio (para BotFriend)
- Node.js 18+ y Firebase CLI (para Cloud Functions)

### Pasos

1. **Clona el repositorio**
   ```bash
   git clone https://github.com/OnicarN/Wibu-Chat.git
   cd Wibu-Chat
   ```

2. **Configura Firebase**
   - Crea un proyecto en [Firebase Console](https://console.firebase.google.com)
   - Activa **Authentication** (email/contraseña), **Realtime Database** y **Cloud Messaging**
   - Descarga `google-services.json` y colócalo en `app/`

3. **Configura la API Key de Gemini**
   - Obtén una clave gratuita en [Google AI Studio](https://aistudio.google.com/apikey)
   - Pégala en `BotFriendFragment.java`:
     ```java
     private static final String API_KEY = "TU_API_KEY_AQUI";
     ```

4. **Despliega las Cloud Functions**
   ```bash
   cd functions
   npm install
   firebase deploy --only functions
   ```
   > ⚠️ Nunca subas `serviceAccountKey.json` a GitHub. Añádelo al `.gitignore`.

5. **Compila y ejecuta** desde Android Studio

---

## 🔒 Seguridad

- Las contraseñas son gestionadas por Firebase Authentication y nunca se almacenan en texto plano
- Todos los mensajes se cifran con **AES-256/CBC** antes de enviarse a Firebase
- El `serviceAccountKey.json` debe estar en `.gitignore` y nunca subirse al repositorio
- La API Key de Gemini debe tratarse como un secreto

---

## 📂 .gitignore recomendado

```
# Firebase
functions/serviceAccountKey.json
google-services.json

# Android
*.iml
.gradle/
local.properties
.idea/
build/
captures/
```

---

## 📄 Licencia

Este proyecto es de uso académico — Trabajo de Fin de Grado del Ciclo Formativo de Grado Superior en Desarrollo de Aplicaciones Multiplataforma (DAM), IES San Andrés, curso 2025/2026.

---

## 👨‍💻 Autor

**Héctor Daniel Polanco Mena**  
[GitHub: @OnicarN](https://github.com/OnicarN)
