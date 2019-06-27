package com.example.visualizer_test


import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.lang.Exception


class VisualSurface_view : SurfaceView, SurfaceHolder.Callback, Runnable {

    val _paint = Paint()
    var _buffer: ShortArray = ShortArray(0)
    var _holder: SurfaceHolder
    var _thread: Thread? = null

    override fun run() {
        while (_thread != null) {
            doDraw(_holder)
        }
    }

    constructor(context: Context, surface: SurfaceView)
            : super(context) {

        _holder = surface.holder
        _holder.addCallback(this)

        //Color of Graph's line, etc...
        _paint.strokeWidth = 2f
        _paint.isAntiAlias = true
        _paint.color = Color.WHITE

        //Don't forget me to run SurfaceView!!!!
        isFocusable = true
        requestFocus()
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        if (holder != null) {
            val canvas = holder.lockCanvas()

            holder.unlockCanvasAndPost(canvas)
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

        _thread = Thread(this)
        _thread?.start()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {

        _thread = null
    }

    fun update(buffer: ShortArray, size: Int) {

        _buffer = buffer.copyOf(size)
    }

    private fun doDraw(holder: SurfaceHolder) {
        if (_buffer.size == 0) {
            return
        }

        try {
            val canvas: Canvas = holder.lockCanvas()

            if (canvas != null) {
                canvas.drawColor(Color.BLACK)

                val baseLine: Float = canvas.height / 2f
                var oldX: Float = 0f
                var oldY: Float = baseLine

                for ((index, value) in _buffer.withIndex()) {
                    val x: Float = canvas.width.toFloat() / _buffer.size.toFloat() * index.toFloat()
                    val y: Float = _buffer[index] / 128 + baseLine

                    canvas.drawLine(oldX,oldY,x,y,_paint)

                    oldX=x
                    oldY=y
                }

                _buffer= ShortArray(0)

                holder.unlockCanvasAndPost(canvas)

            }
        }catch (e:Exception){
            Log.d(this.javaClass.name,"doDraw",e)
        }
    }
}