package com.nexlink.nexlinkmobileapp.view.ml

import androidx.lifecycle.ViewModel
import com.nexlink.nexlinkmobileapp.data.repository.MLRepository

class MLViewModel(private val mlRepository: MLRepository) : ViewModel() {

    fun scheduleML(projectId: String) = mlRepository.scheduleML(projectId)

    fun feedbackML(projectId: String) = mlRepository.feedbackML(projectId)
}