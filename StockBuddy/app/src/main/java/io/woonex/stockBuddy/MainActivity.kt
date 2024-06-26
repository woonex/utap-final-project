package io.woonex.stockBuddy

import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import io.woonex.stockBuddy.databinding.ActionBarBinding
import io.woonex.stockBuddy.databinding.ActivityMainBinding
import io.woonex.stockBuddy.ui.MainViewModel

/** This is adapted from both the FC8 and HW4 reddit
 *
 */
class MainActivity : AppCompatActivity() {
    private var actionBarBinding: ActionBarBinding? = null
    private val viewModel: MainViewModel by viewModels()
    private lateinit var navController : NavController
    private lateinit var authUser: AuthUser

    // An Android nightmare
    // https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
    // https://stackoverflow.com/questions/7789514/how-to-get-activitys-windowtoken-without-view
    fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.rootView.windowToken, 0)
    }

    // https://stackoverflow.com/questions/24838155/set-onclick-listener-on-action-bar-title-in-android/29823008#29823008
    private fun initActionBar(actionBar: ActionBar) {
        // Disable the default and enable the custom
        actionBar.setDisplayShowTitleEnabled(false)
        actionBar.setDisplayShowCustomEnabled(true)
        actionBarBinding = ActionBarBinding.inflate(layoutInflater)
        // Apply the custom view
        actionBar.customView = actionBarBinding?.root
        viewModel.initActionBarBinding(actionBarBinding!!)

        actionBarBinding?.lougout?.setOnClickListener {
            authUser.logout()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        setSupportActionBar(activityMainBinding.toolbar)
        supportActionBar?.let{
            initActionBar(it)
        }
    }

    override fun onStart() {
        super.onStart()

        authUser = AuthUser(activityResultRegistry)
        // authUser needs to observe our lifecycle so it can run login activity
        lifecycle.addObserver(authUser)

        authUser.observeUser().observe(this) {
            viewModel.setCurrentAuthUser(it)
            viewModel.fetchUserFavs()
        }
    }
}
