package com.example.splitwise.util

import androidx.fragment.app.Fragment
import com.example.splitwise.SplitwiseApp
import com.example.splitwise.data.SplitwiseRepository

fun Fragment.repository(): SplitwiseRepository =
    (requireActivity().application as SplitwiseApp).repository
