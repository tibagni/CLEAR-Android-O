package com.clear.androidfeatures.jobscheduler;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.os.PersistableBundle;

import static com.clear.androidfeatures.jobscheduler.Util.MY_JOB_PARAMETER_BACKGROUND_EXEC_KEY;
import static com.clear.androidfeatures.jobscheduler.Util.MY_JOB_PARAMETER_TEXT_KEY;


public class MyJobService extends JobService{

    private static final String TAG = MyJobService.class.getSimpleName();

    private class MyThread extends Thread {

        final String TEXT_THREAD = "Thread";
        String mStartText;
        String mEndText;

        public MyThread(String text){
            mStartText = TEXT_THREAD + " started by " + text;
            mEndText = TEXT_THREAD + " ended.";
        }

        public void run(){
            Util.dump(getApplicationContext(), TAG, mStartText);

            try{
                Thread.sleep(5000);
            } catch (Exception e) {
                Util.dump(getApplicationContext(), TAG, "Oooops... InterruptedException");
            }

            Util.dump(getApplicationContext(), TAG, mEndText);
        }
    }

    private class MyAsyncTask extends AsyncTask<String,Void,String>{

        String mStartText;
        String mEndText;

        protected String doInBackground(String... strings) {

            final String TEXT_ASYNC_TASK = "AsyncTask";
            mStartText = TEXT_ASYNC_TASK + " doInBackground started by " + strings[0];
            mEndText = TEXT_ASYNC_TASK + " ended at onPostExecute.";

            Util.dump(getApplicationContext(), TAG, mStartText);

            // pass mText to onPostExecute
            return mStartText;
        }

        protected void onPostExecute(String string){
            // string received from doInBackground
            Util.dump(getApplicationContext(), TAG, mEndText);
        }
    }


    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        PersistableBundle extra = jobParameters.getExtras();
        if (extra.containsKey(MY_JOB_PARAMETER_TEXT_KEY)){
            // replace default log with received msg
            Util.dump(getApplicationContext(), TAG,
                     extra.getString(Util.MY_JOB_PARAMETER_TEXT_KEY) + "... is received by JobId: "+ jobParameters.getJobId());
        }

        boolean executionContinues = false;
        if (extra.containsKey(MY_JOB_PARAMETER_BACKGROUND_EXEC_KEY)){
            String text =  "OnStartJob";
            String extra_bg_exec = extra.getString(MY_JOB_PARAMETER_BACKGROUND_EXEC_KEY);
            if (extra_bg_exec.equals(Util.BackgroundExecutionUnit.THREAD.toString())){
                MyThread thread = new MyThread(text);
                thread.start();
                executionContinues = true;
            } else if (extra_bg_exec.equals(Util.BackgroundExecutionUnit.ASYNC_TASK.toString())){
                MyAsyncTask asyncTask = new MyAsyncTask();
                asyncTask.execute(text);
                executionContinues = true;
            } else
            {
                Util.dump(getApplicationContext(), TAG, "This point should never be reached!");
            }
        }

        if (!executionContinues){
            jobFinished(jobParameters, false);
        }

        return executionContinues;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Util.dump(getApplicationContext(), TAG, "Job Stopped");
        return false;
    }

}
