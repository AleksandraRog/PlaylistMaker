package com.example.playlistmaker.sharing.domain.repsitory

import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.sharing.domain.model.EmailData

interface ExternalNavigator {

    fun shareLink(link: String,): ConsumerData<Any?>

    fun openLink(link: String,): ConsumerData<Any?>

    fun openEmail(emailData : EmailData,): ConsumerData<Any?>
}