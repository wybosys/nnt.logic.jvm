package com.nnt.component

fun LongitudeIsValid(value: Double): Boolean {
    return value > -180 && value < 180
}

fun LatitudeIsValid(value: Double): Boolean {
    return value > -85.05112878 && value < 85.05112878
}
