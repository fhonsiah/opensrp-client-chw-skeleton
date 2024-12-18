package org.smartregister.chw.tbleprosy.listener;


import android.view.View;

import org.smartregister.chw.tbleprosy.fragment.BaseTBLeprosyCallDialogFragment;
import org.smartregister.chw.tbleprosy.R;

public class BaseTBLeprosyCallWidgetDialogListener implements View.OnClickListener {

    private BaseTBLeprosyCallDialogFragment callDialogFragment;

    public BaseTBLeprosyCallWidgetDialogListener(BaseTBLeprosyCallDialogFragment dialogFragment) {
        callDialogFragment = dialogFragment;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tbleprosy_call_close) {
            callDialogFragment.dismiss();
        }
    }
}
