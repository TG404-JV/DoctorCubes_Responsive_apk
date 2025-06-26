package com.tvm.doctorcube.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.tvm.doctorcube.R
import com.tvm.doctorcube.settings.faq.FAQAdapter
import com.tvm.doctorcube.settings.faq.FAQItem
import com.tvm.doctorcube.settings.faq.FAQViewModel
import androidx.core.net.toUri
import com.tvm.doctorcube.databinding.FragmentFAQBinding

class FragmentFAQ : Fragment() {

    private lateinit var viewBinding: FragmentFAQBinding
    private var recyclerView: RecyclerView? = null
    private var emptyStateLayout: ConstraintLayout? = null
    private var btnWhatsAppContact: MaterialButton? = null
    private var faqAdapter: FAQAdapter? = null
    private var faqViewModel: FAQViewModel? = null
    private var fragmentToolbar: Toolbar? = null // Renamed for clarity
    private var progressBar: ProgressBar? = null
    private var navController: NavController? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the updated layout
        viewBinding = FragmentFAQBinding.inflate(inflater, container, false)
        val view = viewBinding.root
        // Initialize NavController
        navController = NavHostFragment.findNavController(this)
        // Initialize views
        initViews(view)
        // Setup toolbar with back button
        setupToolbar()

        // Setup RecyclerView
        setupRecyclerView()

        // Initialize ViewModel
        faqViewModel = ViewModelProvider(this)[FAQViewModel::class.java]

        // Show loading state
        showLoading()

        // Observe FAQ data
        observeFAQData()

        // Setup WhatsApp button
        setupWhatsAppButton()

        return view
    }

    private fun initViews(view: View) {
        recyclerView = viewBinding.recyclerViewFAQ
        emptyStateLayout = viewBinding.emptyStateLayout
        btnWhatsAppContact = viewBinding.btnWhatsAppContact
        fragmentToolbar = view.findViewById(R.id.toolbar)
        progressBar = viewBinding.progressBar
    }
    private fun setupToolbar() {
        if (fragmentToolbar != null) {
            fragmentToolbar!!.setNavigationOnClickListener { v: View? -> navigateToHome() }
            fragmentToolbar!!.setTitle("FAQ")
        }
    }

    private fun setupRecyclerView() {
        // Add divider between items
        val dividerItemDecoration = DividerItemDecoration(
            requireContext(), DividerItemDecoration.VERTICAL
        )
        recyclerView!!.addItemDecoration(dividerItemDecoration)
    }

    private fun observeFAQData() {
        faqViewModel!!.faqList
            .observe(getViewLifecycleOwner(), Observer { faqItems: MutableList<FAQItem?>? ->
                // Hide loading state
                hideLoading()

                // Check if data exists
                if (faqItems == null || faqItems.isEmpty()) {
                    showEmptyState()
                } else {
                    showContent()
                    faqAdapter = FAQAdapter(faqItems)
                    recyclerView!!.setAdapter(faqAdapter)
                }
            })
    }

    private fun showLoading() {
        progressBar!!.visibility = View.VISIBLE
        recyclerView!!.visibility = View.GONE
        emptyStateLayout!!.visibility = View.GONE
    }

    private fun hideLoading() {
        progressBar!!.visibility = View.GONE
    }
    private fun showContent() {
        recyclerView!!.visibility = View.VISIBLE
        emptyStateLayout!!.visibility = View.GONE
    }
    private fun showEmptyState() {
        recyclerView!!.visibility=View.GONE
        emptyStateLayout!!.visibility = View.VISIBLE
    }

    private fun setupWhatsAppButton() {
        btnWhatsAppContact!!.setOnClickListener { v: View? -> openWhatsApp() }
    }

    private fun openWhatsApp() {
        try {
            // Replace with your support phone number (with country code)
            val phoneNumber = "+1234567890"
            val message = "Hello, I have a question regarding..." // Pre-filled message

            val uri = ("https://api.whatsapp.com/send?phone=" + phoneNumber +
                    "&text=" + Uri.encode(message)).toUri()
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        } catch (_: Exception) {
            println("Some error In Opening Whats app")
        }
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