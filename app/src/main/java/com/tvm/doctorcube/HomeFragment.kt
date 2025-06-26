
package com.tvm.doctorcube

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.card.MaterialCardView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tvm.doctorcube.communication.SearchUtils
import com.tvm.doctorcube.home.adapter.CountryAdapter
import com.tvm.doctorcube.home.adapter.FeaturesAdapter
import com.tvm.doctorcube.home.adapter.TestimonialsSliderAdapter
import com.tvm.doctorcube.home.adapter.UniversityListAdapter
import com.tvm.doctorcube.home.adapter.UpcomingEventAdapter
import com.tvm.doctorcube.home.data.FeatureData
import com.tvm.doctorcube.home.data.Testimonial
import com.tvm.doctorcube.home.model.Country
import com.tvm.doctorcube.home.model.Feature
import com.tvm.doctorcube.home.model.UpcomingEvent
import com.tvm.doctorcube.university.model.University
import com.tvm.doctorcube.university.model.UniversityData
import android.view.animation.AccelerateDecelerateInterpolator
import com.tvm.doctorcube.databinding.FragmentHomeBinding
import com.tvm.doctorcube.home.adapter.SearchResultsAdapter

class HomeFragment : Fragment(), FeaturesAdapter.OnFeatureClickListener,
    UpcomingEventAdapter.OnItemClickListener, CountryAdapter.OnCountryClickListener {


    private var featuresRecyclerView: RecyclerView? = null
    private var universitiesRecyclerView: RecyclerView? = null
    private var eventsRecyclerView: RecyclerView? = null
    private var countryRecyclerView: RecyclerView? = null
    private var featuresAdapter: FeaturesAdapter? = null
    private var universityListAdapter: UniversityListAdapter? = null
    private var eventAdapter: UpcomingEventAdapter? = null
    private var countryAdapter: CountryAdapter? = null
    private var testimonialsViewPager: ViewPager2? = null
    private var testimonialsAdapter: TestimonialsSliderAdapter? = null
    private var sliderHandler: Handler? = null
    private var testimonialsSliderRunnable: Runnable? = null

    private var searchEditText: EditText? = null
    private var clearSearchButton: ImageView? = null
    private var searchResultsRecyclerView: RecyclerView? = null
    private var searchResultsAdapter: SearchResultsAdapter? = null
    private var fullSearchList: MutableList<SearchItem?> = ArrayList()
    private var searchUtils: SearchUtils<SearchItem?>? = null

    private var studyButton: MaterialCardView? = null
    private var examButton: MaterialCardView? = null
    private var universityButton: MaterialCardView? = null
    private var seeAllEventsButton: TextView? = null
    private var inviteButton: AppCompatButton? = null

    private var navController: NavController? = null
    private var databaseReference: DatabaseReference? = null
    private var eventList: MutableList<UpcomingEvent?> = ArrayList()
    private var fullUniversityList: MutableList<University?>? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController(view)
        databaseReference = FirebaseDatabase.getInstance().getReference("UpcomingEvents")
        sliderHandler = Handler(Looper.getMainLooper())

        // Initialize views
        initializeViews(view)

        // Setup back press handling
        setupBackPressHandling()

        // Setup social actions
        setupSocialActions(view)

        // Setup UI components
        setupEventsRecyclerView()
        setupFeaturesRecyclerView()
        setupUniversitiesRecyclerView()
        setupTestimonialsSlider()
        setupCountryRecyclerView()
        setupSearchBar()
        setupCategoryButtons()
        setupEventListeners()

        setupToolbar()
        // Apply animations
        animateViews()
    }

    private fun initializeViews(view: View) {
        searchEditText = view.findViewById(R.id.searchEditText)
        clearSearchButton = view.findViewById(R.id.clearSearchButton)
        searchResultsRecyclerView = view.findViewById(R.id.search_results)
        studyButton = view.findViewById(R.id.studyButton)
        examButton = view.findViewById(R.id.examButton)
        universityButton = view.findViewById(R.id.universityButton)
        seeAllEventsButton = view.findViewById(R.id.see_all_events)
        inviteButton = view.findViewById(R.id.invite_button)
        eventsRecyclerView = view.findViewById(R.id.recyclerView)

        // Validate views
        if (searchEditText == null || clearSearchButton == null || searchResultsRecyclerView == null) {
            Log.e(TAG, "Search UI components missing")
        } else {
            searchResultsRecyclerView?.visibility = View.GONE
            searchResultsRecyclerView?.alpha = 0f
        }
    }

    private fun setupBackPressHandling() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (searchEditText?.hasFocus() == true) {
                    clearSearchState()
                } else {
                    isEnabled = false
                    requireActivity().onBackPressed()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun setupSocialActions(view: View) {
        val socialActions = SocialActions()
        view.findViewById<View?>(R.id.whatsapp_button)?.setOnClickListener {
            socialActions.openWhatsApp(requireActivity())
        }
        view.findViewById<View?>(R.id.call_now_button)?.setOnClickListener {
            socialActions.makeDirectCall(requireActivity())
        }
    }

    private fun setupEventsRecyclerView() {
        eventsRecyclerView?.let { recyclerView ->
            recyclerView.layoutManager = LinearLayoutManager(
                context, LinearLayoutManager.HORIZONTAL, false
            )
            eventAdapter = UpcomingEventAdapter(eventList, this)
            recyclerView.adapter = eventAdapter

            databaseReference?.child("this_month")?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    eventList.clear()
                    snapshot.children.forEach { data ->
                        data.getValue(UpcomingEvent::class.java)?.let { eventList.add(it) }
                    }
                    eventAdapter?.notifyDataSetChanged()
                    updateSearchList()
                    animateRecyclerView(recyclerView)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Failed to load events: ${error.message}")
                    CustomToast.showToast(requireActivity(), "Failed to load events")
                }
            }) ?: Log.e(TAG, "Database reference is null")
        } ?: Log.e(TAG, "eventsRecyclerView is null")
    }

    override fun onItemClick(position: Int, event: UpcomingEvent?) {
        event?.let {
            val args = Bundle().apply {
                putString("imageUrl", it.imageUrl)
                putString("eventTitle", it.title)
                putString("eventDate", "${it.date} • ${it.time}")
                putString("eventLocation", it.location)
            }
            navController?.navigate(R.id.action_homeFragment_to_eventDetailsBottomSheet, args)
                ?: Log.e(TAG, "NavController is null")
        } ?: Log.e(TAG, "Event is null at position: $position")
    }

    private fun setupFeaturesRecyclerView() {
        featuresRecyclerView = requireView().findViewById(R.id.features_recycler_view)
        featuresRecyclerView?.let { recyclerView ->
            recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            val features = FeatureData.getInstance().getFeatures(requireContext())
            featuresAdapter = FeaturesAdapter(features, this)
            recyclerView.adapter = featuresAdapter
            animateRecyclerView(recyclerView)
        } ?: Log.e(TAG, "featuresRecyclerView is null")
    }

    private fun setupUniversitiesRecyclerView() {
        universitiesRecyclerView = requireView().findViewById(R.id.universities_recycler_view)
        universitiesRecyclerView?.let { recyclerView ->
            recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            fullUniversityList = UniversityData.getUniversities(requireContext())
            if (fullUniversityList?.isNotEmpty() == true) {
                universityListAdapter = UniversityListAdapter(
                    requireContext(),
                    ArrayList(fullUniversityList),
                    UniversityListAdapter.OnItemClickListener { university ->
                        navigateToUniversityDetails(university)
                    }
                )
                recyclerView.adapter = universityListAdapter
                updateSearchList()
                animateRecyclerView(recyclerView)
            } else {
                Log.w(TAG, "University list is null or empty")
            }
        } ?: Log.e(TAG, "universitiesRecyclerView is null")
    }

    private fun navigateToUniversityDetails(university: University?) {
        university?.let {
            val args = Bundle().apply {
                putSerializable("UNIVERSITY", it)
                putString("universityId", it.id)
            }
            navController?.navigate(R.id.action_homeFragment_to_universityDetailsFragment, args)
                ?: Log.e(TAG, "NavController is null")
        } ?: Log.e(TAG, "University is null")
    }

    private fun setupTestimonialsSlider() {
        testimonialsViewPager = requireView().findViewById(R.id.testimonials_viewpager)
        testimonialsViewPager?.let { viewPager ->
            val testimonials: MutableList<Testimonial?> = ArrayList()
            testimonials.add(
                Testimonial(
                    R.drawable.img_ektaparmar,
                    "Ekta Parmar",
                    "The Doctorcubes team has been very cooperative from the start...",
                    R.drawable.flag_russia.toString(),
                    "Kemerovo State Medical University",
                    "4th year",
                    5.0f
                )
            )
            testimonials.add(
                Testimonial(
                    R.drawable.img_sneha,
                    "Sneha Prakash Navas",
                    "I will be studying one of my favorite subjects abroad...",
                    R.drawable.flag_russia.toString(),
                    "Maykop University",
                    "3rd year",
                    5.0f
                )
            )
            testimonials.add(
                Testimonial(
                    R.drawable.img_dipanshu,
                    "Dipanshu Tripude",
                    "Initially, when I was planning to go abroad for my studies...",
                    R.drawable.flag_russia.toString(),
                    "Chechen State Medical University",
                    "2nd year",
                    5.0f
                )
            )
            testimonialsAdapter = TestimonialsSliderAdapter(testimonials)
            viewPager.adapter = testimonialsAdapter

            testimonialsSliderRunnable = Runnable {
                val currentPosition = viewPager.currentItem
                viewPager.currentItem = if (currentPosition == (testimonialsAdapter?.itemCount?.minus(1) ?: 0)) 0 else currentPosition + 1
                testimonialsSliderRunnable?.let {
                    sliderHandler?.postDelayed(it, AUTO_SLIDE_INTERVAL.toLong())
                }
            }


            sliderHandler?.postDelayed(testimonialsSliderRunnable!!, AUTO_SLIDE_INTERVAL.toLong())

            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    sliderHandler?.removeCallbacks(testimonialsSliderRunnable!!)
                    sliderHandler?.postDelayed(testimonialsSliderRunnable!!, AUTO_SLIDE_INTERVAL.toLong())
                }
            })

            animateViewPager()
        } ?: Log.e(TAG, "testimonialsViewPager is null")
    }

    private fun setupCountryRecyclerView() {
        countryRecyclerView = requireView().findViewById(R.id.country_recycler_view)
        countryRecyclerView?.let { recyclerView ->
            recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            val countries = UniversityData.getCountries(requireContext())
            countryAdapter = CountryAdapter(countries, this)
            recyclerView.adapter = countryAdapter
            animateRecyclerView(recyclerView)
        } ?: Log.e(TAG, "countryRecyclerView is null")
    }

    override fun onCountryClick(country: Country?) {
        country?.let {
            val args = Bundle().apply {
                putString("COUNTRY_NAME", it.name)
            }
            navController?.navigate(R.id.action_homeFragment_to_universityFragment, args)
                ?: Log.e(TAG, "NavController is null")
        } ?: Log.e(TAG, "Country is null")
    }

    private fun setupSearchBar() {
        if (searchEditText == null || searchResultsRecyclerView == null || clearSearchButton == null) {
            Log.e(TAG, "Search UI components missing")
            return
        }

        searchResultsAdapter = SearchResultsAdapter(ArrayList(), object : SearchResultsAdapter.OnItemClickListener {
            override fun onItemClick(item: SearchItem?) {
                navigateToSection(item)
            }
        })
        searchResultsRecyclerView?.layoutManager = LinearLayoutManager(context)
        searchResultsRecyclerView?.adapter = searchResultsAdapter

        clearSearchButton?.setOnClickListener { clearSearchState() }

        searchEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                clearSearchButton?.visibility = if (s.isNotEmpty()) View.VISIBLE else View.GONE
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        searchUtils = SearchUtils(
            activity,
            searchEditText,
            fullSearchList,
            object : SearchUtils.SearchCallback<SearchItem?> {
                override fun onSearchResults(filteredList: MutableList<SearchItem?>) {
                    searchResultsAdapter?.updateData(filteredList)
                    val shouldShowResults = searchEditText?.isFocused == true || searchEditText?.text.toString().isNotEmpty()
                    if (shouldShowResults && filteredList.isNotEmpty()) {
                        animateSearchResultsIn()
                    } else {
                        animateSearchResultsOut()
                    }
                    Log.d(TAG, "Search results updated: ${filteredList.size} items")
                }

                override fun getSearchText(item: SearchItem?): String {
                    return item?.title ?: ""
                }
            }
        )
        searchUtils?.setupSearchBar()

        searchEditText?.setOnFocusChangeListener { _, hasFocus ->
            val query = searchEditText?.text.toString()
            searchUtils?.filter(query)
            if (hasFocus && query.isNotEmpty()) {
                animateSearchResultsIn()
            } else if (!hasFocus) {
                animateSearchResultsOut()
            }
        }
    }

    private fun setupToolbar() {
        (activity as? AppCompatActivity)?.let { appCompatActivity ->
            appCompatActivity.findViewById<Toolbar>(R.id.toolbar)?.let { toolbar ->
                toolbar.findViewById<TextView>(R.id.app_title)?.text = getString(R.string.app_name)
                appCompatActivity.supportActionBar?.apply {
                    setDisplayHomeAsUpEnabled(false)
                    setHomeButtonEnabled(false)
                }
                toolbar.navigationIcon = null
            } ?: Log.e(TAG, "Toolbar not found")
        } ?: Log.e(TAG, "Activity is not an AppCompatActivity")
    }

    private fun clearSearchState() {
        searchEditText?.let {
            it.setText("")
            it.clearFocus()
        }
        hideKeyboard()
        animateSearchResultsOut()
    }

    private fun hideKeyboard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(searchEditText?.windowToken, 0)
    }

    private fun updateSearchList() {
        fullSearchList.clear()
        fullUniversityList?.forEachIndexed { index, uni ->
            uni?.let {
                if (it.name != null && it.country != null) {
                    fullSearchList.add(SearchItem("${it.name}, ${it.country}", "University", it, index))
                }
            }
        }
        eventList.forEachIndexed { index, event ->
            event?.let {
                if (it.title != null && it.location != null) {
                    fullSearchList.add(SearchItem("${it.title}, ${it.location}", "Event", it, index))
                }
            }
        }
        FeatureData.getInstance().getFeatures(requireContext()).forEachIndexed { index, feature ->
            feature?.let {
                fullSearchList.add(SearchItem(it.title, "Feature", it, index))
            }
        }
        testimonialsAdapter?.getTestimonials()?.forEachIndexed { index, testimonial ->
            testimonial?.let {
                if (it.name != null && it.university != null) {
                    fullSearchList.add(SearchItem("${it.name} - ${it.university}", "Testimonial", it, index))
                }
            }
        }
        searchResultsAdapter?.updateData(fullSearchList)
        Log.d(TAG, "Search list updated with ${fullSearchList.size} items")
    }

    private fun navigateToSection(item: SearchItem?) {
        item?.let {
            when (it.type) {
                "University" -> navigateToUniversityDetails(it.data as? University)
                "Event" -> eventsRecyclerView?.smoothScrollToPosition(it.sectionPosition)
                "Feature" -> onFeatureClick(it.data as? Feature)
                "Testimonial" -> testimonialsViewPager?.setCurrentItem(it.sectionPosition, true)
            }
            clearSearchState()
        } ?: Log.e(TAG, "Search item is null")
    }

    private fun setupCategoryButtons() {
        studyButton?.setOnClickListener {
            animateButtonClick(studyButton)
            navController?.navigate(R.id.action_homeFragment_to_studyMaterialFragment)
                ?: Log.e(TAG, "NavController is null")
        }
        examButton?.setOnClickListener {
            animateButtonClick(examButton)
            CustomToast.showToast(requireActivity(), "Coming Soon")
        }
        universityButton?.setOnClickListener {
            animateButtonClick(universityButton)
            navController?.navigate(R.id.action_homeFragment_to_universityFragment)
                ?: Log.e(TAG, "NavController is null")
        }
    }

    private fun setupEventListeners() {
        seeAllEventsButton?.setOnClickListener {
            navController?.navigate(R.id.action_homeFragment_to_fragmentUpcomingEvents)
                ?: Log.e(TAG, "NavController is null")
        }
        inviteButton?.setOnClickListener {
            try {
                val playStoreLink = "https://play.google.com/store/apps/details?id=com.tvm.doctorcube"
                val message = "Hey! Join me on DoctorCube to explore study abroad opportunities.\nDownload the app now: $playStoreLink"
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, message)
                }
                startActivity(Intent.createChooser(shareIntent, "Invite Friends"))
            } catch (e: Exception) {
                Log.e(TAG, "Error sharing invite: ${e.message}")
            }
        }
    }

    override fun onFeatureClick(feature: Feature?) {
        feature?.let {
            val args = Bundle().apply { putSerializable("FEATURE", it) }
            navController?.navigate(R.id.action_homeFragment_to_featuresFragment, args)
                ?: Log.e(TAG, "NavController is null")
        } ?: Log.e(TAG, "Feature is null")
    }

    private fun animateViews() {
        studyButton?.let { animateViewFadeIn(it, 0) }
        examButton?.let { animateViewFadeIn(it, 100) }
        universityButton?.let { animateViewFadeIn(it, 200) }
    }

    private fun animateViewFadeIn(view: View, delay: Long) {
        view.alpha = 0f
        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(500)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setStartDelay(delay)
            .start()
    }

    private fun animateRecyclerView(recyclerView: RecyclerView) {
        recyclerView.alpha = 0f
        recyclerView.animate()
            .alpha(1f)
            .translationX(0f)
            .setDuration(500)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }

    private fun animateViewPager() {
        testimonialsViewPager?.animate()
            ?.alpha(1f)
            ?.setDuration(500)
            ?.setInterpolator(AccelerateDecelerateInterpolator())
            ?.start()
    }

    private fun animateSearchResultsIn() {
        searchResultsRecyclerView?.takeIf { it.visibility != View.VISIBLE }?.let { recyclerView ->
            recyclerView.visibility = View.VISIBLE
            recyclerView.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(300)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
        }
    }

    private fun animateSearchResultsOut() {
        searchResultsRecyclerView?.takeIf { it.visibility == View.VISIBLE }?.let { recyclerView ->
            recyclerView.animate()
                .alpha(0f)
                .translationY(-50f)
                .setDuration(300)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationEnd(animation: Animator) {
                        recyclerView.visibility = View.GONE
                    }
                    override fun onAnimationStart(animation: Animator) {}
                    override fun onAnimationCancel(animation: Animator) {}
                    override fun onAnimationRepeat(animation: Animator) {}
                })
                .start()
        }
    }

    private fun animateButtonClick(view: View?) {
        view?.animate()
            ?.scaleX(0.9f)
            ?.scaleY(0.9f)
            ?.setDuration(100)
            ?.setInterpolator(AccelerateDecelerateInterpolator())
            ?.withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .start()
            }
            ?.start()
    }

    override fun onResume() {
        super.onResume()
        sliderHandler?.postDelayed(testimonialsSliderRunnable ?: return, AUTO_SLIDE_INTERVAL.toLong())
        clearSearchState()
        updateSearchList()
        setupSearchBar()
        Log.d(TAG, "Search state reset in onResume")
    }

    override fun onPause() {
        super.onPause()
        sliderHandler?.removeCallbacks(testimonialsSliderRunnable ?: return)
        clearSearchState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchUtils?.clear()
        searchUtils = null
        searchResultsAdapter = null
        eventAdapter = null
        featuresAdapter = null
        universityListAdapter = null
        countryAdapter = null
        searchEditText = null
        clearSearchButton = null
        searchResultsRecyclerView = null
        eventsRecyclerView = null
        featuresRecyclerView = null
        universitiesRecyclerView = null
        countryRecyclerView = null
        testimonialsViewPager = null
        Log.d(TAG, "Cleared all resources in onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        sliderHandler?.removeCallbacks(testimonialsSliderRunnable ?: return)
        sliderHandler = null
        testimonialsSliderRunnable = null
    }

    data class SearchItem(
        val title: String,
        val type: String?,
        val data: Any?,
        val sectionPosition: Int
    )

    companion object {

        private const val TAG = "HomeFragment"
        private const val AUTO_SLIDE_INTERVAL = 3000
    }
}
