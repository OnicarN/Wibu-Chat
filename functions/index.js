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

        const mensaje = {
            data: {
                title: "¡Nueva solicitud de amistad!",
                body: `${nombreEmisor} quiere ser tu amigo en WibuChat.`,
                emisorUid: emisorUid,
                solicitudKey: solicitudKey
            },
            token: tokenReceptor
        };

        const response = await admin.messaging().send(mensaje);
        console.log("✅ ¡Enviado con éxito!", response);
        return { success: true };

    } catch (error) {
        console.error("🔥 Error:", error.message);
        return null;
    }
});

// ← NUEVA FUNCIÓN
exports.notificarNuevoMensaje = onValueCreated("/chats/{chatId}/messages/{messageId}", async (event) => {

    const mensaje = event.data.val();
    const emisorUid = mensaje.emisorUid;
    const chatId = event.params.chatId;

    const uids = chatId.split("_");
    const receptorUid = uids[0] === emisorUid ? uids[1] : uids[0];

    try {
        const db = admin.database();
        const [emisorSnap, receptorSnap] = await Promise.all([
            db.ref(`users/${emisorUid}`).once("value"),
            db.ref(`users/${receptorUid}`).once("value")
        ]);

        const nombreEmisor = emisorSnap.val()?.username || "Alguien";
        const tokenReceptor = receptorSnap.val()?.fcmToken;

        if (!tokenReceptor) return null;
        if (mensaje.leido) return null;

        const mensajeFCM = {
            notification: {
                title: nombreEmisor,
                body: "Te ha enviado un mensaje"
            },
            data: {
                title: nombreEmisor,
                body: "Te ha enviado un mensaje",
                tipo: "mensaje"
            },
            token: tokenReceptor
        };

        await admin.messaging().send(mensajeFCM);
        return { success: true };

    } catch (error) {
        console.error("Error:", error.message);
        return null;
    }
});