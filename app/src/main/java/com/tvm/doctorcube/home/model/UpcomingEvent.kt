package com.tvm.doctorcube.home.model


class UpcomingEvent {
    var id: String? = null          // ← Used instead of eventId
    var title: String? = null
    var date: String? = null
    var time: String? = null
    var location: String? = null
    var category: String? = null
    var attendees: String? = null
    var imageUrl: String? = null
    var isPremium: Boolean = false
    var isFeatured: Boolean = false   // ← Add this if needed separately from bookmarked

    constructor()

    constructor(
        id: String?,
        title: String?,
        category: String?,
        date: String?,
        time: String?,
        location: String?,
        attendees: String?,
        imageUrl: String?,
        isPremium: Boolean,
        isFeatured: Boolean
    ) {
        this.id = id
        this.title = title
        this.category = category
        this.date = date
        this.time = time
        this.location = location
        this.attendees = attendees
        this.imageUrl = imageUrl
        this.isPremium = isPremium
        this.isFeatured = isFeatured
    }
}
