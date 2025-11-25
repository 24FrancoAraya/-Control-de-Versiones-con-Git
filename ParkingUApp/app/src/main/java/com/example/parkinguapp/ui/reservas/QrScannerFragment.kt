package com.example.parkinguapp.ui.reservas;

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.parkinguapp.R
import com.example.parkinguapp.databinding.FragmentAppScaneoBinding
import com.google.android.material.card.MaterialCardView
import com.google.zxing.integration.android.IntentIntegrator

class QrScannerFragment : Fragment() {
    private var _binding: FragmentAppScaneoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppScaneoBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnScanQR.setOnClickListener {
            // Inicia el escáner de QR usando el fragment
            val integrator = IntentIntegrator.forSupportFragment(this)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            integrator.setPrompt("Escanea el QR para aceptar la reservación")
            integrator.setCameraId(0)
            integrator.setBeepEnabled(true)
            integrator.setBarcodeImageEnabled(true)
            integrator.initiateScan()
        }
    }

    // Maneja el resultado del escaneo QR
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        val imageView = binding.imageResultadoQR // Si usas ViewBinding

        if (result != null && result.contents != null) {
            if (result.contents == "1234") {
                Toast.makeText(requireContext(), "Reservación aceptada", Toast.LENGTH_LONG).show()
                imageView.setImageResource(R.drawable.ic_aceptado)
            } else {
                Toast.makeText(requireContext(), "Código no válido", Toast.LENGTH_LONG).show()
                imageView.setImageResource(R.drawable.ic_no_aceptado)
            }
            imageView.visibility = View.VISIBLE
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
