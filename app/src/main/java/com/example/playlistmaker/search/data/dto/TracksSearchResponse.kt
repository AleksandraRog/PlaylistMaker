package com.example.playlistmaker.search.data.dto

import com.example.playlistmaker.common.data.dto.Response
import com.example.playlistmaker.common.data.dto.TrackDto

class TracksSearchResponse(val results : List<TrackDto>) : Response()
