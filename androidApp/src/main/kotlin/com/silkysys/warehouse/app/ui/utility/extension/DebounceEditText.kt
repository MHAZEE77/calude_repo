package com.silkysys.warehouse.app.ui.utility.extension

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*

class DebounceEditText @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatEditText(context, attributeSet, defStyleAttr) {

    private val debouncePeriod = 5000L
    private var searchJob: Job? = null

//    @FlowPreview
//    @ExperimentalCoroutinesApi
//    fun setOnDebounceTextWatcher(lifecycle: Lifecycle, onDebounceAction: (String) -> Unit) {
//        searchJob?.cancel()
//        searchJob = onDebounceTextChanged()
//            .distinctUntilChanged()
//            .debounce(debouncePeriod)
//            .onEach { onDebounceAction(it) }
//            .launchIn(lifecycle.coroutineScope)
//    }

    fun removeOnDebounceTextWatcher() {
        searchJob?.cancel()
    }

//    @ExperimentalCoroutinesApi
//    private fun onDebounceTextChanged(): Flow<String> = channelFlow {
//        val textWatcher = object : TextWatcher {
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//
//            override fun afterTextChanged(p0: Editable?) {
//                offer(p0.toString())
//            }
//        }
//
//        addTextChangedListener(textWatcher)
//
//        awaitClose {
//            removeTextChangedListener(textWatcher)
//        }
//    }
}
