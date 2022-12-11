package com.pmsi.lamaruin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.pmsi.lamaruin.databinding.ActivityMainRecuiterBinding
import com.pmsi.lamaruin.utils.hideKeyboard

class MainRecuiterActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainRecuiterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        binding = ActivityMainRecuiterBinding.inflate(layoutInflater)
        binding.root.hideKeyboard()
        setContentView(binding.root)
        supportActionBar?.hide()

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostRec) as NavHostFragment
        navController = navHostFragment.navController
        binding.apply {
            bottomNavViewRec.setupWithNavController(navController)
        }
    }
}