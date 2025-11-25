package com.example.parkinguapp // Paquete raíz

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity // ⬅️ ¡SOLUCIONA EL ERROR!
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.Button
import android.widget.Toast
import com.example.parkinguapp.R // Importa R
import com.google.zxing.integration.android.IntentIntegrator

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Obtener el NavHostFragment
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // 2. Obtener la BottomNavigationView
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // 3. CONEXIÓN CRÍTICA: Conecta la barra inferior
        bottomNavView.setupWithNavController(navController)

        // 4. Lógica para manejar la visibilidad de la barra
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                // Fragmentos donde se OCULTA la barra (Inicio de sesión, detalles, etc.)
                R.id.bienvenidaFragment,
                R.id.loginFragment,
                R.id.reservaDetalleFragment,
                R.id.reservaCanceladaFragment -> {
                    bottomNavView.visibility = View.GONE
                }
                else -> {
                    // Fragmentos donde se MUESTRA la barra (Mapa, Reservas, Perfil)
                    bottomNavView.visibility = View.VISIBLE
                }
            }
        }
    }
    fun iniciarEscaneoQR() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Escanea el QR para aceptar la reservación")
        integrator.setCameraId(0)
        integrator.setBeepEnabled(true)
        integrator.setBarcodeImageEnabled(true)
        integrator.initiateScan()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null && result.contents != null) {
            // Puedes aceptar la reservación aquí:
            Toast.makeText(this, "Código QR: ${result.contents}", Toast.LENGTH_LONG).show()
            // O llama a tu lógica, por ejemplo aceptarReservacion(result.contents)
        } else {
            Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_SHORT).show()
        }
    }


}