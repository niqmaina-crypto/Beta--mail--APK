package com.example.data.model

enum class EmailCategory(val title: String, val badge: String) {
    ALL("All Mail", "All"),
    PRIORITY("Priority", "AI"),
    PRIMARY("Primary", "Direct"),
    SOCIAL("Social", "Connect"),
    PROMOTIONS("Promotions", "Offers"),
    UPDATES("Updates", "Alerts"),
    FORUMS("Forums", "Groups"),
    SNOOZED("Snoozed", "Later"),
    SCHEDULED("Scheduled", "Queue"),
    SPAM("Spam", "Blocked")
}
