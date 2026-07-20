package com.neeldoshii.marrowquiz.ui.navigation

sealed class Route(val path: String) {
    data object Splash : Route("splash")
    data object Quiz : Route("quiz")
    data object Results : Route("results")
}
