package com.jmstudios.redmoon

import android.os.Bundle

class AboutActivity : ThemedAppCompatActivity() {
    override val fragment = AboutFragment()
    override val tag = "jmstudios.fragment.tag.ABOUT"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
