package com.example.draganddropcodelab

import android.content.ClipData
import android.content.ClipDescription
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.util.component1
import androidx.core.util.component2
import androidx.core.view.DragStartHelper
import androidx.core.view.OnReceiveContentListener
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.example.draganddropcodelab.databinding.ActivityReceiveRichContentBinding

class ReceiveRichContentActivity : AppCompatActivity() {
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityReceiveRichContentBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.tvGreeting.text = getString(R.string.greeting_2)
        Glide.with(this).asBitmap().load(getString(R.string.source_url_2)).into(binding.ivSource)
        Glide.with(this).asBitmap().load(getString(R.string.target_url_2)).into(binding.ivTarget)
        binding.ivSource.tag = getString(R.string.source_url_2)
        setupDrag(binding.ivSource)
        setupDrop(binding.ivTarget)
    }

    private fun setupDrag(draggableView: View) {
        DragStartHelper(draggableView)
        { view: View, _: DragStartHelper ->
            val item = ClipData.Item(view.tag as? CharSequence)
            val dragData = ClipData(
                view.tag as? CharSequence,
                arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                item
            )
            view.startDragAndDrop(
                dragData,
                View.DragShadowBuilder(view),
                null,
                0
            )
        }.attach()
    }

    private fun setupDrop(dropTarget: View) {
        ViewCompat.setOnReceiveContentListener(
            binding.ivTarget,
            arrayOf("text/*"),
            listener
        )
    }


    private val listener = OnReceiveContentListener { view, payload ->
        val (textContent, remaining) =
            payload.partition { item: ClipData.Item -> item.text != null }
        if (textContent != null) {
            val clip = textContent.clip
            for (i in 0 until clip.itemCount) {
                val currentText = clip.getItemAt(i).text
                Glide.with(this)
                    .load(currentText)
                    .centerCrop().into(view as ImageView)
            }
        }
        remaining
    }

}