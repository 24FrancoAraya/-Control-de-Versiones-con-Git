package com.example.parkinguapp.ui.perfil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions // Importación necesaria
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.NavHostFragment.Companion
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import com.example.parkinguapp.R
import com.google.android.material.card.MaterialCardView
import androidx.navigation.fragment.findNavController


class PerfilGuardiaFragment: Fragment() {

    // Constantes de SharedPreferences (Mismas que en LoginFragment)
    private val PREFS_NAME = "ParkingUAppPrefs"
    private val KEY_LOGGED_IN = "isLoggedIn"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_perfil_guardia, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cardQrScanner: MaterialCardView = view.findViewById(R.id.card_QR_Scan)

        cardQrScanner.setOnClickListener {
            // Asegúrate que esta acción existe en nav_graph.xml
            findNavController().navigate(R.id.action_loginFragment_to_appScannerFragment)
        }
        val cardIngresoVehiculo: MaterialCardView = view.findViewById(R.id.card_ingreso_vehiculo)

        cardIngresoVehiculo.setOnClickListener {
            // Asegúrate que esta acción existe en nav_graph.xml
            findNavController().navigate(R.id.action_perfilFragment_to_ingresoVehiculoFragment)
        }

        // Tarjeta para "Cerrar sesión"
        val cardCerrarSesion: MaterialCardView = view.findViewById(R.id.card_cerrar_sesion)

        cardCerrarSesion.setOnClickListener {

            // 1. CERRAR SESIÓN: Cambiar el estado a falso
            val sharedPrefs = requireActivity().getSharedPreferences(PREFS_NAME, 0)
            sharedPrefs.edit().putBoolean(KEY_LOGGED_IN, false).apply()

            Toast.makeText(context, "Sesión cerrada.", Toast.LENGTH_SHORT).show()

            // 2. NAVEGAR A BIENVENIDA Y LIMPIAR LA PILA (para evitar volver con "Atrás")
            findNavController().navigate(R.id.action_perfilFragment_to_bienvenidaFragment, null,
                NavOptions.Builder()
                    .setPopUpTo(R.id.bienvenidaFragment, true) // Limpia toda la pila y no permite volver.
                    .build())
        }


        // Conexión Tarjeta "Historial de Reservas" (Mantener la navegación)
        val cardHistorial: MaterialCardView = view.findViewById(R.id.card_historial_reservas)
        cardHistorial.setOnClickListener {
            findNavController().navigate(R.id.reservasFragment)
        }
    }
}

