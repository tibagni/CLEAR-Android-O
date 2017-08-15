package com.clear.androidfeatures.jobintentservice

import android.os.Bundle
import android.support.annotation.MainThread
import android.support.v7.app.AppCompatActivity
import com.clear.androidfeatures.R

@MainThread
class JobIntentServiceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_job_intent_service)
    }
}