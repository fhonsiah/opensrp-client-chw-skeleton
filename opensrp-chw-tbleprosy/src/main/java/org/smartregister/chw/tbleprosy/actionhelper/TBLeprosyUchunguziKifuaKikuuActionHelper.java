package org.smartregister.chw.tbleprosy.actionhelper;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.tbleprosy.domain.MemberObject;
import org.smartregister.chw.tbleprosy.domain.VisitDetail;
import org.smartregister.chw.tbleprosy.model.BaseTBLeprosyVisitAction;
import org.smartregister.chw.tbleprosy.util.JsonFormUtils;
import org.smartregister.chw.tbleprosy.util.VisitUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public abstract class TBLeprosyUchunguziKifuaKikuuActionHelper implements BaseTBLeprosyVisitAction.TBLeprosyVisitActionHelper {

    protected String jsonPayload;

    protected String baseEntityId;

    protected String medical_history;

    protected static String genital_examination;

    protected static String diastolic;

    protected static String systolic;

    private HashMap<String, Boolean> checkObject = new HashMap<>();

    protected Context context;

    protected MemberObject memberObject;

    protected  String majibuUchunguzi;


    public TBLeprosyUchunguziKifuaKikuuActionHelper(Context context, MemberObject memberObject) {
        this.context = context;
        this.memberObject = memberObject;
    }

    @Override
    public void onJsonFormLoaded(String jsonPayload, Context context, Map<String, List<VisitDetail>> map) {
        this.jsonPayload = jsonPayload;
    }

    @Override
    public String getPreProcessed() {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            JSONObject global = jsonObject.getJSONObject("global");


            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected abstract String processMajibuUchunguzi(String majibu);



    @Override
    public void onPayloadReceived(String jsonPayload) {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);

            majibuUchunguzi = JsonFormUtils.getValue(jsonObject, "uchunguzi_wa_tb");
            processMajibuUchunguzi(majibuUchunguzi);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public BaseTBLeprosyVisitAction.ScheduleStatus getPreProcessedStatus() {
        return null;
    }

    @Override
    public String getPreProcessedSubTitle() {
        return null;
    }

    @Override
    public String postProcess(String jsonPayload) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonPayload);
            JSONArray fields = JsonFormUtils.fields(jsonObject);
            JSONObject physcialExamCompletionStatus = JsonFormUtils.getFieldJSONObject(fields, "physical_exam_completion_status");
            assert physcialExamCompletionStatus != null;
            physcialExamCompletionStatus.put(com.vijay.jsonwizard.constants.JsonFormConstants.VALUE, VisitUtils.getActionStatus(checkObject));
        } catch (JSONException e) {
            Timber.e(e);
        }

        if (jsonObject != null) {
            return jsonObject.toString();
        }
        return null;
    }

    @Override
    public String evaluateSubTitle() {
        return null;
    }

    @Override
    public BaseTBLeprosyVisitAction.Status evaluateStatusOnPayload() {
        String status = VisitUtils.getActionStatus(checkObject);

        if (status.equalsIgnoreCase(VisitUtils.Complete)) {
            return BaseTBLeprosyVisitAction.Status.COMPLETED;
        }
        if (status.equalsIgnoreCase(VisitUtils.Ongoing)) {
            return BaseTBLeprosyVisitAction.Status.PARTIALLY_COMPLETED;
        }
        return BaseTBLeprosyVisitAction.Status.PENDING;
    }

    @Override
    public void onPayloadReceived(BaseTBLeprosyVisitAction baseTBLeprosyVisitAction) {
        //overridden
    }

}
