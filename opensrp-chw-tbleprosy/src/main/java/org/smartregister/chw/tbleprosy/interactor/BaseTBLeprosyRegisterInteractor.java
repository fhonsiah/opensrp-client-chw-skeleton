package org.smartregister.chw.tbleprosy.interactor;

import androidx.annotation.VisibleForTesting;

import org.smartregister.chw.tbleprosy.contract.TBLeprosyRegisterContract;
import org.smartregister.chw.tbleprosy.util.AppExecutors;
import org.smartregister.chw.tbleprosy.util.TBLeprosyUtil;

public class BaseTBLeprosyRegisterInteractor implements TBLeprosyRegisterContract.Interactor {

    private AppExecutors appExecutors;

    @VisibleForTesting
    BaseTBLeprosyRegisterInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public BaseTBLeprosyRegisterInteractor() {
        this(new AppExecutors());
    }

    @Override
    public void saveRegistration(final String jsonString, final TBLeprosyRegisterContract.InteractorCallBack callBack) {

        Runnable runnable = () -> {
            try {
                TBLeprosyUtil.saveFormEvent(jsonString);
            } catch (Exception e) {
                e.printStackTrace();
            }

            appExecutors.mainThread().execute(() -> callBack.onRegistrationSaved());
        };
        appExecutors.diskIO().execute(runnable);
    }
}
