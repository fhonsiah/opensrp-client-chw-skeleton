package org.smartregister.chw.tbleprosy.interactor;

import androidx.annotation.VisibleForTesting;

import org.smartregister.chw.tbleprosy.contract.TBLeprosyProfileContract;
import org.smartregister.chw.tbleprosy.domain.MemberObject;
import org.smartregister.chw.tbleprosy.util.AppExecutors;
import org.smartregister.chw.tbleprosy.util.TBLeprosyUtil;
import org.smartregister.domain.AlertStatus;

import java.util.Date;

public class BaseTBLeprosyProfileInteractor implements TBLeprosyProfileContract.Interactor {
    protected AppExecutors appExecutors;

    @VisibleForTesting
    BaseTBLeprosyProfileInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public BaseTBLeprosyProfileInteractor() {
        this(new AppExecutors());
    }

    @Override
    public void refreshProfileInfo(MemberObject memberObject, TBLeprosyProfileContract.InteractorCallBack callback) {
        Runnable runnable = () -> appExecutors.mainThread().execute(() -> {
            callback.refreshFamilyStatus(AlertStatus.normal);
            callback.refreshMedicalHistory(true);
            callback.refreshUpComingServicesStatus("TBLeprosy Visit", AlertStatus.normal, new Date());
        });
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveRegistration(final String jsonString, final TBLeprosyProfileContract.InteractorCallBack callback) {

        Runnable runnable = () -> {
            try {
                TBLeprosyUtil.saveFormEvent(jsonString);
            } catch (Exception e) {
                e.printStackTrace();
            }

        };
        appExecutors.diskIO().execute(runnable);
    }
}
