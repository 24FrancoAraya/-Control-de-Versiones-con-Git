package com.example.parkinguapp.ui.mapa

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues // Importante para SQL
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.parkinguapp.AdminSQLiteOpenHelper // Importamos tu clase de BD
import com.example.parkinguapp.R
import com.example.parkinguapp.ui.ingresovehiculo.IngresoVehiculoFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MapaFragment : Fragment() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    // Variables para la lógica de selección de plaza
    private var selectedSpotView: TextView? = null
    private var selectedSpotName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mapa, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- 1. MOSTRAR DATOS DEL VEHÍCULO ---
        val cardVehiculo = view.findViewById<MaterialCardView>(R.id.card_vehiculo_registrado)
        val textPatente = view.findViewById<TextView>(R.id.text_patente_activa)
        val textTipo = view.findViewById<TextView>(R.id.text_tipo_vehiculo_activo)
        val textModelo = view.findViewById<TextView>(R.id.text_modelo_activo)

        val patenteGuardada = IngresoVehiculoFragment.patenteGuardada
        val tipoVehiculo = IngresoVehiculoFragment.tipoVehiculoGuardado
        val modelo = IngresoVehiculoFragment.modeloGuardado

        if (!patenteGuardada.isNullOrEmpty() && patenteGuardada != "No registrado") {
            cardVehiculo.visibility = View.VISIBLE
            textPatente.text = "Patente: $patenteGuardada"
            textTipo.text = "Tipo: $tipoVehiculo"
            textModelo.text = "Modelo: $modelo"
        } else {
            cardVehiculo.visibility = View.GONE
        }

        // --- 2. FORMULARIO DE SIMULACIÓN DE RESERVA ---
        val inputFecha = view.findViewById<TextInputEditText>(R.id.inputFecha)
        val inputHoraInicio = view.findViewById<TextInputEditText>(R.id.inputHoraInicio)
        val inputHoraTermino = view.findViewById<TextInputEditText>(R.id.inputHoraTermino)
        val inputSede = view.findViewById<AutoCompleteTextView>(R.id.inputSede)
        val btnReservarEspacio = view.findViewById<MaterialButton>(R.id.btnReservarEspacio)
        val textNombrePlaza = view.findViewById<TextView>(R.id.text_nombre_plaza)
        val parkingGrid = view.findViewById<GridLayout>(R.id.parking_spot_grid)

        // Configurar Selector de Sede (Dropdown)
        val sedes = arrayOf("Casa Central", "Campus Rondizzoni", "Campus Huemul")
        val adapterSedes = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sedes)
        inputSede.setAdapter(adapterSedes)
        if (inputSede.text.isNullOrEmpty()) {
            inputSede.setText(sedes[0], false)
        }

        // Configurar Selector de Fecha/Hora
        inputFecha.setOnClickListener { showDatePicker(inputFecha) }
        inputHoraInicio.setOnClickListener { showTimePicker(inputHoraInicio) }
        inputHoraTermino.setOnClickListener { showTimePicker(inputHoraTermino) }

        // --- MANEJO DE SELECCIÓN DE PLAZAS ---
        val spotClickListener = View.OnClickListener { v ->
            val spotTextView = v as TextView
            handleSpotSelection(spotTextView, textNombrePlaza)
        }

        for (i in 0 until parkingGrid.childCount) {
            val child = parkingGrid.getChildAt(i)
            if (child is TextView) {
                child.setOnClickListener(spotClickListener)
            }
        }

        // e) Lógica del botón Simular Reserva (AHORA CON SQLITE)
        btnReservarEspacio.setOnClickListener {
            val fecha = inputFecha.text.toString()
            val horaInicio = inputHoraInicio.text.toString()
            val horaTermino = inputHoraTermino.text.toString()
            val sede = inputSede.text.toString()

            // 1. Validaciones
            if (patenteGuardada.isNullOrEmpty() || patenteGuardada == "No registrado") {
                Toast.makeText(context, "🚫 Por favor, registre su vehículo primero en Perfil para reservar.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (selectedSpotName.isNullOrEmpty()) {
                Toast.makeText(context, "🚫 Por favor, seleccione un espacio de estacionamiento.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (fecha.isEmpty() || horaInicio.isEmpty() || horaTermino.isEmpty() || sede.isEmpty()) {
                Toast.makeText(context, "⚠️ Por favor, complete todos los campos de la reserva (Fecha, Horas y Sede).", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // 2. GUARDAR EN BASE DE DATOS SQLITE
            try {
                // Abrir conexión
                val admin = AdminSQLiteOpenHelper(requireContext(), "administracion", null, 1)
                val bd = admin.writableDatabase

                // Preparar datos
                val registro = ContentValues()
                registro.put("patente", patenteGuardada)
                registro.put("espacio", selectedSpotName)
                registro.put("sede", sede)
                registro.put("fecha", fecha)
                registro.put("hora_inicio", horaInicio)
                registro.put("hora_termino", horaTermino)

                // Insertar y cerrar
                bd.insert("reservas", null, registro)
                bd.close()

                // 3. Mensaje de éxito y navegación
                val mensajeSimulacion = "✅ Reserva guardada correctamente para ${selectedSpotName}."
                Toast.makeText(context, mensajeSimulacion, Toast.LENGTH_LONG).show()

                findNavController().navigate(R.id.reservaDetalleFragment)

                // Limpiar campos
                inputFecha.setText("")
                inputHoraInicio.setText("")
                inputHoraTermino.setText("")
                selectedSpotView?.isSelected = false
                selectedSpotView = null
                selectedSpotName = null
                textNombrePlaza.text = "Seleccione una plaza..."

            } catch (e: Exception) {
                Toast.makeText(context, "Error al guardar en BD: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showDatePicker(input: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            calendar.set(selectedYear, selectedMonth, selectedDay)
            input.setText(dateFormat.format(calendar.time))
        }, year, month, day)

        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }

    private fun showTimePicker(input: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
            calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
            calendar.set(Calendar.MINUTE, selectedMinute)
            input.setText(timeFormat.format(calendar.time))
        }, hour, minute, true)

        timePickerDialog.show()
    }

    private fun handleSpotSelection(newlySelectedSpot: TextView, textNombrePlaza: TextView) {
        selectedSpotView?.isSelected = false
        selectedSpotView = newlySelectedSpot
        selectedSpotView?.isSelected = true
        selectedSpotName = newlySelectedSpot.text.toString()
        textNombrePlaza.text = selectedSpotName
        Toast.makeText(context, "Plaza ${selectedSpotName} seleccionada.", Toast.LENGTH_SHORT).show()
    }
}