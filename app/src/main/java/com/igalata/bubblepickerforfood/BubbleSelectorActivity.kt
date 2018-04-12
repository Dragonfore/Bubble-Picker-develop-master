package com.igalata.bubblepickerforfood

import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.ui.PlacePicker
import com.igalata.bubblepicker.BubblePickerListener
import com.igalata.bubblepicker.adapter.BubblePickerAdapter
import com.igalata.bubblepicker.model.BubbleGradient
import com.igalata.bubblepicker.model.PickerItem
import kotlinx.android.synthetic.main.selector_activity.*
import java.lang.Boolean.FALSE
import java.lang.Boolean.TRUE
import java.util.*

/**
 * Created by irinagalata on 1/19/17.
 */
class BubbleSelectorActivity : AppCompatActivity() {

    private val boldTypeface by lazy { Typeface.createFromAsset(assets, ROBOTO_BOLD) }
    private val mediumTypeface by lazy { Typeface.createFromAsset(assets, ROBOTO_MEDIUM) }
    private val regularTypeface by lazy { Typeface.createFromAsset(assets, ROBOTO_REGULAR) }


    companion object {
        private const val ROBOTO_BOLD = "roboto_bold.ttf"
        private const val ROBOTO_MEDIUM = "roboto_medium.ttf"
        private const val ROBOTO_REGULAR = "roboto_regular.ttf"
        private const val PLACE_PICKER_REQUEST = 1
        private var builder : PlacePicker.IntentBuilder = PlacePicker.IntentBuilder()

        private lateinit var mySingletonClass : ItemSelector
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.selector_activity)

        titleTextView.typeface = mediumTypeface
        subtitleTextView.typeface = boldTypeface
        hintTextView.typeface = regularTypeface
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            subtitleTextView.letterSpacing = 0.06f
            hintTextView.letterSpacing = 0.05f
        }

        val titles = resources.getStringArray(R.array.countries)
        val colors = resources.obtainTypedArray(R.array.colors)
        val images = resources.obtainTypedArray(R.array.images)

        picker.adapter = object : BubblePickerAdapter {

            override val totalCount = titles.size

            override fun getItem(position: Int): PickerItem {
                return PickerItem().apply {
                    title = titles[position]
                    gradient = BubbleGradient(colors.getColor((position * 2) % 8, 0),
                            colors.getColor((position * 2) % 8 + 1, 0), BubbleGradient.VERTICAL)
                    typeface = mediumTypeface
                    textColor = ContextCompat.getColor(this@BubbleSelectorActivity, android.R.color.white)
                    backgroundImage = ContextCompat.getDrawable(this@BubbleSelectorActivity, images.getResourceId(position, 0))
                }
            }
        }

        colors.recycle()
        images.recycle()

        mySingletonClass = ItemSelector.getInstance()

        picker.bubbleSize = 20
        picker.listener = object : BubblePickerListener {
            //override fun onBubbleSelected(item: PickerItem) = toast("${item.title} selected")
            override fun onBubbleSelected(item: PickerItem) = mySingletonClass.update(item.title, TRUE)

            override fun onBubbleDeselected(item: PickerItem) = mySingletonClass.update(item.title, FALSE)
           // override fun onBubbleDeselected(item: PickerItem) = updateSelected(Arrays.toString(mySingletonClass.returnSelected()))
        }

        val myButton = findViewById(R.id.button) as Button

        myButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                try {
                    startActivityForResult(builder.build(this@BubbleSelectorActivity), PLACE_PICKER_REQUEST)
                }
                catch (e: GooglePlayServicesRepairableException){
                    e.printStackTrace()
                }
                catch (e: GooglePlayServicesNotAvailableException){
                    e.printStackTrace()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        picker.onResume()
    }

    override fun onPause() {
        super.onPause()
        picker.onPause()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PLACE_PICKER_REQUEST){
            if (resultCode == Activity.RESULT_OK){
                val place = PlacePicker.getPlace(this, data)
                var addressText = place.name.toString()

                addressText += "\n" + place.address.toString()


            }
        }
    }

    private fun toast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

    private fun updateSelected(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }


}
