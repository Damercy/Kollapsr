package dev.dayaonweb.kollapsr.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.budiyev.android.codescanner.*
import com.budiyev.android.codescanner.CodeScanner.ALL_FORMATS
import com.budiyev.android.codescanner.CodeScanner.CAMERA_BACK
import com.google.android.material.snackbar.Snackbar
import dev.dayaonweb.kollapsr.BottomNavListener
import dev.dayaonweb.kollapsr.MainActivity
import dev.dayaonweb.kollapsr.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.DurationUnit
import kotlin.time.toDuration


class CameraFragment : Fragment(), BottomNavListener {

    override fun onTransitionComplete(isComplete: Boolean) {
        if (isComplete) {
            hideSystemUI()
            codeScanner?.startPreview()
        } else {
            showSystemUI()
            codeScanner?.stopPreview()
        }
    }

    private lateinit var scannerView: CodeScannerView
    private var codeScanner: CodeScanner? = null
    private var snack: Snackbar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.fragment_camera, container, false)
        scannerView = view.findViewById(R.id.scanner_view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        codeScanner = CodeScanner(requireActivity(), scannerView)
        codeScanner?.apply {
            camera = CAMERA_BACK
            formats = ALL_FORMATS
            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isFlashEnabled = false
            isAutoFocusEnabled = true
            decodeCallback = DecodeCallback {
                val displayText =
                    "${it.text}   ${it.barcodeFormat.name}   ${it.timestamp.toDuration(DurationUnit.MINUTES)}"
                snack = Snackbar.make(requireView(), displayText, Snackbar.LENGTH_LONG)
                snack?.show()
            }
            errorCallback = ErrorCallback {
                val displayText = it.localizedMessage ?: ""
                snack = Snackbar.make(requireView(), displayText, Snackbar.LENGTH_LONG)
                snack?.show()
            }
        }
        (activity as? MainActivity)?.onTransitionCompleteListener = this
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        view?.let {
            WindowInsetsControllerCompat(requireActivity().window, it).let { controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }

    }

    private fun showSystemUI() {
        view?.let {
            WindowCompat.setDecorFitsSystemWindows(requireActivity().window, true)
            WindowInsetsControllerCompat(requireActivity().window, it).show(
                WindowInsetsCompat.Type.systemBars()
            )
        }
    }

    override fun onResume() {
        super.onResume()
        onTransitionComplete(true)
    }

    override fun onPause() {
        super.onPause()
        codeScanner?.stopPreview()
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                // Seems like this lib doesn't release resources in background hence doing so explicitly
                codeScanner?.releaseResources()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        codeScanner = null
    }
}