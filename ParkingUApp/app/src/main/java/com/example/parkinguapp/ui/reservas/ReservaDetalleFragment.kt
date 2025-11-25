package com.example.parkinguapp.ui.reservadetalle // Asegúrate que coincida con tu paquete

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.parkinguapp.AdminSQLiteOpenHelper
import com.example.parkinguapp.R
import com.google.android.material.button.MaterialButton

class ReservaDetalleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reserva_detalle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textNombrePlaza = view.findViewById<TextView>(R.id.text_nombre_plaza_detalle)
        val btnCancelar = view.findViewById<MaterialButton>(R.id.btn_cancelar_reserva)
        val btnVolver = view.findViewById<MaterialButton>(R.id.btn_volver_a_reservar)

        // 1. LEER LA ÚLTIMA RESERVA DESDE SQLITE
        // Queremos mostrar la reserva que acabamos de hacer.
        val admin = AdminSQLiteOpenHelper(requireContext(), "administracion", null, 1)
        val bd = admin.readableDatabase

        // Buscamos la última (ID más alto)
        val fila = bd.rawQuery("select id, espacio from reservas order by id desc limit 1", null)

        var idReservaActual = -1 // Para saber qué borrar si cancela

        if (fila.moveToFirst()) {
            // Si encontramos una reserva, mostramos los datos
            idReservaActual = fila.getInt(0) // Guardamos el ID
            val espacio = fila.getString(1)
            textNombrePlaza.text = espacio
        } else {
            // Si no hay reservas
            textNombrePlaza.text = "Sin reserva"
            btnCancelar.isEnabled = false // Desactivamos cancelar si no hay nada
        }

        fila.close()
        bd.close()

        // 2. LÓGICA BOTÓN CANCELAR (BORRAR DE SQLITE)
        btnCancelar.setOnClickListener {
            if (idReservaActual != -1) {
                val adminWrite = AdminSQLiteOpenHelper(requireContext(), "administracion", null, 1)
                val bdWrite = adminWrite.writableDatabase

                // Borramos la reserva específica usando su ID
                val cant = bdWrite.delete("reservas", "id=${idReservaActual}", null)
                bdWrite.close()

                if (cant == 1) {
                    Toast.makeText(context, "Reserva cancelada correctamente", Toast.LENGTH_SHORT).show()
                    // Navegar a la pantalla de confirmación de cancelación
                    findNavController().navigate(R.id.reservaCanceladaFragment)
                } else {
                    Toast.makeText(context, "No se pudo cancelar la reserva", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 3. LÓGICA BOTÓN VOLVER
        btnVolver.setOnClickListener {
            // Volver al mapa para reservar de nuevo
            findNavController().popBackStack()
        }
    }
}