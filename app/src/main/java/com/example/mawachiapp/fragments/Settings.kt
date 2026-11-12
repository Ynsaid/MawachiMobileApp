package com.example.mawachiapp.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.mawachiapp.R
import com.example.mawachiapp.databinding.FragmentSettingsBinding
import com.example.mawachiapp.screens.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Settings : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        // logout btn with dialog confirmation
        binding.logoutBtn.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext()) // Use requireContext() instead of context
            builder
                .setMessage("هل تريد تسجيل الخروج من الحساب؟")
                .setPositiveButton("تسجيل الخروج") { _, _ ->
                    auth.signOut()
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
                .setNegativeButton("إلغاء") { dialog, _ ->
                    dialog.dismiss()
                }

            val dialog = builder.create()
            dialog.show()
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)?.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.soft_red))
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE)?.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.black))
        }
        // Delete account btn with dialog confirmation
        binding.deleteAccBtn.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("Do you want to remove your account ? ")
                .setMessage("سيودي ذلك الى حذف جميع بياناتك الشخضية المتعلقة بالحساب")
                .setPositiveButton("حذف الحساب") { _, _ ->
                    auth.currentUser?.delete()?.addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            auth.signOut()
                            val intent = Intent(requireContext(), LoginActivity::class.java)
                            startActivity(intent)
                            activity?.finish()
                        }
                    }
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
                .setNegativeButton("إلغاء") { dialog, _ ->
                    dialog.dismiss()
                }
            val dialog = builder.create()
            dialog.show()
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)?.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.soft_red))
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE)?.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.black))
        }

        // get name of user
        val db = Firebase.firestore
        val user = FirebaseAuth.getInstance().currentUser?.uid
        db.collection("users").document(user ?: "")
            .get()
            .addOnSuccessListener { document ->
                val name = document.getString("name")
                binding.nameOfUser.text = name
            }


        return binding.root
    }

}