package com.mobile_app

import android.app.Service
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.os.Build
import android.os.IBinder
import android.text.Html
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import kotlinx.coroutines.*
import kotlin.math.abs

class OverlayService : Service() {

    companion object {
        var demoStepCounter = 0
    }

    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: View
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        overlayView = LayoutInflater.from(this).inflate(R.layout.overlay_view, null)

        val layoutFlag: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        // Start top-left
        params.gravity = Gravity.TOP or Gravity.START
        params.x = 0
        params.y = 0

        val bubbleContainer = overlayView.findViewById<View>(R.id.bubble_container)
        val cardContainer = overlayView.findViewById<View>(R.id.card_container)
        
        val stateInitial = overlayView.findViewById<View>(R.id.state_initial)
        val stateSpinner = overlayView.findViewById<View>(R.id.state_spinner)
        val stateResults = overlayView.findViewById<View>(R.id.state_results)
        
        val btnAnalyze = overlayView.findViewById<Button>(R.id.btn_analyze)
        val btnClose = overlayView.findViewById<Button>(R.id.btn_close)
        
        val metricsContainer = overlayView.findViewById<LinearLayout>(R.id.metrics_container)
        val tvAnalysisText = overlayView.findViewById<TextView>(R.id.tv_analysis_text)
        val textBorder = overlayView.findViewById<View>(R.id.text_border)

        // Implement Dragging
        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f
        var isDragging = false

        bubbleContainer.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    isDragging = false
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = event.rawX - initialTouchX
                    val dy = event.rawY - initialTouchY
                    
                    if (abs(dx) > 10 || abs(dy) > 10) {
                        isDragging = true
                        params.x = initialX + dx.toInt()
                        params.y = initialY + dy.toInt()
                        windowManager.updateViewLayout(overlayView, params)
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (!isDragging) {
                        // It was a click!
                        if (cardContainer.visibility == View.GONE) {
                            cardContainer.visibility = View.VISIBLE
                            // Reset state to initial button
                            stateInitial.visibility = View.VISIBLE
                            stateSpinner.visibility = View.GONE
                            stateResults.visibility = View.GONE
                        } else {
                            cardContainer.visibility = View.GONE
                        }
                    }
                    true
                }
                else -> false
            }
        }

        btnClose.setOnClickListener {
            cardContainer.visibility = View.GONE
        }

        btnAnalyze.setOnClickListener {
            stateInitial.visibility = View.GONE
            stateSpinner.visibility = View.VISIBLE

            serviceScope.launch {
                delay(2000) // Simulate 2-second deep scan
                
                demoStepCounter++
                populateResults(demoStepCounter, metricsContainer, tvAnalysisText, textBorder)

                stateSpinner.visibility = View.GONE
                stateResults.visibility = View.VISIBLE
            }
        }

        windowManager.addView(overlayView, params)
    }

    private fun populateResults(step: Int, metricsContainer: LinearLayout, tvAnalysisText: TextView, textBorder: View) {
        metricsContainer.removeAllViews()
        val currentStep = if (step % 4 == 0) 4 else step % 4

        val textHtml: String
        var borderColor = "#DC2626"

        when (currentStep) {
            1 -> {
                addMetricRow(metricsContainer, "Visual Deepfake", "94%", 94)
                addMetricRow(metricsContainer, "Audio Deepfake", "98%", 98)
                addMetricRow(metricsContainer, "Lip-Sync Anomaly", "92%", 92)
                addMetricRow(metricsContainer, "Channel Age", "Recent", null)
                
                textHtml = "This video mentions an AI tool claiming to be <b>95% accurate</b>. It is almost impossible to have a tool with this much reliability. This seems like a <b>scam or an advertisement</b> for such a tool. <br><br>Our forensic engine cross-referenced the speaker's voice with verified authentic samples and found <b>critical frequency deviations</b> indicating synthetic voice cloning. SEBI guidelines strongly prohibit unregistered algorithmic trading advice. Please be aware."
            }
            2 -> {
                addMetricRow(metricsContainer, "Visual AI-Gen", "82%", 82)
                addMetricRow(metricsContainer, "Audio AI-Gen", "85%", 85)
                addMetricRow(metricsContainer, "Regulatory Flags", "3 Pending", null)
                addMetricRow(metricsContainer, "Sentiment", "Hyper-Hype", null)
                
                textHtml = "The claims made in this video do not match any information outside the internet. It appears to be a tactic to <b>pump stock market shares in the defense sector</b>. There are no credible news reports supporting these claims.<br><br>The aggressive call-to-action is a textbook hallmark of pump-and-dump orchestration. NLP analysis reveals a <b>90% correlation with historically blacklisted manipulator scripts</b>. This is a highly AI-generated video."
            }
            3 -> {
                addMetricRow(metricsContainer, "Visual Deepfake", "100%", 100)
                addMetricRow(metricsContainer, "Audio Deepfake", "100%", 100)
                addMetricRow(metricsContainer, "Face Morphing", "99%", 99)
                addMetricRow(metricsContainer, "Identity", "Fabricated", null)
                
                textHtml = "This is a <b>100% highly AI-generated content</b>. The visuals, characters, and audio are completely AI-generated.<br><br>The entire persona is digitally synthesized. Pixel-level analysis reveals <b>heavy artifacting around the jawline</b> and unnatural eye movements. This 'finfluencer' does not exist in any registered intermediary database."
            }
            else -> { // Step 4
                addMetricRow(metricsContainer, "Phishing Risk", "96%", 96)
                addMetricRow(metricsContainer, "Bot Activity", "88%", 88)
                addMetricRow(metricsContainer, "Blacklisted URLs", "5 Found", null)
                addMetricRow(metricsContainer, "Crypto Wallets", "Anonymous", null)
                
                textHtml = "This channel contains <b>numerous unverified links</b>. We need more time to check them. You may come back and click Analyze again to see all the links.<br><br>A deep scan of the channel history reveals an organized attempt to <b>siphon funds into unregulated offshore crypto wallets</b> under the guise of Dabba trading. There is no AI-generated content detected, but the links could be extremely harmful. <b>Please do not click on any of them.</b>"
                borderColor = "#F59E0B" // Orange for warning rather than deepfake red
            }
        }

        tvAnalysisText.text = Html.fromHtml(textHtml, Html.FROM_HTML_MODE_COMPACT)
        textBorder.setBackgroundColor(Color.parseColor(borderColor))
    }

    private fun addMetricRow(container: LinearLayout, label: String, valueText: String, progress: Int?) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 16, 0, 16)
        }
        
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }
        
        val title = TextView(this).apply {
            text = label
            textSize = 12f
            setTextColor(Color.parseColor("#334155"))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        
        val value = TextView(this).apply {
            text = valueText
            textSize = 13f
            setTextColor(Color.parseColor("#DC2626"))
            setTypeface(null, Typeface.BOLD)
        }
        
        header.addView(title)
        header.addView(value)
        row.addView(header)

        if (progress != null) {
            val progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
                max = 100
                this.progress = progress
                progressTintList = ColorStateList.valueOf(Color.parseColor("#DC2626"))
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 16).apply {
                    topMargin = 8
                }
            }
            row.addView(progressBar)
        }

        container.addView(row)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        if (::overlayView.isInitialized) {
            windowManager.removeView(overlayView)
        }
    }
}
