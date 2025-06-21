package com.tvm.doctorcube.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.button.MaterialButton
import com.tvm.doctorcube.R
import com.tvm.doctorcube.SocialActions

// Add this import
// Already imported
class FragmentAbout : Fragment() {
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var navController: NavController? = null
    private val fragmentToolbar: Toolbar? = null // Renamed for clarity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getArguments() != null) {
            mParam1 = requireArguments().getString(ARG_PARAM1)
            mParam2 = requireArguments().getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_about, container, false)

        // Initialize NavController
        navController = NavHostFragment.findNavController(this)

        // Hide MainActivity Toolbar

        // Initialize Fragment's Toolbar and set back button listener
        if (fragmentToolbar != null) {
            fragmentToolbar.setNavigationOnClickListener(View.OnClickListener { v: View? -> navigateToHome() })
        }

        // Initialize UI elements and set listeners
        val emailButton = view.findViewById<MaterialButton>(R.id.btnEmail)
        val whatsappButton = view.findViewById<MaterialButton>(R.id.btnWhatsApp)
        val callButton = view.findViewById<MaterialButton>(R.id.btnCall)

        val openMedia = SocialActions()

        emailButton.setOnClickListener(View.OnClickListener { v: View? ->
            openMedia.openEmailApp(
                requireActivity()
            )
        })
        whatsappButton.setOnClickListener(View.OnClickListener { v: View? ->
            openMedia.openWhatsApp(
                requireActivity()
            )
        })
        callButton.setOnClickListener(View.OnClickListener { v: View? ->
            openMedia.makeDirectCall(
                requireActivity()
            )
        })
        view.findViewById<View?>(R.id.btnInstagram)
            .setOnClickListener(View.OnClickListener { v: View? ->
                openMedia.openInstagram(requireActivity())
            })
        view.findViewById<View?>(R.id.btnTwitter)
            .setOnClickListener(View.OnClickListener { v: View? ->
                openMedia.openTwitter(requireActivity())
            })
        view.findViewById<View?>(R.id.btnYoutube)
            .setOnClickListener(View.OnClickListener { v: View? ->
                openMedia.openYouTube(requireActivity())
            })


        return view
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Show MainActivity Toolbar when leaving this Fragment
    }

    private fun navigateToHome() {
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        fun newInstance(param1: String?, param2: String?): FragmentAbout {
            val fragment = FragmentAbout()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.setArguments(args)
            return fragment
        }
    }
}