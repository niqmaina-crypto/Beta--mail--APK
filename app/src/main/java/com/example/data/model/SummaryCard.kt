package com.example.data.model

sealed interface SummaryCard {
    data class PackageTracking(
        val trackingNumber: String,
        val carrier: String,
        val status: String,
        val deliveryDate: String,
        val itemDescription: String,
        val trackingUrl: String = "https://track.carrier.com"
    ) : SummaryCard

    data class BillPayment(
        val vendor: String,
        val amountDue: String,
        val dueDate: String,
        val invoiceId: String,
        val status: String = "Due Soon",
        val payUrl: String = "https://pay.stripe.com"
    ) : SummaryCard

    data class TravelReservation(
        val airline: String,
        val flightNumber: String,
        val departureAirport: String,
        val arrivalAirport: String,
        val departureTime: String,
        val arrivalTime: String,
        val gate: String,
        val seat: String,
        val boardingGroup: String,
        val bookingRef: String
    ) : SummaryCard

    data class EventItinerary(
        val title: String,
        val dateTime: String,
        val location: String,
        val organizer: String,
        val attendeeCount: Int = 4,
        val rsvpStatus: String = "Awaiting Response"
    ) : SummaryCard
}

data class ThreadMessage(
    val id: String,
    val senderName: String,
    val senderEmail: String,
    val timestamp: Long,
    val body: String,
    val attachmentsRaw: String = ""
)
