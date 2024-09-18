package com.example.secondhomework.entity

data class CountdownTimer(
    var value: Int = 0,
    var maxValue: Int = 0,
    var activate: Boolean = false,
    var minutesMode: Boolean = false
)