package com.example.parkinguapp.ui.reservas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.fragment.app.Fragment
import com.example.parkinguapp.AdminSQLiteOpenHelper // Importamos tu clase de BD
import com.example.parkinguapp.R
import com.google.android.material.card.MaterialCardView

class ReservasFragment : Fragment() {

    // Ya no necesitamos el 'companion object' con la lista temporal.
    // Los datos ahora viven en el teléfono.

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reservas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutListaReservas = view.findViewById<LinearLayout>(R.id.layout_lista_reservas)
        val textNoReservas = view.findViewById<TextView>(R.id.text_no_reservas)

        // Limpiamos la vista por si acaso
        layoutListaReservas.removeAllViews()

        try {
            // 1. ABRIR CONEXIÓN A LA BASE DE DATOS (MODO LECTURA)
            val admin = AdminSQLiteOpenHelper(requireContext(), "administracion", null, 1)
            val bd = admin.readableDatabase

            // 2. CONSULTA SQL: Traer todo ordenado por ID descendente (lo más nuevo primero)
            val fila = bd.rawQuery(
                "select espacio, sede, fecha, hora_inicio, hora_termino from reservas order by id desc",
                null
            )

            if (fila.moveToFirst()) {
                // --- CASO: HAY RESERVAS ---
                textNoReservas.visibility = View.GONE
                layoutListaReservas.visibility = View.VISIBLE

                do {
                    // 3. Extraer los datos de cada columna (0=espacio, 1=sede, etc.)
                    val espacio = fila.getString(0)
                    val sede = fila.getString(1)
                    val fecha = fila.getString(2)
                    val horaInicio = fila.getString(3)
                    val horaTermino = fila.getString(4)

                    // Creamos un mapa temporal para usar tu función de diseño existente
                    val reservaMap = mapOf(
                        "Espacio" to espacio,
                        "Sede" to sede,
                        "Fecha" to fecha,
                        "HoraInicio" to horaInicio,
                        "HoraTermino" to horaTermino
                    )

                    // 4. Crear la "tarjeta" visual y agregarla a la lista
                    val reservaView = createReservationItemView(reservaMap)
                    layoutListaReservas.addView(reservaView)

                } while (fila.moveToNext()) // Repetir mientras queden filas

            } else {
                // --- CASO: NO HAY RESERVAS ---
                textNoReservas.visibility = View.VISIBLE
                layoutListaReservas.visibility = View.GONE
            }

            // 5. Cerrar la conexión y el cursor para liberar memoria
            fila.close()
            bd.close()

        } catch (e: Exception) {
            Toast.makeText(context, "Error al cargar historial: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // --- FUNCIONES AUXILIARES DE DISEÑO (UI) ---

    private fun getThemeColor(@AttrRes attr: Int): Int {
        val typedValue = android.util.TypedValue()
        requireContext().theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }

    private fun createReservationItemView(reserva: Map<String, String>): View {
        val card = MaterialCardView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 8, 0, 8)
            }
            strokeWidth = 1
            radius = 8f
            setCardBackgroundColor(getThemeColor(com.google.android.material.R.attr.colorSurfaceContainerLow))
        }

        val innerLayout = LinearLayout(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.HORIZONTAL
            setPadding(16, 16, 16, 16)
        }

        // TextView para la Plaza
        val textItemPlaza = TextView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            text = reserva["Espacio"]
            setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_TitleMedium)
        }

        // TextView para la Fecha
        val textItemFecha = TextView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f)
            text = "${reserva["Fecha"]} (${reserva["HoraInicio"]} - ${reserva["HoraTermino"]})"
            setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodyMedium)
        }

        // TextView para la Sede
        val textItemSede = TextView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            text = reserva["Sede"]
            textAlignment = View.TEXT_ALIGNMENT_TEXT_END
            setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodyMedium)
        }

        innerLayout.addView(textItemPlaza)
        innerLayout.addView(textItemFecha)
        innerLayout.addView(textItemSede)

        card.addView(innerLayout)

        card.setOnClickListener {
            Toast.makeText(context, "Reserva en: ${reserva["Sede"]}", Toast.LENGTH_SHORT).show()
        }

        return card
    }
}