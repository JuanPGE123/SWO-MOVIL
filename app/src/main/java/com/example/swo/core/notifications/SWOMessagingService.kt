package com.example.swo.core.notifications

import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.swo.data.incidents.worker.SyncIncidentsWorker
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SWOMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Enviar token al servidor corporativo para asociarlo al usuario
        Log.d("FCM", "New Token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        
        // Determinar el tipo de actualización (Incidentes, Proyectos, etc)
        val type = message.data["type"]
        
        when (type) {
            "INCIDENT_UPDATE" -> {
                // Disparar sincronización silenciosa inmediata
                val syncRequest = OneTimeWorkRequestBuilder<SyncIncidentsWorker>().build()
                WorkManager.getInstance(applicationContext).enqueue(syncRequest)
            }
            "PROJECT_ASSIGNED" -> {
                // Lógica similar para otros módulos
            }
        }
        
        // Mostrar notificación visual si el mensaje contiene notificación
        message.notification?.let {
            // Implementación de NotificationCompat para mostrar el aviso al técnico
        }
    }
}
