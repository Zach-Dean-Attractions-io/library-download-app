package com.udacity


sealed class ButtonState {
    object Clicked : ButtonState() // Unused
    object Loading : ButtonState()
    object Completed : ButtonState()
}