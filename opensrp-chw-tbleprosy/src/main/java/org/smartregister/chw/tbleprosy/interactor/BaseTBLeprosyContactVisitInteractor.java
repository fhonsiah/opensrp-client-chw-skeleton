package org.smartregister.chw.tbleprosy.interactor;


import android.content.Context;

import androidx.annotation.VisibleForTesting;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.tbleprosy.R;
import org.smartregister.chw.tbleprosy.TBLeprosyLibrary;
import org.smartregister.chw.tbleprosy.actionhelper.TBLeprosyHtsActionHelper;
import org.smartregister.chw.tbleprosy.actionhelper.TBLeprosyMedicalHistoryActionHelper;
import org.smartregister.chw.tbleprosy.contract.BaseTBLeprosyVisitContract;
import org.smartregister.chw.tbleprosy.domain.MemberObject;
import org.smartregister.chw.tbleprosy.domain.VisitDetail;
import org.smartregister.chw.tbleprosy.model.BaseTBLeprosyVisitAction;
import org.smartregister.chw.tbleprosy.util.AppExecutors;
import org.smartregister.chw.tbleprosy.util.Constants;
import org.smartregister.sync.helper.ECSyncHelper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class BaseTBLeprosyContactVisitInteractor extends BaseTBLeprosyVisitInteractor {

    protected BaseTBLeprosyVisitContract.InteractorCallBack callBack;

    String visitType;
    private final TBLeprosyLibrary tbleprosyLibrary;
    private final LinkedHashMap<String, BaseTBLeprosyVisitAction> actionList;
    protected AppExecutors appExecutors;
    private ECSyncHelper syncHelper;
    private Context mContext;


    @VisibleForTesting
    public BaseTBLeprosyContactVisitInteractor(AppExecutors appExecutors, TBLeprosyLibrary TBLeprosyLibrary, ECSyncHelper syncHelper) {
        this.appExecutors = appExecutors;
        this.tbleprosyLibrary = TBLeprosyLibrary;
        this.syncHelper = syncHelper;
        this.actionList = new LinkedHashMap<>();
    }

    public BaseTBLeprosyContactVisitInteractor(String visitType) {
        this(new AppExecutors(), TBLeprosyLibrary.getInstance(), TBLeprosyLibrary.getInstance().getEcSyncHelper());
        this.visitType = visitType;
    }

    @Override
    protected String getCurrentVisitType() {
        if (StringUtils.isNotBlank(visitType)) {
            return visitType;
        }
        return super.getCurrentVisitType();
    }

    @Override
    protected void populateActionList(BaseTBLeprosyVisitContract.InteractorCallBack callBack) {
        this.callBack = callBack;
        final Runnable runnable = () -> {
            try {
                evaluateTBLeprosyNjiaUchunguzi(details);
                evaluateTBLeprosyPhysicalExam(details);
                evaluateTBLeprosyHTS(details);

            } catch (BaseTBLeprosyVisitAction.ValidationException e) {
                Timber.e(e);
            }

            appExecutors.mainThread().execute(() -> callBack.preloadActions(actionList));
        };

        appExecutors.diskIO().execute(runnable);
    }

    private void evaluateTBLeprosyNjiaUchunguzi(Map<String, List<VisitDetail>> details) throws BaseTBLeprosyVisitAction.ValidationException {

        TBLeprosyMedicalHistoryActionHelper actionHelper = new TBLeprosyMedicalHistory(mContext, memberObject);
        BaseTBLeprosyVisitAction action = getBuilder(context.getString(R.string.tbleprosy_njia_uchunguzi))
                .withOptional(true)
                .withDetails(details)
                .withHelper(actionHelper)
                .withFormName(Constants.TBLEPROSY_FOLLOWUP_FORMS.NJIA_UCHUNGUZI)
                .build();
        actionList.put(context.getString(R.string.tbleprosy_njia_uchunguzi), action);

    }

    private void evaluateTBLeprosyPhysicalExam(Map<String, List<VisitDetail>> details) throws BaseTBLeprosyVisitAction.ValidationException {

        TBLeprosyPhysicalExamActionHelper actionHelper = new TBLeprosyPhysicalExamActionHelper(mContext, memberObject);
        BaseTBLeprosyVisitAction action = getBuilder(context.getString(R.string.tbleprosy_physical_examination))
                .withOptional(true)
                .withDetails(details)
                .withHelper(actionHelper)
                .withFormName(Constants.TBLEPROSY_FOLLOWUP_FORMS.PHYSICAL_EXAMINATION)
                .build();
        actionList.put(context.getString(R.string.tbleprosy_physical_examination), action);
    }

    private void evaluateTBLeprosyHTS(Map<String, List<VisitDetail>> details) throws BaseTBLeprosyVisitAction.ValidationException {

        TBLeprosyHtsActionHelper actionHelper = new TBLeprosyHtsActionHelper(mContext, memberObject);
        BaseTBLeprosyVisitAction action = getBuilder(context.getString(R.string.tbleprosy_hts))
                .withOptional(true)
                .withDetails(details)
                .withHelper(actionHelper)
                .withFormName(Constants.TBLEPROSY_FOLLOWUP_FORMS.HTS)
                .build();
        actionList.put(context.getString(R.string.tbleprosy_hts), action);
    }

    @Override
    protected String getEncounterType() {
        return Constants.EVENT_TYPE.TBLEPROSY_SERVICES;
    }

    @Override
    protected String getTableName() {
        return Constants.TABLES.TBLEPROSY_SERVICE;
    }

    private class TBLeprosyMedicalHistory extends TBLeprosyMedicalHistoryActionHelper {


        public TBLeprosyMedicalHistory(Context context, MemberObject memberObject) {
            super(context, memberObject);
        }

        @Override
        public String postProcess(String s) {
            if (StringUtils.isNotBlank(medical_history)) {
                try {
                    evaluateTBLeprosyPhysicalExam(details);
                    evaluateTBLeprosyHTS(details);
                } catch (BaseTBLeprosyVisitAction.ValidationException e) {
                    e.printStackTrace();
                }
            }
            new AppExecutors().mainThread().execute(() -> callBack.preloadActions(actionList));
            return super.postProcess(s);
        }

    }

    private class TBLeprosyPhysicalExamActionHelper extends org.smartregister.chw.tbleprosy.actionhelper.TBLeprosyPhysicalExamActionHelper {

        public TBLeprosyPhysicalExamActionHelper(Context context, MemberObject memberObject) {
            super(context, memberObject);
        }

        @Override
        public String postProcess(String s) {
            if (StringUtils.isNotBlank(medical_history)) {
                try {
                    evaluateTBLeprosyHTS(details);
                } catch (BaseTBLeprosyVisitAction.ValidationException e) {
                    e.printStackTrace();
                }
            }
            new AppExecutors().mainThread().execute(() -> callBack.preloadActions(actionList));
            return super.postProcess(s);
        }

    }

}
