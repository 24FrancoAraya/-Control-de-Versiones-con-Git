package com.example.parkinguapp.ui.reservacancelada

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.NavOptions
import com.example.parkinguapp.R
import com.google.android.material.button.MaterialButton

class ReservaCanceladaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reserva_cancelada, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Referencia al botón de Volver al inicio (asumiendo el ID btn_volver_inicio de tu XML)
        val btnVolverInicio: MaterialButton = view.findViewById(R.id.btn_volver_inicio)

        btnVolverInicio.setOnClickListener {
            // Acción CORRECTA (definida en nav_graph.xml)
            findNavController().navigate(R.id.action_reservaCanceladaFragment_to_mapaFragment, null,
                NavOptions.Builder()
                    // Limpia la pila hasta el mapa para que el usuario no pueda volver a la cancelación
                    .setPopUpTo(R.id.mapaFragment, true)
                    .build())
        }
    }
}