package com.example.parkinguapp.ui.ingresovehiculo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter // ⬅️ ¡Nueva Importación!
import android.widget.AutoCompleteTextView // ⬅️ ¡Nueva Importación!
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.NavOptions
import com.example.parkinguapp.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class IngresoVehiculoFragment : Fragment() {

    // ... (Companion object se mantiene igual) ...

    companion object {
        var patenteGuardada: String? = null
        var tipoVehiculoGuardado: String? = null
        var modeloGuardado: String? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ingreso_vehiculo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Referencias a vistas
        val inputPatente = view.findViewById<TextInputEditText>(R.id.inputPatente)
        val inputTipo = view.findViewById<AutoCompleteTextView>(R.id.inputTipoVehiculo) // ⬅️ ¡Cambiamos a AutoCompleteTextView!
        val inputModelo = view.findViewById<TextInputEditText>(R.id.inputModelo)
        val btnGuardar: MaterialButton = view.findViewById(R.id.btnGuardarVehiculo)

        // ------------------------------------------------------------------
        // 2. CONFIGURACIÓN DEL LISTADO DESPLEGABLE (TIPO DE VEHÍCULO)
        // ------------------------------------------------------------------
        val tiposVehiculo = resources.getStringArray(R.array.opciones_tipo_vehiculo)
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, tiposVehiculo)

        inputTipo.setAdapter(adapter)

        // 3. Precargar datos si ya existen
        inputPatente.setText(patenteGuardada)

        // Precargar el TIPO DE VEHÍCULO
        if (tipoVehiculoGuardado != null) {
            inputTipo.setText(tipoVehiculoGuardado, false) // El 'false' evita que se dispare la acción al establecer el texto
        }

        inputModelo.setText(modeloGuardado)


        // 4. Lógica para guardar el vehículo temporalmente
        btnGuardar.setOnClickListener {
            val patente = inputPatente.text.toString().trim()
            val tipo = inputTipo.text.toString().trim() // ⬅️ Se obtiene el valor seleccionado
            val modelo = inputModelo.text.toString().trim()

            if (patente.isEmpty() || tipo.isEmpty()) { // Validamos que el tipo no esté vacío
                Toast.makeText(context, "Patente y Tipo de Vehículo son obligatorios.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // A. Guardar datos en la memoria estática (Companion Object)
            patenteGuardada = patente
            tipoVehiculoGuardado = tipo
            modeloGuardado = modelo

            // Log para verificación
            println("Vehículo guardado temporalmente: Patente=$patente, Tipo=$tipo")

            Toast.makeText(context, "Vehículo guardado temporalmente.", Toast.LENGTH_SHORT).show()

            // B. Navegar a Mapa después de guardar
            findNavController().navigate(R.id.action_ingresoVehiculoFragment_to_mapaFragment, null,
                NavOptions.Builder()
                    .setPopUpTo(R.id.mapaFragment, true)
                    .build())
        }
    }
}