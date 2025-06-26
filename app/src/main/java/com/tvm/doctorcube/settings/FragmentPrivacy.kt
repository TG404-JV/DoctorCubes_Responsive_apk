package com.tvm.doctorcube.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.tvm.doctorcube.databinding.FragmentPrivacyBinding

class FragmentPrivacy : Fragment() {
    private var navController: NavController? = null
    private lateinit var binding: FragmentPrivacyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= FragmentPrivacyBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        val view = binding.root

        // Initialize NavController
        navController = NavHostFragment.findNavController(this)


        // Initialize Toolbar
        return view
    }


    override fun onDetach() {
        super.onDetach()
        if (navController != null) {
            navigateToHome()
        }
    }

    private fun navigateToHome() {
    }
}