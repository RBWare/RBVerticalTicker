package com.rbware.rbverticalticker

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.viewinterop.AndroidView

/**
 * A [VerticalTicker] usable directly from XML layouts and Java/View-based code, e.g.:
 * ```xml
 * <com.rbware.rbverticalticker.VerticalTickerView
 *     android:layout_width="wrap_content"
 *     android:layout_height="wrap_content"
 *     android:textSize="18sp"
 *     android:textColor="@color/ticker_text"
 *     android:fontFamily="sans-serif-medium"
 *     app:visibleCount="3"
 *     app:topFadeAlpha="0.2"
 *     app:animationDurationMillis="300" />
 * ```
 * Each visible row is rendered with a real [TextView], so its font size, color, style, and
 * family are the same [TextView] properties you'd set on any other text view - via the XML
 * attributes above, or imperatively with [setTextSize], [setTextColor], and [setTypeface].
 */
class VerticalTickerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AbstractComposeView(context, attrs, defStyleAttr) {

    private var tickerState by mutableStateOf(VerticalTickerState(emptyList()))

    var visibleCount: Int by mutableIntStateOf(1)
    var topFadeAlpha: Float by mutableFloatStateOf(1f)
    var animationDurationMillis: Int by mutableIntStateOf(300)

    private var textSizePx: Float by mutableStateOf(spToPx(16f))
    private var rowTextColor: Int by mutableStateOf(DEFAULT_TEXT_COLOR)
    private var rowTypeface: Typeface by mutableStateOf(Typeface.DEFAULT)
    private var itemShownListener: OnItemShownListener? = null

    init {
        attrs?.let { applyAttributeSet(it, defStyleAttr) }
    }

    private fun applyAttributeSet(attrs: AttributeSet, defStyleAttr: Int) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.VerticalTickerView, defStyleAttr, 0)
        try {
            visibleCount = typedArray.getInt(R.styleable.VerticalTickerView_visibleCount, visibleCount)
            topFadeAlpha = typedArray.getFloat(R.styleable.VerticalTickerView_topFadeAlpha, topFadeAlpha)
            animationDurationMillis = typedArray.getInt(
                R.styleable.VerticalTickerView_animationDurationMillis,
                animationDurationMillis,
            )
            textSizePx = typedArray.getDimension(R.styleable.VerticalTickerView_android_textSize, textSizePx)
            rowTextColor = typedArray.getColor(R.styleable.VerticalTickerView_android_textColor, rowTextColor)
            val style = typedArray.getInt(R.styleable.VerticalTickerView_android_textStyle, Typeface.NORMAL)
            val family = resolveFontFamily(typedArray)
            rowTypeface = Typeface.create(family ?: Typeface.DEFAULT, style)
        } finally {
            typedArray.recycle()
        }
    }

    private fun resolveFontFamily(typedArray: TypedArray): Typeface? {
        val index = R.styleable.VerticalTickerView_android_fontFamily
        if (!typedArray.hasValue(index)) return null
        if (Build.VERSION.SDK_INT >= 26) {
            try {
                typedArray.getFont(index)?.let { return it }
            } catch (_: Exception) {
                // Not a font resource (e.g. a plain family name) - fall through.
            }
        }
        val familyName = typedArray.getString(index) ?: return null
        return Typeface.create(familyName, Typeface.NORMAL)
    }

    private fun spToPx(sp: Float): Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, resources.displayMetrics)

    /** Replaces the rotation with [items]; resets any prior history, matching [rememberVerticalTickerState]. */
    fun setItems(items: List<String>) {
        tickerState = VerticalTickerState(items).also { it.setOnItemShownListener(itemShownListener) }
    }

    /**
     * Registers [listener] to be called with the newly shown item every time [showNext] runs,
     * e.g. so other views on screen can update in step. Pass `null` to remove it.
     */
    fun setOnItemShownListener(listener: OnItemShownListener?) {
        itemShownListener = listener
        tickerState.setOnItemShownListener(listener)
    }

    /** See [VerticalTickerState.showNext]. */
    fun showNext() = tickerState.showNext()

    /** See [VerticalTickerState.showNext]. */
    fun showNext(item: String) = tickerState.showNext(item)

    /** Sets the text size of each row, in SP, matching [TextView.setTextSize]. */
    fun setTextSize(sp: Float) {
        textSizePx = spToPx(sp)
    }

    /** Sets the text color of each row, matching [TextView.setTextColor]. */
    fun setTextColor(color: Int) {
        rowTextColor = color
    }

    /** Sets the typeface of each row, matching [TextView.setTypeface]. */
    fun setTypeface(typeface: Typeface?) {
        rowTypeface = typeface ?: Typeface.DEFAULT
    }

    @Composable
    override fun Content() {
        VerticalTicker(
            state = tickerState,
            modifier = Modifier.fillMaxWidth(),
            visibleCount = visibleCount,
            topFadeAlpha = topFadeAlpha,
            animationDurationMillis = animationDurationMillis,
        ) { text ->
            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { ctx -> TextView(ctx).apply { gravity = Gravity.CENTER } },
                update = { textView ->
                    textView.text = text
                    textView.setTextColor(rowTextColor)
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizePx)
                    textView.typeface = rowTypeface
                    textView.maxLines = 1
                    textView.ellipsize = TextUtils.TruncateAt.END
                },
            )
        }
    }

    private companion object {
        const val DEFAULT_TEXT_COLOR = 0xFF000000.toInt()
    }
}
