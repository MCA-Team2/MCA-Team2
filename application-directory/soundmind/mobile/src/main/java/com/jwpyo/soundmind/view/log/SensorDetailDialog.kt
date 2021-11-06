package com.jwpyo.soundmind.view.log

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider.getUriForFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.jwpyo.soundmind.R
import com.jwpyo.soundmind.base.DatabindingDialog
import com.jwpyo.soundmind.databinding.DialogSensorDetailBinding
import com.jwpyo.soundmind.extensions.applyWindowSize
import com.jwpyo.soundmind.extensions.applyWindowTransparent
import com.jwpyo.soundmind.model.ppg.PPG
import com.jwpyo.soundmind.utils.PermissionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter


class SensorDetailDialog(
    private val viewModel: LogViewModel,
    private val sensorName: String? = null,
) : DatabindingDialog() {
    private lateinit var binding: DialogSensorDetailBinding

    private val permissionManager by lazy { PermissionManager(requireActivity()) }

    private val sensorLiveData: LiveData<List<PPG>> = viewModel.ppgList.map { ppgAllList ->
        ppgAllList
            .filter { it.sensorName == sensorName }
            .sortedBy { it.ldt }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return binding<DialogSensorDetailBinding>(
            inflater,
            R.layout.dialog_sensor_detail,
            container
        ).apply {
            title = sensorName
            lifecycleOwner = viewLifecycleOwner
            this@SensorDetailDialog.binding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        applyWindowTransparent()
        applyWindowSize(0.9f, 0.5f)

        setEventListeners()
        setObservers()
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun setEventListeners() {
        binding.exportButton.setOnClickListener {
            permissionManager.assertPermissionOrRequest(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            ) {
                CoroutineScope(Dispatchers.IO).launch {
                    val ctx = requireContext()
                    val uri = generateDataFrame(sensorLiveData.value!!) {
                        "${it.ldt}, ${it.sensorValue}, ${it.accuracy}"
                    }
                    val shareIntent: Intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_STREAM, uri)
//                        putExtra(Intent.EXTRA_SUBJECT, "Sharing File...")
//                        putExtra(Intent.EXTRA_TEXT, "Sharing File...")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                    }

                    Log.e("hello-uri", "hello-uri : $uri")

                    requireActivity().packageManager.queryIntentActivities(
                        shareIntent,
                        PackageManager.MATCH_DEFAULT_ONLY
                    ).forEach { resolveInfo ->
                        val packageName = resolveInfo.activityInfo.packageName
                        ctx.applicationContext.grantUriPermission(
                            packageName,
                            uri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    }

                    startActivity(
                        Intent.createChooser(shareIntent, resources.getText(R.string.send_to))
                    )
                }
            }
        }
    }

    private fun setObservers() {
        sensorLiveData.observe(this) { ppgList ->
            "#${ppgList.size} of data".also { binding.detailText.text = it }
        }
    }

    private fun <T> generateDataFrame(rows: List<T>, lambda: (T) -> String): Uri {
        val ctx = requireContext()
        val root = getRoot(ctx)
        val fileName = "data.txt"
        val file = File(root, fileName)

        if (!root.exists()) root.mkdirs()

        Log.e("hello", "hello-file : $file")

        FileWriter(file).run {
            rows.forEach {
                append(lambda(it))
                append("\n")
            }
            flush()
            close()
        }

        return getUriForFile(requireActivity(), "com.jwpyo.soundmind.provider", file)
    }

    companion object {
        fun getRoot(context: Context) =
            File(context.getExternalFilesDir(null)!!.absolutePath, "PPG-DATA")
    }
}