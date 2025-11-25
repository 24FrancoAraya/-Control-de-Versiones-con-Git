package com.example.parkinguapp.ui.bienvenida

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.NavOptions // Importación necesaria
import com.example.parkinguapp.R
import com.google.android.material.card.MaterialCardView

class BienvenidaFragment : Fragment() {

    // Constantes de SharedPreferences
    private val PREFS_NAME = "ParkingUAppPrefs"
    private val KEY_LOGGED_IN = "isLoggedIn"

    private fun checkSessionStatus(): Boolean {
        // Obtenemos SharedPreferences. Por defecto, la sesión es 'false'.
        val sharedPrefs = requireActivity().getSharedPreferences(PREFS_NAME, 0)
        return sharedPrefs.getBoolean(KEY_LOGGED_IN, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. VERIFICACIÓN CRÍTICA: Si la sesión está activa, ir directo a Mapa
        if (checkSessionStatus()) {
            // Usamos NavOptions para limpiar la pila y que el usuario no pueda volver a Bienvenida con el botón Atrás
            findNavController().navigate(R.id.mapaFragment, null,
                NavOptions.Builder()
                    .setPopUpTo(R.id.bienvenidaFragment, true)
                    .build())
            return // Detiene la configuración de la vista si se navega.
        }

        // Si la sesión NO está activa, configuramos los listeners normales

        // 1. Conexión de la tarjeta "Iniciar Sesión"
        val cardIniciarSesion: MaterialCardView = view.findViewById(R.id.card_iniciar_sesion)

        cardIniciarSesion.setOnClickListener {
            findNavController().navigate(R.id.action_bienvenidaFragment_to_loginFragment)
        }

        // 2. Conexión de la tarjeta "Ver mapa estacionamiento"
        val cardVerMapa: MaterialCardView = view.findViewById(R.id.card_ver_mapa)

        cardVerMapa.setOnClickListener {
            // Navegación con limpieza de la pila para que Mapa sea el nuevo inicio
            findNavController().navigate(R.id.action_bienvenidaFragment_to_mapaFragment, null,
                NavOptions.Builder()
                    .setPopUpTo(R.id.bienvenidaFragment, true)
                    .build())
        }
    }

    // Mantener onCreateView simple, si lo necesitas:
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bienvenida, container, false)
    }
}