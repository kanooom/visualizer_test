package com.example.visualizer_test


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.AsyncTask
import android.view.SurfaceView
import android.widget.Button


class MainActivity : AppCompatActivity() {

    var _record: Record? = null    //check this Class!!
    var _isRecording = false
    var _visualizer: VisualSurface_view? = null
    var _button: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val surface = findViewById(R.id.visualizer) as SurfaceView
        _visualizer = VisualSurface_view(this, surface)
        _button = findViewById(R.id.start_stop_button) as Button
        _button?.setOnClickListener {
            if (_isRecording == true) {
                stopRecord()
            } else {
                doRecord()
            }
        }
    }

/*
    override fun onBackPressed() {
        super.onBackPressed()
        stopRecord()
    }

    override fun onPause() {
        super.onPause()
        stopRecord()
    }
*/

    fun stopRecord() {
        _isRecording = false
        _button?.text = "start"
        _record?.cancel(true)
    }

    fun doRecord() {
        _isRecording = true
        _button?.text = "stop"

        _record = Record()
        _record?.execute()

    }


    inner class Record : AsyncTask<Void, DoubleArray, Void>() {
        override fun doInBackground(vararg params: Void): Void? {
            val sampleRate = 8000

            val minBufferSize =
                AudioRecord.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                ) * 2

            if (minBufferSize < 0) {
                return null
            }
            val audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                minBufferSize
            )

            val sec = 1
            val buffer: ShortArray = ShortArray(sampleRate * (16 / 8) * 1 * sec)

            audioRecord.startRecording()

            try {
                while (_isRecording) {
                    val readSize = audioRecord.read(buffer, 0, minBufferSize)

                    if (readSize < 0) {
                        break
                    }
                    if (readSize == 0) {
                        continue
                    }

                    //_visualizer?.update(buffer, readSize)
                }
            } finally {
                audioRecord.stop()
                audioRecord.release()
            }

            return null
        }
    }
}