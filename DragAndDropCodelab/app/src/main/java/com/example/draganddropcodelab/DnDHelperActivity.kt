package com.example.draganddropcodelab

import android.app.Activity
import android.content.ClipData
import android.content.ClipDescription
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.component1
import androidx.core.util.component2
import androidx.core.view.ContentInfoCompat
import androidx.core.view.DragStartHelper
import androidx.core.view.OnReceiveContentListener
import androidx.core.view.ViewCompat
import androidx.draganddrop.DropHelper
import com.bumptech.glide.Glide
import com.example.draganddropcodelab.databinding.ActivityMainBinding


class DnDHelperActivity : AppCompatActivity() {
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.tvGreeting.text = getString(R.string.greeting)
        Glide.with(this).asBitmap().load(getString(R.string.source_url_1)).into(binding.ivSource)
        Glide.with(this).asBitmap().load(getString(R.string.target_url_1)).into(binding.ivTarget)
        binding.ivSource.tag = getString(R.string.source_url_1)
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
        DropHelper.configureView(
            this,
            dropTarget,
            arrayOf("text/*"),
            DropHelper.Options.Builder()
                .setHighlightColor(getColor(R.color.green))
                .setHighlightCornerRadiusPx(16)
                .build(),
        ) { _, payload: ContentInfoCompat ->
            // TODO: step through clips if one cannot be loaded
            val item = payload.clip.getItemAt(0)
            val dragData = item.text
            Glide.with(this)
                .load(dragData)
                .centerCrop().into(dropTarget as ImageView)
            // Consume payload by only returning remaining items
            val (_, remaining) = payload.partition { it == item }
            remaining
        }
    }

}