package com.agustin.tarati.ui.components.game.highlights.base

interface BaseHighlight {
    val startDelay: Long
    val duration: Long
    val postDelay: Long
    val persistent: Boolean
    val pulse: Boolean
}