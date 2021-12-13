package com.jwpyo.soundmind.view.history

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.jwpyo.soundmind.R
import com.jwpyo.soundmind.base.DatabindingDialog
import com.jwpyo.soundmind.databinding.DialogShareBinding
import com.jwpyo.soundmind.extensions.applyWindowSize
import com.jwpyo.soundmind.extensions.applyWindowTransparent
import com.jwpyo.soundmind.utils.PcmToWav
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.io.File
import java.lang.String

class ShareDialog : DatabindingDialog() {
    private lateinit var binding: DialogShareBinding
    private val viewModel: HistoryViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return binding<DialogShareBinding>(
            inflater,
            R.layout.dialog_share,
            container
        ).apply {
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner
            this@ShareDialog.binding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        applyWindowTransparent()
        applyWindowSize(0.9f, 0.5f)

        val ctx = requireContext()

        binding.downloadButton.setOnClickListener {
//            val extRoot: File =
//                requireActivity().getExternalFilesDir(null) ?: return@setOnClickListener
//            val someFile = "/test/some.xls"
//
//            val xlsFile = File(extRoot, someFile)
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "audio/wav"

            val contentUri: Uri = FileProvider.getUriForFile(
                ctx,
                ctx.packageName + ".fileprovider",
//                xlsFile
                viewModel.audioByteArray.toTempFile()
            )
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)

            val chooser = Intent.createChooser(shareIntent, "녹취록 공유")

            val resInfoList = ctx.packageManager.queryIntentActivities(
                chooser,
                PackageManager.MATCH_DEFAULT_ONLY
            )
            resInfoList.forEach { resolveInfo ->
                ctx.grantUriPermission(
                    resolveInfo.activityInfo.packageName,
                    contentUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }

            ContextCompat.startActivity(ctx, chooser, null)
        }
    }

    fun ByteArray?.toTempFile(): File {
        val tempPcmFile = File.createTempFile(String.valueOf(hashCode()), ".pcm")
        val tempWavFile = File.createTempFile(String.valueOf(hashCode()), ".wav")

        tempPcmFile.deleteOnExit()
        tempWavFile.deleteOnExit()

        if (this != null) {
            tempPcmFile.writeBytes(this)
            PcmToWav().rawToWave(tempPcmFile, tempWavFile)
        }

        return tempWavFile
    }
}