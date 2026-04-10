const { onValueCreated } = require("firebase-functions/v2/database");
const admin = require("firebase-admin");
const serviceAccount = require("./serviceAccountKey.json");

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: "https://wibu-chat-db-default-rtdb.firebaseio.com"
});

exports.notificarNuevaSolicitud = onValueCreated("/solicitudes/{solicitudId}", async (event) => {
    console.log("--- PROCESANDO CON LLAVE PRIVADA ---");

    const snapshot = event.data;
    if (!snapshot.exists()) return null;

    // Extraemos el ID de la solicitud de los parámetros del evento
    const solicitudKey = event.params.solicitudId; 
    
    const datos = snapshot.val();
    const receptorUid = datos.para;
    const emisorUid = datos.de;

    try {
        const db = admin.database();
        const [emisorSnap, receptorSnap] = await Promise.all([
            db.ref(`users/${emisorUid}`).once("value"),
            db.ref(`users/${receptorUid}`).once("value")
        ]);

        const nombreEmisor = emisorSnap.val()?.username || "Alguien";
        const tokenReceptor = receptorSnap.val()?.fcmToken;

        if (!tokenReceptor) {
            console.log("⚠️ No hay token para el receptor.");
            return null;
        }

        // --- ACTUALIZACIÓN AQUÍ ---
        const mensaje = {
            data: {
                title: "¡Nueva solicitud de amistad!",
                body: `${nombreEmisor} quiere ser tu amigo en WibuChat.`,
                emisorUid: emisorUid,        // El UID de quien envía
                solicitudKey: solicitudKey   // El ID del nodo en /solicitudes
            },
            token: tokenReceptor
        };
        // ---------------------------

        const response = await admin.messaging().send(mensaje);
        console.log("✅ ¡Enviado con éxito!", response);
        return { success: true };

    } catch (error) {
        console.error("🔥 Error con llave privada:", error.message);
        return null;
    }
});