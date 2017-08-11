package com.clear.androidfeatures.jobinfo

import android.content.Intent
import android.os.Bundle
import android.support.annotation.MainThread
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.clear.androidfeatures.R

@MainThread
class JobInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_job_info)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.job_info, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var itemProcessed = true

        when (item.itemId) {
            R.id.menu_item_settings -> {
                val intent = Intent(applicationContext, JobInfoSettingsActivity::class.java)
                startActivity(intent)
            }
            else -> itemProcessed = super.onOptionsItemSelected(item)
        }

        return itemProcessed
    }
}