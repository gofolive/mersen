package com.example.ui.model

enum class UserActor(
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val description: String
) {
    STUDENT(
        title = "Student Portal",
        subtitle = "Personal Meal Pass & Status",
        iconEmoji = "🎓",
        description = "Access your personal dynamic QR code, MTU ID, and view your daily meal status."
    ),
    CASHIER(
        title = "Cashier (Scanner)",
        subtitle = "Entrance Turnstile Verification",
        iconEmoji = "🛡️",
        description = "Verify student entry passes, enforce anti-passback rules, and view shift statistics."
    ),
    ADMIN(
        title = "Cafeteria Admin",
        subtitle = "Headcounts & Student Roster",
        iconEmoji = "📊",
        description = "Monitor live campus-wide dining consumption, manage student eligibility, and export reports."
    )
}

enum class StudentTab(val title: String) {
    MY_CARD("My Meal Card"),
    MEAL_STATUS("My Meal Dashboard")
}

enum class CashierTab(val title: String) {
    SCANNER("Turnstile Scanner"),
    SHIFT_LOGS("Shift Logs")
}

enum class AdminTab(val title: String) {
    HEADCOUNT("Campus Analytics"),
    ROSTER("Student Roster"),
    WEB_PORTAL("Web Dashboard")
}
