package edu.ucdavis.cs.diversitystudy;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.widget.ToggleButton;

public class StudyController extends Activity
{

    @SuppressWarnings("unused")
    private static final String TAG = StudyController.class.getSimpleName();

    private OnClickListener onClickToggleServiceButton = new OnClickListener()
    {

        @Override
        public void onClick(View v)
        {
            ToggleButton b = (ToggleButton) v;
            if(b.isChecked()) {

                AndroidUtil.getInstance().set_participating(true);

                AndroidUtil.getInstance().schedule_service();

                Toast.makeText(
                    StudyController.this,
                    "Thank you! You are now participating in the study.",
                    Toast.LENGTH_LONG).show();
            }
            else {
                AndroidUtil.getInstance().set_participating(false);

                AndroidUtil.getInstance().cancel_schedule_service();

                Toast.makeText(
                    StudyController.this,
                    "You are no longer participating in the study.",
                    Toast.LENGTH_LONG).show();
            }

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.control);

        AndroidUtil.getInstance().initialize(StudyController.this);

        boolean participating = AndroidUtil.getInstance().get_participating();

        ToggleButton b = (ToggleButton) findViewById(R.id.ToggleServiceButton);
        b.setOnClickListener(onClickToggleServiceButton);
        b.setChecked(participating);

        // Need this here if we were installed previously, and the prefs was set
        // and not unset
        if(participating) {
            AndroidUtil.getInstance().schedule_service();
        }
        else {
            AndroidUtil.getInstance().cancel_schedule_service();
        }

    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }
}
