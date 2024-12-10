package com.example.cse476.weatherapphw4.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cse476.weatherapphw4.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private var binding: FragmentSettingsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.binding = FragmentSettingsBinding.inflate(this.layoutInflater)
        return this.binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.binding = null
    }
}