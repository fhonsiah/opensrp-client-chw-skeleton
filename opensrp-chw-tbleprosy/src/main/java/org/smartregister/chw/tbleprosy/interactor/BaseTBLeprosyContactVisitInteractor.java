package org.smartregister.chw.tbleprosy.interactor;


import android.content.Context;
import android.util.Log;

import androidx.annotation.VisibleForTesting;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.tbleprosy.R;
import org.smartregister.chw.tbleprosy.TBLeprosyLibrary;
import org.smartregister.chw.tbleprosy.actionhelper.TBLeprosyHtsActionHelper;
import org.smartregister.chw.tbleprosy.actionhelper.TBLeprosyKuchukuaSampuliActionHelper;
import org.smartregister.chw.tbleprosy.actionhelper.TBLeprosyMedicalHistoryActionHelper;
import org.smartregister.chw.tbleprosy.actionhelper.TBLeprosyNjiaUchunguziActionHelper;
import org.smartregister.chw.tbleprosy.actionhelper.TBLeprosyUchunguziKifuaKikuuActionHelper;
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
                evaluateTBLeprosyUchunguziKifuaKikuu(details);

            } catch (BaseTBLeprosyVisitAction.ValidationException e) {
                Timber.e(e);
            }

            appExecutors.mainThread().execute(() -> callBack.preloadActions(actionList));
        };

        appExecutors.diskIO().execute(runnable);
    }

    private void evaluateTBLeprosyNjiaUchunguzi(Map<String, List<VisitDetail>> details) throws BaseTBLeprosyVisitAction.ValidationException {

        TBLeprosyNjiaUchunguziActionHelper actionHelper = new TBLeprosyNjiaUchunguziActionHelper(mContext, memberObject);
        BaseTBLeprosyVisitAction action = getBuilder(context.getString(R.string.tbleprosy_njia_uchunguzi))
                .withOptional(true)
                .withDetails(details)
                .withHelper(actionHelper)
                .withFormName(Constants.TBLEPROSY_FOLLOWUP_FORMS.NJIA_UCHUNGUZI)
                .build();
        actionList.put(context.getString(R.string.tbleprosy_njia_uchunguzi), action);

    }

    private void evaluateTBLeprosyUchunguziKifuaKikuu(Map<String, List<VisitDetail>> details) throws BaseTBLeprosyVisitAction.ValidationException {

        TBLeprosyUchunguziKifuaKikuuActionHelper actionHelper = new TBLeprosyUchunguziKifuaKikuuActionHelper(mContext, memberObject) {
            @Override
            protected String processMajibuUchunguzi(String majibu) {
                if(StringUtils.isNotBlank(majibu) && !majibu.contains("hana_dalili")){
                    try {
                        Log.d("majibu2",majibu);
                        evaluateTBLeprosyKuchukuaSampuli(details,majibu);
                    } catch (BaseTBLeprosyVisitAction.ValidationException e) {
                        throw new RuntimeException(e);
                    }
                }
                appExecutors.mainThread().execute(() -> callBack.preloadActions(actionList));
                return majibu;
            }

        };
        BaseTBLeprosyVisitAction action = getBuilder(context.getString(R.string.tbleprosy_uchunguzi_kifua_kikuu))
                .withOptional(true)
                .withDetails(details)
                .withHelper(actionHelper)
                .withFormName(Constants.TBLEPROSY_FOLLOWUP_FORMS.UCHUNGUZI_KIFUA_KIKUU)
                .build();
        actionList.put(context.getString(R.string.tbleprosy_uchunguzi_kifua_kikuu), action);
    }

    private void evaluateTBLeprosyKuchukuaSampuli(Map<String, List<VisitDetail>> details, String majibu) throws BaseTBLeprosyVisitAction.ValidationException {

        TBLeprosyKuchukuaSampuliActionHelper actionHelper = new TBLeprosyKuchukuaSampuliActionHelper(mContext, memberObject,majibu);
        BaseTBLeprosyVisitAction action = getBuilder(context.getString(R.string.tbleprosy_kuchukua_sampuli))
                .withOptional(true)
                .withDetails(details)
                .withHelper(actionHelper)
                .withFormName(Constants.TBLEPROSY_FOLLOWUP_FORMS.KUCHUKUA_SAMPULI)
                .build();
        actionList.put(context.getString(R.string.tbleprosy_kuchukua_sampuli), action);
    }

    @Override
    protected String getEncounterType() {
        return Constants.EVENT_TYPE.TBLEPROSY_SERVICES;
    }

    @Override
    protected String getTableName() {
        return Constants.TABLES.TBLEPROSY_SERVICE;
    }

}
