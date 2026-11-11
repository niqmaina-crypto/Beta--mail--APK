package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SummaryCard
import com.example.ui.theme.EncryptedGreen
import com.example.ui.theme.PriorityHighPink
import com.example.ui.theme.PriorityMediumAmber

@Composable
fun SummaryCardView(
    summaryCard: SummaryCard,
    modifier: Modifier = Modifier
) {
    when (summaryCard) {
        is SummaryCard.PackageTracking -> PackageTrackingSummaryCard(summaryCard, modifier)
        is SummaryCard.BillPayment -> BillPaymentSummaryCard(summaryCard, modifier)
        is SummaryCard.TravelReservation -> TravelReservationSummaryCard(summaryCard, modifier)
        is SummaryCard.EventItinerary -> EventItinerarySummaryCard(summaryCard, modifier)
    }
}

@Composable
fun PackageTrackingSummaryCard(
    card: SummaryCard.PackageTracking,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)),
        modifier = modifier.fillMaxWidth().testTag("summary_card_package")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.LocalShipping, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondary, modifier = Modifier.size(18.dp))
                    }
                    Column {
                        Text(card.carrier, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(card.itemDescription, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
                    }
                }
                Surface(
                    color = EncryptedGreen.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = card.status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EncryptedGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Expected Delivery:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f))
                    Text(card.deliveryDate, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                    Text("Tracking: ${card.trackingNumber}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f))
                }

                Button(
                    onClick = { /* simulated open track */ },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.height(34.dp).testTag("button_track_package")
                ) {
                    Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Track", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun BillPaymentSummaryCard(
    card: SummaryCard.BillPayment,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PriorityMediumAmber.copy(alpha = 0.12f)),
        modifier = modifier.fillMaxWidth().testTag("summary_card_bill")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(PriorityMediumAmber),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Payment, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                    Column {
                        Text(card.vendor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Invoice #${card.invoiceId}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Text(
                    text = card.amountDue,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Status: ${card.status} (${card.dueDate})", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                OutlinedButton(
                    onClick = { /* simulated pay */ },
                    modifier = Modifier.height(32.dp).testTag("button_view_invoice")
                ) {
                    Text("View Receipt", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun TravelReservationSummaryCard(
    card: SummaryCard.TravelReservation,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
        modifier = modifier.fillMaxWidth().testTag("summary_card_travel")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.FlightTakeoff, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(18.dp))
                    }
                    Column {
                        Text("${card.airline} • Flight ${card.flightNumber}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Booking Ref: ${card.bookingRef}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "Gate ${card.gate}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(card.departureAirport, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(card.departureTime, fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                Text("➔", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Column(horizontalAlignment = Alignment.End) {
                    Text(card.arrivalAirport, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(card.arrivalTime, fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Seat: ${card.seat} • ${card.boardingGroup}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Button(
                    onClick = { /* simulated boarding pass */ },
                    modifier = Modifier.height(34.dp).testTag("button_boarding_pass")
                ) {
                    Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Boarding Pass", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun EventItinerarySummaryCard(
    card: SummaryCard.EventItinerary,
    modifier: Modifier = Modifier
) {
    var rsvpState by remember { mutableStateOf(card.rsvpStatus) }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)),
        modifier = modifier.fillMaxWidth().testTag("summary_card_event")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.tertiary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.onTertiary, modifier = Modifier.size(18.dp))
                    }
                    Column {
                        Text(card.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(card.organizer, fontSize = 11.sp, color = MaterialTheme.colorScheme.onTertiaryContainer)
                    }
                }
                Surface(
                    color = EncryptedGreen.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = rsvpState,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EncryptedGreen,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("📅 ${card.dateTime}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text("📍 ${card.location}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onTertiaryContainer)

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { rsvpState = "Going ✓" },
                    modifier = Modifier.weight(1f).height(32.dp).testTag("button_rsvp_yes")
                ) {
                    Text("Yes", fontSize = 11.sp)
                }
                OutlinedButton(
                    onClick = { rsvpState = "Maybe" },
                    modifier = Modifier.weight(1f).height(32.dp)
                ) {
                    Text("Maybe", fontSize = 11.sp)
                }
                OutlinedButton(
                    onClick = { rsvpState = "Declined" },
                    modifier = Modifier.weight(1f).height(32.dp)
                ) {
                    Text("No", fontSize = 11.sp)
                }
            }
        }
    }
}
