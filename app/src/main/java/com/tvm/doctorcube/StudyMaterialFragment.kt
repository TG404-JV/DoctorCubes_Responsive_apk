package com.tvm.doctorcube

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButtonToggleGroup
import com.tvm.doctorcube.databinding.FragmentStudyMaterialBinding
import com.tvm.doctorcube.study.adapter.StudyMaterialPagerAdapter

class StudyMaterialFragment : Fragment() {

    private var _binding: FragmentStudyMaterialBinding? = null
    private val binding get() = _binding!!

    private var pagerAdapter: StudyMaterialPagerAdapter? = null

    // Getter for current search query
    var currentSearchQuery: String = ""
        private set

    private var searchRunnable: Runnable? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudyMaterialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initially hide the clear button
        binding.clearSearchButton.visibility = View.GONE

        setupToolbar()
        setupViewPagerAndButtons()
        setupSearch()
    }

    private fun setupToolbar() {
        (activity as? AppCompatActivity)?.let { activity ->
            val toolbar = activity.findViewById<Toolbar>(R.id.toolbar)
            toolbar?.let {
                val appTitle = toolbar.findViewById<TextView>(R.id.app_title)
                appTitle?.text = "Study Material"

                activity.supportActionBar?.apply {
                    setDisplayHomeAsUpEnabled(false)
                    setHomeButtonEnabled(false)
                }
                toolbar.navigationIcon = null
            }
        }
    }

    private fun setupViewPagerAndButtons() {
        pagerAdapter = StudyMaterialPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        binding.segmentedButtonGroup.check(R.id.btnNotes)

        binding.segmentedButtonGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                binding.viewPager.currentItem = if (checkedId == R.id.btnNotes) 0 else 1
                if (currentSearchQuery.isNotEmpty()) {
                    performSearch(currentSearchQuery)
                }
            }
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val buttonId = if (position == 0) R.id.btnNotes else R.id.btnVideos
                binding.segmentedButtonGroup.check(buttonId)
                if (currentSearchQuery.isNotEmpty()) {
                    performSearch(currentSearchQuery)
                }
            }
        })
    }

    private fun setupSearch() {
        binding.clearSearchButton.setOnClickListener {
            binding.searchEditText.setText("")
            binding.clearSearchButton.visibility = View.GONE
            currentSearchQuery = ""
            resetSearch()
            hideKeyboard()
        }

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                currentSearchQuery = binding.searchEditText.text.toString().trim()
                if (currentSearchQuery.isNotEmpty()) {
                    performSearch(currentSearchQuery)
                } else {
                    resetSearch()
                }
                hideKeyboard()
                true
            } else {
                false
            }
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.clearSearchButton.visibility =
                    if (s.isNotEmpty()) View.VISIBLE else View.GONE

                searchRunnable?.let {
                    binding.searchEditText.removeCallbacks(it)
                }

                searchRunnable = Runnable {
                    val query = s.toString().trim()
                    currentSearchQuery = query
                    if (query.length >= 2) {
                        performSearch(query)
                    } else if (query.isEmpty()) {
                        resetSearch()
                    }
                }

                binding.searchEditText.postDelayed(searchRunnable, 300)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }

    fun performSearch(query: String?) {
        pagerAdapter?.getFragmentAt(binding.viewPager.currentItem)?.let { fragment ->
            if (fragment is SearchableFragment) {
                fragment.performSearch(query)
            }
        }
    }

    fun resetSearch() {
        pagerAdapter?.getFragmentAt(binding.viewPager.currentItem)?.let { fragment ->
            if (fragment is SearchableFragment) {
                fragment.resetSearch()
            }
        }
    }

    interface SearchableFragment {
        fun performSearch(query: String?)
        fun resetSearch()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
