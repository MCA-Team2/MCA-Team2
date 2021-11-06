package com.jwpyo.soundmind.view.main

import androidx.lifecycle.*
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.DataClient
import com.jwpyo.soundmind.extensions.getByteArrayFromAsset
import com.jwpyo.soundmind.model.stress.Stress
import com.jwpyo.soundmind.model.voice.Voice
import com.jwpyo.soundmind.repository.StressRepository
import com.jwpyo.soundmind.repository.VoiceRepository
import com.jwpyo.soundmind.utils.PPGConverter
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

@Suppress("JoinDeclarationAndAssignment")
class MainViewModel(
    private val dataClient: DataClient,
    private val ppgConverter: PPGConverter,
    private val voiceRepository: VoiceRepository,
    private val stressRepository: StressRepository,
) : ViewModel() {
    val historyDate: Flow<LocalDate>
    val volume: Flow<List<Float>>
    val stress: Flow<Flow<List<Stress>>>

    val historyDateLiveData: MutableLiveData<LocalDate> = MutableLiveData(LocalDate.now())

    fun insertVoice(asset: Asset, startLDT: LocalDateTime, endLDT: LocalDateTime) {
        viewModelScope.launch {
            voiceRepository.insertVoice(
                Voice(startLDT, endLDT, dataClient.getByteArrayFromAsset(asset))
            )
        }
    }

    fun insertStress(stress: Stress) {
        viewModelScope.launch {
            stressRepository.insertStress(stress)
        }
    }

    init {
        historyDate = historyDateLiveData.asFlow()

        volume = emptyFlow()

        stress = historyDate.map { date ->
            stressRepository.getStresses(date)
        }
    }
}