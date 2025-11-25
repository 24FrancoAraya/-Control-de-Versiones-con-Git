package com.example.parkinguapp.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.NavOptions // Importación necesaria
import com.example.parkinguapp.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class LoginFragment : Fragment() {

    // Constantes para SharedPreferences
    private val PREFS_NAME = "ParkingUAppPrefs"
    private val KEY_LOGGED_IN = "isLoggedIn"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Credenciales simuladas (Hardcoded)
        val CORRECT_EMAIL = "guardia@parking.cl"
        val CORRECT_PASSWORD = "12345"

        val inputEmail: TextInputEditText = view.findViewById(R.id.inputEmail)
        val inputPassword: TextInputEditText = view.findViewById(R.id.inputPassword)
        val loginButton: MaterialButton = view.findViewById(R.id.btnLogin)

        loginButton.setOnClickListener {
            val email = inputEmail.text.toString().trim()
            val password = inputPassword.text.toString().trim()

            if (email == CORRECT_EMAIL && password == CORRECT_PASSWORD) {
                // GUARDAR SESIÓN ACTIVA
                val sharedPrefs = requireActivity().getSharedPreferences(PREFS_NAME, 0)
                sharedPrefs.edit().putBoolean(KEY_LOGGED_IN, true).apply()

                Toast.makeText(context, "Bienvenido!", Toast.LENGTH_SHORT).show()
                if (email == "guardia@parking.cl" && password == "12345") {
                    findNavController().navigate(R.id.perfilGuardiaFragment)
                }
                if(email=="test@parking.cl"&& password=="12345"){
                    findNavController().navigate(R.id.perfilFragment)
                }

            } else {
                Toast.makeText(context, "Credenciales incorrectas.", Toast.LENGTH_LONG).show()
            }
        }
    }

}