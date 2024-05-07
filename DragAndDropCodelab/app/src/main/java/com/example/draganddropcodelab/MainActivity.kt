package com.example.draganddropcodelab

import android.content.ClipData
import android.content.ClipDescription
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.draganddropcodelab.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.tvGreeting.text = getString(R.string.greeting)
        Glide.with(this).asBitmap().load(getString(R.string.source_url)).into(binding.ivSource)
        Glide.with(this).asBitmap().load(getString(R.string.target_url)).into(binding.ivTarget)
        binding.ivSource.tag = getString(R.string.source_url)
        setupDrag(binding.ivSource)
        setupDrop(binding.ivTarget)
    }

    private fun setupDrag(draggableView: View) {
        draggableView.setOnLongClickListener { v ->
            val label = "Dragged Image Url"
            val clipItem = ClipData.Item(v.tag as? CharSequence)
            val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
            val draggedData = ClipData(
                label, mimeTypes, clipItem
            )
            v.startDragAndDrop(
                draggedData,
                View.DragShadowBuilder(v),
                null,
               View.DRAG_FLAG_GLOBAL or View.DRAG_FLAG_GLOBAL_URI_READ
            )
        }
    }

    private fun setupDrop(dropTarget: View) {
        dropTarget.setOnDragListener { v, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    Log.d(TAG, "ON DRAG STARTED")
                    if (event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        (v as? ImageView)?.alpha = 0.5F
                        v.invalidate()
                        true
                    } else {
                        false
                    }
                }

                DragEvent.ACTION_DRAG_ENTERED -> {
                    Log.d(TAG, "ON DRAG ENTERED")
                    (v as? ImageView)?.alpha = 0.3F
                    v.invalidate()
                    true
                }

                DragEvent.ACTION_DRAG_LOCATION -> {
                    Log.d(TAG, "On DRAG LOCATION")
                    true
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    Log.d(TAG, "ON DRAG ENDED")
                    (v as? ImageView)?.alpha = 1.0F
                    true
                }

                DragEvent.ACTION_DRAG_EXITED -> {
                    Log.d(TAG, "ON DRAG EXISITED")
                    (v as? ImageView)?.alpha = 0.5F
                    v.invalidate()
                    true
                }

                DragEvent.ACTION_DROP -> {
                    Log.d(TAG, "On DROP")
                    val dropPermission = requestDragAndDropPermissions(event)
                    val item: ClipData.Item = event.clipData.getItemAt(0)
                    val dragData = item.text
                    Toast.makeText(this, "Dragged Data ${dragData}", Toast.LENGTH_SHORT).show()
                    Glide.with(this).load(item.text).into(v as ImageView)
                    (v as? ImageView)?.alpha = 1.0F
                    dropPermission.release()
                    true
                }

                else -> {
                    false
                }
            }
        }
    }
}