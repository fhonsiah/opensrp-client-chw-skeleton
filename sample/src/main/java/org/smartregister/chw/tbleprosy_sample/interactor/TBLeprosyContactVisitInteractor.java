package org.smartregister.chw.tbleprosy_sample.interactor;

import org.smartregister.chw.tbleprosy.domain.MemberObject;
import org.smartregister.chw.tbleprosy.interactor.BaseTBLeprosyContactVisitInteractor;
import org.smartregister.chw.tbleprosy_sample.activity.EntryActivity;


public class TBLeprosyContactVisitInteractor extends BaseTBLeprosyContactVisitInteractor {
    public TBLeprosyContactVisitInteractor(String visitType) {
        super(visitType);
    }

    @Override
    public MemberObject getMemberClient(String memberID, String profileType) {
        return EntryActivity.getSampleMember();
    }
}
