package pl.marchuck.imdbapiclient.common

import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Suppress("unused")
fun <T : ViewBinding> Fragment.autoCleared(tearDown: (T.() -> Unit)? = null) = AutoClearedProperty(tearDown)

inline fun Fragment.runIfViewExists(block: () -> Unit) {
    viewLifecycleOwnerLiveData.value
        ?.lifecycle
        ?.currentState
        ?.isAtLeast(Lifecycle.State.CREATED)
        ?.takeIf { it }
        ?.let { block() }
}

/**
 * Property which gets cleaned right after [Fragment]'s view is destroyed.
 * Allows to use [ViewBinding] classes without having to declare them as optionals.
 *
 * Example usage in a [Fragment]:
 *     var binding by autoCleared<MyFragmentBinding>()
 */
class AutoClearedProperty<T : ViewBinding>(
    private var tearDown: (T.() -> Unit)? = null
) : ReadWriteProperty<Fragment, T>, DefaultLifecycleObserver {

    private var _value: T? = null

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        return _value!!
    }

    override fun setValue(thisRef: Fragment, property: KProperty<*>, value: T) {
        thisRef.viewLifecycleOwner.lifecycle.addObserver(this)
        _value = value
    }

    override fun onDestroy(owner: LifecycleOwner) {
        _value?.let { tearDown?.invoke(it) }
        tearDown = null
        _value = null
    }
}
