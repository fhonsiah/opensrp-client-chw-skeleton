package org.smartregister.chw.tbleprosy.model;

import org.json.JSONObject;
import org.smartregister.chw.tbleprosy.contract.TBLeprosyRegisterContract;
import org.smartregister.chw.tbleprosy.util.TBLeprosyJsonFormUtils;

public class BaseTBLeprosyRegisterModel implements TBLeprosyRegisterContract.Model {

    @Override
    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId) throws Exception {
        JSONObject jsonObject = TBLeprosyJsonFormUtils.getFormAsJson(formName);
        TBLeprosyJsonFormUtils.getRegistrationForm(jsonObject, entityId, currentLocationId);

        return jsonObject;
    }

}
