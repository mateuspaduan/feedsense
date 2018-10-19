package com.example.pedro.feedsense.modules.home

import android.content.Intent
import android.content.SharedPreferences
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentPagerAdapter
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton
import com.example.pedro.feedsense.*
import com.example.pedro.feedsense.PreferenceHelper.defaultPrefs
import com.example.pedro.feedsense.PreferenceHelper.get
import com.example.pedro.feedsense.databinding.ActivityHomeBinding
import com.example.pedro.feedsense.models.Reaction
import com.example.pedro.feedsense.modules.BaseActivity
import com.example.pedro.feedsense.modules.hideKeyboard
import com.example.pedro.feedsense.modules.login.LoginActivity
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_home_reactions.*
import kotlinx.android.synthetic.main.fragment_line_chart.*
import org.koin.android.architecture.ext.viewModel

class HomeActivity : BaseActivity() {

    val viewModel: HomeViewModel by viewModel()
    lateinit var prefs: SharedPreferences
    private var email: String? = null
    private var isUser = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val binding = DataBindingUtil.setContentView<ActivityHomeBinding>(
                this,
                R.layout.activity_home
        )
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)

        prefs = defaultPrefs(this)

        val adapter = HomePagerAdapter(supportFragmentManager)

        email = prefs["email"]
        viewModel.userToken = email ?: ""
        isUser = email != null

        if (isUser) {
            home_create_session_fab.visibility = View.VISIBLE
            home_show_chart.visibility = View.VISIBLE
            adapter.pages.add(LineChartFragment.newInstance())
        }
        adapter.pages.add(HomeReactionsFragment.newInstance())
        pager.adapter = adapter

        pager.post {
            pager.currentItem = adapter.count
            checkHomeState()
        }
    }

    private fun checkHomeState() {
        val prefs = defaultPrefs(this)
        val sessionId: String? = prefs["sessionId"]
        if (sessionId != null) {
            viewModel.setCurrentSession(sessionId)
            showReactionButtons()
        }
    }

    // Actions
    @Suppress("UNUSED_PARAMETER")
    fun didTapShowChart(view: View) {
        pager.currentItem = 0
    }

    @Suppress("UNUSED_PARAMETER")
    fun didTapShowReactions(view: View) {
        switchToReactionsPage()
    }

    private fun switchToReactionsPage() {
        pager.currentItem = if (isUser) 1 else 0
    }

    @Suppress("UNUSED_PARAMETER")
    fun didTapLogout(view: View) {
        defaultPrefs(this).edit().clear().apply()
        val intent = Intent(this, LoginActivity::class.java)
        finish()
        startActivity(intent)
    }

    @Suppress("UNUSED_PARAMETER")
    fun didTapCreateSession(view: View) {
        hideKeyboard(this)
        home_create_session_button.startAnimation()
        val pin = session_code_field.text.toString()
        viewModel.createSession(pin)
    }

    @Suppress("UNUSED_PARAMETER")
    fun didTapWannaJoinSession(view: View) {
        switchToReactionsPage()
        home_fab_menu.close(true)
        join_session_fields.visibility = View.VISIBLE
        home_create_session_button.visibility = View.GONE
        home_join_session_button.visibility = View.VISIBLE
        reaction_buttons.setMargins(top = 0)
    }

    @Suppress("UNUSED_PARAMETER")
    fun didTapWannaCreateSession(view: View) {
        switchToReactionsPage()
        home_fab_menu.close(true)
        join_session_fields.visibility = View.VISIBLE
        home_create_session_button.visibility = View.VISIBLE
        home_join_session_button.visibility = View.GONE
        reaction_buttons.setMargins(top = 0)
    }

    private fun showReactionButtons() {
        reaction_buttons.visibility = View.VISIBLE
    }

    @Suppress("UNUSED_PARAMETER")
    fun didTapJoinSession(view: View) {
        hideKeyboard(this)
        home_join_session_button.startAnimation()
        val pin = session_code_field.text.toString()
        viewModel.joinSession(pin)
    }

    @Suppress("UNUSED_PARAMETER")
    fun didTapGreenButton(view: View) {
        green_button.startAnimation()
        val reaction = Reaction.LOVING
        viewModel.reactToSession(reaction)
    }

    @Suppress("UNUSED_PARAMETER")
    fun didTapYellowButton(view: View) {
        yellow_button.startAnimation()
        val reaction = Reaction.WHATEVER
        viewModel.reactToSession(reaction)
    }

    @Suppress("UNUSED_PARAMETER")
    fun didTapRedButton(view: View) {
        red_button.startAnimation()
        val reaction = Reaction.HATING
        viewModel.reactToSession(reaction)
    }
}
