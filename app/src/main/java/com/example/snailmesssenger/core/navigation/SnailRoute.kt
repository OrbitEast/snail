package com.example.snailmesssenger.core.navigation

/** Navigation routes for the single-activity Compose app. */
object SnailRoute {
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val CHAT = "chat/{conversationId}"
    const val CALL = "call/{conversationId}/{kind}"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"
    const val FIND_PEOPLE = "find_people"
    const val PRIVACY_SECURITY = "settings/privacy_security"
    const val NOTIFICATIONS = "settings/notifications"
    const val DATA_STORAGE = "settings/data_storage"
    const val APPEARANCE = "settings/appearance"
    const val EDIT_PROFILE = "settings/edit_profile"
    const val TWO_STEP = "security/two_step"
    const val CHAT_SETTINGS = "settings/chat_customization"
    const val WALLPAPER_PICKER = "settings/chat_wallpaper"

    fun chat(conversationId: String) = "chat/$conversationId"
    fun call(conversationId: String, kind: String) = "call/$conversationId/$kind"
}
