package com.silkysys.warehouse.app.picking.pick.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.silkysys.warehouse.app.autentcation.main.activity.MainActivity
import com.silkysys.warehouse.app.picking.pick.adapter.PickPageAdapter
import com.silkysys.warehouse.app.ui.activity.BaseActivity
import com.silkysys.warehouse.app.ui.utility.Utility
import com.silkysys.warehouse.databinding.ActivityPickBinding


class PickActivity : BaseActivity() {

    private lateinit var mDialog: AlertDialog
    private lateinit var binding: ActivityPickBinding
    private lateinit var adapter: PickPageAdapter
    // Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!requireAuthenticatedSession()) return
        mDialog = Utility.getProgressDialog(this)
        binding = ActivityPickBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Setup Methods
        setupPagerAdapter()
        setClicks()
     }

    private fun setClicks() {
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun setupPagerAdapter() {
        adapter = PickPageAdapter(this, supportFragmentManager)
        binding.viewpagerMain.adapter = adapter
        binding.tabsMain.setupWithViewPager(binding.viewpagerMain)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent= Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
