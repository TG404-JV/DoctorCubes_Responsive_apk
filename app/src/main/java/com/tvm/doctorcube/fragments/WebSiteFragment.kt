package com.tvm.doctorcube.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.just.agentweb.AgentWeb
import com.just.agentweb.WebViewClient
import com.tvm.doctorcube.R
import com.tvm.doctorcube.databinding.FragmentWebSiteBinding
import android.view.ViewGroup.LayoutParams

class WebSiteFragment : Fragment() {

    private var _binding: FragmentWebSiteBinding? = null
    private val binding get() = _binding!!

    private lateinit var agentWeb: AgentWeb
    private var toolbar: Toolbar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWebSiteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity() as AppCompatActivity
        toolbar = activity.findViewById(R.id.toolbar)

        // 🔹 Hide toolbar initially
        toolbar?.visibility = View.GONE


        // 🔹 Load website using AgentWeb
        agentWeb = AgentWeb.with(this)
            .setAgentWebParent(
                binding.webContainer,
                LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            )
            .useDefaultIndicator()
            .setWebViewClient(object : WebViewClient() {
                override fun onPageFinished(view: android.webkit.WebView?, url: String?) {
                    super.onPageFinished(view, url)

                    // Optional: animate topView or toolbar if needed
                }
            })
            .createAgentWeb()
            .ready()
            .go("https://doctorcubes.com/index.html")
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // 🔄 Restore the original gradient
        toolbar?.visibility = View.VISIBLE

        _binding = null
    }
}
