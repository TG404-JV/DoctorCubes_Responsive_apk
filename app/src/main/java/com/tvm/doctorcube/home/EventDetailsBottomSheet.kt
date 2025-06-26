package com.tvm.doctorcube.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.tvm.doctorcube.CustomToast.showToast
import com.tvm.doctorcube.R
import com.tvm.doctorcube.authentication.datamanager.EncryptedSharedPreferencesManager
import com.tvm.doctorcube.home.model.UpcomingEvent
import java.util.Locale

class EventDetailsBottomSheet : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.event_details_bottom_sheet, container, false)

        // Initialize views
        val eventImage = view.findViewById<ImageView>(R.id.eventImage)
        val eventCategory = view.findViewById<TextView>(R.id.eventCategory)
        val eventTitle = view.findViewById<TextView>(R.id.eventTitle)
        val eventDate = view.findViewById<TextView>(R.id.eventDate)
        val eventLocation = view.findViewById<TextView>(R.id.eventLocation)
        val favoriteButton = view.findViewById<ImageButton>(R.id.favoriteButton)
        val detailsButton = view.findViewById<MaterialButton>(R.id.detailsButton)
        val registerButton = view.findViewById<MaterialButton>(R.id.registerButton)

        // Get event details from arguments
        val args = arguments
        if (args != null) {
            val eventId = args.getString("eventId", "")
            val imageUrl = args.getString("imageUrl", "")
            val title = args.getString("eventTitle", "Unknown Event")
            val category = args.getString("eventCategory", "Unknown Category")
            val date = args.getString("eventDate", "Unknown Date")
            val time = args.getString("eventTime", "")
            val location = args.getString("eventLocation", "Unknown Location")
            val attendees = args.getString("eventAttendees", "0")
            val premium = args.getBoolean("premium", false)
            val featured = args.getBoolean("featured", false)

            // Load image with Glide
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.date_badge_premium_bg)
                .centerCrop()
                .into(eventImage)

            // Set text fields
            eventCategory.text = category.uppercase(Locale.getDefault())
            eventTitle.text = title
            eventDate.text = if (time.isEmpty()) date else "$date • $time"
            eventLocation.text = location

            // Check favorite status
            checkFavoriteStatus(eventId, favoriteButton)

            // Handle favorite button click
            favoriteButton.setOnClickListener { v: View? ->
                toggleFavorite(
                    eventId,
                    title,
                    favoriteButton
                )
            }

            // Handle details button click
            detailsButton.setOnClickListener { v: View? ->
                Log.d(TAG, "Details clicked for event: $title")
                showToast(requireActivity(), "Details for $title Not Added")
            }

            // Handle register button click
            registerButton.setOnClickListener { v: View? ->
                registerForEvent(
                    eventId,
                    title,
                    category,
                    date,
                    time,
                    location,
                    attendees,
                    imageUrl,
                    premium,
                    featured
                )
            }
        } else {
            // Fallback if arguments are missing
            eventCategory.text = "UNKNOWN CATEGORY"
            eventTitle.text = "Unknown Event"
            eventDate.text = "Unknown Date"
            eventLocation.text ="Unknown Location"
            eventImage.setImageResource(R.drawable.date_badge_premium_bg)
        }

        return view
    }

    private fun checkFavoriteStatus(eventId: String, favoriteButton: ImageButton) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null || eventId.isEmpty()) {
            favoriteButton.setImageResource(R.drawable.ic_bookmark_border)
            return
        }

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(user.uid)
            .collection("favorites")
            .document(eventId)
            .get()
            .addOnSuccessListener(OnSuccessListener { documentSnapshot: DocumentSnapshot? ->
                favoriteButton.setImageResource(if (documentSnapshot!!.exists()) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark_border)
            })
            .addOnFailureListener { e: Exception? ->
                Log.e(TAG, "Error checking favorite status: ", e)
                favoriteButton.setImageResource(R.drawable.ic_bookmark_border)
            }
    }

    private fun toggleFavorite(eventId: String, eventName: String?, favoriteButton: ImageButton) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null || eventId.isEmpty()) {
            showToast(requireActivity(), "Marked")
            return
        }

        val db = FirebaseFirestore.getInstance()
        val favoriteDocPath = "users/" + user.uid + "/favorites/" + eventId

        db.collection("users")
            .document(user.uid)
            .collection("favorites")
            .document(eventId)
            .get()
            .addOnSuccessListener(OnSuccessListener { documentSnapshot: DocumentSnapshot? ->
                if (documentSnapshot!!.exists()) {
                    // Remove from favorites
                    db.document(favoriteDocPath)
                        .delete()
                        .addOnSuccessListener(OnSuccessListener { aVoid: Void? ->
                            favoriteButton.setImageResource(R.drawable.ic_bookmark_border)
                            showToast(requireActivity(), "Removed $eventName from favorites")
                        })
                        .addOnFailureListener { e: Exception? ->
                            Log.e(TAG, "Failed to remove favorite: ", e)
                            showToast(requireActivity(), "Failed to update favorites")
                        }
                } else {
                    // Add to favorites
                    val favoriteData: MutableMap<String?, Any?> = HashMap()
                    favoriteData.put("eventId", eventId)
                    favoriteData.put("eventName", eventName)
                    favoriteData.put("timestamp", FieldValue.serverTimestamp())

                    db.document(favoriteDocPath)
                        .set(favoriteData)
                        .addOnSuccessListener(OnSuccessListener { aVoid: Void? ->
                            favoriteButton.setImageResource(R.drawable.ic_bookmark_filled)
                            showToast(requireActivity(), "Added $eventName to favorites")
                        })
                        .addOnFailureListener { e: Exception? ->
                            Log.e(TAG, "Failed to add favorite: ", e)
                            showToast(requireActivity(), "Failed to update favorites")
                        }
                }
            })
            .addOnFailureListener { e: Exception? ->
                Log.e(TAG, "Error checking favorite status: ", e)
                showToast(requireActivity(), "Failed to update favorites")
            }
    }

    private fun registerForEvent(
        eventId: String?,
        title: String?,
        category: String?,
        date: String?,
        time: String?,
        location: String?,
        attendees: String?,
        imageUrl: String?,
        premium: Boolean,
        featured: Boolean
    ) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            showToast(requireActivity(), "Please log in to register for the event")
            dismiss()
            return
        }

        val prefs = EncryptedSharedPreferencesManager(requireActivity())
        val userName = prefs.getString("name", "")
        val userEmail = prefs.getString("email", "")

        if (userName.isEmpty() || userEmail.isEmpty()) {
            showToast(requireActivity(), "Please complete your profile to register")
            return
        }

        // Check if already registered
        FirebaseFirestore.getInstance()
            .collection("event_registrations")
            .whereEqualTo("userId", user.uid)
            .whereEqualTo("eventId", eventId)
            .get()
            .addOnSuccessListener(OnSuccessListener { querySnapshot: QuerySnapshot? ->
                if (!querySnapshot!!.isEmpty) {
                    showToast(requireActivity(), "You are already registered for $title")
                    dismiss()
                    return@OnSuccessListener
                }
                // Register the user
                val registration: MutableMap<String?, Any?> = HashMap()
                registration.put("userName", userName)
                registration.put("userEmail", userEmail)
                registration.put("userId", user.uid)
                registration.put("eventId", eventId)
                registration.put("eventName", title)
                registration.put("eventCategory", category)
                registration.put("eventImageUrl", imageUrl)
                registration.put("eventDate", date)
                registration.put("eventTime", time)
                registration.put("eventLocation", location)
                registration.put("eventAttendees", attendees)
                registration.put("premium", premium)
                registration.put("featured", featured)
                registration.put("timestamp", FieldValue.serverTimestamp())
                FirebaseFirestore.getInstance()
                    .collection("event_registrations")
                    .add(registration)
                    .addOnSuccessListener(OnSuccessListener { documentReference: DocumentReference? ->
                        showToast(requireActivity(), "Successfully registered for $title")
                        dismiss()
                    })
                    .addOnFailureListener { e: Exception? ->
                        Log.e(TAG, "Failed to register for event: ", e)
                        showToast(requireActivity(), "Registration failed: " + e!!.message)
                    }
            })
            .addOnFailureListener { e: Exception? ->
                Log.e(TAG, "Error checking registration status: ", e)
                showToast(requireActivity(), "Failed to check registration status")
            }
    }

    companion object {
        const val TAG: String = "EventDetailsBottomSheet"

        @JvmStatic
        fun newInstance(event: UpcomingEvent): EventDetailsBottomSheet {
            val fragment = EventDetailsBottomSheet()
            val args = Bundle()
            args.putString("eventId", event.id)
            args.putString("imageUrl", event.imageUrl)
            args.putString("eventTitle", event.title)
            args.putString("eventCategory", event.category)
            args.putString("eventDate", event.date)
            args.putString("eventTime", event.time)
            args.putString("eventLocation", event.location)
            args.putString("eventAttendees", event.attendees)
            args.putBoolean("premium", event.isPremium)
            args.putBoolean("featured", event.isFeatured)



            fragment.setArguments(args)
            return fragment
        }
    }
}