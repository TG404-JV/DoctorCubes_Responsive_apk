package com.tvm.doctorcube.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

class UserProfile  // Default constructor for Firestore
{
    // Getters and setters
    @PropertyName("name")
    var name: String? = null

    @PropertyName("email")
    var email: String? = null

    @PropertyName("mobile")
    var mobile: String? = null

    @PropertyName("country")
    var country: String? = null

    @PropertyName("state")
    var state: String? = null

    @PropertyName("city")
    var city: String? = null

    @PropertyName("neetScore")
    var neetScore: String? = null

    @PropertyName("studyCountry")
    var studyCountry: String? = null

    @PropertyName("universityName")
    var universityName: String? = null

    @PropertyName("lastCallDate")
    var lastCallDate: String? = null

    @PropertyName("hasNeetScore")
    var isHasNeetScore: Boolean = false

    @PropertyName("hasPassport")
    var isHasPassport: Boolean = false

    @PropertyName("isAdmitted")
    var isAdmitted: Boolean = false

    @PropertyName("isApplied")
    var isApplied: Boolean = false

    @PropertyName("lastUpdatedDate")
    var lastUpdatedDate: String? = null

    @PropertyName("timestamp")
    var timestamp: Timestamp? = null
}